package com.roommake.order.controller;

import com.roommake.cart.dto.CartCreateForm;
import com.roommake.cart.dto.CartItemDto;
import com.roommake.cart.dto.CartListDto;
import com.roommake.order.dto.ApproveResponse;
import com.roommake.order.dto.OrderCreateForm;
import com.roommake.order.dto.ReadyResponse;
import com.roommake.order.service.KakaoPayService;
import com.roommake.order.service.OrderService;
import com.roommake.order.vo.Delivery;
import com.roommake.resolver.Login;
import com.roommake.user.security.LoginUser;
import com.roommake.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
@SessionAttributes({"orderCreateForm"})
@Tag(name = "주문 API", description = "주문에 대한 추가, 변경, 삭제, 조회 API를 제공한다.")
public class OrderController {

    private final KakaoPayService kakaoPayService;
    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "주문/결제 폼", description = "주문 결제 폼을 조회한다.")
    @RequestMapping("/form")
    public String orderform(@RequestParam("id") List<Integer> products,
                            @RequestParam("productDetailId") List<Integer> details,
                            @RequestParam("amount") List<Integer> amounts,
                            @Login LoginUser loginUser,
                            Model model) {

        List<CartCreateForm> forms = convert(products, details, amounts);
        List<CartItemDto> items = orderService.getProductsByDetailId(forms);
        CartListDto dto = new CartListDto(items);

        Delivery delivery = orderService.getDefaultDeliveryByUserId(loginUser.getId());

        model.addAttribute("dto", dto);
        model.addAttribute("delivery", delivery);

        return "order/form";
    }

    private List<CartCreateForm> convert(List<Integer> products, List<Integer> details, List<Integer> amounts) {
        List<CartCreateForm> list = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            CartCreateForm form = new CartCreateForm();
            form.setProductId(products.get(i));
            form.setProductDetailId(details.get(i));
            form.setAmount(amounts.get(i));
            list.add(form);
        }

        return list;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "카카오페이 결제 준비", description = "카카오페이 결제창을 연결한다.")
    @PostMapping("/pay/ready")
    public @ResponseBody ReadyResponse payReady(@RequestBody OrderCreateForm orderCreateForm, Model model) {

        model.addAttribute("orderCreateForm", orderCreateForm);

        log.info("주문 상품 이름: " + orderCreateForm.getName());
        log.info("주문 금액: " + orderCreateForm.getTotalPrice());
        log.info("사용포인트: " + orderCreateForm.getUsedPoint());

        // 카카오 결제 준비하기
        ReadyResponse readyResponse = kakaoPayService.payReady(orderCreateForm.getName(), orderCreateForm.getTotalPrice());
        // 세션에 결제 고유번호(tid) 저장
        SessionUtils.addAttribute("tid", readyResponse.getTid());
        log.info("결제 고유번호: " + readyResponse.getTid());

        return readyResponse;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "카카오페이 결제완료", description = "카카오페이 결제 요청 및 승인 후 주문완료 페이지로 이동한다.")
    @GetMapping("/pay/completed")
    public String payCompleted(@RequestParam("pg_token") String pgToken,
                               @ModelAttribute("orderCreateForm") OrderCreateForm orderCreateForm,
                               Model model,
                               @Login LoginUser loginUser) {
        String tid = SessionUtils.getStringAttributeValue("tid");
        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + tid);

        model.addAttribute("orderCreateForm");

        orderService.createOrder(orderCreateForm, loginUser.getId());

        // 카카오 결제 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(tid, pgToken);

        return "redirect:/order/completed";
    }

    // 결제완료 UI 확인용, 추후 삭제 예정 (카카오페이 결제 거치지 않고 바로 진입)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/completed")
    public String completed(@ModelAttribute("orderCreateForm") OrderCreateForm orderCreateForm) {

        return "order/completed";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "주문 상세", description = "주문 상세내역을 조회한다.")
    @GetMapping("/detail")
    public String detail() {
        return "order/detail";
    }
}

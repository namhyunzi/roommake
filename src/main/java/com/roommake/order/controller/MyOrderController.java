package com.roommake.order.controller;

import com.roommake.order.dto.OrderListDto;
import com.roommake.order.service.MyOrderService;
import com.roommake.resolver.Login;
import com.roommake.user.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/myorder")
public class MyOrderController {

    private final MyOrderService myOrderService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String myorder(@RequestParam(name = "beginDate", required = false) String beginDate,
                          @RequestParam(name = "endDate", required = false) String endDate,
                          @Login LoginUser loginUser, Model model) {

        List<OrderListDto> dtos = myOrderService.getAllOrdersByUserId(loginUser.getId(), beginDate, endDate);

        model.addAttribute("dtos", dtos);

        return "user/mypage-order";
    }
}
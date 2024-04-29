package com.roommake.order.service;

import com.roommake.cart.dto.CartCreateForm;
import com.roommake.cart.dto.CartItemDto;
import com.roommake.order.dto.OrderCreateForm;
import com.roommake.order.mapper.DeliveryMapper;
import com.roommake.order.mapper.OrderMapper;
import com.roommake.order.vo.Delivery;
import com.roommake.order.vo.Order;
import com.roommake.order.vo.OrderItem;
import com.roommake.order.vo.Payment;
import com.roommake.product.mapper.ProductMapper;
import com.roommake.product.vo.Product;
import com.roommake.product.vo.ProductDetail;
import com.roommake.user.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final DeliveryMapper deliveryMapper;

    /**
     * 장바구니에 담긴 상품의 정보를 반환한다.
     *
     * @param forms 장바구니 상품의 상품번호, 상품상세번호, 상품수량이 포함된 CartCreateForm 객체 리스트
     * @return 장바구니 상품의 상세한 정보가 포함된 CartItemDto 객체 리스트
     */
    public List<CartItemDto> getProductsByDetailId(List<CartCreateForm> forms) {

        List<CartItemDto> list = new ArrayList<>();
        for (CartCreateForm form : forms) {
            CartItemDto dto = orderMapper.getProductsByDetailId(form);
            // form에서 받아온 상품수량을 dto에 저장
            dto.setAmount(form.getAmount());
            list.add(dto);
        }

        return list;
    }

    /**
     * 로그인한 유저의 기본 배송지를 반환한다.
     *
     * @param userId 유저 번호
     * @return 로그인한 유저의 기본 배송지
     */
    public Delivery getDefaultDeliveryByUserId(int userId) {

        return orderMapper.getDefaultDeliveryByUserId(userId);
    }

    /**
     * 신규 주문 정보가 저장된 orderCreateForm 객체를 전달받아서 주문정보, 주문상세정보, 결제정보를 생성한다.
     *
     * @param orderCreateForm 신규 주문 정보가 포함된 orderCreateForm 객체
     * @param userId          유저 번호
     */
    @Transactional
    public void createOrder(OrderCreateForm orderCreateForm, int userId) {
        User user = User.builder().id(userId).build();

        Delivery delivery = deliveryMapper.getDeliveryById(orderCreateForm.getDeliveryId());

        Order order = new Order();      // orderId 없는 상태
        order.setUser(user);
        order.setTotalPrice(orderCreateForm.getTotalPrice());
        order.setDelivery(delivery);
        order.setUsePoint(orderCreateForm.getUsePoint());

        orderMapper.createOrder(order); // orderId 생성

        // 주문상세정보 생성
        List<CartCreateForm> items = orderCreateForm.getItems();
        for (CartCreateForm form : items) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            Product product = productMapper.getProductById(form.getProductId());
            ProductDetail detail = ProductDetail.builder().id(form.getProductDetailId()).build();

            item.setProduct(product);
            item.setProductDetail(detail);
            item.setAmount(form.getAmount());
            item.setPrice(product.getPrice());

            orderMapper.createOrderItem(item);
        }

        // 결제정보 생성
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPrice(order.getTotalPrice());
        payment.setUsePoint(order.getUsePoint());

        orderMapper.createPayment(payment);
    }
}

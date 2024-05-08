package com.roommake.admin.order.service;

import com.roommake.admin.order.dto.AdminExchangeDto;
import com.roommake.admin.order.dto.ItemCancelDto;
import com.roommake.admin.order.dto.ItemReturnDto;
import com.roommake.admin.order.dto.OrderHistoryResponseDto;
import com.roommake.admin.order.mapper.AdminOrderMapper;
import com.roommake.admin.refund.AdminRefundDto;
import com.roommake.order.vo.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final AdminOrderMapper adminOrderMapper;

    @Transactional
    public void updateExchangeApprovalYn(int id) {
        adminOrderMapper.updateExchangeApprovalYn(id);
    }

    public List<OrderHistoryResponseDto> getAllOrders() {
        return adminOrderMapper.getAllOrders();
    }

    public int updateOrderStatus(Order order) {
        return adminOrderMapper.updateOrderStatus(order);
    }

    public List<AdminRefundDto> getAllRefund() {
        return adminOrderMapper.getRefund();
    }

    public List<AdminExchangeDto> getAllExchanges() {
        return adminOrderMapper.getAllExchanges();
    }

    public List<ItemCancelDto> getAllorderCancel() {
        return adminOrderMapper.getAllorderCancels();
    }

    public List<ItemReturnDto> getAllItemReturn() {
        return adminOrderMapper.getAllItemReturn();
    }

    public int updateReturnYn(String itemReturnStatus, String itemReturnYn, List<Integer> itemReturnId) {
        return adminOrderMapper.updateReturnYn(itemReturnStatus, itemReturnYn, itemReturnId);
    }

    public AdminExchangeDto getExchangeById(Long id) {
        return adminOrderMapper.getExchangeById(id);
    }

//    public ItemCancelDto getAllorderCancelById(Long id) {
//        return adminOrderMapper.getAllorderCancelById(id);
//    }
}

package com.roommake.order.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrderCancel {

    private int id;                         // 주문취소번호
    private Order orderId;                  // 주문번호
    private Date createDate;                // 주문취소 생성일
    private Date updateDate;                // 주문취소 수정일
    private OrderCancelReason reasonId;     // 주문취소사유 번호
}

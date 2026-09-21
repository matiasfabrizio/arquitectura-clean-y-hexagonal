package com.mfpe.port.in;

import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;

public interface GetOrderByIdUseCase {
    Order getOrderById(OrderId orderId);
}

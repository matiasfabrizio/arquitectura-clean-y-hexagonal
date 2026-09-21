package com.mfpe.service;

import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.in.GetOrderByIdUseCase;
import com.mfpe.port.out.FindOrderByIdPort;

public class GetOrderByIdService implements GetOrderByIdUseCase {
    private final FindOrderByIdPort findOrderByIdPort;

    public GetOrderByIdService(FindOrderByIdPort findOrderByIdPort) {
        this.findOrderByIdPort = findOrderByIdPort;
    }

    @Override
    public Order getOrderById(OrderId orderId) {
        return findOrderByIdPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
    }
}

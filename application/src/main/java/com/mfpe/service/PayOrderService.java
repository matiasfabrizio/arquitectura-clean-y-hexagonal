package com.mfpe.service;

import com.mfpe.exception.OrderDomainException;
import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.in.PayOrderUseCase;
import com.mfpe.port.out.FindOrderByIdPort;
import com.mfpe.port.out.PaymentGateway;
import com.mfpe.port.out.SaveOrderPort;

public class PayOrderService implements PayOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final PaymentGateway paymentGateway;
    private final SaveOrderPort saveOrderPort;

    public PayOrderService(FindOrderByIdPort findOrderByIdPort,
                           PaymentGateway paymentGateway,
                           SaveOrderPort saveOrderPort) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.paymentGateway = paymentGateway;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public void payOrder(String orderId) {
        Order order = findOrderByIdPort.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.calculateTotal();

        boolean success = paymentGateway.processPayment(orderId, order.getTotal());
        if (!success) {
            throw new OrderDomainException("Payment failed for order: " + orderId);
        }
        order.pay();
        saveOrderPort.save(order);
    }
}

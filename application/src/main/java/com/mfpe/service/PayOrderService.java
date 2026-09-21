package com.mfpe.service;

import com.mfpe.exception.OrderDomainException;
import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.in.PayOrderUseCase;
import com.mfpe.port.out.FindOrderByIdPort;
import com.mfpe.port.out.NotificationService;
import com.mfpe.port.out.PaymentGateway;
import com.mfpe.port.out.SaveOrderPort;

public class PayOrderService implements PayOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final PaymentGateway paymentGateway;
    private final SaveOrderPort saveOrderPort;
    private final NotificationService notificationService;

    public PayOrderService(FindOrderByIdPort findOrderByIdPort,
                           PaymentGateway paymentGateway,
                           SaveOrderPort saveOrderPort,
                           NotificationService notificationService) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.paymentGateway = paymentGateway;
        this.saveOrderPort = saveOrderPort;
        this.notificationService = notificationService;
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

        // Only update if payment gateway returns success.
        notificationService.notifyOrderStatusChange(orderId, order.getStatus());

        saveOrderPort.save(order);
    }
}

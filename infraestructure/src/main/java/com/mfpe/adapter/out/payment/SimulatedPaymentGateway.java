package com.mfpe.adapter.out.payment;

import com.mfpe.model.vo.Money;
import com.mfpe.port.out.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimulatedPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(SimulatedPaymentGateway.class);

    @Override
    public boolean processPayment(String orderId, Money amount) {
        log.info("[PAYMENT-SIMULATED] Processing for order {}", orderId);
        return true;
    }
}

package com.mfpe.adapter.out.notification;

import com.mfpe.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SimulatedNotificationServiceTest {

    private final SimulatedNotificationService simulatedNotificationService = new SimulatedNotificationService();

    @Test
    public void notifyOrderStatusChange_shouldPrintOrderIdAndStatus() {
        assertDoesNotThrow(
                () -> simulatedNotificationService.notifyOrderStatusChange("1111", OrderStatus.PAID)
        );
    }
}

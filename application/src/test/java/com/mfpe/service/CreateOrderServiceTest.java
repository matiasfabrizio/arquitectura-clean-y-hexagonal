package com.mfpe.service;

import com.mfpe.command.CreateOrderCommand;
import com.mfpe.model.entity.Order;
import com.mfpe.model.enums.OrderStatus;
import com.mfpe.port.out.SaveOrderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private SaveOrderPort saveOrderPort;

    @InjectMocks
    private CreateOrderService createOrderService;

    @Test
    void should_create_and_save_pending_order_when_command_is_valid() {
        // Arrange
        CreateOrderCommand command = new CreateOrderCommand("customer-1");
        when(saveOrderPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = createOrderService.createOrder(command);

        // Assert
        assertNotNull(result.getId());
        assertEquals("customer-1", result.getCustomerId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertTrue(result.getItems().isEmpty());
        verify(saveOrderPort).save(argThat(order ->
                order.getCustomerId().equals("customer-1")
                        && order.getStatus() == OrderStatus.PENDING
                        && order.getItems().isEmpty()));
    }

    @Test
    void should_return_order_saved_by_port_when_creating_order() {
        // Arrange
        CreateOrderCommand command = new CreateOrderCommand("customer-1");
        Order savedOrder = Order.create("customer-1");
        when(saveOrderPort.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = createOrderService.createOrder(command);

        // Assert
        assertSame(savedOrder, result);
    }
}
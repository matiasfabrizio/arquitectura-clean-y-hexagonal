package com.mfpe.service;

import com.mfpe.exception.OrderNotFoundException;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.out.FindOrderByIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetOrderByIdServiceTest {

    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FindOrderByIdPort findOrderByIdPort;

    @InjectMocks
    private GetOrderByIdService getOrderByIdService;

    @Test
    void should_get_order_by_id(){
        // Arrange
        Order order = Order.create("customer-1");
        OrderId orderId = OrderId.of(order.getId().toString());
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order result = getOrderByIdService.getOrderById(orderId);

        // Assert
        assertEquals(order.getId().toString(), result.getId().toString());
        verify(findOrderByIdPort).findById(orderId);
    }

    @Test
    void should_throw_not_found_when_order_does_not_exist_by_id(){
        // Arrange
        when(findOrderByIdPort.findById(OrderId.of(ORDER_ID))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(OrderNotFoundException.class, () -> {
            getOrderByIdService.getOrderById(OrderId.of(ORDER_ID));
        });

        verify(findOrderByIdPort).findById(OrderId.of(ORDER_ID));
    }
}

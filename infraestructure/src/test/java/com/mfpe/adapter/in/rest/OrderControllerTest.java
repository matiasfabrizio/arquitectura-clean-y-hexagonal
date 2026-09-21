package com.mfpe.adapter.in.rest;

import com.mfpe.adapter.in.rest.dto.AddItemRequest;
import com.mfpe.adapter.in.rest.dto.CreateOrderRequest;
import com.mfpe.adapter.in.rest.dto.OrderItemResponse;
import com.mfpe.adapter.in.rest.dto.OrderResponse;
import com.mfpe.adapter.in.rest.dto.OrderResponseMapper;
import com.mfpe.command.AddItemToOrderCommand;
import com.mfpe.command.CreateOrderCommand;
import com.mfpe.model.entity.Order;
import com.mfpe.model.enums.OrderStatus;
import com.mfpe.port.in.AddItemToOrderUseCase;
import com.mfpe.port.in.CancelOrderUseCase;
import com.mfpe.port.in.CreateOrderUseCase;
import com.mfpe.port.in.PayOrderUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private AddItemToOrderUseCase addItemToOrderUseCase;

    @Mock
    private PayOrderUseCase payOrderUseCase;

    @Mock
    private CancelOrderUseCase cancelOrderUseCase;

    @Mock
    private OrderResponseMapper responseMapper;

    @InjectMocks
    private OrderController controller;

    private void setUpRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/orders");
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private void resetRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    private OrderResponse createOrderResponse() {
        return new OrderResponse(
                ORDER_ID,
                "customer-1",
                OrderStatus.PENDING,
                null,
                null,
                LocalDateTime.of(2026, 1, 15, 10, 30),
                List.of(new OrderItemResponse("P-1", "Producto 1", 1,
                        new BigDecimal("10.00"), new BigDecimal("10.00"), "EUR"))
        );
    }

    @Test
    void createOrder_shouldReturn201WithLocation_whenOrderCreated() {
        setUpRequestContext();
        try {
            // Arrange
            CreateOrderRequest req = new CreateOrderRequest("customer-1");
            Order order = Order.create("customer-1");
            OrderResponse response = createOrderResponse();
            when(createOrderUseCase.createOrder(any(CreateOrderCommand.class))).thenReturn(order);
            when(responseMapper.toResponse(order)).thenReturn(response);

            // Act
            ResponseEntity<OrderResponse> result = controller.createOrder(req);

            // Assert
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("http://localhost:8080/api/orders/" + order.getId(), result.getHeaders().getLocation().toString());
            assertSame(response, result.getBody());
        } finally {
            resetRequestContext();
        }
    }

    @Test
    void createOrder_shouldBuildCommandWithCustomerId_whenCreatingOrder() {
        setUpRequestContext();
        try {
            // Arrange
            CreateOrderRequest req = new CreateOrderRequest("customer-1");
            Order order = Order.create("customer-1");
            when(createOrderUseCase.createOrder(any(CreateOrderCommand.class))).thenReturn(order);
            when(responseMapper.toResponse(order)).thenReturn(createOrderResponse());

            // Act
            controller.createOrder(req);

            // Assert
            verify(createOrderUseCase).createOrder(argThat(command ->
                    command.customerId().equals("customer-1")));
        } finally {
            resetRequestContext();
        }
    }

    @Test
    void addItem_shouldReturn200_whenItemAdded() {
        // Arrange
        Order order = Order.create("customer-1");
        OrderResponse response = createOrderResponse();
        AddItemRequest req = new AddItemRequest("P-1", "Producto 1", 1,
                new BigDecimal("10.00"), "EUR");
        when(addItemToOrderUseCase.addItem(any(AddItemToOrderCommand.class))).thenReturn(order);
        when(responseMapper.toResponse(order)).thenReturn(response);

        // Act
        ResponseEntity<OrderResponse> result = controller.addItem(ORDER_ID, req);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void addItem_shouldBuildCommandFromRequestFields_whenAddingItem() {
        // Arrange
        Order order = Order.create("customer-1");
        AddItemRequest req = new AddItemRequest("P-1", "Producto 1", 2,
                new BigDecimal("12.50"), "EUR");
        when(addItemToOrderUseCase.addItem(any(AddItemToOrderCommand.class))).thenReturn(order);
        when(responseMapper.toResponse(order)).thenReturn(createOrderResponse());

        // Act
        controller.addItem(ORDER_ID, req);

        // Assert
        verify(addItemToOrderUseCase).addItem(argThat(command ->
                command.orderId().equals(ORDER_ID)
                        && command.productId().equals("P-1")
                        && command.productName().equals("Producto 1")
                        && command.quantity() == 2
                        && command.unitPrice().compareTo(new BigDecimal("12.50")) == 0
                        && command.currency().equals("EUR")));
    }

    @Test
    void payOrder_shouldReturn204_whenOrderPaid() {

        // Act
        ResponseEntity<Void> result = controller.payOrder(ORDER_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(payOrderUseCase).payOrder(ORDER_ID);
    }

    @Test
    void cancelOrder_shouldReturn204_whenOrderCancelled() {

        // Act
        ResponseEntity<Void> result = controller.cancelOrder(ORDER_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(cancelOrderUseCase).cancelOrder(ORDER_ID);
    }
}
package com.mfpe.adapter.in.rest;

import com.mfpe.adapter.in.rest.dto.AddItemRequest;
import com.mfpe.adapter.in.rest.dto.CreateOrderRequest;
import com.mfpe.adapter.in.rest.dto.OrderResponse;
import com.mfpe.adapter.in.rest.dto.OrderResponseMapper;
import com.mfpe.command.AddItemToOrderCommand;
import com.mfpe.command.CreateOrderCommand;
import com.mfpe.model.entity.Order;
import com.mfpe.model.vo.OrderId;
import com.mfpe.port.in.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final AddItemToOrderUseCase addItemToOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderResponseMapper responseMapper;
    private final GetOrderByIdUseCase getOrderByIdUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           AddItemToOrderUseCase addItemToOrderUseCase,
                           PayOrderUseCase payOrderUseCase,
                           CancelOrderUseCase cancelOrderUseCase,
                           OrderResponseMapper responseMapper,
                           GetOrderByIdUseCase getOrderByIdUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.addItemToOrderUseCase = addItemToOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.responseMapper = responseMapper;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest req) {

        Order order = createOrderUseCase.createOrder(
                new CreateOrderCommand(req.customerId()));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable String id,
            @RequestBody AddItemRequest req
    ) {

        Order order = addItemToOrderUseCase.addItem(new AddItemToOrderCommand(
                id,
                req.productId(),
                req.productName(),
                req.quantity(),
                req.unitPrice(),
                req.currency()
        ));

        return ResponseEntity.ok(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable String id) {
        payOrderUseCase.payOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        // Pasamos directamente el String en vez de un VO para seguir con la consistencia de los métodos de payOrder y cancelOrder
        Order order = getOrderByIdUseCase.getOrderById(new OrderId(UUID.fromString(id)));
        return ResponseEntity.ok(responseMapper.toResponse(order));
    }
}

package com.mfpe.integration;

import com.mfpe.command.CreateOrderCommand;
import com.mfpe.model.entity.Order;
import com.mfpe.model.enums.OrderStatus;
import com.mfpe.port.in.CreateOrderUseCase;
import com.mfpe.port.out.FindOrderByIdPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@SpringBootTest
@Transactional
public class UseCaseIntegrationTest {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private FindOrderByIdPort findOrderByIdPort;

    // createOrder
    @Test
    void createOrder_shouldPersistAndReturnOrder(){
        Order order = createOrderUseCase.createOrder(
                new CreateOrderCommand("customer-1")
        );

        Assertions.assertNotNull(order.getId());
        Assertions.assertEquals("customer-1", order.getCustomerId());
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());

        Optional<Order> fromDb = findOrderByIdPort.findById(order.getId());

        Assertions.assertTrue(fromDb.isPresent());
        Assertions.assertEquals(order.getId().toString(), fromDb.get().getId().toString());

    }

}

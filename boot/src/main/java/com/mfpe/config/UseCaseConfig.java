package com.mfpe.config;

import com.mfpe.port.in.*;
import com.mfpe.port.out.*;
import com.mfpe.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(SaveOrderPort port) {
        return new CreateOrderService(port);
    }

    @Bean
    public AddItemToOrderUseCase addItemToOrderUseCase(SaveOrderPort saveOrderPort,
                                                       FindOrderByIdPort findOrderByIdPort,
                                                       InventoryService inventoryService){
        return new AddItemToOrderService(findOrderByIdPort, inventoryService, saveOrderPort);
    }

    @Bean
    public PayOrderUseCase payOrderUseCase(FindOrderByIdPort findOrderByIdPort,
                                           PaymentGateway paymentGateway,
                                           SaveOrderPort saveOrderPort,
                                           NotificationService notificationService){
        return new PayOrderService(findOrderByIdPort, paymentGateway, saveOrderPort, notificationService);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(FindOrderByIdPort findOrderByIdPort,
                                                 SaveOrderPort saveOrderPort,
                                                 NotificationService notificationService){
        return new CancelOrderService(findOrderByIdPort, saveOrderPort, notificationService);
    }

    @Bean
    public GetOrderByIdUseCase  getOrderByIdUseCase(FindOrderByIdPort findOrderByIdPort){
        return new GetOrderByIdService(findOrderByIdPort);
    }
}

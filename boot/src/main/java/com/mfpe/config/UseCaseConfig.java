package com.mfpe.config;

import com.mfpe.port.in.*;
import com.mfpe.port.out.FindOrderByIdPort;
import com.mfpe.port.out.InventoryService;
import com.mfpe.port.out.PaymentGateway;
import com.mfpe.port.out.SaveOrderPort;
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
                                           SaveOrderPort saveOrderPort){
        return new PayOrderService(findOrderByIdPort, paymentGateway, saveOrderPort);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(FindOrderByIdPort findOrderByIdPort,
                                              SaveOrderPort saveOrderPort){
        return new CancelOrderService(findOrderByIdPort, saveOrderPort);
    }

    @Bean
    public GetOrderByIdUseCase  getOrderByIdUseCase(FindOrderByIdPort findOrderByIdPort){
        return new GetOrderByIdService(findOrderByIdPort);
    }
}

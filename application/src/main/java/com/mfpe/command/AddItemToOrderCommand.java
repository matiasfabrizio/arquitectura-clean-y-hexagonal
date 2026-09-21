package com.mfpe.command;

import java.math.BigDecimal;

/**
 * Comando para agregar un item a una orden de compra.
 */
public record AddItemToOrderCommand(
        String orderId,
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice, // TODO replace with VO
        String currency // TODO replace with VO
) {

    public AddItemToOrderCommand {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be null or blank");
        }
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be a positive integer");
        }
    }

}

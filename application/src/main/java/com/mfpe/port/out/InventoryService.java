package com.mfpe.port.out;

/*
 * Interfaz que define el puerto de salida para la consulta del stock.
 */
public interface InventoryService {
    boolean isAvailable(String productId, int quantity);
}

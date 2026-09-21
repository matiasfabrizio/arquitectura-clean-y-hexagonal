# Tarea: crear los tests unitarios del módulo `application`

## Contexto
Proyecto Maven multi-módulo `com.mfpe:orderflow` (Java 25, arquitectura hexagonal). El módulo `application` contiene los commands (records validados fail-fast en su constructor), los services (casos de uso) y los puertos de entrada/salida. El proyecto no tiene tests, así que créalos desde cero junto con sus dependencias. No modifiques el código main.

## Clases a testear
- `application/src/main/java/com/mfpe/command/CreateOrderCommand.java`
- `application/src/main/java/com/mfpe/command/AddItemCommand.java`
- `application/src/main/java/com/mfpe/service/CreateOrderService.java`
- `application/src/main/java/com/mfpe/service/AddItemToOrderService.java`
- `application/src/main/java/com/mfpe/service/PayOrderService.java`
- `application/src/main/java/com/mfpe/service/CancelOrderService.java`

Lee también los puertos de salida (`port/out`: SaveOrderPort, FindOrderByIdPort, PaymentGateway, InventoryService) y las entidades de dominio que los services usan.

## Stack
- Java 25. Añade al `pom.xml` del módulo estas dependencias de test (scope test): `org.junit.jupiter:junit-jupiter:5.9.3`, `org.mockito:mockito-core:5.22.0` y `org.mockito:mockito-junit-jupiter:5.22.0`. No añadas nada más (no hay AssertJ).

## Convenciones
- Clases de test en el mismo paquete que la clase bajo test, sufijo `Test`.
- Nomenclatura de métodos en snake_case con patrón `should_<acción>_when_<condición>`.
- Tests de services: `@ExtendWith(MockitoExtension.class)` + `@Mock` en cada puerto de salida + `@InjectMocks` en el service. Mockea SOLO los puertos de salida; el dominio (`Order`, `Money`, etc.) siempre real, preparando estados con sus propios métodos (`addItem`, `pay`, `cancel`).
- Estructura Arrange-Act-Assert con comentarios `// Arrange`, `// Act`, `// Assert`.
- Verifica las interacciones con los puertos (`verify`, incluido `never()` en los caminos de error, donde no debe haber efectos secundarios).
- Tests de commands sin Mockito: happy path + `assertThrows(IllegalArgumentException.class, ...)` sobre el constructor para cada campo inválido.
- No uses `@DisplayName`, `@Nested`, `@ParameterizedTest` ni `ArgumentCaptor` (usa `argThat` si necesitas inspeccionar argumentos).

## Cobertura esperada
Cubre la mayoría de casos: happy paths de cada use case, validación de cada command, order no encontrada (`Optional.empty()`), fallos de pago e inventario no disponible, excepciones de dominio que propagan las entidades, y verificación de que se persiste el estado correcto. La cantidad y nombres exactos de tests los decides tú.

## Fase 1 — Planificación
Antes de escribir código, lee las clases bajo test y presenta la lista de tests que vas a crear (nombre + escenario), organizada por clase.

## Fase 2 — Implementación
Añade las dependencias de test al `pom.xml` del módulo y crea los archivos de test bajo `application/src/test/java/com/mfpe/` (en `command/` y `service/`) siguiendo las convenciones. No ejecutes los tests.

## Cierre
Cuando termines, avísame para revisar manualmente los tests creados y los ejecutaremos para verificar su funcionamiento.

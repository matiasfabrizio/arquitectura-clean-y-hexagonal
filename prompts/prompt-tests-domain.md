# Tarea: crear los tests unitarios del módulo `domain`

## Contexto
Proyecto Maven multi-módulo `com.mfpe:orderflow` (Java 25, arquitectura hexagonal, DDD). El módulo `domain` es puro: su código main solo depende de `java.*`. El proyecto no tiene tests, así que créalos desde cero junto con sus dependencias. No modifiques el código main.

## Clases a testear
- `domain/src/main/java/com/mfpe/model/entity/Order.java` (aggregate root)
- `domain/src/main/java/com/mfpe/model/entity/OrderItem.java`

Lee también los value objects (`OrderId`, `Money`), `OrderStatus` y las excepciones de `com.mfpe.exception` para entender los guards y transiciones.

## Stack
- Java 25. Añade al `pom.xml` del módulo la única dependencia de test necesaria: `org.junit.jupiter:junit-jupiter:5.9.3` (scope test). No añadas Mockito ni AssertJ: en este módulo no se usan mocks.

## Convenciones
- Clases de test en el mismo paquete que la clase bajo test, sufijo `Test`.
- Nomenclatura de métodos en snake_case con patrón `should_<acción>_when_<condición>`.
- Construye los datos con las factorías del propio dominio (`Order.create`, `Money.of`, etc.) y literales simples; no compartas estado entre tests.
- No uses `@DisplayName`, `@Nested` ni `@ParameterizedTest`.
- Estructura Arrange-Act-Assert clara (puede ser implícita con líneas en blanco).
- Tip: compara importes `BigDecimal` con `compareTo` para evitar problemas de escala.

## Cobertura esperada
Cubre la mayoría de casos del dominio: happy paths de creación, cálculos de totales/subtotales, transiciones de estado (pay/cancel) y sus invariantes, guards de validación (null, blank, cantidad/precio inválidos) y excepciones de negocio. La cantidad y nombres exactos de tests los decides tú.

## Fase 1 — Planificación
Antes de escribir código, lee las clases bajo test y presenta la lista de tests que vas a crear (nombre + escenario), organizada por clase.

## Fase 2 — Implementación
Añade la dependencia de test al `pom.xml` del módulo y crea los archivos de test en `domain/src/test/java/com/mfpe/model/entity/` siguiendo las convenciones. No ejecutes los tests.

## Cierre
Cuando termines, avísame para revisar manualmente los tests creados y los ejecutaremos para verificar su funcionamiento.

# Tarea: crear los tests unitarios del módulo `infraestructure`

## Contexto
Proyecto Maven multi-módulo `com.mfpe:orderflow` (Java 25, arquitectura hexagonal). El módulo `infraestructure` contiene los adapters de entrada (REST) y salida (JPA, payment, inventory). El proyecto no tiene tests, así que créalos desde cero junto con sus dependencias. No modifiques el código main.

IMPORTANTE: tests unitarios puros, sin contexto de Spring. NO uses MockMvc, `@WebMvcTest`, `@SpringBootTest` ni `@DataJpaTest`: el controller y el exception handler se testean invocando sus métodos directamente y asertando sobre el `ResponseEntity` devuelto.

## Clases a testear
- `infraestructure/src/main/java/com/mfpe/adapter/out/payment/SimulatedPaymentGateway.java`
- `infraestructure/src/main/java/com/mfpe/adapter/out/jpa/OrderMapper.java`
- `infraestructure/src/main/java/com/mfpe/adapter/out/jpa/OrderRepositoryAdapter.java`
- `infraestructure/src/main/java/com/mfpe/adapter/out/inventory/SimulatedInventoryService.java`
- `infraestructure/src/main/java/com/mfpe/adapter/in/rest/dto/OrderResponseMapper.java`
- `infraestructure/src/main/java/com/mfpe/adapter/in/rest/GlobalExceptionHandler.java`
- `infraestructure/src/main/java/com/mfpe/adapter/in/rest/OrderController.java`

## Stack
- Java 25, Spring Boot 4.0.0. Añade al `pom.xml` del módulo la dependencia de test `org.springframework.boot:spring-boot-starter-test` con `<version>4.0.0</version>` explícita (el proyecto NO hereda de `spring-boot-starter-parent`) y scope test. Aporta JUnit Jupiter y Mockito. No se usa AssertJ en este proyecto.

## Convenciones
- Clases de test en el mismo paquete que la clase bajo test, sufijo `Test`.
- Nomenclatura de métodos tipo `métodoBajoTest_shouldComportamiento` (camelCase + guion bajo, p. ej. `toJpa_shouldMapTotal`, `handleNotFound_shouldReturn404`).
- Clases sin colaboradores (mappers, services simulados, exception handler): instáncialas directamente con `new`. Con colaboradores (adapter, controller): `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`, mockeando puertos/use cases, el repositorio Spring Data y los mappers según corresponda.
- Helpers privados con prefijo `create...` para las fixtures.
- Estructura Arrange-Act-Assert con líneas en blanco.
- No uses `@DisplayName` ni `@Nested`.
- Para el `Location` header del controller, que usa `ServletUriComponentsBuilder.fromCurrentRequest()`, monta un contexto fake con `MockHttpServletRequest` + `RequestContextHolder` (y resetéalo en `finally`).

## Cobertura esperada
Cubre la mayoría de casos: mapeos campo a campo en ambas direcciones (incluido un round-trip y los casos de total nulo), el invariante padre-hijo de las entidades JPA, el cableado del adapter (save, found/not found), el comportamiento simulado de payment/inventory, los mapeos a DTOs (incluido el campo derivado subtotal), los códigos de estado del exception handler (404/409/400/500) y el body de error, y los endpoints del controller (201 con Location, 200, 204) verificando los commands que reciben los use cases. La cantidad y nombres exactos de tests los decides tú.

## Fase 1 — Planificación
Antes de escribir código, lee las clases bajo test y sus colaboradores, y presenta la lista de tests que vas a crear (nombre + escenario + enfoque: puro vs Mockito), organizada por clase.

## Fase 2 — Implementación
Añade la dependencia de test al `pom.xml` del módulo y crea los archivos de test bajo `infraestructure/src/test/java/com/mfpe/adapter/` (en `out/payment/`, `out/jpa/`, `out/inventory/`, `in/rest/` y `in/rest/dto/`) siguiendo las convenciones. No crees tests de integración ni ejecutes los tests.

## Cierre
Cuando termines, avísame para revisar manualmente los tests creados y los ejecutaremos para verificar su funcionamiento.

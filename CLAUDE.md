# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

OrderFlow: a Spring Boot 4 / Java 25 REST API for the purchase-order lifecycle, built as a hexagonal-architecture course project (MitoCode). The assignment spec lives in `orderflow_evaluacion.md` (in Spanish). It covers the `GetOrderById` use case, a new simulated output adapter (Notification or Audit), tests, and JWT/security theory answers. Code comments and docs are in Spanish.

## Commands

Maven multi-module build, with no wrapper. `mvn` is not on the PATH in this shell, so use the IDE-bundled Maven or ask the user.

```bash
mvn clean verify                                   # full build + tests + JaCoCo check (required to pass for delivery)
mvn -pl application test                           # one module
mvn -pl application -am test                       # one module + its upstream modules
mvn -pl application test -Dtest=PayOrderServiceTest                          # single test class
mvn -pl application test -Dtest=PayOrderServiceTest#methodName   # single method
mvn package -DskipTests -Djacoco.skip=true         # build jar without tests (what the Dockerfile does)
docker compose up -d                               # Keycloak on :8180, realm imported from keycloak/realms/
```

- JaCoCo runs in every module (root `pluginManagement`) with a **70% instruction coverage minimum**, enforced at `verify`. The `report` module also aggregates coverage with the same threshold. New code without tests can break `mvn verify`.
- Runtime config is in `boot/src/main/resources/application.yml`. Postgres defaults to `localhost:5432/orderflowdb` (orderflow/orderflow) and can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `JPA_DDL_AUTO`. `KEYCLOAK_ISSUER_URI` defaults to the local Keycloak realm `orderflow-realm`.
- `boot` tests: `UseCaseIntegrationWithContainersTest` extends `PostgresTestBase` (Testcontainers), so it needs Docker. `UseCaseIntegrationTest` and `OrderFlowE2ETest` use the configured datasource. The E2E test imports `TestSecurityConfig` (permit-all) to skip JWT.

## Architecture

The Maven modules enforce the dependency direction `domain ← application ← infraestructure ← boot` (the directory name `infraestructure` is spelled that way on purpose; keep it).

- **domain**: pure Java, with no Spring or JPA. It holds the `Order` aggregate root (`addItem`, `calculateTotal`, `pay`, `cancel`, with its state guards), `OrderItem`, the `Money` and `OrderId` value objects, `OrderStatus`, and the exceptions rooted at `OrderDomainException`.
- **application**: pure Java too, with no Spring annotations. It holds the input ports (`port/in/*UseCase`), the output ports (`port/out/`: `FindOrderByIdPort`, `SaveOrderPort`, `PaymentGateway`, `InventoryService`), the command objects that validate their input in the constructor, and the services that implement the use cases. A service loads the order through `FindOrderByIdPort`, throwing `OrderNotFoundException` when it is missing. It then calls the domain behavior and saves through `SaveOrderPort`.
- **infraestructure**: the Spring adapters.
  - Input: `OrderController` (`/api/orders`) with request/response DTOs, `OrderResponseMapper` and `GlobalExceptionHandler`. The handler maps domain exceptions to HTTP codes: not found → 404, already paid or cancelled → 409, empty order → 400, other domain errors → 400.
  - Security: `SecurityConfig` sets up a stateless OAuth2 resource server that is `@ConditionalOnMissingBean(SecurityFilterChain)`, which is why tests can replace it. `KeycloakRoleConverter` maps Keycloak `realm_access.roles` to `ROLE_*`. `POST /api/orders` requires ADMIN, the other POSTs require ADMIN or USER, and everything else only needs authentication.
  - Output: `OrderRepositoryAdapter` implements both persistence ports through Spring Data and `OrderMapper`, with JPA entities kept separate from the domain entities. `Simulated*` classes are `@Component` fakes of the external services.
- **boot**: `OrderFlowApplication` plus `config/UseCaseConfig`. **Application services are not Spring components.** Each one is wired manually as a `@Bean` in `UseCaseConfig`, so a new use case or a new port dependency means adding or updating a bean there.
- **report**: JaCoCo aggregate report only.

Adding a use case follows this path: input port and service in `application`, a controller endpoint in `infraestructure`, and a `@Bean` in `UseCaseConfig`. A new external service gets an output-port interface in `application/port/out` and a `@Component` adapter in `infraestructure/adapter/out/<name>/`.

## Test conventions

These come from `prompts/prompt-tests-*.md`, which hold the full specs.

- Test classes go in the same package as the class under test, with the `Test` suffix. Do not use `@DisplayName` or `@Nested`.
- **domain / application**: method names follow `should_<action>_when_<condition>`. Domain tests use JUnit only, with no mocks.
- **application services**: use `@ExtendWith(MockitoExtension.class)`, put `@Mock` on the output ports only, and put `@InjectMocks` on the service. Domain objects stay real. Mark the steps with `// Arrange` / `// Act` / `// Assert` comments, and verify port interactions, including `never()` on error paths. Use `argThat` instead of `ArgumentCaptor`.
- **infraestructure**: method names follow `methodUnderTest_shouldBehavior` (for example `handleNotFound_shouldReturn404`). Create classes that have no collaborators with `new`, and use Mockito for the controller and adapter. Fixture helpers use the `create...` prefix. The controller's `Location` header needs a fake `MockHttpServletRequest` + `RequestContextHolder` context, reset in `finally`.

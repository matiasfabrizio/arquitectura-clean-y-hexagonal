### P1 ¿Por qué SecurityConfig no está en domain o application? ###
Porque SecurityConfig toca clases de Spring Security, y estas pertenecen al framework de Spring Boot el cual pertenece a la capa de infraestructura.

Si en algún momento se desease cambiar de framework o de configuración de seguridad, el dominio de la app queda intacto y puede ser reutilizado.

### P2 ¿Qué pasaría si un usuario envía un JWT válido pero sin ningún rol de Keycloak? ¿Podría acceder a GET /api/orders/{id}? Justifica tu respuesta con la configuración actual. ###
Gracias a que se está usando la configuración .authenticated() en vez de hasAnyRole("ROL_EJEMPLO") o hasRole(), cualquier usuario válido dentro de keycloak puede acceder a ese endpoint.

Si, por otro lado, un usuario con rol USER quiere acceder a un endpoint protegido con ADMIN exclusivamente, no tendría acceso gracias a la protección de hasRole() que hace que sólo "ADMINS" puedan entrar a ese endpoint.

### P3 ¿Qué función cumple KeycloakRoleConverter y qué sucedería si no existiera? ###
1. Primero se asegura de que extraiga el acceso al realm de la clave jwt.
2. Luego verifica que la clave no sea nula y que sea verdadero que contenga la clave "roles". Si no se cumple alguna de estas, devuelve una colección vacía.
3. Después crea una lista de Strings de los roles haciendo typecasting.
4. Finalmente, usando programación funcional verifica que los roles empiecen con "ROLE_". Si no es así, lo agrega como prefijo y devuelve la colección con todos los roles con el prefijo asignado como SimpleGrantedAuthority.

La clase hace esto pues Spring Security espera que los roles empiecen con el prefijo "ROLE_". Si no lo hacen, Spring Security no los acepta.

Ahora, si KeycloakRoleConverter no existiera, se tendría que configurar Spring Security manualmente para asignar y leer los roles de forma estandarizada, de lo contrario Spring Security usaría su propio lector impidiendo que la aplicación funcione como debería. 

A lo mucho, los usuarios autenticados podrían ejecutar los endpoints de .authenticated() pero ningún endpoint protegido por hasAnyRole() funcionaría, pues retornaría 403 Forbidden.

### P4 Explica la diferencia entre 401 Unauthorized y 403 Forbidden en el contexto de este proyecto. Da un ejemplo concreto de cuándo ocurre cada uno. ###
401 Unauthorized - Una persona que tiene un token inválido o no presenta token.

403 Forbidden - El rol (válido) otorgado al servidor de identificación no tiene el rol necesario para ejecutar la acción.

**Ejemplos:**

- (401 Unauthorized): Si alguien con token expirado, sin token, o con token modificado ejecuta algún endpoint (que no sea /actuator/** pues es el único con .permitAll()), tendrá 401 pues todos los demás endpoints están protegidos por ".anyRequest().authenticated()"
- (403 Forbidden): Si un USER quiere acceder a .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("ADMIN") con POST, no podrá pues ese endpoint está protegido para ser usado sólo por ADMIN.

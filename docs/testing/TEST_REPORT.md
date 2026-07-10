# Reporte de Pruebas — Buganvilla Tours

**Fecha:** 2026-07-10  
**Rama:** `refactor/angular-backend-whatsapp`

---

## Backend (JUnit 5 + Mockito + Spring Boot Test)

**Resultado total:** 140 tests ejecutados · 114 pasando · 26 fallando (pre-existentes)

### Tests nuevos (todos pasan)

| Test | Tipo | Casos | Resultado |
|------|------|-------|-----------|
| `WhatsAppProviderTest` | Unit (Mockito) | 6 | ✅ PASS |
| `ReservaServiceConcurrencyTest` | Unit (Mockito) | 3 | ✅ PASS |
| `ReservaServiceImplTest.testCancelarReserva` | Unit (Mockito) | 1 | ✅ PASS |
| `ReservaServiceImplTest.testCancelarReservaYaCancelada` | Unit (Mockito) | 1 | ✅ PASS |

### Fallas pre-existentes (no introducidas en esta rama)

| Test | Causa | Severidad |
|------|-------|-----------|
| `ReservaControllerIntegrationTest` | FK constraint en `@BeforeEach` teardown: `PAGOS` referencia `RESERVAS` | Media |
| `UsuarioControllerIntegrationTest` | FK constraint teardown: `RESERVAS` referencia `USUARIOS` | Media |
| `LugarControllerIntegrationTest` | FK constraint teardown: paquetes referencian lugares | Media |
| `PagoServiceImplTest.testRechazarPago` | `inventario` es null en fixture — falta setup completo | Baja |

**Causa raíz de los fallos FK:** Los tests de integración no eliminan entidades dependientes antes de eliminar la entidad padre. Solución: añadir `@DirtiesContext` o borrar en orden inverso de dependencia en `@AfterEach`.

---

## Angular (Karma + Jasmine)

**Resultado:** 10/10 tests pasan

| Test | Componente/Service | Casos | Resultado |
|------|-------------------|-------|-----------|
| `app.component.spec.ts` | AppComponent | 1 | ✅ PASS |
| `auth.service.spec.ts` | AuthService | 4 | ✅ PASS |
| `auth.interceptor.spec.ts` | authInterceptor | 2 | ✅ PASS |
| `admin.guard.spec.ts` | adminGuard | 2 | ✅ PASS |

---

## Angular Build

```
ng build --configuration=production
✓ Built successfully (con warnings de budget en CSS — Bootstrap incluido)
16 lazy-loaded chunks generados correctamente
```

---

## Pendiente

- [ ] E2E con Playwright (flujos completos: registro → reserva → pago)
- [ ] Fix fallas pre-existentes de FK en tests de integración
- [ ] `ReservaServiceConcurrencyTest` debe actualizarse a `assertEquals(1, successes.get())` tras implementar `@Lock` en `InventarioPaqueteRepository`
- [ ] Tests de contrato Angular ↔ backend (verificar que todos los endpoints existen)

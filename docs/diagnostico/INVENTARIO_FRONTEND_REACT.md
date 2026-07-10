# Inventario Frontend React — Buganvilla Tours

**Fecha:** 2026-07-10  
**Tecnologías:** React 19.2 + Vite 7.2.2 + Bootstrap 5.3.8 + Axios 1.13.2 + React Router 7.9.6

---

## 1. Páginas y Rutas

| Ruta | Archivo | Descripción | Acceso |
|------|---------|-------------|--------|
| `/` | `pages/Home.jsx` | Landing page con hero, stats, paquetes destacados, testimonios, mapa | Público |
| `/paquetes` | `pages/Packages.jsx` | Grid de paquetes con filtros y modal de detalles | Público |
| `/reservas` | `pages/Reservations.jsx` | Formulario de reserva + integración MercadoPago | Autenticado |
| `/admin` | `pages/Admin.jsx` | Panel administrativo completo | Solo ADMIN |
| `/nosotros` | `pages/About.jsx` | Información corporativa (misión, visión, valores) | Público |
| `/contacto` | `pages/Contact.jsx` | Formulario de contacto (simulado, no conecta al backend) | Público |
| `/register` | `pages/Register.jsx` | Registro de nuevos usuarios | Público |

---

## 2. Componentes por Módulo

### Auth (`components/auth/`)
| Componente | Función |
|-----------|---------|
| `LoginModal.jsx` | Modal de login; redirige a `/admin` si rol=admin |
| `RegisterModal.jsx` | Modal de registro alternativo al de `/register` (BUG: auto-login no funciona por error en llamada) |
| `AuthRequiredModal.jsx` | Modal que se muestra al intentar reservar sin autenticación |

**BUG DETECTADO:** En `RegisterModal.jsx`, el auto-login tras registro llama a `login(token, userData)` con 2 parámetros, pero `useAuth().login()` solo acepta `(email, password)`. La llamada nunca ejecuta el auto-login correctamente.

### Common (`components/common/`)
| Componente | Función |
|-----------|---------|
| `Header.jsx` | Navbar con menú, dropdown de usuario, botón login/logout |
| `Footer.jsx` | Footer con links e información de contacto |
| `LoadingSpinner.jsx` | Spinner de carga reutilizable |
| `RegisterModal.jsx` | **DUPLICADO** de `components/auth/RegisterModal.jsx` — mismo archivo en dos ubicaciones |

### Home (`components/home/`)
| Componente | Función |
|-----------|---------|
| `HeroSection.jsx` | Banner principal con CTA de "Ver Paquetes" |
| `StatsSection.jsx` | Contadores animados (años de experiencia, tours, clientes, destinos) |
| `FeaturedPackages.jsx` | Grilla de paquetes destacados (consume `/paquetes/activos`) |
| `Testimonials.jsx` | Carrusel de testimonios de clientes |
| `MapSection.jsx` | Sección de mapa de ubicación |

### Packages (`components/packages/`)
| Componente | Función |
|-----------|---------|
| `PackageGrid.jsx` | Grid de cards de paquetes |
| `PackageModal.jsx` | Modal con detalles completos del paquete, selector de fechas/inventario, botón de reserva |
| `PackageFilters.jsx` | Filtros por búsqueda de texto y categoría |

### Reservations (`components/reservations/`)
| Componente | Función |
|-----------|---------|
| `ReservationForm.jsx` | Formulario principal: datos del titular, consulta DNI RENIEC, selector de inventario, cantidad de personas |
| `ReservationSummary.jsx` | Panel lateral con resumen de la reserva y precio total |
| `ConfirmationModal.jsx` | Modal de confirmación con opción de pagar con MercadoPago |

### Admin (`components/admin/`)
| Componente | Función | Estado |
|-----------|---------|--------|
| `AdminSidebar.jsx` | Navegación lateral del panel | Funcional |
| `Dashboard.jsx` | Panel con estadísticas y actividad reciente | ❌ **Mock** (datos con `Math.random()`) |
| `PackagesManagement.jsx` | CRUD de paquetes (list, create, edit, delete) | ✅ Funcional |
| `PackageFormModal.jsx` | Modal de formulario para crear/editar paquetes | ✅ Funcional |
| `PackagesInventory.jsx` | Gestión de inventario y cupos por paquete | ✅ Funcional |
| `ReservationsManagement.jsx` | Tabla de gestión de reservas | ❌ **Mock** (datos hardcodeados, acciones solo alertas) |
| `Reports.jsx` | Descarga de reportes Excel/PDF | ✅ Parcial (descarga funciona, stats mockeadas) |
| `ProductsInventory.jsx` | Inventario de productos | ❌ No implementado |
| `Transports.jsx` | Gestión de transportes | ❌ No implementado |

---

## 3. Estado Global (AuthContext)

**Archivo:** `context/AuthContext.jsx`

| Estado | Tipo | Persistencia |
|--------|------|-------------|
| `user` | Object | `localStorage['buganvilla_user']` |
| `loading` | Boolean | Solo en memoria |

**Métodos:**
- `login(email, password)` → POST `/auth/login` → guarda en localStorage
- `logout()` → limpia localStorage → `window.location.href = '/'`
- `register(userData)` → POST `/auth/register` → guarda en localStorage
- `saveSession(token, userData)` → persiste en localStorage

**Computed:**
- `isAuthenticated` → `!!user`
- `isAdmin` → `user.rol === 'admin' || user.rol === 'ADMIN'`

**Claves en localStorage:**
- `buganvilla_token` → JWT string
- `buganvilla_user` → JSON del objeto usuario

---

## 4. Servicios HTTP (`services/api.js`)

**Configuración Axios:**
- Base URL: `import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'`
- Interceptor request: agrega `Authorization: Bearer {token}`
- Interceptor response: en 401, limpia localStorage y redirige a `/` (con flag anti-redirect-loop)

**APIs exportadas:**

```javascript
authAPI       → login, register, getProfile, checkToken
packagesAPI   → getAll, getActive, getById, search, create, update, delete
lugaresAPI    → getAll, getById
inventoryAPI  → getAvailable, getByPaquete, getAvailableByPaquete, getNextDepartures
reservationsAPI → create, getMis, getById
reportsAPI    → getExcel, getPdf
apisNetAPI    → getDni
mercadoPagoAPI → crearPreferencia
```

---

## 5. Flujos de Autenticación

### Registro
```
/register → Register.jsx → POST /auth/register
→ AuthContext.saveSession(token, usuario)
→ localStorage guarda token + user
→ navigate('/')
```

### Login
```
Header → LoginModal → POST /auth/login
→ AuthContext.saveSession(token, usuario)
→ if (admin) navigate('/admin') else permanecer en página
```

### Auto-login tras registro (modal)
```
RegisterModal → POST /auth/register → BUG: intenta login(token, userData) con parámetros incorrectos
→ auto-login NO funciona correctamente en RegisterModal
→ En Register.jsx (página) SÍ funciona correctamente
```

### Logout
```
Header dropdown → AuthContext.logout()
→ localStorage.removeItem('buganvilla_token', 'buganvilla_user')
→ window.location.href = '/'
```

### Expiración de token (401)
```
Cualquier request → Response interceptor → 401
→ si !isRedirecting: isRedirecting = true
→ localStorage.removeItem(...)
→ setTimeout(100ms) → navigate('/')
```

---

## 6. Protección de Rutas

**No existe componente `ProtectedRoute`.** La protección se implementa de forma ad-hoc:

| Mecanismo | Dónde | Descripción |
|-----------|-------|-------------|
| Verificación en componente | `Admin.jsx` | Verifica `user.rol === 'admin'`, muestra "Acceso Denegado" si no |
| Modal de autenticación | `PackageModal.jsx` | Muestra `AuthRequiredModal` si el usuario no está logueado e intenta reservar |
| Guards de backend | Todos los endpoints protegidos | Rechazan request sin JWT válido |

**Deficiencia:** No hay redirect automático a login al intentar acceder a rutas protegidas. Solo se muestra un mensaje de error o un modal.

---

## 7. Variables de Entorno

| Variable | Uso | Configuración actual |
|---------|-----|---------------------|
| `VITE_API_BASE_URL` | URL base de la API | No definida → usa `http://localhost:8080/api` por defecto |

**No existe `.env` ni `.env.local` en el proyecto.** Todo usa valores por defecto en el código.

---

## 8. Assets y Estilos

**Paleta de colores principal:**
```css
--primary-color: #4c1d95;   /* Violeta profundo */
--accent-color: #db2777;    /* Rosado Buganvilla */
--text-dark: #1f2937;
--bg-color: #f3f4f6;
```

**Archivos de estilos:**
- `styles/main.css` — Estilos globales y variables CSS
- `styles/buganvilla-theme.css` — Tema específico del proyecto
- `App.css` — Estilos del componente raíz
- `index.css` — Reset y estilos base

**Assets en `public/`:**
- `public/images/` — Imágenes del sitio
- `public/videos/` — Videos del hero section

---

## 9. Funcionalidades Incompletas

| Funcionalidad | Componente | Estado | Nota |
|--------------|-----------|--------|------|
| Dashboard admin con datos reales | `Dashboard.jsx` | ❌ Mock | Usa `Math.random()` y hardcoded |
| Gestión de reservas admin | `ReservationsManagement.jsx` | ❌ Mock | No conecta a `/api/reservas` |
| Formulario de contacto | `Contact.jsx` | ❌ Simulado | `setTimeout(2000)` sin backend |
| Auto-login en modal de registro | `RegisterModal.jsx` | ❌ Bug | Parámetros incorrectos en login() |
| Gestión de usuarios | (ausente) | ❌ No existe | No hay UI para `/api/usuarios` |
| Gestión de lugares | (ausente) | ❌ No existe | No hay UI para `/api/lugares` |
| Inventario de productos | `ProductsInventory.jsx` | ❌ Vacío | Archivo sin implementar |
| Transportes | `Transports.jsx` | ❌ Vacío | Archivo sin implementar |

---

## 10. Bugs Identificados

| Severidad | Archivo | Descripción |
|-----------|---------|-------------|
| Alto | `RegisterModal.jsx` | Auto-login llama `login(token, userData)` pero `login()` espera `(email, password)` |
| Alto | `ReservationForm.jsx` | Código duplicado para consulta DNI (2 funciones idénticas) |
| Alto | `ReservationForm.jsx` | Comentario "Bypaseo de validación de fecha" — toma primer inventario disponible sin validar |
| Medio | `components/common/RegisterModal.jsx` | Archivo duplicado de `components/auth/RegisterModal.jsx` |
| Medio | `Admin.jsx` | `user.rol.toLowerCase() !== 'admin'` falla si `user.rol` es `undefined` |
| Bajo | `LoginModal.jsx` | Compara rol sin normalizar antes de comparar (puede fallar según capitalización) |

---

## 11. Dependencias

```json
{
  "react": "19.2.0",
  "react-dom": "19.2.0",
  "react-router-dom": "7.9.6",
  "bootstrap": "5.3.8",
  "react-bootstrap": "2.10.10",
  "axios": "1.13.2"
}
```

**Devdependencies:**
```json
{
  "vite": "7.2.2",
  "@vitejs/plugin-react": "^4.5.2",
  "eslint": "^9.29.0"
}
```

**Sin TypeScript.** Todo el código es JavaScript/JSX puro.

**Auditoría de seguridad npm:** Se detectaron vulnerabilidades (ejecutar `npm audit` para detalles). No se actualiza sin validación en la Fase 4.

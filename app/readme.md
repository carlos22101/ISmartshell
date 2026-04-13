# iSmartShell 🛒

Marketplace local click-and-collect para Suchiapa, Chiapas. Conecta compradores y vendedores locales con geolocalización, pedidos por QR y notificaciones push.


---

## 🏗️ Arquitectura

```
MVVM + Clean Architecture
├── features/
│   ├── auth/         → Login, Registro
│   ├── buyer/        → Tiendas, Productos, Pedidos
│   ├── seller/       → Mis Tiendas, Productos, Órdenes
│   ├── qr_scanner/   → Historial de escaneos
│   └── maps/         → Mapa Mapbox
└── core/
    ├── database/     → Room (QrScanEntity, QrScanDao)
    ├── di/           → Módulos Hilt
    ├── managers/     → Cámara, GPS, Vibración
    ├── network/      → Retrofit ApiService
    ├── notifications/→ FCM Service y Token Sync
    ├── workers/      → WorkManager SyncOrdersWorker
    └── ui/components/→ Componentes reutilizables
```

Cada feature tiene tres capas independientes:
- **data** — repositorios, DTOs, mappers
- **domain** — entidades, use cases (sin dependencias Android)
- **presentation** — ViewModel + StateFlow + Compose Screen

---

## 🛠️ Stack tecnológico

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material3 | BOM 2024.11.00 |
| DI | Hilt | 2.53 |
| Base de datos | Room | 2.6.1 |
| Red | Retrofit + OkHttp | 3.0.0 / 4.12.0 |
| Mapas | Mapbox | 10.16.0 |
| Notificaciones | Firebase FCM | BOM 33.1.0 |
| Background | WorkManager | 2.9.1 |
| Cámara | CameraX + ML Kit | 1.3.4 / 17.3.0 |
| Ubicación | Play Services Location | 21.3.0 |
| Build | AGP | 8.6.0 |
| KSP | KSP | 2.0.21-1.0.27 |

---

## ⚙️ Configuración del entorno

### Requisitos
- Android Studio Ladybug o superior
- JDK 17
- Android SDK 35
- Cuenta de Mapbox
- Proyecto Firebase con FCM habilitado

### 1. Clonar el repositorio

```bash
git clone https://github.com/carlos22101/ISmartshell.git
cd ismartshell
```

### 2. Configurar Mapbox

Crea o edita el archivo `local.properties` en la raíz del proyecto:

```properties
MAPBOX_PUBLIC_TOKEN=pk.eyJ1Ijoixxxxxxxx...
```

También agrega el token secreto de Mapbox en `~/.gradle/gradle.properties` para descargar el SDK:

```properties
MAPBOX_DOWNLOADS_TOKEN=sk.eyJ1Ijoixxxxxxxx...
```

### 3. Configurar Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Descarga el archivo `google-services.json` de tu proyecto
3. Colócalo en `app/google-services.json`

### 4. Configurar la URL del backend

En `core/network/NetworkModule.kt` actualiza la base URL:

```kotlin
private const val BASE_URL = "https://TU_BACKEND_AQUI/"
```

### 5. Compilar y ejecutar

```bash
./gradlew assembleDebug
```

O simplemente presiona **Run** en Android Studio con un emulador o dispositivo conectado.

---

## 🔑 Variables de entorno requeridas

| Variable | Archivo | Descripción |
|----------|---------|-------------|
| `MAPBOX_PUBLIC_TOKEN` | `local.properties` | Token público de Mapbox para el mapa |
| `MAPBOX_DOWNLOADS_TOKEN` | `~/.gradle/gradle.properties` | Token secreto para descargar el SDK de Mapbox |
| `google-services.json` | `app/` | Configuración de Firebase (FCM) |

---

## 🔌 API Backend

La app consume una API REST en **Go + Gin + PostgreSQL**.

**Base URL:** `[TU_URL_BACKEND_AQUÍ]`

### Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/auth/register` | Registro de usuario |
| POST | `/api/v1/auth/login` | Login, devuelve JWT |
| GET | `/api/v1/businesses` | Tiendas cercanas (lat, lng, radius) |
| GET | `/api/v1/businesses/mine` | Tiendas del vendedor autenticado |
| POST | `/api/v1/businesses` | Crear tienda |
| POST | `/api/v1/orders` | Crear pedido (genera QR) |
| GET | `/api/v1/orders/my` | Pedidos del comprador |
| POST | `/api/v1/orders/scan` | Validar QR del comprador |
| POST | `/api/v1/orders/{id}/ready` | Marcar pedido como listo |
| POST | `/api/v1/users/fcm-token` | Registrar token FCM |

Todos los endpoints protegidos requieren header:
```
Authorization: Bearer {JWT}
```

---

## 📦 Módulos del proyecto

### `features/auth`
Registro y login. Usa `sealed class AuthUiState` (Idle/Loading/Success/Error) y `LoginFormState`/`RegisterFormState` como StateFlows separados en el ViewModel.

### `features/buyer`
Pantalla principal del comprador. Lista tiendas cercanas con filtro por categoría, detalle de tienda con productos, creación de pedidos con QR. Todo el estado en `HomeBuyerUiState` con `MutableStateFlow`.

### `features/seller`
Gestión completa del vendedor. CRUD de tiendas y productos, visualización de órdenes, escaneo QR para validar entregas. Estado en `CreateStoreUiState`.

### `features/qr_scanner`
Historial de escaneos QR almacenado en Room. `QrHistoryViewModel` usa `stateIn` con `SharingStarted.WhileSubscribed(5_000)` para convertir el `Flow<List>` de Room a StateFlow.

### `features/maps`
Mapa Mapbox con tiendas cercanas. `NearbyStoresMapScreen` para vista de lista en mapa, `StoreMapScreen` para ubicación individual de tienda.

### `core/database`
Room database con una entidad: `QrScanEntity` (historial de QR). DAO con `Flow` reactivo y `OnConflictStrategy.REPLACE`.

### `core/managers`
- **QrScannerManager** — CameraX + ML Kit, `STRATEGY_KEEP_ONLY_LATEST`
- **LocationManager** — FusedLocationProviderClient con `callbackFlow`
- **VibrationManager** — patrones hápticos diferenciados (confirmación/error)

### `core/workers`
`SyncOrdersWorker` — `CoroutineWorker` con `@HiltWorker`. Se ejecuta cada 15 minutos con restricciones de red y batería.

### `core/notifications`
`FcmService` — recibe y muestra notificaciones push. `FcmTokenSync` — sincroniza el token del dispositivo al backend tras login.

---

## 🧪 Verificar WorkManager

Para verificar que `SyncOrdersWorker` está corriendo:

1. Abre **Android Studio**
2. Ve a `View → Tool Windows → App Inspection`
3. Selecciona tu dispositivo/emulador
4. Pestaña **WorkManager**

Deberías ver `sync_orders_worker` con estado `ENQUEUED` o `RUNNING`.

---

## 📄 Licencia

Proyecto académico — Universidad Politécnica de Chiapas  
Ingeniería en Software — Programación para Móviles I

---

> Desarrollado por
>
>-Carlos Daniel Solis Aguilar
>-Rodrigo Emilio Campuzano Culebro—
> UP Suchiapa, Chiapas 🌿
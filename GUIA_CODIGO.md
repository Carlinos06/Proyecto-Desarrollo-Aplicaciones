# Guía de estudio del código — CorpalivApp

Esta guía explica **qué hace cada parte del proyecto**, **cómo se conectan los archivos** y **qué interacción de la aplicación produce cada fragmento de código**.

La idea no es memorizar líneas, sino entender el recorrido:

```text
Usuario toca la pantalla
        ↓
Composable (UI)
        ↓
ViewModel (estado y lógica de presentación)
        ↓
Repositorio / Validaciones (lógica)
        ↓
DataStore / almacenamiento interno (persistencia)
        ↓
StateFlow actualiza el estado
        ↓
Compose vuelve a dibujar la pantalla
```

---

## 1. Vista general de la aplicación

La app tiene dos grandes etapas:

```text
┌─────────────────────────────────────────────────────────────┐
│ MainActivity                                                │
│   └── AppRoot                                               │
│         ├── Cargando  → PantallaCarga                       │
│         ├── SinSesion → AuthNavigation                      │
│         │                ├── LoginScreen                    │
│         │                └── RegisterScreen                 │
│         └── Activa    → MainNavigation                      │
│                          ├── BarraSuperior                  │
│                          ├── Menú lateral (drawer)          │
│                          └── NavHost                        │
│                               ├── Inicio                    │
│                               ├── Quiénes somos             │
│                               ├── Programas                 │
│                               ├── Productos y servicios     │
│                               ├── Historias de impacto      │
│                               ├── Formas de colaborar       │
│                               ├── Contacto                  │
│                               └── Mi perfil                 │
└─────────────────────────────────────────────────────────────┘
```

La aplicación **siempre empieza en `AppRoot`**, que decide si mostrar el login o la aplicación principal según exista o no una sesión guardada.

---

## 2. Estructura del proyecto

Ruta base:

```text
app/src/main/java/com/example/corpalivapp/
```

### Archivos de entrada y navegación

| Archivo | Responsabilidad |
|---|---|
| `MainActivity.kt` | Punto de entrada Android. Configura el tema y llama a `AppRoot()`. |
| `ui/navigation/AppRoot.kt` | Decide entre carga, login/registro o app principal. |
| `AppNavegation.kt` | Navegación principal, menú lateral, rutas y pantalla de perfil. |
| `BarraSuperior.kt` | Barra superior con menú y acceso al perfil. |
| `HomeScreen.kt` | Pantalla de inicio. |
| `Pantallas.kt` | Pantalla simple reutilizable para secciones informativas. |
| `Colores.kt` | Paleta de colores de la app. |

### Autenticación

| Archivo | Responsabilidad |
|---|---|
| `ui/auth/AuthViewModel.kt` | Estado de login/registro, validaciones y llamadas al repositorio. |
| `ui/auth/LoginScreen.kt` | Pantalla de inicio de sesión. |
| `ui/auth/RegisterScreen.kt` | Formulario de registro. |
| `ui/auth/components/ComponentesAuth.kt` | Componentes reutilizables de formularios. |

### Perfil

| Archivo | Responsabilidad |
|---|---|
| `ui/perfil/PerfilViewModel.kt` | Datos del usuario, portada, avatar y mensajes. |
| `ui/perfil/PerfilScreen.kt` | Interfaz del perfil y selección de imágenes. |
| `FotoPerfil.kt` | Círculo con foto o ícono por defecto. |
| `CamaraUriFactory.kt` | Crea la URI temporal para la cámara con `FileProvider`. |

### Datos, seguridad y validaciones

| Archivo | Responsabilidad |
|---|---|
| `data/model/Usuario.kt` | Modelos `Usuario` y `Cuenta`. |
| `data/local/UserPreferences.kt` | Persistencia con DataStore. |
| `data/local/ImageStore.kt` | Copia imágenes al almacenamiento interno. |
| `data/repository/AuthRepository.kt` | Registro, login, sesión e imágenes. |
| `data/security/PasswordHasher.kt` | Hash de contraseñas con PBKDF2 + salt. |
| `domain/Validaciones.kt` | Reglas de validación puras. |

### Recursos y pruebas

| Archivo | Responsabilidad |
|---|---|
| `app/src/main/AndroidManifest.xml` | Permiso de cámara, FileProvider y actividad principal. |
| `app/src/main/res/xml/file_paths.xml` | Ruta de caché que comparte la cámara. |
| `app/src/test/.../ValidacionesTest.kt` | Pruebas unitarias de las validaciones. |
| `app/build.gradle.kts` | Configuración Android y dependencias. |

---

## 3. Flujo de arranque

### 3.1 `MainActivity.kt`

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CorpalivAppTheme {
                AppRoot()
            }
        }
    }
}
```

Qué hace:

1. `enableEdgeToEdge()` permite que la app use toda la pantalla.
2. `setContent { ... }` activa Jetpack Compose.
3. `CorpalivAppTheme` aplica el tema.
4. `AppRoot()` inicia la lógica de sesión.

### 3.2 `ui/navigation/AppRoot.kt`

`AppRoot` crea un `AuthViewModel` y observa su estado de sesión:

```kotlin
val authViewModel: AuthViewModel = viewModel()
val sesion by authViewModel.sesion.collectAsState()
```

Luego usa `AnimatedContent` para cambiar entre pantallas con un fundido:

```kotlin
AnimatedContent(targetState = sesion) { estado ->
    when (estado) {
        SesionEstado.Cargando -> PantallaCarga()
        SesionEstado.SinSesion -> AuthNavigation(authViewModel)
        is SesionEstado.Activa -> MainNavigation(
            authViewModel = authViewModel,
            email = estado.email
        )
    }
}
```

### 3.3 Estados de sesión

En `AuthViewModel.kt`:

```kotlin
sealed interface SesionEstado {
    data object Cargando : SesionEstado
    data object SinSesion : SesionEstado
    data class Activa(val email: String) : SesionEstado
}
```

- `Cargando`: todavía se está leyendo DataStore.
- `SinSesion`: no hay correo guardado; se muestra login.
- `Activa`: hay una sesión; se muestra la app principal.

Al iniciar, `AuthViewModel` lee el flujo de sesión:

```kotlin
init {
    viewModelScope.launch {
        repository.sesionEmail().collect { email ->
            _sesion.value = if (email.isNullOrBlank()) {
                SesionEstado.SinSesion
            } else {
                SesionEstado.Activa(email)
            }
        }
    }
}
```

**Interacción visible:** si cierras y vuelves a abrir la app, DataStore recuerda el correo y entras directamente a la aplicación principal.

---

## 4. Patrón MVVM y flujo unidireccional

El proyecto usa **MVVM**:

```text
Modelo         → datos, repositorios, DataStore
Vista          → Composables
ViewModel      → estado y lógica de presentación
```

Flujo de una interacción:

```text
1. El usuario escribe o presiona un botón.
2. El Composable llama a una función del ViewModel.
3. El ViewModel valida y actualiza un StateFlow.
4. El ViewModel llama al repositorio si necesita guardar/leer.
5. El repositorio usa DataStore o el almacenamiento interno.
6. El StateFlow cambia.
7. Compose observa el StateFlow y vuelve a dibujar la parte afectada.
```

Ejemplo real del login:

```kotlin
// La vista observa el estado
val estado by viewModel.login.collectAsState()

// La vista envía eventos
CampoTextoValidado(
    valor = estado.email,
    onValorChange = viewModel::onLoginEmailChange
)

BotonPrincipal(
    texto = "Iniciar sesión",
    onClick = viewModel::iniciarSesion
)
```

Responsabilidades:

| Capa | No debería hacer |
|---|---|
| Composable | Validar reglas de negocio o escribir en DataStore. |
| ViewModel | Dibujar componentes o conocer colores. |
| Repositorio | Mostrar mensajes en pantalla. |
| DataStore | Decidir si un formulario es válido. |

Esta separación es la razón por la que `Validaciones.kt` no importa nada de Compose.

---

## 5. Capa de datos

### 5.1 Modelos: `data/model/Usuario.kt`

```kotlin
data class Usuario(
    val nombre: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String, // dd/MM/yyyy
    val notificaciones: Boolean
)

data class Cuenta(
    val usuario: Usuario,
    val passwordHash: String,
    val salt: String
)
```

- `Usuario` son los datos que se muestran en la app.
- `Cuenta` es la representación interna que se guarda.
- `passwordHash` y `salt` nunca son la contraseña original.

### 5.2 Persistencia: `data/local/UserPreferences.kt`

DataStore se declara una sola vez:

```kotlin
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "corpaliv_prefs"
)
```

Claves usadas:

| Clave | Contenido |
|---|---|
| `usuarios_json` | Array JSON con todas las cuentas. |
| `sesion_email` | Correo de la sesión activa. |
| `portada_path` | Ruta absoluta de la imagen de portada. |
| `avatar_path` | Ruta absoluta de la foto de perfil. |

Flujos observables:

```kotlin
val sesionEmail: Flow<String?> = context.dataStore.data.map { it[keySesion] }
val portadaPath: Flow<String?> = context.dataStore.data.map { it[keyPortada] }
val avatarPath: Flow<String?> = context.dataStore.data.map { it[keyAvatar] }
```

Cada cuenta se guarda como un objeto JSON:

```json
{
  "nombre": "José Barrientos",
  "email": "correo@dominio.cl",
  "telefono": "912345678",
  "fechaNacimiento": "01/01/2000",
  "notificaciones": true,
  "passwordHash": "hash-generado",
  "salt": "salt-generado"
}
```

Funciones importantes:

- `obtenerCuentas()`: lee el JSON y lo convierte en `List<Cuenta>`.
- `buscarCuenta(email)`: busca sin distinguir mayúsculas/minúsculas.
- `guardarCuenta(cuenta)`: reemplaza la cuenta si el correo ya existe y guarda el array actualizado.
- `guardarSesion(email)`: guarda o elimina la sesión.
- `guardarPortada(path)` / `guardarAvatar(path)`: guardan rutas de imágenes.

### 5.3 Repositorio: `data/repository/AuthRepository.kt`

El repositorio concentra las operaciones de negocio:

```kotlin
suspend fun registrar(...): Result<Usuario>
suspend fun iniciarSesion(email: String, password: String): Result<Usuario>
suspend fun cerrarSesion()
suspend fun obtenerUsuario(email: String): Usuario?
suspend fun guardarPortada(path: String?)
suspend fun guardarAvatar(path: String?)
```

Registro:

1. Normaliza el correo: `trim().lowercase()`.
2. Verifica que no exista otra cuenta con ese correo.
3. Genera un salt nuevo.
4. Calcula el hash en `Dispatchers.Default`.
5. Crea el `Usuario`.
6. Guarda la `Cuenta`.
7. Guarda la sesión.
8. Devuelve `Result.success(usuario)`.

Login:

1. Busca la cuenta por correo.
2. Verifica la contraseña con `PasswordHasher.verificar`.
3. Si es correcta, guarda la sesión.
4. Si falla, devuelve un `Result.failure` con un mensaje.

`Result` permite manejar éxito y error sin lanzar excepciones hacia la UI.

### 5.4 Seguridad: `data/security/PasswordHasher.kt`

La contraseña **no se cifra de forma reversible**: se convierte en un **hash** con PBKDF2.

```kotlin
private const val ITERACIONES = 120_000
private const val LONGITUD_CLAVE = 256
private const val ALGORITMO_MODERNO = "PBKDF2WithHmacSHA256"
private const val ALGORITMO_COMPATIBLE = "PBKDF2WithHmacSHA1"
```

Proceso:

```text
Contraseña + salt aleatorio
        ↓
PBKDF2 (120.000 iteraciones)
        ↓
Hash Base64
        ↓
Se guarda hash + salt, nunca la contraseña
```

Detalles:

- `generarSalt()` crea 16 bytes aleatorios con `SecureRandom`.
- Cada usuario tiene su propio salt.
- `hash()` deriva la clave.
- `verificar()` calcula el hash de la contraseña ingresada y lo compara.
- `compararTiempoConstante()` evita comparaciones que filtren información por tiempo.
- En API 24–25 se usa `PBKDF2WithHmacSHA1` si SHA256 no está disponible.

> Aclaración: en el README se usa la palabra “cifrada”, pero técnicamente es un **hash**. No se puede “descifrar” para recuperar la contraseña; solo se puede verificar si coincide.

### 5.5 Imágenes: `data/local/ImageStore.kt`

```kotlin
val directorio = File(context.filesDir, "imagenes").apply { mkdirs() }
val destino = File(directorio, "${prefijo}_${System.currentTimeMillis()}.jpg")
```

- Copia la imagen desde la URI original al almacenamiento privado de la app.
- Así la imagen sigue disponible aunque el archivo original se mueva o se borre.
- `eliminar(path)` borra la imagen anterior cuando se reemplaza.
- Las imágenes quedan en `filesDir/imagenes`.

---

## 6. Validaciones y pruebas

### 6.1 `domain/Validaciones.kt`

Convención:

```text
null      → el valor es válido
"mensaje" → hay un error y se muestra ese mensaje
```

Reglas:

| Campo | Regla |
|---|---|
| Nombre | Obligatorio, mínimo 3 caracteres, letras/espacios/guiones/apóstrofes. |
| Correo | Formato `usuario@dominio.cl`. |
| Teléfono | Entre 8 y 9 dígitos. |
| Fecha | `dd/MM/yyyy`, fecha real, no futura, edad entre 14 y 120 años. |
| Contraseña | Mínimo 8 caracteres, mayúscula, minúscula y número. |
| Confirmación | Debe coincidir con la contraseña. |
| Términos | Debe estar aceptado. |
| Fortaleza | Devuelve 0, 1, 2 o 3 según longitud, mayúsculas/minúsculas y símbolos. |

Ejemplo:

```kotlin
fun email(valor: String): String? {
    val texto = valor.trim()
    return when {
        texto.isEmpty() -> "El correo es obligatorio."
        !EMAIL_REGEX.matches(texto) -> "Ingresa un correo válido (ejemplo@dominio.cl)."
        else -> null
    }
}
```

### 6.2 `app/src/test/.../ValidacionesTest.kt`

Las pruebas verifican:

- Nombre válido e inválido.
- Correo válido e inválido.
- Teléfono válido e inválido.
- Fecha válida, futura y con formato incorrecto.
- Contraseña que cumple y no cumple.
- Confirmación de contraseña.
- Fortaleza de contraseña.

Ejecutar:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**Para estudiar:** abre `Validaciones.kt` y `ValidacionesTest.kt` uno al lado del otro. Cada `assertNull` corresponde a un caso válido y cada `assertNotNull` a un caso inválido.

---

## 7. Interfaz de autenticación

### 7.1 `LoginScreen.kt`

La pantalla:

1. Observa el estado:

```kotlin
val estado by viewModel.login.collectAsState()
```

2. Muestra una animación de entrada con `AnimatedVisibility`.
3. Contiene dos campos:
   - Correo (`CampoTextoValidado`).
   - Contraseña (`CampoPassword`).
4. Muestra errores por campo con íconos.
5. Muestra un error general si el login falla.
6. El botón cambia a un indicador de carga mientras `cargando` es `true`.
7. El enlace “¿No tienes cuenta? Regístrate” navega al registro.

Interacción:

```text
Escribir correo
  → onLoginEmailChange()
  → actualiza _login.email
  → si ya había error, vuelve a validar
  → Compose redibuja el campo

Presionar "Iniciar sesión"
  → iniciarSesion()
  → valida correo y contraseña
  → si hay error, lo muestra
  → si no, llama al repositorio
  → si es correcto, guarda sesión
  → AppRoot cambia a MainNavigation
```

### 7.2 `RegisterScreen.kt`

Campos:

| Campo | Componente |
|---|---|
| Nombre completo | `CampoTextoValidado` |
| Correo | `CampoTextoValidado` |
| Teléfono | `CampoTextoValidado` con teclado telefónico |
| Fecha de nacimiento | `CampoFecha` + `DatePickerDialog` |
| Contraseña | `CampoPassword` |
| Fortaleza | `IndicadorFortaleza` |
| Repetir contraseña | `CampoPassword` |
| Notificaciones | `Switch` |
| Términos | `Checkbox` |
| Crear cuenta | `BotonPrincipal` |

El selector de fecha:

```kotlin
DatePickerDialog(
    onDismissRequest = { mostrarDatePicker = false },
    confirmButton = {
        TextButton(onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
                viewModel.onRegistroFechaChange(formatearFecha(millis))
            }
            mostrarDatePicker = false
        }) {
            Text("Aceptar", color = VerdePrincipal)
        }
    }
) {
    DatePicker(state = datePickerState)
}
```

La fecha se formatea como `dd/MM/yyyy` usando UTC para evitar que el día cambie por zona horaria.

El `Switch` y el `Checkbox` actualizan directamente el estado del ViewModel:

```kotlin
Switch(
    checked = estado.notificaciones,
    onCheckedChange = viewModel::onNotificacionesChange
)

Checkbox(
    checked = estado.aceptaTerminos,
    onCheckedChange = viewModel::onTerminosChange
)
```

El indicador de fortaleza usa `Validaciones.fortalezaPassword()` y anima los colores de tres segmentos:

```text
1 segmento  → débil   (rojo)
2 segmentos → media   (amarillo)
3 segmentos → segura  (verde)
```

### 7.3 Componentes: `ComponentesAuth.kt`

| Componente | Para qué sirve |
|---|---|
| `CampoTextoValidado` | Campo de texto con ícono, error y texto de ayuda. |
| `CampoPassword` | Campo de contraseña con ojo para mostrar/ocultar. |
| `CampoFecha` | Campo de solo lectura que abre el calendario. |
| `MensajeErrorGeneral` | Tarjeta animada para errores del formulario. |
| `BotonPrincipal` | Botón verde con estado de carga. |
| `EncabezadoAuth` | Logo, título y subtítulo de las pantallas de autenticación. |

Patrón importante: la UI no decide si un campo es válido. Solo recibe `error: String?` y lo muestra.

---

## 8. `AuthViewModel.kt` en detalle

### 8.1 Estados

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)
```

`RegistroUiState` es similar, pero incluye nombre, teléfono, fecha, notificaciones, términos y confirmación.

### 8.2 Flujos

```kotlin
private val _login = MutableStateFlow(LoginUiState())
val login: StateFlow<LoginUiState> = _login.asStateFlow()
```

- `MutableStateFlow` se modifica dentro del ViewModel.
- `StateFlow` es la versión de solo lectura que observa la UI.
- `collectAsState()` convierte el `StateFlow` en estado de Compose.

### 8.3 Validación después de un error

En los `on...Change`, fíjate en este patrón:

```kotlin
emailError = if (it.emailError != null) Validaciones.email(valor) else null
```

Significa:

- Si el campo **todavía no tiene error**, no se muestra error mientras el usuario escribe.
- Si el campo **ya tenía error**, se vuelve a validar en cada tecla para que el mensaje desaparezca cuando se corrige.

Eso evita molestar al usuario con errores antes de que termine de escribir.

### 8.4 Envío del login

```kotlin
fun iniciarSesion() {
    val estado = _login.value
    val emailError = Validaciones.email(estado.email)
    val passwordError = if (estado.password.isEmpty()) {
        "La contraseña es obligatoria."
    } else null

    if (emailError != null || passwordError != null) {
        _login.update { it.copy(emailError = emailError, passwordError = passwordError) }
        return
    }

    viewModelScope.launch {
        _login.update { it.copy(cargando = true) }
        repository.iniciarSesion(estado.email, estado.password)
            .onSuccess { _login.value = LoginUiState() }
            .onFailure { error ->
                _login.update {
                    it.copy(cargando = false, errorGeneral = error.message)
                }
            }
    }
}
```

Puntos clave:

- La validación ocurre antes de llamar al repositorio.
- `cargando = true` deshabilita el botón y muestra el spinner.
- `onSuccess` limpia el formulario. La sesión guardada hace que `AppRoot` cambie de pantalla.
- `onFailure` muestra el error general.

### 8.5 Envío del registro

`registrar()` valida **todos** los campos, construye una lista de errores y usa `any { it != null }` para saber si debe detenerse.

Si todo está correcto:

```kotlin
repository.registrar(
    nombre = estado.nombre,
    email = estado.email,
    telefono = estado.telefono,
    fechaNacimiento = estado.fechaNacimiento,
    notificaciones = estado.notificaciones,
    password = estado.password
)
```

Al registrarse, el repositorio guarda la sesión automáticamente, por lo que el usuario pasa directo a la app principal.

### 8.6 Cerrar sesión

```kotlin
fun cerrarSesion() {
    viewModelScope.launch {
        repository.cerrarSesion()
        _login.value = LoginUiState()
        _registro.value = RegistroUiState()
    }
}
```

`repository.cerrarSesion()` elimina `sesion_email` de DataStore. El flujo de sesión emite `null`, `AppRoot` cambia a `SinSesion` y aparece el login.

---

## 9. Navegación principal

### 9.1 `AuthNavigation`

Está dentro de `AppRoot.kt` y usa un `NavHost` con dos rutas:

```kotlin
NavHost(
    navController = navController,
    startDestination = "login",
    enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 8 } },
    exitTransition = { fadeOut(tween(200)) }
) {
    composable("login") { LoginScreen(...) }
    composable("registro") { RegisterScreen(...) }
}
```

Interacción:

- `onIrARegistro` limpia el formulario de registro y navega a `"registro"`.
- `onVolver` ejecuta `popBackStack()` y vuelve al login.

### 9.2 `MainNavigation`

En `AppNavegation.kt`:

```kotlin
@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    email: String
)
```

Crea:

- `navController`: controla las rutas.
- `drawerState`: estado del menú lateral.
- `perfilViewModel`: datos del perfil y avatar.
- `rutaActual`: ruta visible para resaltar el ítem del menú.

### 9.3 Destinos

```kotlin
sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : Destino("inicio", "Inicio", Icons.Default.Home)
    object QuienesSomos : Destino("quienes_somos", "Quiénes somos", Icons.Default.Info)
    object Programas : Destino("programas", "Programas", Icons.Default.Star)
    object Productos : Destino("productos", "Productos y servicios", Icons.Default.ShoppingCart)
    object Historias : Destino("historias", "Historias de impacto", Icons.Default.ThumbUp)
    object Colaborar : Destino("colaborar", "Formas de colaborar", Icons.Default.Favorite)
    object Contacto : Destino("contacto", "Contacto", Icons.Default.Email)
    object Perfil : Destino("perfil", "Mi perfil", Icons.Default.AccountCircle)
}
```

`sealed class` permite agrupar rutas, títulos e íconos en un solo lugar. El menú se construye recorriendo `todosLosDestinos`.

### 9.4 Navegación sin duplicados

```kotlin
fun irA(destino: Destino) {
    navController.navigate(destino.ruta) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
```

- `popUpTo(...)`: evita acumular pantallas.
- `saveState`: guarda el estado de la pantalla anterior.
- `launchSingleTop`: evita abrir la misma pantalla dos veces.
- `restoreState`: recupera el estado al volver.

### 9.5 Menú lateral

`ModalNavigationDrawer` contiene:

- Encabezado con avatar, “CORPALIV”, “Alba Lab” y el correo.
- Los ocho destinos.
- Separador.
- Botón “Cerrar sesión”.

El avatar del encabezado y el de la barra superior son clicables y llevan al perfil.

### 9.6 `BarraSuperior.kt`

`TopAppBar` con:

- Botón de menú.
- Título “CORPALIV / Alba Lab”.
- Avatar circular que abre el perfil.

El avatar usa `FotoPerfil` y la ruta guardada en DataStore.

---

## 10. Perfil y cambio de imágenes

### 10.1 `PerfilViewModel.kt`

Estados:

```kotlin
val usuario: StateFlow<Usuario?>
val portadaPath: StateFlow<String?>
val avatarPath: StateFlow<String?>
val mensaje: StateFlow<String?>
```

- `usuario` se obtiene a partir del correo de la sesión.
- `portadaPath` y `avatarPath` vienen de DataStore.
- `mensaje` se usa para mostrar el `Snackbar`.

Funciones públicas:

```kotlin
fun actualizarPortadaDesdeGaleria(uri: Uri?)
fun actualizarAvatarDesdeGaleria(uri: Uri?)
fun actualizarPortadaDesdeCamara(uri: Uri?, capturaCorrecta: Boolean)
fun actualizarAvatarDesdeCamara(uri: Uri?, capturaCorrecta: Boolean)
fun consumirMensaje()
```

Función central:

```kotlin
private fun actualizarImagen(uri: Uri?, esPortada: Boolean) {
    if (uri == null) return

    viewModelScope.launch {
        val anterior = if (esPortada) portadaPath.value else avatarPath.value
        val ruta = withContext(Dispatchers.IO) {
            ImageStore.copiarImagen(
                context = getApplication<Application>(),
                uri = uri,
                prefijo = if (esPortada) "portada" else "avatar"
            )
        }

        if (ruta == null) {
            _mensaje.value = "No se pudo cargar la imagen seleccionada."
            return@launch
        }

        if (esPortada) repository.guardarPortada(ruta)
        else repository.guardarAvatar(ruta)

        ImageStore.eliminar(anterior)
        _mensaje.value = if (esPortada) {
            "Imagen de portada actualizada."
        } else {
            "Foto de perfil actualizada."
        }
    }
}
```

Puntos clave:

- La copia se hace en `Dispatchers.IO` para no bloquear la UI.
- Si la copia falla, se muestra un mensaje y no se guarda la ruta.
- Se guarda la nueva ruta en DataStore.
- Se elimina la imagen anterior para no acumular archivos.
- El `StateFlow` actualiza la imagen en pantalla automáticamente.

### 10.2 `PerfilScreen.kt`

La pantalla tiene:

- Portada de 220 dp.
- Avatar circular de 112 dp superpuesto.
- Nombre y correo.
- Tarjeta con teléfono, fecha de nacimiento y notificaciones.
- Botón “Cerrar sesión”.
- `SnackbarHost` para mensajes.

Cada imagen tiene un menú con dos opciones:

```text
Tomar foto
Elegir de archivos
```

### 10.3 Flujo de la cámara

```text
Usuario toca "Cambiar portada" o el lápiz del avatar
        ↓
Se elige "Tomar foto"
        ↓
tomarFoto(TipoImagen.PORTADA o AVATAR)
        ↓
¿Permiso CAMERA concedido?
   ├── No → permisoCamaraLauncher.launch(...)
   │          ├── Usuario acepta → abrirCamara()
   │          └── Usuario rechaza → Toast explicativo
   └── Sí → abrirCamara()
        ↓
CamaraUriFactory.crearUri(context)
        ↓
FileProvider entrega una URI segura
        ↓
ActivityResultContracts.TakePicture()
        ↓
Cámara guarda la foto en cacheDir/camera
        ↓
onResult(exito)
        ↓
PerfilViewModel.actualizarPortadaDesdeCamara(...)
        ↓
ImageStore copia la imagen a filesDir/imagenes
        ↓
DataStore guarda la ruta
        ↓
AsyncImage muestra la nueva portada
        ↓
Snackbar confirma el cambio
```

### 10.4 Flujo de archivos

```text
Usuario elige "Elegir de archivos"
        ↓
ActivityResultContracts.GetContent()
        ↓
Selector de archivos del sistema (SAF)
        ↓
Usuario elige una imagen
        ↓
onResult(uri)
        ↓
PerfilViewModel.actualizar...DesdeGaleria(uri)
        ↓
ImageStore copia a filesDir/imagenes
        ↓
DataStore guarda la ruta
        ↓
La imagen se actualiza y aparece el Snackbar
```

No se pide permiso de almacenamiento porque `GetContent` usa el selector del sistema.

### 10.5 `CamaraUriFactory.kt` y `FileProvider`

```kotlin
val cameraDir = File(context.cacheDir, "camera")
val photoFile = File.createTempFile("perfil_", ".jpg", cameraDir)
return FileProvider.getUriForFile(
    context,
    "${context.packageName}.fileprovider",
    photoFile
)
```

En `AndroidManifest.xml`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

En `res/xml/file_paths.xml`:

```xml
<paths>
    <cache-path name="camera" path="camera/" />
</paths>
```

`FileProvider` es necesario porque Android no permite entregar una `file://` directa a otra app por seguridad. La cámara recibe una URI temporal con permiso.

### 10.6 `FotoPerfil.kt`

```kotlin
if (imagenUri != null) {
    AsyncImage(
        model = imagenUri,
        contentDescription = descripcion,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
} else {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = descripcion,
        tint = VerdePrincipal
    )
}
```

- Usa Coil (`AsyncImage`) para cargar la imagen.
- Si no hay imagen, muestra el ícono de persona.
- `ContentScale.Crop` recorta la imagen para que llene el círculo.

---

## 11. Persistencia y seguridad

### 11.1 Dónde se guarda cada cosa

| Dato | Lugar |
|---|---|
| Cuentas | DataStore, clave `usuarios_json` |
| Sesión activa | DataStore, clave `sesion_email` |
| Ruta de portada | DataStore, clave `portada_path` |
| Ruta de avatar | DataStore, clave `avatar_path` |
| Imágenes | `filesDir/imagenes` |
| Contraseña | Solo hash PBKDF2 + salt |

### 11.2 Qué pasa al cerrar y abrir la app

```text
Abrir app
  → AuthViewModel lee sesion_email
  → Si existe, SesionEstado.Activa
  → AppRoot muestra MainNavigation
  → PerfilViewModel lee portada_path y avatar_path
  → Las imágenes se cargan desde filesDir
```

### 11.3 Qué pasa al cerrar sesión

```text
Cerrar sesión
  → repository.cerrarSesion()
  → DataStore elimina sesion_email
  → AuthViewModel emite SinSesion
  → AppRoot muestra AuthNavigation
  → LoginScreen aparece de nuevo
```

Las cuentas y las imágenes **no se borran** al cerrar sesión; solo se elimina la sesión activa.

---

## 12. Interfaz, colores y animaciones

### 12.1 Paleta

En `Colores.kt`:

```kotlin
val Marfil = Color(0xFFFFF8F1)
val VerdePrincipal = Color(0xFF57D260)
val VerdeSuave = Color(0xFFDDE9D8)
val TextoPrincipal = Color(0xFF294A34)
val TextoSecundario = Color(0xFF657064)
```

Se usan directamente en las pantallas para mantener una identidad visual consistente.

### 12.2 Animaciones principales

| Animación | Dónde | Qué produce |
|---|---|---|
| `AnimatedContent` | `AppRoot` | Fundido entre carga, login y app. |
| `AnimatedVisibility` | Login/Registro | Aparición de encabezado y tarjeta. |
| Transiciones del `NavHost` | `AuthNavigation` | Deslizamiento/fundido entre login y registro. |
| `animateColorAsState` | Indicador de fortaleza | Cambio suave de rojo a amarillo/verde. |
| `AnimatedContent` | `BotonPrincipal` | Cambio de texto a spinner. |
| `AnimatedContent` | Portada del perfil | Fundido al cambiar la imagen. |
| `AnimatedVisibility` | Errores | Expansión/contracción de mensajes. |

### 12.3 Accesibilidad

- Los íconos usan `contentDescription` cuando comunican una acción.
- El avatar de la barra superior usa `Role.Button`.
- Los campos muestran errores con texto, no solo color.
- Los botones tienen altura mínima de 56 dp.
- El contenido es desplazable para pantallas pequeñas y teclado abierto (`imePadding`).

---

## 13. Interacciones de la app, paso a paso

| Acción del usuario | Código que interviene | Resultado visible |
|---|---|---|
| Abrir la app | `MainActivity` → `AppRoot` → `AuthViewModel` | Pantalla de carga y luego login o app. |
| Escribir un correo inválido y enviar | `LoginScreen` → `AuthViewModel.iniciarSesion` → `Validaciones.email` | Campo rojo, ícono y mensaje de error. |
| Iniciar sesión correctamente | `AuthRepository.iniciarSesion` → `PasswordHasher.verificar` → DataStore | Cambio animado a la app principal. |
| Registrar una cuenta | `RegisterScreen` → `AuthViewModel.registrar` → `AuthRepository.registrar` | Cuenta guardada, sesión iniciada y app principal. |
| Elegir fecha de nacimiento | `DatePickerDialog` → `formatearFecha` → `onRegistroFechaChange` | Fecha `dd/MM/yyyy` en el campo. |
| Activar notificaciones | `Switch` → `onNotificacionesChange` | Cambia el booleano del formulario. |
| Aceptar términos | `Checkbox` → `onTerminosChange` | Se habilita la validación de términos. |
| Ver fortaleza de contraseña | `Validaciones.fortalezaPassword` | Tres barras y texto débil/media/segura. |
| Abrir menú lateral | `BarraSuperior` → `drawerState.open()` | Menú con destinos y correo. |
| Ir a otra sección | `irA(Destino...)` → `NavHost` | Cambia la pantalla sin duplicar rutas. |
| Abrir perfil | Avatar o menú → `Destino.Perfil` | `PerfilScreen` con datos e imágenes. |
| Cambiar portada con cámara | `tomarFoto` → permiso → `TakePicture` → `PerfilViewModel` | Nueva portada y Snackbar. |
| Cambiar portada desde archivos | `GetContent` → `PerfilViewModel` | Nueva portada y Snackbar. |
| Cambiar avatar | Mismo flujo con `TipoImagen.AVATAR` | Nueva foto circular en perfil y barra. |
| Cerrar sesión | `authViewModel.cerrarSesion()` → DataStore | Vuelve al login. |
| Volver a abrir la app | DataStore `sesion_email` | Entra directo si la sesión sigue activa. |

---

## 14. Compilar, ejecutar y probar

### 14.1 Requisitos

- Android Studio.
- JDK 21.
- Android SDK 37.
- Emulador o dispositivo con Android 7.0 (API 24) o superior.

### 14.2 Compilar

```powershell
.\gradlew.bat :app:assembleDebug
```

APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 14.3 Ejecutar pruebas

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### 14.4 Limpiar datos para probar desde cero

Si quieres que la app vuelva a pedir login y borrar cuentas/imágenes locales:

```powershell
adb shell pm clear com.example.corpalivapp
```

También puedes hacerlo desde el emulador:

```text
Ajustes → Apps → CorpalivApp → Almacenamiento → Borrar datos
```

### 14.5 Probar la cámara en el emulador

- Abre la cámara del emulador y configura una imagen de prueba.
- En el emulador, la cámara puede mostrar una escena virtual.
- Si no tienes cámara, usa “Elegir de archivos” para probar el flujo de imágenes.

---

## 15. Orden recomendado para estudiar el código

### Día 1 — Entrada y sesión

1. `MainActivity.kt`
2. `ui/navigation/AppRoot.kt`
3. `AuthViewModel.kt` (solo la parte de `SesionEstado` y `init`)

Preguntas:

- ¿Qué pasa si DataStore tiene `sesion_email`?
- ¿Qué pasa si no lo tiene?
- ¿Por qué `AppRoot` usa `AnimatedContent`?

### Día 2 — Datos y seguridad

1. `data/model/Usuario.kt`
2. `data/local/UserPreferences.kt`
3. `data/security/PasswordHasher.kt`
4. `data/repository/AuthRepository.kt`

Preguntas:

- ¿Por qué se guarda un salt por usuario?
- ¿Qué diferencia hay entre hash y cifrado?
- ¿Qué pasa si el correo ya existe?

### Día 3 — Validaciones y pruebas

1. `domain/Validaciones.kt`
2. `ValidacionesTest.kt`

Preguntas:

- ¿Qué significa que una función devuelva `null`?
- ¿Qué casos cubren las pruebas?
- ¿Qué regla agregarías para un apellido?

### Día 4 — Login y registro

1. `LoginScreen.kt`
2. `RegisterScreen.kt`
3. `ComponentesAuth.kt`
4. `AuthViewModel.kt` completo

Preguntas:

- ¿Cómo se muestra un error por campo?
- ¿Cómo se deshabilita el botón mientras carga?
- ¿Dónde se valida la fecha?

### Día 5 — Navegación

1. `AppNavegation.kt`
2. `BarraSuperior.kt`
3. `HomeScreen.kt`
4. `Pantallas.kt`

Preguntas:

- ¿Cómo se evita abrir dos veces la misma pantalla?
- ¿Cómo se resalta el destino actual?
- ¿Qué ocurre al cerrar sesión desde el menú?

### Día 6 — Perfil e imágenes

1. `PerfilViewModel.kt`
2. `PerfilScreen.kt`
3. `CamaraUriFactory.kt`
4. `ImageStore.kt`
5. `AndroidManifest.xml` y `file_paths.xml`

Preguntas:

- ¿Por qué se copia la imagen a `filesDir`?
- ¿Por qué se usa `FileProvider`?
- ¿Qué pasa si el usuario niega el permiso de cámara?

### Día 7 — Repaso y práctica

1. Recorre la app y anota qué archivo interviene en cada acción.
2. Ejecuta las pruebas.
3. Intenta un cambio pequeño y mídelo.

---

## 16. Ejercicios para aprender haciendo

### Ejercicio 1 — Agregar apellido

Tendrías que modificar:

1. `Usuario.kt`: agregar `apellido`.
2. `UserPreferences.kt`: guardar/leer `apellido` en JSON.
3. `RegisterScreen.kt`: agregar un campo.
4. `AuthViewModel.kt`: agregar estado y validación.
5. `Validaciones.kt`: agregar regla.
6. `PerfilScreen.kt`: mostrar el apellido.

**Aprendizaje:** cambios que atraviesan todas las capas.

### Ejercicio 2 — Cambiar la edad mínima

Busca en `Validaciones.kt`:

```kotlin
edad < 14 -> "Debes tener al menos 14 años para registrarte."
```

Cámbiala y actualiza las pruebas.

**Aprendizaje:** regla de negocio + prueba unitaria.

### Ejercicio 3 — Agregar “Eliminar cuenta”

Necesitarías:

1. Un método en `UserPreferences` para eliminar una cuenta.
2. Un método en `AuthRepository`.
3. Un botón en `PerfilScreen`.
4. Confirmación con diálogo.
5. Cerrar sesión después de eliminar.

**Aprendizaje:** operaciones destructivas, confirmación y consistencia de datos.

### Ejercicio 4 — Mejorar las pantallas informativas

Actualmente `Pantallas.kt` muestra título y descripción. Puedes agregar:

- Imágenes.
- Listas.
- Botones.
- Datos desde un archivo de contenido.

**Aprendizaje:** composición de UI y reutilización.

### Ejercicio 5 — Pruebas de casos límite

Agrega pruebas para:

- Fecha de hoy.
- Fecha con día 31 en un mes de 30 días.
- Nombre con apóstrofe.
- Contraseña con símbolo.
- Teléfono con letras.

**Aprendizaje:** pensar en casos borde.

---

## 17. Problemas comunes y cómo interpretarlos

### La app no muestra los cambios

- Verifica que Android Studio tenga abierta la carpeta correcta.
- Presiona **Run ▶**, no solo vuelvas a abrir la app en el emulador.
- Haz `Build → Clean Project` y `Build → Rebuild Project`.

### La cámara no abre

- Revisa que el permiso `CAMERA` esté concedido.
- Verifica que `FileProvider` y `file_paths.xml` coincidan.
- Prueba en un dispositivo o emulador con cámara configurada.

### La imagen no se ve después de reiniciar

- Verifica que la ruta guardada exista en `filesDir/imagenes`.
- Revisa que `ImageStore.copiarImagen` no haya devuelto `null`.
- Comprueba los logs de errores.

### Siempre entra directo sin pedir login

- Es normal si hay una sesión guardada.
- Cierra sesión o borra los datos de la app para probar el login.

### Los tests de fecha fallan con el tiempo

La prueba usa una fecha fija (`01/01/2020`). Con los años, esa fecha puede dejar de ser inválida por edad. Para que la prueba sea estable, conviene calcular una fecha relativa, por ejemplo:

```kotlin
val fechaMenor = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    .format(Date(System.currentTimeMillis() - 10L * 365 * 24 * 60 * 60 * 1000))
```

Y usar esa fecha en la prueba.

---

## 18. Glosario

| Término | Significado |
|---|---|
| **Compose** | Framework declarativo de UI de Android. |
| **Composable** | Función que dibuja una parte de la interfaz. |
| **Estado** | Datos que determinan lo que se muestra. |
| **Recomposición** | Volver a ejecutar un Composable cuando cambia su estado. |
| **StateFlow** | Flujo observable de estado. |
| **ViewModel** | Clase que guarda estado y sobrevive a cambios de configuración. |
| **MVVM** | Modelo-Vista-ViewModel. |
| **DataStore** | Almacenamiento local de pares clave-valor. |
| **Repositorio** | Capa que concentra el acceso a datos. |
| **Hash** | Resultado irreversible de una función sobre datos. |
| **Salt** | Valor aleatorio que se agrega antes del hash. |
| **PBKDF2** | Algoritmo de derivación de claves con muchas iteraciones. |
| **FileProvider** | Componente que comparte archivos de forma segura con otras apps. |
| **SAF** | Storage Access Framework; selector de archivos del sistema. |
| **Coil** | Librería para cargar imágenes en Compose. |
| **NavHost** | Contenedor de rutas de navegación. |
| **Sealed class** | Clase con un conjunto cerrado de subclases. |

---

## 19. Resumen final

Si tuvieras que explicar la app en una frase:

> `MainActivity` inicia `AppRoot`, que revisa la sesión en DataStore; si no hay sesión muestra login/registro, y si hay sesión muestra la navegación principal. El registro valida los datos, genera un hash PBKDF2 con salt y guarda la cuenta; el login verifica ese hash. El perfil lee el usuario y las rutas de imágenes desde DataStore, y permite cambiarlas con cámara o selector de archivos, copiándolas al almacenamiento interno.

Los tres archivos que más conviene dominar primero:

1. `ui/navigation/AppRoot.kt` — decide qué se muestra.
2. `ui/auth/AuthViewModel.kt` — controla login y registro.
3. `ui/perfil/PerfilViewModel.kt` — controla datos e imágenes del perfil.

Después de entender esos tres, el resto son pantallas y componentes que reciben estado y emiten eventos.

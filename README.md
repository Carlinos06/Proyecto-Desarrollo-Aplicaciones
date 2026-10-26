# CorpalivApp

Aplicación Android desarrollada para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**.

La app difunde el propósito, los programas y las formas de colaborar de **CORPALIV (Alba Lab)**, e incorpora un flujo de autenticación local y un perfil personalizable.

> Guía de estudio del código: [GUIA_CODIGO.md](GUIA_CODIGO.md)

---

## Contexto del caso

CORPALIV es una organización sin fines de lucro dedicada a la autonomía, participación e inclusión social y laboral de personas con discapacidad múltiple. A través de la Escuela Especial Jan Van Dijk y la Red Sociolaboral **Alba Lab** desarrolla programas de formación, preparación para el empleo, producción y vinculación con empresas, familias y donantes.

La aplicación busca centralizar la propuesta de valor y facilitar el paso desde el interés inicial hasta una acción concreta de colaboración.

> Proyecto académico MVP. No utiliza datos personales reales de beneficiarios: toda la información es ficticia o anonimizada.

---

## Integrantes

- José Barrientos
- Carlos Salazar

**Docente / sección:** Carlos Abarzua — Sección 009D

---

## Funcionalidades implementadas

### Autenticación
- **Registro** con nombre, correo, teléfono, fecha de nacimiento, contraseña y confirmación.
- **Inicio de sesión** con correo y contraseña.
- **Contraseña cifrada** con `PBKDF2WithHmacSHA256` + salt aleatorio. Nunca se guarda en texto plano.
- **Persistencia local** de cuentas y sesión con `DataStore Preferences`.
- **Cierre de sesión** desde el menú lateral y desde el perfil.
- Mensajes de error claros, íconos de estado y validaciones visuales por campo.

### Perfil
- Visualización de los datos del usuario registrado.
- **Imagen de portada** editable:
  - Tomar una foto con la **cámara** del teléfono.
  - Elegir una imagen desde los **archivos** del teléfono.
- **Foto de perfil (avatar)** editable con los mismos dos métodos.
- Las imágenes se copian al almacenamiento interno de la app para que sigan disponibles.
- Permiso de cámara solicitado en tiempo de ejecución.
- Confirmación mediante `Snackbar` al actualizar una imagen.

### Navegación
- Flujo inicial Login → Registro → Aplicación principal.
- Menú lateral con las secciones: Inicio, Quiénes somos, Programas, Productos y servicios, Historias de impacto, Formas de colaborar, Contacto y Mi perfil.
- Animaciones de transición entre pantallas y retroalimentación visual.

---

## Arquitectura

Se utiliza el patrón **MVVM** con separación de responsabilidades:

```
app/src/main/java/com/example/corpalivapp/
├── data/
│   ├── local/
│   │   ├── UserPreferences.kt      # DataStore: cuentas, sesión e imágenes
│   │   └── ImageStore.kt           # Copia de imágenes a almacenamiento interno
│   ├── model/
│   │   └── Usuario.kt              # Usuario y Cuenta
│   ├── repository/
│   │   └── AuthRepository.kt       # Registro, login y sesión
│   └── security/
│       └── PasswordHasher.kt       # PBKDF2 + salt
├── domain/
│   └── Validaciones.kt             # Reglas de validación puras
├── ui/
│   ├── auth/
│   │   ├── AuthViewModel.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── components/
│   ├── navigation/
│   │   └── AppRoot.kt              # Decide login o app principal
│   └── perfil/
│       ├── PerfilViewModel.kt
│       └── PerfilScreen.kt
├── AppNavegation.kt                # Navegación principal y menú lateral
├── BarraSuperior.kt
├── CamaraUriFactory.kt             # FileProvider para cámara
├── FotoPerfil.kt
├── HomeScreen.kt
├── MainActivity.kt
└── ui/theme/
```

### Persistencia

| Dato | Almacenamiento |
|---|---|
| Cuentas registradas | `DataStore` (`usuarios_json`) |
| Sesión activa | `DataStore` (`sesion_email`) |
| Ruta de portada | `DataStore` (`portada_path`) |
| Ruta de avatar | `DataStore` (`avatar_path`) |
| Imágenes | Almacenamiento interno (`filesDir/imagenes`) |
| Contraseña | Hash PBKDF2 + salt en `DataStore` |

---

## Recursos nativos utilizados

1. **Cámara** (`ActivityResultContracts.TakePicture` + `FileProvider`).
2. **Archivos / almacenamiento** (`ActivityResultContracts.GetContent` con selector de imágenes del sistema).

Ambos recursos se integran en el perfil y gestionan permisos de forma segura. La selección de archivos usa el selector del sistema (SAF), por lo que no requiere permisos amplios de almacenamiento.

---

## Validaciones implementadas

- Nombre: obligatorio, mínimo 3 caracteres, solo letras/espacios/guiones/apóstrofes.
- Correo: formato válido.
- Teléfono: entre 8 y 9 dígitos.
- Fecha de nacimiento: formato `dd/MM/yyyy`, fecha real, no futura y edad mínima de 14 años.
- Contraseña: mínimo 8 caracteres, mayúscula, minúscula y número.
- Confirmación de contraseña.
- Aceptación obligatoria de términos.
- Indicador visual de fortaleza de contraseña.
- Validación en la lógica (`domain/Validaciones.kt`), separada de los componentes visuales.

---

## Animaciones

- Aparición escalonada de los formularios de login y registro.
- Transiciones animadas entre pantallas de autenticación.
- Cambio de estado de sesión con `AnimatedContent`.
- Indicador animado de fortaleza de contraseña.
- Aparición/desaparición animada de mensajes de error.
- Transición suave al actualizar la imagen de portada.
- Botones con estado de carga animado.

---

## Requisitos y ejecución

### Requisitos
- **Android Studio** (versión compatible con AGP 9.4.1).
- **JDK 21**.
- **Android SDK 37** instalado.
- Dispositivo o emulador con **Android 7.0 (API 24)** o superior.

### Pasos
1. Abrir la carpeta del proyecto en Android Studio.
2. Verificar que `local.properties` apunte al SDK local. Si no existe, Android Studio lo genera automáticamente:
   ```properties
   sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
   ```
3. Sincronizar Gradle.
4. Ejecutar la app con **Run ▶** sobre un emulador o dispositivo.
5. En la primera ejecución, crear una cuenta desde **Regístrate**.
6. Iniciar sesión con el correo y la contraseña registrados.

### Compilar desde terminal

```bash
# Windows
gradlew.bat :app:assembleDebug

# macOS / Linux
./gradlew :app:assembleDebug
```

El APK de depuración se genera en:

```
app/build/outputs/apk/debug/app-debug.apk
```

### Pruebas unitarias

```bash
gradlew.bat :app:testDebugUnitTest
```

Las pruebas cubren las reglas de validación de nombre, correo, teléfono, fecha, contraseña y fortaleza.

---

## Seguridad y privacidad

- No se almacenan contraseñas en texto plano.
- Se usa un salt único por cuenta y `PBKDF2` para derivar el hash.
- Las imágenes se copian al almacenamiento privado de la aplicación.
- No se utilizan datos reales de beneficiarios.
- El proyecto no incluye backend ni transmisión de datos a internet: todo funciona localmente para este MVP.

---

## Estado del proyecto

Implementado:
- Login y registro funcional.
- Contraseñas cifradas y persistencia local.
- Perfil con portada y avatar desde cámara o archivos.
- Navegación principal y cierre de sesión.
- Validaciones y animaciones.

Pendiente / mejoras futuras:
- Completar el contenido real de las secciones informativas.
- Formulario de contacto/colaboración.
- Notificaciones locales al registrar una cuenta o recibir novedades.
- Integración con un backend/API REST.
- Recuperación de contraseña.
- Pruebas instrumentadas en dispositivo.

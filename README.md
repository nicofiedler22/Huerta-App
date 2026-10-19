# 🌱 HuertApp – Huerto Escolar

Aplicación Android para gestionar un huerto escolar: el docente administra sectores, cultivos y tareas, y los estudiantes ejecutan sus tareas entregando comentarios y fotografías como evidencia.

## Integrantes
- Nicolás Fiedler Silva

## Funcionalidades
**Administrador (docente/tallerista)**
- Dashboard con avisos (tareas por validar, atrasadas y reportes sin resolver).
- Crear, editar, listar y eliminar sectores; detalle de sector con sus cultivos.
- Crear, editar y eliminar cultivos (siembra, cosecha estimada, estado y frecuencia de riego).
- Crear, editar, asignar y eliminar tareas (riego, siembra, mantenimiento, observación).
- Listado de tareas con filtros por estado, fecha y responsable.
- Revisión de tareas entregadas: aprobar o rechazar (el rechazo exige motivo).
- Revisión de reportes de problemas y cambio de su estado de atención.
- Historial de actividades y validaciones.

**Colaborador (estudiante)**
- Dashboard con avisos de tareas por hacer, atrasadas y en validación.
- Consulta solo sus tareas, las inicia y las entrega con comentarios y fotos.
- Corrige y reenvía tareas rechazadas.
- Reporta problemas de sectores o cultivos (con foto opcional) y consulta sus reportes.

Flujo de estados de una tarea: **Pendiente → En desarrollo → Pendiente de validación → Validada / Rechazada**.

## Cuentas de prueba
| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | docente@huerto.cl | admin123 |
| Estudiante 1 | estudiante1@huerto.cl | 1234 |
| Estudiante 2 | estudiante2@huerto.cl | 1234 |
| Estudiante 3 | estudiante3@huerto.cl | 1234 |

Los datos ficticios (sectores, cultivos, tareas en todos los estados, validaciones y reportes) se cargan solos la primera vez que se abre la app.

## Tecnologías
- Kotlin, Jetpack Compose y Material Design 3
- Room (SQLite) con KSP
- Arquitectura MVVM: UI (Composables) → ViewModel (StateFlow) → Repository → DAO/Room
- Photo Picker y cámara (`TakePicture` + `FileProvider`)

## Estructura
```
data/        entity, dao, database, repository
ui/screens   pantallas (14 vistas)
ui/components componentes reutilizables (campos, chips, fotos, selectores)
ui/theme     colores, colores semánticos y tipografías
ui/viewmodel ViewModels y sesión actual
util/        validadores, fechas e imágenes
```

## Base de datos (3FN)
Entidades: `Usuario`, `Sector`, `Cultivo` (FK sector), `Tarea` (FK sector y responsable), `Evidencia` (FK tarea), `Validacion` (FK tarea y validador) y `ReporteProblema` (FK sector, cultivo opcional y reportante). Cada dato depende solo de la clave de su tabla.

## Validaciones y permisos
- Toda la validación está en `util/Validadores.kt` (fuera de los Composables) y muestra mensajes bajo cada campo.
- La cosecha estimada no puede ser anterior a la siembra; la frecuencia de riego debe ser un entero positivo.
- Los permisos se comprueban en los ViewModels (`SesionActual`), no solo ocultando botones.
- Un estudiante no puede validar tareas ni ver tareas de otros.

## Cómo ejecutar
1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador o dispositivo (minSdk 24).
4. Si ya tenías la app instalada con datos antiguos, desinstálala o borra sus datos para cargar los datos ficticios nuevos.

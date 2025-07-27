# 🎵 BarberMusic&Spa 💆‍♂️

<div align="center">
  <img src="https://i.imgur.com/your-logo-url-here.png" alt="Logo de BarberMusic&Spa" width="200"/>
  
  <p><strong>Experiencia de Lujo en Spa y Barbería con un Toque Musical</strong></p>
  
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.11-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
  [![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green.svg)](https://www.thymeleaf.org/)
  [![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)](https://getbootstrap.com/)
</div>

## 📱 Demo en Vivo

Servidor local: [http://localhost:8080](http://localhost:8080)

## 🌟 Descripción General

BarberMusic&Spa es una aplicación web integral diseñada para una cadena premium de spas y barberías con múltiples sucursales en México. La plataforma combina capacidades de reserva, funcionalidad de comercio electrónico y herramientas administrativas para crear una experiencia perfecta tanto para clientes como para el personal.

Nuestra propuesta única de valor es la integración de música y relajación en cada servicio, proporcionando una experiencia única para nuestros clientes.

## 🏗️ Arquitectura y Organización Actual

### 📁 Estructura de Templates por Roles

La aplicación implementa una arquitectura basada en roles con templates organizados según el **Manual de Roles BarberMusic&Spa**:

```
src/main/resources/templates/
├── publico/                    # Usuarios no autenticados
│   ├── home.html              # Página principal pública
│   ├── login.html             # Formulario de inicio de sesión
│   ├── registro.html          # Formulario de registro
│   ├── serviciosVista.html    # Catálogo público de servicios
│   ├── productosVista.html    # Catálogo público de productos
│   ├── cambiar-password.html  # Recuperación de contraseña
│   ├── mantenimiento.html     # Página de mantenimiento
│   └── template_user.html     # Template base público
├── cliente/                    # Clientes autenticados (ROLE_CLIENTE)
│   ├── perfil.html            # Perfil del cliente
│   ├── compras.html           # Historial de compras
│   ├── detallecompra.html     # Detalle de orden específica
│   ├── carrito.html           # Carrito de compras
│   ├── favoritos.html         # Productos/servicios favoritos
│   └── template_cliente.html  # Template base para clientes
├── empleado/                   # Personal operativo (ROLE_EMPLEADO)
│   └── panel.html             # Panel de control del empleado
├── admin-sucursal/            # Administradores de sucursal (ROLE_ADMIN_SUCURSAL)
│   └── panel.html             # Panel de administración de sucursal
├── gerente/                   # Gerente general (ROLE_GERENTE)
│   └── administrador/         # Panel de súper administrador
│       ├── home.html          # Dashboard principal
│       ├── index.html         # Índice administrativo
│       ├── ordenes.html       # Gestión de órdenes
│       ├── detalleorden.html  # Detalle de orden específica
│       └── profile.html       # Perfil del administrador
├── productos/                 # CRUD de productos (uso administrativo)
├── servicios/                 # CRUD de servicios (uso administrativo)
├── sucursales/               # CRUD de sucursales (uso administrativo)
├── agendamientos/            # CRUD de citas (uso administrativo)
├── recordatorios/            # CRUD de recordatorios (uso administrativo)
├── pagos/                    # Templates de estados de pago
├── emails/                   # Templates para correos electrónicos
└── error/                    # Páginas de error (403, 404, 500)
```

### 🔐 Sistema de Roles y Permisos

La aplicación implementa un sistema de 4 roles jerárquicos según el Manual de Roles:

#### 1. **CLIENTE** (Rol por defecto)

- **Rutas**: `/home`, `/cliente/**`, `/cart/**`
- **Funcionalidades**:
  - Explorar catálogo de servicios y productos
  - Agendar citas en sucursales
  - Realizar compras de productos
  - Gestionar perfil personal y preferencias
  - Dejar reseñas y calificaciones
  - Gestionar favoritos

#### 2. **EMPLEADO** (Personal operativo)

- **Rutas**: `/empleado/**`
- **Funcionalidades**:
  - Panel de control específico
  - Gestión de agendamientos asignados
  - Consulta de órdenes de productos
  - Gestión de recordatorios

#### 3. **ADMIN_SUCURSAL** (Administrador de sucursal)

- **Rutas**: `/admin-sucursal/**`
- **Funcionalidades**:
  - Gestión de catálogo de su sucursal
  - Configuración de horarios y excepciones
  - Moderación de reseñas
  - Gestión de personal de su sucursal
  - Promoción de clientes a empleados

#### 4. **GERENTE** (Súper administrador)

- **Rutas**: `/administrador/**`, `/productos/**`, `/servicios/**`, `/sucursales/**`
- **Funcionalidades**:
  - Acceso total al sistema
  - Gestión global de sucursales
  - Administración de promociones
  - Gestión completa de personal
  - Auditoría y trazabilidad

### 🛡️ Configuración de Spring Security

```java
// Configuración principal en SecurityConfig.java
.requestMatchers("/", "/home", "/publico/**").permitAll()
.requestMatchers("/cliente/**", "/cart/**").hasAuthority("ROLE_CLIENTE")
.requestMatchers("/empleado/**").hasAuthority("ROLE_EMPLEADO")
.requestMatchers("/admin-sucursal/**").hasAuthority("ROLE_ADMIN_SUCURSAL")
.requestMatchers("/administrador/**", "/productos/**", "/servicios/**").hasAuthority("ROLE_GERENTE")
```

## ✨ Características Principales

### 🧔 Portal del Cliente

- **Reserva de Servicios**: Programa citas para tratamientos de spa y servicios de barbería
- **Tienda de Productos**: Navega y compra productos premium para el cuidado personal
- **Sistema de Favoritos**: Marca productos y servicios como favoritos
- **Localizador de Sucursales**: Encuentra la ubicación más cercana con mapas interactivos
- **Perfiles de Usuario**: Seguimiento de citas, pedidos y recomendaciones personalizadas
- **Integración con PayPal y MercadoPago**: Procesamiento seguro de pagos

### 👨‍💼 Panel de Administración

- **Gestión de Citas**: Visualiza y administra todas las citas en todas las ubicaciones
- **Control de Inventario**: Seguimiento de niveles de stock y ventas de productos
- **Programación de Personal**: Gestiona horarios de empleados y asignación de servicios
- **Análisis de Negocio**: Visualiza métricas de rendimiento y tendencias de clientes
- **Gestión de Contenido**: Actualiza servicios, productos e información de sucursales

## 🔧 Detalles Técnicos

BarberMusic&Spa está construido con un stack tecnológico robusto:

- **Backend**: Java Spring Boot 3.2.11 con arquitectura MVC
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5
- **Base de Datos**: MySQL 8.0 con esquema completo de 20+ tablas
- **Seguridad**: Spring Security con acceso basado en roles (RBAC)
- **Autenticación**: BCrypt password encoding + session management
- **Integraciones API**:
  - PayPal y MercadoPago para procesamiento de pagos
  - Google Maps para servicios de localización
  - Sistema de notificación por correo electrónico

## 📊 Modelo de Base de Datos

La aplicación utiliza un esquema de base de datos completo con las siguientes entidades principales:

### Entidades de Usuario y Roles

- **usuarios**: Cuentas con sistema de roles jerárquico
- **personal**: Información adicional para empleados
- **direcciones**: Tabla polimórfica para direcciones de usuarios/sucursales

### Entidades de Negocio

- **sucursales**: 7 sucursales en México con información completa
- **servicios**: 16 servicios desde tratamientos láser hasta masajes
- **productos**: Catálogo de productos para cuidado personal
- **categorias**: Clasificación de servicios y productos
- **especialidades**: Especialidades del personal (láser, masajes, etc.)

### Entidades Operacionales

- **agendamientos**: Sistema de citas con estados y seguimiento
- **ordenes** y **detalle_ordenes**: Gestión completa de pedidos
- **favoritos**: Sistema de favoritos para productos/servicios
- **transacciones_pago**: Registro de pagos con múltiples métodos
- **recordatorios**: Sistema de notificaciones
- **reseñas**: Tabla polimórfica para reseñas de servicios/productos/sucursales

### Tablas de Configuración

- **horarios_sucursal**: Horarios regulares por sucursal
- **excepciones_horario_sucursal**: Días especiales/feriados
- **promociones**: Sistema de códigos de descuento
- **musica_preferencias_navegacion**: Preferencias musicales de clientes

## 🚀 Instalación y Configuración

### Requisitos del Sistema

- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior
- Mínimo 2GB RAM, 1GB de espacio en disco

### Pasos de Instalación

1. **Clona el repositorio**:

   ```bash
   git clone https://github.com/tuusuario/barberspa.git
   cd barberspa
   ```

2. **Configura la base de datos**:

   ```sql
   CREATE DATABASE bmspa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Ejecuta el script SQL completo**:

   ```bash
   mysql -u tu_usuario -p bmspa < database_schema.sql
   ```

4. **Configura application.properties**:

   ```properties
   # Base de datos
   spring.datasource.url=jdbc:mysql://localhost:3306/bmspa?useSSL=false&serverTimezone=UTC
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña

   # Configuración de servidor
   server.port=8080

   # Configuración de Thymeleaf
   spring.thymeleaf.cache=false
   spring.thymeleaf.prefix=classpath:/templates/
   spring.thymeleaf.suffix=.html
   ```

5. **Compila y ejecuta**:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

6. **Accede a la aplicación**:
   ```
   http://localhost:8080
   ```

### Usuarios de Prueba

La aplicación incluye usuarios de prueba para cada rol:

```sql
-- Gerente (Súper Admin)
Email: admin@barbermusicaspa.com
Password: [BCrypt hash incluido en BD]

-- Empleados por sucursal
Email: admin.strada@barbermusicaspa.com (Villahermosa - Plaza Strada)
Email: admin.slp@barbermusicaspa.com (San Luis Potosí)
...

-- Clientes
Email: alejandra.vazquez@gmail.com
Email: roberto.silva@gmail.com
...
```

## 🔄 Cambios Recientes Implementados

### ✅ Reorganización de Templates por Roles

- Migración completa de templates a estructura por roles
- Actualización de todos los controladores para nuevas rutas
- Configuración de Spring Security para nueva estructura

### ✅ Correcciones de Controladores

- **HomeController**: Todas las rutas ahora retornan templates `publico/`
- **UsuarioController**: Rutas actualizadas a `cliente/` y `publico/`
- **FavoritoController**: Migrado de `/usuario/favoritos` a `/cliente/favoritos`
- **LoginController y RegistroController**: Redirects actualizados a `/publico/login`

### ✅ Nuevos Templates Creados

- `publico/serviciosVista.html`: Catálogo completo de servicios
- `cliente/detallecompra.html`: Detalle de órdenes para clientes
- `publico/mantenimiento.html`: Página de mantenimiento
- Templates organizados por roles con navegación específica

### ✅ Spring Security Actualizado

- Configuración de rutas por roles
- Protección de endpoints según jerarquía de permisos
- Manejo de sesiones HTTP + Spring Security context

## 🔴 Problemas Conocidos Pendientes

### 1. **Recursos de Imágenes Faltantes**

```
# Imágenes que faltan en /assets/img/
- proxim500x500.jpg
- musicspavillahermosa-500x500.jpg
- musisss-500x500.jpg
- barbermusicspa-500x500.jpg
- bspa500x500.jpg
- Sucursal San Luis Potosí_barbermusicspa-500x500.jpg
```

### 2. **Spring Security Firewall**

- Bloqueo de URLs con doble slash (`//`)
- Necesario revisar generación de rutas en templates

### 3. **Organización de Assets**

Los assets necesitan reorganización según nueva estructura:

```
src/main/resources/static/assets/img/
├── servicios/          # Imágenes de servicios
├── productos/          # Imágenes de productos
├── sucursales/         # Imágenes de sucursales
└── usuarios/           # Avatares y fotos de perfil
```

## 🔮 Próximas Mejoras

1. **Completar sistema de favoritos** con interfaz AJAX
2. **Implementar carrito de compras** funcional
3. **Integrar pasarelas de pago** (PayPal/MercadoPago)
4. **Sistema de notificaciones** en tiempo real
5. **Panel administrativo** con métricas y reportes
6. **API REST** para integración móvil
7. **Sistema de reseñas** con moderación

## 👨‍💻 Equipo de Desarrollo

- [Rody Avila](https://github.com/username1) - Gerente de Proyecto
- [Daniel Ortiz](https://github.com/username2) - Desarrollador Principal
- [Carlos Rodriguez](https://github.com/username3) - Desarrollador Backend
- [Daniel Gomez](https://github.com/username4) - Desarrollador Frontend

## 📜 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 📧 Contacto

Para consultas o soporte, por favor contáctanos en: [catcomarketing@gmail.com](mailto:catcomarketing@gmail.com)

---

<div align="center">
  <p>© BarberMusic&Spa - Experiencia Premium en Spa y Barbería</p>
  <p>🎵 Donde el estilo se encuentra con la relajación 💈</p>
</div>

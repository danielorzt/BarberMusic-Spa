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

Visita nuestra aplicación: [BarberMusic&Spa](https://barbermusicandspsa.com) (¡Próximamente!)

## 🌟 Descripción General

BarberMusic&Spa es una aplicación web integral diseñada para una cadena premium de spas y barberías con múltiples sucursales en México. La plataforma combina capacidades de reserva, funcionalidad de comercio electrónico y herramientas administrativas para crear una experiencia perfecta tanto para clientes como para el personal.

Nuestra propuesta única de valor es la integración de música y relajación en cada servicio, proporcionando una experiencia única para nuestros clientes.

## ✨ Características Principales

### 🧔 Portal del Cliente
- **Reserva de Servicios**: Programa citas para tratamientos de spa y servicios de barbería
- **Tienda de Productos**: Navega y compra productos premium para el cuidado personal
- **Localizador de Sucursales**: Encuentra la ubicación más cercana de BarberMusic&Spa con mapas interactivos
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

- **Backend**: Java Spring Boot con arquitectura MVC
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5
- **Base de Datos**: MySQL
- **Seguridad**: Spring Security con acceso basado en roles e integración OAuth2
- **Integraciones API**:
  - PayPal y MercadoPago para procesamiento de pagos
  - Google Maps para servicios de localización
  - Sistema de notificación por correo electrónico

## 📋 Requisitos del Sistema

- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior
- Mínimo 2GB RAM, 1GB de espacio en disco

## 🚀 Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tuusuario/barberspa.git
   cd barberspa
   ```

2. Configura application.properties con tus datos de base de datos:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bmspa
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

3. Ejecuta el script SQL para configurar el esquema de la base de datos:
   ```bash
   mysql -u tu_usuario -p bmspa < bmspa.sql
   ```

4. Compila y ejecuta la aplicación:
   ```bash
   mvn clean install
   java -jar target/barberSpa-0.0.1-SNAPSHOT.jar
   ```

5. Accede a la aplicación:
   ```
   http://localhost:63106
   ```

## 📸 Capturas de Pantalla

<div align="center">
  <img src="https://i.imgur.com/screenshot1.png" alt="Página Principal" width="400"/>
  <img src="https://i.imgur.com/screenshot2.png" alt="Panel de Administración" width="400"/>
  <img src="https://i.imgur.com/screenshot3.png" alt="Sistema de Reservas" width="400"/>
  <img src="https://i.imgur.com/screenshot4.png" alt="Tienda de Productos" width="400"/>
</div>

## 📊 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/sena/barberspa/
│   │   ├── config/            # Configuraciones de la aplicación
│   │   ├── controller/        # Controladores MVC
│   │   ├── model/             # Modelos de entidades
│   │   ├── repository/        # Interfaces de acceso a datos
│   │   ├── service/           # Lógica de negocio
│   │   └── BarberSpaApplication.java  # Punto de entrada
│   └── resources/
│       ├── static/            # Recursos estáticos (CSS, JS)
│       ├── templates/         # Plantillas Thymeleaf
│       └── application.properties  # Configuración de la aplicación
└── test/                      # Clases de prueba
```

## 🔄 Modelo de Base de Datos

La aplicación utiliza las siguientes entidades principales:

- **Usuario**: Cuentas de usuario con roles (ADMIN, USER)
- **Servicio**: Servicios disponibles con descripciones, duraciones y precios
- **Producto**: Productos para venta en la tienda en línea
- **Sucursal**: Ubicaciones de sucursales con dirección y horarios de operación
- **Agendamiento**: Reservas de citas que vinculan usuarios, servicios y sucursales
- **Orden y DetalleOrden**: Gestión de pedidos para compras de productos
- **Recordatorio**: Sistema de notificaciones y recordatorios

## 🌈 Roles y Acceso

- **Administradores**: Acceso completo para gestionar servicios, productos, citas, sucursales y ver analíticas
- **Usuarios**: Pueden reservar citas, comprar productos, gestionar su perfil y ver historial de pedidos

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

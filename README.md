# App ServiTech - Evaluación Final

## 1. Integrantes

*   Carlos Poblete
*   Joseph Muñoz

---

## 2. Funcionalidades

La aplicación cuenta con dos roles de usuario (Cliente y Técnico) con las siguientes funcionalidades:

**Para Clientes:**

*   **Registro y Login:** Los clientes pueden crear una cuenta y acceder a la aplicación.
*   **Ver Servicios:** Muestra una lista de todos los servicios ofrecidos por los técnicos, obtenidos desde un microservicio desplegado en la nube.
*   **Crear Órdenes de Servicio:** Los clientes pueden solicitar un nuevo servicio, llenando un formulario con sus datos de contacto, fecha y notas adicionales.
*   **Ver "Mis Órdenes":** Permite a los clientes ver el estado de las órdenes que han solicitado.
*   **API Externa ¡Alegra tu día!:** En la pantalla principal, se consume una API externa para mostrar una lista de chistes de programación en español.

**Para Técnicos:**

*   **Login de Técnico:** Acceso a un dashboard especial con credenciales predefinidas (`tecnico@gmail.com` / `123456`).
*   **Gestión de Servicios (CRUD):**
    *   **Crear:** Añadir nuevos servicios que se ofrecen a los clientes.
    *   **Editar:** Modificar los detalles de servicios existentes.
    *   **Eliminar:** Quitar servicios de la lista.
*   **Gestión de Órdenes:**
    *   Ver una lista completa de todas las órdenes de servicio de los clientes.
    *   Buscar órdenes por el nombre del cliente.
    *   Actualizar el estado de una orden (ej. de "Pendiente" a "Completado").

---

## 3. Endpoints Usados

Se utilizaron endpoints propios (desplegados en la nube) y un endpoint externo.

*   **Endpoints Propios (Microservicio Java/SpringBoot en AWS EC2):**
    *   **Base URL:** `http://[DIRECCION_IP_EC2]:8080` (IP pública de la instancia desplegada en AWS).
    *   `GET /services`: Obtiene la lista de todos los servicios.
    *   `POST /services/new`: Crea un nuevo servicio.
    *   `PUT /services/{id}`: Actualiza un servicio existente.
    *   `DELETE /services/{id}`: Elimina un servicio.

*   **Endpoint Externo (JokeAPI):**
    *   **Base URL:** [`https://v2.jokeapi.dev/`](https://v2.jokeapi.dev/)
    *   `GET /joke/Programming?type=twopart&amount=10&lang=es`: Obtiene 10 chistes de programación en español.

---

## 4. Instrucciones para Ejecutar el Proyecto

1.  **Clonar el Repositorio:** Clonar este repositorio usando `git clone https://github.com/Carlospobletep99/Eval2.git`.
2.  **Abrir en Android Studio:** Abrir el proyecto clonado con la última versión de Android Studio.
3.  **Ejecutar la App:** Compilar y ejecutar la aplicación en un emulador de Android o en un dispositivo físico.

**Nota Importante:** El microservicio backend se encuentra desplegado y activo en una instancia de AWS EC2, por lo que **no se requiere ninguna acción para levantarlo**. La aplicación se conectará a la nube automáticamente siempre que tenga acceso a internet, si no hay conexion a internet la aplicacion funcionara de manera normal pero no apareceran los servicios a ofrecer provenientes del microservicio ni la informacion proveniente desde la API.

---

## 5. APK Firmado y Ubicación del Keystore

Para la publicación y distribución, se ha generado un APK firmado.

*   **Ubicación:** Los archivos se encuentran en una carpeta local llamada **`FIRMA (APK Y KEYSTORE)`**.
*   **Contenido:**
    *   `ServiTech-Firmada.apk`: La versión de la aplicación firmada y optimizada para producción.
    *   `claveApp`: El archivo **Keystore (.jks)** que contiene la clave privada de firma. **Este archivo es crucial y no debe perderse**.
    *   `info_keystore.txt`: Un archivo de texto con las credenciales (alias y contraseñas) necesarias para usar el keystore en futuras actualizaciones.

---

## 6. Código Fuente

El proyecto se compone de dos repositorios de GitHub independientes:

*   **[App Móvil (Android/Kotlin)](https://github.com/Carlospobletep99/Eval2):** Repositorio que contiene todo el código fuente de la aplicación Android.
*   **[Microservicio (Java/SpringBoot)](https://github.com/Carlospobletep99/msservicios):** Repositorio con el código fuente del backend que gestiona los servicios.

---

## 7. Evidencia de Trabajo Colaborativo (Commits)

El desarrollo del proyecto se ha organizado en dos ramas principales para reflejar las distintas fases del trabajo:

*   **[Rama `master`](https://github.com/Carlospobletep99/Eval2/tree/master):** Contiene la versión final y estable del proyecto, integrando todas las funcionalidades y los tests unitarios.
*   **[Rama `version_con_historial`](https://github.com/Carlospobletep99/Eval2/tree/version_con_historial):** Esta rama documenta la **historia completa y detallada de commits** del desarrollo de la aplicación.

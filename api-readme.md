# Arquitectura de la Aplicación FDC API

Este documento describe la arquitectura técnica de la aplicación, detallando el flujo de datos y las responsabilidades de los componentes clave.
Para más información sobre el api-key o limites de acceso a los datos de la FDC (Food Data Central) de la USDA, consulta su página web.

---

## Arquitectura General (MVC)

La aplicación sigue el patrón **Model-View-Controller (MVC)** con una clara separación de responsabilidades entre la obtención de datos externos y la persistencia local.

### Flujo de Datos

1.  **Lectura (API Externa):**
    *   **Clase:** `FdcClient` (`com.fdcapi.client`)
    *   **Responsabilidad:** Realiza las peticiones HTTP a la API de Food Data Central (USDA).
    *   **Uso:** Es invocada por `FoodService` para buscar alimentos y obtener detalles nutricionales.

2.  **Procesamiento (Lógica de Negocio):**
    *   **Clase:** `FoodService` (`com.fdcapi.service`)
    *   **Responsabilidad:** Orquesta la búsqueda y transformación de datos crudos de la API a modelos de dominio (`Food`, `Nutrient`).

3.  **Escritura (Base de Datos Local):**
    *   **Clase:** `DataService` (`com.fdcdatax.service`)
    *   **Responsabilidad:** Persiste la información seleccionada en la base de datos MySQL local.
    *   **Operaciones:** Guarda alimentos (`saveFood`) y registra logs de actividad (`logAction`).

---

## Componentes Principales

### 1. Capa de Controlador (`WebController`)
Maneja las interacciones del usuario y la navegación entre vistas.
- **Rutas:** `/`, `/search`, `/details/{id}`, `/save`, `/my-db`.

### 2. Capa de Servicio
- **`FoodService`:** Intermediario entre el controlador y la API externa.
- **`DataService`:** Intermediario entre el controlador y la base de datos local.
- **`TranslationService`:** Traduce dinámicamente los nombres de nutrientes al español para la vista.

### 3. Capa de Persistencia (JPA/Hibernate)
Entidades que mapean las tablas de la base de datos:
- `Alimento`
- `Nutriente`
- `AlimentoNutriente`
- `ActionLog`

---

## ���️ Manejo de Errores
La aplicación utiliza `GlobalExceptionHandler` para capturar y gestionar excepciones como:
- Fallos de conexión con la API (`FdcApiException`).
- Alimentos no encontrados (`FoodNotFoundException`).

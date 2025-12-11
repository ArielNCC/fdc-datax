# ÌøóÔ∏è Arquitectura de la Aplicaci√≥n FDC API

Este documento describe la arquitectura t√©cnica de la aplicaci√≥n, detallando el flujo de datos y las responsabilidades de los componentes clave.

---

## Ì≥ê Arquitectura General (MVC)

La aplicaci√≥n sigue el patr√≥n **Model-View-Controller (MVC)** con una clara separaci√≥n de responsabilidades entre la obtenci√≥n de datos externos y la persistencia local.

### Ì¥Ñ Flujo de Datos

1.  **Lectura (API Externa):**
    *   **Clase:** `FdcClient` (`com.fdcapi.client`)
    *   **Responsabilidad:** Realiza las peticiones HTTP a la API de Food Data Central (USDA).
    *   **Uso:** Es invocada por `FoodService` para buscar alimentos y obtener detalles nutricionales.

2.  **Procesamiento (L√≥gica de Negocio):**
    *   **Clase:** `FoodService` (`com.fdcapi.service`)
    *   **Responsabilidad:** Orquesta la b√∫squeda y transformaci√≥n de datos crudos de la API a modelos de dominio (`Food`, `Nutrient`).

3.  **Escritura (Base de Datos Local):**
    *   **Clase:** `DataService` (`com.fdcdatax.service`)
    *   **Responsabilidad:** Persiste la informaci√≥n seleccionada en la base de datos MySQL local.
    *   **Operaciones:** Guarda alimentos (`saveFood`) y registra logs de actividad (`logAction`).

---

## Ì∑© Componentes Principales

### 1. Capa de Controlador (`WebController`)
Maneja las interacciones del usuario y la navegaci√≥n entre vistas.
- **Rutas:** `/`, `/search`, `/details/{id}`, `/save`, `/my-db`.

### 2. Capa de Servicio
- **`FoodService`:** Intermediario entre el controlador y la API externa.
- **`DataService`:** Intermediario entre el controlador y la base de datos local.
- **`TranslationService`:** Traduce din√°micamente los nombres de nutrientes al espa√±ol para la vista.

### 3. Capa de Persistencia (JPA/Hibernate)
Entidades que mapean las tablas de la base de datos:
- `Alimento`
- `Nutriente`
- `AlimentoNutriente`
- `ActionLog`

---

## Ìª†Ô∏è Manejo de Errores
La aplicaci√≥n utiliza `GlobalExceptionHandler` para capturar y gestionar excepciones como:
- Fallos de conexi√≥n con la API (`FdcApiException`).
- Alimentos no encontrados (`FoodNotFoundException`).

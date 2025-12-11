# FDC Data Exporter

## 🎯 Objetivo
Dar una solución rápida a profesionales que se dedican a la validación de etiquetados nutricionales, accediendo a la FDC (Food Data Central) para poder encontrar información suficiente y relevante para sus documentaciones. Así también los datos obtenidos pueden ser guardados y usados como guía para la elaboración de otros etiquetados. 

El objetivo es poder llegar a expandir el acceso rápido a la información no solo a pequeños emprendedores, si no que también a usuarios que puedan ser futuros fiscalizadores del cumplimiento de los etiquetados nutricionales.

## 📚 Documentación Técnica
Para detalles específicos sobre la estructura de datos y la integración con la API, consulta los siguientes documentos:
- **Datos y Estructura:** [datos-readme.md](datos-readme.md)
- **Lógica de API:** [api-readme.md](api-readme.md)

## 📋 Requisitos del Sistema
Para ejecutar este programa necesitas tener instalado:
- **Java JDK 21**
- **Maven** (para gestión de dependencias y construcción)
- **MySQL Server** (Base de datos)

## ⚙️ Configuración

> **⚠️ IMPORTANTE:** Debes configurar tus propias credenciales antes de ejecutar el proyecto. Ver [SECURITY.md](SECURITY.md) para instrucciones completas.

### Base de Datos y Puertos
La configuración principal se encuentra en `src/main/resources/application.properties`.

**Base de Datos MySQL:**
Por defecto, la aplicación conecta a tu base de datos MySQL.
```properties
spring.datasource.url=TU_URL
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

**Puerto del Servidor:**
La aplicación web se despliega en tu puerto configurado.
```properties
server.port=TU_PUERTO
```

## 🚀 Comandos para Correr el Programa

1. **Compilar el proyecto:**
   ```bash
   mvn clean install
   ```

2. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

## 🖥️ Accesos y Uso

Una vez que la aplicación esté corriendo, abre tu navegador en las siguientes direcciones:

- **Inicio (Buscador):** [TU_URL/](TU_URL/)
  - Aquí puedes buscar alimentos por **ID** o por **Nombre** (usando términos en inglés, ej: "Beef", "Apple").
- **Mi Base de Datos:** [TU_URL/my-db](TU_URL/my-db)
  - Visualiza, gestiona y elimina los alimentos que has guardado localmente.
- **Historial:** [TU_URL/history](TU_URL/history)
  - Registro de auditoría de las acciones realizadas en el sistema.
- **Configuración:** [TU_URL/settings](TU_URL/settings)
  - Vista de parámetros de configuración (credenciales enmascaradas).

### Flujo de Uso Típico
1. Ingresa al **Inicio** y busca un alimento.
2. Selecciona un resultado para ver el **Detalle**. La información nutricional se mostrará traducida al español.
3. Utiliza las opciones disponibles:
   - **Guardar en BD:** Almacena el registro para uso futuro.
   - **Descargar Imagen:** Genera una imagen de la tabla nutricional.
   - **Descargar CSV:** Exporta los datos en formato plano.

## 🔮 Mejoras a Futuro
1. Permitir modificación libre de token de acceso FDC, URL/usuario/password de la base de datos desde la interfaz.
2. Mejora en la traducción usando un diccionario interno más extenso y dinámico.
3. Búsqueda de alimento por contenido nutricional para encontrar coincidencias específicas.
4. Permitir añadir una columna de porciones a la imagen generada del etiquetado.
5. Calculador de contenido nutricional mezclando distintos alimentos (recetas).
6. Mayor documentación de errores y logs detallados.

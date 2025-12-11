# ��� Estructura de Datos y Persistencia

Este documento detalla cómo se estructuran los datos obtenidos de la API FDC y cómo se almacenan en la base de datos local.

---

## ��� Transformación de Datos

El flujo de datos implica una transformación desde el formato JSON de la API externa hacia el modelo relacional de nuestra base de datos.

### 1. Origen: API Food Data Central (JSON)
Los datos crudos provienen de la API del USDA.
- **Clase Lectora:** `FdcClient`
- **Formato:** JSON complejo con múltiples niveles de anidamiento.

### 2. Modelo de Dominio (Java)
Simplificamos la estructura para uso interno de la aplicación.
- **Clase:** `Food`
    - `fdcId`: Identificador único.
    - `description`: Nombre del alimento.
    - `brandOwner`: Marca.
    - `nutrients`: Lista de objetos `Nutrient`.

### 🔍 Ejemplo de Transformación JSON

**1. JSON Recibido de FDC (Entrada):**
*Nota: Contiene muchos metadatos técnicos y campos adicionales no utilizados.*
```json
{
  "dataType": "Branded",
  "description": "NUT 'N BERRY MIX",
  "fdcId": 534358,
  "foodNutrients": [
    {
      "number": "303",
      "name": "Iron, Fe",
      "amount": 0.53,
      "unitName": "mg",
      "derivationCode": "LCCD",
      "derivationDescription": "Calculated from a daily value percentage per serving size measure"
    }
  ],
  "publicationDate": "4/1/2019",
  "brandOwner": "Kar Nut Products Company",
  "gtinUpc": "077034085228",
  "ndbNumber": 7954,
  "foodCode": "27415110"
}
```

**2. JSON Retornado por Nuestra API (Salida):**
*Nota: Estructura limpia, solo con la información relevante para el usuario.*
```json
{
  "fdcId": 534358,
  "description": "NUT 'N BERRY MIX",
  "brandOwner": "Kar Nut Products Company",
  "nutrients": [
    {
      "nutrientId": 303,
      "name": "Iron, Fe",
      "value": 0.53,
      "unitName": "mg"
    }
  ]
}
```

### 3. Destino: Base de Datos Local (MySQL)
Los datos seleccionados por el usuario son persistidos mediante `DataService`.

#### Esquema Relacional

**Tabla: `alimento`**
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id_alimento` | BIGINT (PK) | ID original de FDC. |
| `descripcion` | VARCHAR | Nombre del alimento (en inglés). |
| `brand_owner` | VARCHAR | Marca del producto. |

**Tabla: `nutriente`**
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id_nutriente` | BIGINT (PK) | ID del nutriente (estándar FDC). |
| `nombre` | VARCHAR | Nombre del nutriente (ej. Protein). |
| `unidad_medida` | VARCHAR | Unidad (g, mg, kcal). |

**Tabla: `alimento_nutriente`**
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | BIGINT (PK) | ID autogenerado. |
| `id_alimento` | BIGINT (FK) | Referencia a `alimento`. |
| `id_nutriente` | BIGINT (FK) | Referencia a `nutriente`. |
| `valor` | DOUBLE | Cantidad del nutriente. |

---

## ��� Notas sobre Persistencia
- **Idioma:** Los datos se guardan en la base de datos en su idioma original (Inglés) para mantener la integridad con la fuente oficial.
- **Traducción:** La visualización en español se realiza en tiempo de ejecución mediante `TranslationService`, sin alterar los datos almacenados.

CREATE DATABASE `chile_fdc` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `chile_fdc`;

-- Tabla de alimentos (Food)
CREATE TABLE alimento (
    id_alimento      BIGINT PRIMARY KEY,         -- fdcId
    descripcion      VARCHAR(255) NOT NULL,
    brand_owner      VARCHAR(255)                -- Puede ser NULL
) ENGINE=InnoDB;

-- Tabla de nutrientes (Nutrient)
CREATE TABLE nutriente (
    id_nutriente     BIGINT PRIMARY KEY,         -- nutrientId
    nombre           VARCHAR(255) NOT NULL,      -- name
    unidad_medida    VARCHAR(20),                -- unitName (puede ser NULL)
    descripcion      VARCHAR(255)                -- opcional, para descripciones extendidas
) ENGINE=InnoDB;

-- Tabla puente alimento_nutriente (relación N:M)
CREATE TABLE alimento_nutriente (
    id_alimento      BIGINT NOT NULL,
    id_nutriente     BIGINT NOT NULL,
    valor            DOUBLE,                     -- value (puede ser NULL)
    PRIMARY KEY (id_alimento, id_nutriente),
    CONSTRAINT fk_alimento FOREIGN KEY (id_alimento) REFERENCES alimento(id_alimento) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_nutriente FOREIGN KEY (id_nutriente) REFERENCES nutriente(id_nutriente) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Índices para escalabilidad
CREATE INDEX idx_alimento_descripcion ON alimento(descripcion);
CREATE INDEX idx_nutriente_nombre ON nutriente(nombre);
CREATE INDEX idx_alimento_nutriente_valor ON alimento_nutriente(valor);

-- ===============================
-- Vistas para consultas rápidas desde aplicaciones externas
-- ===============================

-- Vista 1: Contenido nutricional completo de un alimento por su ID
CREATE OR REPLACE VIEW vista_contenido_nutricional_por_id AS
SELECT 
    a.id_alimento,
    a.descripcion AS alimento,
    a.brand_owner,
    n.id_nutriente,
    n.nombre AS nutriente,
    an.valor AS cantidad,
    n.unidad_medida
FROM alimento a
JOIN alimento_nutriente an ON a.id_alimento = an.id_alimento
JOIN nutriente n ON n.id_nutriente = an.id_nutriente;

-- Vista 2: Consulta resumida de nutrientes clave por palabra en la descripción
CREATE OR REPLACE VIEW vista_nutrientes_clave_por_descripcion AS
SELECT 
    a.id_alimento,
    a.descripcion,
    MAX(CASE WHEN n.id_nutriente = 1051 THEN an.valor END) AS agua,         -- Agua
    MAX(CASE WHEN n.id_nutriente = 1003 THEN an.valor END) AS proteinas,    -- Proteína
    MAX(CASE WHEN n.id_nutriente = 1008 THEN an.valor END) AS calorias,     -- Calorías
    MAX(CASE WHEN n.id_nutriente = 1004 THEN an.valor END) AS grasa_total   -- Grasa total
FROM alimento a
LEFT JOIN alimento_nutriente an ON a.id_alimento = an.id_alimento
LEFT JOIN nutriente n ON n.id_nutriente = an.id_nutriente
GROUP BY a.id_alimento, a.descripcion;
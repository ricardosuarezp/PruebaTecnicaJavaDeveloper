CREATE TABLE clientes(
    id VARCHAR(50) PRIMARY KEY,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE zonas (
    id VARCHAR(50) PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE cargas_idempotencia(
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    archivo_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_idempotencia_hash UNIQUE (idempotency_key, archivo_hash)
);

CREATE TABLE pedidos(
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR(100) NOT NULL UNIQUE,
    cliente_id VARCHAR(50) NOT NULL,
    zona_id VARCHAR(50) NOT NULL,
    fecha_entrega DATE NOT NULL,
    estado VARCHAR(20) CHECK(estado IN ('PENDIENTE','CONFIRMADO','ENTREGADO')),
    requiere_refrigeracion BOOLEAN NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pedidos_estado_fecha ON pedidos(estado,fecha_entrega);

INSERT INTO clientes (id, activo) VALUES ('CLI-123', true), ('CLI-999', true);
INSERT INTO zonas (id, soporte_refrigeracion) VALUES ('ZONA1', true), ('ZONA5', false);
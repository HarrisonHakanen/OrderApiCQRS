CREATE TABLE chaves_idempotencia (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid              UUID NOT NULL DEFAULT gen_random_uuid(),
    escopo            VARCHAR(100) NOT NULL,
    chave             VARCHAR(255) NOT NULL,
    hash_requisicao   VARCHAR(64) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PROCESSANDO',
    pedido_id         BIGINT,
    http_status       SMALLINT,
    resposta          JSONB,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_chaves_idempotencia_uuid UNIQUE (uuid),
    CONSTRAINT uq_chaves_idempotencia_escopo_chave UNIQUE (escopo, chave),
    CONSTRAINT fk_chaves_idempotencia_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT ck_chaves_idempotencia_status
        CHECK (status IN ('PROCESSANDO', 'CONCLUIDO', 'FALHA')),
    CONSTRAINT ck_chaves_idempotencia_http_status
        CHECK (http_status IS NULL OR http_status BETWEEN 100 AND 599)
);

CREATE INDEX idx_chaves_idempotencia_pedido_id
    ON chaves_idempotencia (pedido_id)
    WHERE pedido_id IS NOT NULL;

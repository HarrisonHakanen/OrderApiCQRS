CREATE TABLE clientes (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid(),
    nome        VARCHAR(150) NOT NULL,
    documento   VARCHAR(20),
    email       VARCHAR(255),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_clientes_uuid UNIQUE (uuid),
    CONSTRAINT uq_clientes_documento UNIQUE (documento)
);

CREATE TABLE produtos (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid         UUID NOT NULL DEFAULT gen_random_uuid(),
    nome         VARCHAR(200) NOT NULL,
    sku          VARCHAR(60) NOT NULL,
    preco_atual  NUMERIC(15, 2) NOT NULL,
    ativo        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_produtos_uuid UNIQUE (uuid),
    CONSTRAINT uq_produtos_sku UNIQUE (sku),
    CONSTRAINT ck_produtos_preco_nao_negativo CHECK (preco_atual >= 0)
);

CREATE TABLE pedidos (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid        UUID DEFAULT gen_random_uuid(),
    cliente_id  BIGINT NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'CRIADO',
    subtotal    NUMERIC(15, 2) NOT NULL DEFAULT 0,
    desconto    NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total       NUMERIC(15, 2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pedidos_uuid UNIQUE (uuid),
    CONSTRAINT fk_pedidos_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT ck_pedidos_status
        CHECK (status IN ('CRIADO', 'PROCESSANDO', 'FATURADO', 'CANCELADO')),
    CONSTRAINT ck_pedidos_valores_nao_negativos
        CHECK (subtotal >= 0 AND desconto >= 0 AND total >= 0),
    CONSTRAINT ck_pedidos_desconto_limite
        CHECK (desconto <= subtotal),
    CONSTRAINT ck_pedidos_total
        CHECK (total = subtotal - desconto)
);

CREATE TABLE pedido_itens (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid(),
    pedido_id       BIGINT NOT NULL,
    produto_id      BIGINT NOT NULL,
    quantidade      INTEGER NOT NULL,
    preco_unitario  NUMERIC(15, 2) NOT NULL,
    desconto        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    subtotal        NUMERIC(15, 2) NOT NULL,
    CONSTRAINT uq_pedido_itens_uuid UNIQUE (uuid),
    CONSTRAINT fk_pedido_itens_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT fk_pedido_itens_produto
        FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT ck_pedido_itens_quantidade CHECK (quantidade > 0),
    CONSTRAINT ck_pedido_itens_valores
        CHECK (preco_unitario >= 0 AND desconto >= 0 AND subtotal >= 0),
    CONSTRAINT ck_pedido_itens_desconto_limite
        CHECK (desconto <= quantidade * preco_unitario),
    CONSTRAINT ck_pedido_itens_subtotal
        CHECK (subtotal = quantidade * preco_unitario - desconto)
);


CREATE TABLE estoques (
    produto_id              BIGINT PRIMARY KEY,
    quantidade_disponivel   INTEGER NOT NULL DEFAULT 0,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_estoques_produto
        FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT ck_estoques_quantidade CHECK (quantidade_disponivel >= 0)
);

CREATE TABLE pedido_status_historicos (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid              UUID NOT NULL DEFAULT gen_random_uuid(),
    pedido_id         BIGINT NOT NULL,
    status_anterior   VARCHAR(20),
    status_novo       VARCHAR(20) NOT NULL,
    data_alteracao    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo            VARCHAR(255),
	created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pedido_status_historicos_uuid UNIQUE (uuid),
    CONSTRAINT fk_pedido_status_historicos_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    CONSTRAINT ck_pedido_status_historicos_status_anterior
        CHECK (status_anterior IS NULL OR status_anterior IN
            ('CRIADO', 'PROCESSANDO', 'FATURADO', 'CANCELADO')),
    CONSTRAINT ck_pedido_status_historicos_status_novo
        CHECK (status_novo IN ('CRIADO', 'PROCESSANDO', 'FATURADO', 'CANCELADO'))
);


CREATE INDEX idx_pedidos_cliente_created_at
    ON pedidos (cliente_id, created_at DESC);

CREATE INDEX idx_pedidos_status_created_at
    ON pedidos (status, created_at DESC);

CREATE INDEX idx_pedido_itens_pedido_id
    ON pedido_itens (pedido_id);

CREATE INDEX idx_pedido_itens_produto_id
    ON pedido_itens (produto_id);

CREATE INDEX idx_pedido_status_historicos_pedido_data
    ON pedido_status_historicos (pedido_id, data_alteracao DESC);

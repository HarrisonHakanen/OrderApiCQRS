CREATE TABLE pedidos_leitura (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pedido_uuid UUID NOT NULL UNIQUE,
cliente_uuid UUID NOT NULL,
status VARCHAR(20) NOT NULL,
subtotal NUMERIC(19, 2) NOT NULL,
desconto NUMERIC(19, 2) NOT NULL DEFAULT 0,
total NUMERIC(19, 2) NOT NULL,
criado_em TIMESTAMPTZ NOT NULL,
atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

CONSTRAINT chk_pedidos_leitura_status
CHECK (status IN (
'CRIADO',
'PROCESSANDO',
'FATURADO',
'CANCELADO'
))
);

CREATE TABLE pedido_itens_leitura (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pedido_uuid UUID NOT NULL,
produto_uuid UUID NOT NULL,
quantidade INTEGER NOT NULL,
preco_unitario NUMERIC(19, 2) NOT NULL,
desconto NUMERIC(19, 2) NOT NULL DEFAULT 0,
subtotal NUMERIC(19, 2) NOT NULL,

CONSTRAINT fk_pedido_itens_leitura_pedido
  FOREIGN KEY (pedido_uuid)
      REFERENCES pedidos_leitura (pedido_uuid)
      ON DELETE CASCADE,

CONSTRAINT chk_pedido_itens_leitura_quantidade
  CHECK (quantidade > 0)
);

CREATE TABLE eventos_processados (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 chave_idempotencia UUID NOT NULL,
 tipo_evento VARCHAR(100) NOT NULL,
 processado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

 CONSTRAINT uk_eventos_processados_tipo_chave
     UNIQUE (tipo_evento, chave_idempotencia)
);

CREATE INDEX idx_pedidos_leitura_cliente_data
    ON pedidos_leitura (cliente_uuid, criado_em DESC);

CREATE INDEX idx_pedidos_leitura_status_data
    ON pedidos_leitura (status, criado_em DESC);

CREATE INDEX idx_pedido_itens_leitura_pedido
    ON pedido_itens_leitura (pedido_uuid);

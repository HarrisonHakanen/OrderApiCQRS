CREATE TABLE pedido_status_historicos_leitura (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pedido_uuid UUID NOT NULL,
status VARCHAR(20) NOT NULL,
data_status TIMESTAMPTZ NOT NULL,
observacao VARCHAR(500),

CONSTRAINT chk_status_historico_leitura CHECK (
  status IN ('CRIADO', 'PROCESSANDO', 'FATURADO', 'CANCELADO')
  ),

CONSTRAINT fk_status_historico_pedido
  FOREIGN KEY (pedido_uuid)
      REFERENCES pedidos_leitura(pedido_uuid)
      ON DELETE CASCADE
);

CREATE INDEX idx_status_historico_pedido_data
    ON pedido_status_historicos_leitura(pedido_uuid, data_status);
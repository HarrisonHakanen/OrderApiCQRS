CREATE TABLE comandos_processados (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
command_uuid UUID NOT NULL UNIQUE,
tipo_comando VARCHAR(50) NOT NULL,
pedido_uuid UUID NOT NULL,
processado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
CONSTRAINT ck_comandos_processados
CHECK (tipo_comando IS NULL OR tipo_comando IN
('CRIAR_PEDIDO', 'ALTERAR_STATUS', 'CANCELAR_PEDIDO'))
);
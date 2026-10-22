CREATE TABLE outbox_eventos (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid             UUID NOT NULL DEFAULT gen_random_uuid(),
    agregado_tipo    VARCHAR(100) NOT NULL,
    agregado_id      BIGINT NOT NULL,
    tipo_evento      VARCHAR(100) NOT NULL,
    payload          JSONB NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_em     TIMESTAMPTZ,
    tentativas       INTEGER NOT NULL DEFAULT 0,
    proxima_tentativa_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_erro      TEXT,
	status           VARCHAR(20) NOT NULL DEFAULT 'CRIADO',
    CONSTRAINT uq_outbox_eventos_uuid UNIQUE (uuid),
    CONSTRAINT ck_outbox_tentativas CHECK (tentativas >= 0)
);

CREATE INDEX idx_outbox_eventos_pendentes
    ON outbox_eventos (proxima_tentativa_em, id)
    WHERE publicado_em IS NULL;

INSERT INTO clientes (nome, documento, email, ativo)
VALUES ('Cliente Teste','12345678900','cliente.teste@example.com',TRUE)
ON CONFLICT (documento) DO NOTHING;

INSERT INTO produtos (nome,sku,preco_atual,ativo)
VALUES
('Teclado Mecânico', 'TESTE-TEC-001', 250.00, TRUE),
('Mouse Gamer',      'TESTE-MOU-001', 120.00, TRUE),
('Monitor 24 Polegadas', 'TESTE-MON-001', 850.00, TRUE)
ON CONFLICT (sku) DO NOTHING;
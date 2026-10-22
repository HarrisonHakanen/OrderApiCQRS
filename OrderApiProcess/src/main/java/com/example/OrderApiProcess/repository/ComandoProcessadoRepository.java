package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.ComandoProcessado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComandoProcessadoRepository
        extends JpaRepository<ComandoProcessado, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO comandos_processados (
            command_uuid,
            tipo_comando,
            pedido_uuid,
            processado_em
        )
        VALUES (
            :commandUUID,
            :tipoComando,
            :pedidoUUID,
            NOW()
        )
        ON CONFLICT (command_uuid) DO NOTHING
        """, nativeQuery = true)
    int registrarComando(
            @Param("commandUUID") UUID commandUUID,
            @Param("tipoComando") String tipoComando,
            @Param("pedidoUUID") UUID pedidoUUID
    );
}

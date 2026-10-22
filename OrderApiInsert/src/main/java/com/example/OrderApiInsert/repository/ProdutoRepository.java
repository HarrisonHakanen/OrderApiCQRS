package com.example.OrderApiInsert.repository;

import com.example.OrderApiInsert.models.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByUuidIn(Collection<UUID> uuids);
}
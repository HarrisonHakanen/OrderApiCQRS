package com.example.OrderApiProcess.model.dto;

import com.example.OrderApiProcess.model.entity.Pedido;
import com.example.OrderApiProcess.model.entity.PedidoItem;

import java.util.List;

public record PedidoItens (
        Pedido pedido,
        List<PedidoItem> itensParaSalvar
    ){}

package com.example.OrderApiRead.controller;

import com.example.OrderApiRead.models.dto.RelatorioPedidosResponse;
import com.example.OrderApiRead.service.RelatorioPedidosService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
public class RelatorioPedidosController {

    private final RelatorioPedidosService service;

    public RelatorioPedidosController(
            RelatorioPedidosService service
    ) {
        this.service = service;
    }

    @GetMapping("/orders")
    public RelatorioPedidosResponse gerarRelatorio(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data inicial não pode ser posterior à data final."
            );
        }

        return service.gerarRelatorio(from, to);
    }
}

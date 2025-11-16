package com.rafael.pedido.controller;

import com.rafael.pedido.dtos.request.PedidoRequest;
import com.rafael.pedido.dtos.response.PedidoResponse;
import com.rafael.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest pedido) {

        PedidoResponse creado = pedidoService.crear(pedido);

        URI location = URI.create("/api/productos/" + creado.getId());
        log.info("Pedido creada: {}", creado);
        return ResponseEntity.created(location).body(creado);
    }

}

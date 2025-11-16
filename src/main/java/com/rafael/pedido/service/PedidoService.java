package com.rafael.pedido.service;

import com.rafael.pedido.dtos.request.PedidoRequest;
import com.rafael.pedido.dtos.response.PedidoResponse;
import jakarta.validation.Valid;

public interface PedidoService {
    PedidoResponse crear(@Valid PedidoRequest dto);
}

package com.rafael.pedido.dtos.fakes;

import com.rafael.pedido.dtos.response.ClienteResponse;
import com.rafael.pedido.dtos.response.ProductoDTO;

import java.math.BigDecimal;

public class FallbackConstants {

    public static final ProductoDTO PRODUCTO_FAKE = ProductoDTO.builder()
            .id(0L)
            .codigo("FAKE")
            .nombre("Producto no disponible")
            .precio(new BigDecimal(0.0))
            .stock(null)
            .degradado(Boolean.TRUE)
            .build();

    public static final ClienteResponse CLIENTE_FAKE = ClienteResponse.builder()
            .id(0L)
            .nombre("Cliente no disponible")
            .apellidos(null)
            .nif(null)
            .degradado(Boolean.TRUE)
            .build();
}

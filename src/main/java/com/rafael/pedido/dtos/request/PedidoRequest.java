package com.rafael.pedido.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data  @Builder @NoArgsConstructor @AllArgsConstructor
public class PedidoRequest {
    @JsonProperty("clienteId")
    private Long  clienteId;

    @JsonProperty("detalle")
    private List<DetalleProducto> detalle;

}

package com.rafael.pedido.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rafael.pedido.dtos.request.DetalleProducto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("clienteId")
    private Long  clienteId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellidos")
    private String apellidos;

    @JsonProperty("nif")
    private String nif;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("email")
    private String email;

    @JsonProperty("importeTotal")
    private Double importeTotal;

    @JsonProperty("detalle")
    private List<DetalleResponse> detalle;
}

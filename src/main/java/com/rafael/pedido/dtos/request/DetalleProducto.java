package com.rafael.pedido.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DetalleProducto {

    @JsonProperty("codigo")
    @NotBlank
    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "Formato inválido: debe ser 2 letras y 3 dígitos")
    private String codigo;

    @JsonProperty("cantidad")
    private Long cantidad;
}

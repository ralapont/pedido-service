package com.rafael.pedido.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductoDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("codigo")
    @NotBlank
    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "Formato inválido: debe ser 2 letras y 3 dígitos")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @JsonProperty("nombre")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @JsonProperty("precio")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @JsonProperty("stock")
    private Integer stock;

    @NotNull(message = "La categoría es obligatoria")
    @JsonProperty("categoriaId")
    private Long categoriaId;
}

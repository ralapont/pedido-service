package com.rafael.pedido.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @ToString
@Entity
public class PedidoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    private String productoCodigo; // Código del producto gestionado por otro microservicio
    private Long cantidad;
    private Double precioUnitario;

}

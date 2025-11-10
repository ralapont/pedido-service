package com.rafael.pedido.model.repository;

import com.rafael.pedido.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por cliente
    List<Pedido> findByClienteId(Long clienteId);

    // Buscar pedidos por fecha
    List<Pedido> findByFecha(LocalDate fecha);

    // Buscar pedidos que contengan un producto específico
    @Query("SELECT p FROM Pedido p JOIN p.productos pp WHERE pp.productoCodigo = :codigo")
    List<Pedido> findByProductoCodigo(@Param("codigo") String codigo);

}

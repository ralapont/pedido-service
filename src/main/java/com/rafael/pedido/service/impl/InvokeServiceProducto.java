package com.rafael.pedido.service.impl;

import com.rafael.pedido.dtos.fakes.FallbackConstants;
import com.rafael.pedido.dtos.response.ClienteResponse;
import com.rafael.pedido.dtos.response.ProductoDTO;
import com.rafael.pedido.feign.ClienteServiceClient;
import com.rafael.pedido.feign.ProductoServiceClient;
import com.rafael.pedido.model.cliente.ErrorEventoCliente;
import com.rafael.pedido.model.eventos.ErrorEvento;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvokeServiceProducto {

    private final ProductoServiceClient productoServiceClient;
    private final KafkaTemplate<String, ErrorEvento> kafkaTemplateProducto;
    private final KafkaTemplate<String, ErrorEventoCliente> kafkaTemplateClientes;

    // Inyecta el valor de la nueva propiedad
    @Value("${kafka.topicos.producto}")
    private String topicoProducto;


    @CircuitBreaker(name = "productosCB", fallbackMethod = "fallbackProducto")
    @Retry(name = "productosCB")
    public ProductoDTO obtenerDetalleProducto(String codigoProducto) {
        ResponseEntity<ProductoDTO> response = productoServiceClient.getProductoPorCodigo(codigoProducto);
        if (response.getStatusCode().value() == 403) {
             throw new RuntimeException("Acceso prohibido al servicio Productos");
        }


        log.info("Respuesta del servicio de cliente: {}", response);
        return response.getBody();
    }

    public ProductoDTO fallbackProducto(String codigoProducto, Throwable t) {
        log.warn("Fallo al obtener producto {}: {}", codigoProducto, t.getMessage());
        publicarErrorProducto(codigoProducto, null, "Fallo en el servicio de productos: " + t.getMessage());
        return FallbackConstants.PRODUCTO_FAKE;
    }

    @CircuitBreaker(name = "productosCB", fallbackMethod = "fallbackStockProducto")
    @Retry(name = "productosCB")
    public ResponseEntity<ProductoDTO> actualizarStockProducto(String codigo, Integer nuevoStock) {
        ResponseEntity<ProductoDTO> response = productoServiceClient.actualizarStock(codigo, nuevoStock);
        if (response.getStatusCode().value() == 403) {
            throw new RuntimeException("Acceso prohibido al servicio Productos");
        }
        return response;
    }

    public ResponseEntity<ProductoDTO> fallbackStockProducto(String codigoProducto, Throwable t) {
        log.info("Fallo al obtener producto {}: {}", codigoProducto, t.getMessage());

        publicarErrorProducto(codigoProducto, null, "Fallo al actualizar estock: " + t.getMessage());
        return ResponseEntity.ok(FallbackConstants.PRODUCTO_FAKE);
    }


    public void publicarErrorProducto(String codigo, Integer stock, String mensaje) {
        ErrorEvento evento = new ErrorEvento(codigo,  (stock != null) ? stock : 0, mensaje);
        kafkaTemplateProducto.send(topicoProducto, evento);
    }

}

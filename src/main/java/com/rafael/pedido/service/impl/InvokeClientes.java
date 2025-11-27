package com.rafael.pedido.service.impl;

import com.rafael.pedido.dtos.fakes.FallbackConstants;
import com.rafael.pedido.dtos.response.ClienteResponse;
import com.rafael.pedido.dtos.response.ProductoDTO;
import com.rafael.pedido.feign.ClienteServiceClient;
import com.rafael.pedido.feign.ProductoServiceClient;
import com.rafael.pedido.model.eventos.ErrorEvento;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvokeClientes {

    private final ClienteServiceClient clienteServiceClient;
    private final ProductoServiceClient productoServiceClient;
    private final KafkaTemplate<String, ErrorEvento> kafkaTemplate;

    @CircuitBreaker(name = "clientesCB", fallbackMethod = "fallbackCliente")
    @Retry(name = "clientesCB")
    public ClienteResponse obtenerDetalleCliente(Long clienteId) {
        ResponseEntity<ClienteResponse> response = clienteServiceClient.getClienteDetalle(clienteId);
        if (response.getStatusCode().value() == 403) {
            throw new RuntimeException("Acceso prohibido al servicio Productos");
        }             log.info("Respuesta del servicio de cliente: {}", response);
        return response.getBody();
    }

    // Fallbacks
    public ClienteResponse fallbackCliente(Long clienteId, Throwable t) {

        log.warn("Fallo al obtener cliente {}: {}", clienteId, t.getMessage());
        return FallbackConstants.CLIENTE_FAKE;
    }

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
        publicarError(codigoProducto, null, "Fallo en el servicio de productos: " + t.getMessage());
        return FallbackConstants.PRODUCTO_FAKE;
    }

    public void actualizarStockProducto(String codigo, Integer nuevoStock) {
       productoServiceClient.actualizarStock(codigo, nuevoStock);
    }

    public void publicarError(String codigo, Integer stock, String mensaje) {
        ErrorEvento evento = new ErrorEvento(codigo,  (stock != null) ? stock : 0, mensaje);
        kafkaTemplate.send("errores-producto", evento);
    }
}

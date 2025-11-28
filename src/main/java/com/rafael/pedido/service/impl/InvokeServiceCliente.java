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
public class InvokeServiceCliente {

    private final ClienteServiceClient clienteServiceClient;
    private final KafkaTemplate<String, ErrorEvento> kafkaTemplateProducto;
    private final KafkaTemplate<String, ErrorEventoCliente> kafkaTemplateClientes;

    // Inyecta el valor de la nueva propiedad
    @Value("${kafka.topicos.clientes}")
    private String topicoClientes;

    @CircuitBreaker(name = "clientesCB", fallbackMethod = "fallbackCliente")
    @Retry(name = "clientesCB")
    public ClienteResponse obtenerDetalleCliente(Long clienteId) {
        ResponseEntity<ClienteResponse> response = clienteServiceClient.getClienteDetalle(clienteId);
        if (response.getStatusCode().value() == 403) {
            throw new RuntimeException("Acceso prohibido al servicio Productos");
        }

        return response.getBody();
    }

    // Fallbacks
    public ClienteResponse fallbackCliente(Long clienteId, Throwable t) {

        log.warn("Fallo al obtener cliente {}: {}", clienteId, t.getMessage());
        publicarErrorCliente(clienteId, "Fallo en el servicio de clientes: " + t.getMessage());
        return FallbackConstants.CLIENTE_FAKE;
    }

    private void publicarErrorCliente(Long clienteId, String message) {
        ErrorEventoCliente evento = new ErrorEventoCliente(clienteId, message);
        kafkaTemplateClientes.send(topicoClientes, evento);
    }


}

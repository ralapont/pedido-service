package com.rafael.pedido.feign;

import com.rafael.pedido.config.FeignConfig;
import com.rafael.pedido.dtos.response.ClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service",
        url = "${cliente-service.url}",
        configuration = FeignConfig.class)
public interface ClienteServiceClient {

    @GetMapping("/api/clientes/{clienteId}")
    ResponseEntity<ClienteResponse> getClienteDetalle(@PathVariable("clienteId") Long  clienteId);
}

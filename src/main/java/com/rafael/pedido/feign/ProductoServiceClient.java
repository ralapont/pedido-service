package com.rafael.pedido.feign;

import com.rafael.pedido.config.FeignConfig;
import com.rafael.pedido.dtos.response.ClienteResponse;
import com.rafael.pedido.dtos.response.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "producto-service",
        url = "${producto-service.url}",
        configuration = FeignConfig.class)
public interface ProductoServiceClient {

    @GetMapping("/api/productos/codigo")
    ResponseEntity<ProductoDTO> getProductoPorCodigo( @RequestParam("codigo") String codigo );
}

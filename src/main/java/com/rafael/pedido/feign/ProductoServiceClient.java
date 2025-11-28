package com.rafael.pedido.feign;

import com.rafael.pedido.config.FeignConfig;
import com.rafael.pedido.dtos.response.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "producto-service",
        url = "${producto.service.url}",
        configuration = FeignConfig.class)
public interface ProductoServiceClient {

    @GetMapping("/api/productos/codigo")
    ResponseEntity<ProductoDTO> getProductoPorCodigo( @RequestParam("codigo") String codigo );

    @PutMapping("/api/productos/{codigo}/stock")
    ResponseEntity<ProductoDTO>  actualizarStock(@PathVariable("codigo") String codigo, @RequestParam("stock") Integer stock);
}

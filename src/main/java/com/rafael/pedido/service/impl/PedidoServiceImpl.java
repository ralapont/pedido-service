package com.rafael.pedido.service.impl;

import com.rafael.pedido.dtos.request.PedidoRequest;
import com.rafael.pedido.dtos.response.ClienteResponse;
import com.rafael.pedido.dtos.response.DetalleResponse;
import com.rafael.pedido.dtos.response.PedidoResponse;
import com.rafael.pedido.dtos.response.ProductoDTO;
import com.rafael.pedido.model.entity.Pedido;
import com.rafael.pedido.model.entity.PedidoProducto;
import com.rafael.pedido.model.repository.PedidoRepository;
import com.rafael.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class PedidoServiceImpl  implements PedidoService {

    private final PedidoRepository pedidoRepository;;
    private final InvokeServiceProducto invokeProducto;
    private final InvokeServiceCliente invokeClientes;

    @Override
    public PedidoResponse crear(PedidoRequest pedido) {
        log.info("Creando pedido para el cliente con ID: {}", pedido.getClienteId());

        List<DetalleResponse> detalles = pedido.getDetalle().stream()
                .map(detalle -> buildDetallePedidoReponse(detalle.getCodigo(), detalle.getCantidad()))
                .toList();

        ClienteResponse clienteResponse = invokeClientes.obtenerDetalleCliente(pedido.getClienteId());
        List<PedidoProducto> pedidoProductos = detalles.stream()
                .map(det -> PedidoProducto.builder()
                        .cantidad(det.getCantidad())
                        .precioUnitario(det.getPrecio().doubleValue())
                        .build())
                .toList();

        Double importeTotal = calcularImporteTotalPedido(detalles).doubleValue();
        Pedido pedidoEntity = Pedido.builder()
                .fecha(java.time.LocalDate.now())
                .ImporteTotal(importeTotal)
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedidoEntity);
        return buildPedidoResponse(Objects.requireNonNull(clienteResponse), pedidoGuardado.getId(), importeTotal, detalles);

    }

    private BigDecimal calcularImporteTotalPedido(List<DetalleResponse> detalles) {
        return detalles.stream()
                // 1. Inicializar el acumulador a CERO
                .reduce(
                        BigDecimal.ZERO,

                        // 2. Acumulador: Calcula el total de la línea y lo suma al acumulador
                        (acumulador, det) -> {
                            // Convertir la cantidad Long a BigDecimal para la multiplicación
                            BigDecimal cantidadDecimal = new BigDecimal(det.getCantidad());

                            // Calcular el total de la línea (Precio Unitario * Cantidad)
                            BigDecimal totalLinea = det.getPrecio().multiply(cantidadDecimal);

                            // Sumar el total de la línea al acumulador
                            return acumulador.add(totalLinea);
                        },

                        // 3. Combinador (solo necesario para Streams paralelos, se deja para completar la firma)
                        BigDecimal::add
                );
    }

    private DetalleResponse buildDetallePedidoReponse(String codigo, Long cantidad) {
        ProductoDTO productoDTO = invokeProducto.obtenerDetalleProducto(codigo);

        if (!productoDTO.getNombre().equals("Producto no disponible")) {
            if (productoDTO.getStock() >= cantidad) {
                // Actualizar el stock del producto
                Integer nuevoStock = productoDTO.getStock() - Math.toIntExact(cantidad);
                log.info("Actualizando stock del producto con código: {}. Nuevo stock: {}", codigo, nuevoStock);
                invokeProducto.actualizarStockProducto(codigo, nuevoStock);
            } else {
                invokeProducto.publicarErrorProducto(codigo, productoDTO.getStock(),
                        "Stock insuficiente para el producto con código: " + codigo);
                log.warn("Stock insuficiente para el producto con código: {}. Stock disponible: {}, Cantidad solicitada: {}",
                        codigo, productoDTO.getStock(), cantidad);
                throw new RuntimeException("Stock insuficiente para el producto con código: " + codigo);
            }
        }
        return DetalleResponse.builder()
                .id(Objects.requireNonNull(productoDTO).getId())
                .codigo(productoDTO.getCodigo())
                .nombre(productoDTO.getNombre())
                .precio(productoDTO.getPrecio())
                .stock(productoDTO.getStock())
                .cantidad(cantidad)
                .build();
    }

    private PedidoResponse buildPedidoResponse(ClienteResponse clienteResponse, Long id, Double importeTotal, List<DetalleResponse> detalle) {
        return PedidoResponse.builder()
                .id(id)
                .clienteId(clienteResponse.getId())
                .nombre(clienteResponse.getNombre())
                .apellidos(clienteResponse.getApellidos())
                .nif(clienteResponse.getNif())
                .telefono(clienteResponse.getTelefono())
                .email(clienteResponse.getEmail())
                .importeTotal(importeTotal)
                .detalle(detalle)
                .build();
    }


}

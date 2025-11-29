package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.ricardo.pedidos_service.domain.model.EstadoPedido;
import com.ricardo.pedidos_service.domain.model.Pedido;
import com.ricardo.pedidos_service.domain.ports.out.PedidoRepositoryPort;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.PedidoEntity;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository.SpringDataPedidoRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PedidoPersistenceAdapter implements PedidoRepositoryPort{
    private final SpringDataPedidoRepository jpaRepository;


    @Override
    public List<Pedido> guardarTodo(List<Pedido> pedidos){
        List<PedidoEntity> entidades = pedidos.stream()
        .map(this::mapearAEntidad)
        .collect(Collectors.toList());

        List<PedidoEntity> guardados = jpaRepository.saveAll(entidades);

        return guardados.stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }
    @Override
    public boolean existePorNumeroPedido(String numeroPedido) {
        return jpaRepository.existsByNumeroPedido(numeroPedido);
    }

    private PedidoEntity mapearAEntidad(Pedido pedido) {
        return PedidoEntity.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .clienteId(pedido.getClienteId())
                .zonaId(pedido.getZonaId())
                .fechaEntrega(pedido.getFechaEntrega())
                .estado(pedido.getEstado().name())
                .requiereRefrigeracion(pedido.isRequiereRefrigeracion())
                .build();
    }
    private Pedido mapearADominio(PedidoEntity entity) {
        return Pedido.builder()
                .id(entity.getId())
                .numeroPedido(entity.getNumeroPedido())
                .clienteId(entity.getClienteId())
                .zonaId(entity.getZonaId())
                .fechaEntrega(entity.getFechaEntrega())
                .estado(EstadoPedido.valueOf(entity.getEstado()))
                .requiereRefrigeracion(entity.isRequiereRefrigeracion())
                .build();
    }

}

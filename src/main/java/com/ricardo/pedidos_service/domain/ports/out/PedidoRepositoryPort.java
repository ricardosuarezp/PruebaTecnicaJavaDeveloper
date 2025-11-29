package com.ricardo.pedidos_service.domain.ports.out;

import java.util.List;

import com.ricardo.pedidos_service.domain.model.Pedido;

public interface PedidoRepositoryPort {
    List<Pedido> guardarTodo(List<Pedido> pedidos);

    boolean existePorNumeroPedido(String numeroPedido);
}

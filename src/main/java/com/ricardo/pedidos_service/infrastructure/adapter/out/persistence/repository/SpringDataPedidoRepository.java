package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.PedidoEntity;

public interface SpringDataPedidoRepository extends JpaRepository<PedidoEntity, UUID> {
    
    boolean existsByNumeroPedido(String numeroPedido);
}

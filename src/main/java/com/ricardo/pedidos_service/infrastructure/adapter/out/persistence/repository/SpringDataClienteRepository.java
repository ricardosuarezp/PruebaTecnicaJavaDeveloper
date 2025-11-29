package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.ClienteEntity;

@Repository
public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, String>{
    
}

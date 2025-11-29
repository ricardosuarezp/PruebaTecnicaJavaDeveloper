package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.ZonaEntity;

@Repository
public interface SpringDataZonaRepository extends JpaRepository<ZonaEntity, String>{
    
}

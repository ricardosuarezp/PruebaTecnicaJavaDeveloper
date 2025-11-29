package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.CargaIdempotenciaEntity;

public interface SpringDataIdempotenciaRepository extends JpaRepository<CargaIdempotenciaEntity, UUID> {

    Optional<CargaIdempotenciaEntity> findByIdempotencyKeyAndArchivoHash(String idempotencyKey, String archivoHash);
}

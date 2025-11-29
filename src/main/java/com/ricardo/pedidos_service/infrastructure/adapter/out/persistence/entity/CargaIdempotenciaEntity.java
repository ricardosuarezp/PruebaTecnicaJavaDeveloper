package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cargas_idempotencia")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CargaIdempotenciaEntity {
    @Id
    private UUID id;
    
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "archivo_hash", nullable = false)
    private String archivoHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

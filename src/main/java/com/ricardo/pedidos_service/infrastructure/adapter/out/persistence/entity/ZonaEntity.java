package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zonas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ZonaEntity {
    @Id
private String id;
@Column(name = "soporte_refrigeracion")
private boolean soporteRefrigeracion;
}

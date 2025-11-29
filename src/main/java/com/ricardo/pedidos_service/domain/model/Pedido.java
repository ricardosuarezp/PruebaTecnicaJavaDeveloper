package com.ricardo.pedidos_service.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // reemplaza a los getters y setters
@Builder
@NoArgsConstructor // reemplaza al constructor vacio
@AllArgsConstructor // reemplaza al contructor lleno

public class Pedido {
    private UUID id;
    private String numeroPedido;
    private String clienteId;
    private String zonaId;
    private LocalDate fechaEntrega;
    private EstadoPedido estado;
    private boolean requiereRefrigeracion;

    public boolean validarZona(Zona zona){
        if (this.requiereRefrigeracion && !zona.isSoporteRefrigeracion()) {
            return false;
        }
        return true;
    }
}

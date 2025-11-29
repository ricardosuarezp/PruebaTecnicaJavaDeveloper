package com.ricardo.pedidos_service.domain.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumenCarga {
    private int totalProcesados;
    private int totalGuardados;
    private int totalErrores;
    private List<String> errores;
}

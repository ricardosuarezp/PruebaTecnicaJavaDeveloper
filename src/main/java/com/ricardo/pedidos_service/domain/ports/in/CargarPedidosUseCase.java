package com.ricardo.pedidos_service.domain.ports.in;

import java.io.InputStream;

import com.ricardo.pedidos_service.domain.model.ResumenCarga;

public interface CargarPedidosUseCase {
    ResumenCarga procesarArchivo(InputStream archivoCsv);
}

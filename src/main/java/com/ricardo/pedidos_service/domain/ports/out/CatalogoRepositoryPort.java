package com.ricardo.pedidos_service.domain.ports.out;

import java.util.Optional;

import com.ricardo.pedidos_service.domain.model.Cliente;
import com.ricardo.pedidos_service.domain.model.Zona;

public interface CatalogoRepositoryPort {
    Optional<Cliente> buscarClientePorId(String id);

    Optional<Zona> buscarZonaPorId(String id);
}

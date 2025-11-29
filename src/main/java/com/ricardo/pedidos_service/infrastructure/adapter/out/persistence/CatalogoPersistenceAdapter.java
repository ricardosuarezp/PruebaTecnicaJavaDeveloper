package com.ricardo.pedidos_service.infrastructure.adapter.out.persistence;

import com.ricardo.pedidos_service.domain.model.Cliente;
import com.ricardo.pedidos_service.domain.model.Zona;
import com.ricardo.pedidos_service.domain.ports.out.CatalogoRepositoryPort;

import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository.SpringDataClienteRepository;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository.SpringDataZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CatalogoPersistenceAdapter implements CatalogoRepositoryPort {

    private final SpringDataClienteRepository clienteRepository;
    private final SpringDataZonaRepository zonaRepository;

    @Override
    public Optional<Cliente> buscarClientePorId(String id) {
        
        return clienteRepository.findById(id)
                .map(entity -> Cliente.builder()
                        .id(entity.getId())
                        .activo(entity.isActivo())
                        .build());
    }

    @Override
    public Optional<Zona> buscarZonaPorId(String id) {
        
        return zonaRepository.findById(id)
                .map(entity -> Zona.builder()
                        .id(entity.getId())
                        .soporteRefrigeracion(entity.isSoporteRefrigeracion())
                        .build());
    }
}
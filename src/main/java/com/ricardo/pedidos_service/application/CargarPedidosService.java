package com.ricardo.pedidos_service.application;

import com.ricardo.pedidos_service.domain.model.*;
import com.ricardo.pedidos_service.domain.ports.in.CargarPedidosUseCase;
import com.ricardo.pedidos_service.domain.ports.out.CatalogoRepositoryPort;
import com.ricardo.pedidos_service.domain.ports.out.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargarPedidosService implements CargarPedidosUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final CatalogoRepositoryPort catalogoRepositoryPort;

    private static final int BATCH_SIZE = 500;

    @Override
    public ResumenCarga procesarArchivo(InputStream archivoCsv) {
        List<Pedido> pedidosValidosBatch = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int totalProcesados = 0;
        int totalGuardados = 0;
        int numeroLinea = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivoCsv, StandardCharsets.UTF_8))) {
            String linea;
            
            while ((linea = br.readLine()) != null) {
                numeroLinea++;

                if (numeroLinea == 1 && linea.toLowerCase().startsWith("numeropedido")) {
                    continue; 
                }

                totalProcesados++;
                try {
                    Pedido pedidoCandidato = parsearLinea(linea);

                    validarReglasDeNegocio(pedidoCandidato);
                    
                    pedidosValidosBatch.add(pedidoCandidato);


                    if (pedidosValidosBatch.size() >= BATCH_SIZE) {
                        pedidoRepositoryPort.guardarTodo(pedidosValidosBatch);
                        totalGuardados += pedidosValidosBatch.size();
                        pedidosValidosBatch.clear();
                    }

                } catch (Exception e) {
                    errores.add("Línea " + numeroLinea + ": " + e.getMessage());
                }
            }


            if (!pedidosValidosBatch.isEmpty()) {
                pedidoRepositoryPort.guardarTodo(pedidosValidosBatch);
                totalGuardados += pedidosValidosBatch.size();
            }

        } catch (Exception e) {
            log.error("Error crítico leyendo el archivo", e);
            errores.add("Error general: " + e.getMessage());
        }

        return ResumenCarga.builder()
                .totalProcesados(totalProcesados)
                .totalGuardados(totalGuardados)
                .totalErrores(errores.size())
                .errores(errores)
                .build();
    }


    private Pedido parsearLinea(String linea) {
        String[] columnas = linea.split(",");

        if (columnas.length < 6) {
            throw new IllegalArgumentException("Formato CSV inválido, faltan columnas");
        }

        String numeroPedido = columnas[0].trim();
        String clienteId = columnas[1].trim();
        String fechaStr = columnas[2].trim();
        String estadoStr = columnas[3].trim();
        String zonaId = columnas[4].trim();
        boolean reqFrio = Boolean.parseBoolean(columnas[5].trim());

        LocalDate fechaEntrega = LocalDate.parse(fechaStr);
        EstadoPedido estado = EstadoPedido.valueOf(estadoStr.toUpperCase());

        return Pedido.builder()
                .id(UUID.randomUUID())
                .numeroPedido(numeroPedido)
                .clienteId(clienteId)
                .fechaEntrega(fechaEntrega)
                .estado(estado)
                .zonaId(zonaId)
                .requiereRefrigeracion(reqFrio)
                .build();
    }

    private void validarReglasDeNegocio(Pedido pedido) {
        if (pedidoRepositoryPort.existePorNumeroPedido(pedido.getNumeroPedido())) {
            throw new IllegalArgumentException("Pedido DUPLICADO " + pedido.getNumeroPedido());
        }

        LocalDate hoyEnPeru = LocalDate.now(ZoneId.of("America/Lima"));
        if (pedido.getFechaEntrega().isBefore(hoyEnPeru)) {
            throw new IllegalArgumentException("Fecha inválida: La fecha es pasada");
        }

        Zona zona = catalogoRepositoryPort.buscarZonaPorId(pedido.getZonaId())
                .orElseThrow(() -> new IllegalArgumentException("ZONA INVÁLIDA " + pedido.getZonaId()));

        if (!pedido.validarZona(zona)) {
            throw new IllegalArgumentException("CADENA DE FRÍO NO SOPORTADA: Zona " + zona.getId() + " no soporta refrigeración");
        }
    }

}
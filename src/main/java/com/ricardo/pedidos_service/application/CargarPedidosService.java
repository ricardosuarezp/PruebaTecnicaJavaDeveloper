package com.ricardo.pedidos_service.application;

import com.ricardo.pedidos_service.domain.model.*;
import com.ricardo.pedidos_service.domain.ports.in.CargarPedidosUseCase;
import com.ricardo.pedidos_service.domain.ports.out.CatalogoRepositoryPort;
import com.ricardo.pedidos_service.domain.ports.out.PedidoRepositoryPort;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.CargaIdempotenciaEntity;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository.SpringDataIdempotenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargarPedidosService implements CargarPedidosUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final CatalogoRepositoryPort catalogoRepositoryPort;
    private final SpringDataIdempotenciaRepository idempotenciaRepository;

    private static final int BATCH_SIZE = 500;

    @Override
    public ResumenCarga procesarArchivo(InputStream archivoCsv, String idempotencyKey) {
        try {

            byte[] contenidoBytes = archivoCsv.readAllBytes();

            String hash = calcularHash(contenidoBytes);


            if (idempotenciaRepository.findByIdempotencyKeyAndArchivoHash(idempotencyKey, hash).isPresent()) {
                log.warn("Carga duplicada detectada. Key: {}, Hash: {}", idempotencyKey, hash);
                return ResumenCarga.builder()
                        .totalProcesados(0)
                        .totalGuardados(0)
                        .totalErrores(1)
                        .errores(
                                List.of("DUPLICADO: Este archivo ya fue procesado con la misma llave de idempotencia."))
                        .build();
            }

            idempotenciaRepository.save(CargaIdempotenciaEntity.builder()
                    .id(UUID.randomUUID())
                    .idempotencyKey(idempotencyKey)
                    .archivoHash(hash)
                    .createdAt(LocalDateTime.now())
                    .build());

            return procesarLineas(new ByteArrayInputStream(contenidoBytes));

        } catch (Exception e) {
            log.error("Error procesando archivo", e);
            return ResumenCarga.builder().errores(List.of("Error crítico: " + e.getMessage())).build();
        }
    }


    private String calcularHash(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(bytes);
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private ResumenCarga procesarLineas(InputStream inputStream) {


        List<Pedido> pedidosValidosBatch = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int totalProcesados = 0;
        int totalGuardados = 0;
        int numeroLinea = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (numeroLinea == 1 && linea.toLowerCase().startsWith("numeropedido"))
                    continue;

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
            errores.add("Error leyendo líneas: " + e.getMessage());
        }

        return ResumenCarga.builder()
                .totalProcesados(totalProcesados)
                .totalGuardados(totalGuardados)
                .totalErrores(errores.size())
                .errores(errores)
                .build();
    }

    // ... MANTÉN TUS MÉTODOS parsearLinea Y validarReglasDeNegocio IGUAL QUE ANTES
    // ...
    private Pedido parsearLinea(String linea) {
        String[] columnas = linea.split(",");
        if (columnas.length < 6)
            throw new IllegalArgumentException("Formato CSV inválido, faltan columnas");
        // ... resto de tu lógica de parseo ...
        String numeroPedido = columnas[0].trim();
        String clienteId = columnas[1].trim();
        String fechaStr = columnas[2].trim();
        String estadoStr = columnas[3].trim();
        String zonaId = columnas[4].trim();
        boolean reqFrio = Boolean.parseBoolean(columnas[5].trim());

        return Pedido.builder()
                .id(UUID.randomUUID())
                .numeroPedido(numeroPedido)
                .clienteId(clienteId)
                .fechaEntrega(LocalDate.parse(fechaStr))
                .estado(EstadoPedido.valueOf(estadoStr.toUpperCase()))
                .zonaId(zonaId)
                .requiereRefrigeracion(reqFrio)
                .build();
    }

    private void validarReglasDeNegocio(Pedido pedido) {
        // ... tu lógica original de validación ...
        if (pedidoRepositoryPort.existePorNumeroPedido(pedido.getNumeroPedido())) {
            throw new IllegalArgumentException("Pedido DUPLICADO " + pedido.getNumeroPedido());
        }
        // ... resto de validaciones ...
        LocalDate hoyEnPeru = LocalDate.now(ZoneId.of("America/Lima"));
        if (pedido.getFechaEntrega().isBefore(hoyEnPeru))
            throw new IllegalArgumentException("Fecha inválida: La fecha es pasada");

        Zona zona = catalogoRepositoryPort.buscarZonaPorId(pedido.getZonaId())
                .orElseThrow(() -> new IllegalArgumentException("ZONA INVÁLIDA " + pedido.getZonaId()));

        if (!pedido.validarZona(zona))
            throw new IllegalArgumentException("CADENA DE FRÍO NO SOPORTADA");
    }
}
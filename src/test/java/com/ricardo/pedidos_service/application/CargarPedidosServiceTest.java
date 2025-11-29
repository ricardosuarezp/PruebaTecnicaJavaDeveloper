package com.ricardo.pedidos_service.application;

import com.ricardo.pedidos_service.domain.model.*;
import com.ricardo.pedidos_service.domain.ports.out.CatalogoRepositoryPort;
import com.ricardo.pedidos_service.domain.ports.out.PedidoRepositoryPort;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.entity.CargaIdempotenciaEntity;
import com.ricardo.pedidos_service.infrastructure.adapter.out.persistence.repository.SpringDataIdempotenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CargarPedidosServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private CatalogoRepositoryPort catalogoRepository;

    @Mock
    private SpringDataIdempotenciaRepository idempotenciaRepository;

    @InjectMocks
    private CargarPedidosService service;

    private Cliente clienteMock;
    private Zona zonaMock;

    @BeforeEach
    void setUp() {
        clienteMock = Cliente.builder().id("CLI-001").activo(true).build();
        zonaMock = Zona.builder().id("ZONA-1").soporteRefrigeracion(true).build();
    }

    @Test
    void deberiaProcesarArchivoCorrectamente() {
        String csvContent = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                            "P001,CLI-001,2025-12-01,PENDIENTE,ZONA-1,true";
        InputStream fileStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        String idempotencyKey = "test-key-1";

        when(idempotenciaRepository.findByIdempotencyKeyAndArchivoHash(anyString(), anyString()))
                .thenReturn(Optional.empty()); 
        
        when(pedidoRepository.existePorNumeroPedido("P001")).thenReturn(false);
        
        when(catalogoRepository.buscarClientePorId("CLI-001")).thenReturn(Optional.of(clienteMock));
        when(catalogoRepository.buscarZonaPorId("ZONA-1")).thenReturn(Optional.of(zonaMock));

        ResumenCarga resultado = service.procesarArchivo(fileStream, idempotencyKey);


        if (resultado.getTotalErrores() > 0) {
            System.out.println(" ERRORES EN EL TEST: " + resultado.getErrores());
        }

        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalErrores(), "No debería haber errores");
        assertEquals(1, resultado.getTotalProcesados(), "Debería procesar 1 línea");
        assertEquals(1, resultado.getTotalGuardados(), "Debería guardar 1 pedido");

        verify(pedidoRepository, times(1)).guardarTodo(any());
        verify(idempotenciaRepository, times(1)).save(any(CargaIdempotenciaEntity.class));
    }
}
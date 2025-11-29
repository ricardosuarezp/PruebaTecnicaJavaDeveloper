package com.ricardo.pedidos_service.infrastructure.adapter.in.web;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;


import com.ricardo.pedidos_service.domain.model.ResumenCarga;
import com.ricardo.pedidos_service.domain.ports.in.CargarPedidosUseCase;

import java.io.IOException; 

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {


    private final CargarPedidosUseCase cargarPedidosUseCase;

    @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumenCarga> cargarPedidos(
        @RequestParam("file") MultipartFile file,
        @RequestHeader("idempotency-Key") String idempotencyKey)
        {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ResumenCarga resumen = cargarPedidosUseCase.procesarArchivo(file.getInputStream(), idempotencyKey);
            return ResponseEntity.ok(resumen);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
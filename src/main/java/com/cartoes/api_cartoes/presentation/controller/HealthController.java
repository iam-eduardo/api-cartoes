package com.cartoes.api_cartoes.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health Controller", description = "API para verificação da saúde da aplicação")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @Operation(summary = "Verificar saúde da aplicação", description = "Retorna status da aplicação e seus componentes")
    public ResponseEntity<HealthComponent> verificarSaude() {
        HealthComponent health = healthEndpoint.health();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/liveness")
    @Operation(summary = "Verificar se a aplicação está viva", description = "Retorna status de vida da aplicação")
    public ResponseEntity<Health> verificarLiveness() {
        return ResponseEntity.ok(Health.up().build());
    }

    @GetMapping("/readiness")
    @Operation(summary = "Verificar se a aplicação está pronta", description = "Retorna status de prontidão da aplicação")
    public ResponseEntity<Health> verificarReadiness() {
        // Podemos adicionar lógica específica para verificar a prontidão
        // como conexão com banco de dados, serviços externos, etc.
        return ResponseEntity.ok(Health.up().build());
    }
}
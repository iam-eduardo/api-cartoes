package com.cartoes.api_cartoes.infrastructure.repository;

import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.repository.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {

    private final RestTemplate restTemplate;

    @Value("${api.cliente.url}")
    private String apiClienteUrl;

    @Override
    @CircuitBreaker(name = "clienteService", fallbackMethod = "registrarClienteFallback")
    @Retry(name = "clienteService")
    public UUID registrarCliente(Cliente cliente) {
        log.info("Simulando registro de cliente: {}", cliente.getCpf());
        return registrarClienteFallback(cliente, new RuntimeException("Simulação"));
//        log.info("Registrando cliente na API externa: {}", cliente.getCpf());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("cliente", cliente);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(apiClienteUrl, request, Map.class);
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                String idCliente = (String) response.getBody().get("id_cliente");
//                log.info("Cliente registrado com sucesso. ID: {}", idCliente);
//                return UUID.fromString(idCliente);
//            } else {
//                log.error("Falha ao registrar cliente. Resposta: {}", response);
//                throw new BusinessException("Falha ao registrar cliente na API externa");
//            }
//        } catch (Exception e) {
//            log.error("Erro ao registrar cliente na API externa", e);
//            throw new BusinessException("Erro ao comunicar com a API de clientes: " + e.getMessage());
//        }
    }

    private UUID registrarClienteFallback(Cliente cliente, Exception e) {
        log.warn("Utilizando fallback para registro de cliente devido a: {}", e.getMessage());
        UUID fallbackId = UUID.randomUUID();
        log.info("ID fallback gerado: {}", fallbackId);
        return fallbackId;
    }
}

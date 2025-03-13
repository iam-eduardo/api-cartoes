package com.cartoes.api_cartoes.application.dto.request;

import com.cartoes.api_cartoes.application.dto.ClienteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    private ClienteDTO cliente;
}
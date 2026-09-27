package com.desafio.credito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditoConsultaEvent {

    private String eventId;
    private String tipoConsulta; // "NFSE" ou "NUMERO_CREDITO"
    private String parametroConsulta;
    private int quantidadeResultados;
    private LocalDateTime timestamp;
    private String status; // "SUCESSO" ou "NAO_ENCONTRADO"
}

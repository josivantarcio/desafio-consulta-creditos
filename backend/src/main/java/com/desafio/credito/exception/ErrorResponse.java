package com.desafio.credito.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta padronizada de erro")
public class ErrorResponse {

    @Schema(description = "Código de status HTTP", example = "404")
    private int status;

    @Schema(description = "Descrição do status HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Crédito com o número 999999 não foi encontrado.")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/api/creditos/credito/999999")
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Momento da ocorrência do erro")
    private LocalDateTime timestamp;
}

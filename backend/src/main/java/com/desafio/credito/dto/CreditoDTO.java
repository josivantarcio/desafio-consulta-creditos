package com.desafio.credito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representação detalhada de um crédito constituído")
public class CreditoDTO {

    @Schema(description = "Número identificador do crédito constituído", example = "123456")
    private String numeroCredito;

    @Schema(description = "Número da NFS-e vinculada", example = "7891011")
    private String numeroNfse;

    @Schema(description = "Data de constituição do crédito (AAAA-MM-DD)", example = "2024-02-25")
    private LocalDate dataConstituicao;

    @Schema(description = "Valor do ISSQN constituído", example = "1500.75")
    private BigDecimal valorIssqn;

    @Schema(description = "Tipo do crédito constituído", example = "ISSQN")
    private String tipoCredito;

    @Schema(description = "Indicador de optante pelo Simples Nacional ('Sim' ou 'Não')", example = "Sim")
    private String simplesNacional;

    @Schema(description = "Alíquota aplicada em percentual", example = "5.0")
    private BigDecimal aliquota;

    @Schema(description = "Valor total faturado", example = "30000.00")
    private BigDecimal valorFaturado;

    @Schema(description = "Valor das deduções legais", example = "5000.00")
    private BigDecimal valorDeducao;

    @Schema(description = "Base de cálculo apurada", example = "25000.00")
    private BigDecimal baseCalculo;
}

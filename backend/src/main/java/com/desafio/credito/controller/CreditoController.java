package com.desafio.credito.controller;

import com.desafio.credito.dto.CreditoDTO;
import com.desafio.credito.exception.ErrorResponse;
import com.desafio.credito.service.CreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/creditos")
@RequiredArgsConstructor
@Tag(name = "Créditos Constituídos", description = "Operações para consulta e detalhamento de créditos constituídos")
public class CreditoController {

    private final CreditoService creditoService;

    @Operation(
            summary = "Consulta créditos constituídos por número da NFS-e",
            description = "Retorna uma lista de créditos constituídos associados ao número da NFS-e informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de créditos recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CreditoDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{numeroNfse}")
    public ResponseEntity<List<CreditoDTO>> buscarPorNumeroNfse(
            @Parameter(description = "Número identificador da NFS-e", example = "7891011", required = true)
            @PathVariable String numeroNfse) {
        List<CreditoDTO> creditos = creditoService.buscarPorNumeroNfse(numeroNfse);
        return ResponseEntity.ok(creditos);
    }

    @Operation(
            summary = "Consulta crédito constituído por número do crédito",
            description = "Retorna os detalhes de um crédito constituído específico com base no seu número identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalhes do crédito constituído recuperados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditoDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Crédito não encontrado para o número informado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/credito/{numeroCredito}")
    public ResponseEntity<CreditoDTO> buscarPorNumeroCredito(
            @Parameter(description = "Número identificador do crédito constituído", example = "123456", required = true)
            @PathVariable String numeroCredito) {
        CreditoDTO credito = creditoService.buscarPorNumeroCredito(numeroCredito);
        return ResponseEntity.ok(credito);
    }
}

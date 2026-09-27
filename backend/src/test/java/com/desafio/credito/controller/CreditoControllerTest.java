package com.desafio.credito.controller;

import com.desafio.credito.dto.CreditoDTO;
import com.desafio.credito.exception.ResourceNotFoundException;
import com.desafio.credito.service.CreditoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditoController.class)
class CreditoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoService creditoService;

    @Test
    @DisplayName("GET /api/creditos/{numeroNfse} deve retornar status 200 e lista de créditos")
    void buscarPorNumeroNfse_DeveRetornarStatus200ELista() throws Exception {
        CreditoDTO dto1 = CreditoDTO.builder()
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional("Sim")
                .aliquota(new BigDecimal("5.0"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build();

        CreditoDTO dto2 = CreditoDTO.builder()
                .numeroCredito("789012")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 26))
                .valorIssqn(new BigDecimal("1200.50"))
                .tipoCredito("ISSQN")
                .simplesNacional("Não")
                .aliquota(new BigDecimal("4.5"))
                .valorFaturado(new BigDecimal("25000.00"))
                .valorDeducao(new BigDecimal("4000.00"))
                .baseCalculo(new BigDecimal("21000.00"))
                .build();

        when(creditoService.buscarPorNumeroNfse("7891011")).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/creditos/7891011")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroCredito", is("123456")))
                .andExpect(jsonPath("$[0].simplesNacional", is("Sim")))
                .andExpect(jsonPath("$[1].numeroCredito", is("789012")))
                .andExpect(jsonPath("$[1].simplesNacional", is("Não")));
    }

    @Test
    @DisplayName("GET /api/creditos/credito/{numeroCredito} deve retornar 200 e detalhes do crédito")
    void buscarPorNumeroCredito_QuandoEncontrado_DeveRetornar200() throws Exception {
        CreditoDTO dto = CreditoDTO.builder()
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional("Sim")
                .aliquota(new BigDecimal("5.0"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build();

        when(creditoService.buscarPorNumeroCredito("123456")).thenReturn(dto);

        mockMvc.perform(get("/api/creditos/credito/123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCredito", is("123456")))
                .andExpect(jsonPath("$.numeroNfse", is("7891011")))
                .andExpect(jsonPath("$.valorIssqn", is(1500.75)))
                .andExpect(jsonPath("$.simplesNacional", is("Sim")));
    }

    @Test
    @DisplayName("GET /api/creditos/credito/{numeroCredito} deve retornar 404 quando crédito não existe")
    void buscarPorNumeroCredito_QuandoNaoEncontrado_DeveRetornar404() throws Exception {
        when(creditoService.buscarPorNumeroCredito("999999"))
                .thenThrow(new ResourceNotFoundException("Crédito constituído com o número '999999' não foi encontrado."));

        mockMvc.perform(get("/api/creditos/credito/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Crédito constituído com o número '999999' não foi encontrado.")))
                .andExpect(jsonPath("$.path", is("/api/creditos/credito/999999")));
    }
}

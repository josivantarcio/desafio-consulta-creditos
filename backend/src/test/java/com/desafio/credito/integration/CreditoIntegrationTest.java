package com.desafio.credito.integration;

import com.desafio.credito.domain.entity.Credito;
import com.desafio.credito.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CreditoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreditoRepository creditoRepository;

    @BeforeEach
    void setUp() {
        creditoRepository.deleteAll();

        creditoRepository.save(Credito.builder()
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional(true)
                .aliquota(new BigDecimal("5.00"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build());

        creditoRepository.save(Credito.builder()
                .numeroCredito("789012")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 26))
                .valorIssqn(new BigDecimal("1200.50"))
                .tipoCredito("ISSQN")
                .simplesNacional(false)
                .aliquota(new BigDecimal("4.50"))
                .valorFaturado(new BigDecimal("25000.00"))
                .valorDeducao(new BigDecimal("4000.00"))
                .baseCalculo(new BigDecimal("21000.00"))
                .build());
    }

    @Test
    @DisplayName("Integração: Busca por NFS-e deve retornar lista completa do banco")
    void buscarPorNfse_DeveRetornarRegistrosPersistidos() throws Exception {
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
    @DisplayName("Integração: Busca por Número de Crédito deve retornar o registro correto")
    void buscarPorNumeroCredito_DeveRetornarRegistroPersistido() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCredito", is("123456")))
                .andExpect(jsonPath("$.numeroNfse", is("7891011")))
                .andExpect(jsonPath("$.valorIssqn", is(1500.75)))
                .andExpect(jsonPath("$.simplesNacional", is("Sim")));
    }

    @Test
    @DisplayName("Integração: Busca por Número de Crédito inexistente deve retornar 404")
    void buscarPorNumeroCredito_Inexistente_DeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}

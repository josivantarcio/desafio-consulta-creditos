package com.desafio.credito.service;

import com.desafio.credito.domain.entity.Credito;
import com.desafio.credito.dto.CreditoDTO;
import com.desafio.credito.exception.ResourceNotFoundException;
import com.desafio.credito.mapper.CreditoMapper;
import com.desafio.credito.messaging.KafkaAuditProducer;
import com.desafio.credito.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditoServiceTest {

    @Mock
    private CreditoRepository creditoRepository;

    @Mock
    private CreditoMapper creditoMapper;

    @Mock
    private KafkaAuditProducer kafkaAuditProducer;

    @InjectMocks
    private CreditoService creditoService;

    private Credito creditoMock;
    private CreditoDTO creditoDTOMock;

    @BeforeEach
    void setUp() {
        creditoMock = Credito.builder()
                .id(1L)
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional(true)
                .aliquota(new BigDecimal("5.0"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build();

        creditoDTOMock = CreditoDTO.builder()
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
    }

    @Test
    @DisplayName("Deve retornar lista de créditos constituídos e publicar auditoria quando buscar por NFS-e existente")
    void buscarPorNumeroNfse_DeveRetornarListaDeCreditos_EPublicarAuditoria() {
        // Arrange
        String nfse = "7891011";
        List<Credito> creditos = List.of(creditoMock);
        List<CreditoDTO> dtos = List.of(creditoDTOMock);

        when(creditoRepository.findByNumeroNfse(nfse)).thenReturn(creditos);
        when(creditoMapper.toDTOList(creditos)).thenReturn(dtos);

        // Act
        List<CreditoDTO> resultado = creditoService.buscarPorNumeroNfse(nfse);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroCredito()).isEqualTo("123456");
        assertThat(resultado.get(0).getSimplesNacional()).isEqualTo("Sim");

        verify(creditoRepository).findByNumeroNfse(nfse);
        verify(creditoMapper).toDTOList(creditos);
        verify(kafkaAuditProducer).publicarEventoAuditoria(eq("NFSE"), eq(nfse), eq(1), eq("SUCESSO"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia e publicar auditoria quando buscar por NFS-e inexistente")
    void buscarPorNumeroNfse_QuandoNaoEncontrado_DeveRetornarListaVazia() {
        // Arrange
        String nfse = "0000000";
        when(creditoRepository.findByNumeroNfse(nfse)).thenReturn(Collections.emptyList());
        when(creditoMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<CreditoDTO> resultado = creditoService.buscarPorNumeroNfse(nfse);

        // Assert
        assertThat(resultado).isEmpty();
        verify(kafkaAuditProducer).publicarEventoAuditoria(eq("NFSE"), eq(nfse), eq(0), eq("SUCESSO"));
    }

    @Test
    @DisplayName("Deve retornar crédito e publicar auditoria com sucesso quando buscar por número do crédito existente")
    void buscarPorNumeroCredito_QuandoEncontrado_DeveRetornarDTO() {
        // Arrange
        String numeroCredito = "123456";
        when(creditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.of(creditoMock));
        when(creditoMapper.toDTO(creditoMock)).thenReturn(creditoDTOMock);

        // Act
        CreditoDTO resultado = creditoService.buscarPorNumeroCredito(numeroCredito);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCredito()).isEqualTo("123456");
        assertThat(resultado.getValorIssqn()).isEqualByComparingTo(new BigDecimal("1500.75"));

        verify(creditoRepository).findByNumeroCredito(numeroCredito);
        verify(kafkaAuditProducer).publicarEventoAuditoria(eq("NUMERO_CREDITO"), eq(numeroCredito), eq(1), eq("SUCESSO"));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException e publicar auditoria de não encontrado")
    void buscarPorNumeroCredito_QuandoNaoEncontrado_DeveLancarExcecao() {
        // Arrange
        String numeroCredito = "999999";
        when(creditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> creditoService.buscarPorNumeroCredito(numeroCredito))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999999");

        verify(creditoRepository).findByNumeroCredito(numeroCredito);
        verify(kafkaAuditProducer).publicarEventoAuditoria(eq("NUMERO_CREDITO"), eq(numeroCredito), eq(0), eq("NAO_ENCONTRADO"));
    }
}

package com.desafio.credito.mapper;

import com.desafio.credito.domain.entity.Credito;
import com.desafio.credito.dto.CreditoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreditoMapperTest {

    private CreditoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreditoMapper();
    }

    @Test
    @DisplayName("Deve mapear Credito para CreditoDTO formatando Simples Nacional como 'Sim'")
    void toDTO_QuandoOptanteSimples_DeveMapearSim() {
        Credito entity = Credito.builder()
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
                .build();

        CreditoDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getNumeroCredito()).isEqualTo("123456");
        assertThat(dto.getNumeroNfse()).isEqualTo("7891011");
        assertThat(dto.getDataConstituicao()).isEqualTo(LocalDate.of(2024, 2, 25));
        assertThat(dto.getValorIssqn()).isEqualByComparingTo(new BigDecimal("1500.75"));
        assertThat(dto.getTipoCredito()).isEqualTo("ISSQN");
        assertThat(dto.getSimplesNacional()).isEqualTo("Sim");
        assertThat(dto.getAliquota()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(dto.getValorFaturado()).isEqualByComparingTo(new BigDecimal("30000.00"));
        assertThat(dto.getValorDeducao()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(dto.getBaseCalculo()).isEqualByComparingTo(new BigDecimal("25000.00"));
    }

    @Test
    @DisplayName("Deve mapear Credito para CreditoDTO formatando Simples Nacional como 'Não'")
    void toDTO_QuandoNaoOptanteSimples_DeveMapearNao() {
        Credito entity = Credito.builder()
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
                .build();

        CreditoDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getSimplesNacional()).isEqualTo("Não");
    }

    @Test
    @DisplayName("Deve mapear lista de entidades para lista de DTOs")
    void toDTOList_DeveMapearListaCompleta() {
        Credito entity1 = Credito.builder().numeroCredito("1").simplesNacional(true).build();
        Credito entity2 = Credito.builder().numeroCredito("2").simplesNacional(false).build();

        List<CreditoDTO> dtoList = mapper.toDTOList(List.of(entity1, entity2));

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getNumeroCredito()).isEqualTo("1");
        assertThat(dtoList.get(0).getSimplesNacional()).isEqualTo("Sim");
        assertThat(dtoList.get(1).getNumeroCredito()).isEqualTo("2");
        assertThat(dtoList.get(1).getSimplesNacional()).isEqualTo("Não");
    }
}

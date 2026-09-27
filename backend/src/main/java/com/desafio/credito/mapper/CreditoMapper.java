package com.desafio.credito.mapper;

import com.desafio.credito.domain.entity.Credito;
import com.desafio.credito.dto.CreditoDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditoMapper {

    public CreditoDTO toDTO(Credito entity) {
        if (entity == null) {
            return null;
        }

        return CreditoDTO.builder()
                .numeroCredito(entity.getNumeroCredito())
                .numeroNfse(entity.getNumeroNfse())
                .dataConstituicao(entity.getDataConstituicao())
                .valorIssqn(entity.getValorIssqn())
                .tipoCredito(entity.getTipoCredito())
                .simplesNacional(entity.isSimplesNacional() ? "Sim" : "Não")
                .aliquota(entity.getAliquota())
                .valorFaturado(entity.getValorFaturado())
                .valorDeducao(entity.getValorDeducao())
                .baseCalculo(entity.getBaseCalculo())
                .build();
    }

    public List<CreditoDTO> toDTOList(List<Credito> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

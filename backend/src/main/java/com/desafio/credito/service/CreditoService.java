package com.desafio.credito.service;

import com.desafio.credito.domain.entity.Credito;
import com.desafio.credito.dto.CreditoDTO;
import com.desafio.credito.exception.ResourceNotFoundException;
import com.desafio.credito.mapper.CreditoMapper;
import com.desafio.credito.messaging.KafkaAuditProducer;
import com.desafio.credito.repository.CreditoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditoService {

    private final CreditoRepository creditoRepository;
    private final CreditoMapper creditoMapper;
    private final KafkaAuditProducer kafkaAuditProducer;

    @Transactional(readOnly = true)
    public List<CreditoDTO> buscarPorNumeroNfse(String numeroNfse) {
        log.info("Buscando créditos constituídos associados à NFS-e: {}", numeroNfse);
        List<Credito> creditos = creditoRepository.findByNumeroNfse(numeroNfse);

        kafkaAuditProducer.publicarEventoAuditoria("NFSE", numeroNfse, creditos.size(), "SUCESSO");

        return creditoMapper.toDTOList(creditos);
    }

    @Transactional(readOnly = true)
    public CreditoDTO buscarPorNumeroCredito(String numeroCredito) {
        log.info("Buscando crédito constituído pelo número de crédito: {}", numeroCredito);
        Credito credito = creditoRepository.findByNumeroCredito(numeroCredito)
                .orElseThrow(() -> {
                    kafkaAuditProducer.publicarEventoAuditoria("NUMERO_CREDITO", numeroCredito, 0, "NAO_ENCONTRADO");
                    return new ResourceNotFoundException("Crédito constituído com o número '" + numeroCredito + "' não foi encontrado.");
                });

        kafkaAuditProducer.publicarEventoAuditoria("NUMERO_CREDITO", numeroCredito, 1, "SUCESSO");

        return creditoMapper.toDTO(credito);
    }
}

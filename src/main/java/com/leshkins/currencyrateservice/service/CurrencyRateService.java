package com.leshkins.currencyrateservice.service;

import com.leshkins.currencyrateservice.client.CurrencyApiClient;
import com.leshkins.currencyrateservice.dto.CurrencyRatesResponseDTO;
import com.leshkins.currencyrateservice.model.CurrencyRate;
import com.leshkins.currencyrateservice.model.CurrencyType;
import com.leshkins.currencyrateservice.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private final CurrencyApiClient currencyApiClient;
    private final CurrencyRateRepository currencyRateRepository;

    public Mono<CurrencyRatesResponseDTO> getCurrencyRates() {
        Flux<CurrencyRate> fiatRates = currencyApiClient.fetchFiatRates()
                .map(dto -> CurrencyRate.builder()
                        .currency(dto.getCurrency())
                        .rate(dto.getRate())
                        .type(CurrencyType.FIAT)
                        .lastUpdated(LocalDateTime.now())
                        .build())
                .flatMap(rate -> currencyRateRepository.findByCurrencyAndType(rate.getCurrency(), CurrencyType.FIAT)
                        .flatMap(existingRate -> {
                            existingRate.setRate(rate.getRate());
                            existingRate.setLastUpdated(rate.getLastUpdated());
                            return currencyRateRepository.save(existingRate);
                        })
                        .switchIfEmpty(currencyRateRepository.save(rate)))
                .switchIfEmpty(currencyRateRepository.findAllByType(CurrencyType.FIAT));

        Flux<CurrencyRate> cryptoRates = currencyApiClient.fetchCryptoRates()
                .map(dto -> CurrencyRate.builder()
                        .currency(dto.getName())
                        .rate(dto.getValue())
                        .type(CurrencyType.CRYPTO)
                        .lastUpdated(LocalDateTime.now())
                        .build())
                .flatMap(rate -> currencyRateRepository.findByCurrencyAndType(rate.getCurrency(), CurrencyType.CRYPTO)
                        .flatMap(existingRate -> {
                            existingRate.setRate(rate.getRate());
                            existingRate.setLastUpdated(rate.getLastUpdated());
                            return currencyRateRepository.save(existingRate);
                        })
                        .switchIfEmpty(currencyRateRepository.save(rate)))
                .switchIfEmpty(currencyRateRepository.findAllByType(CurrencyType.CRYPTO));

        return Mono.zip(
                fiatRates.collectList(),
                cryptoRates.collectList(),
                (fiatList, cryptoList) -> new CurrencyRatesResponseDTO(
                        mapToResponse(fiatList),
                        mapToResponse(cryptoList)
                )
        );
    }


    private List<CurrencyRatesResponseDTO.CurrencyRate> mapToResponse(List<CurrencyRate> rates) {
        return rates.stream()
                .map(rate -> new CurrencyRatesResponseDTO.CurrencyRate(rate.getCurrency(), rate.getRate()))
                .collect(Collectors.toList());
    }

}





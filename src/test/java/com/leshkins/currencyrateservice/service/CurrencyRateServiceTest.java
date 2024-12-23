package com.leshkins.currencyrateservice.service;

import com.leshkins.currencyrateservice.client.CurrencyApiClient;
import com.leshkins.currencyrateservice.dto.CurrencyRatesResponseDTO;
import com.leshkins.currencyrateservice.dto.currency.CryptoCurrencyRateDTO;
import com.leshkins.currencyrateservice.dto.currency.FiatCurrencyRateDTO;
import com.leshkins.currencyrateservice.model.CurrencyRate;
import com.leshkins.currencyrateservice.model.CurrencyType;
import com.leshkins.currencyrateservice.repository.CurrencyRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private CurrencyApiClient currencyApiClient;

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    @Test
    void testGetCurrencyRates_FiatAndCryptoRates() {
        // Prepare mock data for Fiat and Crypto rates
        FiatCurrencyRateDTO fiatRateDTO = new FiatCurrencyRateDTO("USD", BigDecimal.valueOf(1.2));
        CryptoCurrencyRateDTO cryptoRateDTO = new CryptoCurrencyRateDTO("BTC", BigDecimal.valueOf(35000));

        // Mock the currencyApiClient to return the DTOs
        when(currencyApiClient.fetchFiatRates()).thenReturn(Flux.just(fiatRateDTO));
        when(currencyApiClient.fetchCryptoRates()).thenReturn(Flux.just(cryptoRateDTO));

        // Mock the repository save() method to return saved currency
        when(currencyRateRepository.save(any(CurrencyRate.class)))
                .thenReturn(Mono.just(new CurrencyRate()));

        // Mock findByCurrencyAndType to return Mono.empty() for non-existent rates
        when(currencyRateRepository.findByCurrencyAndType(eq("USD"), eq(CurrencyType.FIAT)))
                .thenReturn(Mono.empty());  // No existing rate for USD fiat
        when(currencyRateRepository.findByCurrencyAndType(eq("BTC"), eq(CurrencyType.CRYPTO)))
                .thenReturn(Mono.empty());  // No existing rate for BTC crypto

        // Mock findAllByType to return an empty Flux (simulate no previous data)
        when(currencyRateRepository.findAllByType(CurrencyType.FIAT))
                .thenReturn(Flux.empty());
        when(currencyRateRepository.findAllByType(CurrencyType.CRYPTO))
                .thenReturn(Flux.empty());

        // Call the service method
        Mono<CurrencyRatesResponseDTO> response = currencyRateService.getCurrencyRates();

        // Verify the result
        StepVerifier.create(response)
                .expectNextMatches(dto -> dto.getFiat().size() == 1 && dto.getCrypto().size() == 1)
                .verifyComplete();

        // Verify that repository save was called for both fiat and crypto
        verify(currencyRateRepository, times(2)).save(any(CurrencyRate.class));
    }
}


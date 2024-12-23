package com.leshkins.currencyrateservice.client;

import com.leshkins.currencyrateservice.dto.currency.CryptoCurrencyRateDTO;
import com.leshkins.currencyrateservice.dto.currency.FiatCurrencyRateDTO;
import com.leshkins.currencyrateservice.exception.ApiCallException;
import com.leshkins.currencyrateservice.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyApiClient {

    private final WebClient webClient;

    private final String secretKey;

    @Autowired
    public CurrencyApiClient(@Value("${currency-api.base-url}") String baseUrl,
                             @Value("${currency-api.secret-key}") String secretKey) {
        this.secretKey = secretKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<FiatCurrencyRateDTO> fetchFiatRates() {
        return webClient.get()
                .uri("/fiat-currency-rates")
                .header("X-API-KEY", secretKey)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> {
                    log.error("Fiat API: Unauthorized access");
                    return Mono.error(new UnauthorizedException("Invalid API key"));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Fiat API: Server error occurred");
                    return Mono.error(new ApiCallException("Fiat API Error"));
                })
                .bodyToFlux(FiatCurrencyRateDTO.class)
                .onErrorResume(ApiCallException.class, e -> {
                    log.info("Fiat API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<CryptoCurrencyRateDTO> fetchCryptoRates() {
        return webClient.get()
                .uri("/crypto-currency-rates")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    log.info("Crypto API: Server error occurred");
                    return Mono.error(new ApiCallException("Crypto API Error"));
                })
                .bodyToFlux(CryptoCurrencyRateDTO.class)
                .onErrorResume(ApiCallException.class, e -> {
                    log.info("Crypto API fallback due to: {}", e.getMessage());
                    return Flux.empty();
                });
    }

}


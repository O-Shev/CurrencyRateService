package com.leshkins.currencyrateservice.controller;

import com.leshkins.currencyrateservice.dto.CurrencyRatesResponseDTO;
import com.leshkins.currencyrateservice.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency-rates")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping
    public Mono<CurrencyRatesResponseDTO> getCurrencyRates() {
        return currencyRateService.getCurrencyRates();
    }
}

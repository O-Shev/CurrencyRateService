package com.leshkins.currencyrateservice.repository;

import com.leshkins.currencyrateservice.model.CurrencyRate;
import com.leshkins.currencyrateservice.model.CurrencyType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CurrencyRateRepository extends ReactiveCrudRepository<CurrencyRate, Long> {
    Flux<CurrencyRate> findAllByType(CurrencyType type);

    Mono<CurrencyRate> findByCurrencyAndType(String currency, CurrencyType type);
}


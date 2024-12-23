package com.leshkins.currencyrateservice.dto.currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiatCurrencyRateDTO {
    private String currency;
    private BigDecimal rate;
}
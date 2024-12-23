package com.leshkins.currencyrateservice.dto.currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoCurrencyRateDTO {
    private String name;
    private BigDecimal value;
}
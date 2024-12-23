package com.leshkins.currencyrateservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("currency_rate")
public class CurrencyRate {

    @Id
    private Long id;

    @Column("currency")
    private String currency;

    @Column("rate")
    private BigDecimal rate;

    @Column("type")
    private CurrencyType type;

    @Column("last_updated") // Maps the field to the "last_updated" column
    private LocalDateTime lastUpdated;
}

package com.yunus.model;

import com.yunus.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "acount")
public class Account extends BaseEntity {

    @Column(name = "account_no")
    private String accountNo;

   private String iban;

   private BigDecimal amount;

   @Enumerated(EnumType.STRING)
   private CurrencyType currencyType;
}

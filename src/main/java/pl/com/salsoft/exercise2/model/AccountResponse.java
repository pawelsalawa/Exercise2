package pl.com.salsoft.exercise2.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Account domain object representation used by API, where balance is expressed
 * as decimal number, instead of internal representation (long).
 */
@Setter
@Getter
@Builder
public class AccountResponse {
	private String number;
	private BigDecimal balance;
}

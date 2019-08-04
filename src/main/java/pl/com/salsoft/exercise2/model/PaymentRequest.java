package pl.com.salsoft.exercise2.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Request for payment as passed to the API.
 * None of the fields can be null.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class PaymentRequest {
	@NonNull private String sourceAccount;
	@NonNull private String targetAccount;
	@NonNull private BigDecimal amount;
}

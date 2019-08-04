package pl.com.salsoft.exercise2.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Provides details of the payment request processing result.
 * The accountNumber and the balance fields belong to the source account (sending money),
 * so caller can show the issuer his current balance after operation (successful or not).
 * The success field determines whether the operation was successful and the message field
 * provide details (reason) for the user in case the operation failed.
 */
@Setter
@Getter
@EqualsAndHashCode(exclude = {"balance"})
@Builder
public class PaymentResult {
	private String accountNumber;
	private BigDecimal balance;
	private Boolean success;
	private String message;
}

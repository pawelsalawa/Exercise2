package pl.com.salsoft.exercise2.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.com.salsoft.exercise2.utils.BigDecimalUtils;

/**
 * Domain class representing account with its unique identification number and balance.
 * Balance is stored as long value, so AtomicLong can be used for all concurrent operations.
 */
@Setter
@Getter
@Builder
@EqualsAndHashCode(exclude = {"balance"})
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private String number;
	@JsonIgnore
	private AtomicLong balance;

	/**
	 * Jackson serializer for the balance field.
	 * @return Balance formatted as BigDecimal (with decimal point shifted 2 places left)
	 */
	@JsonGetter("balance")
	public BigDecimal getBalanceForJson() {
		return BigDecimalUtils.toPrice(balance.get());
	}

	/**
	 * Jackson deserializer for the balance field.
	 * @param balance New balance value.
	 */
	@JsonSetter("balance")
	public void setBalanceFromJson(final BigDecimal balance) {
		this.balance = new AtomicLong(BigDecimalUtils.fromPrice(balance));
	}
}

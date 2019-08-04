package pl.com.salsoft.exercise2.service;

import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.model.PaymentRequest;
import pl.com.salsoft.exercise2.model.PaymentResult;
import pl.com.salsoft.exercise2.model.PaymentResult.PaymentResultBuilder;
import pl.com.salsoft.exercise2.rest.ErrorMessages;
import pl.com.salsoft.exercise2.utils.BigDecimalUtils;

/**
 * Service implementing money transfer logic.
 * It basically has only one public method to achieve its purpose.
 */
public class PaymentService {
	private final AccountDao accountDao;

	/**
	 * Creates service with account DAO injected.
	 * @param accountDao Account DAO to use.
	 */
	public PaymentService(@NonNull final AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/**
	 * Processes new payment request (money transfer) from one account to another.
	 * All details (source account, target account and amount) are read from the request body.
	 * @param payment Payment request details.
	 * @return Result of the operation. See Javadoc for PaymentResult for details. Never null.
	 */
	public PaymentResult newPayment(@NonNull final PaymentRequest payment) {
		final long amount = BigDecimalUtils.fromPrice(payment.getAmount());

		final AtomicLong sourceBalance = accountDao.getOrCreate(payment.getSourceAccount()).getBalance();
		final AtomicLong targetBalance = accountDao.getOrCreate(payment.getTargetAccount()).getBalance();

		if (amount <= 0L) {
			return PaymentResult.builder()
					.accountNumber(payment.getSourceAccount())
					.balance(BigDecimalUtils.toPrice(sourceBalance.get()))
					.success(false)
					.message(ErrorMessages.INVALID_AMOUNT_MESSAGE)
					.build();

		}

		final long currentBalance = sourceBalance.get();
		final long updatedBalance = sourceBalance.updateAndGet(balance -> balance >= amount ? balance - amount : balance);

		final PaymentResultBuilder resultBuilder = PaymentResult.builder()
				.accountNumber(payment.getSourceAccount())
				.balance(BigDecimalUtils.toPrice(updatedBalance));

		if (currentBalance == updatedBalance) {
			return resultBuilder
					.success(false)
					.message(ErrorMessages.INSUFFICIENT_FUNDS_MESSAGE)
					.build();
		}

		targetBalance.addAndGet(amount);

		return resultBuilder
				.success(true)
				.build();
	}
}

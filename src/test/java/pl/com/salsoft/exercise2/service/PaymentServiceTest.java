package pl.com.salsoft.exercise2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.model.Account;
import pl.com.salsoft.exercise2.model.PaymentRequest;
import pl.com.salsoft.exercise2.model.PaymentResult;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {
	private PaymentService service;

	@Mock
	private AccountDao accountDao;

	@Before
	public void setup() {
		service = new PaymentService(accountDao);
	}

	@Test
	public void testNewPayment() {
		// Given
		final String sourceAccountNumber = "111";
		final String targetAccountNumber = "222";
		final long initialBalance = 10000;

		final Account sourceAccount = createAccount(sourceAccountNumber, initialBalance);
		final Account targetAccount = createAccount(targetAccountNumber, initialBalance);

		doReturn(sourceAccount).when(accountDao).getOrCreate(eq(sourceAccountNumber));
		doReturn(targetAccount).when(accountDao).getOrCreate(eq(targetAccountNumber));

		final PaymentRequest request = createRequest(sourceAccountNumber, targetAccountNumber, "15.25");

		// When
		final PaymentResult result = service.newPayment(request);

		// Then
		final var sourceBalanceAfterwards = 8475;
		final var sourceBalanceAfterwardsBigDecimal = new BigDecimal("84.75");
		final var targetBalanceAfterwards = 11525;
		assertNotNull(result);
		assertEquals(request.getSourceAccount(), result.getAccountNumber());
		assertEquals(sourceBalanceAfterwardsBigDecimal, result.getBalance());
		assertEquals(sourceBalanceAfterwards, sourceAccount.getBalance().get());
		assertEquals(targetBalanceAfterwards, targetAccount.getBalance().get());
		assertTrue(result.getSuccess());
		assertNull(result.getMessage());
	}

	@Test(expected = NullPointerException.class)
	public void testNewPaymentError() {
		// Given
		// Nothing

		// When
		service.newPayment(null);

		// Then
		// Exception is thrown.
	}

	@Test
	public void testNewPaymentInssuficientFunds() {
		// Given
		final String sourceAccountNumber = "111";
		final String targetAccountNumber = "222";
		final long initialBalance = 1000;

		final Account sourceAccount = createAccount(sourceAccountNumber, initialBalance);
		final Account targetAccount = createAccount(targetAccountNumber, initialBalance);

		doReturn(sourceAccount).when(accountDao).getOrCreate(eq(sourceAccountNumber));
		doReturn(targetAccount).when(accountDao).getOrCreate(eq(targetAccountNumber));

		final PaymentRequest request = createRequest(sourceAccountNumber, targetAccountNumber, "500");

		// When
		final PaymentResult result = service.newPayment(request);

		// Then
		final var sourceBalanceAfterwards = initialBalance;
		final var sourceBalanceAfterwardsBigDecimal = new BigDecimal(initialBalance).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		final var targetBalanceAfterwards = initialBalance;
		assertNotNull(result);
		assertEquals(request.getSourceAccount(), result.getAccountNumber());
		assertEquals(sourceBalanceAfterwardsBigDecimal, result.getBalance());
		assertEquals(sourceBalanceAfterwards, sourceAccount.getBalance().get());
		assertEquals(targetBalanceAfterwards, targetAccount.getBalance().get());
		assertFalse(result.getSuccess());
		assertNotNull(result.getMessage());
	}

	@Test
	public void testNewPaymentNegativeAmount() {
		// Given
		final String sourceAccountNumber = "111";
		final String targetAccountNumber = "222";
		final long initialBalance = 10000;

		final Account sourceAccount = createAccount(sourceAccountNumber, initialBalance);
		final Account targetAccount = createAccount(targetAccountNumber, initialBalance);

		doReturn(sourceAccount).when(accountDao).getOrCreate(eq(sourceAccountNumber));
		doReturn(targetAccount).when(accountDao).getOrCreate(eq(targetAccountNumber));

		final PaymentRequest request = createRequest(sourceAccountNumber, targetAccountNumber, "-1");

		// When
		final PaymentResult result = service.newPayment(request);

		// Then
		final var sourceBalanceAfterwards = initialBalance;
		final var sourceBalanceAfterwardsBigDecimal = new BigDecimal(initialBalance).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		final var targetBalanceAfterwards = initialBalance;
		assertNotNull(result);
		assertEquals(request.getSourceAccount(), result.getAccountNumber());
		assertEquals(sourceBalanceAfterwardsBigDecimal, result.getBalance());
		assertEquals(sourceBalanceAfterwards, sourceAccount.getBalance().get());
		assertEquals(targetBalanceAfterwards, targetAccount.getBalance().get());
		assertFalse(result.getSuccess());
		assertNotNull(result.getMessage());
	}

	private Account createAccount(final String sourceAccountNumber, final long balance) {
		return Account.builder().number(sourceAccountNumber).balance(new AtomicLong(balance)).build();
	}

	private PaymentRequest createRequest(final String sourceAccountNumber, final String targetAccountNumber, final String amount) {
		return PaymentRequest.builder()
				.sourceAccount(sourceAccountNumber)
				.targetAccount(targetAccountNumber)
				.amount(new BigDecimal(amount))
				.build();
	}

}

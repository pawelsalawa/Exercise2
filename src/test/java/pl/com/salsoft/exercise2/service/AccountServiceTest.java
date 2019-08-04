package pl.com.salsoft.exercise2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.model.Account;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	private AccountService service;

	@Mock
	private AccountDao accountDao;

	@Before
	public void setup() {
		service = new AccountService(accountDao);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteError() {
		// Given
		// Nothing

		// When
		service.deleteAccount(null);

		// Then
		// Exception is thrown.
	}

	@Test
	public void testDeleteNegative() {
		// Given
		final String number = "123";
		doReturn(false).when(accountDao).delete(eq(number));

		// When
		final boolean result = service.deleteAccount(number);

		// Then
		assertFalse(result);
	}

	@Test
	public void testDeletePositive() {
		// Given
		final String number = "123";
		doReturn(true).when(accountDao).delete(eq(number));

		// When
		final boolean result = service.deleteAccount(number);

		// Then
		assertTrue(result);
	}

	@Test
	public void testGetAccount() {
		// Given
		final String number = "123";
		final Account account = Account.builder().number(number).build();
		doReturn(Optional.of(account)).when(accountDao).get(eq(number));

		// When
		final var result = service.getAccount(number);

		// Then
		assertTrue(result.isPresent());
		assertTrue(account == result.orElseThrow());
	}

	@Test(expected = NullPointerException.class)
	public void testGetAccountError() {
		// Given
		// Nothing

		// When
		service.getAccount(null);

		// Then
		// Exception is thrown.
	}

	@Test
	public void testGetAccounts() {
		// Given
		final var accounts = Set.of(
				Account.builder().number("1").build(),
				Account.builder().number("2").build()
				);

		doReturn(accounts).when(accountDao).getAll();

		// When
		final var result = service.getAccounts();

		// Then
		assertFalse(result.isEmpty());
		assertEquals(accounts, result);
	}

	@Test
	public void testGetAccountsEmpty() {
		// Given
		doReturn(Set.of()).when(accountDao).getAll();

		// When
		final var result = service.getAccounts();

		// Then
		assertTrue(result.isEmpty());
	}
}

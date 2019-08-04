package pl.com.salsoft.exercise2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import pl.com.salsoft.exercise2.model.Account;

public class AccountDaoTest {
	private static final long DEFAULT_BALANCE = 10000;
	private AccountDao dao;

	@Before
	public void beforeTest() {
		dao = new AccountDao(DEFAULT_BALANCE);
	}


	@Test
	public void testDefaultBalance() {
		final String number = "123";

		// When
		final long balance = dao.getOrCreate(number).getBalance().get();

		// Then
		assertEquals(DEFAULT_BALANCE, balance);
	}

	@Test
	public void testDelete() {
		// Given
		final String number = "123";
		dao.getOrCreate(number);

		// When
		final boolean result = dao.delete(number);

		// Then
		assertTrue(result);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteError() {
		// Given
		// Nothing

		// When
		dao.delete(null);

		// Then
		// Exception is thrown
	}

	@Test
	public void testDeleteMultipleNoError() {
		// Given
		final String number = "123";
		dao.getOrCreate(number);

		// When
		final boolean result1 = dao.delete(number);
		final boolean result2 = dao.delete(number);
		final boolean result3 = dao.delete(number);

		// Then
		assertTrue(result1);
		assertFalse(result2);
		assertFalse(result3);
	}

	@Test
	public void testGetAll() {
		// Given
		final var accounts = IntStream.range(0, 10)
			.boxed()
			.map(number -> dao.getOrCreate("" + number))
			.collect(Collectors.toSet());

		// When
		final var allAccounts = dao.getAll();

		// Then
		assertEquals(accounts, allAccounts);
	}

	@Test
	public void testGetAllEmpty() {
		// Given
		// Nothing

		// When
		final var allAccounts = dao.getAll();

		// Then
		assertTrue(allAccounts.isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void testGetError() {
		// Given
		// Nothing

		// When
		dao.get(null);

		// Then
		// Exception is thrown
	}

	@Test
	public void testGetNegative() {
		// Given
		final var number = "123";
		dao.getOrCreate(number);

		// When
		final var foundAccount = dao.get(number + "000");

		// Then
		assertFalse(foundAccount.isPresent());
	}

	@Test
	public void testGetOrCreate() {
		// Given
		final var number = "123";

		// When
		final Account account = dao.getOrCreate(number);

		// Then
		assertNotNull(account);
	}

	@Test
	public void testGetOrCreateExisting() {
		// Given
		final var number = "123";
		final Account initialAccount = dao.getOrCreate(number);

		// When
		final Account account = dao.getOrCreate(number);

		// Then
		assertTrue(initialAccount == account);
	}

	@Test
	public void testGetPositive() {
		// Given
		final var number = "123";
		dao.getOrCreate(number);

		// When
		final var foundAccount = dao.get(number);

		// Then
		assertTrue(foundAccount.isPresent());
	}
}

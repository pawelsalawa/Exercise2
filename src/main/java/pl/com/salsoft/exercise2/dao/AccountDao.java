package pl.com.salsoft.exercise2.dao;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import pl.com.salsoft.exercise2.model.Account;

/**
 * Registry of active accounts with their number and balance.
 * There is no dedicated method just to create/insert account.
 * Accounts are created with getOrCreate() method.
 * Newly created accounts have default balance of 100.
 */
public class AccountDao {
	private final long defaultBalance;
	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	/**
	 * Creates account DAO with predefined default value of initial account balance
	 * for bewly created accounts.
	 * @param defaultBalance Default balance for new accounts.
	 */
	public AccountDao(final long defaultBalance) {
		this.defaultBalance = defaultBalance;
	}

	/**
	 * Deletes an account from registry. This operation is thread-safe.
	 * @param number Number of account to delete.
	 * @return true if account was deleted, or false if no such account was in the registry.
	 */
	public boolean delete(@NonNull final String number) {
		return accounts.remove(number) != null;
	}

	/**
	 * Finds account with given number in the registry. This operation is thread-safe.
	 * @param number Account number to find.
	 * @return Optional of account found, or empty optional if no such account was found.
	 */
	public Optional<Account> get(final String number) {
		return Optional.ofNullable(accounts.get(number));
	}

	/**
	 * Finds all accounts currently active in the application. This operation is thread-safe.
	 * @return Set of accounts. Could be empty set, but never null.
	 */
	public Set<Account> getAll() {
		final Collection<Account> values = accounts.values();
		// While it's safe to call values() out of synchronized block,
		// iterating over the result collection should already be done inside of such block.
		// That's what Java documentation states.
		synchronized (accounts) {
			return Set.copyOf(values);
		}
	}

	/**
	 * Finds account with given number in the registry, or creates one if it didn't exist
	 * and persists it in the registry. Balance of the newly created account is set to the default
	 * value of 100. This operation is thread-safe.
	 * @param number Account number to find/create.
	 * @return Found or created account. Never null.
	 */
	public Account getOrCreate(final String number) {
		return accounts.computeIfAbsent(number, key -> Account.builder()
				.number(number)
				.balance(new AtomicLong(defaultBalance))
				.build());
	}
}

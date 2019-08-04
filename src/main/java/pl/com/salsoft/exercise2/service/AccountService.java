package pl.com.salsoft.exercise2.service;

import java.util.Optional;
import java.util.Set;

import lombok.NonNull;
import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.model.Account;

/**
 * Provides (some of) basic operations on accounts.
 */
public class AccountService {

	private final AccountDao accountDao;

	/**
	 * Creates service with account DAO injected.
	 * @param accountDao Account DAO to use. Cannot be null.
	 */
	public AccountService(@NonNull final AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/**
	 * Deletes account with given number.
	 * @param number Number identifying an account to delete. Cannot be null.
	 * @return true if account was deleted, or false if there was no such account.
	 */
	public boolean deleteAccount(@NonNull final String number) {
		return accountDao.delete(number);
	}

	/**
	 * Finds account with given number.
	 * @param number Number identifying an account to find. Cannot be null.
	 * @return Optional with account found, or empty optional otherwise.
	 */
	public Optional<Account> getAccount(@NonNull final String number) {
		return accountDao.get(number);
	}

	/**
	 * Finds all accounts active in the application.
	 * @return Set of accounts. Possibly empty set, but never null.
	 */
	public Set<Account> getAccounts() {
		return accountDao.getAll();
	}
}

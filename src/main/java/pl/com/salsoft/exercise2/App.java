package pl.com.salsoft.exercise2;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Set;

import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.rest.AccountController;
import pl.com.salsoft.exercise2.rest.PaymentController;
import pl.com.salsoft.exercise2.service.AccountService;
import pl.com.salsoft.exercise2.service.JsonService;
import pl.com.salsoft.exercise2.service.PaymentService;
import pl.com.salsoft.exercise2.utils.BigDecimalUtils;
import spark.Spark;

/**
 * Exercise2 application entry point.
 */
public class App {
	private static final String MISSING_CONFIGURATION_ENTRY = "Missing configuration entry: %s";
	private static final String CONFIG_FILE_NAME = "config.properties";
	private static final String CONFIG_PORT = "port";
	private static final String CONFIG_DEFAULT_BALANCE = "default.balance";
	private static final Properties CONFIG = new Properties();

	/**
	 * Runs Spark (REST) and Guice (IoC/DI), effectively starting the application.
	 * @param args Command line arguments. None are supported at the moment. Anything passed here will be ignored.
	 * @throws IOException If configuration file could not be read.
	 */
	public static void main(final String[] args) throws IOException {
		loadProperties();
		Spark.port(getServerPort());

		createBeans().forEach(Initializable::init);
	}

	private static Set<Initializable> createBeans() {
		// This is a small application. Let's do whole IoC here in pure Java.
		final AccountDao accountDao = new AccountDao(getDefaultBalance());
		final JsonService jsonService = new JsonService();
		final PaymentService paymentService = new PaymentService(accountDao);
		final AccountService accountService = new AccountService(accountDao);
		final PaymentController paymentController = new PaymentController(jsonService, paymentService);
		final AccountController accountController = new AccountController(jsonService, accountService);
		return Set.of(paymentController, accountController);
	}

	private static long getDefaultBalance() {
		try {
			return BigDecimalUtils.fromPrice(new BigDecimal(CONFIG.get(CONFIG_DEFAULT_BALANCE).toString()));
		} catch (final NumberFormatException e) {
			throw invalidConfigEntry(CONFIG_DEFAULT_BALANCE);
		}
	}

	private static int getServerPort() {
		try {
			return Integer.parseInt(CONFIG.get(CONFIG_PORT).toString());
		} catch (final NumberFormatException e) {
			throw invalidConfigEntry(CONFIG_PORT);
		}
	}

	private static RuntimeException invalidConfigEntry(final String entry) {
		return new RuntimeException(String.format("Invalid format of %s configuration entry.", entry));
	}

	private static void loadProperties() throws IOException {
		try (InputStream input = App.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
			CONFIG.load(input);
		}
		if (!CONFIG.containsKey(CONFIG_PORT)) {
			throw missingConfigEntry(CONFIG_PORT);
		}
		if (!CONFIG.containsKey(CONFIG_DEFAULT_BALANCE)) {
			throw missingConfigEntry(CONFIG_DEFAULT_BALANCE);
		}
	}

	private static RuntimeException missingConfigEntry(final String entry) {
		return new RuntimeException(String.format(MISSING_CONFIGURATION_ENTRY, entry));
	}
}

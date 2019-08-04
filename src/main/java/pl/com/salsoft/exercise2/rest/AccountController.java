package pl.com.salsoft.exercise2.rest;

import java.util.stream.Collectors;

import org.eclipse.jetty.http.HttpStatus;

import lombok.NonNull;
import pl.com.salsoft.exercise2.Initializable;
import pl.com.salsoft.exercise2.service.AccountService;
import pl.com.salsoft.exercise2.service.JsonService;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Controller handling /account endpoint. It provides operations for inspecting and deleting accounts.
 */
public class AccountController extends AbstractController implements Initializable {
	private static final String SUPPORTED_ACTIONS = "GET,DELETE,OPTIONS,HEAD";
	private static final String NUMBER = ":number";
	private static final String RESOURCE_ROOT = "/account";
	private static final String RESOURCE_BY_ID = String.format("%s/%s", RESOURCE_ROOT, NUMBER);

	private final AccountService accountService;

	/**
	 * Creates controller with account and JSON services injected.
	 * @param jsonService JSON service to use. Cannot be null.
	 * @param accountService Account service to use. Cannot be null.
	 */
	public AccountController(@NonNull final JsonService jsonService, @NonNull final AccountService accountService) {
		super(jsonService);
		this.accountService = accountService;
	}

	/**
	 * Sets up all supported REST request mappings.
	 * Should be called at the application start.
	 */
	@Override
	public void init() {
		Spark.get(RESOURCE_ROOT, handle(this::getAll));
		Spark.get(RESOURCE_BY_ID, handle(this::getSingle));
		Spark.delete(RESOURCE_BY_ID, handle(this::delete));
		Spark.options(RESOURCE_ROOT, handle(this::options));
		Spark.head(RESOURCE_ROOT, handle(this::head));
	}

	/**
	 * Implements DELETE method from REST.
	 */
	private Object delete(final Request request, final Response response) {
		final boolean deleted = accountService.deleteAccount(readNumber(request));
		response.status(deleted ? HttpStatus.NO_CONTENT_204 : HttpStatus.NOT_FOUND_404);
		return null;
	}

	/**
	 * Implements GET method from REST, variation without account number.
	 */
	private Object getAll(final Request request, final Response response) {
		response.status(HttpStatus.OK_200);
		return accountService.getAccounts().stream()
				.collect(Collectors.toSet());
	}

	/**
	 * Implements GET method from REST, variation with account number provided in the path.
	 */
	private Object getSingle(final Request request, final Response response) {
		final var order = accountService.getAccount(readNumber(request));
		if (order.isPresent()) {
			response.status(HttpStatus.OK_200);
			return order.orElseThrow();
		}
		response.status(HttpStatus.NOT_FOUND_404);
		return null;
	}

	/**
	 * Implements HEAD method from REST.
	 */
	private Object head(final Request request, final Response response) {
		response.status(HttpStatus.OK_200);
		return null;
	}

	/**
	 * Implements OPTIONS method from REST.
	 */
	private Object options(final Request request, final Response response) {
		response.header("Allow", SUPPORTED_ACTIONS);
		response.status(HttpStatus.OK_200);
		return null;
	}

	private @NonNull String readNumber(final Request request) {
		return request.params(NUMBER);
	}
}

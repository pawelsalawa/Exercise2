package pl.com.salsoft.exercise2.rest;

import java.util.Optional;

import org.eclipse.jetty.http.HttpStatus;

import lombok.NonNull;
import pl.com.salsoft.exercise2.Initializable;
import pl.com.salsoft.exercise2.model.PaymentRequest;
import pl.com.salsoft.exercise2.service.JsonService;
import pl.com.salsoft.exercise2.service.PaymentService;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Controller handling /account endpoint. It provides currently just one operation using
 * POST method - to trigger money transfer process.
 */
public class PaymentController extends AbstractController implements Initializable {
	private static final String SUPPORTED_ACTIONS = "POST,OPTIONS,HEAD";
	private static final String RESOURCE_ROOT = "/payment";

	private final PaymentService paymentService;

	/**
	 * Creates controller with payment and JSON services injected.
	 * @param jsonService JSON service to use. Cannot be null.
	 * @param paymentService Payment service to use. Cannot be null.
	 */
	public PaymentController(@NonNull final JsonService jsonService, @NonNull final PaymentService paymentService) {
		super(jsonService);
		this.paymentService = paymentService;
	}

	/**
	 * Sets up all REST request mappings.
	 * Should be called at the application start.
	 */
	@Override
	public void init() {
		Spark.post(RESOURCE_ROOT, handle(this::post));
		Spark.options(RESOURCE_ROOT, handle(this::options));
		Spark.head(RESOURCE_ROOT, handle(this::head));
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

	/**
	 * Implements POST method from REST. It performs money transfer operation,
	 * according to details passed in the body.
	 */
	private Object post(final Request request, final Response response) {
		response.status(HttpStatus.OK_200);
		return paymentService.newPayment(readPayment(request));
	}

	private @NonNull PaymentRequest readPayment(final Request request) {
		return Optional.ofNullable(jsonService.map(request.body(), PaymentRequest.class)).orElseThrow();
	}
}

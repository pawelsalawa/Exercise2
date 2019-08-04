package pl.com.salsoft.exercise2.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.com.salsoft.exercise2.dao.AccountDao;
import pl.com.salsoft.exercise2.model.Account;
import pl.com.salsoft.exercise2.model.PaymentRequest;
import pl.com.salsoft.exercise2.model.PaymentResult;
import pl.com.salsoft.exercise2.service.AccountService;
import pl.com.salsoft.exercise2.service.JsonService;
import pl.com.salsoft.exercise2.service.PaymentService;
import spark.Spark;

public class IntegrationTest {

	private static final String URL_PATTERN = "http://localhost:%d/%s";
	private static final String APPLICATION_JSON = "application/json";
	private static final int FREE_PORT = findFreePort();
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final long DEFAULT_BALANCE = 10000;

	private static int findFreePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpClient client;

	@After
	public void afterTest() {
		Spark.stop();
		Spark.awaitStop();
	}

	@Before
	public void beforeTest() {
		Spark.port(FREE_PORT);

		final AccountDao accountDao = new AccountDao(DEFAULT_BALANCE);
		final JsonService jsonService = new JsonService();
		final PaymentService paymentService = new PaymentService(accountDao);
		final AccountService accountService = new AccountService(accountDao);
		new PaymentController(jsonService, paymentService).init();
		new AccountController(jsonService, accountService).init();

		client = HttpClient.newHttpClient();
	}

	@Test
	public void testAccountHead() throws IOException, InterruptedException {
		// Given

		// When
		final var response = head("account");

		// Then
		assertEquals(HttpStatus.OK_200, response.statusCode());
		assertEquals("", response.body());
	}

	@Test
	public void testAccountOptions() throws IOException, InterruptedException {
		// Given
		final Set<String> expectedActions = Set.of("GET", "DELETE", "OPTIONS", "HEAD");

		// When
		final var response = options("account");

		// Then
		assertEquals(HttpStatus.OK_200, response.statusCode());
		assertEquals("", response.body());

		final Map<String, List<String>> headers = response.headers().map();
		assertTrue(headers.containsKey("Allow"));
		assertNotNull(headers.get("Allow"));
		assertEquals(1, headers.get("Allow").size());

		final String allow = headers.get("Allow").get(0).toString();
		final Set<String> responseActions = Pattern.compile("\\s*,\\s*").splitAsStream(allow).collect(Collectors.toSet());
		assertEquals(expectedActions, responseActions);
	}

	@Test
	public void testDeleteAccount() throws IOException, InterruptedException {
		// Given
		final PaymentRequest request = PaymentRequest.builder()
				.amount(new BigDecimal("60.00"))
				.sourceAccount("111")
				.targetAccount("222")
				.build();

		// When
		final var postResponse = post("payment", toJson(request));
		final var deleteResponse = delete("account/111");
		final var getResponse = get("account/111");

		// Then
		final PaymentResult expectedResult1 = PaymentResult.builder()
				.accountNumber("111")
				.balance(new BigDecimal("40.00"))
				.success(true)
				.build();

		assertEquals(HttpStatus.OK_200, postResponse.statusCode());
		assertEquals(toJson(expectedResult1), postResponse.body());
		assertEquals(HttpStatus.NO_CONTENT_204, deleteResponse.statusCode());
		assertEquals(HttpStatus.NOT_FOUND_404, getResponse.statusCode());
	}

	@Test
	public void testGetAccounts() throws IOException, InterruptedException {
		// Given
		final PaymentRequest request = PaymentRequest.builder()
				.amount(new BigDecimal("60.00"))
				.sourceAccount("111")
				.targetAccount("222")
				.build();

		// When
		final var postResponse = post("payment", toJson(request));
		final var getResponse = get("account");

		// Then
		final PaymentResult expectedResult1 = PaymentResult.builder()
				.accountNumber("111")
				.balance(new BigDecimal("40.00"))
				.success(true)
				.build();

		assertEquals(HttpStatus.OK_200, postResponse.statusCode());
		assertEquals(toJson(expectedResult1), postResponse.body());
		assertEquals(HttpStatus.OK_200, getResponse.statusCode());

		final List<Account> accounts = fromJson(getResponse.body(), new TypeReference<Set<Account>>() {})
				.stream()
				.sorted(Comparator.comparing(Account::getNumber))
				.collect(Collectors.toList());
		assertEquals(2, accounts.size());
		assertEquals("111", accounts.get(0).getNumber());
		assertEquals(4000L, accounts.get(0).getBalance().get());
		assertEquals("222", accounts.get(1).getNumber());
		assertEquals(16000L, accounts.get(1).getBalance().get());
	}

	@Test
	public void testPaymentHead() throws IOException, InterruptedException {
		// Given

		// When
		final var response = head("payment");

		// Then
		assertEquals(HttpStatus.OK_200, response.statusCode());
		assertEquals("", response.body());
	}

	@Test
	public void testPaymentOptions() throws IOException, InterruptedException {
		// Given
		final Set<String> expectedActions = Set.of("POST", "OPTIONS", "HEAD");

		// When
		final var response = options("payment");

		// Then
		assertEquals(HttpStatus.OK_200, response.statusCode());
		assertEquals("", response.body());

		final Map<String, List<String>> headers = response.headers().map();
		assertTrue(headers.containsKey("Allow"));
		assertNotNull(headers.get("Allow"));
		assertEquals(1, headers.get("Allow").size());

		final String allow = headers.get("Allow").get(0).toString();
		final Set<String> responseActions = Pattern.compile("\\s*,\\s*").splitAsStream(allow).collect(Collectors.toSet());
		assertEquals(expectedActions, responseActions);
	}

	@Test
	public void testSecondTransferTooBig() throws IOException, InterruptedException {
		// Given
		final PaymentRequest request = PaymentRequest.builder()
				.amount(new BigDecimal("60.00"))
				.sourceAccount("111")
				.targetAccount("222")
				.build();

		// When
		final var response1 = post("payment", toJson(request));
		final var response2 = post("payment", toJson(request));

		// Then
		final PaymentResult expectedResult1 = PaymentResult.builder()
				.accountNumber("111")
				.balance(new BigDecimal("40.00"))
				.success(true)
				.build();

		assertEquals(HttpStatus.OK_200, response1.statusCode());
		assertEquals(toJson(expectedResult1), response1.body());

		final PaymentResult expectedResult2 = PaymentResult.builder()
				.accountNumber("111")
				.balance(new BigDecimal("40.00"))
				.success(false)
				.message(ErrorMessages.INSUFFICIENT_FUNDS_MESSAGE)
				.build();

		assertEquals(HttpStatus.OK_200, response2.statusCode());
		assertEquals(toJson(expectedResult2), response2.body());
	}

	@Test
	public void testSingleTransfer() throws IOException, InterruptedException {
		// Given
		final PaymentRequest request = PaymentRequest.builder()
				.amount(new BigDecimal("40.35"))
				.sourceAccount("111")
				.targetAccount("222")
				.build();

		// When
		final var response = post("payment", toJson(request));

		// Then
		final PaymentResult expectedResult = PaymentResult.builder()
				.accountNumber("111")
				.balance(new BigDecimal("59.65"))
				.success(true)
				.build();

		assertEquals(HttpStatus.OK_200, response.statusCode());
		assertEquals(toJson(expectedResult), response.body());
	}

	private HttpResponse<String> delete(final String resource) throws IOException, InterruptedException {
		return send(builder -> builder.DELETE(), resource);
	}

	private <T> T fromJson(final String json, final TypeReference<T> type) throws IOException {
		return mapper.readValue(json, type);
	}

	private HttpResponse<String> get(final String resource) throws IOException, InterruptedException {
		return send(builder -> builder.GET(), resource);
	}

	private HttpResponse<String> head(final String resource) throws IOException, InterruptedException {
		return send(builder -> builder.method("HEAD", BodyPublishers.noBody()), resource);
	}

	private HttpResponse<String> options(final String resource) throws IOException, InterruptedException {
		return send(builder -> builder.method("OPTIONS", BodyPublishers.noBody()), resource);
	}


	private HttpResponse<String> post(final String resource, final String body) throws IOException, InterruptedException {
		return send(builder -> builder.POST(BodyPublishers.ofString(body)), resource);
	}

	private HttpResponse<String> send(final Consumer<Builder> methodProvider, final String resource) throws IOException, InterruptedException {
		final Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(String.format(URL_PATTERN, FREE_PORT, resource)))
				.header("Content-Type",  APPLICATION_JSON)
				.version(Version.HTTP_2);

		methodProvider.accept(builder);
		final HttpRequest request = builder.build();
		return client.send(request, BodyHandlers.ofString());
	}

	private String toJson(final Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}
}

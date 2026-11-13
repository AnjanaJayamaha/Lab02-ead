package lk.ac.mrt.fit.library_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryApiApplicationTests {

	@Value("${local.server.port}")
	private int port;
	private final HttpClient client = HttpClient.newHttpClient();

	private HttpResponse<String> request(String method, String path, String body) throws Exception {
		return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
				.header("Content-Type", "application/json")
				.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
						: HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
	}

	@Test
	void bookLifecycleAndValidation() throws Exception {
		assertEquals(400, request("POST", "/api/books", "{\"title\":\"\",\"author\":\"\"}").statusCode());
		var created = request("POST", "/api/books", "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"publishedYear\":2008}");
		assertEquals(201, created.statusCode());
		String path = created.headers().firstValue("Location").orElseThrow();
		assertEquals(200, request("GET", path, null).statusCode());
		assertTrue(request("GET", "/api/books", null).body().contains("Clean Code"));
		var updated = request("PUT", path, "{\"title\":\"Updated title\",\"author\":\"Robert Martin\"}");
		assertEquals(200, updated.statusCode());
		assertTrue(request("GET", path, null).body().contains("Updated title"));
		assertEquals(204, request("DELETE", path, null).statusCode());
		assertEquals(404, request("GET", path, null).statusCode());
		assertEquals(404, request("PUT", path, "{\"title\":\"Missing\",\"author\":\"Author\"}").statusCode());
		assertEquals(404, request("DELETE", path, null).statusCode());
		assertEquals(200, request("GET", "/v3/api-docs", null).statusCode());
		assertEquals(200, request("GET", "/swagger-ui/index.html", null).statusCode());
	}

	@Test
	void contextLoads() {
	}

}

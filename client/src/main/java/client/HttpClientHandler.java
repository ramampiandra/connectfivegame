package client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class process the HTTP Request send or receive to/from the server
 * We use the HttpClient java 11 class
 * @author claire
 *
 */
public class HttpClientHandler {
	
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
	
	public static void sendDataPost(GameState gameState, String url) throws IOException, InterruptedException {
		// json formatted data
        String json = new StringBuilder()
                .append("{")
                .append("\"turn\":\""+ gameState.getTurn() +"\",")
                .append("\"userNames\":\""+ gameState.getUserNames() +"\",")
                .append("\"stateGame\":\""+ gameState.getStateGame() +"\",")
                .append("\"status\":\""+ gameState.getStatus() +"\",")
                .append("}").toString();

        // add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localost:8080" + url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<GameState> response = httpClient.send(request, new JsonBodyHandler<>(GameState.class));
	}
	
	public static GameState getData(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080" + url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();

        HttpResponse<GameState> response = httpClient.send(request, new JsonBodyHandler<>(GameState.class));
        return response.body();
	}
	
	
	public static class JsonBodyHandler<W> implements HttpResponse.BodyHandler<W> {

	    private Class<W> wClass;

	    public JsonBodyHandler(Class<W> wClass) {
	        this.wClass = wClass;
	    }

	    @Override
	    public HttpResponse.BodySubscriber<W> apply(HttpResponse.ResponseInfo responseInfo) {
	        return asJSON(wClass);
	    }

	    public static <T> HttpResponse.BodySubscriber<T> asJSON(Class<T> targetType) {
	        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

	        return HttpResponse.BodySubscribers.mapping(
	                upstream,
	                (String body) -> {
	                    try {
	                        ObjectMapper objectMapper = new ObjectMapper();
	                        return objectMapper.readValue(body, targetType);
	                    } catch (IOException e) {
	                        //throw new UncheckedIOException(e);
	                    	return null;
	                    }
	                });
	    }
	}

}

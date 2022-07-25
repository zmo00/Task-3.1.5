import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class Task {

    private final static String URL = "http://94.198.50.185:7081/api/users";

    private final static RestTemplate REST_TEMPLATE = new RestTemplate();

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException, URISyntaxException {

        ResponseFromFirstRequest responseFromFirstRequest = getListAllUser();

        HttpHeaders headers = responseFromFirstRequest.getHeaders();
        System.out.println(responseFromFirstRequest.getUsers());

        User user = new User(3L, "James", "Brown", (byte) 21);

        String result1 = createUser(user, headers);

        user.setName("Thomas");
        user.setLastName("Shelby");

        String result2 = updateUser(user, headers);

        String result3 = deleteUser(user, headers);

        String result = result1 + result2 + result3;
        System.out.println(result + " -- " + result.length() + " символов");
    }

    private static String deleteUser(User user, HttpHeaders headers) throws URISyntaxException {
        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.DELETE, new URI(URL + "/" + user.getId()));
        ResponseEntity<String> response = REST_TEMPLATE.exchange(request, String.class);

        return response.getBody();
    }

    private static String updateUser(User user, HttpHeaders headers) throws URISyntaxException {
        RequestEntity<User> request = new RequestEntity<>(user, headers, HttpMethod.PUT, new URI(URL));
        ResponseEntity<String> response = REST_TEMPLATE.exchange(request, String.class);

        return response.getBody();
    }

    private static String createUser(User user, HttpHeaders headers) throws URISyntaxException {
        RequestEntity<User> request = new RequestEntity<>(user, headers, HttpMethod.POST, new URI(URL));
        ResponseEntity<String> response = REST_TEMPLATE.exchange(request, String.class);

        return response.getBody();
    }

    private static ResponseFromFirstRequest getListAllUser() throws JsonProcessingException {
        ResponseEntity<String> response = REST_TEMPLATE.getForEntity(URL, String.class);

        List<User> users = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {});

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.COOKIE, Objects.requireNonNull(response.getHeaders().get("Set-cookie")).get(0));

        return new ResponseFromFirstRequest(users, headers);
    }

    private static class ResponseFromFirstRequest {

        private final List<User> users;

        private final HttpHeaders headers;

        public ResponseFromFirstRequest(List<User> users, HttpHeaders headers) {
            this.users = users;
            this.headers = headers;
        }

        public List<User> getUsers() {
            return users;
        }

        public HttpHeaders getHeaders() {
            return headers;
        }
    }

}

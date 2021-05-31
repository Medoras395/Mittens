package my.bot.mittens;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JsonDownloader {

    private static String pictureUrl;
//    String pictureUrl;
    String testUrl = "https://jsonplaceholder.typicode.com/albums";


    JsonDownloader(){}

    public static void readJsonFromUrl() throws IOException, InterruptedException {
        String apiKey = "667252f1-5d34-46ba-86c0-8c2ef95fc49a";
        String apiUrl = "https://api.thecatapi.com/v1/images/search";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("x-api-key", apiKey)
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//        ObjectMapper mapper = new ObjectMapper();
//        List<Post> posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
//
//        posts.forEach(System.out::println);
    }

    public static void jsonDownload() {
        String apiKey = "667252f1-5d34-46ba-86c0-8c2ef95fc49a";
        String apiUrl = "https://api.thecatapi.com/v1/images/search";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("x-api-key", apiKey)
                .uri(URI.create(apiUrl))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(JsonDownloader::parseOne)   // class name
                //.thenAccept(System.out::println)
                .join();
    }

    public static String parse(String responseBody) {
        JSONArray albums = new JSONArray(responseBody);
        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i);
            String picture = album.getString("url");
            System.out.println(picture);
        }
        return null;
    }

    public static String parseOne(String responseBody) {
        JSONArray albums = new JSONArray(responseBody);
        JSONObject album = albums.getJSONObject(0);
        String picture = album.getString("url");

        setPictureUrl(picture);

        //System.out.println(picture);

        return null;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public static void setPictureUrl(String newUrl) {
        pictureUrl = newUrl;
    }


}

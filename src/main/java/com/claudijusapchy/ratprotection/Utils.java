package com.claudijusapchy.ratprotection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class Utils {
    public static String[] getProfileInfo(String token) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            String ign = jsonObject.get("name").getAsString();
            String uuid = jsonObject.get("id").getAsString().replace("-", "");
            return new String[]{ign, uuid};
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Error", e);
        }
    }

    public static UUID stringToUUID(String uuid) {
        return UUID.fromString(uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"));
    }
}
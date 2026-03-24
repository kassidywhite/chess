package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        var req = buildRequest("POST", "/user", request, null);
        var response = sendRequest(req);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var req = buildRequest("POST", "/session", request, null);
        var response = sendRequest(req);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(String token) throws Exception {
        var req = buildRequest("DELETE", "/session", null, token);
        var response = sendRequest(req);
        return handleResponse(response, LogoutResult.class);
    }

    public NewGameResult createGame(NewGameRequest request) throws Exception {
        var req = buildRequest("POST", "/game", request, null);
        var response = sendRequest(req);
        return handleResponse(response, NewGameResult.class);
    }

    public ListGamesResult listGames(String token) throws Exception{
        var req = buildRequest("GET", "/game", null, token);
        var response = sendRequest(req);
        return handleResponse(response, ListGamesResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String auth) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if(auth != null){
            request.header("Authorization", auth);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)){
            var body = response.body();
            if (body != null) {
                throw new Exception(body);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {return status / 100 == 2;}
}


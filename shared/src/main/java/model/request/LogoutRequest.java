package model.request;

import model.AuthData;

public record LogoutRequest (String authToken) {
}

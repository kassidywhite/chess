package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void addAuth(AuthData data) {

    }

    @Override
    public String getAuthByUser(String username) {
        return "";
    }

    @Override
    public String getUserByAuth(String token) {
        return "";
    }

    @Override
    public void deleteAuth(String token) {

    }

    @Override
    public void deleteAllAuth() {

    }
}

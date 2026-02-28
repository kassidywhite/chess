package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

import java.util.ArrayList;

public class AuthService {

    private final AuthDAO authAccess = new MemoryAuthDAO();

    public AuthService(){}

    public AuthData getAuth(String username){
        return authAccess.getAuth(username);
    }

    public ArrayList<AuthData> listAuth(){
        return authAccess.listAuth();
    }
}

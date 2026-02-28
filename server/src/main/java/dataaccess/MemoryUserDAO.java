package dataaccess;

import model.UserData;
import model.result.DeleteResult;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final private HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void UserDAO() {

    }

    @Override
    public void addUser(UserData data) {
        users.put(data.username(), data);
    }

    @Override
    public ArrayList<UserData> listUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public UserData getUser(String username) {
        if(users.containsKey(username)){
            return users.get(username);
        }
        return null;
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void deleteAllUsers(){
        users.clear();
    }
}

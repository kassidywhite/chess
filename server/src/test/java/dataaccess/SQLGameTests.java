package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameTests {
    static GameDAO games = new SQLGameDAO();

    @BeforeEach
    void setUp() {
        games.deleteAllGames();
    }

    @Test
    void createNewGamePositive() throws DataAccessException {
        games.createNewGame("Queen's Gambit");
        assert !games.listGames().isEmpty();
    }

    @Test
    void createNewGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->{
            games.createNewGame(null);
        });
    }

    @Test
    void addGamePositive() throws DataAccessException {
        games.addGame(new GameData(3, null, null, "yuh", new ChessGame()));
        assert !games.listGames().isEmpty();
    }

    @Test
    void addGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->{
            games.addGame(new GameData(3, null, null, null, new ChessGame()));
        });
    }

    @Test
    void getGameByNamePositive() throws DataAccessException {
        games.createNewGame("kassidy");
        games.createNewGame("abbie");
        assert games.getGameByName("abbie") != null;
    }

    @Test
    void getGameByNameNegative() throws DataAccessException {
        games.createNewGame("peanut butter");
        games.createNewGame("jelly");
        assert games.getGameByName("abbie") == null;
    }

    @Test
    void getGameByIDPositive() throws DataAccessException {
        games.addGame(new GameData(1, null, null, "mhmmm", new ChessGame()));
        assert games.getGameByID(1) != null;
    }

    @Test
    void getGameByIDNegative() throws DataAccessException {
        games.addGame(new GameData(256, null, null, "im so close", new ChessGame()));
        assert games.getGameByID(0) == null;
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        games.createNewGame("destiny");
        games.createNewGame("I <3 Kaytranada");
        assert !games.listGames().isEmpty();
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        assert games.listGames().isEmpty();
    }

    @Test
    void deleteGamePositive() throws DataAccessException  {
        games.createNewGame("Kaytranada");
        games.createNewGame("Jamie XX");
        games.deleteGame("Kaytranada");
        assert games.listGames().size() == 1;
    }

    @Test
    void deleteGameNegative() throws DataAccessException  {
        games.createNewGame("im coding");
        games.deleteGame(null);
        assert games.listGames().size() == 1;
    }

    @Test
    void deleteAllGames() throws DataAccessException {
        games.createNewGame("salute");
        games.createNewGame("RUFUS DU SOL");
        games.createNewGame("Fred Again...");
        games.createNewGame("ur mom");
        games.deleteAllGames();
        assert games.listGames().isEmpty();
    }
}
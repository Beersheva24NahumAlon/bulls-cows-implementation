package telran.game.exceptions;

public class GamerCannotStartGameException extends IllegalStateException {

    public GamerCannotStartGameException(String username, long gameId) {
        super("Gamer %s can't start the game %d".formatted(username, gameId));
    }

}

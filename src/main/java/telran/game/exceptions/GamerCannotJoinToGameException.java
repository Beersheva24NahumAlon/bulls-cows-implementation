package telran.game.exceptions;

public class GamerCannotJoinToGameException extends IllegalStateException {

    public GamerCannotJoinToGameException(String username, long gameId) {
        super("Gamer %s can't join to the game %d!".formatted(username, gameId));
    }

}

package telran.game.exceptions;

import java.util.NoSuchElementException;

public class GamerNotInGameException extends NoSuchElementException{
    public GamerNotInGameException(String username, long gameId) {
        super("Gamer %s not participate in game %d".formatted(username, gameId));
    }
}

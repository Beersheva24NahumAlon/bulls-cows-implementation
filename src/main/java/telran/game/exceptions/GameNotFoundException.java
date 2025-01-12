package telran.game.exceptions;

import java.util.NoSuchElementException;

public class GameNotFoundException extends NoSuchElementException {
    public GameNotFoundException(long id) {
        super("Game with id %d is not found".formatted(id));
    }
}

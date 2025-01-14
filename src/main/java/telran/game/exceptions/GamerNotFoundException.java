package telran.game.exceptions;

import java.util.NoSuchElementException;

public class GamerNotFoundException extends NoSuchElementException {
    public GamerNotFoundException(String username) {
        super("Gamer with name %s is not found!".formatted(username));
    }
}

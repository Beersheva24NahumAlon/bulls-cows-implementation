package telran.game.exceptions;

public class GamerAlreadyExistsException extends IllegalStateException {

    public GamerAlreadyExistsException(String username) {
        super("Can't register gamer %s. Gamer with this username already exists".formatted(username));
    }

}

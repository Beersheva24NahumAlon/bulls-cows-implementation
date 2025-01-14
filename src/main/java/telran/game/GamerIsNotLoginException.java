package telran.game;

public class GamerIsNotLoginException extends IllegalStateException {
    public GamerIsNotLoginException() {
        super("Gamer didn't login to the server");
    }
}

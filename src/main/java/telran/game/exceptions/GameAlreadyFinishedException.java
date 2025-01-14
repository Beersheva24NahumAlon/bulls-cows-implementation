package telran.game.exceptions;

public class GameAlreadyFinishedException extends IllegalStateException {
    public GameAlreadyFinishedException(long gameId, String winner) {
        super("Gamer %s has already won the game %d!".formatted(winner, gameId));
    }
}

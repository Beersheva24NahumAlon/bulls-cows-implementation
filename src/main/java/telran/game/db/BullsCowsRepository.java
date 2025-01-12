package telran.game.db;

import java.time.LocalDate;
import java.util.List;
import telran.game.MoveResult;

public interface BullsCowsRepository {
    public boolean isGamerExists(String username);

    public void createGamer(String username, LocalDate birthDate);

    public long createGame(String sequence);

    public List<Long> findJoinebleGames(String username);

    public void createGameGamer(String username, long gameId); //join to game

    public List<Long> findStartebleGames(String username);

    public void setGameDateTime(long gameId); //start game

    public void createMove(String username, long gameId, String sequence, int bulls, int cows);

    public String findWinnerGame(long gameId);

    public void setWinnerAndFinishGame(String username, long gameId);

    public List<MoveResult> findAllMovesGameGamer(String username, long gameId);
}

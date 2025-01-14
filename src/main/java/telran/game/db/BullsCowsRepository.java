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

    public void setGameDateTime(String username, long gameId); //start game

    public List<Long> findPlaybleGames(String username);

    public void createMove(String username, long gameId, String sequence, int bulls, int cows);

    public void setWinnerAndFinishGame(String username, long gameId, String sequence, int bulls, int cows);

    public List<MoveResult> findAllMovesGameGamer(String username, long gameId);

    public String findSequence(long gameId);
}

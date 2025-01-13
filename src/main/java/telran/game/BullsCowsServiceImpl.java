package telran.game;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import telran.game.db.BullsCowsRepository;
import telran.game.db.jpa.BullsCowsRepositoryImpl;
import telran.game.exceptions.*;

public class BullsCowsServiceImpl implements BullsCowsService {
    private static final long N_DIGITS = 4;

    BullsCowsRepository repo = new BullsCowsRepositoryImpl();
    String username = "";

    @Override
    public void register(String username, LocalDate birthdate) {
        repo.createGamer(username, birthdate);
    }

    @Override
    public void login(String username) {
        if (!repo.isGamerExists(username)) {
            throw new GamerNotFoundException(username);
        }
        this.username = username;
    }

    @Override
    public long createGame() {
        return repo.createGame(generateSequence());
    }

    public String generateSequence() {
        return new Random().ints(0, 10).distinct().limit(N_DIGITS).boxed()
                .map(i -> i.toString()).collect(Collectors.joining());
    }

    @Override
    public List<Long> getListJoinebleGames(String username) {
        return repo.findJoinebleGames(username);
    }

    @Override
    public void joinToGame(String username, long gameId) {
        repo.createGameGamer(username, gameId);
    }

    @Override
    public List<Long> getListStartebleGames(String username) {
        return repo.findStartebleGames(username);
    }

    @Override
    public void startGame(String username, long gameId) {
        repo.setGameDateTime(gameId);
    }

    @Override
    public List<MoveResult> makeMove(String username, long gameId, String sequence) {
        if (repo.isGameFinished(gameId)) {
            String winner = repo.findWinnerGame(gameId);
            throw new GameAlreadyFinishedException(gameId, winner);
        }
        String gameSequence = repo.findSequence(gameId);
        MoveResult res = calculateMove(sequence, gameSequence);
        repo.createMove(username, gameId, sequence, res.bulls(), res.cows());
        if (res.bulls() == 4) {
            repo.setWinnerAndFinishGame(username, gameId);
        }
        return repo.findAllMovesGameGamer(username, gameId);
    }

    public MoveResult calculateMove(String sequence, String gameSequence) {
        int bulls = 0;
        int cows = 0;
        for (int i = 0; i < N_DIGITS; i++) {
            if (gameSequence.indexOf(sequence.charAt(i)) > -1) {
                cows++;
            }
            if (gameSequence.charAt(i) == sequence.charAt(i)) {
                bulls++;
            }
        }
        return new MoveResult(sequence, bulls, cows);
    }

}

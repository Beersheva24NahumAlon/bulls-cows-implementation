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

    @Override
    public void register(String username, LocalDate birthdate) {
        repo.createGamer(username, birthdate);
    }

    @Override
    public void login(String username) {
        if (!repo.isGamerExists(username)) {
            throw new GamerNotFoundException(username);
        }
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
        checkLogin(username);
        return repo.findJoinebleGames(username);
    }

    @Override
    public void joinToGame(String username, long gameId) {
        checkLogin(username);
        repo.createGameGamer(username, gameId);
    }

    @Override
    public List<Long> getListStartebleGames(String username) {
        checkLogin(username);
        return repo.findStartebleGames(username);
    }

    @Override
    public void startGame(String username, long gameId) {
        checkLogin(username);
        repo.setGameDateTime(username, gameId);
    }

    @Override
    public List<Long> getListPlaybleGames(String username) {
        checkLogin(username);
        return repo.findPlaybleGames(username);
    }

    @Override
    public List<MoveResult> makeMove(String username, long gameId, String sequence) {
        checkLogin(username);
        String gameSequence = repo.findSequence(gameId);
        MoveResult res = calculateMove(sequence, gameSequence);
        if (res.bulls() == 4) {
            repo.setWinnerAndFinishGame(username, gameId, sequence, res.bulls(), res.cows());
        } else {
            repo.createMove(username, gameId, sequence, res.bulls(), res.cows());
        }
        return repo.findAllMovesGameGamer(username, gameId);
    }

    public MoveResult calculateMove(String sequence, String gameSequence) {
        int bulls = 0;
        int cows = 0;
        for (int i = 0; i < N_DIGITS; i++) {
            if (gameSequence.charAt(i) == sequence.charAt(i)) {
                bulls++;
            } else {
                if (gameSequence.indexOf(sequence.charAt(i)) > -1) {
                    cows++;
                }
            }
        }
        return new MoveResult(sequence, bulls, cows);
    }

    private void checkLogin(String username) {
        if (username.equals("anonimus")) {
            throw new GamerIsNotLoginException();
        }
    }
}

package telran.game.db.jpa;

import java.time.*;
import java.util.*;
import org.hibernate.jpa.HibernatePersistenceProvider;
import jakarta.persistence.*;
import jakarta.persistence.spi.PersistenceUnitInfo;
import telran.game.MoveResult;
import telran.game.db.BullsCowsRepository;
import telran.game.db.jpa.config.BullsCowsPersistenceUnitInfo;
import telran.game.exceptions.*;

public class BullsCowsRepositoryImpl implements BullsCowsRepository {
    EntityManager em;

    public BullsCowsRepositoryImpl() {
        PersistenceUnitInfo persistenceUnitInfo = new BullsCowsPersistenceUnitInfo();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        EntityManagerFactory emf = provider.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
        em = emf.createEntityManager();
    }

    @Override
    public boolean isGamerExists(String username) {
        GamerEntity gamer = em.find(GamerEntity.class, username);
        return gamer != null;
    }

    @Override
    public void createGamer(String username, LocalDate birthDate) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            if (isGamerExists(username)) {
                throw new GamerAlreadyExistsException(username);
            }
            GamerEntity gamer = new GamerEntity(username, birthDate);
            em.persist(gamer);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public long createGame(String sequence) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            GameEntity game = new GameEntity(sequence, false);
            em.persist(game);
            transaction.commit();
            return game.getId();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<Long> findJoinebleGames(String username) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT id FROM GameEntity WHERE dateTime is null EXCEPT SELECT game.id FROM GameGamerEntity WHERE gamer.username = ?1",
                Long.class);
        query.setParameter(1, username);
        return query.getResultList();
    }

    @Override
    public void createGameGamer(String username, long gameId) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            GameEntity game = getGame(gameId);
            GamerEntity gamer = getGamer(username);
            checkJoinebleGame(username, gameId);
            GameGamerEntity gameGamer = new GameGamerEntity(game, gamer);
            em.persist(gameGamer);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<Long> findStartebleGames(String username) {
        TypedQuery<Long> query = em.createQuery(
                "select game.id from GameGamerEntity where gamer.username = ?1 and game.dateTime is null",
                Long.class);
        query.setParameter(1, username);
        return query.getResultList();
    }

    @Override
    public List<Long> findPlaybleGames(String username) {
        TypedQuery<Long> query = em.createQuery(
                "select game.id from GameGamerEntity where gamer.username = ?1 and game.dateTime is not null and not game.isFinished",
                Long.class);
        query.setParameter(1, username);
        return query.getResultList();
    }

    @Override //start game
    public void setGameDateTime(String username, long gameId) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            GameEntity game = getGame(gameId);
            LocalDateTime dateTime = LocalDateTime.now();
            checkStartebleGame(username, gameId);
            game.setDateTime(dateTime);
            em.persist(game);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void createMove(String username, long gameId, String sequence, int bulls, int cows) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            checkGameFinished(gameId);
            GameGamerEntity gameGamer = getGameGamer(username, gameId);
            MoveEntity move = new MoveEntity(gameGamer, bulls, cows, sequence);
            em.persist(move);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private String findWinnerGame(long gameId) {
        TypedQuery<String> query = em.createQuery(
                "select gamer.username from GameGamerEntity where game.id = ?1 and isWinner",
                String.class);
        query.setParameter(1, gameId);
        List<String> res = query.getResultList();
        return res.isEmpty() ? "" : res.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MoveResult> findAllMovesGameGamer(String username, long gameId) {
        Query query = em.createQuery(
                "select sequence, bulls, cows from MoveEntity where gameGamer.game.id = ?1 and gameGamer.gamer.username = ?2");
        query.setParameter(1, gameId);
        query.setParameter(2, username);
        List<Object[]> res = query.getResultList();
        return res.stream().map(arr -> new MoveResult((String) arr[0], (int) arr[1], (int) arr[2])).toList();
    }

    @Override
    public void setWinnerAndFinishGame(String username, long gameId, String sequence, int bulls, int cows) {
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            GameEntity game = checkGameFinished(gameId);
            GameGamerEntity gameGamer = getGameGamer(username, gameId);
            MoveEntity move = new MoveEntity(gameGamer, bulls, cows, sequence);
            em.persist(move);
            game.setIsFinished(true);
            em.persist(game);
            gameGamer.setWinner(true);
            em.persist(gameGamer);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public String findSequence(long gameId) {
        GameEntity game = getGame(gameId);
        return game.getSequence();
    }

    private GamerEntity getGamer(String username) {
        GamerEntity gamer = em.find(GamerEntity.class, username);
        if (gamer == null) {
            throw new GamerNotFoundException(username);
        }
        return gamer;
    }

    private GameEntity getGame(long gameId) {
        GameEntity game = em.find(GameEntity.class, gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }
        return game;
    }

    private GameGamerEntity getGameGamer(String username, long gameId) {
        TypedQuery<GameGamerEntity> query = em.createQuery(
                "select gameGamer from GameGamerEntity gameGamer where game.id = ?1 and gamer.username = ?2",
                GameGamerEntity.class);
        query.setParameter(1, gameId);
        query.setParameter(2, username);
        List<GameGamerEntity> res = query.getResultList();
        if (res.isEmpty()) {
            throw new GamerNotInGameException(username, gameId);
        }
        return res.get(0);
    }

    private void checkJoinebleGame(String username, long gameId) { 
        if (!findJoinebleGames(username).contains(gameId)) {
            throw new GamerCannotJoinToGameException(username, gameId);
        }  
    }

    private void checkStartebleGame(String username, long gameId) { 
        if (!findStartebleGames(username).contains(gameId)) {
            throw new GamerCannotStartGameException(username, gameId);
        }  
    }

    private GameEntity checkGameFinished(long gameId) {
        GameEntity game = getGame(gameId);
        if (game.isFinished()) {
            throw new GameAlreadyFinishedException(gameId, findWinnerGame(gameId));
        }
        return game;
    }

}

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
        if (isGamerExists(username)) {
            throw new GamerAlreadyExistsException(username);
        }
        var transaction = em.getTransaction();
        transaction.begin();
        try {
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
            GameEntity game = new GameEntity(sequence);
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
                "select game.id from GameGamerEntity where gamer.username != ?1 and game.dateTime is null",
                Long.class);
        query.setParameter(1, username);
        return query.getResultList();
    }

    @Override
    public void createGameGamer(String username, long gameId) {
        GameEntity game = getGame(gameId);
        GamerEntity gamer = getGamer(username);
        var transaction = em.getTransaction();
        transaction.begin();
        try {
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
    public void setGameDateTime(long gameId) {
        GameEntity game = getGame(gameId);
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            LocalDateTime dateTime = LocalDateTime.now();
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
        GameGamerEntity gameGamer = getGameGamer(username, gameId);
        var transaction = em.getTransaction();
        transaction.begin();
        try {
            MoveEntity move = new MoveEntity(gameGamer, bulls, cows, sequence);
            em.persist(move);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public String findWinnerGame(long gameId) {
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
    public void setWinnerAndFinishGame(String username, long gameId) {
        GameEntity game = getGame(gameId);
        GameGamerEntity gameGamer = getGameGamer(username, gameId);
        var transaction = em.getTransaction();
        transaction.begin();
        try {
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

    @Override
    public boolean isGameFinished(long gameId) {
        GameEntity game = getGame(gameId);
        return game.isFinished();
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
                "select gameGamer from GameGamerEntity gameGamer where game_id = ?1 and gamer_id = ?2",
                GameGamerEntity.class);
        query.setParameter(1, gameId);
        query.setParameter(2, username);
        List<GameGamerEntity> res = query.getResultList();
        if (res.isEmpty()) {
            throw new GamerNotInGameException(username, gameId);
        }
        return res.get(0);
    }
}

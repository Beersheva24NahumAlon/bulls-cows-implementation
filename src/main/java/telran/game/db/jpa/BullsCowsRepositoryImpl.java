package telran.game.db.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.hibernate.jpa.HibernatePersistenceProvider;
import jakarta.persistence.*;
import jakarta.persistence.spi.PersistenceUnitInfo;
import telran.game.MoveResult;
import telran.game.db.BullsCowsRepository;
import telran.game.exceptions.GameNotFoundException;
import telran.game.exceptions.GamerNotFoundException;

public class BullsCowsRepositoryImpl implements BullsCowsRepository {
    EntityManager em;

    public BullsCowsRepositoryImpl(PersistenceUnitInfo persistenceUnitInfo, HashMap<String, Object> properties) {
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
        //TODO checking
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
        //TODO checking
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findJoinebleGames'");
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

    @Override
    public List<Long> findStartebleGames(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findStartebleGames'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findWinnerGame'");
    }

    @Override
    public List<MoveResult> findAllMovesGameGamer(String username, long gameId) {
        //TODO checking
        Query query = em.createQuery("select sequence, bulls, cows from MoveEntity move where game_gamer_id = (slect id from GameGamer where game_id = ?1 and gamer_id = ?2)", 
                MoveEntity.class);
        query.setParameter(1, gameId);
        query.setParameter(2, username);
        return null; //query.getResultList().stream().map(arr -> new MoveResult(arr[0], arr[1], arr[2])).toList();
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

    private GameGamerEntity getGameGamer(String username, long gameId) {
        //TODO checking
        TypedQuery<GameGamerEntity> query = em.createQuery(
                "select gameGamer from GameGamerEntity gameGamer where game_id = ?1 and gamer_id = ?2", 
                        GameGamerEntity.class);
        query.setParameter(1, gameId);
        query.setParameter(2, username);
        GameGamerEntity gameGamer = query.getResultList().get(0);
        return gameGamer;
    }

}

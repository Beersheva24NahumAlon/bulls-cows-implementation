package telran.game;

import org.junit.jupiter.api.Test;

public class BullsCowsServiceTest {
    BullsCowsServiceImpl service = new BullsCowsServiceImpl();

    @Test
    void generateSequenceTest() {
        System.out.println(service.generateSequence());
    }

    @Test
    void calculateMoveTest() {
        String sequence = service.generateSequence();
        String gameSequence = service.generateSequence();
        MoveResult result = service.calculateMove(sequence, gameSequence);
        System.out.println(gameSequence);
        System.out.println(sequence);
        System.out.println("bulls: %d cows: %d".formatted(result.bulls(), result.cows()));
    }

    @Test
    void getListJoinebleGamesTest() {
        System.out.println(service.getListJoinebleGames("gamer1").toString());
        
    }

    @Test
    void getListStartebleGamesTest() {
        System.out.println(service.getListStartebleGames("gamer1").toString());
        
    }

    @Test
    void findAllMovesGameGamerTest() {
        System.out.println(service.repo.findAllMovesGameGamer("gamer9", 1031).toString());
        
    }
}

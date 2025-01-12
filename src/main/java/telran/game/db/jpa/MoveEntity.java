package telran.game.db.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "move")
public class MoveEntity {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "game_gamer_id")
    private GameGamerEntity gameGamer;
    private int bulls;
    private int cows;
    private String sequence;

    @Override
    public String toString() {
        return "Move [id=" + id + ", gameGamer=" + gameGamer + ", bulls=" + bulls + ", cows=" + cows + ", sequence="
                + sequence + "]";
    }

    public MoveEntity(GameGamerEntity gameGamer, int bulls, int cows, String sequence) {
        this.gameGamer = gameGamer;
        this.bulls = bulls;
        this.cows = cows;
        this.sequence = sequence;
    }

    
}

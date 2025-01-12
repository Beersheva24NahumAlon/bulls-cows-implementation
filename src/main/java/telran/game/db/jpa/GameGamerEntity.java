package telran.game.db.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "game_gamer")
public class GameGamerEntity {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;
    @ManyToOne
    @JoinColumn(name = "gamer_id")
    private GamerEntity gamer;
    @Column(name = "is_winner")
    private boolean isWinner;
    
    @Override
    public String toString() {
        return "GameGamer [id=" + id + ", game=" + game + ", gamer=" + gamer + ", isWinner=" + isWinner + "]";
    }

    public GameGamerEntity(GameEntity game, GamerEntity gamer) {
        this.game = game;
        this.gamer = gamer;
    }

    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    
}

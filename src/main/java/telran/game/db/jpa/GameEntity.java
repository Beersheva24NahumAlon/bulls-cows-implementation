package telran.game.db.jpa;

import java.time.*;
import jakarta.persistence.*;

@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "is_finished")
    private boolean isFinished;
    private String sequence;

    @Override
    public String toString() {
        return "Game [id=" + id + ", dateTime=" + dateTime + ", isFinished=" + isFinished + ", sequence=" + sequence
                + "]";
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public long getId() {
        return id;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public GameEntity(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public GameEntity() {
    }
}

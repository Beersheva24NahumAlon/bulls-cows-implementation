package telran.game.db.jpa;

import java.time.*;
import jakarta.persistence.*;

@Entity
@Table(name = "gamer")
public class GamerEntity {
    @Id
    private String username;
    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Override
    public String toString() {
        return "Gamer [username=" + username + ", birthDate=" + birthDate + "]";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public GamerEntity(String username, LocalDate birthDate) {
        this.username = username;
        this.birthDate = birthDate;
    }

    public GamerEntity() {
    }    
}

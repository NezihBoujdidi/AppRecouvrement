package tn.enicar.apprecouvrementback.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Phase")
public class Phase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int delai;

    @Enumerated(EnumType.STRING)
    private MoyenRelance moyenRelance;

    // Getters and Setters

    public Phase(Long id, int delai, MoyenRelance moyenRelance) {
        this.id = id;
        this.delai = delai;
        this.moyenRelance = moyenRelance;
    }
}
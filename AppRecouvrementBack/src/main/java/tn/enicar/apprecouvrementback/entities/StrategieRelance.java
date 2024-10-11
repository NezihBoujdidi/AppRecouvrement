package tn.enicar.apprecouvrementback.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
@Table(name = "StrategieRelance")
public class StrategieRelance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phase> phases;


    public StrategieRelance(Long id, String nom, List<Phase> phases) {
        this.id = id;
        this.nom = nom;
        this.phases = phases;
    }
}

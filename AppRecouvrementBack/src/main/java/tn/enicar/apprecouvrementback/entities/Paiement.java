package tn.enicar.apprecouvrementback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Paiements")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paiementID;

    private double montantPaye;
    private String libelle;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL)
    private List<PaiementSpecifique> paiementsSpecifiques = new ArrayList<>();


    public Paiement(double montantPaye, String libelle, LocalDate datePaiement, Client client) {
        this.montantPaye = montantPaye;
        this.libelle = libelle;
        this.datePaiement = datePaiement;
        this.client = client;
    }


    public void addPaiementSpecifique(PaiementSpecifique paiementSpecifique) {
        paiementsSpecifiques.add(paiementSpecifique);
        paiementSpecifique.setPaiement(this);
    }
}

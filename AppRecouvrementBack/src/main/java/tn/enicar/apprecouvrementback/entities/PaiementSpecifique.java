package tn.enicar.apprecouvrementback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "PaiementSpf")
public class PaiementSpecifique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double montantPaye;


    @OneToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;


    public PaiementSpecifique(double montantPaye, Facture facture, Paiement paiement) {
        this.montantPaye = montantPaye;
        this.facture = facture;
        this.paiement = paiement;
    }
}

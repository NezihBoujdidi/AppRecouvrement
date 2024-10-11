package tn.enicar.apprecouvrementback.entities;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int factureID;

    @Column(unique = true)
    private String referenceFacture;

    private String libelle;

    @Temporal(TemporalType.DATE)
    private Date dateFacture;

    @Temporal(TemporalType.DATE)
    private Date dateEcheance;

    private double montantNominal;
    private double montantOuvert;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonBackReference
    @JoinColumn(name = "client_id")
    private Client client;


//    @OneToOne(mappedBy = "facture", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private PaiementSpecifique paiementSpecifique;

    // Constructors

    public Facture(String referenceFacture, String libelle, Date dateFacture, Date dateEcheance, double montantNominal, double montantOuvert, Client client) {
        this.referenceFacture= referenceFacture;
        this.libelle = libelle;
        this.dateFacture = dateFacture;
        this.dateEcheance = dateEcheance;
        this.montantNominal = montantNominal;
        this.montantOuvert = montantOuvert;
        this.client = client;
    }
}
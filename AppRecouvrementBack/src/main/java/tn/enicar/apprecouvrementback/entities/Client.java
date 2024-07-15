package tn.enicar.apprecouvrementback.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int clientId;

    private String nom;
    private String prenom;
    private String adresse;
    private String numTel;
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Facture> factures;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Paiement> paiements;

    // Constructor with fields
    public Client(String nom, String prenom, String adresse, String numTel, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.numTel = numTel;
        this.email = email;
    }

    public Integer getClientId() {
        return this.clientId;
    }
}


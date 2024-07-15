package tn.enicar.apprecouvrementback.services;

import tn.enicar.apprecouvrementback.entities.Facture;
import tn.enicar.apprecouvrementback.entities.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.entities.PaiementSpecifique;
import tn.enicar.apprecouvrementback.repositories.FactureRepository;
import tn.enicar.apprecouvrementback.repositories.PaiementRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {
    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;

    @Autowired
    public PaiementService(PaiementRepository paiementRepository, FactureRepository factureRepository) {
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
    }

    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    public Optional<Paiement> getPaiementById(int paiementID) {
        return paiementRepository.findById(paiementID);
    }

    public Paiement savePaiement(Paiement paiement) {
        // Ensure bidirectional relationship is set for paiementsSpecifiques
        for (PaiementSpecifique ps : paiement.getPaiementsSpecifiques()) {
            ps.setPaiement(paiement);

            // Fetch the associated Facture and update montantOuvert
            Facture facture = factureRepository.findById(ps.getFacture().getFactureID())
                    .orElseThrow(() -> new RuntimeException("Facture not found"));
            facture.setMontantOuvert(facture.getMontantOuvert() - ps.getMontantPaye());
            factureRepository.save(facture);
        }

        return paiementRepository.save(paiement);
    }

    public Paiement updatePaiement(int paiementID, Paiement paiementDetails) {
        return paiementRepository.findById(paiementID).map(paiement -> {
            paiement.setMontantPaye(paiementDetails.getMontantPaye());
            paiement.setLibelle(paiementDetails.getLibelle());
            paiement.setDatePaiement(paiementDetails.getDatePaiement());
            paiement.setClient(paiementDetails.getClient());
            paiement.setPaiementsSpecifiques(paiementDetails.getPaiementsSpecifiques());

            // Ensure bidirectional relationship is set for paiementsSpecifiques
            for (PaiementSpecifique ps : paiement.getPaiementsSpecifiques()) {
                ps.setPaiement(paiement);

                // Fetch the associated Facture and update montantOuvert
                Facture facture = factureRepository.findById(ps.getFacture().getFactureID())
                        .orElseThrow(() -> new RuntimeException("Facture not found"));
                facture.setMontantOuvert(facture.getMontantOuvert() - ps.getMontantPaye());
                factureRepository.save(facture);
            }

            return paiementRepository.save(paiement);
        }).orElseGet(() -> {
            paiementDetails.setPaiementID(paiementID);
            return paiementRepository.save(paiementDetails);
        });
    }

    public void deletePaiement(int paiementID) {
        paiementRepository.deleteById(paiementID);
    }

    public List<Paiement> getPaiementByClientId(int clientId) {
        return paiementRepository.findByClientClientId(clientId);
    }
}

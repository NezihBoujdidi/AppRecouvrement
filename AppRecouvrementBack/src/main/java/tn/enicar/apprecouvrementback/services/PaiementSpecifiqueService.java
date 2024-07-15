package tn.enicar.apprecouvrementback.services;

import tn.enicar.apprecouvrementback.entities.PaiementSpecifique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.repositories.PaiementSpecifiqueRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementSpecifiqueService {
    private final PaiementSpecifiqueRepository paiementSpecifiqueRepository;

    @Autowired
    public PaiementSpecifiqueService(PaiementSpecifiqueRepository paiementSpecifiqueRepository) {
        this.paiementSpecifiqueRepository = paiementSpecifiqueRepository;
    }

    public List<PaiementSpecifique> getAllPaiementsSpecifiques() {
        return paiementSpecifiqueRepository.findAll();
    }

    public Optional<PaiementSpecifique> getPaiementSpecifiqueById(int id) {
        return paiementSpecifiqueRepository.findById(id);
    }

    public PaiementSpecifique savePaiementSpecifique(PaiementSpecifique paiementSpecifique) {
        return paiementSpecifiqueRepository.save(paiementSpecifique);
    }

    public PaiementSpecifique updatePaiementSpecifique(int id, PaiementSpecifique paiementSpecifiqueDetails) {
        return paiementSpecifiqueRepository.findById(id).map(paiementSpecifique -> {
            paiementSpecifique.setMontantPaye(paiementSpecifiqueDetails.getMontantPaye());
            paiementSpecifique.setFacture(paiementSpecifiqueDetails.getFacture());
            paiementSpecifique.setPaiement(paiementSpecifiqueDetails.getPaiement());
            return paiementSpecifiqueRepository.save(paiementSpecifique);
        }).orElseGet(() -> {
            paiementSpecifiqueDetails.setId(id);
            return paiementSpecifiqueRepository.save(paiementSpecifiqueDetails);
        });
    }

    public void deletePaiementSpecifique(int id) {
        paiementSpecifiqueRepository.deleteById(id);
    }

    public List<PaiementSpecifique> getPaiementSpecifiqueByPaiementID(int paiementID) {
        return paiementSpecifiqueRepository.findByPaiement_PaiementID(paiementID);
    }
}

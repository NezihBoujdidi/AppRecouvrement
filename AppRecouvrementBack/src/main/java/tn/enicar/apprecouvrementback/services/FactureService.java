package tn.enicar.apprecouvrementback.services;

import tn.enicar.apprecouvrementback.entities.Client;
import tn.enicar.apprecouvrementback.entities.Facture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.repositories.FactureRepository;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FactureService {
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository; // Assuming you have a ClientRepository

    @Autowired
    public FactureService(FactureRepository factureRepository, ClientRepository clientRepository) {
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> getFactureById(int factureID) {
        return factureRepository.findById(factureID);
    }

    public Facture saveFacture(Facture facture) {
        // Ensure client is attached to facture if it exists in DB
        if (facture.getClient() != null && facture.getClient().getClientId() != null) {
            Client client = clientRepository.findById(facture.getClient().getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            facture.setClient(client);
        }
        return factureRepository.save(facture);
    }

    public Facture updateFacture(int factureID, Facture factureDetails) {
        return factureRepository.findById(factureID).map(facture -> {
            facture.setLibelle(factureDetails.getLibelle());
            facture.setDateFacture(factureDetails.getDateFacture());
            facture.setDateEcheance(factureDetails.getDateEcheance());
            facture.setMontantNominal(factureDetails.getMontantNominal());
            facture.setMontantOuvert(factureDetails.getMontantOuvert());

            // Ensure client is attached to facture if it exists in DB
            if (factureDetails.getClient() != null && factureDetails.getClient().getClientId() != null) {
                Client client = clientRepository.findById(factureDetails.getClient().getClientId())
                        .orElseThrow(() -> new RuntimeException("Client not found"));
                facture.setClient(client);
            }

            return factureRepository.save(facture);
        }).orElseGet(() -> {
            factureDetails.setFactureID(factureID);
            return factureRepository.save(factureDetails);
        });
    }

    public void deleteFacture(int factureID) {
        factureRepository.deleteById(factureID);
    }

    public List<Facture> getAllFacturesByClientId(int clientId) {
        return factureRepository.findByClient_ClientId(clientId);
    }
}

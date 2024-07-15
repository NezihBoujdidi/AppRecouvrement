package tn.enicar.apprecouvrementback.services;

import tn.enicar.apprecouvrementback.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.entities.Facture;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(int clientId) {
        return clientRepository.findById(clientId);
    }

    public Client saveClient(Client client) {
        if (client.getFactures() != null) {
            for (Facture facture : client.getFactures()) {
                facture.setClient(client);
            }
        }
        return clientRepository.save(client);
    }

    public Client updateClient(int clientId, Client clientDetails) {
        return clientRepository.findById(clientId).map(client -> {
            client.setNom(clientDetails.getNom());
            client.setPrenom(clientDetails.getPrenom());
            client.setAdresse(clientDetails.getAdresse());
            client.setNumTel(clientDetails.getNumTel());
            client.setEmail(clientDetails.getEmail());
            return clientRepository.save(client);
        }).orElseGet(() -> {
            clientDetails.setClientId(clientId);
            return clientRepository.save(clientDetails);
        });
    }

    public void deleteClient(int clientId) {
        clientRepository.deleteById(clientId);
    }
}

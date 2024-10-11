package tn.enicar.apprecouvrementback.services;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.entities.Facture;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

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

    @Transactional
    public List<Client> saveClientsFromCsv(MultipartFile file) throws IOException {
        List<Client> newClients = new ArrayList<>();
        List<Client> existingClients = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                String referenceClient  = record.get("ReferenceClient");
                String nom = record.get("nom");
                String prenom = record.get("prenom");
                String adresse = record.get("adresse");

                // Stop processing if both "nom" and "prenom" are null or empty
                if ((referenceClient== null || nom.trim().isEmpty()) && (nom == null || nom.trim().isEmpty()) && (prenom == null || prenom.trim().isEmpty())) {
                    break;
                }

                // Check if a client with the same nom, prenom, and adresse already exists
                Optional<Client> existingClient = clientRepository.findByNomAndPrenomAndAdresse(nom, prenom, adresse);
                if (existingClient.isPresent()) {
                    // Client already exists, add to existingClients list
                    existingClients.add(existingClient.get());
                    continue;
                }

                Client client = new Client();
                client.setReferenceClient(referenceClient);
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setAdresse(adresse);
                client.setEmail(record.get("email"));
                client.setNumTel(record.get("numTel"));

                newClients.add(client);
            }
        }
        clientRepository.saveAll(newClients);
        return existingClients;
    }

    public Client updateClient(int clientId, Client clientDetails) {
        return clientRepository.findById(clientId).map(client -> {
            client.setReferenceClient(clientDetails.getReferenceClient());
            client.setNom(clientDetails.getNom());
            client.setPrenom(clientDetails.getPrenom());
            client.setAdresse(clientDetails.getAdresse());
            client.setNumTel(clientDetails.getNumTel());
            client.setEmail(clientDetails.getEmail());
            client.setStrategieRelance(clientDetails.getStrategieRelance());
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

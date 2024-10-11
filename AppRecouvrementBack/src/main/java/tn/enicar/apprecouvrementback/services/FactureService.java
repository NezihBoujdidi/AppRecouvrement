package tn.enicar.apprecouvrementback.services;

import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Client;
import tn.enicar.apprecouvrementback.entities.Facture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.repositories.FactureRepository;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

//    @Transactional
//    public void saveFacturesFromCsv(MultipartFile file) throws IOException {
//        List<Facture> factures = new ArrayList<>();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        List<Integer> missingClientIds = new ArrayList<>();
//
//        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
//            for (CSVRecord record : records) {
//                Facture facture = new Facture();
//                facture.setLibelle(record.get("libelle"));
//                facture.setDateFacture(parseDate(record.get("dateFacture"), formatter));
//                facture.setDateEcheance(parseDate(record.get("dateEcheance"), formatter));
//                facture.setMontantNominal(Double.parseDouble(record.get("MontantNominal")));
//                facture.setMontantOuvert(Double.parseDouble(record.get("MontantOuvert")));
//
//                int clientId = Integer.parseInt(record.get("clientID"));
//                Client client = clientRepository.findById(clientId).orElse(null);
//                if (client == null) {
//                    missingClientIds.add(clientId);
//                } else {
//                    facture.setClient(client);
//                    factures.add(facture);
//                }
//            }
//        }
//
//        if (!missingClientIds.isEmpty()) {
//            throw new RuntimeException("Client IDs not found: " + missingClientIds);
//        }
//
//        factureRepository.saveAll(factures);
//    }

    @Transactional
    public Map<String, Object> saveFacturesFromCsv(MultipartFile file) throws IOException {
        List<Facture> facturesToSave = new ArrayList<>();
        List<String> missingClientRefs = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                String referenceFacture = record.get("referenceFacture");
                String libelle = record.get("libelle");

                if ((referenceFacture == null || referenceFacture.trim().isEmpty()) && (libelle == null || libelle.trim().isEmpty()) ) {
                    break;
                }

                Date dateFacture = parseDate(record.get("dateFacture"), formatter);
                Date dateEcheance = parseDate(record.get("dateEcheance"), formatter);
                Double montantNominal = Double.parseDouble(record.get("MontantNominal"));
                Double montantOuvert = Double.parseDouble(record.get("MontantOuvert"));
                String referenceClient = record.get("referenceClient");

                Client client = clientRepository.findByReferenceClient(referenceClient).orElse(null);
                if (client == null) {
                    missingClientRefs.add(referenceClient);
                    continue; // Skip this record if the client does not exist
                }

                // ***Check if the facture already exists***
                Facture existingFacture = factureRepository.findByReferenceFactureAndLibelle(referenceFacture, libelle);
                if (existingFacture != null) {
                    // ***Update the existing facture***
                    existingFacture.setDateEcheance(dateEcheance);
                    existingFacture.setMontantOuvert(montantOuvert);
                    facturesToSave.add(existingFacture);
                } else {
                    // Create a new facture
                    Facture facture = new Facture();
                    facture.setReferenceFacture(referenceFacture);
                    facture.setLibelle(libelle);
                    facture.setDateFacture(dateFacture);
                    facture.setDateEcheance(dateEcheance);
                    facture.setMontantNominal(montantNominal);
                    facture.setMontantOuvert(montantOuvert);
                    facture.setClient(client);
                    facturesToSave.add(facture);
                }
            }
        }

        // Save valid factures to the database
        factureRepository.saveAll(facturesToSave);

        // Prepare the result map
        Map<String, Object> result = new HashMap<>();
        result.put("missingClientRefs", missingClientRefs);
        result.put("savedFactures", facturesToSave.size());

        return result;
    }

    private Date parseDate(String dateStr, SimpleDateFormat formatter) {
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            System.err.println("Failed to parse date: " + dateStr);
            throw new RuntimeException("Failed to parse date: " + dateStr, e);
        }
    }

    public Facture updateFacture(int factureID, Facture factureDetails) {
        return factureRepository.findById(factureID).map(facture -> {
            facture.setReferenceFacture(factureDetails.getReferenceFacture());
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

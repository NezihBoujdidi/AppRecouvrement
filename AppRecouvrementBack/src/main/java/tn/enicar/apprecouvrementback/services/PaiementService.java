package tn.enicar.apprecouvrementback.services;

import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Client;
import tn.enicar.apprecouvrementback.entities.Facture;
import tn.enicar.apprecouvrementback.entities.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.entities.PaiementSpecifique;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;
import tn.enicar.apprecouvrementback.repositories.FactureRepository;
import tn.enicar.apprecouvrementback.repositories.PaiementRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
public class PaiementService {
    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public PaiementService(PaiementRepository paiementRepository, FactureRepository factureRepository, ClientRepository clientRepository) {
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
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

//    @Transactional
//    public Map<String, Object> savePaiementsFromCsv(MultipartFile file) throws IOException {
//        List<Paiement> paiementsToSave = new ArrayList<>();
//        List<String> missingClientRefs = new ArrayList<>();
//        List<String> missingFactureRefs = new ArrayList<>();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
//            Map<String, Paiement> paiementMap = new HashMap<>();
//
//            for (CSVRecord record : records) {
//                String montantPayeTotalStr = record.get("montantPayeTotal");
//                String libelle = record.get("libelle");
//                String datePaiementStr = record.get("datePaiement");
//                String referenceClient = record.get("referenceClient");
//                String referenceFacture = record.get("referenceFacture");
//                String montantPayeStr = record.get("montantPaye");
//
//                if (montantPayeTotalStr.isEmpty() || datePaiementStr.isEmpty() || referenceClient.isEmpty()) {
//                    continue; // Skip incomplete records
//                }
//
//                double montantPayeTotal = Double.parseDouble(montantPayeTotalStr);
//                Date datePaiement = formatter.parse(datePaiementStr);
//
//                Client client = clientRepository.findByReferenceClient(referenceClient).orElse(null);
//                if (client == null) {
//                    missingClientRefs.add(referenceClient);
//                    continue; // Skip this record if the client does not exist
//                }
//
//                String key = montantPayeTotal + libelle + datePaiementStr + referenceClient;
//                Paiement paiement = paiementMap.getOrDefault(key, new Paiement(montantPayeTotal, libelle, datePaiement.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), client));
//                paiementMap.putIfAbsent(key, paiement);
//
//                if (referenceFacture == null || referenceFacture.isEmpty() && (montantPayeStr == null || montantPayeStr.isEmpty())) {
//                    List<Facture> factures = factureRepository.findByClientOrderByDateEcheanceAsc(client);
//                    for (Facture facture : factures) {
//                        double montantOuvert = facture.getMontantOuvert();
//                        if (montantPayeTotal > 0) {
//                            double montantPaye = Math.min(montantOuvert, montantPayeTotal);
//                            montantPayeTotal -= montantPaye;
//
//                            PaiementSpecifique paiementSpecifique = new PaiementSpecifique(montantPaye, facture, paiement);
//                            paiement.addPaiementSpecifique(paiementSpecifique);
//
//                            facture.setMontantOuvert(montantOuvert - montantPaye);
//                            factureRepository.save(facture);
//                        } else {
//                            break;
//                        }
//                    }
//                } else {
//                    Facture facture = factureRepository.findByReferenceFacture(referenceFacture);
//                    if (facture == null) {
//                        missingFactureRefs.add(referenceFacture);
//                        continue; // Skip this record if the facture does not exist
//                    }
//                    if (!facture.getClient().getReferenceClient().equals(referenceClient)) {
//                        missingFactureRefs.add(referenceFacture); // Facture does not belong to the client
//                        continue; // Skip this record
//                    }
//
//                    double montantPaye = Double.parseDouble(montantPayeStr);
//                    PaiementSpecifique paiementSpecifique = new PaiementSpecifique(montantPaye, facture, paiement);
//                    paiement.addPaiementSpecifique(paiementSpecifique);
//
//                    facture.setMontantOuvert(facture.getMontantOuvert() - montantPaye);
//                    factureRepository.save(facture);
//                }
//            }
//
//            paiementsToSave.addAll(
//                    paiementMap.values().stream()
//                            .filter(paiement -> !paiement.getPaiementsSpecifiques().isEmpty())
//                            .toList()
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error processing CSV file", e);
//        }
//
//        // Save valid paiements to the database
//        paiementRepository.saveAll(paiementsToSave);
//
//        // Prepare the result map
//        Map<String, Object> result = new HashMap<>();
//        result.put("missingClientRefs", missingClientRefs);
//        result.put("missingFactureRefs", missingFactureRefs);
//        result.put("savedPaiements", paiementsToSave.size());
//
//        return result;
//    }
@Transactional
public Map<String, Object> savePaiementsFromCsv(MultipartFile file) throws IOException {
    List<Paiement> paiementsToSave = new ArrayList<>();
    List<String> missingClientRefs = new ArrayList<>();
    List<String> missingFactureRefs = new ArrayList<>();
    Map<String, Double> remainingAmounts = new HashMap<>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        Map<String, Paiement> paiementMap = new HashMap<>();

        for (CSVRecord record : records) {
            String montantPayeTotalStr = record.get("montantPayeTotal");
            String libelle = record.get("libelle");
            String datePaiementStr = record.get("datePaiement");
            String referenceClient = record.get("referenceClient");
            String referenceFacture = record.get("referenceFacture");
            String montantPayeStr = record.get("montantPaye");

            if (montantPayeTotalStr.isEmpty() || datePaiementStr.isEmpty() || referenceClient.isEmpty()) {
                continue; // Skip incomplete records
            }

            double montantPayeTotal = Double.parseDouble(montantPayeTotalStr);
            Date datePaiement = formatter.parse(datePaiementStr);

            Client client = clientRepository.findByReferenceClient(referenceClient).orElse(null);
            if (client == null) {
                missingClientRefs.add(referenceClient);
                continue; // Skip this record if the client does not exist
            }

            String key = montantPayeTotal + libelle + datePaiementStr + referenceClient;
            Paiement paiement = paiementMap.getOrDefault(key, new Paiement(montantPayeTotal, libelle, datePaiement.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), client));
            paiementMap.putIfAbsent(key, paiement);

            if ((referenceFacture == null || referenceFacture.isEmpty()) && (montantPayeStr == null || montantPayeStr.isEmpty())) {
                List<Facture> factures = factureRepository.findByClientOrderByDateEcheanceAsc(client);
                for (Facture facture : factures) {
                    double montantOuvert = facture.getMontantOuvert();
                    if (montantPayeTotal > 0) {
                        double montantPaye = Math.min(montantOuvert, montantPayeTotal);
                        montantPayeTotal -= montantPaye;

                        PaiementSpecifique paiementSpecifique = new PaiementSpecifique(montantPaye, facture, paiement);
                        paiement.addPaiementSpecifique(paiementSpecifique);

                        facture.setMontantOuvert(montantOuvert - montantPaye);
                        factureRepository.save(facture);
                    } else {
                        break;
                    }
                }
            } else {
                Facture facture = factureRepository.findByReferenceFacture(referenceFacture);
                if (facture == null) {
                    missingFactureRefs.add(referenceFacture);
                    continue; // Skip this record if the facture does not exist
                }
                if (!facture.getClient().getReferenceClient().equals(referenceClient)) {
                    missingFactureRefs.add(referenceFacture); // Facture does not belong to the client
                    continue; // Skip this record
                }

                double montantPaye = Double.parseDouble(montantPayeStr);
                PaiementSpecifique paiementSpecifique = new PaiementSpecifique(montantPaye, facture, paiement);
                paiement.addPaiementSpecifique(paiementSpecifique);

                facture.setMontantOuvert(facture.getMontantOuvert() - montantPaye);
                factureRepository.save(facture);
            }
        }

        paiementsToSave.addAll(
                paiementMap.values().stream()
                        .filter(p -> !p.getPaiementsSpecifiques().isEmpty())
                        .toList()
        );

        // Calculate remaining amount and update the Factures if needed
        for (Paiement paiement : paiementsToSave) {
            double sumMontantPaye = paiement.getPaiementsSpecifiques().stream()
                    .mapToDouble(PaiementSpecifique::getMontantPaye)
                    .sum();

            double remainingMontantPayeTotal = paiement.getMontantPaye() - sumMontantPaye;

            if (remainingMontantPayeTotal > 0) {
                List<Facture> remainingFactures = factureRepository.findByClientOrderByDateEcheanceAsc(paiement.getClient());

                for (Facture remainingFacture : remainingFactures) {
                    double montantOuvert = remainingFacture.getMontantOuvert();
                    if (remainingMontantPayeTotal > 0 && montantOuvert > 0) {
                        double montantPaye = Math.min(montantOuvert, remainingMontantPayeTotal);
                        remainingMontantPayeTotal -= montantPaye;

                        PaiementSpecifique paiementSpecifique = new PaiementSpecifique(montantPaye, remainingFacture, paiement);
                        paiement.addPaiementSpecifique(paiementSpecifique);

                        remainingFacture.setMontantOuvert(montantOuvert - montantPaye);
                        factureRepository.save(remainingFacture);
                    }
                }

                if (remainingMontantPayeTotal > 0) {
                    remainingAmounts.put(paiement.getClient().getReferenceClient(), remainingMontantPayeTotal);
                }
            }
        }

        // Save valid paiements to the database
        paiementRepository.saveAll(paiementsToSave);

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error processing CSV file", e);
    }

    // Prepare the result map
    Map<String, Object> result = new HashMap<>();
    result.put("missingClientRefs", missingClientRefs);
    result.put("missingFactureRefs", missingFactureRefs);
    result.put("savedPaiements", paiementsToSave.size());
    result.put("remainingAmounts", remainingAmounts);

    return result;
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

        paiementRepository.findById(paiementID).ifPresent(paiement -> {
            // Revert the montantOuvert for each Facture associated with this Paiement
            for (PaiementSpecifique ps : paiement.getPaiementsSpecifiques()) {
                Facture facture = ps.getFacture();
                if (facture != null) {
                    facture.setMontantOuvert(facture.getMontantOuvert() + ps.getMontantPaye());
                    factureRepository.save(facture);
                }
            }
            paiementRepository.delete(paiement);
        });
    }

    public List<Paiement> getPaiementByClientId(int clientId) {
        return paiementRepository.findByClientClientId(clientId);
    }
}

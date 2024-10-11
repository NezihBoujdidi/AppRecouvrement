package tn.enicar.apprecouvrementback.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.enicar.apprecouvrementback.entities.*;
import tn.enicar.apprecouvrementback.repositories.ClientRepository;
import tn.enicar.apprecouvrementback.repositories.StrategieRelanceRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StrategieRelanceService {

    @Autowired
    private StrategieRelanceRepository strategieRelanceRepository;
    @Autowired
    private ClientRepository clientRepository;

    private static final Logger logger = LoggerFactory.getLogger(StrategieRelanceService.class);
    @Autowired
    private ClientService clientService;

    // Create or update a StrategieRelance
    public StrategieRelance saveStrategieRelance(StrategieRelance strategieRelance) {
        logger.debug("Saving StrategieRelance: {}", strategieRelance);
        StrategieRelance savedStrategie = strategieRelanceRepository.save(strategieRelance);
        logger.debug("StrategieRelance saved: {}", savedStrategie);
        return savedStrategie;
    }

    // Find a StrategieRelance by ID
    public Optional<StrategieRelance> findById(Long id) {
        return strategieRelanceRepository.findById(id);
    }

    // Find all StrategieRelances
    public List<StrategieRelance> findAll() {
        return strategieRelanceRepository.findAll();
    }
    public Optional<StrategieRelance> getStrategieRelanceByClientId(int clientId) {
        // Fetch the client from the client repository
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (clientOptional.isEmpty()) {
            logger.warn("Client with ID {} not found", clientId);
            return Optional.empty(); // Client not found, return empty
        }

        Client client = clientOptional.get();

        String strategieName = client.getStrategieRelance(); // Assuming this returns the name of the strategy
        Optional<StrategieRelance> strategieRelanceOptional = strategieRelanceRepository.findByNom(strategieName);

        if (strategieRelanceOptional.isEmpty()) {
            logger.warn("StrategieRelance with name {} not found for Client with ID {}", strategieName, clientId);
            return Optional.empty(); // StrategieRelance not found, return empty
        }

        logger.info("Found StrategieRelance for Client with ID {}: {}", clientId, strategieRelanceOptional.get());
        return strategieRelanceOptional; // Return the StrategieRelance
    }


    // Update a StrategieRelance by ID
    public StrategieRelance updateStrategieRelance(Long id, StrategieRelance updatedStrategie) {
        logger.debug("updating StrategieRelance with ID: " + id);
        return strategieRelanceRepository.findById(id)
                .map(strategie -> {
                    strategie.setNom(updatedStrategie.getNom());
                    strategie.setPhases(updatedStrategie.getPhases());
                    return strategieRelanceRepository.save(strategie);
                })
                .orElseThrow(() -> new RuntimeException("StrategieRelance not found with id " + id));
    }

    public MoyenRelance determineMoyenRelance(Client client) {
        // Fetch the strategieRelance based on the string stored in Client
        Optional<StrategieRelance> strategieOptional = strategieRelanceRepository.findByNom(client.getStrategieRelance());

        if (strategieOptional.isEmpty()) {
            return null; // No strategy assigned
        }

        StrategieRelance strategie = strategieOptional.get();
        if (strategie.getPhases() == null || strategie.getPhases().isEmpty()) {
            return null; // No phases in the strategy, no relance
        }

        // Get the oldest dateEcheance from the client's factures
        LocalDate oldestEcheance = client.getFactures().stream()
                .map(facture -> {
                    Date dateEcheance = facture.getDateEcheance(); // Get the java.util.Date
                    if (dateEcheance instanceof java.sql.Date) {
                        return ((java.sql.Date) dateEcheance).toLocalDate(); // Directly convert to LocalDate
                    } else {
                        // Handle other types of Date, if necessary
                        return dateEcheance.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(); // Convert Date to LocalDate
                    }
                })
                .min(Comparator.naturalOrder()) // Find the oldest date
                .orElse(null);

        if (oldestEcheance == null) {
            return null; // No factures available for relance
        }

        // Calculate the days overdue
        long daysOverdue = LocalDate.now().toEpochDay() - oldestEcheance.toEpochDay();

        MoyenRelance finalMoyenRelance = null; // Variable to hold the best matching moyenRelance

        for (Phase phase : strategie.getPhases()) {
            // Check if the days overdue meet the criteria for the current phase
            if (daysOverdue >= phase.getDelai()) {
                finalMoyenRelance = phase.getMoyenRelance(); // Update to the current phase's moyenRelance
            }
        }
        client.setMoyenRelanceActuel(finalMoyenRelance); // Optionally update the client's current moyenRelance
        clientService.saveClient(client);
// After checking all phases, return the most appropriate moyenRelance or null if none matched
        return finalMoyenRelance;

    }


    // Delete a StrategieRelance by ID
    public void deleteById(Long id) {
        strategieRelanceRepository.deleteById(id);
    }

    // Delete all StrategieRelances
    public void deleteAll() {
        strategieRelanceRepository.deleteAll();
    }
}

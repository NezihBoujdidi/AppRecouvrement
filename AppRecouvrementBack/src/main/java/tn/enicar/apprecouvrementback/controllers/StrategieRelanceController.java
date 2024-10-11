package tn.enicar.apprecouvrementback.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.entities.Client;
import tn.enicar.apprecouvrementback.entities.MoyenRelance;
import tn.enicar.apprecouvrementback.entities.StrategieRelance;
import tn.enicar.apprecouvrementback.services.ClientService;
import tn.enicar.apprecouvrementback.services.StrategieRelanceService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/strategies")
public class StrategieRelanceController {

    @Autowired
    private StrategieRelanceService strategieRelanceService;

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(StrategieRelanceController.class);


    // Create a new StrategieRelance
    @PostMapping
    public ResponseEntity<StrategieRelance> createStrategieRelance(@RequestBody StrategieRelance strategieRelance) {
        logger.info("Received request to create StrategieRelance: {}", strategieRelance);
        StrategieRelance savedStrategie = strategieRelanceService.saveStrategieRelance(strategieRelance);
        logger.info("Saved StrategieRelance: {}", savedStrategie);
        return new ResponseEntity<>(savedStrategie, HttpStatus.CREATED);
    }

    // Get a StrategieRelance by ID
    @GetMapping("/{id}")
    public ResponseEntity<StrategieRelance> getStrategieRelanceById(@PathVariable Long id) {
        Optional<StrategieRelance> strategieRelance = strategieRelanceService.findById(id);
        return strategieRelance.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get all StrategieRelances
    @GetMapping
    public ResponseEntity<List<StrategieRelance>> getAllStrategieRelances() {
        List<StrategieRelance> strategieRelances = strategieRelanceService.findAll();
        return new ResponseEntity<>(strategieRelances, HttpStatus.OK);
    }

    // Update an existing StrategieRelance
    @PutMapping("/{id}")
    public ResponseEntity<StrategieRelance> updateStrategieRelance(@PathVariable Long id,
                                                                   @RequestBody StrategieRelance strategieRelance) {
        try {
            StrategieRelance updatedStrategie = strategieRelanceService.updateStrategieRelance(id, strategieRelance);
            return new ResponseEntity<>(updatedStrategie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<StrategieRelance> findStrategieByClientId(@PathVariable int clientId) {
        logger.info("Received request to find StrategieRelance for Client ID: {}", clientId);

        // Use the service to get StrategieRelance by client ID
        Optional<StrategieRelance> strategieRelanceOptional = strategieRelanceService.getStrategieRelanceByClientId(clientId);

        if (strategieRelanceOptional.isPresent()) {
            logger.info("StrategieRelance found for Client ID: {}", clientId);
            return new ResponseEntity<>(strategieRelanceOptional.get(), HttpStatus.OK);
        } else {
            logger.warn("StrategieRelance not found for Client ID: {}", clientId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/relance-methode/{clientId}")
    public ResponseEntity<MoyenRelance> getRelanceMethodeForClient(@PathVariable int clientId) {
        // Fetch the client by ID
        Optional<Client> clientOptional = clientService.getClientById(clientId);

        if (clientOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Client not found
        }

        Client client = clientOptional.get();

        // Logic to determine the relance method based on the client's strategy and factures
        MoyenRelance moyenRelance = strategieRelanceService.determineMoyenRelance(client);

        if (moyenRelance == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No relance method found
        }

        return new ResponseEntity<>(moyenRelance, HttpStatus.OK); // Return the relance method
    }

    // Delete a StrategieRelance by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrategieRelance(@PathVariable Long id) {
        if (!strategieRelanceService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        strategieRelanceService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete all StrategieRelances
    @DeleteMapping
    public ResponseEntity<Void> deleteAllStrategieRelances() {
        strategieRelanceService.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

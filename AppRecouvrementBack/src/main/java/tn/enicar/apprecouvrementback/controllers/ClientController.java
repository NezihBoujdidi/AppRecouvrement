package tn.enicar.apprecouvrementback.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.ClientService;
import tn.enicar.apprecouvrementback.services.LogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;
    private final LogService logService;

    @Autowired
    public ClientController(ClientService clientService, LogService logService) {
        this.clientService = clientService;
        this.logService= logService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable int clientId) {
        return clientService.getClientById(clientId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.saveClient(client);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, String>> uploadClients(@RequestParam("file") MultipartFile file) {
//        Map<String, String> response = new HashMap<>();
//        try {
//            clientService.saveClientsFromCsv(file);
//            response.put("message", "File uploaded successfully.");
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            response.put("message", "Failed to upload file.");
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
//        }
//    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadClients(@RequestParam("file") MultipartFile file) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Client> existingClients = clientService.saveClientsFromCsv(file);
            response.put("message", "File uploaded successfully.");
            response.put("existingClients", existingClients);
            logService.logAction("Upload Clients", "SUCCESS", null, System.currentTimeMillis() - startTime, "Uploaded " + existingClients.size() + " clients.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "Failed to upload file.");
            response.put("error", e.getMessage()); // Add error message to response
            logService.logAction("Upload Clients", "ERROR", e.getMessage(), System.currentTimeMillis() - startTime, "Failed upload.");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(@PathVariable int clientId, @RequestBody Client clientDetails) {
        return ResponseEntity.ok(clientService.updateClient(clientId, clientDetails));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable int clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }
}

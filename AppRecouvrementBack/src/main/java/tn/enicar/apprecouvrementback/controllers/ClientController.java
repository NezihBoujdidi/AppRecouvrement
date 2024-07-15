package tn.enicar.apprecouvrementback.controllers;

import tn.enicar.apprecouvrementback.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
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

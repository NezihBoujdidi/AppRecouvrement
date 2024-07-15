package tn.enicar.apprecouvrementback.controllers;

import tn.enicar.apprecouvrementback.entities.Facture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.FactureService;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FactureController {
    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    public List<Facture> getAllFactures() {
        return factureService.getAllFactures();
    }

    @GetMapping("/{factureID}")
    public ResponseEntity<Facture> getFactureById(@PathVariable int factureID) {
        return factureService.getFactureById(factureID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Facture createFacture(@RequestBody Facture facture) {
        return factureService.saveFacture(facture);
    }

    @PutMapping("/{factureID}")
    public ResponseEntity<Facture> updateFacture(@PathVariable int factureID, @RequestBody Facture factureDetails) {
        return ResponseEntity.ok(factureService.updateFacture(factureID, factureDetails));
    }

    @DeleteMapping("/{factureID}")
    public ResponseEntity<Void> deleteFacture(@PathVariable int factureID) {
        factureService.deleteFacture(factureID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public List<Facture> getAllFacturesByClientId(@PathVariable int clientId) {
        return factureService.getAllFacturesByClientId(clientId);
    }
}

package tn.enicar.apprecouvrementback.controllers;

import tn.enicar.apprecouvrementback.entities.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.PaiementService;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {
    private final PaiementService paiementService;

    @Autowired
    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    public List<Paiement> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    @GetMapping("/{paiementID}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable int paiementID) {
        return paiementService.getPaiementById(paiementID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Paiement createPaiement(@RequestBody Paiement paiement) {
        return paiementService.savePaiement(paiement);
    }

    @PutMapping("/{paiementID}")
    public ResponseEntity<Paiement> updatePaiement(@PathVariable int paiementID, @RequestBody Paiement paiementDetails) {
        return ResponseEntity.ok(paiementService.updatePaiement(paiementID, paiementDetails));
    }

    @DeleteMapping("/{paiementID}")
    public ResponseEntity<Void> deletePaiement(@PathVariable int paiementID) {
        paiementService.deletePaiement(paiementID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public List<Paiement> getPaiementByClientId(@PathVariable int clientId) {
        return paiementService.getPaiementByClientId(clientId);
    }
}


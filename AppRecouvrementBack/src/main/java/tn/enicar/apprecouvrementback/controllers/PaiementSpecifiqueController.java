package tn.enicar.apprecouvrementback.controllers;

import tn.enicar.apprecouvrementback.entities.PaiementSpecifique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.PaiementSpecifiqueService;

import java.util.List;

@RestController
@RequestMapping("/api/paiementsSpecifiques")
public class PaiementSpecifiqueController {
    private final PaiementSpecifiqueService paiementSpecifiqueService;

    @Autowired
    public PaiementSpecifiqueController(PaiementSpecifiqueService paiementSpecifiqueService) {
        this.paiementSpecifiqueService = paiementSpecifiqueService;
    }

    @GetMapping
    public List<PaiementSpecifique> getAllPaiementsSpecifiques() {
        return paiementSpecifiqueService.getAllPaiementsSpecifiques();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementSpecifique> getPaiementSpecifiqueById(@PathVariable int id) {
        return paiementSpecifiqueService.getPaiementSpecifiqueById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/paiement/{paiementID}")
    public List<PaiementSpecifique> getPaiementSpecifiqueByPaiementID(@PathVariable int paiementID) {
        return paiementSpecifiqueService.getPaiementSpecifiqueByPaiementID(paiementID);
    }

    @PostMapping
    public PaiementSpecifique createPaiementSpecifique(@RequestBody PaiementSpecifique paiementSpecifique) {
        return paiementSpecifiqueService.savePaiementSpecifique(paiementSpecifique);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaiementSpecifique> updatePaiementSpecifique(@PathVariable int id, @RequestBody PaiementSpecifique paiementSpecifiqueDetails) {
        return ResponseEntity.ok(paiementSpecifiqueService.updatePaiementSpecifique(id, paiementSpecifiqueDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiementSpecifique(@PathVariable int id) {
        paiementSpecifiqueService.deletePaiementSpecifique(id);
        return ResponseEntity.noContent().build();
    }
}

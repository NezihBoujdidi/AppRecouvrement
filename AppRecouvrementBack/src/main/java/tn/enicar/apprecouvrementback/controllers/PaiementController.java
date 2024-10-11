package tn.enicar.apprecouvrementback.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.LogService;
import tn.enicar.apprecouvrementback.services.PaiementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {
    private final PaiementService paiementService;
    private final LogService logService;

    @Autowired
    public PaiementController(PaiementService paiementService, LogService logService) {

        this.paiementService = paiementService;
        this.logService = logService;
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPaiements(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> result = paiementService.savePaiementsFromCsv(file);
            response.put("message", "File uploaded successfully.");
            response.put("missingClients", result.get("missingClientRefs"));
            response.put("missingFactures", result.get("missingFactureRefs"));
            response.put("savedPaiements", result.get("savedPaiements"));
            response.put("remainingAmounts", result.get("remainingAmounts"));
            logService.logAction("Upload Paiements", "SUCCESS", null, System.currentTimeMillis() - startTime, "Uploaded " + result.get("savedPaiements") + " paiements.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            logService.logAction("Upload Paiements", "ERROR", e.getMessage(), System.currentTimeMillis() - startTime, "Failed upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while uploading paiements.");
            logService.logAction("Upload Paiements", "ERROR", e.getMessage(), System.currentTimeMillis() - startTime, "Failed upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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


package tn.enicar.apprecouvrementback.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.apprecouvrementback.entities.Facture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.apprecouvrementback.services.FactureService;
import tn.enicar.apprecouvrementback.services.LogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
public class FactureController {
    private final FactureService factureService;
    private final LogService logService;

    @Autowired
    public FactureController(FactureService factureService, LogService logService) {

        this.factureService = factureService;
        this.logService = logService;
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFactures(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> result = factureService.saveFacturesFromCsv(file);
            response.put("message", "File uploaded successfully.");
            response.put("missingClients", result.get("missingClientRefs"));
            response.put("savedFactures", result.get("savedFactures"));
            logService.logAction("Upload Factures", "SUCCESS", null, System.currentTimeMillis() - startTime, "Uploaded " + result.get("savedFactures") + " factures.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            catch (RuntimeException e) {
              response.put("message", e.getMessage());
                logService.logAction("Upload Factures", "ERROR", e.getMessage(), System.currentTimeMillis() - startTime, "Failed upload.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
            } catch (Exception e) {
            response.put("message", "An error occurred while uploading factures.");
            logService.logAction("Upload Factures", "ERROR", e.getMessage(), System.currentTimeMillis() - startTime, "Failed upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred while uploading factures."));
//        }
//            catch (Exception e) {
//                response.put("message", "Failed to upload file.");
//                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
//            }
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

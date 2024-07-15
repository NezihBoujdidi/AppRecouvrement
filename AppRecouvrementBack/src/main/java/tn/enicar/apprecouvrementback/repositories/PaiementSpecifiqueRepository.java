package tn.enicar.apprecouvrementback.repositories;

import tn.enicar.apprecouvrementback.entities.PaiementSpecifique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementSpecifiqueRepository extends JpaRepository<PaiementSpecifique, Integer> {
    List<PaiementSpecifique> findByPaiement_PaiementID(int paiementId);
}

package tn.enicar.apprecouvrementback.repositories;

import tn.enicar.apprecouvrementback.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    List<Paiement> findByClientClientId(int clientId);
}


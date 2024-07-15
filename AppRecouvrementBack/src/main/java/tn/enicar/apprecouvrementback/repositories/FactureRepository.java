package tn.enicar.apprecouvrementback.repositories;

import tn.enicar.apprecouvrementback.entities.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer> {
    List<Facture> findByClient_ClientId(int clientId);
}

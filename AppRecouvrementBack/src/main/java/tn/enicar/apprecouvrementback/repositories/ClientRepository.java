package tn.enicar.apprecouvrementback.repositories;

import tn.enicar.apprecouvrementback.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByNomAndPrenomAndAdresse(String nom, String prenom, String adresse);

    Optional<Client> findByReferenceClient(String referenceClient);
}

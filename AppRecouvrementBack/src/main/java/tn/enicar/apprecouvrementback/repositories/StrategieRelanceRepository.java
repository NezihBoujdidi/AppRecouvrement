package tn.enicar.apprecouvrementback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.apprecouvrementback.entities.StrategieRelance;

import java.util.Optional;

public interface StrategieRelanceRepository extends JpaRepository<StrategieRelance, Long> {
        Optional<StrategieRelance> findByNom(String nom);
}

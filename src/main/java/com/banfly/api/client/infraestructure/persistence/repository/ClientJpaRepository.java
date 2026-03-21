package com.banfly.api.client.infraestructure.persistence.repository;

import com.banfly.api.client.infraestructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByIdentificationNumber(String identificationNumber);
    Optional<ClientEntity> findByEmail(String email);

   // @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.client.id = :clientId")
    //boolean hasProducts(@Param("clientId") Long clientId);

}

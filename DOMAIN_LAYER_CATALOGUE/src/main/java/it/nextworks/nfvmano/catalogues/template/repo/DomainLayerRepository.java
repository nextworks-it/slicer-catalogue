package it.nextworks.nfvmano.catalogues.template.repo;

import it.nextworks.nfvmano.catalogue.template.elements.DomainLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainLayerRepository extends JpaRepository<DomainLayer, Long>{
	Optional<DomainLayer> findById(Long id);
	Optional<DomainLayer> findByNameAndDescription(String name, String description);
}


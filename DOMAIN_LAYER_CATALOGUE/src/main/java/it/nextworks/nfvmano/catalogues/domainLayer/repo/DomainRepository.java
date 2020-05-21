package it.nextworks.nfvmano.catalogues.domainLayer.repo;

import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long>{
	Optional<Domain> findById(Long id);
	Optional<Domain> findByDomainId(String domainId);
	List<Domain> findAll();
}


package it.nextworks.nfvmano.catalogues.template.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.springframework.stereotype.Repository;

@Repository
public interface NsTemplateRepository extends JpaRepository<NST, Long>{
	Optional<NST> findByNstId(String id);
	Optional<NST> findByNstNameAndNstVersion(String name, String version);
}

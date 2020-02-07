package it.nextworks.nfvmano.catalogues.template.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface NsTemplateInfoRepository extends JpaRepository<NsTemplateInfo, UUID>{
	Optional<NsTemplateInfo> findByNsTemplateId(String id);
	Optional<NsTemplateInfo> findByNameAndNsTemplateVersion(String name, String version);
}

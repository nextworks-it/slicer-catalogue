package it.nextworks.nfvmano.catalogues.template.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;

public interface NsTemplateInfoRepository extends JpaRepository<NsTemplateInfo, Long>{
	Optional<NsTemplateInfo> findByNsTemplateId(String id);
	Optional<NsTemplateInfo> findByNameAndNsTemplateVersion(String name, String version);
}

package it.nextworks.nfvmano.catalogues.template.repo;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.libs.ifa.templates.nsst.NSST;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NsstRepository extends JpaRepository<NSST, UUID> {
    Optional<NSST> findByNsstName(String name);
    Optional<NSST> findByNsstId(String nsstId);
}

package it.nextworks.nfvmano.catalogues.template.services;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsstRequest;
import it.nextworks.nfvmano.catalogues.template.repo.NsstRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.templates.nsst.NSST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NsstCatalogueService {

    private static final Logger log = LoggerFactory.getLogger(NsstCatalogueService.class);

    @Autowired
    private NsstRepository nsstRepository;


    public synchronized String onBoardNsst(OnBoardNsstRequest request)
            throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
        log.debug("Processing request to on board a new NSST");
        request.isValid();
        String nsstId = storeNsst(request.getNsst());
        return nsstId;
    }

    private synchronized String storeNsst(NSST nsst) throws AlreadyExistingEntityException, MalformattedElementException {
        String nsstName = nsst.getNsstName();
        log.debug("On boarding NSST with name " + nsstName);
        if (nsstRepository.findByNsstName(nsstName).isPresent()) {
            String logErrorStr= "NSST with name " + nsstName +" already present in DB.";
            log.error(logErrorStr);
            throw new AlreadyExistingEntityException(logErrorStr);
        }
        nsstRepository.saveAndFlush(nsst);
        String nstTargetID =String.valueOf(nsst.getUuid());
        nsst.setNsstId(nstTargetID);
        nsstRepository.saveAndFlush(nsst);
        log.debug("Added NSST with ID " + nstTargetID);
        return nstTargetID;
    }


    public synchronized NSST getNsstById(String nsstId) throws NotExistingEntityException {
        NsTemplateInfo nstInfo;
        Optional<NSST> nsst=nsstRepository.findByNsstId(nsstId);
        if(!nsst.isPresent()){
            throw new NotExistingEntityException("NSST with ID " + nsstId + " not found in DB.");
        }else{
            return nsst.get();
        }
    }

    public synchronized void deleteNsst(String nsstId) throws MalformattedElementException, NotExistingEntityException {
        log.debug("Processing request to delete a nsst with ID " + nsstId);
        if (nsstId == null) throw new MalformattedElementException("The nsst ID is null");

        Optional<NSST> nsst=nsstRepository.findByNsstId(nsstId);
        if(!nsst.isPresent()){
            throw new NotExistingEntityException("NSST with ID " + nsstId + " not found in DB.");
        }else{
            nsstRepository.delete(nsst.get());
            log.info("NSST with ID "+nsstId+" has been deleted.");
        }
    }
}

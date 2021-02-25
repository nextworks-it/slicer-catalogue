package it.nextworks.nfvmano.catalogues.domainLayer.services;


import it.nextworks.nfvmano.catalogue.domainLayer.*;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.SubscriptionRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DomainCatalogueSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(DomainCatalogueSubscriptionService.class);

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private RestTemplate restTemplate;

    public DomainCatalogueSubscriptionService() {
        this.restTemplate= new RestTemplate(new BufferingClientHttpRequestFactory(
                new SimpleClientHttpRequestFactory()
        ));
    }

    public List<DomainCatalogueSubscription> getDomainCatalogueSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public DomainCatalogueSubscription getDomainCatalogueSubscription(String subscriptionId) throws NotExistingEntityException {
        Optional<DomainCatalogueSubscription> subscriptionOptional = subscriptionRepository.findBySubscriptionId(subscriptionId);
        if(!subscriptionOptional.isPresent())
            throw new NotExistingEntityException(String.format("Domain Catalogue Subscription with Id %s not found", subscriptionId));
        log.debug("{}", subscriptionOptional.get().toString());
        return subscriptionOptional.get();
    }

    public String subscribe(DomainCatalogueSubscriptionRequest subscriptionRequest) throws FailedOperationException{
        String subscriptionId = UUID.randomUUID().toString();
        log.info("Creating new Domain Catalogue Subscription with Id {}", subscriptionId);
        DomainCatalogueSubscription subscription = new DomainCatalogueSubscription();
        subscription.setSubscriptionId(subscriptionId);
        subscription.setSubscriptionType(subscriptionRequest.getSubscriptionType());
        subscription.setCallbackURI(subscriptionRequest.getCallbackURI());
        if(subscriptionRequest.getSubscriptionType().equals(SubscriptionType.DOMAIN_SUBSCRIPTION))
            subscriptionRepository.saveAndFlush(subscription);
        else
            throw new FailedOperationException("Subscription type not supported");
        log.info("Domain Catalogue Subscription with Id {} created and stored", subscription.getSubscriptionId());
        log.debug("{}", subscription.toString());
        return subscriptionId;
    }

    public void unsubscribe(String subscriptionId) throws NotExistingEntityException {
        Optional<DomainCatalogueSubscription> subscription = subscriptionRepository.findBySubscriptionId(subscriptionId);
        if(!subscription.isPresent())
            throw new NotExistingEntityException(String.format("Domain Catalogue Subscription with Id %s not found", subscriptionId));
        subscriptionRepository.delete(subscription.get());
        log.info("Deleted Domain Catalogue Subscription with Id {}", subscriptionId);
    }

    public void notify(DomainCatalogueSubscriptionNotification msg) {
        if(msg.getType().equals(NotificationType.DOMAIN_ONBOARDING))
            log.info("Notifying domain on-boarding with Id {}", msg.getDomain().getDomainId());
        else
            log.info("Notifying domain removal with Id {}", msg.getDomain().getDomainId());
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json");
        HttpEntity<?> entity = new HttpEntity<>(msg, header);
        List<DomainCatalogueSubscription> subscriptions = subscriptionRepository.findAll();
        for(DomainCatalogueSubscription subscription : subscriptions){
            log.debug("Sending HTTP request to {}", subscription.getCallbackURI());
            try {
                ResponseEntity<?> httpResponse = restTemplate.exchange(subscription.getCallbackURI(), HttpMethod.POST, entity, DomainCatalogueSubscriptionNotification.class);
                HttpStatus code = httpResponse.getStatusCode();
                if (code.equals(HttpStatus.OK))
                    log.debug("Notification sent correctly");
            } catch (HttpClientErrorException e) {
                log.error("Error sending notification to {} : Client error", subscription.getCallbackURI());
            } catch (HttpServerErrorException e) {
                log.error("Error sending notification to {} :  Server error", subscription.getCallbackURI());
            } catch (UnknownHttpStatusCodeException e) {
                log.error("Error sending notification to {} : Unknown error", subscription.getCallbackURI());
            }catch (Exception e){
                log.error("Generic Error while sending notification to {} : Client error", subscription.getCallbackURI());
            }
        }
    }
}
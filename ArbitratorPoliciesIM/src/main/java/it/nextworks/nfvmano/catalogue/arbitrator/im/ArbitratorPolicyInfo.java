package it.nextworks.nfvmano.catalogue.arbitrator.im;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.Date;

@Entity
public class ArbitratorPolicyInfo {


    @Id
    @GeneratedValue
    @JsonIgnore
    public Long id;

    private String arbitratorPolicyInfoId;

    private int timesUsed;

    private Date lastUpdate;

    private String trainedModelFilePath;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)

    @JoinColumn(name="arbitrator_policy_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ArbitratorPolicy arbitratorPolicy;

    public ArbitratorPolicyInfo() {
    }

    public ArbitratorPolicyInfo(String arbitratorPolicyInfoId, int timesUsed, Date lastUpdate, String trainedModelFilePath, ArbitratorPolicy arbitratorPolicy) {
        this.arbitratorPolicyInfoId = arbitratorPolicyInfoId;
        this.timesUsed = timesUsed;
        this.lastUpdate = lastUpdate;
        this.trainedModelFilePath = trainedModelFilePath;
        this.arbitratorPolicy= arbitratorPolicy;
    }


    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTrainedModelFilePath() {
        return trainedModelFilePath;
    }

    public void setTrainedModelFilePath(String trainedModelFilePath) {
        this.trainedModelFilePath = trainedModelFilePath;
    }

    public String getArbitratorPolicyInfoId() {
        return arbitratorPolicyInfoId;
    }

    public ArbitratorPolicy getArbitratorPolicy() {
        return arbitratorPolicy;
    }



    public boolean trainedFileUpdateRequired(){
        if(trainedModelFilePath==null)
            return true;
        if(arbitratorPolicy.getPolicyUpdateStrategy().equals(PolicyUpdateStrategy.ALWAYS)){
            return true;
        }else if(arbitratorPolicy.getPolicyUpdateStrategy().equals(PolicyUpdateStrategy.EVERY_N_EXECUTIONS)
            && Integer.parseInt(this.arbitratorPolicy.getPolicyUpdateStrategyParams().get(PolicyUpdateStrategyParams.EXECUTION_TIMES))>=timesUsed){
            return true;
        }else if(arbitratorPolicy.isDefault()&&timesUsed==0){
            return true;
        }
        return false;
    }
}

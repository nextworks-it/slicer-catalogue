package it.nextworks.nfvmano.catalogue.blueprint.elements;

import javax.persistence.Embeddable;

@Embeddable
public class KpiThreshold {

    private int upperBound;
    private int lowerBound;

    public KpiThreshold() {
    }


    public KpiThreshold(int upperBound, int lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }


    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }
}

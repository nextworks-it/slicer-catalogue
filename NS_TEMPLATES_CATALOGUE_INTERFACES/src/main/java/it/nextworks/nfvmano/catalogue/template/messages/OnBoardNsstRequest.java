package it.nextworks.nfvmano.catalogue.template.messages;

import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.templates.nsst.NSST;

public class OnBoardNsstRequest implements InterfaceMessage {
    private NSST nsst;

    public OnBoardNsstRequest() { }

    public OnBoardNsstRequest(NSST nst) {
        this.nsst = nst;
    }


    @Override
    public void isValid() throws MalformattedElementException {
        if (nsst == null) throw new MalformattedElementException("On board NSST request without NSST");
        else nsst.isValid();
    }

    public NSST getNsst() {
        return nsst;
    }

    public void setNsst(NSST nst) {
        this.nsst = nst;
    }
}

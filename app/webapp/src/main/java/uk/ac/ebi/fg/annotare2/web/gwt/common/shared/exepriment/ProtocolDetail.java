package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProtocolDetail implements IsSerializable{

    private ProtocolType protocolType;

    private String protocolDescription;

    public void ProtocolDetail(ProtocolType _protocolType, String _protocolDescription)
    {
        this.protocolType = _protocolType;
        this.protocolDescription = _protocolDescription;
    }

    public void setProtocolType(ProtocolType _protocolType)
    {
        this.protocolType = _protocolType;
    }

    public ProtocolType getProtocolType()
    {
        return this.protocolType;
    }

    public void setProtocolDescription(String _protocolDescription)
    {
        this.protocolDescription = _protocolDescription;
    }

    public String getProtocolDescription()
    {
        return this.protocolDescription;
    }
}
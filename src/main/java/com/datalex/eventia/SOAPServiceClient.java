package com.datalex.eventia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class SOAPServiceClient extends WebServiceGatewaySupport {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @SuppressWarnings("unchecked")
    public <RQ, RS> RS send(RQ requestPayload, Class<RS> responseType, String serviceName) {
        return (RS) webServiceTemplate.marshalSendAndReceive(requestPayload);
    }


}
/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.otrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OtrsConnector {
    private final URL otrsUrl;
    private final String otrsUser;
    private final String otrsPass;

    private final static String URI_NS_OTRS_CORE = "Core";

    private final Logger log = LoggerFactory.getLogger(getClass());

    public OtrsConnector(String otrsUrl, String otrsUser, String otrsPass)
            throws MalformedURLException {
        super();
        this.otrsUrl = new URL(otrsUrl);
        this.otrsUser = otrsUser;
        this.otrsPass = otrsPass;
    }

    public SOAPMessage dispatchCall(String object, String method)
            throws SOAPException, IOException {
        return this.dispatchCall(object, method, new HashMap<String, Object>());
    }

    private SOAPMessage buildRPCMessage() throws SOAPException {
        // Create message
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();

        // Object for message parts
        SOAPPart sp = msg.getSOAPPart();

        SOAPEnvelope env = sp.getEnvelope();
        env.addNamespaceDeclaration("xsd",      XMLConstants.W3C_XML_SCHEMA_NS_URI);
        env.addNamespaceDeclaration("xsi",      XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        env.addNamespaceDeclaration("soapenc",  SOAPConstants.URI_NS_SOAP_ENCODING);
        env.addNamespaceDeclaration("env",      SOAPConstants.URI_NS_SOAP_ENVELOPE);
        env.addNamespaceDeclaration("core",     URI_NS_OTRS_CORE);
        env.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);

        SOAPBody body = env.getBody();
        //body.addNamespaceDeclaration("core", "Core");
        SOAPBodyElement dispatch = body.addBodyElement(new QName("", "Dispatch", "core"));
        dispatch.addChildElement("Username").addTextNode(otrsUser)
                .setAttribute("xsi:type", "xsd:string");
        dispatch.addChildElement("Password").addTextNode(otrsPass)
                .setAttribute("xsi:type", "xsd:string");

        return msg;
    }

    private SOAPElement getDispatchBodyElement(SOAPMessage msg)
            throws SOAPException {
        return (SOAPElement) msg.getSOAPBody()
                .getChildElements(new QName("", "Dispatch", "core")).next();
    }

    private SOAPMessage prepareSOAPMessage(String object, String method,
                                           Map<String, Object> params) throws SOAPException {
        SOAPMessage msg = this.buildRPCMessage();
        SOAPElement dispatch = this.getDispatchBodyElement(msg);

        dispatch.addChildElement("Object").addTextNode(object)
                .setAttribute("xsi:type", "xsd:string");
        dispatch.addChildElement("Method").addTextNode(method)
                .setAttribute("xsi:type", "xsd:string");

        String[] keys = params.keySet().toArray(new String[params.keySet().size()]);

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];

            dispatch.addChildElement("Param" + i + "_Name").addTextNode(key)
                    .setAttribute("xsi:type", "xsd:string");

            // Check if we need an array
            if (params.get(key).getClass().isArray()) {
                SOAPElement element = new SoapArrayFactory().createSoapArray(
                        "Param" + i + "_Value", Arrays.asList(params.get(key)));
                dispatch.addChildElement(element);
                // collections will also be encoded as array
            } else if (params.get(key) instanceof Collection<?>) {
                SOAPElement element = new SoapArrayFactory()
                        .createSoapArray("Param" + i + "_Value",
                                (Collection<?>) params.get(key));
                dispatch.addChildElement(element);
                // maps need special encoding as well
            } else if (params.get(key) instanceof Map<?, ?>) {
                SOAPElement element = new SoapMapFactory().createSoapMap("Param" + i + "_Value", (Map<?, ?>) params.get(key));
                dispatch.addChildElement(element);
                // or a simple node
            } else {
                String xsdType = XSDTypeConverter.simpleTypeForObject(params.get(key));
                String value = params.get(key).toString();
                dispatch.addChildElement("Param" + i + "_Value")
                        .addTextNode(value).setAttribute("xsi:type", xsdType);
            }
        }
        return msg;
    }

    public SOAPMessage dispatchCall(String object, String method,
                                    Map<String, Object> params) throws SOAPException, IOException {
        SOAPMessage msg = prepareSOAPMessage(object, method, params);

        logMessage(msg);

        SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
        SOAPConnection conn = scf.createConnection();
        SOAPMessage answerMsg = conn.call(msg, otrsUrl);

        logMessage(answerMsg);
        return answerMsg;
    }

    private void logMessage(SOAPMessage msg) throws IOException,
            SOAPException {
        if (log.isDebugEnabled()) {
            // This will write the request to log
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            msg.writeTo(outputStream);
            byte[] byteArray = outputStream.toByteArray();
            String soapMsg = new String(byteArray, StandardCharsets.UTF_8);
            log.debug(soapMsg);
        }
    }
}

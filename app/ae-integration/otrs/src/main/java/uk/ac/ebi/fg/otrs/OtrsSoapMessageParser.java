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

package uk.ac.ebi.fg.otrs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtrsSoapMessageParser {

    private Object nodeToObject(Node node) {
        Node xsdTypeNode = node.getAttributes().getNamedItemNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");

        Object value;
        if (xsdTypeNode != null) {
            value = XSDTypeConverter.convertXSDToObject(node.getTextContent().trim(), xsdTypeNode.getTextContent().trim());
        } else {
            value = node.getTextContent().trim();
        }

        return value;
    }

    public Object[] nodesToArray(SOAPMessage msg) throws SOAPException {
        Document doc = msg.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        Element el = doc.getDocumentElement();
        NodeList nl = el.getChildNodes();

        Object[] results = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            results[i] = this.nodeToObject(nl.item(i));
        }
        return results;
    }

    public List<?> nodesToList(SOAPMessage msg) throws SOAPException {
        return Arrays.asList(this.nodesToArray(msg));
    }

    public Map<String, Object> nodesToMap(SOAPMessage msg) throws SOAPException {
        Map<String, Object> map = new HashMap<>();

        Document doc = msg.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        Element el = doc.getDocumentElement();
        NodeList nl = el.getChildNodes();

        for (int i = 0; i < (nl.getLength() / 2); i++) {
            Node valueNode = nl.item(i * 2 + 1);
            String key = nl.item(i * 2).getTextContent().trim();

            Object value = this.nodeToObject(valueNode);

            map.put(key, value);
        }

        return map;
    }
}
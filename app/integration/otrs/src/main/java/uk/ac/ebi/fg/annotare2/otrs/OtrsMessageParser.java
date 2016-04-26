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

public class OtrsMessageParser {

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

    public <Type> Type toObject(SOAPMessage msg, Class<Type> clazz) throws SOAPException {
        Object[] results = toArray(msg);
        if (null == results || 0 == results.length) {
            return null;
        } else if (1 == results.length) {
            if (null == results[0]) {
                return null;
            } else if (clazz.isAssignableFrom(results[0].getClass())) {
                return clazz.cast(results[0]);
            }
        }
        throw new SOAPException("Unable to cast result to " + clazz.getName());
    }

    public Object[] toArray(SOAPMessage msg) throws SOAPException {
        Document doc = msg.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        Element el = doc.getDocumentElement();
        NodeList nl = el.getChildNodes();

        Object[] results = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            results[i] = this.nodeToObject(nl.item(i));
        }
        return results;
    }

    public List<?> toList(SOAPMessage msg) throws SOAPException {
        return Arrays.asList(this.toArray(msg));
    }

    public Map<String, Object> toMap(SOAPMessage msg) throws SOAPException {
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
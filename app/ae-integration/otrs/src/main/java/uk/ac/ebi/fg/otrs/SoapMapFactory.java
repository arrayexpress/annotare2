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

import org.w3c.dom.DOMException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.util.Map;

class SoapMapFactory {
    protected SOAPElement createSoapMap(String name, Map<?, ?> values)
            throws DOMException, SOAPException {
        SOAPElement element = SOAPFactory.newInstance().createElement(name);
        element.addNamespaceDeclaration("xml-soap", "http://xml.apache.org/xml-soap");
        element.setAttribute("xsi:type", "xml-soap:Map");
        SOAPElement item = SOAPFactory.newInstance().createElement("item");
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            item.addChildElement("key").addTextNode(entry.getKey().toString()).setAttribute("xsi:type", "xsd:string");
            SOAPElement valueElement = item.addChildElement("value");
            if (entry.getValue() == null) {
                valueElement.setAttribute("xsi:nil", "true");
            } else {
                valueElement.addTextNode(entry.getValue().toString()).setAttribute("xsi:type", "xsd:string");
            }
        }
        element.addChildElement(item);
        return element;
    }
}
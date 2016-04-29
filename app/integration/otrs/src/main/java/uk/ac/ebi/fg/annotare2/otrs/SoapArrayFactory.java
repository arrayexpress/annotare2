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

import org.w3c.dom.DOMException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.util.Collection;

class SoapArrayFactory {

    <T> SOAPElement createSoapArray(String name, Collection<T> values)
            throws DOMException, SOAPException {
        SOAPElement element = SOAPFactory.newInstance().createElement(name);

        // Check the type of the array and default to string if empty
        String type;
        if (values.size() != 0) {
            type = XSDTypeConverter.simpleTypeForObject(values.iterator().next());
        } else {
            type = "xsi:string";
        }

        element.setAttribute("soapenc:arrayType", type + "[" + values.size() + "]");
        element.setAttribute("xsi:type", "soapenc:Array");

        for (T s : values) {
            element.addChildElement("item").addTextNode(s.toString()).setAttribute("xsi:type", type);
        }

        return element;
    }
}
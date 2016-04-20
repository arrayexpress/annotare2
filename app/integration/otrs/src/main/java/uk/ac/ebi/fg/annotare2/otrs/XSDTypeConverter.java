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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class XSDTypeConverter {

    private static final Logger LOG = LoggerFactory.getLogger(XSDTypeConverter.class);

    /**
     * Returns the xsd simple Type for the given Object
     * Only supports simple types!
     * <br/>
     * defaults to xsd:string
     *
     * @param obj object to get type for
     * @return xsd:simple type
     */
    static String simpleTypeForObject(Object obj) {
        if (obj instanceof String) {
            return "xsd:string";
        } else if (obj instanceof Long) {
            return "xsd:long";
        } else if (obj instanceof Integer) {
            return "xsd:integer";
        } else if (obj instanceof Boolean) {
            return "xsd:boolean";
        } else if (obj instanceof Date) {
            return "xsd:date";
        } else if (obj instanceof Float) {
            return "xsd:float";
        } else {
            return "xsd:string";
        }
    }

    /**
     * Given a string and an xsd-Type, this will convert the string back to a java object
     *
     * @param obj     the string to convert
     * @param xsdType the xsd-type
     * @return java object matching the given type
     */
    static Object convertXSDToObject(String obj, String xsdType) {
        xsdType = xsdType.toLowerCase();

        switch (xsdType) {
            case "xsd:string":
                return obj;
            case "xsd:long":
                return Long.valueOf(obj);
            case "xsd:integer":
                return Integer.valueOf(obj);
            case "xsd:int":
                return Integer.valueOf(obj);
            case "xsd:float":
                return Float.valueOf(obj);
            case "xsd:boolean":
                return Boolean.valueOf(obj);
            case "xsd:decimal":
                return Long.valueOf(obj);
            case "xsd:date":
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(obj);
                } catch (ParseException e) {
                    LOG.error("Failed to parse date: {}", obj, e);
                }
                break;
            case "xsd:base64binary":
                return new String(Base64.decodeBase64(obj.getBytes()), StandardCharsets.UTF_8);
            default:
                LOG.warn("Could not convert data type {}.", xsdType);
                break;
        }
        return obj;
    }
}

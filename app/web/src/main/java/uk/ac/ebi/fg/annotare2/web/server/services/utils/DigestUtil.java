/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services.utils;

import com.google.common.base.Charsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static uk.ac.ebi.fg.annotare2.web.server.LogUtil.logUnexpected;


/**
 * @author Olga Melnichuk
 */
public class DigestUtil {

    private DigestUtil() {
    }

    public static String md5Hex(String str) {
        try {
            byte[] md5 = MessageDigest.getInstance("MD5").digest(str.getBytes(Charsets.UTF_8));
            return  toHexString(md5);
        } catch (NoSuchAlgorithmException e) {
            throw logUnexpected("md5Hex error", e);
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString((aByte & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();
    }
}

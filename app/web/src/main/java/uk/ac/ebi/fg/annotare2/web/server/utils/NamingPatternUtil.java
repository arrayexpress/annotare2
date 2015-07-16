/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.server.utils;

public class NamingPatternUtil {

    public static String convert(String namingPattern) {
        // should convert single # to %1$d and multiple ##s to %1$0Nd where N is a number of ##s
        StringBuilder format = new StringBuilder();

        boolean isPrevCharSharp = false, hasFoundSharp = false;
        int sharpsCount = 0;

        char c;
        for (int i = 0; i <= namingPattern.length(); ++i) {
            c = i < namingPattern.length() ? namingPattern.charAt(i) : 0;
            if ('#' == c) {
                hasFoundSharp = true;
                if (isPrevCharSharp) {
                    sharpsCount++;
                } else {
                    isPrevCharSharp = true;
                    sharpsCount = 1;
                }
            } else {
                if (isPrevCharSharp) {
                    isPrevCharSharp = false;
                    if (1 == sharpsCount) {
                        format.append("%1$d");
                    } else {
                        format.append("%1$0" + sharpsCount + "d");
                    }
                }
                if (0 != c) {
                    format.append(c);
                }
            }
        }

        if (!hasFoundSharp) {
            format.append("%d");
        }

        return format.toString();
    }
}

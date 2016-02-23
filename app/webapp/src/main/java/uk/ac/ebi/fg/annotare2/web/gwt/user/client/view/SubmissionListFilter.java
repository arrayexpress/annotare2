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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

/**
 * @author Olga Melnichuk
 */
public enum SubmissionListFilter {
    ALL_SUBMISSIONS("all"),
    COMPLETED_SUBMISSIONS("completed"),
    INCOMPLETE_SUBMISSIONS("incomplete");

    private final String token;

    SubmissionListFilter(String prefix) {
        this.token = prefix;
    }

    public String getToken() {
        return token;
    }

    private boolean matches(String token) {
        return this.token.equalsIgnoreCase(token);
    }

    public static SubmissionListFilter getIfPresent(String token) {
        if (null == token || token.isEmpty()) {
            return null;
        }
        for (SubmissionListFilter f : values()) {
            if (f.matches(token)) {
                return f;
            }
        }
        return null;
    }
}

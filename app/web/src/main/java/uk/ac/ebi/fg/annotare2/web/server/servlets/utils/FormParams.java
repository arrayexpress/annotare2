package uk.ac.ebi.fg.annotare2.web.server.servlets.utils;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;

public abstract class FormParams {
    public static final String NAME_PARAM = "name";
    public static final String EMAIL_PARAM = "email";
    public static final String PASSWORD_PARAM = "password";
    public static final String CONFIRM_PASSWORD_PARAM = "confirm-password";
    public static final String TOKEN_PARAM = "token";

    private Map<String,RequestParam> paramMap = new HashMap<String, RequestParam>();
    private Set<RequestParam> mandatoryParamSet = new HashSet<RequestParam>();

    public String getParamValue(String paramName) {
        if (paramMap.containsKey(paramName)) {
            return paramMap.get(paramName).getValue();
        }
        return null;
    }

    public abstract ValidationErrors validate();

    protected void addParam(RequestParam param, boolean isMandatory)
    {
        paramMap.put(param.getName(), param);
        if (isMandatory) {
            mandatoryParamSet.add(param);
        }
    }

    protected ValidationErrors validateMandatory() {
        ValidationErrors errors = new ValidationErrors();
        for (RequestParam p : getMandatoryParams()) {
            if (p.isEmpty()) {
                errors.append(p.getName(), "Please specify a value, " + p.getName() + " is required");
            }
        }
        return errors;
    }

    protected Collection<RequestParam> getMandatoryParams() {
        return Collections.unmodifiableSet(mandatoryParamSet);
    }

    protected boolean isEmailGoodEnough() {
        return nullToEmpty(getParamValue(EMAIL_PARAM)).matches(".+@.+");
    }

    protected boolean isPasswordGoodEnough() {
        return nullToEmpty(getParamValue(PASSWORD_PARAM)).matches("^(?=.*\\d)(?=.*[a-zA-Z])\\S{4,}$");
    }

    protected boolean hasPasswordConfirmed() {
        return nullToEmpty(getParamValue(PASSWORD_PARAM)).equals(getParamValue(CONFIRM_PASSWORD_PARAM));
    }
}

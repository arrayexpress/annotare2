/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Olga Melnichuk
 */
public class ApplicationProperties implements IsSerializable {

    private String ftpUrl;
    private String ftpUsername;
    private String ftpPassword;

    ApplicationProperties() {
    }

    private ApplicationProperties(ApplicationProperties other) {
        this.ftpUrl = other.getFtpUrl();
        this.ftpUsername = other.getFtpUsername();
        this.ftpPassword = other.getFtpPassword();
    }

    public String getFtpUrl() {
        return ftpUrl;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public static class Builder {
        private final ApplicationProperties properties;

        public Builder() {
            this.properties = new ApplicationProperties();
        }

        public Builder setFtpUrl(String url) {
            properties.ftpUrl = url;
            return this;
        }

        public Builder setFtpUsername(String username) {
            properties.ftpUsername = username;
            return this;
        }

        public Builder setFtpPassword(String password) {
            properties.ftpPassword = password;
            return this;
        }

        public ApplicationProperties build() {
            return new ApplicationProperties(properties);
        }
    }
}

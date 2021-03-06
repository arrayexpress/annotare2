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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Olga Melnichuk
 */
public class FtpFileInfo implements IsSerializable {

    private String fileName;

    private String md5;

    @SuppressWarnings("unused")
    FtpFileInfo() {
        /* used by GWT serialization */
    }

    public FtpFileInfo(String fileName, String md5) {
        this.fileName = fileName;
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMd5() {
        return md5;
    }
}

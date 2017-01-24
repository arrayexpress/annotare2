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

package uk.ac.ebi.fg.annotare2.integration;

import com.google.gwt.thirdparty.json.JSONException;
import com.google.gwt.thirdparty.json.JSONObject;
import com.google.inject.Inject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class FileValidationService {

    private final ExtendedAnnotareProperties properties;
    final static String FILE_ERROR_KEY = "file_errors";
    final static String PAIRS_ERROR_KEY = "pairs_errors";
    final static String EXECUTION_ERROR_KEY = "execution_errors";
    final static String INTEGRITY_ERROR_KEY = "integrity_errors";

    @Inject
    public FileValidationService(ExtendedAnnotareProperties properties) {
        this.properties = properties;
    }


    public void submit(String ftpSubDirectory, Long id) throws Exception {
        URL url = new URL(properties.getFileValidationUrl());
        String parameters = "data_dir="+ftpSubDirectory+"&id="+id;
        byte[] data = parameters.getBytes( StandardCharsets.UTF_8 );

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setInstanceFollowRedirects( false );
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", Integer.toString(data.length));
        httpURLConnection.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream())) {
            wr.write(data);
        }

        if (httpURLConnection.getResponseCode()!=200) {
            throw new Exception("Error " + httpURLConnection.getResponseCode() + " communicating with the file validator at " + ftpSubDirectory + " for id = " + id);
        }
        httpURLConnection.disconnect();
    }

    public JSONObject checkStatus(Long id) throws Exception {
        URL url = new URL(properties.getFileValidationUrl() + id+"/");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        StringBuilder result = new StringBuilder();
        try {
            if (httpURLConnection.getResponseCode() != 200) {
                throw new Exception("Error " + httpURLConnection.getResponseCode() + " file validation status for id = " + id);
            }
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            httpURLConnection.disconnect();
        }
        JSONObject json = new JSONObject(result.toString());
        return  json;
    }

    public enum FileValidationStatus {
        PENDING,
        FINISHED,
        EXECUTION_ERROR
        ;
    }


    public boolean hasErrors(JSONObject json) throws JSONException {
        if (json.has(FILE_ERROR_KEY) && json.getJSONObject(FILE_ERROR_KEY).length()>0) {
            return true;
        }
        if (json.has(PAIRS_ERROR_KEY) && json.getJSONArray(PAIRS_ERROR_KEY).length()>0) {
            return true;
        }
        if (json.has(EXECUTION_ERROR_KEY) &&  json.getJSONArray(EXECUTION_ERROR_KEY).length()>0) {
            return true;
        }
        if (json.has(INTEGRITY_ERROR_KEY) &&  json.getJSONArray(INTEGRITY_ERROR_KEY).length()>0) {
            return true;
        }
        return false;
    }


    public String getErrorString(JSONObject json) throws JSONException {
        StringBuilder errors = new StringBuilder();
        errors.append("_______________________________\n\n");

        if (json.has(FILE_ERROR_KEY)) {
            JSONObject fileErrors = json.getJSONObject(FILE_ERROR_KEY);
            if (fileErrors.names()!=null && fileErrors.names().length()>0) {
                errors.append("File Errors:\n\n");
                for (Iterator it = fileErrors.keys(); it.hasNext(); ) {
                    String key = (String) it.next();
                    errors.append(key).append(": ").append(fileErrors.getJSONArray(key).join("\n" + key + ": ").replace("\\", ""));
                    errors.append("\n");
                }
                errors.append("\n");
            }
        }

        if (json.has(PAIRS_ERROR_KEY) && json.getJSONArray(PAIRS_ERROR_KEY).length()>0) {
            errors.append("Pair Errors:\n\n");
            errors.append(json.getJSONArray(PAIRS_ERROR_KEY).join("\n"));
            errors.append("\n");
        }

        if (json.has(INTEGRITY_ERROR_KEY) && json.getJSONArray(INTEGRITY_ERROR_KEY).length()>0) {
            errors.append("Integrity Errors:\n\n");
            errors.append(  json.getJSONArray(INTEGRITY_ERROR_KEY).join("\n"));
            errors.append("\n");
        }


        return errors.toString();
    }



}

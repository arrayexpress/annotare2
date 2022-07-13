package uk.ac.ebi.fg.annotare2.submission.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorRequest {
    @JsonProperty("experiment_json")
    @JsonRawValue
    private String experimentJSON;
    @JsonProperty("data_files")
    private List<String> dataFiles;

    public ValidatorRequest(String experimentJSON, Set<DataFile> files) {
        this.experimentJSON = experimentJSON;
        dataFiles = files.stream().map(DataFile::getName).collect(Collectors.toList());
    }
}

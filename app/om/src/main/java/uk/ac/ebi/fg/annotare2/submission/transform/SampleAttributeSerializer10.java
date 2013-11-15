package uk.ac.ebi.fg.annotare2.submission.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttribute;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;

/**
 * @author Olga Melnichuk
 */
class SampleAttributeSerializer10 extends JsonSerializer<SampleAttribute> {

    static final List<String> SAMPLE_ATTRIBUTE_JSON_FIELDS = asList(
            "id",
            "name",
            "term",
            "type",
            "valueSubType",
            "units",
            "ontologyBranch",
            "isEditable");

    @Override
    public void serialize(SampleAttribute sampleAttribute, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, sampleAttribute, SAMPLE_ATTRIBUTE_JSON_FIELDS);
    }
}

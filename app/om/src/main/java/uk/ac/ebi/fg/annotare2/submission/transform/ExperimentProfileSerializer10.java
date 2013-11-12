package uk.ac.ebi.fg.annotare2.submission.transform;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.fest.reflect.reference.TypeRef;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.fest.reflect.core.Reflection.field;

/**
 * @author Olga Melnichuk
 */
public class ExperimentProfileSerializer10 extends JsonSerializer<ExperimentProfile> {

    @Override
    public void serialize(ExperimentProfile experimentProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        //TODO do not write the field if it's null
        jgen.writeObjectField("nextId", field("nextId").ofType(Integer.TYPE).in(experimentProfile).get());
        jgen.writeObjectField("type", field("type").ofType(ExperimentProfileType.class).in(experimentProfile).get());
        jgen.writeObjectField("title", field("title").ofType(String.class).in(experimentProfile).get());
        jgen.writeObjectField("description", field("description").ofType(String.class).in(experimentProfile).get());
        jgen.writeObjectField("experimentDate", field("experimentDate").ofType(Date.class).in(experimentProfile).get());
        jgen.writeObjectField("publicReleaseDate", field("publicReleaseDate").ofType(Date.class).in(experimentProfile).get());
        jgen.writeObjectField("experimentalDesigns", field("experimentalDesigns").ofType(new TypeRef<List<OntologyTerm>>() {
        }).in(experimentProfile).get());

        jgen.writeEndObject();
    }
}

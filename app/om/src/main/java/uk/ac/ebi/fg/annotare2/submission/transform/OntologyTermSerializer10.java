package uk.ac.ebi.fg.annotare2.submission.transform;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.io.IOException;

import static org.fest.reflect.core.Reflection.field;

/**
 * @author Olga Melnichuk
 */
public class OntologyTermSerializer10 extends JsonSerializer<OntologyTerm> {

    @Override
    public void serialize(OntologyTerm term, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeObjectField("accession", field("accession").ofType(String.class).in(term).get());
        jgen.writeObjectField("label", field("label").ofType(String.class).in(term).get());
        jgen.writeEndObject();
    }
}

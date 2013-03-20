package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.store;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class ValueSource {

    private static ValueSource DEFAULT_SOURCE = new ValueSource("None");

    public static List<ValueSource> ALL = asList(
            new ValueSource("EFO"),
            DEFAULT_SOURCE
    );

    private final String name;

    public ValueSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return this == DEFAULT_SOURCE;
    }

    public static ValueSource get(String name) {
        for (ValueSource vs : ALL) {
            if (name.equals(vs.getName())) {
                return vs;
            }
        }
        return null;
    }
}

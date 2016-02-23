package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

/**
 * @author Olga Melnichuk
 */
public interface EditorTab {

    String getTitle();

    boolean isEqualTo(EditorTab other);
}

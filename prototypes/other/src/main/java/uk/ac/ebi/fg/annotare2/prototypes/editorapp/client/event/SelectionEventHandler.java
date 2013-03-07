package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Olga Melnichuk
 */
public interface SelectionEventHandler<T> extends EventHandler {
    void onSelection(SelectionEvent<T> event);
}

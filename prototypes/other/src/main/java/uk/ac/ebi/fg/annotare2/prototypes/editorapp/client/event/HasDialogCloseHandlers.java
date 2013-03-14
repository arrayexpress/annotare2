package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Olga Melnichuk
 */
public interface HasDialogCloseHandlers<T> extends HasHandlers {

    HandlerRegistration addDialogCloseHandler(DialogCloseHandler<T> handler);
}

package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Olga Melnichuk
 */
public class SelectionEvent<T> extends GwtEvent<SelectionEventHandler<T>> {

    private static Type<SelectionEventHandler<?>> TYPE = new Type<SelectionEventHandler<?>>();

    private final T selection;

    protected SelectionEvent(T selection) {
        this.selection = selection;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type<SelectionEventHandler<T>> getAssociatedType() {
        return (Type)TYPE;
    }

    @Override
    protected void dispatch(SelectionEventHandler<T> handler) {
        handler.onSelection(this);
    }

    public T getSelection() {
        return selection;
    }

    public static Type<SelectionEventHandler<?>> getType() {
        return TYPE;
    }

    public static <T> void fire(HasSelectionHandlers<T> source, T selection) {
        if (TYPE != null) {
            SelectionEvent<T> event = new SelectionEvent<T>(selection);
            source.fireEvent(event);
        }
    }
}

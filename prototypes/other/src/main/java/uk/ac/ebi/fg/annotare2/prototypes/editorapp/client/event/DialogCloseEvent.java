package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Olga Melnichuk
 */
public class DialogCloseEvent<T> extends GwtEvent<DialogCloseHandler<T>> {

    private static Type<DialogCloseHandler<?>> TYPE = new Type<DialogCloseHandler<?>>();

    private final T target;

    private final boolean hasResult;

    private DialogCloseEvent(T target, boolean hasResult) {
        this.target = target;
        this.hasResult = hasResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type<DialogCloseHandler<T>> getAssociatedType() {
        return (Type)TYPE;
    }

    @Override
    protected void dispatch(DialogCloseHandler<T> handler) {
        handler.onDialogClose(this);
    }

    public static Type<DialogCloseHandler<?>> getType() {
        return TYPE;
    }

    public boolean hasResult() {
        return hasResult;
    }

    public T getTarget() {
        return target;
    }

    public static <T> void fire(HasDialogCloseHandlers<T> source, T target, boolean hasResult) {
        if (TYPE != null) {
            DialogCloseEvent<T> event = new DialogCloseEvent<T>(target, hasResult);
            source.fireEvent(event);
        }
    }
}


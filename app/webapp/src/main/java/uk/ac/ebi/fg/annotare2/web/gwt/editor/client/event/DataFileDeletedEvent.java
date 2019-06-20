package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event;

import com.google.gwt.event.shared.GwtEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.HashSet;
import java.util.Set;

public class DataFileDeletedEvent extends GwtEvent<DataFileDeletedEventHandler> {
    private static GwtEvent.Type<DataFileDeletedEventHandler> TYPE = new GwtEvent.Type<DataFileDeletedEventHandler>();
    private Set<DataFileRow> deletedFiles;

    @Override
    public GwtEvent.Type<DataFileDeletedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataFileDeletedEventHandler handler) {
        handler.onDelete(this);
    }

    /*public static void fire(HasDeleteEventHandlers source) {
        if (TYPE != null) {
            DataFileDeletedEvent event = new DataFileDeletedEvent();
            source.fireEvent(event);
        }
    }*/

    public static GwtEvent.Type<DataFileDeletedEventHandler> getType() {
        return TYPE;
    }

    public DataFileDeletedEvent(Set<DataFileRow> deletedFiles) {
        this.deletedFiles = new HashSet<>();
        this.deletedFiles = deletedFiles;
    }

    public Set<DataFileRow> getDeletedFiles() {
        return deletedFiles;
    }
}

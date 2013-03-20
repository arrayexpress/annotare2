package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.store.SdrfValue;

/**
 * @author Olga Melnichuk
 */
public interface SdrfCellValueEditor extends IsWidget {

     SdrfValue getValue();
}

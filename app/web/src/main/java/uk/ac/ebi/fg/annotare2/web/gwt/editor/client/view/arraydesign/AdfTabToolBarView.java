package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Olga Melnichuk
 */
public interface AdfTabToolBarView extends IsWidget {

    void setPresenter(Presenter presenter);

    void hideImportButtons(boolean hide);

    public static interface Presenter {

        void importFile(AsyncCallback<Void> callback);
    }
}

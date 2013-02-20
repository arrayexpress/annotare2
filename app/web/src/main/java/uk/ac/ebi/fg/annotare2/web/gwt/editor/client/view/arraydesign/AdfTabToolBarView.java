package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.AsyncEventFinishListener;

/**
 * @author Olga Melnichuk
 */
public interface AdfTabToolBarView extends IsWidget {

    void setPresenter(Presenter presenter);

    public static interface Presenter {

        void importFile(AsyncEventFinishListener listener);
    }
}

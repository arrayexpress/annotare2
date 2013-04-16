package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.dialog;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignSuggestOracle extends SuggestOracle {

    private final Service service;

    public ArrayDesignSuggestOracle(Service service) {
        if (service == null) {
            throw new IllegalArgumentException("service == null");
        }
        this.service = service;
    }

    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        service.getArrayDesigns(request.getQuery(), new AsyncCallback<List<ArrayDesignRef>>() {
            @Override
            public void onFailure(Throwable caught) {
                //todo log
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<ArrayDesignRef> result) {
                callback.onSuggestionsReady(request, createResponse(result));
            }
        });
    }

    private Response createResponse(List<ArrayDesignRef> result) {
        List<ArrayDesignSuggestion> suggestions = new ArrayList<ArrayDesignSuggestion>();
        for (ArrayDesignRef ad : result) {
            suggestions.add(new ArrayDesignSuggestion(ad));
        }
        return new Response(suggestions);
    }

    static class ArrayDesignSuggestion implements Suggestion {

        private final ArrayDesignRef ad;

        ArrayDesignSuggestion(ArrayDesignRef ad) {
            this.ad = ad;
        }

        @Override
        public String getDisplayString() {
            return ad.getName() + " : " + ad.getDescription();
        }

        @Override
        public String getReplacementString() {
            return ad.getName();
        }
    }

    public static interface Service {
        void getArrayDesigns(String query, AsyncCallback<List<ArrayDesignRef>> callback);
    }
}

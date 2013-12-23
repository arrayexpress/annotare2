package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

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

    private final SuggestService<ArrayDesignRef> suggestService;

    public ArrayDesignSuggestOracle(SuggestService<ArrayDesignRef> suggestService) {
        if (suggestService == null) {
            throw new IllegalArgumentException("service == null");
        }
        this.suggestService = suggestService;
    }

    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        suggestService.suggest(request.getQuery(), request.getLimit(), new AsyncCallback<List<ArrayDesignRef>>() {
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
            return ad.getDescription() + " : " + ad.getName();
        }

        @Override
        public String getReplacementString() {
            return ad.getName();
        }
    }
}

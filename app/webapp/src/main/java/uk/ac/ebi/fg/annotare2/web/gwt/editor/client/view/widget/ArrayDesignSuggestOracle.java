package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.SuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
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
    public boolean isDisplayStringHTML() {
        return true;
    }

    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        suggestService.suggest(request.getQuery(), request.getLimit(),
                new ReportingAsyncCallback<ArrayList<ArrayDesignRef>>(FailureMessage.UNABLE_TO_LOAD_ARRAYS_LIST) {
                    @Override
                    public void onSuccess(ArrayList<ArrayDesignRef> result) {
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
            return ad.getName() + " : <a href=\"http://www.ebi.ac.uk/arrayexpress/arrays/" + ad.getAccession() + "\" target=\"_blank\">" + ad.getAccession() + "</a>";
        }

        @Override
        public String getReplacementString() {
            return ad.getAccession();
        }
    }
}

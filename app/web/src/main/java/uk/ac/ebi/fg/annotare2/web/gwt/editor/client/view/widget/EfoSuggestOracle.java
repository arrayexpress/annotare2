package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EfoSuggestOracle extends SuggestOracle {

    private final SuggestService<EfoTermDto> suggestService;

    public EfoSuggestOracle(SuggestService<EfoTermDto> suggestService) {
        this.suggestService = suggestService;
    }

    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        suggestService.suggest(request.getQuery(), request.getLimit(), new AsyncCallback<List<EfoTermDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                //todo log
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<EfoTermDto> result) {
                callback.onSuggestionsReady(request, createResponse(result));
            }
        });
    }

    private Response createResponse(List<EfoTermDto> result) {
        List<EfoTermSuggestion> suggestions = new ArrayList<EfoTermSuggestion>();
        for (EfoTermDto term : result) {
            suggestions.add(new EfoTermSuggestion(term));
        }
        return new Response(suggestions);
    }

    public static class EfoTermSuggestion implements SuggestOracle.Suggestion {
        private final EfoTermDto term;

        private EfoTermSuggestion(EfoTermDto term) {
            this.term = term;
        }

        public EfoTermDto getTerm() {
            return term;
        }

        @Override
        public String getDisplayString() {
            return term.getLabel() + " (" + term.getAccession().trim() + ")";
        }

        @Override
        public String getReplacementString() {
            return term.getLabel();
        }
    }
}

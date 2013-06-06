/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;

import java.util.Date;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface AdfDetailsView extends IsWidget {

    void setPrintingProtocols(List<PrintingProtocolDto> protocols);

    void setArrayDesignName(Cell<String> cell);

    void setVersion(Cell<String> cell);

    void setPrintingProtocol(Cell<String> cell);

    void setDescription(Cell<String> cell);

    void setReleaseDate(Cell<Date> cell);

    void setOrganism(Cell<String> cell);

    void setPresenter(Presenter presenter);

    public interface Presenter {

        void getOrganisms(String query, int limit, AsyncCallback<List<EfoTermDto>> callback);
    }
}

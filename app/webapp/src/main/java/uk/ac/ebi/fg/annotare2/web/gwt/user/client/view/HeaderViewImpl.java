/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.CookieDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.CookiePopupDeatils;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.AppHeader;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class HeaderViewImpl extends Composite implements HeaderView {

    private CookiePopupDeatils cookiePopupDeatils;
    interface Binder extends UiBinder<Widget, HeaderViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    AppHeader appHeader;

    public HeaderViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setUserName(String name) {
        appHeader.setUserName(name);
    }

    private void showNotice() {
        Date stopNoticeDate = new Date();
        stopNoticeDate.setTime(cookiePopupDeatils.getStopNoticeDate());
        if (!"YEZ".equalsIgnoreCase(Cookies.getCookie(cookiePopupDeatils.getName())) && (new Date().before(stopNoticeDate))) {
            Date expiryDate = new Date();
            expiryDate.setTime(cookiePopupDeatils.getExpiryDate());
            CookieDialog dialogBox = new CookieDialog(
                    cookiePopupDeatils.getTitle(),
                    cookiePopupDeatils.getHtml().trim(),
                    cookiePopupDeatils.getName(),
                    expiryDate
            );
            dialogBox.show();
        }
    }

    public void setNoticeCookie(CookiePopupDeatils cookiePopupDeatils){
        this.cookiePopupDeatils = cookiePopupDeatils;
        showNotice();
    }
}

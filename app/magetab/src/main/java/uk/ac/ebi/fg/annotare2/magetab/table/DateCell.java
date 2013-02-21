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

package uk.ac.ebi.fg.annotare2.magetab.table;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.format.TextFormatter;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class DateCell implements Cell<Date> {

    private final Cell<String> cell;

    public DateCell(Cell<String> cell) {
        this.cell = cell;
    }

    @Override
    public void setValue(Date date) {
        cell.setValue(format(date));
    }

    @Override
    public Date getValue() {
        return parse(cell.getValue());
    }

    @Override
    public boolean isEmpty() {
        return cell.isEmpty();
    }

    private String format(Date date) {
        return TextFormatter.getInstance().formatDate(date);
    }

    private Date parse(String s) {
        return TextFormatter.getInstance().parseDate(s);
    }
}

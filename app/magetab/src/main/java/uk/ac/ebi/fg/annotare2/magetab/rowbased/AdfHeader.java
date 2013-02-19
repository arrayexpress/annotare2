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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.table.Row;
import uk.ac.ebi.fg.annotare2.magetab.table.RowSet;
import uk.ac.ebi.fg.annotare2.magetab.table.RowTag;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfHeader.Tag.*;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class AdfHeader {

    static enum Tag implements RowTag {
        ARRAY_DESIGN_NAME("Array Design Name"),
        VERSION("Version"),
        PROVIDER("Provider"),
        PRINTING_PROTOCOL("Printing Protocol"),

        TECHNOLOGY_TYPE("Technology Type"),
        TECHNOLOGY_TYPE_TERM_SOURCE_REF("Technology Type Term Source REF"),
        TECHNOLOGY_TYPE_TERM_ACCESSION_NUMBER("Technology Type Term Accession Number"),

        SURFACE_TYPE("Surface Type"),
        SURFACE_TYPE_TERM_SOURCE_REF("Surface Type Term Source REF"),
        SURFACE_TYPE_TERM_ACCESSION_NUMBER("Surface Type Term Accession Number"),

        SUBSTRATE_TYPE("Substrate Type"),
        SUBSTRATE_TYPE_TERM_SOURCE_REF("Substrate Type Term Source REF"),
        SUBSTRATE_TYPE_TERM_ACCESSION_NUMBER("Substrate Type Term Accession Number"),

        SEQUENCE_POLYMER_TYPE("Sequence Polymer Type"),
        SEQUENCE_POLYMER_TYPE_TERM_SOURCE_REF("Sequence Polymer Type Term Source REF"),
        SEQUENCE_POLYMER_TYPE_TERM_ACCESSION_NUMBER("Sequence Polymer Type Term Accession Number"),

        TERM_SOURCE_NAME("Term Source Name"),
        TERM_SOURCE_FILE("Term Source File"),
        TERM_SOURCE_VERSION("Term Source Version");

        private String tagName;

        private Tag(String title) {
            this.tagName = title;
        }

        @Override
        public String getName() {
            return tagName;
        }
    }

    private final TermSourceList termSourceList;

    private final GeneralInfoList generalInfoList;

    private final TermBasedObjectList technologyTypeList;

    private final TermBasedObjectList surfaceTypeList;

    private final TermBasedObjectList substrateTypeList;

    private final TermBasedObjectList sequiencePolymerTypeList;

    private final Map<String, CommentList> comments = new HashMap<String, CommentList>();

    private final Table table;

    public AdfHeader() {
        this(new Table());
    }

    public AdfHeader(Table table) {
        this.table = table;

        termSourceList = new TermSourceList(table,
                TERM_SOURCE_NAME,
                TERM_SOURCE_VERSION,
                TERM_SOURCE_FILE);

        generalInfoList = new GeneralInfoList(table);
        if (generalInfoList.isEmpty()) {
            generalInfoList.add();
        }

        technologyTypeList = new TermBasedObjectList(table, termSourceList,
                TECHNOLOGY_TYPE,
                TECHNOLOGY_TYPE_TERM_ACCESSION_NUMBER,
                TECHNOLOGY_TYPE_TERM_SOURCE_REF);

        surfaceTypeList = new TermBasedObjectList(table, termSourceList,
                SURFACE_TYPE,
                SURFACE_TYPE_TERM_ACCESSION_NUMBER,
                SURFACE_TYPE_TERM_SOURCE_REF);

        substrateTypeList = new TermBasedObjectList(table, termSourceList,
                SUBSTRATE_TYPE,
                SUBSTRATE_TYPE_TERM_ACCESSION_NUMBER,
                SUBSTRATE_TYPE_TERM_SOURCE_REF);

        sequiencePolymerTypeList = new TermBasedObjectList(table, termSourceList,
                SEQUENCE_POLYMER_TYPE,
                SEQUENCE_POLYMER_TYPE_TERM_ACCESSION_NUMBER,
                SEQUENCE_POLYMER_TYPE_TERM_SOURCE_REF);
    }

    public Row.Cell<String> getArrayDesignName() {
        return generalInfoList.get(0).getArrayDesignName();
    }

    public Row.Cell<String> getVersion() {
        return generalInfoList.get(0).getVersion();
    }

    public Row.Cell<String> getProvider() {
        return generalInfoList.get(0).getProvider();
    }

    public Row.Cell<String> getPrintingProtocol() {
        return generalInfoList.get(0).getPrintingProtocol();
    }

    public Term getTechnologyType() {
        return getFirstOrNull(technologyTypeList);
    }

    public Term getSurfaceType() {
        return getFirstOrNull(surfaceTypeList);
    }

    public Term getSubstrateType() {
        return getFirstOrNull(substrateTypeList);
    }

    public Term getSequencePolymerType() {
        return getFirstOrNull(sequiencePolymerTypeList);
    }

    public List<Row.Cell<String>> getComments(String key, boolean atLeastOneRequired) {
        CommentList list = getCommentList(key);
        if (list.isEmpty() && atLeastOneRequired) {
            createComment(list);
        }
        return list.getAll();
    }

    public Row.Cell<String> addComment(String key) {
        return createComment(getCommentList(key));
    }

    private Row.Cell<String> createComment(CommentList list) {
        return list.add();
    }

    private CommentList getCommentList(String key) {
        CommentList list = comments.get(key);
        return list == null ? new CommentList(table, key) : list;
    }

    private <T> T getFirstOrNull(ObjectList<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    private static class GeneralInfoList extends ObjectList<AdfInfo> {

        private GeneralInfoList(Table table) {
            super(new RowSet(
                    ARRAY_DESIGN_NAME,
                    VERSION,
                    PROVIDER,
                    PRINTING_PROTOCOL).from(table),
                    new ObjectCreator<AdfInfo>() {
                        @Override
                        public AdfInfo create(Map<RowTag, Row.Cell<String>> map) {
                            AdfInfo generalInfo = new AdfInfo();
                            generalInfo.setArrayDesignName(map.get(ARRAY_DESIGN_NAME));
                            generalInfo.setVersion(map.get(VERSION));
                            generalInfo.setProvider(map.get(PROVIDER));
                            generalInfo.setPrintingProtocol(map.get(PRINTING_PROTOCOL));
                            return generalInfo;
                        }
                    });
        }
    }

    private static class CommentList extends ObjectList<Row.Cell<String>> {

        private CommentList(Table table, String key) {
            super(new RowSet(new CommentTag(key)).from(table), new ObjectCreator<Row.Cell<String>>() {
                @Override
                public Row.Cell<String> create(Map<RowTag, Row.Cell<String>> map) {
                    return map.entrySet().iterator().next().getValue();
                }
            });
        }
    }

    private static class CommentTag implements RowTag {

        private final String name;

        private CommentTag(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return "Comment[" + name + "]";
        }
    }
}

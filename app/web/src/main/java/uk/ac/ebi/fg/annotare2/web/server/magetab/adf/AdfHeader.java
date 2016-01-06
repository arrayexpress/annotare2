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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.adf.AdfHeader.Tag.*;

/**
 * @author Olga Melnichuk
 */
public class AdfHeader {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

    private final GeneralInfoList generalInfoList;

    private final TermBasedObjectList technologyTypeList;

    private final TermBasedObjectList surfaceTypeList;

    private final TermBasedObjectList substrateTypeList;

    private final TermBasedObjectList sequencePolymerTypeList;

    private final Table table;

    public AdfHeader(Table table) {
        this.table = table;

        TermSourceList termSourceList = new TermSourceList(table,
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

        sequencePolymerTypeList = new TermBasedObjectList(table, termSourceList,
                SEQUENCE_POLYMER_TYPE,
                SEQUENCE_POLYMER_TYPE_TERM_ACCESSION_NUMBER,
                SEQUENCE_POLYMER_TYPE_TERM_SOURCE_REF);
    }

    public String getArrayDesignName() {
        return generalInfoList.get(0).getName();
    }

    public String getVersion() {
        return generalInfoList.get(0).getVersion();
    }

    public String getProvider() {
        return generalInfoList.get(0).getProvider();
    }

    public String getPrintingProtocol() {
        return generalInfoList.get(0).getPrintingProtocol();
    }

    public Term getTechnologyType() {
        return getFirst(technologyTypeList, false);
    }

    public Term getSurfaceType() {
        return getFirst(surfaceTypeList, false);
    }

    public Term getSubstrateType() {
        return getFirst(substrateTypeList, false);
    }

    public Term getSequencePolymerType() {
        return getFirst(sequencePolymerTypeList, false);
    }

    public String getDescription() {
        return getFirst(getCommentList("Description"), false);
    }

    public String getOrganism() {
        return getFirst(getCommentList("Organism"), false);
    }

    public Date getArrayExpressReleaseDate() {
        return parseDate(getFirst(getCommentList("ArrayExpressReleaseDate"), false));
    }

    public List<String> getComments(String key, boolean atLeastOneRequired) {
        CommentList list = getCommentList(key);
        if (list.isEmpty() && atLeastOneRequired) {
            list.add();
        }
        return list.getAll();
    }

    private CommentList getCommentList(String key) {
        return new CommentList(table, key);
    }

    private <T> T getFirst(ObjectList<T> list, boolean create) {
        return list.isEmpty() ? (create ? list.add() : null) : list.get(0);
    }

    private Date parseDate(String str) {
        try {
            return isNullOrEmpty(str) ? null : DATE_FORMAT.parse(str);
        } catch (ParseException e) {
            return null;
        }
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
                        public AdfInfo create(Map<RowTag, String> map) {
                            return new AdfInfo(
                                    map.get(ARRAY_DESIGN_NAME),
                                    map.get(VERSION),
                                    map.get(PROVIDER),
                                    map.get(PRINTING_PROTOCOL));
                        }
                    });
        }
    }

    private static class CommentList extends ObjectList<String> {

        private CommentList(Table table, String key) {
            super(new RowSet(new CommentTag(key)).from(table), new ObjectCreator<String>() {
                @Override
                public String create(Map<RowTag, String> map) {
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

/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.base;

import com.google.common.annotations.GwtCompatible;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class GwtQuirks {

    /**
     * Removes elements from a list by their indices.
     * Note: for some unknown to me reason list.remove(i) doesn't work as expected in GWT;
     * that is why "remove" implemented as new array list substitution.
     *
     * @param list    a list to remove elements from
     * @param indices a list of indices of elements to be removed
     * @param <T>     a type of elements
     * @return a new list where deleted elements are filtered out
     */
    public static <T> List<T> remove(List<T> list, List<Integer> indices) {
        List<Integer> sorted = new ArrayList<Integer>();
        sorted.addAll(indices);
        Collections.sort(sorted);

        List<T> newList = new ArrayList<T>();
        int k = 0, i = sorted.isEmpty() ? -1 : sorted.get(k);
        for (int j = 0; j < list.size(); j++) {
            if (j != i) {
                newList.add(list.get(j));
            } else if (k < sorted.size() - 1) {
                i = sorted.get(++k);
            }
        }
        return newList;
    }
}

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

package uk.ac.ebi.fg.annotare2.db.model;

import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.AclType;
import uk.ac.ebi.fg.annotare2.db.model.enums.Role;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "acl")
public class Acl {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "aclType", nullable = false)
    private AclType aclType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acl")
    @OrderBy("role ASC")
    private List<AclEntry> entries;

    public Acl() {
        this(null);
    }

    public Acl(AclType type) {
        this.aclType = type;
        entries = newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AclEntry> getEntries() {
        return entries;
    }

    public boolean hasPermission(Collection<? extends Role> roles, Permission permission) {
        for (AclEntry p : entries) {
            if (p.complies(roles, permission)) {
                return true;
            }
        }
        return false;
    }
}

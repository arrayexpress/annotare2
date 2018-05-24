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

package uk.ac.ebi.fg.annotare2.db.model;

import org.hibernate.annotations.Filter;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;

import javax.persistence.*;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.db.model.FilterNames.NOT_DELETED_SUBMISSION_FILTER;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "emailVerified", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean emailVerified;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "passwordChangeRequested", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean passwordChangeRequested;

    @Column(name = "verificationToken", nullable = true)
    private String verificationToken;

    @Column(name = "referrer", nullable = true)
    private String referrer;

    @Column(name = "privacyNoticeVersion", columnDefinition = "INT default 0")
    private int privacyNoticeVersion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @OrderBy("role ASC")
    private List<UserRole> roles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "createdBy")
    @OrderBy("created DESC")
    @Filter(name = NOT_DELETED_SUBMISSION_FILTER)
    private List<Submission> submissions;

    public User() {
        this(null, null);
    }

    public User(String email, String password) {
        this("Annotare user", email, password);
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        submissions = newArrayList();
        roles = newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPasswordChangeRequested() {
        return passwordChangeRequested;
    }

    public void setPasswordChangeRequested(boolean passwordChangeRequested) {
        this.passwordChangeRequested = passwordChangeRequested;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public int getPrivacyNoticeVersion() {
        return privacyNoticeVersion;
    }

    public void setPrivacyNoticeVersion(int privacyNoticeVersion) {
        this.privacyNoticeVersion = privacyNoticeVersion;
    }

    public boolean isAllowed(HasEffectiveAcl obj, Permission permission) {
        return obj.getEffectiveAcl().hasPermission(this, permission);
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}

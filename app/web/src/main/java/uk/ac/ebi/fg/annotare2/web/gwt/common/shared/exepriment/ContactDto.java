package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactDto implements IsSerializable {

    private int id;

    private String firstName;

    private String lastName;

    private String midInitials;

    private String email;

    private String phone;

    private String fax;

    private String affiliation;

    private String address;

    private List<String> roles;

    public ContactDto() {
    }

    public ContactDto(int id) {
        this.id = id;
    }

    public ContactDto(ContactDto other) {
        this(other.id,
                other.firstName,
                other.lastName,
                other.midInitials,
                other.email,
                other.phone,
                other.fax,
                other.affiliation,
                other.address,
                other.roles);
    }

    public ContactDto(int id,
                      String firstName,
                      String lastName,
                      String midInitials,
                      String email,
                      String phone,
                      String fax,
                      String affiliation,
                      String address,
                      List<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.midInitials = midInitials;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.affiliation = affiliation;
        this.address = address;
        if (roles != null) {
            this.roles = new ArrayList<String>(roles);
        }
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getEmail() {
        return email;
    }

    public String getFax() {
        return fax;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMidInitials() {
        return midInitials;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactDto that = (ContactDto) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Editor editor() {
        return new Editor(this);
    }

    public boolean isContentEqual(ContactDto that) {
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (affiliation != null ? !affiliation.equals(that.affiliation) : that.affiliation != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (midInitials != null ? !midInitials.equals(that.midInitials) : that.midInitials != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        return true;
    }

    public static class Editor {

        private final ContactDto copy;

        public Editor(ContactDto contact) {
            copy = new ContactDto(contact);
        }

        public String getAddress() {
            return copy.getAddress();
        }

        public String getAffiliation() {
            return copy.getAffiliation();
        }

        public String getEmail() {
            return copy.getEmail();
        }

        public String getFax() {
            return copy.getFax();
        }

        public String getFirstName() {
            return copy.getFirstName();
        }

        public String getLastName() {
            return copy.getLastName();
        }

        public String getMidInitials() {
            return copy.getMidInitials();
        }

        public String getPhone() {
            return copy.getPhone();
        }

        public List<String> getRoles() {
            return copy.getRoles();
        }

        public void setAddress(String address) {
            copy.address = address;
        }

        public void setAffiliation(String affiliation) {
            copy.affiliation = affiliation;
        }

        public void setFax(String fax) {
            copy.fax = fax;
        }

        public void setEmail(String email) {
            copy.email = email;
        }

        public void setFirstName(String firstName) {
            copy.firstName = firstName;
        }

        public void setLastName(String lastName) {
            copy.lastName = lastName;
        }

        public void setMidInitials(String midInitials) {
            copy.midInitials = midInitials;
        }

        public void setPhone(String phone) {
            copy.phone = phone;
        }

        public void setRoles(List<String> roles) {
            copy.roles = roles;
        }

        public ContactDto copy() {
            return new ContactDto(copy);
        }
    }
}

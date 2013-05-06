package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactDto implements IsSerializable {

    private int id;

    private int tmpId;

    private String firstName;

    private String lastName;

    private String midInitials;

    private String email;

    private String phone;

    private String fax;

    private String affiliation;

    private String address;

    private List<String> roles = new ArrayList<String>();

    public ContactDto() {
    }

    public ContactDto(int id) {
        this.id = id;
        this.tmpId = id;
    }

    public ContactDto(ContactDto other) {
        this(other.id,
                other.tmpId,
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
                      int tmpId,
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
        this.tmpId = tmpId;
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

    public int getTmpId() {
        return tmpId;
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

    public ContactDto update(ContactDto toBeUpdated) {
        return new ContactDto(
                id,
                toBeUpdated.getTmpId(),
                toBeUpdated.getFirstName(),
                toBeUpdated.getLastName(),
                toBeUpdated.getMidInitials(),
                toBeUpdated.getEmail(),
                toBeUpdated.getPhone(),
                toBeUpdated.getFax(),
                toBeUpdated.getAffiliation(),
                toBeUpdated.getAddress(),
                toBeUpdated.getRoles()
        );
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

        public void setTmpId(int id) {
            copy.tmpId = id;
        }

        public ContactDto copy() {
            return new ContactDto(copy);
        }
    }
}

package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static jakarta.persistence.CascadeType.*;

@Entity
@EntityInjector
@Builder
@Table(name = "employers")
@AllArgsConstructor
@NoArgsConstructor
public class EmployerEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String login;

    private String password;

    private LocalDate hiredDate;

    @Column(insertable = false)
    private Boolean firstAccess;

    @JoinColumn
    @OneToOne(cascade = {PERSIST, MERGE, REMOVE})
    private UserDetailsEntity userDetails;

    @JoinColumn
    @OneToOne(cascade = {PERSIST, MERGE, REMOVE})
    private LocationAddressEntity locationAddress;

    @JoinColumn
    @OneToOne(cascade = {PERSIST, MERGE})
    private RoleEntity role;

    public EmployerEntity(LocalDate hiredDate, UserDetailsEntity userDetails, LocationAddressEntity locationAddress) {
        this.hiredDate = hiredDate;
        this.userDetails = userDetails;
        this.locationAddress = locationAddress;
    }

    String getLogin() {
        return login;
    }

    void setLogin(String login) {
        this.login = login;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    public UserDetailsEntity getUserDetails() {
        return userDetails;
    }

    void setUserDetails(UserDetailsEntity userDetails) {
        this.userDetails = userDetails;
    }

    public LocationAddressEntity getLocationAddress() {
        return locationAddress;
    }

    void setLocationAddress(LocationAddressEntity locationAddress) {
        this.locationAddress = locationAddress;
    }

    RoleEntity getRole() {
        return role;
    }

    void setRole(RoleEntity role) {
        this.role = role;
    }

    Boolean getFirstAccess() {
        return firstAccess;
    }

    void setFirstAccess(Boolean firstAccess) {
        this.firstAccess = firstAccess;
    }

    public String getEmailAddress() {
        return userDetails.getEmailAddress();
    }

    @Override
    public String toString() {
        return "{" +
            "login=" + login +
            ", password=******" +
            ", hiredDate=" + hiredDate +
            ", firstAccess=" + firstAccess +
            ", role=" + role +
            '}';
    }
}

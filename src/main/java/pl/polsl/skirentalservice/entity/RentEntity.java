package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.converter.RentStatusConverter;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.util.RentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@EntityInjector
@Table(name = "rents")
@NoArgsConstructor
public class RentEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String issuedIdentifier;

    private LocalDateTime issuedDateTime;

    private LocalDateTime rentDateTime;

    private LocalDateTime returnDateTime;

    private Integer tax;

    private String description;

    private BigDecimal totalPrice;

    private BigDecimal totalDepositPrice;

    @Convert(converter = RentStatusConverter.class)
    private RentStatus status;

    @JoinColumn
    @ManyToOne(cascade = {PERSIST, MERGE})
    private CustomerEntity customer;

    @JoinColumn
    @ManyToOne(cascade = {PERSIST, MERGE})
    private EmployerEntity employer;

    @OneToMany(cascade = {PERSIST, MERGE, REMOVE}, mappedBy = "rent")
    private Set<RentEquipmentEntity> equipments = new HashSet<>();

    public String getIssuedIdentifier() {
        return issuedIdentifier;
    }

    void setIssuedIdentifier(String issuedIdentifier) {
        this.issuedIdentifier = issuedIdentifier;
    }

    LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    LocalDateTime getRentDateTime() {
        return rentDateTime;
    }

    void setRentDateTime(LocalDateTime rentDateTime) {
        this.rentDateTime = rentDateTime;
    }

    LocalDateTime getReturnDateTime() {
        return returnDateTime;
    }

    void setReturnDateTime(LocalDateTime returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    public RentStatus getStatus() {
        return status;
    }

    public void setStatus(RentStatus status) {
        this.status = status;
    }

    Integer getTax() {
        return tax;
    }

    void setTax(Integer tax) {
        this.tax = tax;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    BigDecimal getTotalDepositPrice() {
        return totalDepositPrice;
    }

    public void setTotalDepositPrice(BigDecimal totalDepositPrice) {
        this.totalDepositPrice = totalDepositPrice;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    EmployerEntity getEmployer() {
        return employer;
    }

    public void setEmployer(EmployerEntity employer) {
        this.employer = employer;
    }

    public Set<RentEquipmentEntity> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<RentEquipmentEntity> equipments) {
        this.equipments = equipments;
    }

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier=" + issuedIdentifier +
            ", issuedDateTime=" + issuedDateTime +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", status=" + status +
            ", tax=" + tax +
            ", description=" + description +
            ", totalPrice=" + totalPrice +
            ", totalDepositPrice=" + totalDepositPrice +
            ", customer=" + customer +
            ", employer=" + employer +
            '}';
    }
}

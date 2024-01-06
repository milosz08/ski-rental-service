/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@EntityInjector
@Table(name = "ota_tokens")
@NoArgsConstructor
public class OtaTokenEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @Column(name = "ota_token", updatable = false)
    private String otaToken;

    @Column(name = "expired_at", insertable = false, updatable = false)
    private LocalDateTime expiredDate;

    @Column(name = "is_used", insertable = false)
    private Boolean isUsed;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private EmployerEntity employer;

    public OtaTokenEntity(String otaToken, EmployerEntity employer) {
        this.otaToken = otaToken;
        this.employer = employer;
    }

    String getOtaToken() {
        return otaToken;
    }

    void setOtaToken(String otaToken) {
        this.otaToken = otaToken;
    }

    LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    EmployerEntity getEmployer() {
        return employer;
    }

    void setEmployer(EmployerEntity employer) {
        this.employer = employer;
    }

    @Override
    public String toString() {
        return '{' +
            "otaToken=" + otaToken +
            ", expiredDate=" + expiredDate +
            ", isUsed=" + isUsed +
            ", employer=" + employer +
            '}';
    }
}

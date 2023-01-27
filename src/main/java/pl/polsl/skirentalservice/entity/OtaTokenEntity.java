/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OtaTokenEntity.java
 *  Last modified: 01/01/2023, 04:21
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import lombok.*;
import jakarta.persistence.*;

import pl.polsl.skirentalservice.core.db.*;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "ota_tokens")
@NoArgsConstructor
public class OtaTokenEntity extends AuditableEntity {

    @Column(name = "ota_token", updatable = false)                       private String otaToken;
    @Column(name = "expired_at", insertable = false, updatable = false)  private LocalDateTime expiredDate;
    @Column(name = "is_used", insertable = false)                        private Boolean isUsed;

    @ManyToOne(cascade = { MERGE, PERSIST }, fetch = LAZY)
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private EmployerEntity employer;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public OtaTokenEntity(String otaToken, EmployerEntity employer) {
        this.otaToken = otaToken;
        this.employer = employer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "otaToken='" + otaToken +
            ", expiredDate=" + expiredDate +
            ", isUsed=" + isUsed +
            ", employer=" + employer +
            '}';
    }
}

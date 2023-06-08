/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OtaTokenEntity.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.entity;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "ota_tokens")
@NoArgsConstructor
public class OtaTokenEntity extends AuditableEntity {

    @Column(name = "ota_token", updatable = false)                       private String otaToken;
    @Column(name = "expired_at", insertable = false, updatable = false)  private LocalDateTime expiredDate;
    @Column(name = "is_used", insertable = false)                        private Boolean isUsed;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
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

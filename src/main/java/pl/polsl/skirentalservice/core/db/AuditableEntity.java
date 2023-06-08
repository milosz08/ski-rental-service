/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AuditableEntity.java
 * Last modified: 6/3/23, 12:26 AM
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

package pl.polsl.skirentalservice.core.db;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@EntityInjector
@MappedSuperclass
@NoArgsConstructor
public abstract class AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")                                                    private Long id;

    @Column(name = "created_at", insertable = false, updatable = false)     private Date createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)     private Date updatedAt;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    Date getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    Date getUpdatedAt() {
        return updatedAt;
    }

    void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "id=" + id +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}

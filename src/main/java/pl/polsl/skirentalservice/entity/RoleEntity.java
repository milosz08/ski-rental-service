/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RoleEntity.java
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

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity extends AuditableEntity {

    @Column(name = "role_name")     private String roleName;
    @Column(name = "alias")         private Character alias;
    @Column(name = "role_eng")      private String roleEng;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getRoleName() {
        return roleName;
    }

    void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Character getAlias() {
        return alias;
    }

    void setAlias(Character alias) {
        this.alias = alias;
    }

    String getRoleEng() {
        return roleEng;
    }

    void setRoleEng(String roleEng) {
        this.roleEng = roleEng;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "roleName='" + roleName +
            ", alias=" + alias +
            ", roleEng='" + roleEng +
            '}';
    }
}

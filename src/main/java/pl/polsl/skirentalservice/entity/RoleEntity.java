/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

@Entity
@EntityInjector
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity extends AuditableEntity {

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "alias")
    private Character alias;

    @Column(name = "role_eng")
    private String roleEng;

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

    @Override
    public String toString() {
        return "{" +
            "roleName='" + roleName +
            ", alias=" + alias +
            ", roleEng='" + roleEng +
            '}';
    }
}

package pl.polsl.skirentalservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityInjector
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String roleName;

    private Character alias;

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
            "roleName=" + roleName +
            ", alias=" + alias +
            ", roleEng=" + roleEng +
            '}';
    }
}

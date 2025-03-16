package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@EntityInjector
@Table(name = "ota_tokens")
@NoArgsConstructor
public class OtaTokenEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @Column(updatable = false)
    private String otaToken;

    @Column(insertable = false, updatable = false)
    private LocalDateTime expiredAt;

    @Column(insertable = false)
    private Boolean isUsed;

    @JoinColumn
    @ManyToOne(cascade = {MERGE, PERSIST})
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

    LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
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
            ", expiredAt=" + expiredAt +
            ", isUsed=" + isUsed +
            ", employer=" + employer +
            '}';
    }
}

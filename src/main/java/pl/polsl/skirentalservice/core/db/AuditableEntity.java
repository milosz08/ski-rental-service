/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AuditableEntity.java
 *  Last modified: 01/01/2023, 05:13
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.db;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Date;

//----------------------------------------------------------------------------------------------------------------------

@EntityInjector
@MappedSuperclass
@NoArgsConstructor
public abstract class AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")            private Long id;
    @Column(name = "created_at")    private Date createdAt;
    @Column(name = "updated_at")    private Date updatedAt;

    //------------------------------------------------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    void setId(Long id) {
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

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

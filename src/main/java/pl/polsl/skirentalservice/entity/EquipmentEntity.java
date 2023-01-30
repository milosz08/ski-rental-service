/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EquipmentEntity.java
 *  Last modified: 26/01/2023, 15:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.*;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.core.db.*;
import pl.polsl.skirentalservice.util.Gender;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.EnumType.STRING;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "equipments")
@NoArgsConstructor
public class EquipmentEntity extends AuditableEntity {

    @Column(name = "name")                          private String name;
    @Column(name = "model")                         private String model;
    @Column(name = "gender") @Enumerated(STRING)    private Gender gender;
    @Column(name = "description")                   private String description;
    @Column(name = "barcode")                       private String barcode;
    @Column(name = "count_in_store")                private Integer countInStore;
    @Column(name = "size")                          private BigDecimal size;
    @Column(name = "price_per_hour")                private BigDecimal pricePerHour;
    @Column(name = "price_for_next_hour")           private BigDecimal priceForNextHour;
    @Column(name = "price_per_day")                 private BigDecimal pricePerDay;
    @Column(name = "value_cost")                    private BigDecimal valueCost;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private EquipmentTypeEntity equipmentType;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private EquipmentBrandEntity equipmentBrand;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private EquipmentColorEntity equipmentColor;

    @OneToMany(fetch = LAZY, cascade = { PERSIST, MERGE }, mappedBy = "equipment")
    private Set<RentEquipmentEntity> rentsEquipments = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getModel() {
        return model;
    }

    void setModel(String model) {
        this.model = model;
    }

    Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    Integer getCountInStore() {
        return countInStore;
    }

    void setCountInStore(Integer countInStore) {
        this.countInStore = countInStore;
    }

    BigDecimal getSize() {
        return size;
    }

    void setSize(BigDecimal size) {
        this.size = size;
    }

    BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    BigDecimal getPriceForNextHour() {
        return priceForNextHour;
    }

    void setPriceForNextHour(BigDecimal priceForNextHour) {
        this.priceForNextHour = priceForNextHour;
    }

    BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    BigDecimal getValueCost() {
        return valueCost;
    }

    void setValueCost(BigDecimal valueCost) {
        this.valueCost = valueCost;
    }

    EquipmentTypeEntity getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentTypeEntity equipmentType) {
        this.equipmentType = equipmentType;
    }

    EquipmentBrandEntity getEquipmentBrand() {
        return equipmentBrand;
    }

    public void setEquipmentBrand(EquipmentBrandEntity equipmentBrand) {
        this.equipmentBrand = equipmentBrand;
    }

    EquipmentColorEntity getEquipmentColor() {
        return equipmentColor;
    }

    public void setEquipmentColor(EquipmentColorEntity equipmentColor) {
        this.equipmentColor = equipmentColor;
    }

    Set<RentEquipmentEntity> getRentsEquipments() {
        return rentsEquipments;
    }

    public void setRentsEquipments(Set<RentEquipmentEntity> rentsEquipments) {
        this.rentsEquipments = rentsEquipments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            ", model='" + model +
            ", gender=" + gender +
            ", description='" + description +
            ", barcode='" + barcode +
            ", countInStore=" + countInStore +
            ", size=" + size +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", valueCost=" + valueCost +
            ", equipmentType=" + equipmentType +
            ", equipmentBrand=" + equipmentBrand +
            ", equipmentColor=" + equipmentColor +
            '}';
    }
}

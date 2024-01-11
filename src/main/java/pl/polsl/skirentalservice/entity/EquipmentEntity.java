/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.converter.GenderConverter;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.util.Gender;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@EntityInjector
@Table(name = "equipments")
@NoArgsConstructor
public class EquipmentEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String name;

    private String model;

    private String description;

    private String barcode;

    private Integer countInStore;

    private Integer availableCount;

    private BigDecimal size;

    private BigDecimal pricePerHour;

    private BigDecimal priceForNextHour;

    private BigDecimal pricePerDay;

    private BigDecimal valueCost;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE })
    private EquipmentTypeEntity type;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE })
    private EquipmentBrandEntity brand;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE })
    private EquipmentColorEntity color;

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "equipment")
    private Set<RentEquipmentEntity> rentsEquipments = new HashSet<>();

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getModel() {
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

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
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

    public EquipmentTypeEntity getType() {
        return type;
    }

    public void setType(EquipmentTypeEntity type) {
        this.type = type;
    }

    EquipmentBrandEntity getBrand() {
        return brand;
    }

    public void setBrand(EquipmentBrandEntity brand) {
        this.brand = brand;
    }

    EquipmentColorEntity getColor() {
        return color;
    }

    public void setColor(EquipmentColorEntity color) {
        this.color = color;
    }

    Set<RentEquipmentEntity> getRentsEquipments() {
        return rentsEquipments;
    }

    public void setRentsEquipments(Set<RentEquipmentEntity> rentsEquipments) {
        this.rentsEquipments = rentsEquipments;
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", model=" + model +
            ", gender=" + gender +
            ", description=" + description +
            ", barcode=" + barcode +
            ", countInStore=" + countInStore +
            ", availableCount=" + availableCount +
            ", size=" + size +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", valueCost=" + valueCost +
            ", type=" + type +
            ", brand=" + brand +
            ", color=" + color +
            '}';
    }
}

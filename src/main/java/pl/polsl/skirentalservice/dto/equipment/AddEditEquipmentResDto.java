/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AddEditEquipmentResDto.java
 * Last modified: 6/2/23, 11:49 PM
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

package pl.polsl.skirentalservice.dto.equipment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormSelectsDto;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AddEditEquipmentResDto {
    private FormValueInfoTupleDto name;
    private FormValueInfoTupleDto model;
    private FormValueInfoTupleDto description;
    private FormValueInfoTupleDto countInStore;
    private FormValueInfoTupleDto size;
    private FormValueInfoTupleDto pricePerHour;
    private FormValueInfoTupleDto priceForNextHour;
    private FormValueInfoTupleDto pricePerDay;
    private FormValueInfoTupleDto valueCost;
    private FormSelectsDto types = new FormSelectsDto();
    private FormSelectsDto brands = new FormSelectsDto();
    private FormSelectsDto colors = new FormSelectsDto();
    private List<FormSelectTupleDto> genders = Gender.getGendersWithUnisex();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentResDto(ValidatorSingleton validator, AddEditEquipmentReqDto reqDto) {
        this.name = validator.validateField(reqDto, "name", reqDto.getName());
        this.model = validator.validateField(reqDto, "model", reqDto.getModel());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
        this.countInStore = validator.validateField(reqDto, "countInStore", reqDto.getCountInStore());
        this.size = validator.validateField(reqDto, "size", reqDto.getSize());
        this.pricePerHour = validator.validateField(reqDto, "pricePerHour", reqDto.getPricePerHour());
        this.priceForNextHour = validator.validateField(reqDto, "priceForNextHour", reqDto.getPriceForNextHour());
        this.pricePerDay = validator.validateField(reqDto, "pricePerDay", reqDto.getPricePerDay());
        this.valueCost = validator.validateField(reqDto, "valueCost", reqDto.getValueCost());
        this.types = validator.validateSelectField(reqDto, "type", this.types, reqDto.getType());
        this.brands = validator.validateSelectField(reqDto, "brand", this.brands, reqDto.getBrand());
        this.colors = validator.validateSelectField(reqDto, "color", this.colors, reqDto.getColor());
        this.genders = Gender.getSelectedGenderWithUnisex(reqDto.getGender());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void insertTypesSelects(List<FormSelectTupleDto> selects) {
        selects.add(0, new FormSelectTupleDto(true, "none", "wybierz typ..."));
        types.getSelects().addAll(selects);
        setSelectedField(types);
    }

    public void insertBrandsSelects(List<FormSelectTupleDto> selects) {
        selects.add(0, new FormSelectTupleDto(true, "none", "wybierz markę..."));
        brands.getSelects().addAll(selects);
        setSelectedField(brands);
    }

    public void insertColorsSelects(List<FormSelectTupleDto> selects) {
        selects.add(0, new FormSelectTupleDto(true, "none", "wybierz kolor..."));
        colors.getSelects().addAll(selects);
        setSelectedField(colors);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setSelectedField(FormSelectsDto attr) {
        for (final FormSelectTupleDto select: attr.getSelects()) {
            if (select.getValue().equals(attr.getSelected())) select.setIsSelected("selected");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", model=" + model +
            ", description=" + description +
            ", countInStore=" + countInStore +
            ", size=" + size +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", valueCost=" + valueCost +
            ", types=" + types +
            ", brands=" + brands +
            ", colors=" + colors +
            ", genders=" + genders +
            '}';
    }
}

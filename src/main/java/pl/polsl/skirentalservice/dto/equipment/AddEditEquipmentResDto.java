package pl.polsl.skirentalservice.dto.equipment;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.FormSelectsDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;
import pl.polsl.skirentalservice.util.Gender;

import java.util.List;

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

    public AddEditEquipmentResDto(ValidatorBean validator, AddEditEquipmentReqDto reqDto) {
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

    private void setSelectedField(FormSelectsDto attr) {
        for (final FormSelectTupleDto select : attr.getSelects()) {
            if (select.getValue().equals(attr.getSelected())) select.setIsSelected("selected");
        }
    }

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

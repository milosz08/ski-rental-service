/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;
import pl.polsl.skirentalservice.util.Gender;

import java.util.List;

@Data
@NoArgsConstructor
public class AddEditCustomerResDto {
    private FormValueInfoTupleDto firstName;
    private FormValueInfoTupleDto lastName;
    private FormValueInfoTupleDto pesel;
    private FormValueInfoTupleDto phoneNumber;
    private FormValueInfoTupleDto bornDate;
    private FormValueInfoTupleDto emailAddress;
    private FormValueInfoTupleDto street;
    private FormValueInfoTupleDto buildingNo;
    private FormValueInfoTupleDto apartmentNo;
    private FormValueInfoTupleDto city;
    private FormValueInfoTupleDto postalCode;
    private List<FormSelectTupleDto> genders = Gender.getGenders();

    public AddEditCustomerResDto(ValidatorBean validator, AddEditCustomerReqDto reqDto) {
        this.firstName = validator.validateField(reqDto, "firstName", reqDto.getFirstName());
        this.lastName = validator.validateField(reqDto, "lastName", reqDto.getLastName());
        this.pesel = validator.validateField(reqDto, "pesel", reqDto.getPesel());
        this.phoneNumber = validator.validateField(reqDto, "phoneNumber", reqDto.getPhoneNumber());
        this.bornDate = validator.validateField(reqDto, "bornDate", reqDto.getBornDate());
        this.emailAddress = validator.validateField(reqDto, "emailAddress", reqDto.getEmailAddress());
        this.street = validator.validateField(reqDto, "street", reqDto.getStreet());
        this.buildingNo = validator.validateField(reqDto, "buildingNo", reqDto.getBuildingNo());
        this.apartmentNo = validator.validateField(reqDto, "apartmentNo", reqDto.getApartmentNo());
        this.city = validator.validateField(reqDto, "city", reqDto.getCity());
        this.postalCode = validator.validateField(reqDto, "postalCode", reqDto.getPostalCode());
        this.genders = List.of(
            Gender.MALE.convertToTuple(reqDto.getGender()),
            Gender.FEMALE.convertToTuple(reqDto.getGender())
        );
    }

    @Override
    public String toString() {
        return "{" +
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", pesel=" + pesel +
            ", phoneNumber=" + phoneNumber +
            ", bornDate=" + bornDate +
            ", emailAddress=" + emailAddress +
            ", street=" + street +
            ", buildingNo=" + buildingNo +
            ", apartmentNo=" + apartmentNo +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}

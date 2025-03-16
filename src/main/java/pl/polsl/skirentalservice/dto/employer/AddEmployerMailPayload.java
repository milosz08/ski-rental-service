package pl.polsl.skirentalservice.dto.employer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEmployerMailPayload {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String bornDate;
    private String hiredDate;
    private String address;
    private String gender;
    private String emailAddress;
    private String emailPassword;

    public AddEmployerMailPayload(AddEditEmployerReqDto reqDto, String emailAddress, String emailPassword) {
        this.fullName = reqDto.getFirstName() + " " + reqDto.getLastName();
        this.pesel = reqDto.getPesel();
        this.phoneNumber = "+48" + reqDto.getPhoneNumber();
        this.bornDate = reqDto.getBornDate();
        this.hiredDate = reqDto.getHiredDate();
        this.address = reqDto.getFullAddress();
        this.gender = reqDto.getGender().getName();
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;
    }
}

package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartReqDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;

@Local
public interface CartEquipmentService {
    void addEquipmentToCart(AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto,
                            InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, Long equipmentId);

    void editEquipmentFromCart(AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto,
                               InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, Long equipmentId);

    void deleteEquipmentFromCart(InMemoryRentDataDto rentData, Long equipmentId, LoggedUserDataDto loggedUser);
}

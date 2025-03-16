package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.change_password.ChangeForgottenPasswordReqDto;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;
import pl.polsl.skirentalservice.dto.first_access.FirstAccessReqDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;

@Local
public interface AuthService {
    LoggedUserDataDto loginUser(LoginFormReqDto reqDto);

    void checkUserAndSendToken(RequestToChangePasswordReqDto reqDto, WebServletRequest req);

    boolean isFirstAccessOrIsSellerAccount(LoggedUserDataDto employer);

    void changePassword(String token, ChangeForgottenPasswordReqDto reqDto, WebServletRequest req);

    ChangePasswordEmployerDetailsDto getChangePasswordEmployerDetails(String token, WebServletRequest req);

    void prepareEmployerAccount(FirstAccessReqDto reqDto, LoggedUserDataDto loggedUserDataDto);

    boolean checkIfTokenIsExist(String token);
}

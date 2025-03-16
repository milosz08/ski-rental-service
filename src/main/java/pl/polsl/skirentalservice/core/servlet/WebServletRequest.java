package pl.polsl.skirentalservice.core.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.UserRole;

import java.io.IOException;
import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Builder
public class WebServletRequest {
    private HttpServletRequest httpReq;
    private HttpServletResponse httpRes;

    public WebServletRequest(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        this.httpReq = httpReq;
        this.httpRes = httpRes;
    }

    public void addAttribute(String name, Object data) {
        httpReq.setAttribute(name, data);
    }

    public <T> T getFromSession(Attribute attribute, Class<T> clazz, boolean isDestroy, boolean isInstantiated) {
        final HttpSession httpSession = httpReq.getSession();
        T object = clazz.cast(httpSession.getAttribute(attribute.getAttributeName()));
        if (object == null && isInstantiated) {
            try {
                object = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {
            }
        }
        if (isDestroy) {
            httpSession.removeAttribute(attribute.getAttributeName());
        }
        return object;
    }

    public <T> T getFromSessionAndDestroy(Attribute attribute, Class<T> clazz) {
        return getFromSession(attribute, clazz, true, false);
    }

    public <T> T getFromSessionOrCreate(Attribute attribute, Class<T> clazz) {
        return getFromSession(attribute, clazz, true, true);
    }

    public <T> T getFromSession(Attribute attribute, Class<T> clazz) {
        return getFromSession(attribute, clazz, false, false);
    }

    public AlertTupleDto getAlertAndDestroy(Attribute attribute) {
        return getFromSession(attribute, AlertTupleDto.class, true, true);
    }

    public AttributeModalResDto getModalAndDestroy(Attribute attribute) {
        return getFromSession(attribute, AttributeModalResDto.class, true, false);
    }

    public void setSessionAttribute(Attribute attribute, Object data) {
        final HttpSession httpSession = httpReq.getSession();
        httpSession.setAttribute(attribute.getAttributeName(), data);
    }

    public void deleteSessionAttribute(Attribute attribute) {
        final HttpSession httpSession = httpReq.getSession();
        httpSession.removeAttribute(attribute.getAttributeName());
    }

    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(httpReq.getAttribute(name));
    }

    public LoggedUserDataDto getLoggedUser() {
        return getFromSession(SessionAttribute.LOGGED_USER_DETAILS, LoggedUserDataDto.class);
    }

    public void sendRedirect(String url) throws IOException {
        httpRes.sendRedirect(url);
    }

    public void forwardRequest(String path) throws ServletException, IOException {
        httpReq.getRequestDispatcher(path).forward(httpReq, httpRes);
    }

    public String getParameter(String name) {
        return httpReq.getParameter(name);
    }

    public String getParameter(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(httpReq.getParameter(name), defaultValue);
    }

    public String getLoggedUserRole() {
        final LoggedUserDataDto loggedUser = getLoggedUser();
        if (loggedUser == null) {
            return StringUtils.EMPTY;
        }
        return loggedUser.getRoleEng();
    }

    public boolean hasRole(UserRole role) {
        final LoggedUserDataDto loggedUser = getLoggedUser();
        if (loggedUser == null) {
            return false;
        }
        return Objects.equals(loggedUser.getRoleAlias(), role.getAlias());
    }

    public String getBaseRequestPath() {
        final boolean isHttp = httpReq.getScheme().equals("http") && httpReq.getServerPort() == 80;
        final boolean isHttps = httpReq.getScheme().equals("https") && httpReq.getServerPort() == 443;
        return new StringJoiner(StringUtils.EMPTY)
            .add(httpReq.getScheme())
            .add("://")
            .add(httpReq.getServerName())
            .add(isHttp || isHttps ? "" : ":")
            .add(String.valueOf(httpReq.getServerPort()))
            .toString();
    }

    public String getContextPath() {
        return httpReq.getContextPath();
    }
}

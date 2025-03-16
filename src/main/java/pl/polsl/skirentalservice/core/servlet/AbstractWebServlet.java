package pl.polsl.skirentalservice.core.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.util.PageTitle;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class AbstractWebServlet extends HttpServlet {
    private static final String JSP_PAGES_CONTAINER = "/WEB-INF/pages/";
    private static final String JSP_PAGES_EXT = ".jsp";

    private final ServerConfigBean serverConfigBean;

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final WebServletRequest webServletRequest = new WebServletRequest(req, res);
        try {
            final WebServletResponse webServletResponse = httpGetCall(webServletRequest);
            makeTerminateCall(webServletRequest, webServletResponse);
        } catch (WebServletRedirectException ex) {
            res.sendRedirect("/" + ex.getRedirectTo());
        }
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final WebServletRequest webServletRequest = new WebServletRequest(req, res);
        try {
            final WebServletResponse webServletResponse = httpPostCall(webServletRequest);
            makeTerminateCall(webServletRequest, webServletResponse);
        } catch (WebServletRedirectException ex) {
            if (ex.getAttribute() != null) {
                webServletRequest.setSessionAttribute(ex.getAttribute(), ex.getData());
            }
            res.sendRedirect("/" + ex.getRedirectTo());
        }
    }

    private void makeTerminateCall(WebServletRequest req, WebServletResponse res)
        throws ServletException, IOException {
        if (res.mode().equals(HttpMethodMode.JSP_GENERATOR)) {
            final PageTitle pageTitle = res.pageTitle();
            req.addAttribute("title", pageTitle == null
                ? serverConfigBean.getTitlePageTag()
                : pageTitle.getName(serverConfigBean));
        }
        switch (res.mode()) {
            case REDIRECT -> req.sendRedirect("/" + res.pageOrRedirectTo());
            case JSP_GENERATOR -> req.forwardRequest(JSP_PAGES_CONTAINER + res.pageOrRedirectTo() + JSP_PAGES_EXT);
        }
    }

    private WebServletResponse redirectOnEmptyMethod() {
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("404")
            .build();
    }

    protected WebServletResponse httpGetCall(WebServletRequest req) {
        return redirectOnEmptyMethod();
    }

    protected WebServletResponse httpPostCall(WebServletRequest req) {
        return redirectOnEmptyMethod();
    }
}

package pl.polsl.skirentalservice.filter.method;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/owner/delete-equipment-type",
    "/owner/delete-equipment-brand",
    "/owner/delete-equipment-color",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectOnPostMethodRequestFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String userId = req.getParameter("id");
        if (req.getMethod().equals("POST") || !StringUtils.isNumeric(userId)) {
            res.sendRedirect("/owner/add-equipment");
            return;
        }
        chain.doFilter(req, res);
    }
}

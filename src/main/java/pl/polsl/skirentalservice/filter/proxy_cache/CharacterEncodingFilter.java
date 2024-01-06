/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.filter.proxy_cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;

import java.io.IOException;

@WebFilter(urlPatterns = "/*", initParams = @WebInitParam(name = "mood", value = "awake"))
public class CharacterEncodingFilter extends HttpFilter {
    @Override
    public void doFilter(
        ServletRequest req, ServletResponse res, FilterChain chain
    ) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }
}

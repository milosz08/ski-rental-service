/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AbstractWebFilter extends HttpFilter {
    @Override
    protected final void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final WebServletRequest webServletRequest = new WebServletRequest(req, res);
        doWebFilter(webServletRequest, chain);
    }

    protected void continueRequest(WebServletRequest req, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(req.getHttpReq(), req.getHttpRes());
    }

    protected abstract void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException;
}

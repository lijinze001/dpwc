package com.accelerator.framework.spring.boot.autoconfigure.web.filter;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class LogfileCharsetFilter extends OncePerRequestFilter {

    private String contentType;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setContentType(contentType);
        filterChain.doFilter(request, response);
    }

    public void setCharset(String charset) {
        MediaType mediaType = new MediaType(MediaType.TEXT_PLAIN, Charset.forName(charset));
        contentType = mediaType.toString();
    }

}

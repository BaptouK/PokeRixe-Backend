package fr.baptouk.pokerixe.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;


@Component
public class SameSiteCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        chain.doFilter(req, response);

        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean first = true;
        for (String header : headers) {
            if (!header.contains("SameSite")) {
                header = header + "; SameSite=Lax";
            }
            if (first) {
                response.setHeader(HttpHeaders.SET_COOKIE, header);
                first = false;
            } else {
                response.addHeader(HttpHeaders.SET_COOKIE, header);
            }
        }
    }
}
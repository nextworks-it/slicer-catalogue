package it.nextworks.nfvmano.evecatalogue.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    private ArrayList<String> ignoreHosts= new ArrayList<>();


    public void addIgnoreHost(String host){
        log.debug("IgnoreHost:"+host);
        ignoreHosts.add(host);
    }

    @Override
    public void init(FilterConfig filterConfig){
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        final String val = req.getHeader("Authorization");
        //log.debug("Authorization header : " + val);
        log.debug("RequestHost:"+request.getRemoteHost());
        if(ignoreHosts.contains(request.getRemoteHost())){
            log.debug("Ignoring host:"+request.getRemoteHost());
            log.debug("Skipping filter chain");
            request.getRequestDispatcher(((HttpServletRequest) request).getServletPath()).forward(request, response);
        }else if (val == null || val.length() == 0) {
            //((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The 'Authorization' header is missing. Please provide a valid token.");
            log.error("The 'Authorization' header is missing. Please provide a valid bearer token.\n");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().print("The 'Authorization' header is missing. Please provide a valid bearer token.\n");
        } else if(!val.matches("Bearer (.+)")){
            log.error("Token format is not valid. Please provide a valid bearer token.\n");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().print("Token format is not valid. Please provide a valid bearer token.\n");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy(){

    }
}

package com.example.personalbutler.util;

import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.service.UserService;
import com.example.personalbutler.service.UserTokenService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @description: 跨域问题解决方案
 * @author: Mask
 * @time: 2020/9/18 3:53 下午
 */
@Component
@WebFilter(urlPatterns = {"/api/*"})
@Order(1)
public class AuthenticationFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private List<String> ignoreList = null;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserService userService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ignoreList = Arrays.asList(
                "/api/user/login",
                "/api/user/create"
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUrl = request.getRequestURI();
        logger.debug("AuthenticationFilter fires: {}", requestUrl);
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            logger.trace("skip options request");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (!requestUrl.startsWith("/api/") || isIgnoreApi(requestUrl)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") != null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            } else {
                // check http basic auth
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    StringTokenizer st = new StringTokenizer(authHeader);
                    if (st.hasMoreTokens()) {
                        String basic = st.nextToken();
                        if (basic.equalsIgnoreCase("Basic")) {
                            try {
                                String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                                logger.debug("checking HTTP Basic authentication: Credentials is {}", credentials);
                                int p = credentials.indexOf(":");
                                if (p != -1) {
                                    String _username = credentials.substring(0, p).trim();
                                    String _password = credentials.substring(p + 1).trim();
                                    UserEntity userEntity = userService.checkLogin(_username, _password);
                                    if (userEntity != null) {
                                        // put userEntity to session
                                        logger.debug("logged from HTTP Basic authentication");
                                        session.setAttribute("user", userEntity);
                                        filterChain.doFilter(servletRequest, servletResponse);
                                        return;
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException("Couldn't retrieve authentication", e);
                            }
                        }
                    }
                }

                // checking from token

                String token = request.getHeader("x-auth-token");
                if (token == null || token.isEmpty()) {
                    token = request.getParameter("x-auth-token");
                    logger.debug("get token from request parameter");
                }
                String deviceId = getDeviceId(request);
                logger.debug("checking token: token = {}, deviceId = {}", token, deviceId);
                UserEntity userEntity = userTokenService.checkToken(token, deviceId);
                if (userEntity != null) {
                    logger.debug("logged from token");
                    session.setAttribute("user", userEntity);
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    logger.info("access to url denied : {}", requestUrl);
                    HttpServletResponse response = (HttpServletResponse) servletResponse;
                    response.setStatus(401);
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"msg\":\"Unauthorized\"}");
                }
            }

        }

    }

    @Override
    public void destroy() {

    }

    private boolean isIgnoreApi(String requestUrl) {
        for (String ignoreItem : ignoreList) {
            if (requestUrl.startsWith(ignoreItem) || requestUrl.matches(ignoreItem)) {
                return true;
            }
        }
        return false;
    }

    public static String getDeviceId(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}

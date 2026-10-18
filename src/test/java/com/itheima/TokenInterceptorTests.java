package com.itheima;

import com.itheima.exception.JwtSessionUnavailableException;
import com.itheima.interceptor.TokenInterceptor;
import com.itheima.service.JwtSessionService;
import com.itheima.utils.CurrentHolder;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenInterceptorTests {
    private JwtUtils jwtUtils;
    private JwtSessionService sessionService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private TokenInterceptor interceptor;
    private Claims claims;

    @BeforeEach
    void setUp() {
        jwtUtils = mock(JwtUtils.class);
        sessionService = mock(JwtSessionService.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        interceptor = new TokenInterceptor(jwtUtils, sessionService);

        claims = new DefaultClaims();
        claims.put("id", 7);
        claims.setId("session-id");
    }

    @AfterEach
    void cleanThreadLocal() {
        CurrentHolder.remove();
    }

    @Test
    void acceptsSignedAndPersistedSession() throws Exception {
        when(request.getHeader("token")).thenReturn("jwt-token");
        when(jwtUtils.parseJWT("jwt-token")).thenReturn(claims);
        when(sessionService.isActive("jwt-token", claims)).thenReturn(true);

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(7, CurrentHolder.getCurrentId());
    }

    @Test
    void rejectsRevokedOrUnknownSession() throws Exception {
        when(request.getHeader("token")).thenReturn("jwt-token");
        when(jwtUtils.parseJWT("jwt-token")).thenReturn(claims);
        when(sessionService.isActive("jwt-token", claims)).thenReturn(false);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void failsClosedWhenSessionStoreIsUnavailable() throws Exception {
        when(request.getHeader("token")).thenReturn("jwt-token");
        when(jwtUtils.parseJWT("jwt-token")).thenReturn(claims);
        when(sessionService.isActive("jwt-token", claims))
                .thenThrow(new JwtSessionUnavailableException("unavailable", new RuntimeException()));

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
}

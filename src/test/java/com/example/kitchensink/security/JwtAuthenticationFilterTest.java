package com.example.kitchensink.security;

import com.example.kitchensink.service.BlacklistToken;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    JwtService jwtService = mock(JwtService.class);
    BlacklistToken blacklistToken = mock(BlacklistToken.class);

    JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtService, blacklistToken);

    @Test
    void shouldPassFilterWhenNoToken() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        when(jwtService.getTokenFromCookie(request)).thenReturn(null);

        filter.doFilterInternal(request,response,chain);

        verify(chain,times(1)).doFilter(request,response);
    }
}
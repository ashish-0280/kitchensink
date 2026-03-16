package com.example.kitchensink.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionTest {

    GlobalException handler = new GlobalException();

    @Test
    void handleNotFound_shouldReturn404(){

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        ResourceNotFoundException ex =
                new ResourceNotFoundException("not found");

        var response = handler.handleNotFound(ex, request);

        assertEquals(404,response.getStatusCodeValue());
    }

}
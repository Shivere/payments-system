package com.example.mamlaka.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResponseDtoTest {

    private ResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        // Initialize the ResponseDto object with some default values
        responseDto = new ResponseDto("200", "Success");
    }

    @Test
    public void testConstructorAndGetters() {
        // Verify that the constructor correctly initializes the fields
        assertEquals("200", responseDto.getStatusCode());
        assertEquals("Success", responseDto.getStatusMsg());
    }

    @Test
    public void testSetAndGetStatusCode() {
        String newStatusCode = "404";
        responseDto.setStatusCode(newStatusCode);

        // Verify that the status code is updated and retrieved correctly
        assertEquals(newStatusCode, responseDto.getStatusCode());
    }

    @Test
    public void testSetAndGetStatusMsg() {
        String newStatusMsg = "Not Found";
        responseDto.setStatusMsg(newStatusMsg);

        // Verify that the status message is updated and retrieved correctly
        assertEquals(newStatusMsg, responseDto.getStatusMsg());
    }

    @Test
    public void testAllFieldsTogether() {
        String expectedStatusCode = "500";
        String expectedStatusMsg = "Internal Server Error";

        // Set both fields
        responseDto.setStatusCode(expectedStatusCode);
        responseDto.setStatusMsg(expectedStatusMsg);

        // Verify that both fields are correctly set and retrieved
        assertEquals(expectedStatusCode, responseDto.getStatusCode());
        assertEquals(expectedStatusMsg, responseDto.getStatusMsg());
    }
}

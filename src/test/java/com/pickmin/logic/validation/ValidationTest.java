package com.pickmin.logic.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pickmin.logic.validation.Validation;

public class ValidationTest {

    @Test
    public void testValidAddressFormat() {
        assertTrue(Validation.isValidAddressFormat("Keizersgracht 101, 1015 CV Amsterdam"), 
                   "Het adres 'Keizersgracht 101, 1015 CV Amsterdam' zou geldig moeten zijn.");
        assertTrue(Validation.isValidAddressFormat("Coolsingel 42, 3012 AG Rotterdam"), 
                   "Het adres 'Coolsingel 42, 3012 AG Rotterdam' zou geldig moeten zijn.");
        assertTrue(Validation.isValidAddressFormat("Lange Voorhout 56, 2514 EJ Den Haag"), 
                   "Het adres 'Lange Voorhout 56, 2514 EJ Den Haag' zou geldig moeten zijn.");
    }

    @Test
    public void testInvalidAddressFormat() {
        assertFalse(Validation.isValidAddressFormat("Ongeldig adres, 12345 ZZ Testplaats"), 
                    "Het adres 'Ongeldig adres, 12345 ZZ Testplaats' zou ongeldig moeten zijn.");
        assertFalse(Validation.isValidAddressFormat("Keizersgracht, Amsterdam"), 
                    "Het adres 'Keizersgracht, Amsterdam' zou ongeldig moeten zijn.");
        assertFalse(Validation.isValidAddressFormat("1015 CV Amsterdam"), 
                    "Het adres '1015 CV Amsterdam' zou ongeldig moeten zijn.");
        assertFalse(Validation.isValidAddressFormat("Keizersgracht 101, Amsterdam"), 
                    "Het adres 'Keizersgracht 101, Amsterdam' zou ongeldig moeten zijn.");
    }
}
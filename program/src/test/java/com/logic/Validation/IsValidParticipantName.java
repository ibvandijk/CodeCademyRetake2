package com.logic.Validation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for InputValidation.isValidParticipantName method.
 * This method tests if a given participant name is properly capitalized,
 * meaning the first letter of each word should be uppercase and all other letters should be lowercase.
 */
public class IsValidParticipantName{

    /**
     * Test to verify that a single word name with proper capitalization is recognized as valid.
     */
    @Test
    public void testIsValidParticipantName_ProperlyCapitalizedSingleName() {
        assertTrue("A properly capitalized single name should be valid", 
            InputValidation.isValidParticipantName("John"));
    }

    /**
     * Test to verify that a full name with each word properly capitalized is recognized as valid.
     */
    @Test
    public void testIsValidParticipantName_ProperlyCapitalizedFullName() {
        assertTrue("A properly capitalized full name should be valid", 
            InputValidation.isValidParticipantName("John Doe"));
    }

    /**
     * Test to verify that a full name with incorrect capitalization (e.g., second word not capitalized) is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_IncorrectlyCapitalizedFullName() {
        assertFalse("A full name with incorrect capitalization should be invalid", 
            InputValidation.isValidParticipantName("John doe"));
    }

    /**
     * Test to verify that a name written entirely in uppercase letters is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_AllUpperCase() {
        assertFalse("A name in all uppercase letters should be invalid", 
            InputValidation.isValidParticipantName("JOHN DOE"));
    }

    /**
     * Test to verify that a name written entirely in lowercase letters is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_AllLowerCase() {
        assertFalse("A name in all lowercase letters should be invalid", 
            InputValidation.isValidParticipantName("john doe"));
    }

    /**
     * Test to verify that an empty string is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_EmptyString() {
        assertFalse("An empty string should be invalid", 
            InputValidation.isValidParticipantName(""));
    }

    /**
     * Test to verify that null as a name input is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_Null() {
        assertFalse("Null as a name should be invalid", 
            InputValidation.isValidParticipantName(null));
    }

    /**
     * Test to verify that a name with extra spaces between words is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_ExtraSpaces() {
        assertFalse("A name with extra spaces should be invalid", 
            InputValidation.isValidParticipantName("John  Doe"));
    }

    /**
     * Test to verify that a name with leading or trailing spaces is recognized as invalid.
     */
    @Test
    public void testIsValidParticipantName_LeadingTrailingSpaces() {
        assertFalse("A name with leading or trailing spaces should be invalid", 
            InputValidation.isValidParticipantName(" John Doe "));
    }
}

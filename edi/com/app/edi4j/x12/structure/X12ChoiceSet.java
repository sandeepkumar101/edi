package com.app.edi4j.x12.structure;

import org.jdom.*;

/**
 * Class representing choices for an EDIElement or EDISubElement entry
 */
public class X12ChoiceSet {
    private String code;
    private String externalCodeSet;
    private String value;
    /**
     * sets the code portion of a choice set
     * @param code a string representing the code to be set
     */    
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * sets the value associated with a given code
     * @param value a string representing a value associated with a given code value
     */    
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Sets a reference to an external code set usually defined though an XML file
     * @param externalCodeSet Full path to an XML file containing code definitions
     */    
    public void setExternalCodeSet(String externalCodeSet) {
        this.externalCodeSet = externalCodeSet;
    }
    
    /**
     * returns the code for a given choice set
     * @return a code
     */    
    public String getCode() {
        return code;
    }
    
    /**
     * returns the value for a given code
     * @return a code value
     */    
    public String getValue() {
        return value;
    }
    
    /**
     * returns a full path to an XML file defining an external code set reference
     * @return a path to an XML file
     */    
    public String getExternalCodeSet() {
        return externalCodeSet;
    }
    
    /**
     * constructor for EDIChoiceSet object
     */    
    public X12ChoiceSet() {
    }
    
    public X12ChoiceSet(Element domElement) {
        setCode(domElement.getAttributeValue("code"));
        setValue(domElement.getAttributeValue("value"));
        setExternalCodeSet(domElement.getAttributeValue("code_source"));
    }
}

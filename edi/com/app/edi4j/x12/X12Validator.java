package com.app.edi4j.x12;

import com.app.edi4j.x12.structure.*;

public class X12Validator {
    public X12Validator() {
  }

  /**
   * validateElementRequirement
   */
  public String validateElementRequirementAndUsage(X12Element subjectX12Element) {
    if (subjectX12Element.getRequirement().compareTo("") == 0) {
      return "";
    }
    if (subjectX12Element.getUsage().compareTo("") == 0) {
      return "";
    }
    if (subjectX12Element.getRequirement().compareTo("REQUIRED") == 0) {
      return "X12Element " + subjectX12Element.getName() +
          " is a required element";
    }
    if (subjectX12Element.getUsage().compareTo("MANDATORY") == 0) {
      return "X12Element " + subjectX12Element.getName() +
          " is a manadatory element";
    }
    if (subjectX12Element.getRequirement().compareTo("NOT USED") == 0) {
      return "X12Element " + subjectX12Element.getName() +
          " is not used in this transaction";
    }
    return "";
  }

  /**
   * validateElementCharacterLength
   */
  public String validateElementCharacterLength(X12Element subjectX12Element,
                                               String value) {
    char[] characters = value.trim().toCharArray();
    if (subjectX12Element.getMin().compareTo("") == 0 | subjectX12Element.getMin().compareTo("*") == 0) {
      return "";
    }
    if (subjectX12Element.getMax().compareTo("") == 0 | subjectX12Element.getMax().compareTo("*") == 0) {
      return "";
    }

    Integer min = new Integer(subjectX12Element.getMin());
    Integer max = new Integer(subjectX12Element.getMax());
    if (characters.length > max.intValue()) {
      return "X12Element " + subjectX12Element.getName() +
          " has a maximum character value of " + subjectX12Element.getMax() +
          ", found " + characters.length + " characters in value";
    }
    if (characters.length < min.intValue()) {
      return "X12Element " + subjectX12Element.getName() +
          " has a minimum character value of " + subjectX12Element.getMin() +
          ", found " + characters.length + " characters in value";
    }
    return "";
  }


}

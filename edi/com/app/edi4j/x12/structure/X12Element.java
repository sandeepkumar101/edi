package com.app.edi4j.x12.structure;

import java.util.*;
import org.jdom.*;

public class X12Element {
  private String name = null;
  private String ref_des = null;
  private String usage = null;
  private String value = null;
  private String requirement = null;
  private String max = null;
  private String min = null;
  private String data_type = null;
  private String industryDescription = null;
  private String alias = null;
  private Vector choices = new Vector();
  private Vector subelements = new Vector();
  private String data_element = null;

  public void setName(String name) {
    this.name = name;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public void setRef_des(String ref_des) {
    this.ref_des = ref_des;
  }

  public void setData_element(String data_element) {
    this.data_element = data_element;
  }

  public void setRequirement(String requirement) {
    this.requirement = requirement;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public void setData_type(String data_type) {
    this.data_type = data_type;
  }

  public void setIndustryDescription(String industryDescription) {
    this.industryDescription = industryDescription;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setSubelements(Vector subelements) {
    this.subelements = subelements;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getUsage() {
    return usage;
  }

  public String getRef_des() {
    return ref_des;
  }

  public String getData_element() {
    return data_element;
  }

  public String getRequirement() {
    return requirement;
  }

  public String getMax() {
    return max;
  }

  public String getMin() {
    return min;
  }

  public String getData_type() {
    return data_type;
  }

  public String getIndustryDescription() {
    return industryDescription;
  }

  public String getAlias() {
    return alias;
  }

  public Vector getChoices() {
    return choices;
  }

  public Vector getSubelements() {
    return subelements;
  }

  public String getValue() {
    return value;
  }

  public void addChoice(X12ChoiceSet cs) { 
    choices.add(cs);
  }

  public void addSubElement(X12SubElement se) {
    subelements.add(se);
    sortSubElements();
  }

  /**
   * cloneElement
   *
   * @return X12Element
   */
  public X12Element cloneElement() {
    X12Element r = new X12Element();

    String aname = this.getName();
    r.setName(aname);
    String aref_des = this.getRef_des();
    r.setRef_des(aref_des);
    String ausage = this.getUsage();
    r.setUsage(ausage);
    String arequirement = this.getRequirement();
    r.setRequirement(arequirement);
    String amax = this.getMax();
    r.setMax(amax);
    String amin = this.getMin();
    r.setMin(amin);
    String adata_type = this.getData_type();
    r.setData_type(adata_type);
    String aindustryDescription = this.getIndustryDescription();
    r.setIndustryDescription(aindustryDescription);
    String aalias = this.getAlias();
    r.setAlias(aalias);
    String adata_element = this.getData_element();
    r.setData_element(adata_element);
    for(int i = 0; i < subelements.size(); i++){
      X12SubElement sub = (X12SubElement)subelements.get(i);
      r.addSubElement(sub.cloneSubElement());
    }
    for(int i = 0; i < choices.size(); i ++){
      X12ChoiceSet choice = (X12ChoiceSet)choices.get(i);
      r.addChoice(choice);
    }

    return r;
  }

  /**
   * isActive
   *
   * @return boolean
   */
  public boolean isActive() {
    if (this.value != null) {
      return true;
    }
    for (int i = 0; i < subelements.size(); i++) {
      X12SubElement sub = (X12SubElement) subelements.get(i);
      if (sub.isActive()) {
        return true;
      }
    }
    return false;
  }

  /**
   * getSubElementFromPosition
   *
   * @param elementPosition String
   */
  public X12SubElement getSubElementFromReference(String elementReference) {
    X12SubElement returnL = null;
    for (int i = 0; i < subelements.size(); i++) {
      X12SubElement thisElement = (X12SubElement) subelements.get(i);
      if (thisElement.getRef_des().compareTo(elementReference) == 0) {
        returnL = thisElement;
        return returnL;
      }
    }
    return returnL;

  }

  /**
   * sortSubElements
   */
  public void sortSubElements() {
    String[] elementReference = new String[subelements.size()];
    for (int i = 0; i < subelements.size(); i++) {
      X12SubElement thisElement = (X12SubElement) subelements.get(i);
      elementReference[i] = thisElement.getRef_des();
    }
    Arrays.sort(elementReference);
    Vector newElements = new Vector();
    for (int i = 0; i < subelements.size(); i++) {
      newElements.add(getSubElementFromReference(elementReference[i]));
    }
    setSubelements(newElements);
  }

  public X12Element() {
  }
  
  public X12Element(Element domElement){
        setRef_des(domElement.getAttributeValue("ref_des"));
        setData_type(domElement.getAttributeValue("data_type"));
        setData_element(domElement.getAttributeValue("data_element"));
        setMax(domElement.getAttributeValue("max"));
        setMin(domElement.getAttributeValue("min"));
        setName(domElement.getAttributeValue("name"));
        setRequirement(domElement.getAttributeValue("requirement"));
        setUsage(domElement.getAttributeValue("usage"));
        setValue(domElement.getText());
        
        Element industry = domElement.getChild("description");
        if(industry != null){
            setIndustryDescription(industry.getText());
        }
        
        Element alias = domElement.getChild("alias");
        if(alias != null){
            setAlias(alias.getText());
        }
  }
}

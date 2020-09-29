package com.app.edi4j.x12.structure;

import java.util.Vector;
import org.jdom.*;

public class X12SubElement extends X12Element {
    public X12SubElement() { 
  }
    
    public X12SubElement(Element domElement){
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

  /**
   * cloneSubElement
   *
   * @return X12SubElement
   */
  public X12SubElement cloneSubElement() {
    X12SubElement r = new X12SubElement();

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
    Vector cs = this.getChoices();
    for (int i = 0; i < cs.size(); i++) {
      X12ChoiceSet choice = (X12ChoiceSet) cs.get(i);
      r.addChoice(choice);
    }

    return r;
  }

}

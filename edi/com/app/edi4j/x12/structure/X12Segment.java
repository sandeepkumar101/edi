package com.app.edi4j.x12.structure;

import java.util.*;
import org.jdom.*;

public class X12Segment {
    private String position = null;
    private String level = null;
    private String loopID = null;
    private String loopRepeat = null;
    private String repeat = null;
    private String loopName = null;
    private String segmentID = null;
    private Vector elements = new Vector();
    private String usage = null;
    private String igRef = null;
    private String description = null;
    private String requirement = null;
    private String maxUse = null;
    private String name = null;
    
    /**
     * getElementFromPosition
     *
     * @param elementPosition String
     */
    public X12Element getElementFromReference(String elementReference) {
        X12Element returnL = null;
        for (int i = 0; i < elements.size(); i++) {
            X12Element thisElement = (X12Element) elements.get(i);
            if (thisElement.getRef_des().compareTo(elementReference) == 0) {
                returnL = thisElement;
                return returnL;
            }
        }
        return returnL;
        
    }
    
    /**
     * cloneSegment
     *
     * @return X12Segment
     */
    public X12Segment cloneSegment() {
        X12Segment r = new X12Segment();
        String aposition = getPosition();
        r.setPosition(aposition);
        String alevel = getLevel();
        r.setLevel(alevel);
        String arepeat = getRepeat();
        r.setRepeat(arepeat);
        String asegmentID = getId();
        r.setId(asegmentID);
        String ausage = getUsage();
        r.setUsage(ausage);
        String adescription = getDescription();
        r.setDescription(adescription);
        String arequirement = getRequirement();
        r.setRequirement(arequirement);
        String amaxUse = getMaxUse();
        r.setMaxUse(amaxUse);
        String aname = getName();
        r.setName(aname);
        
        for (int i = 0; i < elements.size(); i++) {
            X12Element element = (X12Element) elements.get(i);
            r.addX12Element(element.cloneElement());
        }
        
        return r;
    }
    
    /**
     * areElementsSet
     *
     * @return boolean
     */
    public boolean areElementsSet() {
        boolean returnB = false;
        for (int i = 0; i < elements.size(); i++) {
            X12Element thisElement = (X12Element) elements.get(i);
            if (thisElement.isActive()) {
                return true;
            }
            Vector theseSubs = thisElement.getSubelements();
            for (int j = 0; j < theseSubs.size(); j++) {
                X12SubElement thisSub = (X12SubElement) theseSubs.get(j);
                if (thisSub.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * sortElements
     */
    public void sortElements() {
        String[] elementReference = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            X12Element thisElement = (X12Element) elements.get(i);
            elementReference[i] = thisElement.getRef_des();
        }
        Arrays.sort(elementReference);
        Vector newElements = new Vector();
        for (int i = 0; i < elements.size(); i++) {
            newElements.add(getElementFromReference(elementReference[i]));
        }
        setX12Elements(newElements);
    }
    
    public void addX12Element(X12Element e) {
        elements.add(e);
        sortElements();
    }
    
    public void setX12Elements(Vector e) {
        this.elements = e;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setId(String segmentID) {
        this.segmentID = segmentID;
    }
    
    public void setLoopID(String loopID) {
        this.loopID = loopID;
    }
    
    public void setLoopName(String loopName) {
        this.loopName = loopName;
    }
    
    public void setLoopRepeat(String loopRepeat) {
        this.loopRepeat = loopRepeat;
    }
    
    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
    
    public void setMaxUse(String maxUse) {
        this.maxUse = maxUse;
    }
    
    public void setUsage(String usage) {
        this.usage = usage;
    }
    
    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
    
    public void setIgRef(String igRef) {
        this.igRef = igRef;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Vector getX12Elements() {
        return elements;
    }
    
    public String getLevel() {
        return level;
    }
    
    public String getPosition() {
        return position;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return segmentID;
    }
    
    public String getLoopID() {
        return loopID;
    }
    
    public String getLoopName() {
        return loopName;
    }
    
    public String getLoopRepeat() {
        return loopRepeat;
    }
    
    public String getRepeat() {
        return repeat;
    }
    
    public String getMaxUse() {
        return maxUse;
    }
    
    public String getUsage() {
        return usage;
    }
    
    public String getRequirement() {
        return requirement;
    }
    
    public String getIgRef() {
        return igRef;
    }
    
    public String getDescription() {
        return description;
    }
    
    public X12Segment() {
    }
    
    public X12Segment(Element domElement) {
        setDescription(domElement.getAttributeValue("description"));
        setId(domElement.getAttributeValue("id"));
        setLevel(domElement.getAttributeValue("level"));
        setMaxUse(domElement.getAttributeValue("maxuse"));
        setName(domElement.getAttributeValue("name"));
        setPosition(domElement.getAttributeValue("position"));
        setRepeat(domElement.getAttributeValue("repeat"));
        setRequirement(domElement.getAttributeValue("requirement"));
        setUsage(domElement.getAttributeValue("usage"));
    }
}

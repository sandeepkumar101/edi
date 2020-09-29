package com.app.edi4j.x12.structure;

import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import java.io.*;
import com.app.edi4j.x12.X12File;
/**
 *
 * @author  kurt
 */
public class X12Interchange {
    private Vector groups = new Vector();
    private Vector segments = new Vector();
    private Vector footerSegments = new Vector();
    private Vector headerSegments = new Vector();
    /** Creates a new instance of X12Interchange */
    public X12Interchange() throws JDOMException, IOException{
        initialize();
    }
    
    public X12Interchange(X12File file) throws JDOMException, IOException{
        initialize(file);
    }
    
    public void setGroups(Vector groups) {
        this.groups = groups;
    }
    
    public void setSegments(Vector segments) {
        this.segments = segments;
    }
    
    public void setHeaderSegments(Vector headerSegments) {
        this.headerSegments = headerSegments;
    }
    
    public void setFooterSegment(Vector footerSegments) {
        this.footerSegments = footerSegments;
    }
    
    public Vector getGroups() {
        return groups;
    }
    
    public Vector getSegments() {
        return segments;
    }
    
    public Vector getHeaderSegments() {
        return headerSegments;
    }
    
    public Vector getFooterSegment() {
        return footerSegments;
    }
    
    public void addGroup(X12Group group){
        groups.add(group);
    }
    
    /**
     * sortSegments
     */
    public void sortSegments() {
        String[] segmentPosition = new String[segments.size()];
        for (int i = 0; i < segments.size(); i++) {
            X12Segment thisSegment = (X12Segment) segments.get(i);
            segmentPosition[i] = thisSegment.getPosition();
        }
        Arrays.sort(segmentPosition);
        Vector newSegments = new Vector();
        for (int i = 0; i < segments.size(); i++) {
            newSegments.add(getSegmentFromPosition(segmentPosition[i]));
        }
        setSegments(newSegments);
    }
    
    /**
     * addSegment
     *
     * @return Segment
     */
    public void addSegment(X12Segment aSegment) {
        segments.add(aSegment);
        sortSegments();
        Integer pos = new Integer(aSegment.getPosition());
        if(pos.intValue() > 100){
            footerSegments.add(aSegment);
        }else{
            headerSegments.add(aSegment);
        }
    }
    
    /**
     * getSegmentFromID
     *
     * @param segmentID String
     */
    public X12Segment getSegmentFromPosition(String segmentPosition) {
        X12Segment returnL = null;
        for (int i = 0; i < segments.size(); i++) {
            X12Segment thisSegment = (X12Segment) segments.get(i);
            if (thisSegment.getPosition().compareTo(segmentPosition) == 0) {
                returnL = thisSegment;
                return returnL;
            }
        }
        return returnL;
        
    }
    
    /**
     * getSegmentFromID
     *
     * @param segmentID String
     */
    public X12Segment getSegmentFromID(String segmentID) {
        X12Segment returnL = null;
        for (int i = 0; i < segments.size(); i++) {
            X12Segment thisSegment = (X12Segment) segments.get(i);
            if (thisSegment.getId().compareTo(segmentID) == 0) {
                returnL = thisSegment;
                return returnL;
            }
        }
        return returnL;
        
    }
    
    void initialize(File interchangeFile) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        Document interchangeDoc = saxBuilder.build(interchangeFile);
        Element root = interchangeDoc.getRootElement();
        List l = root.getChildren("segment");
        Iterator it = l.iterator();
        while(it.hasNext()){
            Element segmentElement = (Element)it.next();
            X12Segment segment = new X12Segment(segmentElement);
            List elementList = segmentElement.getChildren("element");
            Iterator it1 = elementList.iterator();
            while(it1.hasNext()){
                Element elementElement = (Element)it1.next();
                X12Element element = new X12Element(elementElement);
                List choiceList = elementElement.getChildren("code_definition");
                Iterator it2 = choiceList.iterator();
                while(it2.hasNext()){
                    Element choiceElement = (Element)it2.next();
                    X12ChoiceSet choice = new X12ChoiceSet(choiceElement);
                    element.addChoice(choice);
                }
                segment.addX12Element(element);
            }
            addSegment(segment);
        }
    }
    
    void initialize()throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        Document interchangeDoc = saxBuilder.build(System.getProperty("application.templates") + System.getProperty("file.separator") + "x12" + System.getProperty("file.separator") + "interchange.xml");
        Element root = interchangeDoc.getRootElement();
        List l = root.getChildren("segment");
        Iterator it = l.iterator();
        while(it.hasNext()){
            Element segmentElement = (Element)it.next();
            X12Segment segment = new X12Segment(segmentElement);
            List elementList = segmentElement.getChildren("element");
            Iterator it1 = elementList.iterator();
            while(it1.hasNext()){
                Element elementElement = (Element)it1.next();
                X12Element element = new X12Element(elementElement);
                List choiceList = elementElement.getChildren("code_definition");
                Iterator it2 = choiceList.iterator();
                while(it2.hasNext()){
                    Element choiceElement = (Element)it2.next();
                    X12ChoiceSet choice = new X12ChoiceSet(choiceElement);
                    element.addChoice(choice);
                }
                segment.addX12Element(element);
            }
            addSegment(segment);
        }
    }
    
}

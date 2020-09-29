package com.app.edi4j.x12;

import java.io.*;
import java.util.*;

import org.jdom.*;
import com.app.edi4j.x12.structure.*;
import com.app.edi4j.template.*;
import com.app.edi4j.x12.X12ParserFile;
import com.app.edi4j.template.Template;

public class X12Document {
    private Document xmlDocument;
    private Document xmlTransaction;
    private X12File ediFile;
    private X12Transaction transaction;
    public X12Interchange interchange;
    String templateFileName = null;
    
    /**
     * read
     */
    private void read() {
        
    }
    
    public X12Interchange getInterchange(){
        return interchange;
    }
    
    /**
     * exportX12File
     *
     * @return X12File
     */
    public void exportX12File(String exportPath, String exportName) throws
    IOException {
        File f = new File(exportPath);
        File ff = new File(f, exportName);
        FileOutputStream out = new FileOutputStream(ff.getAbsolutePath());
        PrintStream p;
        p = new PrintStream(out);
        
        Document xml = exportXMLTransaction();
        
        Element transaction = xml.getRootElement();
        List children1 = transaction.getChildren();
        Iterator children1It = children1.iterator();
        while (children1It.hasNext()) {
            Element child1 = (Element) children1It.next();
            if (child1.getName().compareTo("segment") == 0) {
                String values = getSegmentString(child1);
                p.println(values);
                System.out.println(values);
            }
            else {
                List children2 = child1.getChildren();
                Iterator children2It = children2.iterator();
                while (children2It.hasNext()) {
                    Element child2 = (Element) children2It.next();
                    if (child2.getName().compareTo("segment") == 0) {
                        String values = getSegmentString(child2);
                        p.println(values);
                        System.out.println(values);
                    }
                    else {
                        List children3 = child2.getChildren();
                        Iterator children3It = children3.iterator();
                        while (children3It.hasNext()) {
                            Element child3 = (Element) children3It.next();
                            if (child3.getName().compareTo("segment") == 0) {
                                String values = getSegmentString(child3);
                                p.println(values);
                                System.out.println(values);
                            }
                            else {
                                List children4 = child3.getChildren();
                                Iterator children4It = children4.iterator();
                                while (children4It.hasNext()) {
                                    Element child4 = (Element) children4It.next();
                                    if (child4.getName().compareTo("segment") == 0) {
                                        String values = getSegmentString(child4);
                                        p.println(values);
                                        System.out.println(values);
                                    }
                                    else {
                                        List children5 = child4.getChildren();
                                        Iterator children5It = children5.iterator();
                                        while (children5It.hasNext()) {
                                            Element child5 = (Element) children5It.next();
                                            if (child5.getName().compareTo("segment") == 0) {
                                                String values = getSegmentString(child5);
                                                p.println(values);
                                                System.out.println(values);
                                            }
                                            else {
                                                List children6 = child5.getChildren();
                                                Iterator children6It = children6.iterator();
                                                while (children6It.hasNext()) {
                                                    Element child6 = (Element) children5It.next();
                                                    if (child6.getName().compareTo("segment") == 0) {
                                                        String values = getSegmentString(child6);
                                                        p.println(values);
                                                        System.out.println(values);
                                                    }
                                                    else {
                                                        List children7 = child6.getChildren();
                                                        Iterator children7It = children7.iterator();
                                                        while (children7It.hasNext()) {
                                                            Element child7 = (Element) children7It.next();
                                                        }
                                                    }
                                                    
                                                }
                                            }
                                            
                                        }
                                    }
                                    
                                }
                            }
                            
                        }
                    }
                    
                }
            }
        }
        p.close();
    }
    
    /**
     * getSegmentString
     *
     * @param aSegmentElement Element
     * @return String
     */
    public String getSegmentString(Element aSegmentElement) {
        String segmentID = aSegmentElement.getAttributeValue("id");
        String valueString = segmentID;
        List elements = aSegmentElement.getChildren("element");
        Iterator elementIt = elements.iterator();
        while (elementIt.hasNext()) {
            Element element = (Element) elementIt.next();
            if (ediElementDOMHasSubelements(element)) {
                valueString = valueString + regexSafe(ediFile.elementSeparator) + getCompositeElementString(element);
            }
            else {
                valueString = valueString + regexSafe(ediFile.elementSeparator) + element.getText();
            }
        }
        valueString = trimAndTerminateSegment(valueString);
        return valueString;
    }
    
    /**
     * getCompositeElementString
     *
     * @param ediElementDOM Element
     * @return String
     */
    public String getCompositeElementString(Element ediElementDOM) {
        String returnS = "";
        List subs = ediElementDOM.getChildren("subelement");
        int counter = 0;
        Iterator subIt = subs.iterator();
        while (subIt.hasNext()) {
            Element sub = (Element) subIt.next();
            returnS = returnS + sub.getText() + regexSafe(ediFile.subelementSeparartor);
            counter++;
        }
        return trimCompositeElement(returnS);
    }
    
    /**
     * trimCompositeElement
     *
     * @param compositeElementString String
     * @return String
     */
    public String trimCompositeElement(String compositeElementString) {
        String[] parts = compositeElementString.split(regexSafe(ediFile.subelementSeparartor));
        Vector vParts = new Vector();
        String returnS = "";
        for (int i = 0; i < parts.length; i++) {
            vParts.add(parts[i]);
        }
        int last = parts.length - 1;
        String test = parts[last];
        while (emptyString(test) && last > 0) {
            vParts.remove(last);
            last--;
            test = parts[last];
        }
        String first = parts[0];
        returnS = first;
        for (int i = 1; i < vParts.size(); i++) {
            returnS = returnS + regexSafe(ediFile.subelementSeparartor) + vParts.get(i).toString();
        }
        
        return returnS;
    }
    
    /**
     * trimAndTerminateSegment
     *
     * @param aSegmentString String
     * @return String
     */
    public String trimAndTerminateSegment(String aSegmentString) {
        String[] parts = aSegmentString.split(regexSafe(ediFile.elementSeparator));
        Vector vParts = new Vector();
        String returnS = "";
        for (int i = 0; i < parts.length; i++) {
            vParts.add(parts[i]);
        }
        int last = parts.length - 1;
        String test = parts[last];
        while (emptyString(test) && last > 0) {
            vParts.remove(last);
            last--;
            test = parts[last];
        }
        returnS = returnS + parts[0];
        for (int i = 1; i < vParts.size(); i++) {
            returnS = returnS + ediFile.elementSeparator + vParts.get(i).toString();
        }
        returnS = returnS + ediFile.lineTerminator;
        
        return returnS;
    }
    
    /**
     * emptyString
     *
     * @param aString String
     * @return boolean
     */
    public boolean emptyString(String aString) {
        if (aString.trim().compareTo("") == 0) {
            return true;
        }
        return false;
    }
    
    /**
     * ediElementDOMHasSubelements
     *
     * @param ediElementDOM Element
     * @return boolean
     */
    public boolean ediElementDOMHasSubelements(Element ediElementDOM) {
        boolean has = false;
        List pSubs = ediElementDOM.getChildren("subelement");
        Iterator subIt = pSubs.iterator();
        while (subIt.hasNext()) {
            Element sub = (Element) subIt.next();
            if (sub.getText().trim().compareTo("") != 0) {
                if (sub.getText() != null) {
                    has = true;
                }
            }
        }
        return has;
    }
    
    /**
     * exportXMLTransaction
     */
    public void exportXMLTransaction(String exportFilePath) {
        org.jdom.Element root = new org.jdom.Element("interchange");
        Vector rootSegments = interchange.getSegments();
        for (int i = 0; i < rootSegments.size(); i++) {
            X12Segment segment = (X12Segment) rootSegments.get(i);
            Integer pos = new Integer(segment.getPosition());
            if (pos.intValue() < 100) {
                Element eSeg = new Element("segment");
                eSeg.setAttribute("id", segment.getId());
                eSeg.setAttribute("name", segment.getName());
                eSeg.setAttribute("position", segment.getPosition());
                eSeg = writeSegment(eSeg, segment);
                root.addContent(eSeg);
            }
        }
        
        Vector groups = interchange.getGroups();
        for(int a = 0; a < groups.size(); a++){
            X12Group group = (X12Group)groups.get(a);
            org.jdom.Element groupElement = new org.jdom.Element("group");
            for (int i = 0; i < group.getSegments().size(); i++) {
                X12Segment segment = (X12Segment) group.getSegments().get(i);
                Integer pos = new Integer(segment.getPosition());
                if (pos.intValue() < 100) {
                    Element eSeg = new Element("segment");
                    eSeg.setAttribute("id", segment.getId());
                    eSeg.setAttribute("name", segment.getName());
                    eSeg.setAttribute("position", segment.getPosition());
                    eSeg = writeSegment(eSeg, segment);
                    groupElement.addContent(eSeg);
                }
            }
            Vector transactions = group.getTransactions();
            for(int c = 0; c < transactions.size(); c++){
                X12Transaction transaction = (X12Transaction)transactions.get(c);
                org.jdom.Element transactionElement = new org.jdom.Element("transaction");
                for (int i = 0; i < transaction.getSegments().size(); i++) {
                    X12Segment segment = (X12Segment) transaction.getSegments().get(i);
                    Integer pos = new Integer(segment.getPosition());
                    if (pos.intValue() < 100) {
                        Element eSeg = new Element("segment");
                        eSeg.setAttribute("id", segment.getId());
                        eSeg.setAttribute("name", segment.getName());
                        eSeg.setAttribute("position", segment.getPosition());
                        eSeg = writeSegment(eSeg, segment);
                        transactionElement.addContent(eSeg);
                    }
                }
                Vector level1 = transaction.getLoops();
                for (int i = 0; i < level1.size(); i++) {
                    X12Loop level1Loop = (X12Loop) level1.get(i);
                    if (level1Loop.isActive()) {
                        Element loop1 = new Element("loop");
                        loop1 = writeLoop(loop1, level1Loop);
                        Vector level2 = level1Loop.getX12Loops();
                        for (int j = 0; j < level2.size(); j++) {
                            X12Loop level2Loop = (X12Loop) level2.get(j);
                            if (level2Loop.isActive()) {
                                Element loop2 = new Element("loop");
                                loop2 = writeLoop(loop2, level2Loop);
                                Vector level3 = level2Loop.getX12Loops();
                                for (int k = 0; k < level3.size(); k++) {
                                    X12Loop level3Loop = (X12Loop) level3.get(k);
                                    if (level3Loop.isActive()) {
                                        Element loop3 = new Element("loop");
                                        loop3 = writeLoop(loop3, level3Loop);
                                        Vector level4 = level3Loop.getX12Loops();
                                        for (int l = 0; l < level4.size(); l++) {
                                            X12Loop level4Loop = (X12Loop) level4.get(l);
                                            if (level4Loop.isActive()) {
                                                Element loop4 = new Element("loop");
                                                loop4 = writeLoop(loop4, level4Loop);
                                                Vector level5 = level4Loop.getX12Loops();
                                                for (int m = 0; m < level5.size(); m++) {
                                                    X12Loop level5Loop = (X12Loop) level5.get(m);
                                                    if (level5Loop.isActive()) {
                                                        Element loop5 = new Element("loop");
                                                        loop5 = writeLoop(loop5, level5Loop);
                                                        loop4.addContent(loop5);
                                                    }
                                                }
                                                loop3.addContent(loop4);
                                            }
                                        }
                                        loop2.addContent(loop3);
                                    }
                                }
                                loop1.addContent(loop2);
                            }
                        }
                        transactionElement.addContent(loop1);
                    }
                }
                for (int i = 0; i < transaction.getSegments().size(); i++) {
                    X12Segment segment = (X12Segment) transaction.getSegments().get(i);
                    Integer pos = new Integer(segment.getPosition());
                    if (pos.intValue() > 100) {
                        Element eSeg = new Element("segment");
                        eSeg.setAttribute("id", segment.getId());
                        eSeg.setAttribute("name", segment.getName());
                        eSeg.setAttribute("position", segment.getPosition());
                        eSeg = writeSegment(eSeg, segment);
                        transactionElement.addContent(eSeg);
                    }
                }
                groupElement.addContent(transactionElement);
            }
            for (int i = 0; i < group.getSegments().size(); i++) {
                X12Segment segment = (X12Segment) group.getSegments().get(i);
                Integer pos = new Integer(segment.getPosition());
                if (pos.intValue() > 100) {
                    Element eSeg = new Element("segment");
                    eSeg.setAttribute("id", segment.getId());
                    eSeg.setAttribute("name", segment.getName());
                    eSeg.setAttribute("position", segment.getPosition());
                    eSeg = writeSegment(eSeg, segment);
                    groupElement.addContent(eSeg);
                }
            }
            root.addContent(groupElement);
        }
        
        for (int i = 0; i < rootSegments.size(); i++) {
            X12Segment segment = (X12Segment) rootSegments.get(i);
            Integer pos = new Integer(segment.getPosition());
            if (pos.intValue() > 100) {
                Element eSeg = new Element("segment");
                eSeg.setAttribute("id", segment.getId());
                eSeg.setAttribute("name", segment.getName());
                eSeg.setAttribute("position", segment.getPosition());
                eSeg = writeSegment(eSeg, segment);
                root.addContent(eSeg);
            }
        }
        
        Document doc = new Document(root);
        
        try {
            org.jdom.output.XMLOutputter serializer = new org.jdom.output.
            XMLOutputter();
            serializer.setIndent("  ");
            serializer.setNewlines(true);
            FileWriter fileWriter = new FileWriter(exportFilePath);
            serializer.output(doc, fileWriter);
        }
        catch (IOException e) {
            System.err.println(e);
        }
        
    }
    
    /**
     * exportXMLTransaction
     */
    public Document exportXMLTransaction() {
        org.jdom.Element root = new org.jdom.Element("interchange");
        Vector rootSegments = interchange.getSegments();
        for (int i = 0; i < rootSegments.size(); i++) {
            X12Segment segment = (X12Segment) rootSegments.get(i);
            Integer pos = new Integer(segment.getPosition());
            if (pos.intValue() < 100) {
                Element eSeg = new Element("segment");
                eSeg.setAttribute("id", segment.getId());
                eSeg.setAttribute("name", segment.getName());
                eSeg.setAttribute("position", segment.getPosition());
                eSeg = writeSegment(eSeg, segment);
                root.addContent(eSeg);
            }
        }
        
        Vector groups = interchange.getGroups();
        for(int a = 0; a < groups.size(); a++){
            X12Group group = (X12Group)groups.get(a);
            org.jdom.Element groupElement = new org.jdom.Element("group");
            for (int i = 0; i < group.getSegments().size(); i++) {
                X12Segment segment = (X12Segment) group.getSegments().get(i);
                Integer pos = new Integer(segment.getPosition());
                if (pos.intValue() < 100) {
                    Element eSeg = new Element("segment");
                    eSeg.setAttribute("id", segment.getId());
                    eSeg.setAttribute("name", segment.getName());
                    eSeg.setAttribute("position", segment.getPosition());
                    eSeg = writeSegment(eSeg, segment);
                    groupElement.addContent(eSeg);
                }
            }
            Vector transactions = group.getTransactions();
            for(int c = 0; c < transactions.size(); c++){
                X12Transaction transaction = (X12Transaction)transactions.get(c);
                org.jdom.Element transactionElement = new org.jdom.Element("transaction");
                for (int i = 0; i < transaction.getSegments().size(); i++) {
                    X12Segment segment = (X12Segment) transaction.getSegments().get(i);
                    Integer pos = new Integer(segment.getPosition());
                    if (pos.intValue() < 100) {
                        Element eSeg = new Element("segment");
                        eSeg.setAttribute("id", segment.getId());
                        eSeg.setAttribute("name", segment.getName());
                        eSeg.setAttribute("position", segment.getPosition());
                        eSeg = writeSegment(eSeg, segment);
                        transactionElement.addContent(eSeg);
                    }
                }
                Vector level1 = transaction.getLoops();
                for (int i = 0; i < level1.size(); i++) {
                    X12Loop level1Loop = (X12Loop) level1.get(i);
                    if (level1Loop.isActive()) {
                        Element loop1 = new Element("loop");
                        loop1 = writeLoop(loop1, level1Loop);
                        Vector level2 = level1Loop.getX12Loops();
                        for (int j = 0; j < level2.size(); j++) {
                            X12Loop level2Loop = (X12Loop) level2.get(j);
                            if (level2Loop.isActive()) {
                                Element loop2 = new Element("loop");
                                loop2 = writeLoop(loop2, level2Loop);
                                Vector level3 = level2Loop.getX12Loops();
                                for (int k = 0; k < level3.size(); k++) {
                                    X12Loop level3Loop = (X12Loop) level3.get(k);
                                    if (level3Loop.isActive()) {
                                        Element loop3 = new Element("loop");
                                        loop3 = writeLoop(loop3, level3Loop);
                                        Vector level4 = level3Loop.getX12Loops();
                                        for (int l = 0; l < level4.size(); l++) {
                                            X12Loop level4Loop = (X12Loop) level4.get(l);
                                            if (level4Loop.isActive()) {
                                                Element loop4 = new Element("loop");
                                                loop4 = writeLoop(loop4, level4Loop);
                                                Vector level5 = level4Loop.getX12Loops();
                                                for (int m = 0; m < level5.size(); m++) {
                                                    X12Loop level5Loop = (X12Loop) level5.get(m);
                                                    if (level5Loop.isActive()) {
                                                        Element loop5 = new Element("loop");
                                                        loop5 = writeLoop(loop5, level5Loop);
                                                        loop4.addContent(loop5);
                                                    }
                                                }
                                                loop3.addContent(loop4);
                                            }
                                        }
                                        loop2.addContent(loop3);
                                    }
                                }
                                loop1.addContent(loop2);
                            }
                        }
                        transactionElement.addContent(loop1);
                    }
                }
                for (int i = 0; i < transaction.getSegments().size(); i++) {
                    X12Segment segment = (X12Segment) transaction.getSegments().get(i);
                    Integer pos = new Integer(segment.getPosition());
                    if (pos.intValue() > 100) {
                        Element eSeg = new Element("segment");
                        eSeg.setAttribute("id", segment.getId());
                        eSeg.setAttribute("name", segment.getName());
                        eSeg.setAttribute("position", segment.getPosition());
                        eSeg = writeSegment(eSeg, segment);
                        transactionElement.addContent(eSeg);
                    }
                }
                groupElement.addContent(transactionElement);
            }
            for (int i = 0; i < group.getSegments().size(); i++) {
                X12Segment segment = (X12Segment) group.getSegments().get(i);
                Integer pos = new Integer(segment.getPosition());
                if (pos.intValue() > 100) {
                    Element eSeg = new Element("segment");
                    eSeg.setAttribute("id", segment.getId());
                    eSeg.setAttribute("name", segment.getName());
                    eSeg.setAttribute("position", segment.getPosition());
                    eSeg = writeSegment(eSeg, segment);
                    groupElement.addContent(eSeg);
                }
            }
            root.addContent(groupElement);
        }
        
        for (int i = 0; i < rootSegments.size(); i++) {
            X12Segment segment = (X12Segment) rootSegments.get(i);
            Integer pos = new Integer(segment.getPosition());
            if (pos.intValue() > 100) {
                Element eSeg = new Element("segment");
                eSeg.setAttribute("id", segment.getId());
                eSeg.setAttribute("name", segment.getName());
                eSeg.setAttribute("position", segment.getPosition());
                eSeg = writeSegment(eSeg, segment);
                root.addContent(eSeg);
            }
        }
        
        Document doc = new Document(root);
        return doc;
        
    }
    
    /**
     * exportXMLTransaction
     */
    
    
    /**
     * writeLoop
     */
    public Element writeLoop(Element loopsElement, X12Loop loopsObject) {
        loopsElement.setAttribute("id", loopsObject.getId());
        loopsElement.setAttribute("name", loopsObject.getName());
        Vector segments = loopsObject.getSegments();
        for (int j = 0; j < segments.size(); j++) {
            X12Segment seg = (X12Segment) segments.get(j);
            if (seg.areElementsSet()) {
                Element eSeg = new Element("segment");
                eSeg.setAttribute("id", seg.getId());
                eSeg.setAttribute("name", seg.getName());
                eSeg.setAttribute("position", seg.getPosition());
                eSeg = writeSegment(eSeg, seg);
                loopsElement.addContent(eSeg);
            }
        }
        return loopsElement;
    }
    
    /**
     * writeSegment
     */
    public Element writeSegment(Element segment, X12Segment writeSegment) {
        Vector ediElements = writeSegment.getX12Elements();
        for (int i = 0; i < ediElements.size(); i++) {
            X12Element thisElement = (X12Element) ediElements.get(i);
            Element writeElement = new Element("element");
            if (thisElement.getSubelements().size() > 0 && thisElement.isActive()) {
                if (thisElement.getAlias() != null) {
                    writeElement.setAttribute("alias", thisElement.getAlias());
                }
                if (thisElement.getIndustryDescription() != null) {
                    writeElement.setAttribute("industry",
                    thisElement.getIndustryDescription());
                }
                writeElement.setAttribute("name", thisElement.getName());
                writeElement.setAttribute("reference", thisElement.getRef_des());
                writeElement.setText(thisElement.getValue());
                
                Vector subs = thisElement.getSubelements();
                for (int j = 0; j < subs.size(); j++) {
                    X12SubElement sub = (X12SubElement) subs.get(j);
                    Element writeSub = new Element("subelement");
                    if (sub.getAlias() != null) {
                        writeSub.setAttribute("alias", sub.getAlias());
                    }
                    if (sub.getIndustryDescription() != null) {
                        writeSub.setAttribute("industry", sub.getIndustryDescription());
                    }
                    writeSub.setAttribute("name", sub.getName());
                    writeSub.setAttribute("reference", sub.getRef_des());
                    writeSub.setText(sub.getValue());
                    writeElement.addContent(writeSub);
                }
                segment.addContent(writeElement);
            }
            else {
                if (thisElement.getAlias() != null) {
                    writeElement.setAttribute("alias", thisElement.getAlias());
                }
                if (thisElement.getIndustryDescription() != null) {
                    writeElement.setAttribute("industry",
                    thisElement.getIndustryDescription());
                }
                if (thisElement.getName() != null) {
                    writeElement.setAttribute("name", thisElement.getName());
                }
                writeElement.setAttribute("reference", thisElement.getRef_des());
                writeElement.setText(thisElement.getValue());
                segment.addContent(writeElement);
            }
            
        }
        return segment;
    }
    
    /**
     * normalizeName
     *
     * @param name String
     * @return String
     */
    public String normalizeName(String name) {
        name = name.replaceAll("'", " ");
        name = name.replaceAll("/", "-");
        String[] parts1 = name.split(" ");
        String[] newParts1 = new String[parts1.length];
        for (int i = 0; i < parts1.length; i++) {
            char[] chars = parts1[i].trim().toCharArray();
            String[] newPieces = new String[chars.length];
            newPieces[0] = String.valueOf(chars[0]).toUpperCase();
            for (int j = 1; j < chars.length; j++) {
                String piece = String.valueOf(chars[j]);
                newPieces[j] = piece.toLowerCase();
            }
            String formatted = "";
            for (int j = 0; j < newPieces.length; j++) {
                formatted = formatted + newPieces[j];
            }
            newParts1[i] = formatted;
        }
        String returnS = "";
        for (int i = 0; i < newParts1.length; i++) {
            returnS = returnS + newParts1[i];
        }
        
        return returnS;
    }
    
    /**
     * writeElement
     */
    public void writeElement() {
    }
    
    public void setX12File(X12File ediFile) {
        this.ediFile = ediFile;
    }
    
    public void setXmlDocument(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }
    
    public void setXmlTransaction(Document xmlTransaction) {
        this.xmlTransaction = xmlTransaction;
    }
    
    public void setTransaction(X12Transaction transaction) {
        this.transaction = transaction;
    }
    
    public X12File getX12File() {
        return ediFile;
    }
    
    public Document getXmlDocument() {
        return xmlDocument;
    }
    
    public Document getXmlTransaction() {
        return xmlTransaction;
    }
    
    public X12Transaction getTransaction() {
        return transaction;
    }
    
    public X12Document() throws JDOMException, java.io.IOException, Exception {
        
    }
    
    public X12Document(X12File file) throws JDOMException, java.io.IOException, Exception {
        
        ediFile = file;
        readInterchangeInfo(file);
        buildGroups(breakGroupsIntoParserFiles(file));
        
    }
    
    public X12Document(X12File file, String templateFileName) throws JDOMException, java.io.IOException, Exception {
        this.templateFileName = templateFileName;
        ediFile = file;
        readInterchangeInfo(file);
        buildGroups(breakGroupsIntoParserFiles(file));
        
        
    }
    
    void buildGroups(Vector pfs) throws JDOMException, java.io.IOException, Exception{
        for(int i = 0; i < pfs.size(); i++){
            GregorianCalendar gc = new GregorianCalendar();
            X12ParserFile pf = (X12ParserFile)pfs.get(i);
            long pos = pf.getFilePointer();
            X12Group group = new X12Group();
            String gs = pf.readLine();
            Template template;
            String[] parts = gs.split(regexSafe(pf.elementSeparator));
            X12Segment gsSegment = group.getSegmentFromID("GS");
            gsSegment.getElementFromReference("GS01").setValue(parts[1]);
            gsSegment.getElementFromReference("GS02").setValue(parts[2]);
            gsSegment.getElementFromReference("GS03").setValue(parts[3]);
            gsSegment.getElementFromReference("GS04").setValue(parts[4]);
            gsSegment.getElementFromReference("GS05").setValue(parts[5]);
            gsSegment.getElementFromReference("GS06").setValue(parts[6]);
            gsSegment.getElementFromReference("GS07").setValue(parts[7]);
            gsSegment.getElementFromReference("GS08").setValue(clearLineTerm(parts[8]));
            
            X12Segment geSegment = group.getSegmentFromID("GE");
            String ge;
            while ( (ge = pf.readLine()) != null) {
                if(ge.startsWith("GE")){
                    String[] geParts = ge.split(regexSafe(pf.elementSeparator));
                    geSegment.getElementFromReference("GE01").setValue(geParts[1]);
                    geSegment.getElementFromReference("GE02").setValue(clearLineTerm(geParts[2]));
                }
            }
            
            String functionCode = gsSegment.getElementFromReference("GS01").getValue();
            String versionCode = gsSegment.getElementFromReference("GS08").getValue();
            if(templateFileName == null){
                template = new Template(new File(System.getProperty("application.templates") + "x12" + System.getProperty("file.separator") + versionCode + "_" + functionCode + ".xml"));
            }
            else{
                template = new Template(new File(System.getProperty("application.templates") + "x12" + System.getProperty("file.separator") + templateFileName));
            }
            
            File outfile = new File(System.getProperty("application.temp") + gc.getTimeInMillis());
            PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile)));
            
            pf.seek(pos);
            while ( (ge = pf.readLine()) != null) {
                if(!ge.startsWith("GE") | !ge.startsWith("GS")){
                    out.println(ge);
                }
            }
            out.close();
            
            X12ParserFile parseFile = new X12ParserFile(outfile);
            Vector tpfs  = breakTransactionsIntoParserFiles(parseFile);
            for(int b = 0; b < tpfs.size(); b++){
                X12ParserFile pf1 = (X12ParserFile)tpfs.get(b);
                X12Parser p = new X12Parser(template, new X12ParserFile(pf1));
                X12Transaction trans = p.getTransaction();
                group.addTransaction(trans);
            }
            
            
            
            interchange.addGroup(group);
        }
    }
    
    String clearLineTerm(String s){
        return s.replaceAll(ediFile.lineTerminator, "");
    }
    
    Vector breakTransactionsIntoParserFiles(X12ParserFile file)throws JDOMException, java.io.IOException, Exception{
        long pos = file.getFilePointer();
        String isaLine;
        Vector parserFiles = new Vector();
        boolean writing = false;
        GregorianCalendar gc = new GregorianCalendar();
        File outfile = new File(System.getProperty("application.temp") + gc.getTimeInMillis());
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile)));
        
        while ( (isaLine = file.readLine()) != null) {
            if(isaLine.startsWith("ST")){
                writing = true;
                outfile = new File(System.getProperty("application.temp") + gc.getTimeInMillis());
                out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile)));
                out.println(isaLine);
                System.out.println(isaLine);
            }
            else if(isaLine.startsWith("SE")){
                out.println(isaLine);
                out.close();
                writing = false;
                X12ParserFile pf = new X12ParserFile(outfile);
                pf.FileEndPointer = pos;
                pf.subelementSeparartor = file.subelementSeparartor;
                pf.lineTerminator = file.lineTerminator;
                pf.elementSeparator = file.elementSeparator;
                parserFiles.add(pf);
                System.out.println(isaLine);
            }
            else{
                if(writing){
                    out.println(isaLine);
                    System.out.println(isaLine);
                    
                }
            }
        }
        
        //outfile.delete();
        return parserFiles;
    }
    
    Vector breakGroupsIntoParserFiles(X12File file)throws JDOMException, java.io.IOException, Exception{
        long pos = file.getFilePointer();
        String isaLine = file.readLine();
        Vector parserFiles = new Vector();
        boolean writing = false;
        GregorianCalendar gc = new GregorianCalendar();
        File outfile = new File(System.getProperty("application.temp") + gc.getTimeInMillis());
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile)));
        
        while ( (isaLine = file.readLine()) != null) {
            if(isaLine.startsWith("GS")){
                writing = true;
                outfile = new File(System.getProperty("application.temp") + gc.getTimeInMillis());
                out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile)));
                out.println(isaLine);
            }
            else if(isaLine.startsWith("GE")){
                out.println(isaLine);
                out.close();
                writing = false;
                X12ParserFile pf = new X12ParserFile(outfile);
                pf.FileEndPointer = pos;
                pf.subelementSeparartor = file.subelementSeparartor;
                pf.lineTerminator = file.lineTerminator;
                pf.elementSeparator = file.elementSeparator;
                parserFiles.add(pf);
            }
            else{
                if(writing){
                    out.println(isaLine);
                }
            }
        }
        
        outfile.delete();
        return parserFiles;
    }
    
    void readInterchangeInfo(X12File file)throws JDOMException, java.io.IOException, Exception{
        long pos = file.getFilePointer();
        String isaLine = file.readLine();
        interchange = new X12Interchange();
        X12Segment isa = interchange.getSegmentFromID("ISA");
        isa.getElementFromReference("ISA01").setValue(isaLine.substring(4, 6));
        isa.getElementFromReference("ISA02").setValue(isaLine.substring(7, 17));
        isa.getElementFromReference("ISA03").setValue(isaLine.substring(18, 20));
        isa.getElementFromReference("ISA04").setValue(isaLine.substring(21, 31));
        isa.getElementFromReference("ISA05").setValue(isaLine.substring(32, 34));
        isa.getElementFromReference("ISA06").setValue(isaLine.substring(35, 50));
        isa.getElementFromReference("ISA07").setValue(isaLine.substring(51, 53));
        isa.getElementFromReference("ISA08").setValue(isaLine.substring(54, 69));
        isa.getElementFromReference("ISA09").setValue(isaLine.substring(70, 76));
        isa.getElementFromReference("ISA10").setValue(isaLine.substring(77, 81));
        isa.getElementFromReference("ISA11").setValue(isaLine.substring(82, 83));
        isa.getElementFromReference("ISA12").setValue(isaLine.substring(84, 89));
        isa.getElementFromReference("ISA13").setValue(isaLine.substring(90, 99));
        isa.getElementFromReference("ISA14").setValue(isaLine.substring(100, 101));
        isa.getElementFromReference("ISA15").setValue(isaLine.substring(102, 103));
        isa.getElementFromReference("ISA16").setValue(clearLineTerm(isaLine.substring(104, 105)));
        
        while ( (isaLine = file.readLine()) != null) {
            if(isaLine.startsWith("IEA")){
                String[] parts = isaLine.split(regexSafe(file.elementSeparator));
                X12Segment iea = interchange.getSegmentFromID("IEA");
                iea.getElementFromReference("IEA01").setValue(parts[1]);
                iea.getElementFromReference("IEA02").setValue(clearLineTerm(parts[2]));
            }
        }
        
        file.seek(pos);
    }
    
    String regexSafe(String string){
        if(string.compareTo("*") == 0){
            return "\\*";
        }
        return string;
    }
}

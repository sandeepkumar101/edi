package com.app.edi4j.x12;

import java.io.*;
import java.util.*;

import org.jdom.*;
import com.app.edi4j.x12.structure.*;
import com.app.edi4j.template.*;

public class X12Parser {
  private Document xmlTemplate;
  private X12Transaction transaction;
  private X12Transaction template;
  private String previousLine;
  private String currentLine;
  private int currentLoopIndex = 0;
  private String[] allLoopIDs;
  private String[] trimedLineParts;
  private String[] lineParts;
  private String segmentID;
  private boolean inDetailLoops = false;
  private boolean inHeaderLoops = false;
  private boolean inLoops = false;
  private X12Segment currentSegment;
  private X12Loop currentLoop;
  private X12Validator validator;
  private X12ParserFile ediFile;
  public void setXmlTemplate(Document xmlTemplate) {
    this.xmlTemplate = xmlTemplate;
  }

  public void setEdiFile(X12ParserFile ediFile) {
    this.ediFile = ediFile;
  }

  public void setTemplate(X12Transaction template) {
    this.template = template;
  }

  public void setTransaction(X12Transaction transaction) {
    this.transaction = transaction;
  }

  public void setCurrentLine(String currentLine) {
    this.currentLine = currentLine;
  }

  public void setPreviousLine(String previousLine) {
    this.previousLine = previousLine;
  }

  public void setAllLoopIDs(String[] allLoopIDs) {
    this.allLoopIDs = allLoopIDs;
  }

  public void setCurrentLoop(X12Loop currentLoop) {
    this.currentLoop = currentLoop;
  }

  public void setCurrentSegment(X12Segment currentSegment) {
    this.currentSegment = currentSegment;
  }

  public void setValidator(X12Validator validator) {
    this.validator = validator;
  }

  public Document getXmlTemplate() {
    return xmlTemplate;
  }

  public X12ParserFile getEdiFile() {
    return ediFile;
  }

  public X12Transaction getTemplate() {
    return template;
  }

  public X12Transaction getTransaction() {
    return transaction;
  }

  public String getCurrentLine() {
    return currentLine;
  }

  public String getPreviousLine() {
    return previousLine;
  }

  public String[] getAllLoopIDs() {
    return allLoopIDs;
  }

  public X12Loop getCurrentLoop() {
    return currentLoop;
  }

  public X12Segment getCurrentSegment() {
    return currentSegment;
  }

  public X12Validator getValidator() {
    return validator;
  }

  public X12Parser(Template template, X12ParserFile file) throws IOException, JDOMException {
    setXmlTemplate(template.getTransactionDOM());
    setEdiFile(file);
    setTemplate(template.getTransactionTemplate());
    setTransaction(new X12Transaction());
    allLoopIDs = new String[getTemplate().getLoops().size()];
    for (int i = 0; i < allLoopIDs.length; i++) {
      X12Loop loop = (X12Loop) getTemplate().getLoops().get(i);
      allLoopIDs[i] = loop.getId();
    }
    while ( (currentLine = ediFile.readLine()) != null) {
      handleLine(currentLine);
      previousLine = currentLine;
    }
  }

  /**
   * repeatLoopHeader
   *
   * @return boolean
   */
  public boolean repeatLoopHeader() {
    X12Segment segment = (X12Segment) getCurrentLoop().getSegments().get(0);
    X12Element element = (X12Element) segment.getX12Elements().get(1);
    if (element.getValue().compareTo(trimedLineParts[1]) == 0) {
      return true;
    }
    return false;
  }

  /**
   * startsTransactionLevelLoop
   *
   * @return boolean
   */
  public boolean startsTransactionLevelLoop() {
    Vector loops = template.getLoops();
    for(int i = 0; i < loops.size(); i++){
      X12Loop loop = (X12Loop)loops.get(i);
      X12Segment firstSegment = (X12Segment)loop.getSegments().get(0);
      if(firstSegment.getId().compareTo(segmentID) == 0){
        return true;
      }
    }
    return false;
  }

  /**
   * getNextTransactionLoop
   *
   * @return X12Loop
   */
  public X12Loop getNextTransactionLoop() {
    return template.getLoopFromID(allLoopIDs[currentLoopIndex]).cloneLoop();
  }

  /**
   * handle
   *
   * @param line String
   */
  public void handle(String line) {
    initializeLineVariables(line);
    System.out.println("reading line " + line +
                       " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    if(!inLoops && isHeaderLevelSegment()){
      setCurrentSegment(template.getSegmentFromID(segmentID).cloneSegment());
      transaction.addSegment(getCurrentSegment());
      setData();
    }else if(!inLoops && startsTransactionLevelLoop()){
      setCurrentLoop(getNextTransactionLoop());
      setCurrentSegment(getCurrentLoop().getSegmentFromID(segmentID));
    }

  }

  /**
   * sysncTransactionToTemplate
   */
  public void sysncTransactionToTemplate() {

  }

  /**
   * handleLine
   *
   * @param aLine String
   */
  public void handleLine(String aLine) throws JDOMException {
    initializeLineVariables(aLine);
    System.out.println("reading line " + aLine +
                       " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    if (segmentID.compareTo("HL") == 0) {
      if (currentLoopIndex == 0 && !inDetailLoops) {
        startUnbrokenDetailLoop();
        setData();
      }
      else if (repeatLoopHeader()) {
        startUnbrokenDetailLoop();
        setData();
      }
      else {
        addDetailLoop();
        setData();
      }
    }
    else if (!inDetailLoops && !inHeaderLoops && isHeaderLevelSegment()) {
      addHeaderLevelSegment();
      setData();
    }
    else if (!inDetailLoops && !inHeaderLoops && startsHeaderLoop()) {
      addFirstHeaderLoop();
      setData();
    }
    else if (!inDetailLoops && inHeaderLoops && startsHeaderLoop()) {
      addHeaderLoop();
      setData();
    }
    else if (!inDetailLoops && inHeaderLoops && segmentFollowsInCurrentLoop()) {
      setData();
    }
    else if (inDetailLoops && !inHeaderLoops && segmentFollowsInCurrentLoop()) {
      setData();
    }
    else if (inDetailLoops && !inHeaderLoops && isMutableRepeatingSegment()) {
      setData();
    }
    else if (inDetailLoops && !inHeaderLoops && startsDetailLoop()) {
      if (repeatOfCurrentLoop()) {
        X12Loop loop = transaction.getParentOfLoop(getCurrentLoop());
        X12Loop newLoop = getCurrentLoop().cloneLoop();
        loop.addX12Loop(newLoop);
        setCurrentLoop(newLoop);
        setCurrentSegment( (X12Segment) newLoop.getSegments().get(0));
        setData();
      }
      else if (startsSiblingLoop()) {
        setData();
      }
      else if (startsChildLoop()) {
        setData();
      }
      else if (startsParentLevelLoop()) {
        setData();
      }
      else {
        System.out.println("Inside trouble!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
    }
    else if (inDetailLoops && !inHeaderLoops && isFooterLevelSegment()) {
      addFooterLevelSegment();
      setData();
    }
    else {
      System.out.println("not found trouble!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.println("culprit " + aLine);
    }

  }

  /**
   * isMutableRepeatingSegment
   */
  public boolean isMutableRepeatingSegment() {
    if (getCurrentLoop() == null) {
      return false;
    }
    Vector segments = getCurrentLoop().getSegments();
    int multiple = 0;
    for (int i = 0; i < segments.size(); i++) {
      X12Segment segment = (X12Segment) segments.get(i);
      if (segment.getId().compareTo(segmentID) == 0) {
        multiple++;
      }
    }
    if (multiple < 2) {
      return false;
    }
    else {
      for (int i = 0; i < segments.size(); i++) {
        X12Segment segment = (X12Segment) segments.get(i);
        Vector elements = segment.getX12Elements();
        for (int j = 0; j < elements.size(); j++) {
          X12Element elem = (X12Element) elements.get(j);
          Vector choices = elem.getChoices();
          for (int k = 0; k < choices.size(); k++) {
            X12ChoiceSet cs = (X12ChoiceSet) choices.get(k);
            if (cs.getCode().compareTo(trimedLineParts[0]) == 0) {
              setCurrentSegment(segment);
              return true;
            }
          }
        }
      }

    }
    return false;
  }

  /**
   * isFooterLevelSegment
   *
   * @return boolean
   */
  public boolean isFooterLevelSegment() {
    X12Segment segment = template.getSegmentFromID(segmentID);
    if (segment == null) {
      return false;
    }
    Vector footers = template.getFooterSegment();
    for (int i = 0; i < footers.size(); i++) {
      X12Segment seg = (X12Segment) footers.get(i);
      if (seg.getId().compareTo(segmentID) == 0) {
        setCurrentSegment(segment);
        return true;
      }
    }

    return false;
  }

  /**
   * startsSiblingLoop
   *
   * @return boolean
   */
  public boolean startsSiblingLoop() {
    if (template.containsLoop(getCurrentLoop())) {
      return false;
    }
    X12Loop parentLoop = template.getParentOfLoop(getCurrentLoop());
    Vector siblingLoops = parentLoop.getX12Loops();
    int qualifiedSiblings = 0;
    for (int i = 0; i < siblingLoops.size(); i++) {
      X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
      X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
      if (segment.getId().compareTo(segmentID) == 0) {
        qualifiedSiblings++;
      }
    }
    if (qualifiedSiblings == 1) {
      for (int i = 0; i < siblingLoops.size(); i++) {
        X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
        X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
        if (segment.getId().compareTo(segmentID) == 0) {
          setCurrentLoop(siblingLoop);
          setCurrentSegment(segment);
          return true;
        }
      }
    }
    else if (qualifiedSiblings > 1) {
      for (int i = 0; i < siblingLoops.size(); i++) {
        X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
        X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
        Vector theseElements = segment.getX12Elements();
        X12Element qualifier = (X12Element) theseElements.get(0);
        Vector choices = qualifier.getChoices();
        for (int k = 0; k < choices.size(); k++) {
          X12ChoiceSet cs = (X12ChoiceSet) choices.get(k);
          if (cs.getCode().compareTo(trimedLineParts[0]) == 0) {
            setCurrentSegment(segment);
            setCurrentLoop(siblingLoop);
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * startsParentLoop
   */
  public boolean startsParentLevelLoop() {
    if (template.containsLoop(getCurrentLoop())) {
      return false;
    }
    X12Loop parentLoop = template.getParentOfLoop(getCurrentLoop());
    X12Loop grandParentLoop = template.getParentOfLoop(parentLoop);
    Vector siblingLoops = grandParentLoop.getX12Loops();
    int qualifiedSiblings = 0;
    for (int i = 0; i < siblingLoops.size(); i++) {
      X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
      X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
      if (segment.getId().compareTo(segmentID) == 0) {
        qualifiedSiblings++;
      }
    }
    if (qualifiedSiblings == 1) {
      for (int i = 0; i < siblingLoops.size(); i++) {
        X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
        X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
        if (segment.getId().compareTo(segmentID) == 0) {
          setCurrentLoop(siblingLoop);
          setCurrentSegment(segment);
          return true;
        }
      }
    }
    else if (qualifiedSiblings > 1) {
      for (int i = 0; i < siblingLoops.size(); i++) {
        X12Loop siblingLoop = (X12Loop) siblingLoops.get(i);
        X12Segment segment = (X12Segment) siblingLoop.getSegments().get(0);
        Vector theseElements = segment.getX12Elements();
        X12Element qualifier = (X12Element) theseElements.get(0);
        Vector choices = qualifier.getChoices();
        for (int k = 0; k < choices.size(); k++) {
          X12ChoiceSet cs = (X12ChoiceSet) choices.get(k);
          if (cs.getCode().compareTo(trimedLineParts[0]) == 0) {
            setCurrentSegment(segment);
            setCurrentLoop(siblingLoop);
            return true;
          }
        }
      }
    }
    return false;

  }

  /**
   * startsChildLoop
   *
   * @return boolean
   */
  public boolean startsChildLoop() {
    Vector allLoops = template.getAllLoops();
    int qualifiedChildren = 0;
    for (int i = 0; i < allLoops.size(); i++) {
      X12Loop thisLoop = (X12Loop) allLoops.get(i);
      if (thisLoop.getId().compareTo(getCurrentLoop().getId()) == 0) {
        Vector childLoops = thisLoop.getX12Loops();
        for (int j = 0; j < childLoops.size(); j++) {
          X12Loop childLoop = (X12Loop) childLoops.get(j);
          if (childLoop.getSegmentFromID(segmentID) != null) {
            qualifiedChildren++;
          }
        }
      }
    }
    if (qualifiedChildren == 1) {
      for (int i = 0; i < allLoops.size(); i++) {
        X12Loop thisLoop = (X12Loop) allLoops.get(i);
        if (thisLoop.getId().compareTo(getCurrentLoop().getId()) == 0) {
          Vector childLoops = thisLoop.getX12Loops();
          for (int j = 0; j < childLoops.size(); j++) {
            X12Loop childLoop = (X12Loop) childLoops.get(j);
            if (childLoop.getSegmentFromID(segmentID) != null) {
              setCurrentSegment(childLoop.getSegmentFromID(segmentID));
              setCurrentLoop(childLoop);
              return true;
            }
          }
        }
      }
    }
    else if (qualifiedChildren > 1) {
      for (int i = 0; i < allLoops.size(); i++) {
        X12Loop thisLoop = (X12Loop) allLoops.get(i);
        if (thisLoop.getId().compareTo(getCurrentLoop().getId()) == 0) {
          Vector childLoops = thisLoop.getX12Loops();
          for (int j = 0; j < childLoops.size(); j++) {
            X12Loop childLoop = (X12Loop) childLoops.get(j);
            if (childLoop.getSegmentFromID(segmentID) != null) {
              X12Segment thisSegment = childLoop.getSegmentFromID(segmentID);
              Vector theseElements = thisSegment.getX12Elements();
              X12Element qualifier = (X12Element) theseElements.get(0);
              Vector choices = qualifier.getChoices();
              for (int k = 0; k < choices.size(); k++) {
                X12ChoiceSet cs = (X12ChoiceSet) choices.get(k);
                if (cs.getCode().compareTo(trimedLineParts[0]) == 0) {
                  setCurrentSegment(childLoop.getSegmentFromID(segmentID));
                  setCurrentLoop(childLoop);
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * startsSiblingRoot
   */
  public boolean startsSiblingRoot() {
    return false;
  }

  /**
   * repeatOfCurrentLoop
   *
   * @return boolean
   */
  public boolean repeatOfCurrentLoop() {
    boolean hasModifier = false;
    X12Segment seg = (X12Segment) getCurrentLoop().getSegments().get(0);
    X12Element elem = (X12Element) seg.getX12Elements().get(0);
    Vector choices = elem.getChoices();
    if (choices.size() > 0) {
      hasModifier = true;
    }

    if (elem.getValue() != null) {
      if (seg.getId().compareTo(segmentID) == 0 && !hasModifier) {
        return true;
      }
    }
    return false;
  }

  public boolean startsDetailLoop() throws JDOMException {
    Vector allLoops = template.getAllLoops();
    for (int i = 0; i < allLoops.size(); i++) {
      X12Loop thisLoop = (X12Loop) allLoops.get(i);
      Vector segments = thisLoop.getSegments();
      if (segments.size() > 0) {
        X12Segment first = (X12Segment) segments.get(0);
        if (first.getId().compareTo(segmentID) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * segmentFollowInCurrentLoop
   */
  public boolean segmentFollowsInCurrentLoop() {
    boolean returnB = false;
    int pos = -1;
    if (getCurrentLoop() == null) {
      return false;
    }
    Vector segments = getCurrentLoop().getSegments();
    for (int i = 0; i < segments.size(); i++) {
      X12Segment segment = (X12Segment) segments.get(i);
      if (segment.getId().compareTo(getCurrentSegment().getId()) == 0) {
        pos = i;
      }
    }
    if (pos > -1) {
      for (int i = pos + 1; i < segments.size(); i++) {
        X12Segment segment = (X12Segment) segments.get(i);
        if (segment.getId().compareTo(segmentID) == 0) {
          setCurrentSegment(segment);
          returnB = true;
          return returnB;
        }
      }
    }
    return returnB;
  }

  /**
   * addHeaderLoop
   */
  public void addHeaderLoop() {
    currentLoopIndex++;
    setCurrentLoop(template.getLoopFromID(allLoopIDs[currentLoopIndex]));
    setCurrentSegment(getCurrentLoop().getSegmentFromID(segmentID));
    transaction.addLoop(getCurrentLoop());

  }

  /**
   * addFirstHeaderLoop
   */
  public void addFirstHeaderLoop() {
    inHeaderLoops = true;
    setCurrentLoop(template.getLoopFromID(allLoopIDs[currentLoopIndex]));
    setCurrentSegment(getCurrentLoop().getSegmentFromID(segmentID));
    transaction.addLoop(getCurrentLoop());
  }

  /**
   * startsHeaderLoop
   *
   * @return short
   */
  public boolean startsHeaderLoop() {
    boolean returnB = false;
    Vector hLoops = template.getLoops();
    for (int i = 0; i < hLoops.size(); i++) {
      X12Loop thisLoop = (X12Loop) hLoops.get(i);
      if (thisLoop.getId().compareTo(allLoopIDs[currentLoopIndex]) == 0) {
        Vector theseSegments = thisLoop.getSegments();
        for (int j = 0; j < theseSegments.size(); j++) {
          X12Segment thisSegment = (X12Segment) theseSegments.get(0);
          if (thisSegment.getId().compareTo(segmentID) == 0) {
            returnB = true;
            return returnB;
          }
        }
      }
    }

    return returnB;
  }

  /**
   * isHeaderLevelSegment
   *
   * @return boolean
   */
  public boolean isHeaderLevelSegment() {
    X12Segment segment = template.getSegmentFromID(segmentID);
    if (segment == null) {
      return false;
    }
    Integer pos = new Integer(segment.getPosition());
    if (pos.intValue() > 100) {
      return false;
    }
    return true;
  }

  /**
   * addHeaderLevelSegment
   */
  public void addHeaderLevelSegment() {
    setCurrentSegment(template.getSegmentFromID(segmentID));
    transaction.addSegment(getCurrentSegment());
  }

  /**
   * addFooterLevelSegment
   */
  public void addFooterLevelSegment() {
    setCurrentLoop(null);
    setCurrentSegment(template.getSegmentFromID(segmentID));
    transaction.addSegment(getCurrentSegment());
  }

  /**
   * addDetailLoop
   */
  public void addDetailLoop() {
    inDetailLoops = true;
    inHeaderLoops = false;

    currentLoopIndex++;
    setCurrentLoop(template.getLoopFromID(allLoopIDs[currentLoopIndex]));
    transaction.addLoop(currentLoop);
    setCurrentSegment( (X12Segment) currentLoop.getSegments().get(0));
  }

  /**
   * startUnbrokenDetailLoop
   */
  public void startUnbrokenDetailLoop() {
    inDetailLoops = true;
    inHeaderLoops = false;

    setCurrentLoop(template.getLoopFromID(allLoopIDs[currentLoopIndex]));
    transaction.addLoop(currentLoop);
    setCurrentSegment( (X12Segment) currentLoop.getSegments().get(0));

  }

  /**
   * setData
   */
  public void setData() {
    Vector elements = getCurrentSegment().getX12Elements();
    if (getCurrentLoop() != null) {
    }
    for (int i = 0; i < trimedLineParts.length; i++) {
      X12Element thisElement = (X12Element) elements.get(i);
      String contents = trimedLineParts[i].trim();
      if (contents.compareTo("") != 0) {

        //X12Segment isaSegment = (X12Segment) transaction.getSegmentFromID("ISA");
        //Vector isaElements = isaSegment.getX12Elements();
        //X12Element sepElement = (X12Element) isaElements.get(isaElements.size() -
        //    1);
        //String separator = sepElement.getValue();
        String[] subparts = contents.split(":");
        //String[] subparts = contents.split(separator);
        if (subparts.length > 1) {
          Vector subs = thisElement.getSubelements();
          for (int j = 0; j < subparts.length; j++) {
            if (subparts[j].compareTo("") != 0) {
              X12SubElement thisSub = (X12SubElement) subs.get(j);
              thisSub.setValue(subparts[j]);
            }
          }
        }
        else {
          thisElement.setValue(contents);
        }
      }
    }

  }

  /**
   * initializeLineVariables
   */
  public void initializeLineVariables(String aLine) {
    aLine = aLine.replaceAll("~", "");
    currentLine = aLine;
    lineParts = aLine.split("\\*");
    trimedLineParts = new String[lineParts.length - 1];
    for (int i = 1; i < lineParts.length; i++) {
      trimedLineParts[i - 1] = lineParts[i];
    }
    segmentID = lineParts[0];
  }

}

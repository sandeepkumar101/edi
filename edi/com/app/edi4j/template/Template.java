package com.app.edi4j.template;
import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import com.app.edi4j.x12.structure.*;

public class Template {
  private String transactionType;
  private File xmlStructureTemplateFile;
  private Document transactionDOM;

  private Vector xmlRuleFiles = new Vector();
  private X12Transaction transactionTemplate = new X12Transaction();
  private String transmissionType;
  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public void setTransmissionType(String transmissionType) {
    this.transmissionType = transmissionType;
  }

  public void setXmlStructureTemplateFile(File xmlStructureTemplateFile) {
    this.xmlStructureTemplateFile = xmlStructureTemplateFile;
  }

  public void setXmlRuleFiles(Vector xmlRuleFiles) {
    this.xmlRuleFiles = xmlRuleFiles;
  }

  public void setTransactionTemplate(X12Transaction transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  public void setTransactionDOM(Document transactionDOM) {
    this.transactionDOM = transactionDOM;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public String getTransmissionType() {
    return transmissionType;
  }

  public File getXmlStructureTemplateFile() {
    return xmlStructureTemplateFile;
  }

  public Vector getXmlRuleFiles() {
    return xmlRuleFiles;
  }

  public X12Transaction getTransactionTemplate() {
    return transactionTemplate;
  }

  public Document getTransactionDOM() {
    return transactionDOM;
  }

  /**
   * addXMLRuleFile
   *
   * @param ruleXML File
   */
  public void addXMLRuleFile(File ruleXML) {
    xmlRuleFiles.add(ruleXML);
  }

  public Template(File templateFile) throws org.jdom.JDOMException, IOException {
      setXmlStructureTemplateFile(templateFile);
      initialize();
  }

  /**
   * initializeTemplateVariables
   */
  public void initialize() throws org.jdom.JDOMException, IOException {
    SAXBuilder saxBuilder = new SAXBuilder();
    transactionDOM = saxBuilder.build(getXmlStructureTemplateFile());
    Element root = transactionDOM.getRootElement();
    List l = root.getChildren("loop");
    Iterator it = l.iterator();
    //loop level 1
    ////////////////////////////////////
    while (it.hasNext()) {
      Element levelOneElement = (Element) it.next();
      X12Loop levelOneLoop = new X12Loop();
      levelOneLoop = registerLoop(levelOneElement, levelOneLoop);
      List levelOneList = levelOneElement.getChildren();
      Iterator levelOneIterator = levelOneList.iterator();
      //loop level 2
      /////////////////////////////////////
      while (levelOneIterator.hasNext()) {
        Element levelTwoElement = (Element) levelOneIterator.next();
        //if level 2 has allLoopIDs
        //////////////////////
        if (levelTwoElement.getName().compareTo("loop") == 0) {
          X12Loop levelTwoLoop = new X12Loop();
          levelTwoLoop = registerLoop(levelTwoElement, levelTwoLoop);
          levelOneLoop.addX12Loop(levelTwoLoop);
          List levelTwoList = levelTwoElement.getChildren();
          Iterator levelTwoIterator = levelTwoList.iterator();
          //level 3 allLoopIDs
          ////////////////////////
          while (levelTwoIterator.hasNext()) {
            Element levelThreeElement = (Element) levelTwoIterator.next();
            //if level 3 has allLoopIDs
            /////////////////////////
            if (levelThreeElement.getName().compareTo("loop") == 0) {
              X12Loop levelThreeLoop = new X12Loop();
              levelThreeLoop = registerLoop(levelThreeElement, levelThreeLoop);
              levelTwoLoop.addX12Loop(levelThreeLoop);
              List levelThreeList = levelThreeElement.getChildren();
              Iterator levelThreeIterator = levelThreeList.iterator();
              //loop level 4
              //////////////////////////////
              while (levelThreeIterator.hasNext()) {
                Element levelFourElement = (Element) levelThreeIterator.next();
                //If level 4 has allLoopIDs
                if (levelFourElement.getName().compareTo("loop") == 0) {
                  X12Loop levelFourLoop = new X12Loop();
                  levelFourLoop = registerLoop(levelFourElement, levelFourLoop);
                  levelThreeLoop.addX12Loop(levelFourLoop);
                  List levelFourList = levelFourElement.getChildren();
                  Iterator levelFourIterator = levelFourList.iterator();
                  //loop level 4
                  //////////////////////////////
                  while (levelFourIterator.hasNext()) {
                    Element levelFiveElement = (Element) levelFourIterator.
                        next();
                    //If level 4 has allLoopIDs
                    if (levelFiveElement.getName().compareTo("loop") == 0) {
                      X12Loop levelFiveLoop = new X12Loop();
                      levelFiveLoop = registerLoop(levelFiveElement,
                          levelFiveLoop);
                      levelFourLoop.addX12Loop(levelFiveLoop);
                    } //end if level 4 has allLoopIDs
                    //if level 4 has segments
                    ///////////////////////////////////
                    else if (levelFiveElement.getName().compareTo("segment") ==
                             0) {
                      X12Segment levelFiveSegment = new X12Segment();
                      levelFiveSegment = registerSegment(levelFiveElement,
                          levelFiveSegment);
                      List levelFiveList = levelFiveElement.getChildren(
                          "element");
                      Iterator levelFiveIterator = levelFiveList.iterator();
                      while (levelFiveIterator.hasNext()) { //while more elements
                        Element levelSixElement = (Element) levelFiveIterator.
                            next();
                        X12Element levelSixX12Element = new X12Element();
                        levelSixX12Element = registerElement(levelSixElement,
                            levelSixX12Element);
                        levelFiveSegment.addX12Element(levelSixX12Element);
                      } //end while elements
                      levelFourLoop.addSegment(levelFiveSegment);
                    } //end if level 4 segments
                  } ///end loop level 4

                } //end if level 4 has allLoopIDs
                //if level 4 has segments
                ///////////////////////////////////
                else if (levelFourElement.getName().compareTo("segment") == 0) {
                  X12Segment levelFourSegment = new X12Segment();
                  levelFourSegment = registerSegment(levelFourElement,
                      levelFourSegment);
                  List levelFourList = levelFourElement.getChildren("element");
                  Iterator levelFourIterator = levelFourList.iterator();
                  while (levelFourIterator.hasNext()) { //while more elements
                    Element levelFiveElement = (Element) levelFourIterator.next();
                    X12Element levelFiveX12Element = new X12Element();
                    levelFiveX12Element = registerElement(levelFiveElement,
                        levelFiveX12Element);
                    levelFourSegment.addX12Element(levelFiveX12Element);
                  } //end while elements
                  levelThreeLoop.addSegment(levelFourSegment);
                } //end if level 4 segments
              } ///end loop level 4
            } //end if level 3 has allLoopIDs
            //if level 3 has segments
            else if (levelThreeElement.getName().compareTo("segment") == 0) {
              X12Segment levelThreeSegment = new X12Segment();
              levelThreeSegment = registerSegment(levelThreeElement,
                                                  levelThreeSegment);
              List levelThreeList = levelThreeElement.getChildren("element");
              Iterator levelThreeIterator = levelThreeList.iterator();
              while (levelThreeIterator.hasNext()) {
                Element levelFourElement = (Element) levelThreeIterator.next();
                X12Element levelFourX12Element = new X12Element();
                levelFourX12Element = registerElement(levelFourElement,
                    levelFourX12Element);
                levelThreeSegment.addX12Element(levelFourX12Element);
              }
              levelTwoLoop.addSegment(levelThreeSegment);
            } ///end if level 3 has segments
          } //////////end loop level 3

        } ///end if level level 2 has allLoopIDs
        else if (levelTwoElement.getName().compareTo("segment") == 0) {
          X12Segment levelTwoSegment = new X12Segment();
          levelTwoSegment = registerSegment(levelTwoElement, levelTwoSegment);
          List levelTwoList = levelTwoElement.getChildren("element");
          Iterator levelTwoIterator = levelTwoList.iterator();
          while (levelTwoIterator.hasNext()) {
            Element levelThreeElement = (Element) levelTwoIterator.next();
            X12Element levelThreeX12Element = new X12Element();
            levelThreeX12Element = registerElement(levelThreeElement,
                levelThreeX12Element);
            levelTwoSegment.addX12Element(levelThreeX12Element);
          }
          levelOneLoop.addSegment(levelTwoSegment);
        }
      } ////////////end level 2
      transactionTemplate.addLoop(levelOneLoop);
    } ////////////end level 1
    List l2 = root.getChildren("segment");
    Iterator it2 = l2.iterator();
    while (it2.hasNext()) {
      Element segElement = (Element) it2.next();
      X12Segment seg2 = new X12Segment();
      seg2 = registerSegment(segElement, seg2);
      List l3 = segElement.getChildren("element");
      Iterator it3 = l3.iterator();
      while (it3.hasNext()) {
        Element elElement = (Element) it3.next();
        X12Element elX12Element = new X12Element();
        elX12Element = registerElement(elElement,
                                       elX12Element);
        seg2.addX12Element(elX12Element);
      }
      transactionTemplate.addSegment(seg2);
    }
  }

  /**
   * registerLoop
   */
  public X12Loop registerLoop(Element JDOMLoop, X12Loop loop) {
    loop.setId(JDOMLoop.getAttributeValue("id"));
    loop.setLevel(JDOMLoop.getAttributeValue("level"));
    loop.setName(JDOMLoop.getAttributeValue("name"));
    loop.setRepeat(JDOMLoop.getAttributeValue("repeat"));
    return loop;
  }

  /**
   * registerSubElement
   */
  public X12SubElement registerSubElement(Element JDOMElement,
                                          X12SubElement element) {
    element.setAlias(JDOMElement.getAttributeValue("alias"));
    element.setData_type(JDOMElement.getAttributeValue("dat_type"));
    element.setData_element(JDOMElement.getAttributeValue("data_element"));
    element.setMin(JDOMElement.getAttributeValue("min"));
    element.setMax(JDOMElement.getAttributeValue("max"));
    element.setName(JDOMElement.getAttributeValue("name"));
    element.setRef_des(JDOMElement.getAttributeValue("ref_des"));
    element.setRequirement(JDOMElement.getAttributeValue("requirement"));
    element.setUsage(JDOMElement.getAttributeValue("usage"));
    if (JDOMElement.getChildText("alias") != null) {
      element.setAlias(JDOMElement.getChildText("alias").toString());
    }
    if (JDOMElement.getChildText("description") != null) {
      element.setIndustryDescription(JDOMElement.getChildText("description").
                                     toString());
    }
    JDOMElement.getChildText("code_definition");
    List l1 = JDOMElement.getChildren("code_definition");
    Iterator it1 = l1.iterator();
    while (it1.hasNext()) {
      Element nextElement = (Element) it1.next();
      X12ChoiceSet cs1 = new X12ChoiceSet();
      if (nextElement.getAttributeValue("code").toString() != null) {
        cs1.setCode(nextElement.getAttributeValue("code").toString());
      }
      if (nextElement.getAttributeValue("description").toString() != null) {
        cs1.setValue(nextElement.getAttributeValue("description").toString());
      }
      if (nextElement.getAttributeValue("code_source").toString() != null) {
        cs1.setExternalCodeSet(nextElement.getAttributeValue("code_source").
                               toString());
      }
      element.addChoice(cs1);
    }
    return element;

  }

  /**
   * registerElement
   */
  public X12Element registerElement(Element JDOMElement, X12Element element) {

    element.setAlias(JDOMElement.getAttributeValue("alias"));
    element.setData_type(JDOMElement.getAttributeValue("dat_type"));
    element.setData_element(JDOMElement.getAttributeValue("data_element"));
    element.setMin(JDOMElement.getAttributeValue("min"));
    element.setMax(JDOMElement.getAttributeValue("max"));
    element.setName(JDOMElement.getAttributeValue("name"));
    element.setRef_des(JDOMElement.getAttributeValue("ref_des"));
    element.setRequirement(JDOMElement.getAttributeValue("requirement"));
    element.setUsage(JDOMElement.getAttributeValue("usage"));
    if (JDOMElement.getChildText("alias") != null) {
      element.setAlias(JDOMElement.getChildText("alias").toString());
    }
    if (JDOMElement.getChildText("description") != null) {
      element.setIndustryDescription(JDOMElement.getChildText("description").
                                     toString());
    }
    JDOMElement.getChildText("code_definition");
    List l1 = JDOMElement.getChildren("code_definition");
    Iterator it1 = l1.iterator();
    while (it1.hasNext()) {
      Element nextElement = (Element) it1.next();
      X12ChoiceSet cs1 = new X12ChoiceSet();
      if (nextElement.getAttributeValue("code") != null) {
        cs1.setCode(nextElement.getAttributeValue("code").toString());
      }
      if (nextElement.getAttributeValue("description").toString() != null) {
        cs1.setValue(nextElement.getAttributeValue("description").toString());
      }
      if (nextElement.getAttributeValue("code_source").toString() != null) {
        cs1.setExternalCodeSet(nextElement.getAttributeValue("code_source").
                               toString());
      }
      element.addChoice(cs1);
    }

    List l2 = JDOMElement.getChildren("subelement");
    Iterator it2 = l2.iterator();
    while (it2.hasNext()) {
      Element subelement = (Element) it2.next();
      X12SubElement se = new X12SubElement();
      se = registerSubElement(subelement, se);
      element.addSubElement(se);
    }

    return element;
  }

  /**
   * registerSegment
   */
  public X12Segment registerSegment(Element JDOMElement, X12Segment segment) {

    segment.setId(JDOMElement.getAttributeValue("id"));
    segment.setName(JDOMElement.getAttributeValue("name"));
    segment.setRepeat(JDOMElement.getAttributeValue("repeat"));
    segment.setPosition(JDOMElement.getAttributeValue("position"));
    segment.setDescription(JDOMElement.getAttributeValue("description"));
    segment.setLevel(JDOMElement.getAttributeValue("level"));
    segment.setMaxUse(JDOMElement.getAttributeValue("maxuse"));
    segment.setRequirement(JDOMElement.getAttributeValue("requirement"));
    segment.setUsage(JDOMElement.getAttributeValue("usage"));
    return segment;
  }

}

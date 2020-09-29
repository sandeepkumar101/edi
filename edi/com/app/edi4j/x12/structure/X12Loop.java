package com.app.edi4j.x12.structure;

import java.util.*;

public class X12Loop {
  private String id = null;
  private String repeat = null;
  private Vector loops = new Vector();
  private Vector segments = new Vector();
  private String level = null;
  private String name = null;

  /**
     * clone
     *
     * @return X12Loop
     */
    public X12Loop cloneLoop() {

      X12Loop r = new X12Loop();
      for(int i = 0; i < getX12Loops().size(); i++){
        X12Loop loopToAdd = (X12Loop)getX12Loops().get(i);
        r.addX12Loop(loopToAdd.cloneLoop());
      }
      for(int i = 0; i < getSegments().size(); i++){
        X12Segment segmentToAdd = (X12Segment)getSegments().get(i);
        r.addSegment(segmentToAdd.cloneSegment());
      }
      String aID = getId();
      r.setId(aID);
      String aLevel = getLevel();
      r.setLevel(aLevel);
      String aName = getName();
      r.setName(aName);
      String aRepeat = getRepeat();
      r.setRepeat(aRepeat);

      return r;
    }


  /**
   * isActive
   *
   * @return boolean
   */
  public boolean isActive() {

    for(int i = 0; i < segments.size(); i++){
      X12Segment segment = (X12Segment)segments.get(i);
      Vector elements = segment.getX12Elements();
      for(int j = 0; j < elements.size(); j++){
        X12Element element = (X12Element)elements.get(j);
        if(element.isActive()){
          return true;
        }
        Vector subs = element.getSubelements();
        for(int k = 0; k < subs.size(); k++){
          X12SubElement sub = (X12SubElement)subs.get(k);
          if(sub.isActive()){
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * addSegment
   */
  public void addSegment(X12Segment s) {
    segments.add(s);
  }

  /**
   * addX12Loop
   *
   * @param anObject X12Loop
   */
  public void addX12Loop(X12Loop aX12Loop) {
    loops.add(aX12Loop);
  }

  /**
   * sortSegments
   */
  public void sortSegments() {
    String[] segmentPosition = new String[segments.size()];
    for(int i = 0; i < segments.size(); i++){
      X12Segment thisSegment = (X12Segment)segments.get(i);
      segmentPosition[i] = thisSegment.getPosition();
    }
    Arrays.sort(segmentPosition);
    Vector newSegments = new Vector();
    for(int i = 0; i < segments.size(); i++){
      newSegments.add(getSegmentFromPosition(segmentPosition[i]));
    }
    setSegments(newSegments);
  }


  /**
   * getSegmentFromID
   *
   * @param segmentID String
   */
  public X12Segment getSegmentFromPosition(String segmentPosition) {
    X12Segment returnL = null;
    for(int i = 0; i < segments.size(); i++){
      X12Segment thisSegment = (X12Segment)segments.get(i);
      if(thisSegment.getPosition().compareTo(segmentPosition) == 0){
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
    for(int i = 0; i < segments.size(); i++){
      X12Segment thisSegment = (X12Segment)segments.get(i);
      if(thisSegment.getId().compareTo(segmentID) == 0){
        returnL = thisSegment;
        return returnL;
      }
    }
      return returnL;

  }


  /**
   * containsLoop
   *
   * @return boolean
   */
  public boolean containsLoop(X12Loop loop) {
    for(int i = 0; i < loops.size(); i++){
      X12Loop thisLoop = (X12Loop)loops.get(i);
      if(thisLoop.getId().compareTo(loop.getId()) == 0){
        return true;
      }
    }
    return false;
  }

  /**
   * containsSegment
   *
   * @param segmentID String
   */
  public boolean containsSegment(String segmentID) {
    for(int i = 0; i < segments.size(); i++){
      X12Segment segment = (X12Segment)segments.get(i);
      if(segment.getId().compareTo(segmentID) == 0){
        return true;
      }
    }
    return false;
  }

  /**
   * getLoopFromID
   *
   * @param loopID String
   */
  public X12Loop getLoopFromID(String loopID) {
    X12Loop returnL = null;
    for(int i = 0; i < loops.size(); i++){
      X12Loop thisLoop = (X12Loop)loops.get(i);
      if(thisLoop.getId().compareTo(loopID) == 0){
        returnL = thisLoop;
        return returnL;
      }
    }
      return returnL;
  }

  /**
   * sortLoops
   */
  public void sortLoops() {
    String[] loopID = new String[loops.size()];
    for(int i = 0; i < loops.size(); i++){
      X12Loop thisLoop = (X12Loop)loops.get(i);
      loopID[i] = thisLoop.getId();
    }
    Arrays.sort(loopID);
    Vector newLoops = new Vector();
    for(int i = 0; i < loops.size(); i++){
      newLoops.add(getLoopFromID(loopID[i]));
    }
    setX12Loops(newLoops);
  }


  public void setId(String id) {
    this.id = id;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRepeat(String repeat) {
    this.repeat = repeat;
  }

  public void setSegments(Vector segments) {
    this.segments = segments;
  }

  public void setX12Loops(Vector loops) {
    this.loops = loops;
  }

  public String getId() {
    return id;
  }

  public String getLevel() {
    return level;
  }

  public String getName() {
    return name;
  }

  public String getRepeat() {
    return repeat;
  }

  public Vector getSegments() {
    return segments;
  }

  public Vector getX12Loops() {
    return loops;
  }

  public X12Loop() {
  }
}

package com.app.edi4j.x12.structure;

import java.util.*;

public class X12Transaction {
  private Vector loops = new Vector();
  private Vector segments = new Vector();
  private Vector footerSegments = new Vector();
  private Vector headerSegments = new Vector();

  public void setLoops(Vector loops) {
    this.loops = loops;
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

  public Vector getLoops() {
    return loops;
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
   * getAllLoops
   */
  public Vector getAllLoops() {
    Vector returnV = new Vector();
    for (int i = 0; i < loops.size(); i++) {
      X12Loop level1 = (X12Loop) loops.get(i);
      returnV.add(level1);
      Vector level2Loops = level1.getX12Loops();
      for (int j = 0; j < level2Loops.size(); j++) {
        X12Loop level2 = (X12Loop) level2Loops.get(j);
        returnV.add(level2);
        Vector level3Loops = level2.getX12Loops();
        for (int k = 0; k < level3Loops.size(); k++) {
          X12Loop level3 = (X12Loop) level3Loops.get(k);
          returnV.add(level3);
          Vector level4Loops = level3.getX12Loops();
          for (int l = 0; l < level4Loops.size(); l++) {
            X12Loop level4 = (X12Loop) level4Loops.get(l);
            returnV.add(level4);
          }
        }
      }
    }

    return returnV;
  }

  /**
   * getParentOfLoop
   */
  public X12Loop getParentOfLoop(X12Loop loop) {
    boolean found = false;
    X12Loop returnL = null;
    for (int i = 0; i < loops.size(); i++) {
      X12Loop level1 = (X12Loop) loops.get(i);
      if (level1.containsLoop(loop)) {
        found = true;
        return level1;
      }
      Vector level2Loops = level1.getX12Loops();
      for (int j = 0; j < level2Loops.size(); j++) {
        X12Loop level2 = (X12Loop) level2Loops.get(j);
        if (level2.containsLoop(loop)) {
          found = true;
          return level2;
        }
        Vector level3Loops = level2.getX12Loops();
        for (int k = 0; k < level3Loops.size(); k++) {
          X12Loop level3 = (X12Loop) level3Loops.get(k);
          if (level3.containsLoop(loop)) {
            found = true;
            return level3;
          }
          Vector level4Loops = level3.getX12Loops();
          for (int l = 0; l < level4Loops.size(); l++) {
            X12Loop level4 = (X12Loop) level4Loops.get(l);
            if (level4.containsLoop(loop)) {
              found = true;
              return level4;
            }
          }
        }
      }
    }
    return returnL;
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



  /**
   * getLoopFromID
   *
   * @param loopID String
   */
  public X12Loop getLoopFromID(String loopID) {
    X12Loop returnL = null;
    for (int i = 0; i < loops.size(); i++) {
      X12Loop thisLoop = (X12Loop) loops.get(i);
      if (thisLoop.getId().compareTo(loopID) == 0) {
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
    if(loops.size() > 1){

      String[] loopID = new String[loops.size()];
      for (int i = 0; i < loops.size(); i++) {
        X12Loop thisLoop = (X12Loop) loops.get(i);
        loopID[i] = thisLoop.getId();
      }
      Arrays.sort(loopID);
      Vector newLoops = new Vector();
      for (int i = 0; i < loops.size(); i++) {
        newLoops.add(getLoopFromID(loopID[i]));
      }
      setLoops(newLoops);
    }
  }

  /**
   * addLoop
   *
   * @param aLoop Loop
   */
  public void addLoop(X12Loop aLoop) {
    loops.add(aLoop);
    sortLoops();
  }

  public X12Transaction() {
  }
}

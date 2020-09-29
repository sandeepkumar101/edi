package com.app.editoolkit.util;

import java.util.Comparator;
import java.util.StringTokenizer;

public class AlphaNumericComparator
    implements Comparator {
  /* (non-Javadoc) * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
  public int compare(Object arg0, Object arg1) { // TODO Auto-generated method stub
    if ( (arg1 instanceof String) && (arg0 instanceof String)) {
      if ( ( (String) arg0).indexOf("Name_") != -1 &&
          ( (String) arg1).indexOf("Name_") != -1) {
        if (getNumber( (String) arg0) == getNumber( (String) arg1)) {
          return ( (String) arg0).compareTo( ( (String) arg1));
        }
        else if (getNumber( (String) arg0) > getNumber( (String) arg1)) {
          return 1;
        }
        else {
          return -1;
        }
      }
    }
    return 0;
  }

  public int getNumber(String value) {
    StringTokenizer st = new StringTokenizer(value, "0123456789");
    String firstValue = st.nextToken();
    String secondValue = st.nextToken();
    int retValue = Integer.parseInt(value.substring(value.indexOf(firstValue) +
        firstValue.length(), value.indexOf(secondValue)));
    return retValue;
  }
}

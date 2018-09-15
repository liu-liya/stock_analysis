package analysis.stock.com;

import java.io.*;

import java.text.*;
import java.util.Date;

public class Node {
    public Date   dayTime;
    public int    index;
    public Float  open;
    public Float  high;
    public Float  low;
    public Float  close;
    public Float  volumn;
    public Float  money;
        
    public Node () {
        
    }

    public Node ( int pDayCount, Date p_dayTime, Float pOpen, Float pHigh, Float pLow, Float pClose, Float pVolumn, Float pMoney ) {
        dayTime = p_dayTime;

        index = pDayCount;
        open = pOpen;
        high = pHigh;
        low = pLow;
        close = pClose;
        volumn = pVolumn;
        money = pMoney;
    }
	
  public Node ( int pDayCount, String p_dayTime, Float pOpen, Float pHigh, Float pLow, Float pClose, Float pVolumn, Float pMoney ) {
      dayTime = new Date();
      DateFormat dateFormat = new SimpleDateFormat ("yyyyMMdd" );
      try {
          dayTime = dateFormat.parse( p_dayTime );
      } 
      catch ( Exception e ) {
          e.printStackTrace();
      }
      index = pDayCount;
      open = pOpen;
      high = pHigh;
      low = pLow;
      close = pClose;
      volumn = pVolumn;
      money = pMoney;
  }
  
    public Date getDayTime ( ) {
        return dayTime;
    }
    
    public Float getValue ( String p_flag ) {
        if (p_flag.equals("OPEN")) 
            return open;
        else if ( p_flag.equals( "HIGH") ) 
            return high;
        else if ( p_flag.equals ("LOW") )
            return low;
        else if ( p_flag.equals( "CLOSE"))
            return close;
        else if ( p_flag.equals ("VOLUMN"))
            return volumn;
        else if ( p_flag.equals ("MONEY"))
            return money;
        
        return close;
    }

    public Float getHigh ( ) {
        return high;
    }

    public Float getOpen ( ) {
        return open;
    }

    public Float getLow ( ) {
        return low;
    }

    public Float getClose ( ) {
        return close;
    }

    public Float getVolumn ( ) {
        return volumn;
    }

    public Float getMoney ( ) {
        return money;
    }
    
    public int getIndex () {
      return index;
    }

    public void printNode ( ) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                
        System.out.println ( dateFormat.format(dayTime) );
        System.out.println ( open );
        System.out.println ( high );
        System.out.println ( low );
        System.out.println ( close );
        DecimalFormat df = new DecimalFormat ("0.00");
        System.out.println ( df.format(volumn) );
        System.out.println ( df.format(money) );
    }
}

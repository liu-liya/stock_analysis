package analysis.stock.com;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.text.*;
import java.util.Date;
import	java.util.ArrayList;
import	java.util.List;
import	java.util.LinkedList;
import	java.util.Iterator;

public class SortTimeRange {
    List < Stock > listSortTime;
        
    public SortTimeRange( String p_BeginDate, String p_EndDate, StockList p_StockList ) {
        Date beginDate = new Date();
        Date endDate = new Date();
        
        DateFormat dateFormat = new SimpleDateFormat ("yyyymmdd" );
        try {
            beginDate = dateFormat.parse( p_BeginDate );
            endDate = dateFormat.parse( p_EndDate );
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
    }
}

package analysis.stock.com;

import java.text.DecimalFormat;

import java.util.LinkedList;
import java.util.List;

public class MACD {

    List <Float> vDIF;
    EMA emaDEA;
    List <Float> vMACD;
    //DIF:EMA(CLOSE,SHORT)-EMA(CLOSE,LONG);
    //DEA:EMA(DIF,MID);
    //MACD:(DIF-DEA)*2,COLORSTICK;
    
    public MACD( final List<Float> pListValue, int pShort, int pLong, int pMid ) {
        EMA emaShort;
        EMA emaLong;
        
        vDIF = new LinkedList<Float> ();
        vMACD = new LinkedList<Float> ();
        
        int i;

        emaShort = new EMA ( pListValue, pShort );
        emaLong = new EMA ( pListValue, pLong );

        for ( i = 0; i<pListValue.size(); i++ ) {
            vDIF.add( emaShort.getValue(i) - emaLong.getValue(i) );
        }

        emaDEA = new EMA ( vDIF, pMid );
        
        for ( i = 0;i<pListValue.size(); i++ ) {
            vMACD.add( ( vDIF.get(i) - emaDEA.getValue(i) ) *2);
        }
    }
    
    public void listValue ( ) {
        Float value;
        Float value1;
        DecimalFormat df ; 
        
        for ( int i=0; i < vDIF.size(); i++ ) {
           value=vDIF.get(i);
           df = new DecimalFormat ("0.00");              
           value1=vMACD.get(i); 
           System.out.println ( "dif:"+ df.format(value) + "dea:" + emaDEA.getValue(i) + "macd:" + df.format(value1) );
        }
    }
    
    public Float getValue ( int pIndex , String pFlag ) {
        if ( pFlag.equals( "DIF") ) 
            return vDIF.get(pIndex);
        else if ( pFlag.equals( "DEA") )
            return emaDEA.getValue(pIndex);
        else
            return vMACD.get(pIndex);
    }
}

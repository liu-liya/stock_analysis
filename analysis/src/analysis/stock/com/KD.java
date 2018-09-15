package analysis.stock.com;

import java.util.LinkedList;
import java.util.List;

//RSV:=(CLOSE-LLV(LOW,N))/(HHV(HIGH,N)-LLV(LOW,N))*100;
//K:SMA(RSV,M1,1);
//D:SMA(K,M2,1);
public class KD {
    SMA K;
    SMA D;
    int indexCount;
    
    public KD (final List <Float> pClose, final List <Float> pLLV, final List <Float> pHHV, int pN, int pM1, int pM2 ) { 
        List <Float> lRSV; 
        int i;
        
        lRSV = new LinkedList<Float> ();
        for ( i = 0; i<pClose.size(); i++ ) { 
            lRSV.add( (pClose.get(i) - pLLV.get(i))/(pHHV.get(i)-pLLV.get(i))*100 );
        }
        indexCount = i;
        K=new SMA(lRSV,pM1, 1);
        D=new SMA(K.getList(),pM2,1);
    }
    
    public Float getValue (  int pIndex , String pFlag ) {
        if ( pFlag.equals( "K") ) 
            return K.getValue(pIndex);
        else if ( pFlag.equals( "D") )
            return D.getValue(pIndex); 
        return null;
    }
    
    public int getIndexCount( ) {
      return indexCount;
    }
}

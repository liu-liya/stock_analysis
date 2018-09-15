package analysis.stock.com;

import java.text.*;
import java.util.Date;

public class TranRecord {
    String   tranType;  //S卖 B 买
    int      nodeIndex;
    Float    tranPersent; 
    Float    tranPrice;
    
    public int getNodeIndex() {
        return nodeIndex;        
    } 
    
    public TranRecord () {
        
    }
    
    public Float getTranPrice () {
        return tranPrice;
    }
    //
    
    public Float getTranPersent () {
        return tranPersent;
    }
    
    public String getTranType () {
        return tranType;
    }
    
    public TranRecord ( String pTtranType, int pNodeIndex, Float pTranPersent, Float pTranPrice ) {
        tranType = pTtranType;
        nodeIndex = pNodeIndex;
        tranPersent = pTranPersent;
        tranPrice = pTranPrice;
    } 
}

package analysis.stock.com;

import java.text.DecimalFormat;

import java.util.List;

public class ListValue {    
    List <Float>    vListValue ;
    int             vStep;
    
    public ListValue() {
        super();
    }

    public void listValue ( ) {
            for ( int i=0; i < vListValue.size(); i++ ) {
                    Float value=vListValue.get(i);
                    DecimalFormat df = new DecimalFormat ("0.00");
                    System.out.println ( df.format(value) );
            }
    }

    public Float getValue ( int pIndex ) {
            return vListValue.get(pIndex );
    }

    public int getStep ( ) {
            return vStep;
    }
    
    public void addValue ( Float pValue ) {
        vListValue.add( pValue );
    }
    
    public int getSize () {
        return vListValue.size();
    }
    
    public List <Float> getList ( ) {
        return  vListValue;
    }
}

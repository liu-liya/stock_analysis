package analysis.stock.com;

import	java.util.*;
import 	java.text.DecimalFormat;
import	java.io.*;

public	class MA extends ListValue {
    public MA ( final List<Float> pListValue, int pStep ) {
        vListValue = new LinkedList<Float> ();
        
        if ( pStep > pListValue.size () )
            return;

	for ( int i=pStep - 1; i < pListValue.size(); i++) {
            Float MaVal = new Float(0.0);

            for ( int j=0; j<pStep; j++ ) {
		MaVal += pListValue.get(i-j);
            }
            vListValue.add ( MaVal/pStep );
        }
                
        vStep = pStep;
    }
}
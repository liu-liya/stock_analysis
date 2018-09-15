package analysis.stock.com;

import  java.util.*;
import  java.text.DecimalFormat;
import  java.io.*;

public  class DMA  extends ListValue {
        public DMA ( final List<Float> pListValue, int pStep) {
                vListValue = new LinkedList<Float> ();
		
		for ( int i=0; i<pListValue.size(); i++) { 
			if ( i == 0 ) {
				vListValue.add( pListValue.get(i) );
			}
			else { 
				vListValue.add( pStep*pListValue.get(i)+(1-pStep)*vListValue.get(i-1) );
			}
		}
                vStep = pStep;
	}
}

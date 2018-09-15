package analysis.stock.com;

import  java.util.*;
import  java.text.DecimalFormat;
import  java.io.*;

public  class EMA extends ListValue {

        public EMA ( final List<Float> pListValue, int pStep) {
                vListValue = new LinkedList<Float> ();
		
		for ( int i=0; i<pListValue.size(); i++) { 
			if ( i == 0 ) {
				vListValue.add( pListValue.get(i) );
			}
			else { 
				vListValue.add( (2*pListValue.get(i)+(pStep-1)*vListValue.get(i-1))/(pStep+1) );
			}
		}
                vStep = pStep;
	}
}

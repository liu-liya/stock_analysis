package analysis.stock.com;

import  java.util.*;
import  java.text.DecimalFormat;
import  java.io.*;

public  class SMA extends ListValue {
        public SMA ( final List<Float> pListValue, int pStep, int pWeight ) {
                vListValue = new LinkedList<Float> ();
		
		for ( int i=0; i<pListValue.size(); i++) { 
			if ( i == 0 ) {
				vListValue.add( pListValue.get(i) );
			}
			else { 
				vListValue.add( (pWeight*pListValue.get(i)+(pStep-pWeight)*vListValue.get(i-1))/pStep );
			}
		}
                vStep = pStep;
	}
}

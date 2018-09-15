package analysis.stock.com;


import java.io.*;

public class StandNode {
    public String day;
    public Float  high;
    public Float  low;
    public int    index;
    public int    i;

    public StandNode (  int p_index, Float p_high, Float p_low ) {
	high = p_high;
	low = p_low;

	index = p_index;
    }

    public String getDay ( ) {
		return day;
    }

	public int getIndex ( ) {
		return index;
	}

	public Float getHigh ( ) {
		return high;
	}

	public Float getLow ( ) {
		return low;
	}

	public void setHigh ( Float p_high ) {
		high = p_high;
	}

	public void setLow ( Float p_low ) {
		low = p_low;
	}

	public void setDay ( String p_day ) {
		day = p_day;
	}

	public void printStandDay ( ) {
                System.out.println ( index );
                System.out.println ( day );
                System.out.println ( high );
                System.out.println ( low );
	}
}


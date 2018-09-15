
JAVA  = java
JAVAC = javac

stock : MA.class Node.class Stock.class SMA.class EMA.class App.class

%.class:%.java
	$(JAVAC) -classpath /volumes/app/Oracle/MyWork/Stock/analysis/src $<

run : stock
	$(JAVA) -classpath /volumes/app/Oracle/MyWork/Stock/analysis/src /volumes/app/Oracle/MyWork/Stock/analysis/src/analysis.stock.com.App

clean : 
	rm -f *.class

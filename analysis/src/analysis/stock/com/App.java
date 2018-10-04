package analysis.stock.com;

import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import	java.util.ArrayList;
import  java.util.Date;
import	java.util.List;
import	java.util.LinkedList;
import	java.util.Iterator;
import  analysis.util.com.*;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class App {        
    public static void main( String[] args ) {
        Date lBeginDate = null;
        Date lEndDate = null;
        ComboPooledDataSource dbPool = new ComboPooledDataSource("VIS"); 
        
        DateFormat dateFormat = new SimpleDateFormat ("yyyyMMdd" );
        try {
            lBeginDate = dateFormat.parse( "20000120" );
            lEndDate = dateFormat.parse( "20180520" );            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        // 数据导出格式分隔符�??;�? 日期格式yyyymmdd 无题头信�? 前复�?
      StockList lStockList = new  StockList("D:\\Github\\tmp3\\", 500);
    
      lStockList.initPolicy( lBeginDate, lEndDate );
 
      lStockList.genPolicyStockList( dbPool );
      
      System.out.println( " Sequence : " + ( float )( System.currentTimeMillis() - startTime ) / 1000f + "s" );
        
      //lStockList.initStockPolicy();
      //lStockList.setCycleFlag( "WEEK");
      //同步数据至数据库
      //lStockList.syncStockInfoToDB(dbPool);
      //lStockList.listStock(); 
      //lStockList.tranWeekMACD();
      //lStockList.tranWeekKD(2, lBeginDate, lEndDate, 20.0F);
      //根据kd和macd创建交易记录
      //lStockList.tranWeekKDMACD( lBeginDate, lEndDate, 20.0F);
      //lStockList.printTran();
      //lStockList.syncStockTranToDB( dbPool);
      //计算起止时间段内股票涨跌幅度
      //lStockList.initTranByDate("20151220", "20180525");
//List <Float> listHHV = stock.HHV("CLOSE", 10);

//SMA SMAStep = new SMA ( listValue, 5, 1);
//SMAStep.listValue ( );
//EMA EMAStep = new EMA ( listValue, 5);
////EMAStep.listValue ( );
//      stock.listStockStandDay ( );
//      stock.initInflection ( );
//      stock.listInflection ( );
    }
}

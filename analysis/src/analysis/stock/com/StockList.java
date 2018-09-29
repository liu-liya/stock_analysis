package analysis.stock.com;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import analysis.stock.com.Stock;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date; 

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class StockList extends RecursiveAction {
    int start, end; //并行计算起始位置
    static final int THRESHOLD = 10;  //并行最小串行计算股票数
    
    int stockCount;  //股票数量
    
    int maxPerCount = 4000;
    String locDir;
    List<Stock> stockList;
    //String cycleFlag;
    //交易策略计算起止日期
    Date beginDate = null;
    Date endDate = null; 
    Float policyBuyK;
    
    int policyCnt;
    List<Policy> policyList;

    // add start 
    public StockList(List<Stock> pStockList, List<Policy> pPolicyList, int start, int end, Date pBeginDate, Date pEndDate, Float pBuyK) {
        this.stockList = pStockList;
        this.policyList = pPolicyList;
        this.start = start;
        this.end = end; 
        
        this.beginDate = pBeginDate;
        this.endDate = pEndDate;
        this.policyBuyK = pBuyK;
    }
    
    //
    // 初始化策略清单
    //
    public void initPolicy(Date pBeginDate, Date pEndDate) {
        Policy lPolicy = new Policy(1);
        addPolicy(lPolicy);
        beginDate = pBeginDate;
        endDate = pEndDate;
    }

    //
    // 复制交易策略至股票清单中
    //
    public void initStockPolicy() {
        for (int i = 0; i < stockList.size(); i++) {
            stockList.get(i).initTranPolicy();
        }
    }

    public void addPolicy(Policy pPolicy) {
        policyList.add(pPolicy);
        policyCnt++;
    } 
    
    public void genPolicyStockList(ComboPooledDataSource pDBPool) {
        stockCount = 0;
        stockList = new LinkedList<Stock>();
        File dirFile = new File(locDir);
        int i;

        //获得当前路径下的所有文件和文件夹
        File[] allFiles = dirFile.listFiles();
System.out.println("Begin init StockList.");
        for ( i = 0; i < allFiles.length; i++) {
            //分批处理数据 防止内存溢出
            if (i % maxPerCount != 0 || i == 0) {
                if (allFiles[i].getName()
                               .trim()
                               .toLowerCase()
                               .endsWith(".txt")) {
                    Stock newStock = new Stock();
                    newStock.initStockByFile(locDir + allFiles[i].getName());
                    newStock.initTranPolicy();
                    stockList.add(newStock);
                    stockCount++;
                }
            } else {
System.out.println("list size:" + stockList.size());
                //同步股票信息至数据库
                syncStockInfoToDB(pDBPool);
                //同步股票日线周线信息 
                ////根据kd和macd创建交易记录
                tranWeekKDMACD( beginDate, endDate, 20.0F);
                //同步交易信息至数据库
                syncStockTranToDB(pDBPool);
                stockCount = 0;
                //清空链表+释放内存
                stockList.clear();
                stockList = null;
                stockList = new LinkedList<Stock>();
                //加入当前值
                Stock newStock = new Stock();
                newStock.initStockByFile(locDir + allFiles[i].getName());
                newStock.initTranPolicy(); 
                stockList.add(newStock);
                stockCount++;
            }
        }
System.out.println("End init Stock List from IO File. Begin paralle cacle tran. ");           
        //边界值 最后一个记录正好为allFiles.length
        if ( i%maxPerCount !=0 || i == allFiles.length ) {
            //同步股票信息至数据库
            syncStockInfoToDB(pDBPool);
System.out.println("After sync stockinfo to DB.");
            ////根据kd和macd创建交易记录
            tranWeekKDMACD( beginDate, endDate, 20.0F);
System.out.println("After cacle policy and tran");
            //同步交易信息至数据库
            syncStockTranToDB(pDBPool);
            stockCount = 0;
            //清空链表
            stockList.clear();
            stockList = null;
        }
    }

    @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                // 如果任务足够小,直接计算: 
                String stockNumber = null;
                for (int i = start; i < end; i++) 
                    for (int j = 0; j < policyList.size(); j++)  {
                        stockList.get(i).tranWeekKDMACD(policyList.get(j), beginDate, endDate, policyBuyK); 
                    }
                return;
            }
            // 任务太大,一分为二:
            int middle = (end + start) / 2;
            StockList subtask1 = new StockList(this.stockList, this.policyList, start, middle, beginDate, endDate, policyBuyK ); 
            StockList subtask2 = new StockList(this.stockList, this.policyList, middle, end, beginDate, endDate, policyBuyK ); 
            invokeAll(subtask1, subtask2);
            subtask1.join();
            subtask2.join();
            return ;
        }

    // 计算kdmacd指标计算结果
    public void tranWeekKDMACD(Date pBeginDate, Date pEndDate, Float pBuyK) {
        System.out.println("股票代码" + "|" + "最终收益" + "|" + "平均收益" + "|" + "最低收益" + "|" + "成功率" + "|" + "成功次数" + "|" +
                           "总次数");
        //并行计算股票 交易
        ForkJoinPool fjp = new ForkJoinPool(); // 最大并发数4
        StockList task = new StockList(stockList, policyList, 0, stockList.size(), pBeginDate, pEndDate, pBuyK ); 
        fjp.invoke(task); 
        
        /*
        for (int i = 0; i < stockCount; i++)
            for (int j = 0; j < policyList.size(); j++) {
                stockList.get(i).tranWeekKDMACD(policyList.get(j), pBeginDate, pEndDate, pBuyK);
            }
        */
    }
    /*
    public void tranWeekMACD( int pPolicy, Date pBeginDate, Date pEndDate ) {
        int i;
        int lIndex;
        Float vDIF = new Float(0);
        Float vDEA = new Float(0);
        Float vMACD = new Float(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Float vBenfit;

        for (i = 0; i < getCount(); i++) {
            List<Float> lListValue = getStock(i).getValue(cycleFlag, "CLOSE");

            MACD lMACD = new MACD(lListValue, 12, 26, 9);

            lIndex = getStock(i).getIndexByDate(cycleFlag, pBeginDate);
            if (lIndex != -1) {
                vDIF = lMACD.getValue(lIndex, "DIF");
                vDEA = lMACD.getValue(lIndex, "DEA");
                vMACD = lMACD.getValue(lIndex, "MACD");
                vBenfit = getStock(i).getTotalTranLoss(pPolicy);
                System.out.println(getStockNum(i) + "|" + vDIF + "|" + vDEA + "|" + vMACD + "|" + vBenfit);
            }
        }
    }

    public void tranWeekKD( int pPolicy, Date pBeginDate, Date pEndDate, Float pBuyK) {
        int i, j;
        int lIndex;
        int lStartIndex, lEndIndex;
        //Float lfBuyK = new Float(15.0);


        Float lvK = new Float(0);
        Float lvD = new Float(0);

        String lStatus = "E";
        int lBuyIndex = -1;
        Float lBuyPrice = new Float(0);
        int lSellIndex = -1;
        Float lSellPrice = new Float(0);
        Float lCurPrice = new Float(0);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        System.out.println("股票代码" + "|" + "最终收益" + "|" + "平均收益" + "|" + "最低收益" + "|" + "成功率" + "|" + "成功次数" + "|" +
                           "总次数");
        for (i = 0; i < getCount(); i++) {
            List<Float> lListValue = getStock(i).getValue(cycleFlag, "CLOSE");
            List<Float> lHHV = getStock(i).HHV(cycleFlag, "HIGH", 9);
            List<Float> lLLV = getStock(i).LLV(cycleFlag, "LOW", 9);
            KD lKD = new KD(lListValue, lLLV, lHHV, 9, 3, 3);

            lStartIndex = getStock(i).getIndexByDate(cycleFlag, pBeginDate);
            lEndIndex = getStock(i).getIndexByDate(cycleFlag, pEndDate);

            lStatus = "E"; //初始化空仓状态
            lSellIndex = -1;
            //System.out.println("=================" + getStock(i).getStockNumber() + "=====================");
            //        System.out.println("Start index:" + lStartIndex + "|" + "End index: " + lEndIndex );
            // 初始化交易记录
            for (j = 0; j < lKD.getIndexCount(); j++) {
                lvK = lKD.getValue(j, "K");
                // K<20低位买入后最大跌幅 和 K>20以后价格比
                // 买入信号 而且是空仓
                if (lvK < pBuyK && lStatus == "E") {
                    lBuyIndex = j;
                    lBuyPrice = lListValue.get(j);
                    lSellPrice = lBuyPrice;
                    lStatus = "B";
                    getStock(i).addTranRecord( pPolicy, "B", lBuyIndex, 1.0F, lBuyPrice);
                    //System.out.println( " lBuyIndex:" + lBuyIndex );
                }
                // 到达止损位-10%

            //if ( lStatus == 1 && lvK < lfBuyK ) {
            //   lCurPrice = lListValue.get(j);
                //达到止损位置最大止损深度
            //    if ( lCurPrice <= lBuyPrice*0.9 && lCurPrice < lSellPrice ) {
            //        lSellIndex = j;
            //        lSellPrice = lCurPrice;
            //    }
            //}

                // K>20 结束统计 加入交易记录
                // K已经结束
                if (lStatus == "B" && j - lBuyIndex > 30) { //lvK > lfBuyK ) {
                    if (lSellIndex == -1) {
                        //System.out.println( " buyIndex:" + lBuyIndex + "  sellIndex:"+j);
                        if (lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex)
                            getStock(i).addTranRecord(pPolicy, "S", j, 1.0F, lListValue.get(j));
                    } else {
                        //System.out.println( " buyIndex:" + lBuyIndex + "  sellIndex:"+lSellIndex);
                        if (lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex) {
                            //getStock(i).addTranRecord( pPolicy, lBuyIndex, lSellIndex);
                            getStock(i).addTranRecord(pPolicy, "S", j, 1.0F, lListValue.get(j));
                        }
                        lSellIndex = -1;
                    }
                    lStatus = "E";
                } // 买入信号 而且是空仓

            //if ( lvK > 70 && lStatus == 0 ) {
            //    lBuyIndex = j;
            //    lBuyPrice = lListValue.get(j);
            //    lSellPrice = lBuyPrice;
            //    lStatus = 1;
            //}
            // 到达止损位-10%

            if ( lStatus == 1 && lvK > 70 ) {
                lCurPrice = lListValue.get(j);
                //达到止损位置最大止赢高度
                if ( lCurPrice <= lBuyPrice*1.1 && lCurPrice > lSellPrice ) {
                    lSellIndex = j;
                    lSellPrice = lCurPrice;
                }
            }
            // K>20 结束统计 加入交易记录
            // K已经结束
            //if ( lStatus == 1 && lvK < 70 ) {
            //    if ( lSellIndex == -1 ) {
                    //System.out.println( " buyIndex:" + lBuyIndex + "  sellIndex:"+j);
            //        if ( lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex )
            //            getStock(i).addTranRecord( lBuyIndex, j );
            //    }
            //    else {
                    //System.out.println( " buyIndex:" + lBuyIndex + "  sellIndex:"+lSellIndex);
            //        if ( lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex ) {
            //            getStock(i).addTranRecord( lBuyIndex, lSellIndex );
            //        }
            //        lSellIndex = -1;
            //    }
            //    lStatus = 0;
           // }

           // }
            //打印交易记录
            //getStock(i).printTranRecord( cycleFlag );
            //计算收益率
            getStock(i).caclProfitLoss(pPolicy, cycleFlag);
            //打印收益信息
            //getStock(i).printTranInfo(cycleFlag);
        }
    }
*/

    public void printTran() {
        for (int j = 0; j < policyList.size(); j++) {
            for (int i = 0; i < stockCount; i++)
                stockList.get(i).printTran(policyList.get(j));
        }
    }

    public void syncStockInfoToDB(ComboPooledDataSource pDBPool) {
        Stock curStock;
        for (int i = 0; i < stockCount; i++) {
            curStock = stockList.get(i);
            //已退市股票或一个交易日都没有的 不同步至数据库
            if ( curStock.firstDayTime != null ) {
                curStock.syncInfoToDB(pDBPool);
                curStock.syncNodeToDB(pDBPool, "WEEK");
            }
        }
    }

    public void syncStockTranToDB(ComboPooledDataSource pDBPool) {
        for (int j = 0; j < policyList.size(); j++) {
            for (int i = 0; i < stockCount; i++)
                stockList.get(i).syncTranToDB(policyList.get(j), pDBPool);
        }
    }
    /*
    public void listStock() {
        Stock curStock;

        for (int i = 0; i < stockCount; i++) {
            curStock = stockList.get(i);
            if (cycleFlag.equals("BENIFIT")) {
                //curStock.printStockInfo();
                return;
            }
            else if (cycleFlag.equals("DAY"))
                curStock.listStockDay("DAY");
            else if (cycleFlag.equals("WEEK"))
                curStock.listStockDay("WEEK");
        }
    }
*/ 

    public StockList(String pStockDir, int pMaxPerCount) {
        maxPerCount = pMaxPerCount;
        policyCnt = 0;
        policyList = new LinkedList<Policy>();
        locDir = pStockDir;
    } 
     
    public String getStockNum(int pIndex) {
        return stockList.get(pIndex).stockNumber;
    }

    public int getCount() {
        return stockCount;
    }

    public Stock getStock(int pIndex) {
        return stockList.get(pIndex);
    }
}

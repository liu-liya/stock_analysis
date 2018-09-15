package analysis.stock.com;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.*;

import java.text.DecimalFormat;

import java.text.*;

import java.util.Date;

import analysis.stock.com.*;

import analysis.util.com.*;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.math.BigInteger;

import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import sun.misc.GC;

public class Stock {
    Number stockId; //数据库id标识
    String stockNumber; //股票代码
    static Float startCASH = 100000.00F; //初始资金
    Date firstDayTime; //首日日期
    Date lastDayTime; //末日日期
    int dayCount; //日线总天数
    List<Node> dayNode; //日K线列表
    List<StandNode> standNode; //日K线列表

    int weekCount; //周线总周数
    List<Node> weekNode; //周K线列表
    Date firstWeekTime; //首周日期
    Date lastWeekTime; //末周日期

    List<Policy> tranPolicy;

    public Stock() {
        dayCount = 0;
        weekCount = 0;

        dayNode = new LinkedList<Node>();
        weekNode = new LinkedList<Node>();
        standNode = new LinkedList<StandNode>();

        tranPolicy = null;
    }
    
    public static <T> List<T> deepCopyList(List<T> src)
    {
        List<T> dest = null;
        try
        {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        }
        catch (IOException e)
        {

        }
        catch (ClassNotFoundException e)
        {

        }
        return dest;
    }
    //
    // 此处参数 pPolicyList为传址引用
    // 复制StockList中的 policyList链表.
    //
    public void initTranPolicy( )  {
        tranPolicy = new LinkedList<Policy>();
        Policy lPolicy = new Policy(1);
        tranPolicy.add(lPolicy); 
        /*
        for(int i=0;i<pPolicy.size();i++) 
            tranPolicy.add((Policy)pPolicy.get(i).clone());
       */
        //for (int i = 0; i < pPolicy.size(); i++) {
        //    tranPolicy.add(pPolicy.get(i));
        //}
    }

    //
    // 生成KDMACD指标策略的交易记录
    // 计算策略成功率
    // 买入策略 KD指标中的K < pBuyK && lvMACD <60.0F && K指标出现拐点
    // 卖出策略
    //
    public void tranWeekKDMACD(Policy pPolicy, Date pBeginDate, Date pEndDate, Float pBuyK) {
        int i, j;
        int lStartIndex, lEndIndex; //根据参数日期确定起始 索引位置及结束索引位置
        int lBuySeq = 0;
        int lMaxBuyCnt = 0;
        int lSellSeq = 0;
        int lMaxSellCnt = 0;

        Float lvK = 0.0F;
        Float lvPreK = 0.0F;
        Float lvD = 0.0F;
        Float lvMACD = 0.0F;

        String lStatus = "B"; // 是否买入状态 0 未买入 1已买入
        int lBuyIndex = -1;
        Float lBuyPrice = 0.0F;
        int lSellIndex = -1;

        String lCycleType = pPolicy.getCycleType();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");


        List<Float> lListValue = getValue(lCycleType, "CLOSE");
        List<Float> lHHV = HHV(lCycleType, "HIGH", 9);
        List<Float> lLLV = LLV(lCycleType, "LOW", 9);
        KD lKD = new KD(lListValue, lLLV, lHHV, 9, 3, 3);
        MACD lMACD = new MACD(lListValue, 12, 26, 9);
//for ( int l=0;l<lListValue.size(); l++)
//    System.out.println( "lListValue(" + l + ")=" + lListValue.get(l));
        lStartIndex = getIndexByDate(lCycleType, pBeginDate);
        lEndIndex = getIndexByDate(lCycleType, pEndDate);

        lStatus = "B"; //初始化空仓状态
        lSellIndex = -1;

        //addPolicy(pPolicy); 
//System.out.println("stock Number:" + stockNumber + "begin make tran kd count: " + lKD.getIndexCount());
//System.out.println("lStartIndex="+ lStartIndex + " lEndIndex="+ lEndIndex);
        // 初始化交易记录
        for (j = 0; j < lKD.getIndexCount(); j++) {
            lvPreK = lvK;
            lvK = lKD.getValue(j, "K");
            lvMACD = lMACD.getValue(j, "MACD");
//System.out.println ( "j=" + j + " lvPreK:" + lvPreK + " lvK=" + lvK );
//if (lvK < pBuyK && lvK > lvPreK )
//    System.out.println( "lvMacd=" + lvMACD + " j=" + j + " lStatus=" + lStatus);
            // K<20 且 K>前一天K值
            // 买入信号 而且是空仓 lStatus == "E"
            if (lvK < pBuyK && lvK > lvPreK && lvMACD <= 60.0F && j > lStartIndex && lStatus != "S") {
                lBuyIndex = j; //记录买点位置
                lBuyPrice = lListValue.get(j); //记录买点价格
                addTranRecord(pPolicy, "B", lBuyIndex, getPersent(pPolicy, "B", lBuySeq), lBuyPrice);
//System.out.println( " B:" + " J:" + j + lListValue.get(j));
                lBuySeq++;
                lStatus = "M";
            }

            //达到最大买入次数 改变改变交易状态 只能执行卖出操作
            if (lBuySeq == pPolicy.getMaxBuyCnt()) {
                lBuySeq = 0;
                lSellSeq = 0;
                lStatus = "S";
            }

            //当前已是买入状态
            if (lStatus != "B" && (j - lBuyIndex > 30 || j - lBuyIndex > 40 || j - lBuyIndex > 50)) { //lvK > lfBuyK ) {
                //截至到交易结束日期从未执行卖出操作
                if (lSellIndex == -1) {
                    if (lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex) {
                        addTranRecord(pPolicy, "S", j, getPersent(pPolicy, "S", lSellSeq), lListValue.get(j));
//System.out.println( " S:" + " J:" + j + lListValue.get(j));                        
                        lSellSeq++;
                        lStatus = "M";
                        lSellIndex = j;
                    }
                } else {
                    if (lBuyIndex >= lStartIndex && lBuyIndex < lEndIndex && j > lStartIndex && j <= lEndIndex) {
                        addTranRecord(pPolicy, "S", j, getPersent(pPolicy, "S", lSellSeq), lListValue.get(j));
//System.out.println( " S:" + " J:" + j + lListValue.get(j));                         
                        lSellSeq++;
                        lSellIndex = j;
                        lStatus = "M";
                    }
                    lSellIndex = -1;
                }

                // 已达最大卖出次数 已空仓 切换状态为买入状态
                if (lSellSeq == pPolicy.getMaxSellCnt()) {
                    lSellSeq = 0;
                    lBuySeq = 0;
                    lStatus = "B";
                }
            } // 买入信号 而且是空仓
            //计算收益率

        }
      
        caclProfitLoss(pPolicy, lCycleType);
        //打印交易记录
        //printTranRecord(lCycleType);
        //打印收益信息
        printTranInfo(pPolicy, lCycleType);
        
        //清空节点信息 + 释放内存
        dayNode.clear();
        weekNode.clear();
        
        dayNode = null;
        weekNode = null;
        //GC.Collect();  
    }

    public String getStockNumber() {
        return stockNumber;
    }

    //
    // 根据文件创建日线、周线列表
    //
    public void initStockByFile(String fileName) {
        int fileNameLen = fileName.length();
        int curWeekDay;
        Node newNode = new Node();
        Node newWeekNode = new Node();

        DateUtil dateUtil = new DateUtil();
        Date weekFirstDay;
        Date weekLastDay;
        Float weekOpen;
        Float weekHigh;
        Float weekLow;
        Float weekClose;
        Float weekVolumn;
        Float weekMoney;

        stockNumber = fileName.substring(fileNameLen - 10, fileNameLen - 4);

        File file = new File(fileName);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            // read file until null for the end
            while ((tempString = reader.readLine()) != null && !tempString.equals(new String("数据来源:通达信"))) {
                // echo the line number
                String strColumn[] = tempString.split(";");
                String Title = strColumn[0];
                Float Open = Float.valueOf(strColumn[1]);
                Float High = Float.valueOf(strColumn[2]);
                Float Low = Float.valueOf(strColumn[3]);
                Float Close = Float.valueOf(strColumn[4]);
                Float Volumn = Float.valueOf(strColumn[5]);
                Float Money = Float.valueOf(strColumn[6]);

                newNode = new Node(dayCount, Title, Open, High, Low, Close, Volumn, Money);

                dayNode.add(newNode);
                if (dayCount == 0)
                    firstDayTime = newNode.getDayTime();
                dayCount++;
            }
            lastDayTime = newNode.getDayTime();
            reader.close();

            if (dayCount > 0) {
                Node curNode;
                weekFirstDay = dayNode.get(0).getDayTime();
                weekLastDay = dateUtil.getLastDayOfWeek(weekFirstDay);

                weekOpen = dayNode.get(0).getOpen();
                weekClose = dayNode.get(0).getClose();
                weekHigh = dayNode.get(0).getHigh();
                weekLow = dayNode.get(0).getLow();
                weekVolumn = dayNode.get(0).getVolumn();
                weekMoney = dayNode.get(0).getMoney();
                firstWeekTime = dayNode.get(0).getDayTime();

                curWeekDay = 1;

                for (int i = 0; i < dayNode.size(); i++) {
                    curNode = dayNode.get(i);
                    // 在同一周
                    if (curNode.getDayTime().compareTo(weekFirstDay) > 0 &&
                        (curNode.getDayTime().compareTo(weekLastDay) == 0 ||
                         curNode.getDayTime().compareTo(weekLastDay) < 0)) {
                        if (curNode.getHigh() > weekHigh)
                            weekHigh = curNode.getHigh();
                        if (curNode.getLow() < weekLow)
                            weekLow = curNode.getLow();
                        weekClose = curNode.getClose();
                        weekVolumn += curNode.getVolumn();
                        weekMoney += curNode.getMoney();

                        curWeekDay++;
                    }
                    // 下周第一天
                    else if (curNode.getDayTime().compareTo(weekLastDay) > 0) {
                        newWeekNode =
                            new Node(weekCount, weekLastDay, weekOpen, weekHigh, weekLow, weekClose, weekVolumn,
                                     weekMoney);
                        weekNode.add(newWeekNode);
                        weekCount++;

                        weekFirstDay = curNode.getDayTime();
                        weekLastDay = dateUtil.getLastDayOfWeek(weekFirstDay);

                        weekOpen = curNode.getOpen();
                        weekClose = curNode.getClose();
                        weekHigh = curNode.getHigh();
                        weekLow = curNode.getLow();
                        weekVolumn = curNode.getVolumn();
                        weekMoney = curNode.getMoney();
                        curWeekDay = 1;
                    }
                }
                //最后一组循环后  无下周的数据
                newWeekNode =
                    new Node(weekCount, weekLastDay, weekOpen, weekHigh, weekLow, weekClose, weekVolumn, weekMoney);
                lastWeekTime = weekFirstDay;
                weekNode.add(newWeekNode);
                weekCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public Float getPersent(Policy pPolicy, String pTranType, int pTranIndex) {
        for (int i = 0; i < tranPolicy.size(); i++) {
            if (tranPolicy.get(i).getPolicyId() == pPolicy.getPolicyId()) {
                if (pTranType == "B")
                    return tranPolicy.get(i).getBuyPersent(pTranIndex);
                else if (pTranType == "S")
                    return tranPolicy.get(i).getSellPersent(pTranIndex);
            }
        }
        return 1.00F;
    }

    public Date getFirstDay() {
        return firstDayTime;
    }

    public void syncInfoToDB(ComboPooledDataSource pDBPool) {

        QueryRunner qr = new QueryRunner(pDBPool);
        //System.out.println("URL:" + pool.getJdbcUrl());
        try {
            //执行SQL语句
            java.sql.Date lFirstDay = new java.sql.Date(firstDayTime.getTime());
            qr.update("insert into cux_stocks(stock_id,market_name, stock_num, start_date,enable_flag ) values(CUX_SEQ_STOCK_ID.nextval,?, ?, ?,?)",
                      new Object[] { "SH", stockNumber, lFirstDay, "Y" });
            //stockId = (Number) qr.query("select CUX_SEQ_STOCK_ID.currval from dual", new ScalarHandler());
            stockId =
                (Number) qr.query("select stock_id from cux_stocks where stock_num = '" + stockNumber + "'",
                                  new ScalarHandler());
            //System.out.println("StockID:" + stockId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return;
    }

    public void syncNodeToDB(ComboPooledDataSource pDBPool, String pCycle) {
        QueryRunner qr = new QueryRunner(pDBPool);
        Node curNode;

        if (pCycle == "DAY") {
            for (int i = 0; i < dayNode.size(); i++) {
                curNode = dayNode.get(i);
                try {
                    //执行SQL语句
                    java.sql.Date lCurDay = new java.sql.Date(curNode.getDayTime().getTime());
                    qr.update("insert into cux_stock_node(stock_id, cycle_type, day_index, day_date, open_price, high_price, low_price, close_price, volumn, vol_money ) values( ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                              new Object[] { stockId, pCycle, curNode.getIndex(), lCurDay, curNode.getOpen(),
                                             curNode.getHigh(), curNode.getLow(), curNode.getClose(),
                                             curNode.getVolumn(), curNode.getMoney() });
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        } else if (pCycle == "WEEK") {
            for (int i = 0; i < weekNode.size(); i++) {
                curNode = weekNode.get(i);
                try {
                    //执行SQL语句
                    java.sql.Date lCurDay = new java.sql.Date(curNode.getDayTime().getTime());
                    qr.update("insert into cux_stock_node(stock_id, cycle_type, day_index, day_date, open_price, high_price, low_price, close_price, volumn, vol_money ) values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                              new Object[] { stockId, pCycle, curNode.getIndex(), lCurDay, curNode.getOpen(),
                                             curNode.getHigh(), curNode.getLow(), curNode.getClose(),
                                             curNode.getVolumn(), curNode.getMoney() });
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return;
    }
    
    public void printTran( Policy  pPolicy) {
        Policy currPolicy;
        for (int policyIndex = 0; policyIndex < tranPolicy.size(); policyIndex++) {
            if (tranPolicy.get(policyIndex).getPolicyId() == pPolicy.getPolicyId()) {
                currPolicy = tranPolicy.get(policyIndex);
                for (int i = 0; i < currPolicy.getTranCount(); i++) {
                    TranRecord lTranRecord = currPolicy.getTranRecord(i);
                }
            }
        }
    }

    public void syncTranToDB(Policy pPolicy, ComboPooledDataSource pDBPool) {
        Node lBuyNode = null, lSellNode = null;
        QueryRunner qr = new QueryRunner(pDBPool);
        Policy currPolicy;

        for (int policyIndex = 0; policyIndex < tranPolicy.size(); policyIndex++) {
            if (tranPolicy.get(policyIndex).getPolicyId() == pPolicy.getPolicyId()) {
                currPolicy = tranPolicy.get(policyIndex);
                for (int i = 0; i < currPolicy.getTranCount(); i++) {
                    TranRecord lTranRecord = currPolicy.getTranRecord(i);
                    try {
                        //执行SQL语句
                        //java.sql.Date lCurDay = new java.sql.Date(curNode.getDayTime().getTime());
                        qr.update("insert into cux_stock_tran( stock_id,  policy_id , cycle_type, tran_type, node_index, tran_price, tran_present  ) values( ?, 1, ?, ?, ?, ?, ? )",
                                  new Object[] { stockId, pPolicy.getCycleType(), lTranRecord.getTranType(),
                                                 lTranRecord.getNodeIndex(), lTranRecord.getTranPrice(),
                                                 lTranRecord.getTranPersent() });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void listStockDay(String p_Flag) {
        Node curNode;

        if (p_Flag.equals("DAY")) {
            for (int i = 0; i < dayNode.size(); i++) {
                curNode = dayNode.get(i);
                curNode.printNode();
            }
        } else if (p_Flag.equals("WEEK")) {
            for (int i = 0; i < weekNode.size(); i++) {
                curNode = weekNode.get(i);
                curNode.printNode();
            }
        }
    }

    public List<Float> getValue(String pCycle, String pFlag) {
        List<Float> listValue = new LinkedList();
        Iterator<Node> i = null;

        if (pCycle.equals("DAY"))
            i = dayNode.iterator();
        else if (pCycle.equals("WEEK"))
            i = weekNode.iterator();

        while (i.hasNext()) {
            Node curNode = i.next();

            listValue.add(curNode.getValue(pFlag));
        }
        return listValue;
    }

    public void initStandNode() {
        StandNode curStandNode;
        Float lHigh = null, lLow = null;
        String lDirect = "UP";

        Node curNode = null, preNode = null;
        for (int i = 0; i < dayNode.size(); i++) {
            if (i > 1) {
                preNode = curNode;
                curNode = dayNode.get(i);
                if (lDirect == "UP") {
                    if (curNode.getHigh() >= preNode.getHigh()) {
                        lHigh = curNode.getHigh();
                        if (curNode.getLow() >= preNode.getLow())
                            lLow = curNode.getLow();
                        else
                            lLow = preNode.getLow();
                    } else {
                        lHigh = preNode.getHigh();
                        if (curNode.getLow() <= preNode.getLow())
                            lLow = curNode.getLow();
                        else
                            lLow = preNode.getLow();
                    }
                } else {

                }
            } else {
                curNode = dayNode.get(i);
                lHigh = curNode.getHigh();
                lLow = curNode.getLow();
            }
            StandNode newStandNode = new StandNode(i, lHigh, lLow);
            standNode.add(newStandNode);
        }
    }

    public void MA(String pCycle, String pFLAG, int pStep) {
        List<Float> listValue = getValue(pCycle, pFLAG);

        MA MAStep = new MA(listValue, 10);
        //MAStep.listMA ( );
    }

    public List<Float> LLV(String pCycle, String pFlag, int pStep) {
        List<Float> listLow = getValue(pCycle, pFlag);
        List<Float> listLLV = new LinkedList();
        Float lowValue;
        Float curValue;

        for (int i = 0; i < listLow.size(); i++) {
            curValue = listLow.get(i);

            for (int j = 0; j < pStep && i - j >= 0; j++) {
                lowValue = listLow.get(i - j);
                if (lowValue < curValue)
                    curValue = lowValue;
            }
            listLLV.add(curValue);
        }
        return listLLV;
    }

    public List<Float> HHV(String pCycle, String pFlag, int pStep) {
        List<Float> listHigh = getValue(pCycle, pFlag);
        List<Float> listHHV;
        Float hisValue;
        Float curValue;

        listHHV = new LinkedList();
        for (int i = 0; i < listHigh.size(); i++) {
            curValue = listHigh.get(i);
            for (int j = 0; j < pStep && i - j >= 0; j++) {
                hisValue = listHigh.get(i - j);
                if (hisValue > curValue)
                    curValue = hisValue;
            }
            listHHV.add(curValue);
        }
        return listHHV;
    }

    public void addPolicy(Policy pPloicy) {
        tranPolicy.add(pPloicy);
    }

    public void addTranRecord(Policy pPolicy, String pTranFlag, int pNodeIndex, Float pTranPersent, Float pNodePrice) {
        TranRecord lTranRecord = new TranRecord(pTranFlag, pNodeIndex, pTranPersent, pNodePrice);
        for (int i = 0; i < tranPolicy.size(); i++) {
            if (tranPolicy.get(i).getPolicyId() == pPolicy.getPolicyId()) {
                tranPolicy.get(i).addTranRecord(lTranRecord);
                return;
            }
        }

    }

    public void printTranInfo(Policy pPolicy, String pCycle) {
        for (int i = 0; i < tranPolicy.size(); i++) {
            if (tranPolicy.get(i).getPolicyId() == pPolicy.getPolicyId())
                System.out.println(stockNumber + "|" + tranPolicy.get(i).getTotalLoss() + "|" + 1 + "|" + 1 + "|" + 1 +
                                   "|" + 1 + "|" + 1);
        }

    }

    /*
    public float caclTranLoss(int pPolicy, String pCycle, int pIndex) {
        float lTranLoss = 0;
        Node lBuyNode = null, lSellNode = null;
        Policy currPolicy;

        for (int i = 0; i < tranPolicy.size(); i++) {
            if (tranPolicy.get(i).getPolicyId() == pPolicy) {
                currPolicy = tranPolicy.get(i);
                if (pCycle.equals("DAY")) {
                    lBuyNode = dayNode.get(currPolicy.getTranRecord(pIndex).getBuyIndex());
                    lSellNode = dayNode.get(currPolicy.getTranRecord(pIndex).getSellIndex());
                    lTranLoss = (lSellNode.getClose() - lBuyNode.getClose()) / Math.abs(lBuyNode.getClose());
                } else if (pCycle.equals("WEEK")) {
                    lBuyNode = weekNode.get(currPolicy.getTranRecord(pIndex).getBuyIndex());
                    lSellNode = weekNode.get(currPolicy.getTranRecord(pIndex).getSellIndex());
                    lTranLoss = (lSellNode.getClose() - lBuyNode.getClose()) / Math.abs(lBuyNode.getClose());
                }
            }
        }
        return lTranLoss;
    }
    */
    public void caclProfitLoss(Policy pPolicy, String pCycle) {
        float lTranLoss;
        Policy currPolicy = null;
        int i;
        
        Float cashTotal = startCASH; //起始现金比例
        Float stockTotal = 0.00F; //起始股票数量
        TranRecord currTranRecord = null;
        for (int j = 0; j < tranPolicy.size(); j++) {
            if (tranPolicy.get(j).getPolicyId() == pPolicy.getPolicyId()) {
                currPolicy = tranPolicy.get(j);
                for (i = 0; i < currPolicy.getTranCount(); i++) { 
                    currTranRecord = currPolicy.getTranRecord(i);
                    if (currTranRecord.getTranType() == "B") {
                        stockTotal =
                            stockTotal + cashTotal * currTranRecord.getTranPersent() / currTranRecord.getTranPrice();
                        cashTotal = cashTotal - cashTotal * currTranRecord.getTranPersent();
                        //System.out.println( "tran index:"+ i + "buy persent:" + currTranRecord.getTranPersent() + " price:" + currTranRecord.getTranPrice() + " cashTotal:" + cashTotal + " stockTotal:" + stockTotal);
                    } else if (currTranRecord.getTranType() == "S") {
                        cashTotal =
                            cashTotal + stockTotal * currTranRecord.getTranPersent() * currTranRecord.getTranPrice();
                        stockTotal = stockTotal - stockTotal * currTranRecord.getTranPersent();
                        //System.out.println( "tran index:"+ i + "sell persent:" + currTranRecord.getTranPersent() + " price:" + currTranRecord.getTranPrice() + " cashTotal:" + cashTotal + " stockTotal:" + stockTotal);
                    }
                }
                if ( i != 0)
                currPolicy.setStatisticsResult(1, 1, 1,
                                               (cashTotal + stockTotal * currTranRecord.getTranPrice()) / startCASH, 1,
                                               1);
            }
        }
    }

    public Node getNode(String pCycle, int pIndex) {
        if (pCycle.equals("DAY"))
            return dayNode.get(pIndex);
        else if (pCycle.equals("WEEK"))
            return weekNode.get(pIndex);

        return null;
    }
    // 第一天>= 参数日期 返回第一天的记录
    // 最后一天<= 参数日期 返回最后一天的记录
    // 其他情况返回比参数日期大的 最近交易记录
    public Node getNode(Date pDate) {
        Node curNode = null;

        if (dayCount > 0) {
            try {
                if (firstDayTime.compareTo(pDate) >= 0) {
                    return dayNode.get(0);
                } else if (lastDayTime.compareTo(pDate) <= 0) {
                    return dayNode.get(dayNode.size() - 1);
                } else if (firstDayTime.compareTo(pDate) < 0 && lastDayTime.compareTo(pDate) > 0) {
                    for (int i = 0; i < dayNode.size(); i++) {
                        curNode = dayNode.get(i);
                        if (curNode.getDayTime().compareTo(pDate) >= 0) {
                            curNode = dayNode.get(i - 1);
                            break;
                        }
                    }
                    return curNode;
                }
            } catch (Exception e) {
                System.out.println(stockNumber);
            }
        } else
            return null;
        return curNode;
    }

    public int getIndexByDate(String pCycle, Date pDate) {
        Node curNode = null;
        int i;

        if (pCycle.equals("DAY")) {
            if (dayCount > 0) {
                if (firstDayTime.compareTo(pDate) > 0)
                    return 0;
                else if (lastDayTime.compareTo(pDate) < 0)
                    return dayCount - 1;
                else if (firstDayTime.compareTo(pDate) <= 0 && lastDayTime.compareTo(pDate) >= 0) {
                    for (i = 0; i < dayNode.size(); i++) {
                        curNode = dayNode.get(i);
                        if (curNode.getDayTime().compareTo(pDate) > 0)
                            return i - 1;
                    }
                } else
                    return -1;
            }
        } else if (pCycle.equals("WEEK")) {
            if (weekCount > 0)
                if (firstWeekTime.compareTo(pDate) > 0)
                    return 0;
                else if (lastWeekTime.compareTo(pDate) < 0)
                    return weekCount - 1;
                else if (firstWeekTime.compareTo(pDate) <= 0 && lastWeekTime.compareTo(pDate) >= 0) {
                    for (i = 0; i < weekNode.size(); i++) {
                        curNode = weekNode.get(i);
                        if (curNode.getDayTime().compareTo(pDate) > 0)
                            return i - 1;
                    }
                } else
                    return -1;
        }
        return -1;
    }

    // 按日期初始化交易记录
    // 买的日期、卖的日期
    /*
    public void initTranRecordByDate(Number pPolicy, String pBeginDate, String pEndDate) {
        Date lBeginDate = null;
        Date lEndDate = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            lBeginDate = dateFormat.parse(pBeginDate);
            lEndDate = dateFormat.parse(pEndDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int buyIndex = getIndexByDate("DAY", lBeginDate);
        int sellIndex = getIndexByDate("DAY", lEndDate);

        if (buyIndex != -1 && sellIndex != -1 && buyIndex != sellIndex) {
            TranRecord tranRecord = new TranRecord( buyIndex, sellIndex);
            tranList.add(tranRecord);
            tranCount++;
        }
    }
    */

    public Float getTotalTranLoss(int pPolicyIndex) {
        for (int i = 0; i < tranPolicy.size(); i++) {
            if (tranPolicy.get(i).getPolicyId() == pPolicyIndex)
                return tranPolicy.get(i).getTotalLoss();
        }
        return 0.0F;
    }
}

package analysis.stock.com;

import java.util.LinkedList;
import java.util.List;

public class Policy implements Cloneable {
    int policyId;       //策略标识
    String policyName;  //策略名称
    String cycleType;   //策略评估周期 DAY 日线 WEEK周线
    
    int maxBuyCnt;      //最大买次数
    Float buyPersent[]; //每次买入占当前现金的比例
    int maxSellCnt;     //最大卖次数
    Float sellPersent[];    //每次卖出占当前股票数量比例 
    
    float maxTranLoss; //最大收益率
    float avgTranLoss; //平均收益率 算术计算收益
    float minTranLoss; //最小收益率
    float totalTranLoss; //累积收益 连续计算收益
    int successTran; //盈利次数
    int loseTran; //亏损次数    
    
    int tranCount; //交易次数
    List<TranRecord> tranList; //交易记录
    
    public int getMaxSellCnt () {
        return maxSellCnt;
    }
    
    public Object clone() {   
            Policy o = null;   
            try {   
                o = (Policy) super.clone();   
            } catch (CloneNotSupportedException e) {   
                e.printStackTrace();   
            }   
            return o;   
        }   
    
    public int getMaxBuyCnt () {
        return maxBuyCnt;
    }    
         
    public Policy( int pPolicy) {
        policyId = pPolicy; 
        totalTranLoss = 1;
        tranCount = 0;
        tranList = new LinkedList<TranRecord>();
        maxBuyCnt = 1;
        buyPersent = new Float[] { 1.0F, 0.7F, 1.0F };
        
        maxSellCnt = 1;
        sellPersent = new Float[] { 1.0F, 0.5F, 1.0F };
        
        cycleType = "WEEK"; 
    }
    
    public Float getBuyPersent ( int pIndex ) {
        if ( pIndex < maxBuyCnt )
            return buyPersent[pIndex];
        return 1.0F;
        
    }
    
    public String getCycleType () {
        return cycleType;
    }
    
    public Float getSellPersent ( int pIndex ) {
        if ( pIndex < maxSellCnt )
            return sellPersent[pIndex];
        return 1.0F;
    }
    
    public void setBuyInfo (int pMaxBuyCnt, Float pBuyPersent[]) {
        maxBuyCnt = pMaxBuyCnt;
        buyPersent = pBuyPersent;
    }
    
    public void setSellInfo ( int pMaxSellCnt, Float pSellPersent[]) {
        maxSellCnt = pMaxSellCnt;
        sellPersent = pSellPersent;
    }
    
    public int getPolicyId () {
        return policyId;
    }
    
    public int getTranCount (){
        return tranCount;
    } 
    
    public int getTranListCnt () {
        return tranList.size();
    }
    
    public TranRecord getTranRecord ( int tranIndex ) {
        return tranList.get(tranIndex);
    }
    
    public void addTranRecord( TranRecord pTranRecord ) { 
        tranList.add(pTranRecord);
        tranCount++;
    }
    
    public void setStatisticsResult ( float pMaxTranLoss, float pAvgTranLoss, float pMinTranLoss, float pTotalTranLoss, int pSuccessTran, int pLoseTran) {
        maxTranLoss =  pMaxTranLoss;
        avgTranLoss = pAvgTranLoss;
        minTranLoss = pMinTranLoss;
        totalTranLoss = pTotalTranLoss;
        successTran = pSuccessTran;
        loseTran = pLoseTran;
    }
    
    public float getTotalLoss () {
        return totalTranLoss;
    }
}

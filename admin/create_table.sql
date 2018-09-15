create user demantra identified by demantra;
grant dba to demantra;

create table demantra.cux_stocks ( stock_id number ,  --标识
                                       market_name varchar2(10) , --市场名
                                       stock_num varchar2(50) ,  --编号
                                       stock_name varchar2(200),  --名称
                                       start_date date,           --上市日期
                                       end_date date,             --退市日期
                                       enable_flag varchar2(2),   --启用
                                       industry varchar2(20), --行业
                                       capitalization number, --总股本
                                       tradable number,       --流通股
                                       creation_date date,
                                       created_by number,
                                       last_updated_by number,
                                       last_update_date date,
                                       last_update_login number,
                                       attribute_category varchar2(50),
                                       attribute1 varchar2(150),
                                       attribute2 varchar2(150),
                                       attribute3 varchar2(150),
                                       attribute4 varchar2(150),
                                       attribute5 varchar2(150) );
create public synonym cux_stocks for demantra.cux_stocks; 

create sequence demantra.cux_seq_stock_id START WITH 1;
create public synonym cux_seq_stock_id for demantra.cux_seq_stock_id; 

-- drop synonym cux_day_node;

--drop table demantra.cux_stock_node ;
create table demantra.cux_stock_node ( stock_id number,  --股票id
                                     cycle_type varchar2(120), --日线类型 DAY WEEK
                                     day_index number,  --日线索引
                                     day_date date, --日线日期
                                     open_price  number, --开盘价
                                     high_price number,  --最高价
                                     low_price number,  --最低价
                                     close_price number,  --收盘价
                                     volumn      number,  --成交量
                                     vol_money   number,
                                     creation_date date,
                                       created_by number,
                                       last_updated_by number,
                                       last_update_date date,
                                       last_update_login number,
                                       attribute_category varchar2(50),
                                       attribute1 varchar2(150),
                                       attribute2 varchar2(150),
                                       attribute3 varchar2(150),
                                       attribute4 varchar2(150),
                                       attribute5 varchar2(150)
                                     ); --成交额
                                        
create public synonym cux_stock_node for demantra.cux_stock_node; 

--drop table demantra.cux_stock_tran;
create table demantra.cux_stock_tran ( stock_id   number,
                                 policy_id  number,
                                 cycle_type varchar(20),
                                 tran_type  varchar2(3),  --'B' 买 'S' 卖
                                 node_index  number,
                                 tran_price  number,
                                 tran_present number,
                                 creation_date date,
                                       created_by number,
                                       last_updated_by number,
                                       last_update_date date,
                                       last_update_login number,
                                       attribute_category varchar2(50),
                                       attribute1 varchar2(150),
                                       attribute2 varchar2(150),
                                       attribute3 varchar2(150),
                                       attribute4 varchar2(150),
                                       attribute5 varchar2(150) 
                               );
create public synonym cux_stock_tran for demantra.cux_stock_tran;                               

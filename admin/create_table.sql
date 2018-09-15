create user demantra identified by demantra;
grant dba to demantra;

create table demantra.cux_stocks ( stock_id number ,  --��ʶ
                                       market_name varchar2(10) , --�г���
                                       stock_num varchar2(50) ,  --���
                                       stock_name varchar2(200),  --����
                                       start_date date,           --��������
                                       end_date date,             --��������
                                       enable_flag varchar2(2),   --����
                                       industry varchar2(20), --��ҵ
                                       capitalization number, --�ܹɱ�
                                       tradable number,       --��ͨ��
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
create table demantra.cux_stock_node ( stock_id number,  --��Ʊid
                                     cycle_type varchar2(120), --�������� DAY WEEK
                                     day_index number,  --��������
                                     day_date date, --��������
                                     open_price  number, --���̼�
                                     high_price number,  --��߼�
                                     low_price number,  --��ͼ�
                                     close_price number,  --���̼�
                                     volumn      number,  --�ɽ���
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
                                     ); --�ɽ���
                                        
create public synonym cux_stock_node for demantra.cux_stock_node; 

--drop table demantra.cux_stock_tran;
create table demantra.cux_stock_tran ( stock_id   number,
                                 policy_id  number,
                                 cycle_type varchar(20),
                                 tran_type  varchar2(3),  --'B' �� 'S' ��
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

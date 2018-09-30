truncate table DEMANTRA.CUX_STOCK_NODE;
truncate table demantra.cux_stocks;
truncate table demantra.cux_stock_tran;

begin
select count(1) from cux_stocks  ;
select count(1) from CUX_STOCK_NODE csn ; 
select count(1) from cux_stock_tran;
end;

select * from cux_stocks;

select market_name, stock_num, count(1) from cux_stocks 
group by market_name, stock_num
having count(1) > 1

order by stock_num 

where csn.stock_id = 11003;
select count(1) from CUX_STOCK_NODE csn where csn.stock_id = 72 and csn.day_index = 139 order by day_index;
select * from cux_stock_tran;
select CUX_SEQ_STOCK_ID.currval from dual

select cst.stock_num,
       cstr.cycle_type,
       cstr.tran_type,
       cstr.tran_present,
       csn.day_index,
       csn.day_date,
       cstr.tran_price,
       cstr.*
  from cux_stock_tran cstr,
       cux_stocks cst,
       CUX_STOCK_NODE csn
where cstr.stock_id =  cst.stock_id
  and csn.stock_id = cstr.stock_id
  and csn.cycle_type = cstr.cycle_type
  and csn.day_index = cstr.node_index
  and stock_num = '002550'
order by cst.stock_id, day_index

package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;



public class Receipt{

    final static long serialVersionUID = 800L;
    boolean debug = false;
    static Logger logger = LogManager.getLogger(Receipt.class);
	
    String bid = "", receipt_no="";
    String rec_date = "",rec_from = "", check_no = "", paid=""; // pay method
    double rec_sum = 0.;
	
    Bill bill = null;
	
    public Receipt(boolean val){
	debug = val;
    }
    public Receipt(boolean val, String val2){
	debug = val;
	setRid(val2);
    }
    public Receipt(boolean deb,
	    String val,
	    String val2,
	    String val3,
	    String val4,
	    String val5,
	    String val6,
	    String val7
	    ){
	debug = deb;
	setRid(val);
	setBid(val2);
	setRec_sum(val3);
	setRec_date(val4);
	setRec_from(val5);
	setPaidMethod(val6);
	setCheck_no(val7);
    }	
    public void setBid(String val){
	if(val != null)
	    bid = val;
    }
    public void setBill(Bill val){
	if(val != null){
	    bill = val;
	    bid = bill.getBid();
	}
    }	
    public void setRid(String val){
	if(val != null)
	    receipt_no = val;
    }
    public void setReceipt_no(String val){
	if(val != null)
	    receipt_no = val;
    }	
    public void setRec_date(String val){
	if(val != null)
	    rec_date = val;
    }
    public void setRec_from(String val){
	if(val != null)
	    rec_from = val;
    }	
    public void setCheck_no(String val){
	if(val != null)
	    check_no = val;
    }
    public void setPaidMethod(String val){
	if(val != null)
	    paid = val;
    }	
    public void setRec_sum(String val){
	if(val != null){
	    try{
		rec_sum = Double.parseDouble(val);
	    }catch(Exception ex){};
	}
    }
    //
    public String getBid(){
	return bid;
    }
    public String getRid(){
	return receipt_no;
    }
    public String getReceipt_no(){
	return receipt_no;
    }	
    public String getRec_date(){
	return rec_date;
    }
    public String getRec_from(){
	return rec_from;
    }	
    public String getCheck_no(){
	return check_no;
    }
    public String getPaidMethod(){
	return paid ;
    }	
    public double getRec_sum(){
	return rec_sum;
    }
    public Bill getBill(){
	if(bill == null && !bid.equals("")){
	    bill = new Bill(debug, bid);
	    String back = bill.doSelect();
	    if(!back.equals("")){
		logger.error(back);
	    }
	}
	return bill;
    }
    public String doSave(){

	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
		
	String back = "";
	double balance = 0, old_balance = 0;
	if(bid.equals("")){
	    back = "Bill id not set ";
	    return back;
	}
	else if(bill == null){
	    bill = new Bill(debug, bid);
	    back = bill.doSelect();
	}
	if(bill != null){
	    old_balance = bill.getBalance();
	}

	if(rec_sum > 0 && old_balance > 0){
	    balance = old_balance - rec_sum;
	    if(balance < 0) balance = 0;
	    String str = "", qq = "";
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    try{
		//
		// make a receipt number 
		//
		qq = " select reg_receipt_seq.nextval from dual";
		if(debug){
		    logger.debug(qq);
		}
		stmt2 = con.prepareStatement(qq);					
		rs = stmt2.executeQuery();
		if(rs.next())
		    receipt_no = rs.getString(1);
		qq = "insert into reg_paid values(?,?,";
		if(rec_date.equals(""))
		    qq += "null,";
		else
		    qq += "to_date('"+rec_date+"','mm/dd/yyyy'),";
		qq += "?,?,?,?)";
		if(debug){
		    logger.debug(qq);
		}
		stmt = con.prepareStatement(qq);
		stmt.setString(1, bid);
		stmt.setDouble(2, rec_sum);
		if(rec_from.equals(""))
		    stmt.setNull(3, Types.VARCHAR);
		else
		    stmt.setString(3, rec_from);
		if(paid.equals(""))
		    stmt.setNull(4, Types.VARCHAR);
		else
		    stmt.setString(4, paid);	
		if(check_no.equals(""))
		    stmt.setNull(5, Types.VARCHAR);
		else
		    stmt.setString(5, check_no);
		stmt.setString(6, receipt_no);
		stmt.executeUpdate();
		//
		// change the status to paid only if the sum
		// paid equals the balance
		//
		if(balance == 0){ // paid totally 
		    bill.setStatus("Paid");
		    back += bill.doUpdate();
		}
	    }
	    catch(Exception ex){
		back +=" You could not save "+ex;
		logger.error(ex+" : "+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, rs, stmt, stmt2);
	    }
	}
	return back;
    }
    public String doSelect(){
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String back = "", str="", qq = "";
	qq = "select bid,rec_sum,to_char(rec_date,'mm/dd/yyyy'),"+
	    "rec_from,paid_by,check_no from reg_paid "+
	    " where receipt_no=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }

	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, receipt_no);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		setBid(str);
		str = rs.getString(2);
		setRec_sum(str);
		str = rs.getString(3);
		setRec_date(str);
		str = rs.getString(4);
		setRec_from(str);
		str = rs.getString(5);
		setPaidMethod(str);
		str = rs.getString(6);
		setCheck_no(str);				
	    }
	}
	catch(Exception ex){
	    back +=" You could not save "+ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }

}
























































package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.naming.directory.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Bill{

    boolean debug = false;
    final static long serialVersionUID = 90L;
    static Logger logger = LogManager.getLogger(Bill.class);
    double bul_rate=77., 
	unit_rate=28.,
	bath_rate=28., 
	reinsp_rate=77.,
	noshow_rate=50.,
	summary_rate=0.,
	appeal_rate=20.,
	IDL_rate=25.;
    // 
    int bul_cnt=1, unit_cnt=1, 
	reinsp_cnt=0, noshow_cnt=0;
    double bath_cnt = 0.;
    int summary_cnt=0, IDL_cnt=0;	
    String reinsp_date="", noshow_date="",status="", 
	due_date="",today="",issue_date="", summary_flag="",IDL_flag="";
    String other_fee_title = "", other_fee2_title="";
    String paid="",check_no="",invoice_num="", appeal = "";
    double credit=0.,appeal_fee=20.0, old_balance=0., bhqa_fine=0;

    // 
    double insp_fee=0, reinsp_fee=0,noshow_fee=0,balance=0,
	summary_fee=0,IDL_fee=0, rec_sum=0, total=0., other_fee=0.,
	other_fee2=0.;
		
    //
    // receipt items
    String rec_date="",rec_from="";
    //
    String id="", bid=""; // rental id, bill id
    Rent rent = null;
    ReceiptList receipts = null;
	
    public Bill (boolean val){
	debug = val;
    }
    public Bill (boolean val, String val2){
	debug = val;
	bid = val2;
    }
    public Bill (boolean val, String val2, String val3){
	debug = val;
	if(val2 != null)
	    bid = val2;
	if(val3 != null)
	    id = val3;
    }	
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setBid(String val){
	if(val != null)
	    bid = val;
    }
    public void setBul_rate(String val){
	if(val != null && !val.equals(""))
	    bul_rate = setDouble(val);
    }
    public void setUnit_rate(String val){
	if(val != null && !val.equals(""))
	    unit_rate = setDouble(val);
    }
    public void setBath_rate(String val){
	if(val != null && !val.equals(""))
	    bath_rate = setDouble(val);
    }
    public void setReinsp_rate(String val){
	if(val != null && !val.equals(""))
	    reinsp_rate = setDouble(val);
    }	
    public void setNoshow_rate(String val){
	if(val != null && !val.equals(""))
	    noshow_rate = setDouble(val);
    }
    public void setSummary_rate(String val){
	if(val != null && !val.equals(""))
	    summary_rate = setDouble(val);
    }
    public void setIDL_rate(String val){
	if(val != null && !val.equals(""))
	    IDL_rate = setDouble(val);
    }
    public void setAppeal_fee(String val){
	if(val != null && !val.equals(""))
	    appeal_fee = setDouble(val);
    }
    public void setInsp_fee(String val){
	if(val != null && !val.equals(""))
	    insp_fee = setDouble(val);
    }
    public void setReinsp_fee(String val){
	if(val != null && !val.equals(""))
	    reinsp_fee = setDouble(val);
    }
    public void setNoshow_fee(String val){
	if(val != null && !val.equals(""))
	    noshow_fee = setDouble(val);
    }
    public void setSummary_fee(String val){
	if(val != null && !val.equals(""))
	    summary_fee = setDouble(val);
    }
    public void setIDL_fee(String val){
	if(val != null && !val.equals(""))
	    IDL_fee = setDouble(val);
    }
    public void setRec_sum(String val){
	if(val != null && !val.equals(""))
	    rec_sum = setDouble(val);
    }
    public void setBhqa_fine(String val){
	if(val != null && !val.equals(""))
	    bhqa_fine = setDouble(val);
    }
    public void setBalance(String val){
	if(val != null && !val.equals(""))
	    balance = setDouble(val);
    }	
    public void setCredit(String val){
	if(val != null && !val.equals(""))
	    credit = setDouble(val);
    }
    public void setBul_cnt(String val){
	if(val != null && !val.equals(""))
	    bul_cnt = setInt(val);
    }
    public void setUnit_cnt(String val){
	if(val != null && !val.equals(""))
	    unit_cnt = setInt(val);
    }
    public void setBath_cnt(String val){
	if(val != null && !val.equals(""))
	    bath_cnt = setDouble(val);
    }
    public void setOther_fee(String val){
	if(val != null && !val.equals(""))
	    other_fee = setDouble(val);
    }
    public void setOther_fee2(String val){
	if(val != null && !val.equals(""))
	    other_fee2 = setDouble(val);
    }		
    public void setReinsp_cnt(String val){
	if(val != null && !val.equals(""))
	    reinsp_cnt = setInt(val);
    }
    public void setNoshow_cnt(String val){
	if(val != null && !val.equals(""))
	    noshow_cnt = setInt(val);
    }
    public void setSummary_cnt(String val){
	if(val != null && !val.equals(""))
	    summary_cnt = setInt(val);
    }
    public void setIDL_cnt(String val){
	if(val != null && !val.equals(""))
	    IDL_cnt = setInt(val);
    }	
    double setDouble(String val){
	double ret = 0.;
	if(val != null){
	    try{
		ret = Double.parseDouble(val);
	    }catch(Exception ex){
		logger.error("Invalid double "+val);
	    }
	}
	return ret;
    }
    int setInt(String val){
	int ret = 0;
	if(val != null){
	    try{
		ret = Integer.parseInt(val);
	    }catch(Exception ex){
		logger.error("Invalid integer "+val);
	    }
	}
	return ret;
    }
    public void setReinsp_date(String val){
	if(val != null)
	    reinsp_date = val;
    }
    public void setNoshow_date(String val){
	if(val != null)
	    noshow_date = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setAppeal(String val){
	if(val != null)
	    appeal = "Y";
    }	
    public void setDue_date(String val){
	if(val != null)
	    due_date = val;
    }
    public void setOther_fee_title(String val){
	if(val != null)
	    other_fee_title = val.trim();
    }
    public void setOther_fee2_title(String val){
	if(val != null)
	    other_fee2_title = val.trim();
    }		
    public void setIssue_date(String val){
	if(val != null)
	    issue_date = val;
    }
    public void setSummary_flag(String val){
	if(val != null)
	    summary_flag = val;
    }
    public void setIDL_flag(String val){
	if(val != null)
	    IDL_flag = val;
    }
    public void setRent(Rent val){
	if(val != null)
	    rent = val;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getBid(){
	return bid;
    }
    public double getBul_rate(){
	return bul_rate;
    }
    public double getUnit_rate(){
	return unit_rate;
    }
    public double getBath_rate(){
	return bath_rate ;
    }
    public double getReinsp_rate(){
	return reinsp_rate ;
    }	
    public double getNoshow_rate(){
	return noshow_rate ;
    }
    public double getOther_fee(){
	return other_fee ;
    }
    public double getOther_fee2(){
	return other_fee2 ;
    }		
    public double getSummary_rate(){
	return summary_rate ;
    }
    public double getIDL_rate(){
	return IDL_rate ;
    }
    public double getAppeal_fee(){
	return appeal_fee ;
    }
    public double getInsp_fee(){
	return insp_fee;
    }
    public double getReinsp_fee(){
	return reinsp_fee;
    }
    public double getNoshow_fee(){
	return noshow_fee;
    }
    public double getSummary_fee(){
	return summary_fee;
    }
    public double getIDL_fee(){
	return IDL_fee;
    }
    public double getRec_sum(){
	return rec_sum;
    }
    public double getBhqa_fine(){
	return bhqa_fine;
    }
    public double getBalance(){
	return balance;
    }	
    public double getCredit(){
	return credit;
    }
    public double getTotal(){
	return total; // balance
    }	
    public int getBul_cnt(){
	return bul_cnt ;
    }
    public int getUnit_cnt(){
	return unit_cnt;
    }
    public double getBath_cnt(){
	return bath_cnt;
    }
    public int getReinsp_cnt(){
	return reinsp_cnt;
    }
    public int getNoshow_cnt(){
	return noshow_cnt;
    }
    public String getStatus(){
	return status;
    }
    public String getReinsp_date(){
	return reinsp_date ;
    }
    public String getNoshow_date(){
	return noshow_date ;
    }
    public String getDue_date(){
	return due_date ;
    }
    public String getIssue_date(){
	return issue_date ;
    }
    public String getSummary_flag(){
	return summary_flag ;
    }
    public String getIDL_flag(){
	return IDL_flag ;
    }
    public String getOther_fee_title(){
	return other_fee_title;
    }
    public String getOther_fee2_title(){
	return other_fee2_title;
    }		
    public String getAppeal(){
	return appeal ;
    }
    public int getSummary_cnt(){
	return summary_cnt ;
    }
    public int getIDL_cnt(){
	return IDL_cnt ;
    }	
    public ReceiptList getReceipts(){
	if(receipts == null){
	    if(!bid.equals("")){
		receipts = new ReceiptList(debug, bid);
		String back = receipts.find();
		if(!back.equals("")){
		    logger.error(back);
		}
	    }
	}
	return receipts;
    }
    public Rent getRent(){
	if(rent == null && !id.equals("")){
	    rent = new Rent(id, debug);
	    String back = rent.doSelect();
	    if(!back.equals("")){
		logger.error(back);
	    }
	}
	return rent;
    }
    public String doSave(){
	//
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
		
	String str="", qq="", back="";
	//
	// insert these info in the database if it is not inserted yet
	// get an id for this bill
	//
	try{
	    qq = "select reg_bill_seq.nextval from dual"; 
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    stmt2 = con.prepareStatement(qq);					
	    rs = stmt2.executeQuery();
			
	    if(rs.next()){
		bid = rs.getString(1);
	    }
	    qq = "insert into reg_bills values (" +
		bid + ",'"+id+"',";
	    if(issue_date.equals(""))
		qq += "null,";
	    else
		qq += "to_date('"+issue_date+"','mm/dd/yyyy'),";
	    if(due_date.equals(""))
		qq += "null,";
	    else
		qq += "to_date('"+due_date+"','mm/dd/yyyy'),";
	    if(!appeal.equals("")){
		bul_rate=0;
		unit_rate=0;
		bath_rate=0;
		noshow_rate=0;
		reinsp_rate=0;
		bhqa_fine=0;
	    }
	    qq += "?,?,?,?,?,";
	    qq += "?,?,?,?,?,";
	    qq += "?,?,?,?,?,";
	    qq += "?,?,?,?,?,";
	    qq += "?,?,?,";
	    qq += "?,?,?,?)";// other_fee_title,other_fee,other_fee2_title,other_fee2
						
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    int jj=1;
	    stmt.setDouble(jj++, bul_rate);
	    stmt.setDouble(jj++, unit_rate);
	    stmt.setDouble(jj++, bath_rate);
	    stmt.setDouble(jj++, reinsp_rate);
	    stmt.setDouble(jj++, noshow_rate);
	    stmt.setDouble(jj++, bhqa_fine);
	    stmt.setInt(jj++, bul_cnt);
	    stmt.setInt(jj++, unit_cnt);
	    stmt.setDouble(jj++, bath_cnt);
	    stmt.setInt(jj++, noshow_cnt);
	    stmt.setInt(jj++, reinsp_cnt); // 12
			
	    if(reinsp_date.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, reinsp_date);						
	    if(noshow_date.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, noshow_date);

	    if(status.equals(""))status = "Unpaid";
	    stmt.setString(jj++,status);
	    if(appeal.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");
	    if(appeal.equals("Y")){
		stmt.setDouble(jj++,appeal_fee);
	    }
	    else{
		stmt.setDouble(jj++,0.);
	    }
	    stmt.setDouble(jj++, credit); // 18
			
	    if(summary_flag.equals(""))
		stmt.setDouble(jj++,0);
	    else
		stmt.setDouble(jj++,summary_rate);
	    if(IDL_flag.equals(""))
		stmt.setDouble(jj++,0);
	    else
		stmt.setDouble(jj++,IDL_rate);
	    if(summary_flag.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");
	    if(IDL_flag.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");
	    stmt.setInt(jj++, summary_cnt);
	    stmt.setInt(jj++, IDL_cnt);
	    if(other_fee_title.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, other_fee_title);
	    stmt.setDouble(jj++,other_fee);
	    if(other_fee2_title.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, other_fee2_title);
	    stmt.setDouble(jj++,other_fee2);
	    //
	    stmt.executeUpdate();
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);			
	    computeFees();
	}
	catch(Exception ex){
	    back += "You could not save "+ex;
	    logger.error(back +":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con,stmt,rs);
	}
	return back;
    }
    //
    public String doUpdate(){
		
	String str="", qq="", back="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	//
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    qq = "update reg_bills set ";
	    if(issue_date.equals(""))
		qq += "issue_date=null,";
	    else
		qq += "issue_date=to_date('"+issue_date+
		    "','mm/dd/yyyy'),";
	    if(due_date.equals(""))
		qq += "due_date=null,";
	    else
		qq += "due_date=to_date('"+due_date+
		    "','mm/dd/yyyy'),";
	    qq += "bul_rate=?,";
	    qq += "unit_rate=?,";
	    qq += "bath_rate=?,";
	    qq += "reinsp_rate=?,";
	    qq += "noshow_rate=?,";
	    qq += "bhqa_fine=?,";
	    qq += "bul_cnt=?,";
	    qq += "unit_cnt=?,";
	    qq += "bath_cnt=?,";
	    qq += "noshow_cnt=?,";
	    qq += "reinsp_cnt=?,";
	    qq += "credit=?,";
	    qq += "summary_rate=?,";
	    qq += "IDL_rate=?,";
	    qq += "summary_cnt=?,";
	    qq += "IDL_cnt=?,";
	    //
	    qq += "summary_flag=?,";
	    qq += "IDL_flag=?,";  
	    qq += "reinsp_date=?,";			
	    qq += "noshow_date=?,";
	    qq += "status=?,";
			
	    qq += "appeal=?,";
	    qq += "appeal_fee=?, ";
	    qq += "other_fee_title=?,";
	    qq += "other_fee=?,";
	    qq += "other_fee2_title=?,";
	    qq += "other_fee2=? ";						
	    qq += " where bid=? "; //23
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);					
	    int jj=1;
			
	    stmt.setDouble(jj++, bul_rate);
	    stmt.setDouble(jj++, unit_rate);
	    stmt.setDouble(jj++, bath_rate);
	    stmt.setDouble(jj++, reinsp_rate);
	    stmt.setDouble(jj++, noshow_rate);
			
	    stmt.setDouble(jj++, bhqa_fine);
	    stmt.setInt(jj++, bul_cnt);
	    stmt.setInt(jj++, unit_cnt);
	    stmt.setDouble(jj++, bath_cnt);
	    stmt.setInt(jj++, noshow_cnt);
			
	    stmt.setInt(jj++, reinsp_cnt);
	    stmt.setDouble(jj++, credit);
	    stmt.setDouble(jj++, summary_rate);
	    stmt.setDouble(jj++, IDL_rate);
	    stmt.setInt(jj++, summary_cnt);
			
	    stmt.setInt(jj++, IDL_cnt);			
	    if(summary_flag.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");
	    if(IDL_flag.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");
	    if(reinsp_date.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, reinsp_date);	
	    if(noshow_date.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, noshow_date);
	    if(status.equals(""))
		stmt.setString(jj++, "Unpaid");
	    else
		stmt.setString(jj++,status);
	    if(appeal.equals(""))
		stmt.setNull(jj++,Types.CHAR);
	    else
		stmt.setString(jj++,"Y");

	    if(appeal.equals("Y")){
		stmt.setDouble(jj++,appeal_fee);
	    }
	    else{
		stmt.setDouble(jj++,0.);
	    }
	    if(other_fee_title.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, other_fee_title);
	    stmt.setDouble(jj++,other_fee);
	    //
	    if(other_fee2_title.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, other_fee2_title);
	    stmt.setDouble(jj++,other_fee2);
	    //
	    stmt.setString(jj++, bid);
	    stmt.executeUpdate();
	    //
	    Helper.databaseDisconnect(con, stmt, rs);
	    //
	    computeFees();
	}
	catch(Exception ex){
	    back += "Could not update "+ex;
	    logger.error(back +":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con,stmt,rs);
	}
	return back;
    }
    public String doSelect(){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;

	String back="", qq = "", str="";
	qq = "select id,to_char(issue_date,'mm/dd/yyyy'),"+
	    "to_char(due_date,'mm/dd/yyyy'),bul_rate,unit_rate,"+
	    "bath_rate,reinsp_rate,noshow_rate,bhqa_fine,bul_cnt,"+
	    "unit_cnt,bath_cnt,noshow_cnt,reinsp_cnt,reinsp_date,"+
	    "noshow_date,status,appeal,appeal_fee,credit, "+
	    "summary_rate,IDL_rate,summary_flag,IDL_flag, "+
	    "summary_cnt,IDL_cnt, "+
	    "other_fee_title,other_fee,"+
	    "other_fee2_title,other_fee2 "+						
	    "from reg_bills where bid=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, bid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) id = str;
		str = rs.getString(2);
		if(str != null) issue_date = str;    
		str = rs.getString(3);
		if(str != null) due_date = str;   
		str = rs.getString(4);
		if(str != null) bul_rate = setDouble(str);   
		str = rs.getString(5);
		if(str != null) unit_rate = setDouble(str);   
		str = rs.getString(6);
		if(str != null) bath_rate = setDouble(str);   
		str = rs.getString(7);
		if(str != null) reinsp_rate = setDouble(str);   
		str = rs.getString(8);
		if(str != null) noshow_rate = setDouble(str);   
		str = rs.getString(9);
		if(str != null) bhqa_fine = setDouble(str);   
		str = rs.getString(10);
		if(str != null) bul_cnt = setInt(str);   
		str = rs.getString(11);
		if(str != null) unit_cnt = setInt(str);   
		str = rs.getString(12);
		if(str != null) bath_cnt = setDouble(str); 
		str = rs.getString(13);
		if(str != null) noshow_cnt = setInt(str);   
		str = rs.getString(14);
		if(str != null) reinsp_cnt = setInt(str); 
		str = rs.getString(15);
		if(str != null) reinsp_date = str; 
		str = rs.getString(16);
		if(str != null) noshow_date = str; 
		str = rs.getString(17);
		if(str != null) status = str; 
		str = rs.getString(18);
		if(str != null && str.equals("Y")) appeal = "Y";
		str = rs.getString(19);
		if(str != null) appeal_fee = setDouble(str); 
		str = rs.getString(20);
		if(str != null) credit = setDouble(str); 
		str = rs.getString(21);
		if(str != null) summary_rate = setDouble(str); 
		str = rs.getString(22);
		if(str != null) IDL_rate = setDouble(str); 
		str = rs.getString(23);
		if(str != null)
		    summary_flag = str;
		str = rs.getString(24);
		if(str != null)
		    IDL_flag = str;
		str = rs.getString(25);
		if(str != null) summary_cnt = setInt(str);
		str = rs.getString(26);
		if(str != null) IDL_cnt = setInt(str);
		str = rs.getString(27);
		if(str != null)
		    other_fee_title = str;
		str = rs.getString(28);
		if(str != null) other_fee = setDouble(str); 								
		str = rs.getString(29);
		if(str != null)
		    other_fee2_title = str;
		str = rs.getString(30);
		if(str != null) other_fee2 = setDouble(str);
		//
		rent = new Rent(id, debug);
		computeFees();				
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(qq+" : "+ex);
	}
	finally{
	    Helper.databaseDisconnect(con,stmt,rs);
	}
	return back;
    }
    public String doDelete(){

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	String qq = "", back="";
	qq = "delete from reg_bills where bid=?";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1,bid);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    back += "Could not delete "+ex;
	    logger.error(back+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con,stmt,rs);
	}
	return back;
    }
    public double getPaidSum(){

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	double paidSum = 0;
	//
	// for new bills
	if(bid.equals("")) return paidSum;
	//
	String qq = " select sum(rec_sum) from reg_paid where bid=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		logger.error(back);
		return 0.;
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, bid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null){
		    paidSum = setDouble(str);
		}
	    }
	}
	catch(Exception ex){
	    logger.error(qq+" : "+ex);
	}
	finally{
	    Helper.databaseDisconnect(con,stmt,rs);
	}
	return paidSum;
    }
	
    public String computeFees(){
	String back = "", prop_type="";
	total = 0.;
	double paidSum = 0.;
	double other_total = 0.;
	paidSum = getPaidSum();
	if(rent == null){
	    rent = new Rent(id, debug);
	}
	back = rent.doSelect();
	if(back.equals("")){
	    prop_type = rent.getProp_type();
	}
	if(appeal.equals("")){
	    double uc = 0;
	    insp_fee = bul_cnt * bul_rate;
	    if(prop_type.startsWith("Room")){
		uc = bath_cnt;
		insp_fee +=  bath_cnt*bath_rate;
	    }
	    else {
		uc = 0.0+unit_cnt;
		insp_fee += unit_rate* unit_cnt;
	    }				
	    if(noshow_cnt > 0){
		noshow_fee = noshow_cnt * noshow_rate;
	    }
	    if(reinsp_cnt > 0){
		reinsp_fee = reinsp_cnt * reinsp_rate; 
	    }
	    if( bhqa_fine > 0){
		total += bhqa_fine;
	    }
	    if(!summary_flag.equals("")){
		if(summary_cnt == 0){
		    summary_fee = uc * summary_rate;
		}
		else{
		    summary_fee = summary_cnt * summary_rate;
		}
	    }
	    if(!IDL_flag.equals("")){
		if(IDL_cnt == 0)
		    IDL_fee = uc * IDL_rate;
		else
		    IDL_fee = IDL_cnt * IDL_rate;					
	    }
	    other_total = other_fee+other_fee2;
	    total += insp_fee+noshow_fee+reinsp_fee+summary_fee+IDL_fee+other_total;
	    // 
	    // deduce any credit
	    if(credit > 0){
		total = total - credit;
	    }
	}
	else{ // appeal
	    total = appeal_fee;
	}
	if(paidSum > 0){
	    balance = total - paidSum;
	}
	else{
	    balance = total;
	}
	return back ;
    }
}
























































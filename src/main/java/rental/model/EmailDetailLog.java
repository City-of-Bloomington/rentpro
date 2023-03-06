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

public class EmailDetailLog{

    static Logger logger = LogManager.getLogger(EmailDetailLog.class);
    final static long serialVersionUID = 292L;
    boolean debug = false;		
    String date = "", userid = "", id = "", to="", cc="", bcc="",
	rentals_id="",owners_id="",agents_id="", status="";

    public EmailDetailLog(boolean val){
	debug = val;
    }
    public EmailDetailLog(boolean val, String val2){
	debug = val;
	if(val2 != null)
	    id = val2;
    }
    public EmailDetailLog(boolean deb,
			  String val3,
			  String val4,
			  String val5,
			  String val6,
			  String val7,
			  String val8,
			  String val9,
			  String val10
			  ){
	debug = deb;
	setUserid(val3);
	setTo(val4);
	setCc(val5);
	setBcc(val6);
	setOwners_id(val7);
	setAgents_id(val8);
	setRentals_id(val9);
	setStatus(val10);
				
    }	
		
    public EmailDetailLog(boolean deb,
			  String val,
			  String val2,
			  String val3,
			  String val4,
			  String val5,
			  String val6,
			  String val7,
			  String val8,
			  String val9,
			  String val10
			  ){
	debug = deb;
	setId(val);
	setDate(val2);
	setUserid(val3);
	setTo(val4);
	setCc(val5);
	setBcc(val6);
	setOwners_id(val7);
	setAgents_id(val8);
	setRentals_id(val9);
	setStatus(val10);
				
    }	

    public String getDate(){
	return date;
    }
    public String getUserid(){
	return userid;
    }
    public String getId(){
	return id;
    }
    public String getStatus(){
	return status;
    }
    public String getTo(){
	return to;
    }
    public String getCc(){
	return cc;
    }
    public String getBcc(){
	return bcc;
    }
    public String getOwners_id(){
	return owners_id;
    }
    public String getAgents_id(){
	return agents_id;
    }
    public String getRentals_id(){
	return rentals_id;
    }
    //
    public void setDate(String val){
	if(val != null)
	    date = val;
    }
    public void setUserid(String val){
	if(val != null)
	    userid = val;
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setTo(String val){
	if(val != null)
	    to = val.replace("<","&lt;").replace(">","&gt;");
    }
    public void setCc(String val){
	if(val != null)
	    cc = val.replace("<","&lt;").replace(">","&gt;");
    }
    public void setBcc(String val){
	if(val != null)
	    bcc = val.replace("<","&lt;").replace(">","&gt;");
    }
    public void setOwners_id(String val){
	if(val != null)
	    owners_id = val;
    }
    public void setAgents_id(String val){
	if(val != null)
	    agents_id = val;
    }
    public void setRentals_id(String val){
	if(val != null)
	    rentals_id = val;
    }
		

		
		
    public String doSave(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(date.equals(""))
	    date = Helper.getToday();
	String qq = "select email_detail_logs_seq.nextval from dual";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);			
	    rs = pstmt.executeQuery();
	    rs.next();
	    id = rs.getString(1);
	    qq = " insert into email_detail_logs values(?,to_date('"+date+"','mm/dd/yyyy'),?,?,?,?, ?,?,?,?)";
	    if(debug)
		logger.debug(qq);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.setString(2, userid);
	    if(to.equals(""))
		pstmt.setNull(3, Types.VARCHAR);								
	    else
		pstmt.setString(3, to);
	    if(cc.equals(""))
		pstmt.setNull(4, Types.VARCHAR);								
	    else
		pstmt.setString(4, cc);
	    if(bcc.equals(""))
		pstmt.setNull(5, Types.VARCHAR);								
	    else
		pstmt.setString(5, bcc);
	    if(owners_id.equals(""))
		pstmt.setNull(6, Types.VARCHAR);								
	    else
		pstmt.setString(6, owners_id);
	    if(agents_id.equals(""))
		pstmt.setNull(7, Types.VARCHAR);								
	    else
		pstmt.setString(7, agents_id);
	    if(rentals_id.equals(""))
		pstmt.setNull(8, Types.VARCHAR);								
	    else
		pstmt.setString(8, rentals_id);
	    if(status.equals(""))
		pstmt.setNull(9, Types.VARCHAR);
	    else
		pstmt.setString(9, status);
	    pstmt.executeUpdate();
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
		
    }
    public String doSelect(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "";
	qq = " select id,to_char(send_date,'mm/dd/yyyy'),userid,log_to,log_cc,log_bcc,owners_id,agents_id,rentals_id,send_status from email_detail_logs where id=? ";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    setDate(rs.getString(2));
		    setUserid(rs.getString(3));
		    setTo(rs.getString(4));					
		    setCc(rs.getString(5));
		    setBcc(rs.getString(6));
		    setOwners_id(rs.getString(7));
		    setAgents_id(rs.getString(8));
		    setRentals_id(rs.getString(9));
		    setStatus(rs.getString(10));
		}
		else{
		    back = "No match found";
		}
	    }
	    else{
		back = "Could not connect to DB ";
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
	
}
























































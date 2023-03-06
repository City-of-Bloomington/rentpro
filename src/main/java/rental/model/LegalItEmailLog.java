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

public class LegalItEmailLog{

    static Logger logger = LogManager.getLogger(LegalItEmailLog.class);
    final static long serialVersionUID = 290L;
    String to="", send_date="", from="", cc="", rental_id="",subject="",
	msg="", status="", error="";
    boolean debug = false;
    public LegalItEmailLog(boolean val){
	debug = val;
    }
    // for output
    public LegalItEmailLog(boolean deb,
			   String val,
			   String val2,
			   String val3,
			   String val4,
			   String val5,
			   String val6,
			   String val7,
			   String val8,
			   String val9
			   ){
	debug = deb;
	setRental_id(val);
	setDate(val2);
	setFrom(val3);
	setTo(val4);
	setCc(val5);
	setSubject(val6);
	setMsg(val7);
	setStatus(val8);
	setError(val9);
    }
    public void setFrom(String val){
	from = val; // even if it is null
    }
    public void setTo(String val){
	to = val; // even if it is null
    }
    public void setCc(String val){
	cc = val; // even if it is null
    }
    public void setRental_id(String val){
	rental_id = val; // even if it is null
    }
    public void setSubject(String val){
	subject = val; // even if it is null
    }
    public void setMsg(String val){
	msg = val; // event if it is null
    }
    public void setStatus(String val){
	status = val; // event if it is null
    }
    public void setDate(String val){
	if(val != null)
	    send_date = val;
    }
    public void setError(String val){
	if(val != null)
	    error = val;
    }											
    public String getRental_id(){
	return rental_id;
    }
    public String getDate(){
	return send_date;
    }
    public String getFrom(){
	return from;
    }
    public String getTo(){
	return to;
    }
    public String getCc(){
	return cc;
    }
    public String getSubject(){
	return subject;
    }
    public String getError(){
	return error;
    }		
    public String getMsg(){
	return msg;
    }
    public String getStatus(){
	return status;
    }		
    public String doSave(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(send_date.equals(""))
	    send_date = Helper.getToday();
	String qq = " insert into legalit_email_logs (rental_id,email_from,email_to,email_cc,email_subject,email_msg,status,fail_error) values (?,?,?,?,?,?,?,?)";
	if(debug)
	    logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to database ";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, rental_id);
	    pstmt.setString(2, from);
	    pstmt.setString(3, to);
	    pstmt.setString(4, cc);
	    pstmt.setString(5, subject);
	    pstmt.setString(6, msg);
	    pstmt.setString(7, status);
	    pstmt.setString(8, error);
	    pstmt.executeUpdate();
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
























































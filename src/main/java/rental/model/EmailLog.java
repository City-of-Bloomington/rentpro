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


public class EmailLog{

    static Logger logger = LogManager.getLogger(EmailLog.class);
    final static long serialVersionUID = 290L;
    String type="Expire", send_date="", userid="";
    boolean debug = false;
    public EmailLog(boolean val){
	debug = val;
    }
    public EmailLog(boolean val, String val2){
	debug = val;
	if(val2 != null)
	    type = val2;
    }
    public EmailLog(boolean val, String val2, String val3){
	debug = val;
	if(val2 != null)
	    type = val2;
	if(val3 != null)
	    userid = val3;		
    }	
    public EmailLog(boolean val, String val2, String val3, String val4){
	debug = val;
	if(val2 != null)
	    send_date = val2;
	if(val3 != null)
	    type = val3;
	if(val4 != null)
	    userid = val4;	
    }
    public String getDate(){
	return send_date;
    }
    public String getUserid(){
	return userid;
    }
    public String getType(){
	return type;
    }	
    public String doSave(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(send_date.equals(""))
	    send_date = Helper.getToday();
	String qq = " insert into email_logs values(to_date('"+send_date+"','mm/dd/yyyy'),'"+type+"','"+userid+"')";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.executeUpdate();
	    }
	    else{
		back = "Could not connect to DB ";
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
		
    }
	
}
























































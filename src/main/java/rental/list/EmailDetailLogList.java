package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
/**
 *
 *
 */

public class EmailDetailLogList extends ArrayList<EmailDetailLog>{

    static Logger logger = LogManager.getLogger(EmailDetailLogList.class);
    final static long serialVersionUID = 302L;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String date_from="", date_to="", date_at="";
    boolean debug = false;
    List<String> dateList = null;
    public EmailDetailLogList(boolean val){
	debug = val;
    }
    public EmailDetailLogList(boolean val, String val2, String val3){
	debug = val;
	setDate_from(val2);
	setDate_to(val3);
    }
    public void setDate_from(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDate_to(String val){
	if(val != null)
	    date_to = val;
    }
    public void setDate_at(String val){
	if(val != null)
	    date_at = val;
    }		
    public List<String> getDateList(){
	if(dateList == null){
	    findDateList();
	}
	return dateList;
    }
    public String findDateList(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	// year worth of data
	String qq = " select * from(select distinct send_date, to_char(send_date,'mm/dd/yyyy') from email_detail_logs order by send_date desc) where rownum < 13 ";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);				
	    rs = pstmt.executeQuery();
	    dateList = new ArrayList<String>();
	    while(rs.next()){
		String str  = rs.getString(2); //owner id
		dateList.add(str);
	    }
	}catch(Exception ex){
	    System.err.println(ex);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;				
    }
    public String findLatestDate(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = " select to_char(send_date,'mm/dd/yyyy') from email_detail_logs order by send_date desc";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    rs = pstmt.executeQuery();
	    if(rs.next()){ // we need only one
		date_at = rs.getString(1);
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
    public String find(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	//
	if(date_at.equals("")){
	    back = findLatestDate(); // bring the last one
	}
	String qq = " select id,to_char(send_date,'mm/dd/yyyy'),userid,log_to,log_cc,log_bcc,owners_id,agents_id,rentals_id,send_status from email_detail_logs ";
	String qw = "";
	if(!date_at.equals("")){
	    qw += " send_date = ? ";
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += " order by send_date desc ";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!date_at.equals("")){
		pstmt.setDate(jj++,new java.sql.Date(dateFormat.parse(date_at).getTime()));			
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		EmailDetailLog elog = new EmailDetailLog(debug,
							 rs.getString(1),
							 rs.getString(2),
							 rs.getString(3),
							 rs.getString(4),
							 rs.getString(5),
							 rs.getString(6),
							 rs.getString(7),
							 rs.getString(8),
							 rs.getString(9),
							 rs.getString(10));
		add(elog);
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
























































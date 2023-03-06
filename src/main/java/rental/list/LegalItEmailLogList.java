package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
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

public class LegalItEmailLogList extends ArrayList<LegalItEmailLog>{

    static Logger logger = LogManager.getLogger(LegalItEmailLogList.class);
    final static long serialVersionUID = 320L;
    String send_date="", rental_id="";
    boolean debug = false;
    public LegalItEmailLogList(boolean val){
	debug = val;
    }
    public LegalItEmailLogList(boolean val, String val2){
	debug = val;
	setRental_id(val2);
    }	
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }

    public String find(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select * from (select rental_id,to_char(send_date,'mm/dd/yyyy'),email_from,email_to,email_cc,email_subject,email_msg,status,fail_error from legalit_email_logs ";
	String qq2 = " order by send_date desc) where rownum < 10 ";
	if(!rental_id.equals("")){
	    qq += " where rental_id = ? ";
	}
	qq += qq2;
	if(debug)
	    logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
				
	}		
	try{
	    pstmt = con.prepareStatement(qq);
	    if(!rental_id.equals("")){
		pstmt.setString(1, rental_id);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		LegalItEmailLog elog =
		    new LegalItEmailLog(debug,
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getString(4),
					rs.getString(5),
					rs.getString(6),
					rs.getString(7),
					rs.getString(8),
					rs.getString(9)
					);
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
























































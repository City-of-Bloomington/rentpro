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

public class EmailLogList extends ArrayList<EmailLog>{

    static Logger logger = LogManager.getLogger(EmailLogList.class);
    final static long serialVersionUID = 300L;
    String type="Expire", send_date="", userid="";
    boolean debug = false;
    public EmailLogList(boolean val){
	debug = val;
    }
    public EmailLogList(boolean val, String val2){
	debug = val;
	if(val2 != null)
	    type = val2;
    }	

    public String find(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select * from (";
	qq += " select to_char(send_date,'mm/dd/yyyy'),type,userid from email_logs where type=? order by send_date desc) where rownum < 4 ";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,type);
		rs = pstmt.executeQuery();
		while(rs.next()){
		    String str = rs.getString(1);
		    String str2 = rs.getString(2);
		    String str3 = rs.getString(3);					
		    EmailLog elog = new EmailLog(debug,str, str2, str3 );
		    add(elog);
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
























































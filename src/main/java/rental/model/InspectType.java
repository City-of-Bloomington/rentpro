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


public class InspectType extends Item{

    static Logger logger = LogManager.getLogger(InspectType.class);
    final static long serialVersionUID = 430L;
    public InspectType(boolean val){
	super(val);
    }
    public InspectType(boolean val, String val2){
	super(val);
	if(val2 != null)
	    id = val2;
    }	
    public InspectType(boolean val, String val2, String val3){
	super(val, val2, val3);
    }

    public String doSelect(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = " select insp_desc from inspection_types where insp_type=?";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    String str = rs.getString(1);
		    if(str != null) name = str;
		}				
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
























































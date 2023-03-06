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

public class Zone extends Item{

    static Logger logger = LogManager.getLogger(Zone.class);
    final static long serialVersionUID = 1050L;
    public Zone(boolean val){
	super(val);
    }
    public Zone(boolean val, String val2){
	super(val, val2);
    }	
    public Zone(boolean deb, String val2, String val3){
	super(deb, val2, val3);
    }

    public String doSelect(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = " select initcap(zone_text) from zoning_2007 where zoned=?";
	if(debug)
	    logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) name = str;
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
























































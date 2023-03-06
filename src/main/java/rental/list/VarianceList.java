package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
/**
 *
 *
 */

public class VarianceList extends ArrayList<Variance>{

    boolean debug;
    final static long serialVersionUID = 1030L;
    static Logger logger = LogManager.getLogger(VarianceList.class);
    String registr_id="";
    //   List<Variance> variances = null;
    //
    // basic constructor
    public VarianceList(boolean deb){

	debug = deb;
    }
    public VarianceList(boolean deb, String val){

	debug = deb;
	registr_id = val;
    }	
	
    //
    // setters
    //
    public List<Variance> getVariances(){
	return this;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select vid,variance,to_char(variance_date,'mm/dd/yyyy') from variances where id=? order by variance_date desc"; //id = registr_id
	String back = "", str="", str2="", str3="";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, registr_id);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		Variance var = new Variance(debug, str, registr_id, str2, str3);
		add(var);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
}























































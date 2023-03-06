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

public class ReceiptList extends ArrayList<Receipt>{

    boolean debug;
    String bid=""; // bill id
    final static long serialVersionUID = 810L;
    static Logger logger = LogManager.getLogger(ReceiptList.class);
    //
    // basic constructor
    public ReceiptList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public ReceiptList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setBid(val);
    }
    //
    // setters
    //
    public void setBid(String val){
	if(val != null && !val.equals(""))
	    bid = val;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	String qq = "";// "select receipt_no from reg_paid ";
	qq = "select receipt_no, bid, rec_sum,to_char(rec_date,'mm/dd/yyyy'),"+
	    "rec_from,paid_by,check_no from reg_paid where bid=? ";
	qq += " order by 1 ";
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, bid);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		String str6 = rs.getString(6);
		String str7 = rs.getString(7);	
		Receipt rec = new Receipt(debug,
					  str, str2, str3, str4,
					  str5, str6, str7
					  );
		this.add(rec);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
}























































package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class BillList extends ArrayList<Bill>{

    boolean debug;
    String id=""; // rental id
    final static long serialVersionUID = 100L;
    static Logger logger = LogManager.getLogger(BillList.class);
    //
    // basic constructor
    public BillList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public BillList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setId(val);
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "select bid from reg_bills ";
	String qw = "";
	if(!id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " id="+id;
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	qq += " order by bid DESC ";
	String back = "";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = "Could not connect to DB ";
		return back;
	    }
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str  = rs.getString(1);
		Bill bill = new Bill(debug, str);
		str = bill.doSelect();
		if(str.equals("")) 
		    this.add(bill);
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























































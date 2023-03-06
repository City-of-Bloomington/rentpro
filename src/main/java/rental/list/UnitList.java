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

public class UnitList extends ArrayList<Unit>{

    boolean debug;
    String sid="";
    boolean uninspections = false;
    final static long serialVersionUID = 980L;
    static Logger logger = LogManager.getLogger(UnitList.class);
    //
    // basic constructor
    public UnitList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public UnitList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	this.sid = val;
    }
    //
    // setters
    //
    public void setSid(String val){
	if(val != null && !val.equals(""))
	    sid = val;
    }
    public boolean hasUninspections(){
	return uninspections;
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
	String qq = "select id,sid,units,bedrooms,occLoad,sleepRoom,"+
	    " uninspected "+
	    " from rental_units ";
	String qw = "";
	if(!sid.equals("")){
	    qw += " sid=?";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	qq += " order by id ";
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    if(!sid.equals("")){
		stmt.setString(1, sid);
	    }	
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		String str6 = rs.getString(6);
		String str7 = rs.getString(7);
		Unit unit = new Unit(debug, str, str2, str3, str4, str5,
				     str6, str7);
		if(unit.isUninspected()){
		    uninspections = true;
		}
		add(unit);
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























































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
 */

public class InspectorList extends ArrayList<Inspector>{

    boolean debug = false;
    String rid="", sid="";
    final static long serialVersionUID = 490L;
    static Logger logger = LogManager.getLogger(InspectorList.class);
    //
    // basic constructor
    public InspectorList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    //
    // setters
    //
    public List<Inspector>  getInspectors(){
	return this;
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
				
	String qq = "select initials,initcap(name),active from "+
	    "inspectors where name "+
	    "is not null and name not in ('Cas','Charles Brandt','Eric Sader') order by 2";

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
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		Inspector one = new Inspector(str, str2, str3, debug);
		if(!this.contains(one)) 
		    add(one);
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























































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

public class PropStatList extends ArrayList<Item>{

    boolean debug;
    static Logger logger = LogManager.getLogger(PropStatList.class);
    final static long serialVersionUID = 750L;
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    //
    // basic constructor
    public PropStatList(boolean deb){

	debug = deb;
	//
    }
    //
    // setters
    //
    public List<Item> getPropStatuses(){
	return this;
    }
    //
    // find all matching records
    //
    public String find(){
	//
	String qq = "select * from prop_status";
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
		Item item = new Item(debug, str, str2);
		add(item);
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























































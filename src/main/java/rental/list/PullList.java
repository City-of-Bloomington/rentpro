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


public class PullList extends ArrayList<Item>{

    boolean debug = false;
    final static long serialVersionUID = 780L;
    static Logger logger = LogManager.getLogger(PullList.class);
    //
    // basic constructor
    public PullList(boolean deb){

	debug = deb;
	//
    }
    //
    // setters
    //
    public List<Item> getPulls(){
	return this;
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
		
	String qq = "select p_reason,initcap(pull_text) from pull_reas order by pull_text";
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
	    rs = stmt.executeQuery();
			
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























































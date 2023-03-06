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

public class InspectTypeList extends ArrayList<Item>{

    boolean debug;
    final static long serialVersionUID = 440L;
    static Logger logger = LogManager.getLogger(InspectTypeList.class);
    String id = "";
    List<Item> inspectTypes = null;
    //
    // basic constructor
    public InspectTypeList(boolean deb){

	debug = deb;
	//
    }
    public InspectTypeList(boolean deb, String val){

	debug = deb;
	setId(val);
	//
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    //
    // setters
    //
    public List<Item> getInspectTypes(){
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

	String qq = "select * from inspection_types ";
	String back = "";
	if(!id.equals("")){
	    qq += " where insp_type = ? ";
	}
	qq += " order by 2 ";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    stmt = con.prepareStatement(qq);
	    if(!id.equals("")){
		stmt.setString(1,id);
	    }
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























































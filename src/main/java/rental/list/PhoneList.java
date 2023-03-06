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

public class PhoneList extends ArrayList<Phone>{

    boolean debug;
    final static long serialVersionUID = 690L;
    static Logger logger = LogManager.getLogger(PhoneList.class);
    String ownerId = "";
    List<Phone> phones;
    //
    // basic constructor
    public PhoneList(boolean deb){

	debug = deb;
	//
    }
    public PhoneList(boolean deb, String val){

	debug = deb;
	ownerId = val;
	//
    }	
    //
    // setters
    //
    public void setOwnerId(String val){
	if(val != null)
	    ownerId = val;
    }
    //
    // getters
    //
    public List<Phone> getPhones(){
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
	String qq = "select id,name_num,phone_num,type from owner_phones";
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    if(!ownerId.equals("")){
		qq += " where name_num =?";
	    }
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!ownerId.equals("")){
		pstmt.setString(1,ownerId);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3  = rs.getString(3);
		String str4 = rs.getString(4);	
		Phone ph = new Phone(debug, str, str2, str3, str4);
		add(ph);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
}























































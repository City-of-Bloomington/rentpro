package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class AddressList{

    boolean debug;
    final static long serialVersionUID = 70L;
    String id="", registr_id="", exclude_id="";
    Address addr = null;
    boolean invalid = false;
    String limit="";
    static Logger logger = LogManager.getLogger(AddressList.class);
    List<Address> addresses = null;
    //
    // basic constructor
    public AddressList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public AddressList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	this.registr_id = val;
    }
    public void setAddress (Address val){
	if(val != null)
	    addr = val;
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    public void setRegistr_id(String val){
	if(val != null && !val.equals(""))
	    registr_id = val;
    }
    public void excludeId(String val){
	if(val != null && !val.equals("")){
	    exclude_id = val;
	}
    }
    public void setLimit(String val){
	if(val != null)
	    limit = val;
    }
    public void setInvalid(){
	invalid = true;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    //
    public List<Address> getAddresses(){
	return addresses;
    }
    public String deletePrev(){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "", message = "";
	String qq = "delete from rental_addresses ";		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    message += ex;
	    logger.error(message);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}	
	return message;		
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String lookFor(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select id, registr_id,"+
	    " street_num,street_dir,initcap(street_name),street_type,"+
	    " sud_type,sud_num,post_dir, "+
	    " invalid_addr, "+
	    " location_id,street_address_id,subunit_id, "+
	    "streetAddress "+
	    "from "+Address.addressTable;
	String qw = "";
	if(!registr_id.equals("")){
	    qw += " registr_id=?";
	}
	if(!exclude_id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " not id=? ";
	}
	if(invalid){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " (invalid_addr = 'Y' or location_id is null) ";
	}
	if(!limit.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " rownum < "+limit;
	}		
	if(!qw.equals(""))
	    qq += " where "+qw;

	String back = "", message = "";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!registr_id.equals("")){
		pstmt.setString(jj++, registr_id);
	    }
	    if(!exclude_id.equals("")){
		pstmt.setString(jj++, exclude_id);
	    }
	    rs = pstmt.executeQuery();
	    addresses = new ArrayList<Address>();
	    while(rs.next()){
		String str   = rs.getString(1); // id
		String str2  = rs.getString(2); // registr_id
		String str3  = rs.getString(3);
		String str4  = rs.getString(4);
		String str5  = rs.getString(5);
				
		String str6  = rs.getString(6);
		String str7  = rs.getString(7);
		String str8  = rs.getString(8);
		String str9  = rs.getString(9);
		String str10 = rs.getString(10);
		String str11  = rs.getString(11);
		String str12  = rs.getString(12);
		String str13  = rs.getString(13);
				
		String str14  = rs.getString(14);				
		Address addr = new Address(debug,
					   str, str2, str3, str4, str5,
					   str6, str7, str8, str9, str10,
					   str11, str12, str13, str14);
		addresses.add(addr);
	    }
	}
	catch(Exception ex){
	    message += ex;
	    logger.error(message);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}	
	return message;
    }
	
    /*
     * for invalid addresses we use this method to find similar addresses
     * direction, street_name and type
     *
     */
    public String findSimilarAddr(String url){
	//
	// Vector<Address> addresses = null;
	//
	String msg = "";
	String urlStr = url+"/?";
	String query="format=xml;queryType=address;query=";
	boolean IN = false;
	if(addr == null){
	    msg = " No address set ";
	    return msg;
	}
	try{
	    if(!addr.getStreet_num().equals("")){
		query += java.net.URLEncoder.encode(addr.getStreet_num(), "UTF-8");
		IN = true;
	    }
	    if(!addr.getStreet_dir().equals("")){
		if(IN) query += "+";
		query += addr.getStreet_dir();
		IN = true;
	    }
	    if(!addr.getStreet_name().equals("")){
		if(IN) query += "+";
		query += java.net.URLEncoder.encode(addr.getStreet_name(), "UTF-8");
		IN = true;
	    }
	    if(!addr.getStreet_type().equals("")){
		if(IN)  query += "+";
		query += addr.getStreet_type();
		IN = true;
	    }
	    if(!addr.getSud_type().equals("")){
		if(IN)  query += "+";			
		query += addr.getSud_type();
		IN = true;
	    }
	    if(!addr.getSud_num().equals("")){
		if(IN)  query += "+";			
		query += java.net.URLEncoder.encode(addr.getSud_num(), "UTF-8");
		IN = true;
	    }
	    query +="+Bloomington;";
	    urlStr += query;
	    if(debug){
		logger.debug(urlStr);
	    }
	    HandleAddress ha = new HandleAddress(urlStr, debug);
	    addresses = ha.getAddresses();
	}catch(Exception ex){
	    logger.error(" "+ex);
	    msg += ex;
	}
	return msg;
    }	

}























































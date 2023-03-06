package rental.model;

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class Variance{

    boolean debug = false;
    final static long serialVersionUID = 1020L;
    static Logger logger = LogManager.getLogger(Variance.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
    String text = "", date="", id="";
    String registr_id="";
	
    //
    public Variance(boolean deb){
	debug = deb;
    }
    public Variance(boolean deb, String val){
	debug = deb;
	id = val;
    }
    public Variance(boolean deb, String val, String val2, String val3, String val4){
	debug = deb;
	setId(val);
	setRegistrId(val2);
	setText(val3);
	setDate(val4);
    }	

    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRegistrId(String val){
	if(val != null)
	    registr_id = val;
    }
    public void setText(String val){
	if(val != null)
	    text = val;
    }
    public void setDate(String val){
	if(val != null)
	    date = val;
    }

    public String getId(){
	return id;
    }
    public String getRegistrId(){
	return registr_id;
    }
    public String getText(){
	return text;
    }
    public String getDate(){
	return date;
    }

    public String doSave(){
		
	String qq = "", back = "";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rs = null;
		
	qq = " select variance_seq.nextval from dual";
	//
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = " Could not connect to DB ";
		return back;
	    }
	    if(id.equals("")){  // we may provide the id ourself
		// for delete and update purpose
		if(debug){
		    logger.debug(qq);
		}
		pstmt2 = con.prepareStatement(qq);
		rs = pstmt2.executeQuery();
		if(rs.next()){
		    id = rs.getString(1);
		}
	    }
	    qq = "insert into variances values (?,?,?,?)";
	    if(debug){
		logger.debug(qq);
	    }	
	    pstmt = con.prepareStatement(qq);
	    if(date.equals(""))
		date = Helper.getToday();
	    pstmt.setDate(1, new java.sql.Date(dateFormat.parse(date).getTime()));
	    InputStream stream = null;  			
	    stream = new ByteArrayInputStream(text.getBytes());  
	    pstmt.setAsciiStream(2,stream,stream.available());  		
	    // pstmt.setString(2, text);		
	    pstmt.setString(3, registr_id);
	    pstmt.setString(4, id);		
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;

    }
	
    public String doUpdate(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "", qq = "";
	try{
	    qq = "update variances set "+
		"variance_date=?,"+
		"variance=? "+
		"where vid =?";
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    if(date.equals(""))
		pstmt.setString(1, null);
	    else
		pstmt.setDate(1, new java.sql.Date(dateFormat.parse(date).getTime()));
	    InputStream stream = null;  			
	    stream = new ByteArrayInputStream(text.getBytes());  
	    pstmt.setAsciiStream(2,stream,stream.available());  			
	    // pstmt.setString(1,text);
	    pstmt.setString(3,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    public String doSelect(){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "", qq = "", str="";
	try{
	    qq = "select id,"+
		"to_char(variance_date,'mm/dd/yyyy'),"+
		"variance from variances "+
		"where vid=?";

	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null)
		    registr_id = str;
		str = rs.getString(2);
		if(str != null)
		    date = str;	
		str = rs.getString(3);
		if(str != null)
		    text = str;				
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	
    }
    public String doDelete(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	String back = "", qq = "";
	try{
	    qq = "delete from variances "+
		"where vid =?";
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
	
    public String toString(){
	return text;
    }
			
}























































package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class MediaFile{

    static Logger logger = LogManager.getLogger(MediaFile.class);
    String id="", rid="",date="", name="", notes="";
    boolean debug = false;
    final static long serialVersionUID = 600L;	
    public MediaFile(boolean val){
	debug = val;
    }
    public MediaFile(boolean deb, String val){ // for delete purpose
	debug = deb;
	setId(val);
    }	
    public MediaFile(boolean deb, String val,
		     String val2, String val3,
		     String val4, String val5){
	debug = deb;
	setId(val);
	setRid(val2);
	setDate(val3);		
	setName(val4);
	setNotes(val5);
		
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRid(String val){
	if(val != null)
	    rid = val;
    }
    public void setDate(String val){
	if(val != null)
	    date = val;
    }	
    public void setName(String val){
	if(val != null)
	    name = val;
    }
    public void setNotes(String val){
	if(val != null)
	    notes = val.trim();
    }
    //
    // getter
    //
    public String getId(){
	return id;
    }	
    public String getRid(){
	return rid;
    }	
    public String getDate(){
	return date;
    }
    public String getName(){
	return name;
    }
    public String getNotes(){
	return notes;
    }
    public boolean isImage(){
	return (name.endsWith("gif") || name.endsWith("png") ||
		name.endsWith("jpg") || name.endsWith("jpeg"));
    }
    public boolean hasName(){
	return !name.equals("");
    }	
    public boolean hasDate(){
	return !date.equals("");
    }
    public boolean hasNotes(){
	return !notes.equals("");
    }	
    public String get2DigitYear(){
	String ret = "";
	if(!date.equals("") && date.length() > 9){
	    ret = date.substring(8,10);
	}
	return ret;
    }	
    public String doSave(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    qq = "select rental_image_seq.nextval from dual";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);			
	    rs = pstmt.executeQuery();
	    rs.next();
	    id = rs.getString(1);			
	    date = Helper.getToday();
	    qq = "insert into rental_images values (?,?,sysdate,?,?)"; // curdate
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.setString(2, rid);
	    pstmt.setString(3, name);
	    if(notes.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4, notes);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	
    }
    public String doSelect(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	qq = "select rid,to_char(image_date,'mm/dd/yyyy'),image_file,notes from rental_images where id=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) rid = str;
		str = rs.getString(2);
		if(str != null) date = str;
		str = rs.getString(3);
		if(str != null) name = str;
		str = rs.getString(4);
		if(str != null) notes = str;
				
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}		
	return back;
    }
    //
    public String doDelete(){
	String back = "", qq = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    qq = "delete from rental_images where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	
    }	
    public String toString(){
	return name;
    }
    public String checkAndSetFileName(String inFile){

	String newFile = inFile;		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String today = Helper.getToday();
	String month = today.substring(0,2);
	String day = today.substring(3,5);		
	con = Helper.getConnection();
	if(con == null){
	    logger.error("Could not connect to DB");
	    return newFile;
	}		

	int seq = 100;
	String qq = "";
	try{
	    int cnt = 1;
	    while (cnt > 0){
		qq = "select count(*) "+
		    " from rental_images where " +
		    "image_file like '"+newFile+"%' "+
		    " and to_char(image_date,'mm/dd/yyyy')= ? ";
		cnt = 0;
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, today);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    cnt = rs.getInt(1);
		    if(cnt > 0){
			seq++;
			newFile = "rent"+month+day+seq; 
		    }
		}
	    }
	}
	catch(Exception ex){
	    System.err.println(ex);
	    logger.error(ex+": "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return newFile;
    }
    public boolean check(String imageName){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String today = Helper.getToday();
	String yy = today.substring(8,10);
	String qq = "select count (*) "+
	    " from rental_images where image_file=? "+
	    " and to_char(image_date,'yyyy') =? ";

	String str="";
	boolean found = false;
	int cnt = 0;
	if(debug){
	    System.err.println(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    logger.error("Could not connect to DB");
	}						
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, imageName);
	    pstmt.setString(2, yy);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		cnt = rs.getInt(1);
		if(cnt > 0) found = true;
	    }
	}
	catch(Exception ex){
	    System.err.println(ex);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}		
	return found;
    }
	
}
























































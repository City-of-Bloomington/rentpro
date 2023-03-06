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

public class Phone{

    static Logger logger = LogManager.getLogger(Phone.class);
    final static long serialVersionUID = 680L;
    String id="", type="", phone_num="", name_num="";
    boolean debug = false;
	
    public Phone(boolean val){
	debug = val;
    }
    public Phone(boolean deb, String val){ // for delete purpose
	debug = deb;
	id = val;
    }	
    public Phone(boolean deb, String val, String val2, String val3,
		 String val4){
	debug = deb;
	id = val;
	name_num = val2;
	phone_num = val3;
	type = val4;
		
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setOwnerId(String val){
	if(val != null)
	    name_num = val;
    }
    public void setNumber(String val){
	if(val != null)
	    phone_num = val;
    }
    public void setType(String val){
	if(val != null)
	    type = val;
    }
    //
    // getter
    //
    public String getId(){
	return id;
    }	
    public String getOwnerId(){
	return name_num;
    }
    public String getNumber(){
	return phone_num;
    }
    public String getType(){
	return type;
    }
    public boolean hasNumber(){
	return !phone_num.equals("");
    }
    public String doSave(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	String qq = "select phone_id_seq.nextval from dual";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{

	    if(debug){
		logger.debug(qq);
	    }
	    pstmt2 = con.prepareStatement(qq);
	    rs = pstmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into owner_phones values (?,?,?,?)";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.setString(2, name_num);
	    pstmt.setString(3, phone_num);
	    pstmt.setString(4, type);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;	
    }
    /*
     * delete all the phones for certain owner
     */
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
	    qq = "delete from owner_phones where id=?";
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
	return phone_num;
    }
	
}
























































package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class MediaFileList extends ArrayList<MediaFile>{

    boolean debug;
    final static long serialVersionUID = 610L;
    static Logger logger = LogManager.getLogger(MediaFileList.class);
    String rid = "";
    //
    // basic constructor
    public MediaFileList(boolean deb){

	debug = deb;
	//
    }
    public MediaFileList(boolean deb, String val){

	debug = deb;
	setRid(val);
	//
    }	
    //
    // setters
    //
    public void setRid(String val){
	if(val != null)
	    rid = val;
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
	String qq = "select id,rid,to_char(image_date,'mm/dd/yyyy'),image_file,notes from rental_images where rid=?";
	String back = "";
	if(rid.equals("")){
	    back = "rental id not set ";
	    return back;
	}		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,rid);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3  = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);				
		MediaFile one = new MediaFile(debug, str, str2, str3, str4, str5);
		add(one);
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























































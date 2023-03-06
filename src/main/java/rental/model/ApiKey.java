/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 */
package rental.model;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class ApiKey{

    String name="", value="",inactive="";
    static Logger logger = LogManager.getLogger(ApiKey.class);
    static final long serialVersionUID = 202L;	

    String errors = "";
    public ApiKey(String val, String val2){
	setName(val);
	setValue(val2);
    }
    public ApiKey(String val, String val2, boolean val3){
	setName(val);
	setValue(val2);
	setInactive(val3);
    }			
    public ApiKey(){
    }
    //
    // we need for list
    //
    // getters
    //
    public String getName(){
	return name;
    }
    public String getValue(){
	return value;
    }
    public boolean isActive(){
	return inactive.equals("");
    }
    public boolean isInactive(){
	return !inactive.equals("");
    }		
		
    public boolean hasErrors(){
	return !errors.equals("");
    }

    public void setValue(String val){
	if(val != null)
	    value = val;
    }
    public void setName(String val){
	if(val != null)
	    name = val;
    }
    public void setInactive(boolean val){
	if(val)
	    inactive = "y";
    }		
	
    public String toString(){
	return value;
    }
    public String doSelect(){
		
	String msg = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to Database ";
	    logger.error(msg);
	    return msg;
	}
	try {
	    qq = "select value,inactive from api_keys where name=? "; 
	    logger.debug(qq);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, name);
	    rs = pstmt.executeQuery();
	    //
	    if(rs.next()){
		setValue(rs.getString(1));
		setInactive(rs.getString(2) != null);
	    }
	    else{
		msg = "No record found for name "+name;
	    }
	}
	catch (Exception ex){
	    msg += ex+":"+qq;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return msg;

    }	
	
}

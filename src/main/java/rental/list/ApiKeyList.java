/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 */
package rental.list;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import javax.naming.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;

public class ApiKeyList{

    static Logger logger = LogManager.getLogger(ApiKeyList.class);
    static final long serialVersionUID = 203L;
    boolean active_only = false;
    List<ApiKey> keys = null;
    String errors = "";
    public ApiKeyList(){
    }

    public String getErrors(){
	return errors;
    }
    public List<ApiKey> getKeys(){
	return keys;
    }
    public void setActiveOnly(){
	active_only = true;
    }
	
    public String find(){
		
	String msg="";
		
	String qq = "select * from api_keys ";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	if(active_only){
	    qq += " where inactive is null ";
	}
	logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con == null){
		msg = "Could not connect ";
	    }
	    else{
		stmt = con.createStatement();
	    }
	    keys = new ArrayList<ApiKey>();
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);								
		if(str != null && str2 != null){
		    ApiKey one = new ApiKey(str, str2, str3 != null);
		    keys.add(one);
		}
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg;
    }
	
}

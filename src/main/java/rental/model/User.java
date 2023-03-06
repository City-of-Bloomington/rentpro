package rental.model;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class User implements java.io.Serializable{

    String username="", fullName="", dept="", role="", id="";
    boolean debug = false, userExist = false;
    final static long serialVersionUID = 1000L;
    static Logger logger = LogManager.getLogger(User.class);
    String errors = "";
    public User(String val){
	setUsername(val);
    }
    public User(boolean deb, String val){
	setUsername(val);
	debug = deb;
    }
    public User(boolean deb, String val, String val2){
	setId(val);				
	setUsername(val2);
	debug = deb;
    }		
    //
    public boolean hasRole(String val){
		
	return role.indexOf(val) > -1;

    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getUsername(){
	return username;
    }
    public String getFullName(){
	return fullName;
    }
    public String getDept(){
	return dept;
    }
    public String getRole(){
	return role;
    }
		
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setUsername (String val){
	if(val != null)
	    username = val.toLowerCase();
    }
    public void setFullName (String val){
	fullName = val;
    }
    public void setRole (String val){
	if(val != null)
	    role = val;
    }
    public boolean canEdit(){
	return hasRole("Edit");
    }
    public boolean canDelete(){
	return hasRole("Edit"); // no delete in roles for now
    }
    public boolean isAdmin(){
	return hasRole("Admin");
    }
    public boolean isInspector(){
	return hasRole("Inspect");
    }
    public boolean userExists(){
	return userExist;
    }
	
    public void setDept (String val){
	dept = val;
    }
    public String toString(){
	if(fullName == null || fullName.equals("")) return username;
	else return fullName;
    }
    public String doSelect(){
	//
	String msg="";
	PreparedStatement stmt = null;
	Connection con = null;
	ResultSet rs = null;		
	String qq = "select id,username,full_name,role from rental.users where ";
	if(!id.equals("")){
	    qq += " id=? ";
	}
	else{
	    qq += " username=? ";
	}
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    msg += " could not connect to database";
	    logger.error(msg);
	    return msg;
	}		
	try{
	    stmt = con.prepareStatement(qq);
	    if(!id.equals("")){
		stmt.setString(1, id);
	    }
	    else{
		stmt.setString(1, username);
	    }
	    rs = stmt.executeQuery();
	    if(rs.next()){
		setId(rs.getString(1));
		setUsername(rs.getString(2));
		setFullName(rs.getString(3));
		setRole(rs.getString(4));
		userExist = true;
	    }
	    else{
		msg = " User does not exist ";
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(msg+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg;
    }
	
}

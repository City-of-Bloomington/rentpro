package rental.model;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Inspector implements java.io.Serializable{

    String name="", initials="", active="";
    boolean debug = false;
    final static long serialVersionUID = 480L;
    static Logger logger = LogManager.getLogger(Inspector.class);
    String errors = "";
    public Inspector(String val, boolean deb){
	setInitials(val);
	debug = deb;
    }
    //
    public Inspector(String val, String val2, String val3, boolean deb){
	setInitials(val);
	setName(val2);
	setActive(val3);
	debug = deb;
    }
    //
    // getters
    //
    public String getInitials(){
	return initials;
    }
    public String getName(){
	return name;
    }
    public boolean isActive(){
	return !active.equals("");
    }
    //
    // setters
    //
    public void setName (String val){
	if(val != null)
	    name = val;
    }
    public void setInitials (String val){
	if(val != null)
	    initials = val;
    }
    public void setActive(String val){
	if(val != null)
	    active = val;
    }
    public String toString(){
	return name;
    }
    // change later to id
    public boolean equals(Object ob){
	if(ob instanceof Inspector){
	    Inspector one = (Inspector)ob;
	    if(one.getInitials().equals(initials)){
		return true;
	    }
	}
	return  false;
    }
    // change later to id
    public int hashCode(){
	int ret = 37;
	if(!initials.equals("")){
	    return initials.hashCode();
	}
	return ret;
    }

		
    public String doSelect(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;		
	String msg="";
	String qq = "select initcap(name),active from "+
	    "inspectors where initials=?";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, initials);				
		rs = pstmt.executeQuery();
		if(rs.next()){
		    String str = rs.getString(1);
		    if(str != null)
			name = str;
		    str = rs.getString(2);
		    if(str != null)
			active = str;
		}
		else{
		    msg = "No match found";
		}
	    }
	    else{
		msg = "Could not connect to DB ";
		return msg;
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(msg+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return msg;
    }
	
}

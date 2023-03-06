package rental.model;
import java.util.List;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Defendant implements java.io.Serializable{

    String did="",
	f_name="",l_name="",
	notes="", phone="", email="";
    final static long serialVersionUID = 230L;
    static Logger logger = LogManager.getLogger(Defendant.class);
	
    // Address address = new Address();
    List<DefAddress> addresses = null; // in case of multiple
    DefAddress address = null;
    boolean debug = false;
    String errors = "";
    public Defendant(boolean val){
	debug = val;
    }
	
    public Defendant(String val, boolean deb){
	did = val;
	debug = deb;
    }
    //
    // getters
    //
    public String getDid(){
	return did;
    }
    public String getFullName(){
	String str = l_name;
	if(!f_name.equals("")){
	    str += ", ";
	    str += f_name;
			
	}
	return str;
    }
    public DefAddress getAddress(){
	return address;
    }
    // setters
    //
    public void setFullName(String val){
	//
	// used to set the name from rental owners where we have the full name
	// the names have the composition of xxxx, yyyyy
	// if the name contains a comma we assume that the first part is last name
	// otherwise the whole name will be in l_name and we leave f_name name empty
	//
	int ind =-1;
	ind = val.indexOf(",");  
	if(ind > 0){
	    l_name = val.substring(0,ind);
	    if(ind+1 < val.length())
		f_name = val.substring(ind+1).trim();
			
	}
	else{
	    l_name = val;
	}
    }
	
    public void setF_name (String val){
	if(val != null)		
	    f_name = val;
    }
    public void setL_name (String val){
	if(val != null)		
	    l_name = val;
    }
    public void setPhone (String val){
	if(val != null)
	    phone = val;
    }
    public void setEmail (String val){
	if(val != null)
	    email = val;
    }	
    public void setAddress (DefAddress val){
	if(val != null)
	    address = val;
    }
    public String doSelect(){
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select f_name,l_name, "+
	    "ssn,date_format(dob,'%m/%d/%Y'), "+
	    "dln,phone,phone_2,email "+
	    "from legal_defendents where did=?";//  + did;
		
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, did);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) f_name = str;
		str = rs.getString(2);
		if(str != null) l_name = str;
		DefAddressList dal = new DefAddressList(debug, did);
		str = dal.lookFor();
		if(str.equals("")){
		    addresses = dal.getAddresses();
		    if(addresses != null && addresses.size() > 0){
			address = addresses.get(0); //  first one
		    }
		}
		else{
		    back += str;
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;		
    }
    public String doInsert(){

	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;				
	String qq = " insert into legal_defendents values(0,?,?,null,null,null,null,null,?,null,?)"; //
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    if(l_name.equals("")){
		stmt.setNull(1, Types.VARCHAR);
	    }
	    else{
		stmt.setString(1, l_name);
	    }
	    if(f_name.equals("")){
		stmt.setNull(2, Types.VARCHAR);
	    }
	    else {
		stmt.setString(2, f_name);
	    }
	    if(phone.equals("")){
		stmt.setNull(3, Types.VARCHAR);
	    }
	    else {
		stmt.setString(3, phone);
	    }
	    if(email.equals("")){
		stmt.setNull(4, Types.VARCHAR);
	    }
	    else {
		stmt.setString(4, email);
	    }	
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		did = rs.getString(1);
	    }
	    address.setDefId(did);
	    if(address.isValid()){
		back = address.doSave();
		if(!back.equals("")){
		    logger.error(back);
		}
	    }			
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
		
    }
    public String toString(){
	String ret = "";
	ret += " FullName: "+f_name+" "+l_name;
	ret += "\n Address: "+address;
	return ret;
    }

}

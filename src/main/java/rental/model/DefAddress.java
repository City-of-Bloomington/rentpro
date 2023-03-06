package rental.model;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class DefAddress extends Address{

    String defId="", addr_date="";
    boolean debug = false;
    final static long serialVersionUID = 210L;
    static Logger logger = LogManager.getLogger(DefAddress.class);
    public DefAddress(boolean deb){
	super(deb);
    }
    public DefAddress(boolean deb, String val){
	super(deb, val);
    }
    public DefAddress(boolean deb,
		      String val, // id
		      String val2, // defId
		      String val3,
		      String val4,
		      String val5,
		      String val6,
		      String val7,
		      String val8,
		      String val9,
		      String val10,
		      String val11,
		      String val12,
		      String val13,
		      String val14
		      ){
	super(deb,
	      val, // id
	      "", // registr_id
	      val3, val4, val5, val6, val7,
	      val8, val9, val10, val11,val12,
	      val13, val14
	      );
	defId = val2;
				
    }	
    public String getDefId(){
	return defId;
    }
    public void setDefId (String val){
	if(val != null)
	    defId = val;
    }
    public void setAddress(Address addr){
	street_num = addr.getStreet_num();
	street_dir = addr.getStreet_dir();
	street_name = addr.getStreet_name();
	street_type = addr.getStreet_type();
	post_dir = addr.getPost_dir();
	sud_num = addr.getSud_num();
	sud_type = addr.getSud_type();
	streetAddress = addr.getAddress();
	city = addr.getCity();
	state = addr.getState();
	zip = addr.getZip();
    }
    //
    public String toString(){
	return getAddress();
    }
    /** 
     * this is needed to avoid saving null addresses
     */
    public boolean isValid(){
	return !street_name.equals("");
    }
    public boolean isLocal(){
	if(city.equals("") && state.equals("")) return true;
	if(city.toUpperCase().equals("BLOOMINGTON") &&
	   state.toUpperCase().equals("IN")) return true;
	return false;
    }
    //
    public String doSave(){
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;				
	if(street_name.equals("")){
	    back = "Street Name is required";
	    return back;
	}
	String qq = " insert into legal_def_addresses value(0,?,?,?,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?)";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    //
	    stmt.setString(1, defId);
	    //
	    if(street_num.equals("")){
		stmt.setNull(2, Types.VARCHAR);
	    }
	    else {
		stmt.setString(2,street_num);
	    }
	    if(street_dir.equals("")){
		stmt.setNull(3, Types.VARCHAR);
	    }
	    else {
		stmt.setString(3,street_dir);
	    }
	    if(street_name.equals("")){
		stmt.setNull(4, Types.VARCHAR);			
	    }
	    else {
		stmt.setString(4,street_name);
	    }
	    if(street_type.equals("")){ // 5
		stmt.setNull(5, Types.VARCHAR);			
	    }
	    else {
		stmt.setString(5,street_type);
	    }
	    if(post_dir.equals("")){ 
		stmt.setNull(6, Types.VARCHAR);
	    }
	    else {
		stmt.setString(6,post_dir);
	    }
	    if(sud_type.equals("")){
		stmt.setNull(7, Types.VARCHAR);
	    }
	    else {
		stmt.setString(7,sud_type);
	    }
	    if(sud_num.equals("")){
		stmt.setNull(8, Types.VARCHAR);
	    }
	    else {
		stmt.setString(8,sud_num);
	    }
	    if(invalid_addr.equals(""))
		stmt.setNull(9, Types.CHAR);
	    else
		stmt.setString(9,"Y");	
	    if(city.equals(""))
		stmt.setNull(10, Types.VARCHAR);
	    else
		stmt.setString(10,city);			
	    if(state.equals(""))
		stmt.setNull(11, Types.VARCHAR);			
	    else
		stmt.setString(11,state);			
	    if(zip.equals(""))
		stmt.setNull(12, Types.VARCHAR);			
	    else
		stmt.setString(12,zip);			
	    stmt.setNull(13, Types.VARCHAR); // addr_date
	    /*
	      if(streetAddress.equals("")){
	      stmt.setNull(14, Types.VARCHAR);	
	      }
	      else {
	      stmt.setString(14,streetAddress);			
	      }
	    */
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not save address ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doDelete(){
		
	String back = "";
	String qq = "delete from legal_def_addresses where id=?";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not delete address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}			
	return back;
		
    }
    public String doSelect(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String str="", back = "";
	String qq = "select defId,"+
	    "street_num,street_dir,street_name,street_type,"+
	    "sud_type,sud_num,post_dir, "+
	    "invalid_addr,city,state,zip, "+
	    "date_format(addr_date,'%m/%d/%Y') "+
	    // "streetAddress "+
	    "from legal_def_addresses where id=?";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}	
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		defId = str;
		str = rs.getString(2);
		if(str != null) street_num = str;
		str = rs.getString(3);
		if(str != null) street_dir = str;
		str = rs.getString(4);
		if(str != null) street_name = str;
		str = rs.getString(5);  
		if(str != null) street_type = str; 
		str = rs.getString(6);
		if(str != null) sud_type = str;
		str = rs.getString(7);
		if(str != null) sud_num = str;
		str = rs.getString(8);
		if(str != null) post_dir = str;
		str = rs.getString(9);
		if(str != null && !str.toUpperCase().equals("N"))
		    invalid_addr = str.toUpperCase();
		str = rs.getString(10);
		if(str != null) city = str;
		str = rs.getString(11);
		if(str != null) state = str;
		str = rs.getString(12);
		if(str != null) zip = str;
		str = rs.getString(13);
		if(str != null) addr_date = str;
		/*
		  str = rs.getString(14);
		  if(str != null) streetAddress = str;
		*/
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not save address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}				
	return back;
    }

    public String doUpdate(){
	//		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = " update legal_def_addresses set ";
	qq += "street_num = ?,";
	qq += "street_dir = ?,";
	qq += "street_name = ?,";		
	qq += "street_type = ?,";
	qq += "post_dir = ?,";
	qq += "sud_type = ?,";
	qq += "sud_nume = ?,";
	qq += "invalid_addr =?,";
	qq += "city = ?,";
	qq += "state = ?,";
	qq += "zip = ? ";
	// qq += ", streetAddress =?";
	qq += "where id = ? ";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    if(street_num.equals("")){
		stmt.setNull(1, Types.VARCHAR);
	    }
	    else {
		stmt.setString(1,street_num);
	    }
	    if(street_dir.equals("")){
		stmt.setNull(2, Types.VARCHAR);
	    }
	    else {
		stmt.setString(2,street_dir);
	    }
	    if(street_name.equals("")){
		stmt.setNull(3, Types.VARCHAR);			
	    }
	    else {
		stmt.setString(3,street_name);
	    }
	    if(street_type.equals("")){ // 5
		stmt.setNull(4, Types.VARCHAR);			
	    }
	    else {
		stmt.setString(4,street_type);
	    }
	    if(post_dir.equals("")){ 
		stmt.setNull(5, Types.VARCHAR);
	    }
	    else {
		stmt.setString(5,post_dir);
	    }
	    if(sud_type.equals("")){
		stmt.setNull(6, Types.VARCHAR);
	    }
	    else {
		stmt.setString(6,sud_type);
	    }
	    if(sud_num.equals("")){
		stmt.setNull(7, Types.VARCHAR);
	    }
	    else {
		stmt.setString(7,sud_num);
	    }
	    if(invalid_addr.equals(""))
		stmt.setNull(8, Types.CHAR);
	    else
		stmt.setString(8,"Y");	
	    if(city.equals(""))
		stmt.setNull(9, Types.VARCHAR);
	    else
		stmt.setString(9,city);			
	    if(state.equals(""))
		stmt.setNull(10, Types.VARCHAR);			
	    else
		stmt.setString(10,state);			
	    if(zip.equals(""))
		stmt.setNull(11, Types.VARCHAR);			
	    else
		stmt.setString(11,zip);
	    /*
	      if(streetAddress.equals(""))
	      stmt.setNull(12, Types.VARCHAR);			
	      else
	      stmt.setString(12,streetAddress);
	    */
	    stmt.setString(12, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not update address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    /**
     * this function is called before an address is updated, therefore we
     * campare to its previous values
     */
    public boolean isModified(){
	String back = "";
	DefAddress old = new DefAddress(debug, id);
	back = old.doSelect();
	return !equals(old);
    }
    /**
     * two addresses are equals if all components are equal
     */
    public boolean equals(DefAddress addr){

	if(addr.getStreet_num().equals(street_num) &&
	   addr.getStreet_dir().equals(street_dir) &&
	   addr.getStreet_name().equals(street_name) &&
	   addr.getStreet_type().equals(street_type) &&		   
	   addr.getSud_type().equals(sud_type) &&
	   addr.getSud_num().equals(sud_num) &&
	   addr.getPost_dir().equals(post_dir) &&
	   addr.getCity().equals(city) &&
	   addr.getState().equals(state) &&
	   // addr.getStreetAddress().equals(streetAddress) &&
	   addr.getZip().equals(zip)) return true;
	return false;
		   
    }
}

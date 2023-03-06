package rental.utils;
import java.util.Vector;
import java.util.Hashtable;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;


public class AddressHandle implements java.io.Serializable{

    static Logger logger = LogManager.getLogger(AddressHandle.class);
    final static long serialVersionUID = 60L;	
    Hashtable<String, String> htStreetTypes = null;
    Hashtable<String, String> htSudTypes = null;
    Hashtable<String, String> htAlias = null;
    boolean debug = false;
    String errors = "";

    public AddressHandle(boolean val){
	debug = val;
	fillHashTables();
    }

    public void fillHashTables(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	htStreetTypes = new Hashtable<String,String>(100);
	htSudTypes = new Hashtable<String,String>(25);
	htAlias = new Hashtable<String,String>(22);
	String qq = " select * from eng.MAST_STREET_TYPE_SUFFIX_MASTER";		
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		logger.error("Could not connect to Database ");
		return ;
	    }
	    if(debug)
		System.err.println(qq);
	    pstmt = con.prepareStatement(qq);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String key = rs.getString(1);
		String val = rs.getString(2);

		if(key != null && val != null){
		    // System.err.println(key+" "+val);
		    htStreetTypes.put(val.trim(),key.trim());
		}
	    }
	    qq = " select * from eng.MAST_ADDR_SUBUNIT_TYPES_MAST";
	    if(debug)
		System.err.println(qq);
	    pstmt = con.prepareStatement(qq);			
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String key = rs.getString(1);
		String val = rs.getString(2);
		if(key != null && val != null){
		    // System.err.println(key+" "+val);
		    htSudTypes.put(val.trim(),key.trim());
		}
	    }
	}
	catch(Exception ex){
	    logger.error(" "+ex);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	//
	// create the alias hashtable
	//
	if(htAlias != null){
	    htAlias.put("first","1st");
	    htAlias.put("second","2nd");
	    htAlias.put("third","3rd");
	    htAlias.put("forth","4th");
	    htAlias.put("fourth","4th");
	    htAlias.put("fifth","5th");
	    htAlias.put("fiveth","5th");
	    htAlias.put("sixth","6th");
	    htAlias.put("seventh","7th");
	    htAlias.put("eighth","8th");
	    htAlias.put("nineth","9th");
	    htAlias.put("tenth","10th");
	    htAlias.put("eleventh","11th");
	    htAlias.put("twelveth","12th");
	    htAlias.put("thirteenth","13th");
	    htAlias.put("fourteenth","14th");
	    htAlias.put("fifteenth","15th");
	    htAlias.put("sixteenth","16th");
	    htAlias.put("seventeenth","17th");
	    htAlias.put("eighteenth","18th");
	    htAlias.put("nineteenth","19th");
	    htAlias.put("twentieth","20th");
	}
    }
    //
    public Address extractAddress (String val){
	Address addr = new Address();
		
	if(val != null && !val.equals("")){
	    String str[] = val.split("\\s");
	    int len = str.length;
	    int ind = 0;
	    if(len == 1){
		addr.setStreet_name(str[0]);
	    }
	    else{
		addr.setStreet_num(str[ind]);
		ind++;
		if(ind < len){
		    if(Helper.isDirection(str[ind])){
			addr.setStreet_dir(str[ind]);
			ind++;
		    }
		}
		if(ind < len){
		    addr.setStreet_name(str[ind]);
		    ind++;
		}
		if(ind < len){
		    while(ind < len){
			if(isStreetType(str[ind])){
			    if(ind+1 < len && isStreetType(str[ind+1])){
				String str2 = findStreetType(str[ind+1]);
				addr.setStreet_type(str2);
				addr.appendStreet_name(str[ind]);
				ind++;
				ind++;
			    }
			    else{
				String str2 = findStreetType(str[ind]);
				addr.setStreet_type(str2);
				ind++;
			    }
			}
			else if(isSudType(str[ind])){
			    String str2 = findSudType(str[ind]);
			    addr.setSud_type(str2);
			    ind++;
			    if(ind < len){
				addr.setSud_num(str[ind]);
				ind++;
			    }
			}
			else {
			    addr.appendStreet_name(str[ind]);
			    ind++;
			}
		    }
		}
	    }
	}
	return addr;
		
    }
    public boolean isStreetType(String str){

	boolean ret = false;
	if(str != null && !str.equals("")){
	    if(htStreetTypes.containsValue(str)){
		ret = true;
	    }
	    else if(htStreetTypes.containsKey(str)){
		ret =  true;
	    }
	}
	return ret;
    }
    public String findStreetType(String str){

	String ret = "";
	if(str != null && !str.equals("")){
	    if(htStreetTypes.containsValue(str)){
		ret = str;
	    }
	    else if(htStreetTypes.containsKey(str)){
		ret =  htStreetTypes.get(str);
	    }
	}
	return ret;
    }
    public boolean isSudType(String str){
	boolean ret = false;
		
	if(str != null && !str.equals("")){
	    if(htSudTypes.containsValue(str)){
		ret = true;
	    }
	    else if(htSudTypes.containsKey(str)){
		ret =  true;
	    }	
	}
	return ret;
    }
    public String findSudType(String str){
	String ret = "";
		
	if(str != null && !str.equals("")){
	    if(htSudTypes.containsValue(str)){
		ret = str;
	    }
	    else if(htSudTypes.containsKey(str)){
		ret =  htSudTypes.get(str);
	    }	
	}
	return ret;
    }	
}

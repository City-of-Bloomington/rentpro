package rental.model;
import java.util.Vector;
import java.sql.*;
import java.io.*;
//
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class LegalAddress implements java.io.Serializable{

    String id="", caseId="",
	street_num="",street_dir="", rental_addr="", streetAddress="",
	street_name="",street_type="",sud_type="",sud_num="", post_dir="",
	invalid_addr="", city="", state="", zip="";
    boolean debug = false;
    final static long serialVersionUID = 520L;
    static Logger logger = LogManager.getLogger(LegalAddress.class);
    public LegalAddress(boolean deb){
	debug = deb;
    }
    public LegalAddress(boolean deb, String val){
	debug = deb;
	id = val;
    }
    public LegalAddress(boolean deb,
			String val,
			String val2,
			String val3,
			String val4,
			String val5,
			String val6,
			String val7,
			String val8,
			String val9,
			String val10,
			String val11,
			String val12
			){
	debug = deb;
	setId(val);
	setCase_id(val2);
	setStreet_num(val3);
	setStreet_dir(val4);
	setStreet_name(val5);
	setStreet_type(val6);
	setPost_dir(val7);
	setSud_type(val8);
	setSud_num(val9);
	setInvalid_addr(val10);
	setRental_addr(val11);
	setStreetAddress(val12);
    }	
    //
    // getters
    //
    public String getStreet_num(){
	return street_num;
    }
    public String getStreet_dir(){
	return street_dir;
    }
    public String getStreet_name(){
	return street_name;
    }
    public String getStreetAddress(){
	return streetAddress;
    }		
    public String getStreet_type(){
	return street_type;
    }
    public String getSud_num(){
	return sud_num;
    }
    public String getSud_type(){
	return sud_type;
    }
    public String getPost_dir(){
	return post_dir;
    }
    public String getId(){
	return id;
    }
    public String getCity(){
	return city;
    }
    public String getState(){
	return state;
    }
    public String getZip(){
	return zip;
    }
    public String getCase_id(){
	return caseId;
    }
    public boolean isInvalid(){
	return invalid_addr.equals("Y");
    }
    public String getInvalid_addr(){
	return invalid_addr;
    }
    public String getRental_addr(){
	return rental_addr;
    }
    public boolean isRental(){
	return !rental_addr.equals("");
    }
    //	
    public String getAddress(){
	return getAddress(" ");
    }
    public String getAddress(String sep){

	if(!streetAddress.equals("")){
	    return streetAddress;
	}
	String str = street_num;
	if(!street_dir.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_dir;
	}
	if(!street_name.equals("")){
	    if(!str.equals("")) str += sep;
	    str += Helper.initCap(street_name);
	}
	if(!street_type.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_type;
	}
	if(!post_dir.equals("")){
	    if(!str.equals("")) str += sep;
	    str += post_dir;
	}
	if(!sud_type.equals("")){
	    if(!str.equals("")) str += sep;
	    str += sud_type;
	}
	if(!sud_num.equals("")){
	    if(!str.equals("")) str += sep;
	    str += sud_num;
	}	
	return str;
    }
	
    public String getCityStateZip(){
	String ret = Helper.initCap(city);
	if(!state.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += state;
	}
	if(!zip.equals("")){
	    if(!ret.equals("")) ret += " ";
	    ret += zip;
	}
	return ret;
    }
    public String getFullAddress(){
	String str = getAddress();
	String str2 = getCityStateZip();
	if(!str.equals("")){
	    if(!str2.equals(""))
		str += " "+str2;
	}
	return str;
    }
    //
    public void setStreet_num (String val){
	if(val != null)
	    street_num = val.trim();
    }
    public void setStreet_dir (String val){
	if(val != null)
	    street_dir = val;
    }
    public void setStreet_name (String val){
	if(val != null)
	    street_name = val.toUpperCase();
    }
    public void setStreet_type (String val){
	if(val != null)
	    street_type = val;
    }
    public void setStreetAddress(String val){
	if(val != null)
	    streetAddress = val;
    }		
    public void setPost_dir (String val){
	if(val != null)
	    post_dir = val;
    }
    public void setSud_type (String val){
	if(val != null)
	    sud_type = val;
    }
    public void setSud_num (String val){
	if(val != null)
	    sud_num = val.trim();
    }
    public void setCase_id (String val){
	if(val != null)
	    caseId = val;
    }
    public void setId (String val){
	if(val != null)
	    id = val;
    }
    public void setCity (String val){
	if(val != null)
	    city = val.toUpperCase();
    }
    public void setState (String val){
	if(val != null)
	    state = val.toUpperCase();
    }
    public void setZip (String val){
	if(val != null)
	    zip = val.trim();
    }
    public void setRental_addr (String val){
	if(val != null)
	    rental_addr = val;
    }
    public void setInvalid_addr (String val){
	if(val != null && val.toUpperCase().equals("Y"))
	    invalid_addr = "Y";
	else
	    invalid_addr = "";
    }
    //
    public void setInvalid(){
	invalid_addr = "Y";
    }
    //
    public void appendStreet_name (String val){
	if(val != null){
	    if(!street_name.equals("")) street_name += " ";
	    street_name += val;
	}
    }
    //
    public String toString(){
	return getAddress();
    }
    //
    public String doSave(){
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = " insert into legal_addresses value(0,?,?,?,?, ?,?,?,?,?, ?)";
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
	    stmt.setString(1, caseId);
	    back += fillStatement(stmt, 2);
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
    String fillStatement(PreparedStatement stmt, int j){
	String back = "";
	int jj=j;
	try{
	    if(street_num.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, street_num);
	    }
	    if(street_dir.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, street_dir);			
	    }
	    if(street_name.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, street_name);	
	    }
	    // 3
	    if(street_type.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, street_type);				
	    }
	    if(post_dir.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, post_dir);	
	    }
	    if(sud_type.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, sud_type);				
	    } // 6
	    if(sud_num.equals("")){
		stmt.setString(jj++, null);
	    }
	    else {
		stmt.setString(jj++, sud_num);				
	    }
	    if(invalid_addr.equals(""))
		stmt.setString(jj++, null);
	    else
		stmt.setString(jj++, "Y");
	    if(rental_addr.equals(""))
		stmt.setString(jj++, null);
	    else
		stmt.setString(jj++, "Y");
	    // 9
	    /*
	      if(streetAddress.equals("")){
	      stmt.setString(jj++, null);
	      }
	      else {
	      stmt.setString(jj++, streetAddress);				
	      }
	    */
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	return back;
    }
    //
    public String doDelete(){
	String back = "";
	String qq = "delete from legal_addresses where id=?";//+id;
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
	    stmt.setString(1,id);
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
	String str="", back = "";
	String qq = "select caseId,"+
	    "street_num,street_dir,street_name,street_type,"+
	    "sud_type,sud_num,post_dir, "+
	    "invalid_addr,rental_addr,streetAddress "+
	    "from legal_addresses where id=?";
	if(debug){
	    logger.debug(qq);
	}
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		caseId = str;
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
		if(str != null) rental_addr = str;
		str = rs.getString(11);
		if(str != null) streetAddress = str;
				
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
	String qq = " update legal_addresses set ";
	qq += " street_num = ?, street_dir=?, street_name=?,street_type=?,"+
	    "post_dir=?,sud_type=?,sud_num=?,invalid_addr=?,rental_addr=? "+
	    // ",streetAddress=? "+
	    "where id=? ";
		
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
	    back += fillStatement(stmt, 1);
	    stmt.setString(10, id);
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
    public boolean isValid(String url){
	//
	boolean ret = false;
	try{
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    //url = url;
	    //url = url+"+Bloomington&format=xml";
	    HttpPost httpost = new HttpPost(url);
	    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    /*
	      if(!street_num.equals("")){
	      // to avoid st_num like 51-57 banch
	      String st_num = street_num;
	      if(street_num.indexOf("-") > -1){
	      st_num = street_num.substring(0,street_num.indexOf("-"));
	      }
	      else if(street_num.indexOf(",") > -1){
	      st_num = street_num.substring(0,street_num.indexOf(","));
	      }
	      st_num = st_num.trim();
	      nvps.add(new BasicNameValuePair("street_number", st_num));
	      }
	      if(!street_dir.equals(""))
	      nvps.add(new BasicNameValuePair("direction", street_dir));
	      if(!street_name.equals(""))
	      nvps.add(new BasicNameValuePair("street_name", street_name));
	      if(!street_type.equals(""))
	      nvps.add(new BasicNameValuePair("street_type", street_type));
	      if(!post_dir.equals(""))
	      nvps.add(new BasicNameValuePair("postDirection", post_dir));
	      if(!sud_type.equals(""))
	      nvps.add(new BasicNameValuePair("subunitType", sud_type));
	      if(!sud_num.equals(""))
	    */
			
	    nvps.add(new BasicNameValuePair("address", getAddress()+" Bloomington"));
	    nvps.add(new BasicNameValuePair("format", "txt"));					
	    httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF_8"));
			
	    HttpResponse response = httpclient.execute(httpost);
	    HttpEntity entity = response.getEntity();
	    if(debug)
		System.out.println("Response: " + response.getStatusLine());
	    if (entity != null) {
		System.out.println(entity.getContentType());
		System.out.println(entity.getContentLength());
		long ll = entity.getContentLength();
		int len = (int)ll;
		java.io.InputStream ist = entity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(ist));
		String str = "";
		char[] c = new char[len];
		int n = 0;
		n = br.read(c,0,len);
		for(int i=0;i<len;i++)
		    str += c[i];
		if(str.indexOf("Invalid") == -1) ret = true;
		if(debug)
		    logger.debug(" response :"+str);
		// entity.consumeContent();
		ist.close();
	    }
	    // When HttpClient instance is no longer needed, 
	    // shut down the connection manager to ensure
	    // immediate deallocation of all system resources
	    httpclient.getConnectionManager().shutdown();
	}catch(Exception ex){
	    logger.error(ex);
	}
	return ret;
    }
	
    public boolean isValid2(String url){
	//
	boolean ret = false;
	try{
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httpost = new HttpPost(url);
	    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    if(!street_num.equals("")){
		// to avoid st_num like 51-57 banch
		String st_num = street_num;
		if(street_num.indexOf("-") > -1){
		    st_num = street_num.substring(0,street_num.indexOf("-"));
		}
		else if(street_num.indexOf(",") > -1){
		    st_num = street_num.substring(0,street_num.indexOf(","));
		}
		st_num = st_num.trim();
		nvps.add(new BasicNameValuePair("street_number", st_num));
	    }
	    if(!street_dir.equals(""))
		nvps.add(new BasicNameValuePair("direction", street_dir));
	    if(!street_name.equals(""))
		nvps.add(new BasicNameValuePair("street_name", street_name));
	    if(!street_type.equals(""))
		nvps.add(new BasicNameValuePair("street_type", street_type));
	    if(!post_dir.equals(""))
		nvps.add(new BasicNameValuePair("postDirection", post_dir));
	    if(!sud_type.equals(""))
		nvps.add(new BasicNameValuePair("subunitType", sud_type));
	    if(!sud_num.equals(""))
		nvps.add(new BasicNameValuePair("subunitIdentifier", sud_num));
	    nvps.add(new BasicNameValuePair("city", "Bloomington"));
	    nvps.add(new BasicNameValuePair("format", "txt"));					
	    httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF_8"));
			
	    HttpResponse response = httpclient.execute(httpost);
	    HttpEntity entity = response.getEntity();
	    if(debug)
		System.out.println("Response: " + response.getStatusLine());
	    if (entity != null) {
		System.out.println(entity.getContentType());
		System.out.println(entity.getContentLength());
		long ll = entity.getContentLength();
		int len = (int)ll;
		java.io.InputStream ist = entity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(ist));
		String str = "";
		char[] c = new char[len];
		int n = 0;
		n = br.read(c,0,len);
		for(int i=0;i<len;i++)
		    str += c[i];
		if(str.indexOf("Invalid") == -1) ret = true;
		if(debug)
		    logger.debug(" response :"+str);
		ist.close();
		// entity.consumeContent();
	    }
	    // When HttpClient instance is no longer needed, 
	    // shut down the connection manager to ensure
	    // immediate deallocation of all system resources
	    httpclient.getConnectionManager().shutdown();
	}catch(Exception ex){
	    logger.error(ex);
	}
	return ret;
    }
	
}

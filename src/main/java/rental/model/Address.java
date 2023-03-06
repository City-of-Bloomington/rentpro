
package rental.model;
import java.io.*;
import java.sql.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Address implements java.io.Serializable{

    public final static String addressTable = "address2"; // ce.address2
    static Logger logger = LogManager.getLogger(Address.class);
    final static long serialVersionUID = 40L;	
	
    String id="", rid="",
	street_num="",
	street_dir="",
	street_name="",street_type="",sud_type="",sud_num="", post_dir="",
	notes="", uid="";
    //
    // from master address
    String location_id="", street_address_id="", subunit_id="";
    String streetAddress = "";  
	
    String tag = ""; // for GIS search


    String lat="", lng="";
    //
    String city="",state="",zip=""; // for start legal
    String invalid_addr="";
    List<Subunit> subunits = null;
    boolean debug = false;


    public Address(){

    }

    public Address(boolean deb){
	debug = deb;
    }
    public Address(boolean deb, String val){
	debug = deb;
	id = val;
    }
    public Address(boolean deb, String val, String val2,
		   String val3, String val4, String val5,
		   String val6, String val7, String val8,
		   String val9, String val10,
		   String val11, String val12, String val13,
		   String val14
		   ){
	debug = deb;
	setId(val);
	setRid(val2);
	setStreet_num(val3);
	setStreet_dir(val4);
	setStreet_name(val5);
	setStreet_type(val6);
	setSud_type(val7);
	setSud_num(val8);
	setPost_dir(val9);
	setInvalid_addr(val10);
	setLocation_id(val11);
	setStreet_address_id(val12);
	setSubunit_id(val13);
	setStreetAddress(val14);
    }
	
    public Address(boolean deb, String val, String val2,
		   String val3, String val4, String val5,
		   String val6, String val7, String val8,
		   String val9, String val10,
		   String val11, String val12, String val13
		   ){
	debug = deb;
	setId(val);
	setRid(val2);
	setStreet_num(val3);
	setStreet_dir(val4);
	setStreet_name(val5);
	setStreet_type(val6);
	setSud_type(val7);
	setSud_num(val8);
	setPost_dir(val9);
	setInvalid_addr(val10);
	setCity(val11);
	setState(val12);
	setZip(val13);		
    }	
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getStreet_address_id(){
	return street_address_id;
    }
    public String getLocation_id(){
	return location_id;
    }
    public String getSubunit_id(){
	return subunit_id;
    }	
    public String getRegistr_id(){
	return rid;
    }
    public String getRid(){
	return rid;
    }	
    public String getStreet_num(){
	return street_num;
    }
    public String getStreet_dir(){
	return street_dir;
    }
    public String getStreet_name(){
	return street_name;
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
    public String getInvalid_addr(){
	return invalid_addr;
    }
    public boolean isInvalid(){
	if(!rid.equals("")){
	    return !invalid_addr.equals("") || location_id.equals("");
	}
	return !invalid_addr.equals("");
    }
    public boolean isLegit(){
	return !street_name.equals("");
    }
    public String getUid(){
	return uid;
    }
    public String getStreetAddress(){ // for owners
	return streetAddress;
    }
    public List<Subunit> getSubunits(){
	return subunits;
    }

    public String getAddress(String sep){
	if(!streetAddress.equals("")){
	    return streetAddress.replace(" ",sep);
	}
	String str = street_num;
	if(!street_dir.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_dir;
	}
	if(!street_name.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_name;
	}
	if(!street_type.equals("")){
	    if(!str.equals("")) str += sep;
	    if(street_type.equals("BYP"))
		str += "Bypass";
	    else
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
    public String getAddress(){
	if(!streetAddress.equals("")){
	    return streetAddress;
	}
	return getAddress(" "); // single space
    }
    //
    // exclude the subunits address
    //
    public String getShortAddress(String sep){
	String str = "";
	if(street_num != null && !street_num.equals("")){
	    str = street_num;
	}
	if(street_dir != null && !street_dir.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_dir;
	}
	if(!street_name.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_name;
	}
	if(!street_type.equals("")){
	    if(!str.equals("")) str += sep;
	    str += street_type;
	}
	if(!post_dir.equals("")){
	    if(!str.equals("")) str += sep;
	    str += post_dir;
	}
	return str;
    }
    public String getShortAddress(){
	return getShortAddress(" "); // single space
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
    public String getLat(){
	return lat;
    }
    public String getLng(){
	return lng;
    }
    public String getCityStateZip(){
	String ret = city;
	if(!state.equals("")){
	    if(!ret.equals(""))
		ret += ", ";
	    ret += state;
	}
	if(!zip.equals("")){
	    if(!ret.equals(""))
		ret += " ";
	    ret += zip;
	}
	return ret;
    }
    public void setStreet_num (String val){
	if(val != null){
	    street_num = val;
	}
    }
    public void setStreet_dir (String val){
	if(val != null)
	    street_dir = val;
    }
    public void setStreet_name (String val){
	if(val != null)
	    street_name = val;
    }
    public void setStreet_type (String val){
	if(val != null)
	    street_type = val;
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
	    sud_num = val;
    }
    public void setInvalid_addr (String val){
	if(val != null)
	    invalid_addr = val;
    }
    public void setId (String val){
	if(val != null)
	    id = val;
    }
    public void setStreet_address_id (String val){
	if(val != null)
	    street_address_id = val;
    }
    public void setLocation_id (String val){
	if(val != null)
	    location_id = val;
    }
    public void setSubunit_id (String val){
	if(val != null)
	    subunit_id = val;
    }	
    public void setTag (String val){
	if(val != null && !val.trim().equals(""))
	    tag = val;
    }	
    public void setLat (String val){
	if(val != null)
	    lat = val;
    }
    public void setLng (String val){
	if(val != null)
	    lng = val;
    }
    public void setCity (String val){
	if(val != null)
	    city = val;
    }
    public void setState (String val){
	if(val != null)
	    state = val;
    }
    public void setZip (String val){
	if(val != null){
	    if(!zip.equals(""))
		zip += "-"+val;
	    else
		zip = val;
	}
    }
    public void setStreetAddress (String val){
	if(val != null)
	    streetAddress = val;
    }
    public void setRegistr_id (String val){
	if(val != null)
	    rid = val;
    }
    public void setRid (String val){
	if(val != null)
	    rid = val;
    }	
    public void appendStreet_name (String val){
	if(val != null){
	    if(!street_name.equals("")) street_name += " ";
	    street_name += val;
	}
    }
    public void addSubunit (Subunit val){
	if(val != null){
	    if(subunits == null){
		subunits = new ArrayList<Subunit>(); 
	    }
	    subunits.add(val);
	}
    }
	
    public boolean hasSubunits(){
	return subunits != null && subunits.size() > 0;
    }
    //
    // to check if  this is a new address
    //
    public boolean isNew(){
	if(id.equals("") && !street_name.equals("")) return true;
	return false;
    }
    //
    public String doSave(){

	String back = "";
	String qq = "select address_seq.nextval from dual";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt2 = con.prepareStatement(qq);
	    rs = pstmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = " insert into "+addressTable+" values(?,?,?,?,?, "+
		"?,?,?,?,?, ?,?,?,?)";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(street_num.equals("")){
		pstmt.setNull(1,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(1, street_num);
	    }
	    if(street_dir.equals("")){
		pstmt.setNull(2,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(2, street_dir);
	    }
	    if(street_name.equals("")){
		pstmt.setNull(3,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(3, street_name);
	    }
	    if(street_type.equals("")){
		pstmt.setNull(4,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(4, street_type);
	    }
	    if(post_dir.equals("")){
		pstmt.setNull(5,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(5, post_dir);
	    }
	    if(sud_type.equals("")){
		pstmt.setNull(6,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(6, sud_type);
	    }
	    if(sud_num.equals("")){
		pstmt.setNull(7,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(7, sud_num);
	    }
	    if(invalid_addr.equals("") || !location_id.equals(""))
		pstmt.setNull(8,Types.CHAR);
	    else
		pstmt.setString(8, "Y");
	    pstmt.setString(9, rid);
	    pstmt.setString(10, id);	// 11
	    if(location_id.equals(""))
		pstmt.setNull(11,Types.INTEGER);
	    else
		pstmt.setString(11, location_id);
	    if(street_address_id.equals(""))
		pstmt.setNull(12,Types.INTEGER);
	    else
		pstmt.setString(12, street_address_id);

	    if(subunit_id.equals(""))
		pstmt.setNull(13,Types.INTEGER);
	    else
		pstmt.setString(13, subunit_id);
	    if(streetAddress.equals("")){
		pstmt.setNull(14,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(14, streetAddress);
	    }
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back = " Could not save address: "+qq;
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}		
	return back;
    }
    public String addMastAddrInfo(){

	String back = "";
	String qq = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(location_id.equals("")) return back;
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    qq = " update address2 set invalid_addr=null, location_id=?, street_address_id=?, subunit_id=?, streetAddress=? "+
		" where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);

	    pstmt.setString(1, location_id);
	    pstmt.setString(2, street_address_id);
	    if(subunit_id.equals("")){
		pstmt.setNull(3, Types.INTEGER);
	    }
	    else{
		pstmt.setString(3, subunit_id);
	    }
	    if(streetAddress.equals("")){
		pstmt.setNull(4, Types.VARCHAR);
	    }
	    else{
		pstmt.setString(4, streetAddress);
	    }
	    pstmt.setString(4, id);			
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back = " Could not save address: "+qq;
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}		
	return back;
    }
    //
    public String doDelete(){

	String qq = "delete from "+addressTable+" where id=?", back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back = " Could not delete address: "+qq+" ";
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }	
    //
    public String doSelect(){
		
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String str="", back = "";
	String qq = "select registr_id,"+
	    "street_num,street_dir,initcap(street_name),street_type,"+
	    "sud_type,sud_num,post_dir, "+
	    "invalid_addr, "+
	    "location_id,street_address_id,subunit_id, "+
	    "streetAddress "+
	    "from "+addressTable+" where id=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!id.equals("")){
		pstmt.setString(1, id);
	    }
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		rid = str;
		str = rs.getString(2);
		if(str != null) setStreet_num(str);
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
		if(str != null) invalid_addr = str;
		str = rs.getString(10);
		if(str != null) location_id = str;
		str = rs.getString(11);
		if(str != null) street_address_id = str;
		str = rs.getString(12);
		if(str != null) subunit_id = str;
		str = rs.getString(13);
		if(str != null) streetAddress = str;
	    }
	}
	catch(Exception ex){
	    back = " Could not retreive address "+qq+" ";
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    //
    public String doUpdate(){
	//
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String qq = " update "+addressTable+" set ";
	qq += "street_num=?,";
	qq += "street_dir=?,";
	qq += "street_name=?,";
	qq += "street_type=?,";
	qq += "post_dir=?,";
	qq += "sud_type=?,";
	qq += "sud_num=?,";
	qq += "invalid_addr=?,";
	qq += "location_id=?,street_address_id=?,subunit_id=?, "+
	    "streetAddress=? ";
	qq += " where id=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);		
	    if(street_num.equals("")){
		pstmt.setNull(1,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(1, street_num);
	    }
	    if(street_dir.equals("")){
		pstmt.setNull(2,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(2, street_dir);
	    }
	    if(street_name.equals("")){
		pstmt.setNull(3,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(3, street_name);
	    }
	    if(street_type.equals("")){
		pstmt.setNull(4,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(4, street_type);
	    }
	    if(post_dir.equals("")){
		pstmt.setNull(5,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(5, post_dir);
	    }
	    if(sud_type.equals("")){
		pstmt.setNull(6,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(6, sud_type);
	    }
	    if(sud_num.equals("")){
		pstmt.setNull(7,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(7, sud_num);
	    }
	    if(invalid_addr.equals("") || !location_id.equals(""))
		pstmt.setNull(8,Types.CHAR);
	    else
		pstmt.setString(8, "Y"); // Y
	    if(location_id.equals("")){
		pstmt.setNull(9,Types.INTEGER);
	    }
	    else {
		pstmt.setString(9, location_id);
	    }
	    if(street_address_id.equals("")){
		pstmt.setNull(10,Types.INTEGER);
	    }
	    else {
		pstmt.setString(10, street_address_id);
	    }
	    if(subunit_id.equals("")){
		pstmt.setNull(11,Types.INTEGER);
	    }
	    else {
		pstmt.setString(11, subunit_id);
	    }
	    if(streetAddress.equals("")){
		streetAddress = getAddress();
	    }
	    if(streetAddress.equals("")){
		pstmt.setNull(12,Types.VARCHAR);
	    }
	    else {
		pstmt.setString(12, streetAddress);
	    }
	    pstmt.setString(13, id);	// 13
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back = " Could not update address:  "+qq+" ";
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    public synchronized boolean hasMasterAddressInfo(String url2){

	// testing url
	boolean ret = false;
	String url = "";
	DefaultHttpClient httpclient = new DefaultHttpClient();		
	try{
	    url = url2+"/locations/verify.php?format=json&address="+java.net.URLEncoder.encode(getAddress(), "UTF-8")+"+Bloomington";
	    // System.err.println("url "+url);
	    // System.err.println(getAddress());
	    HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            logger.debug("----------------------------------------");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");
	    JSONObject jObj = new JSONObject(responseBody);
	    if(jObj.has("location_id")){
		location_id = jObj.getString("location_id");
		// System.err.println(" location_id: "+location_id);
		street_address_id = jObj.getString("street_address_id");
		// System.err.println(" street_address_id: "+street_address_id);
		if(!jObj.isNull("subunit_id")){
		    subunit_id = jObj.getString("subunit_id");
		    // System.err.println(" subunit_id: "+subunit_id);
		}
		if(!jObj.isNull("addressString")){
		    streetAddress = jObj.getString("addressString");
		    // System.err.println(" Address: "+streetAddress);
		}
		invalid_addr = "";
		ret = true;
	    }
			
	}
	catch(Exception ex){
	    logger.error(" "+ex+":"+getAddress());
	}
	finally{
	    // 
	    // shut down the connection manager to ensure
	    // immediate deallocation of all system resources
	    //
	    httpclient.getConnectionManager().shutdown();
	}
	return ret;
    }	
    public boolean isValid(String url){
	//
	// if we have street_address_id, this means
	// we already found the address, no need to search
	// else we ask master address for street_address_id
	//
	if(street_address_id.equals("")){
	    return hasMasterAddressInfo(url);
	}
	return true;
    }
	
    /**
     * old isValid method
     */
    /*
      boolean isValid2(String url2){
      boolean ret = false;
      String url = "";
      DefaultHttpClient httpclient = new DefaultHttpClient();		
      try{
      url = url2+"/addresses/verify.php?format=txt&address="+java.net.URLEncoder.encode(getAddress(), "UTF-8")+"+Bloomington";			
      HttpGet httpget = new HttpGet(url);
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String responseBody = httpclient.execute(httpget, responseHandler);
      logger.debug("----------------------------------------");
      logger.debug(responseBody);
      logger.debug("----------------------------------------");
      if(responseBody.indexOf("Invalid") == -1) ret = true;
      }
      catch(Exception ex){
      logger.error(" "+ex+":"+getAddress());
      }
      finally{
      // 
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      //
      httpclient.getConnectionManager().shutdown();
      }
      return ret;
      }
    */
    //
    public void findLatLong2(String url){
	//
	String urlStr = url+"/addresses/verify.php?";
	try{
	    if(!street_num.equals(""))
		urlStr +="street_number="+java.net.URLEncoder.encode(street_num, "UTF-8");
	    if(!street_dir.equals(""))
		urlStr +="&direction="+street_dir;
	    if(!street_name.equals(""))
		urlStr +="&street_name="+java.net.URLEncoder.encode(street_name, "UTF-8");
	    if(!street_type.equals(""))
		urlStr +="&street_type="+street_type;
	    if(!post_dir.equals(""))
		urlStr +="&postDirection="+post_dir;
	    if(!sud_type.equals(""))
		urlStr +="&subunitType="+sud_type;
	    if(!sud_num.equals(""))
		urlStr +="&subunitIdentifier="+java.net.URLEncoder.encode(sud_num, "UTF-8");
	    urlStr +="&city=Bloomington";
	    urlStr +="&format=xml";
	    if(debug){
		logger.debug(urlStr);
	    }
	    HandleAddress ha = new HandleAddress(urlStr, debug);
	    List<Address> lls = ha.getAddresses();
	    if(lls != null && lls.size() > 0){
		Address addr = lls.get(0); // we need one address
		lat = addr.getLat();
		lng = addr.getLng();
	    }
	}catch(Exception ex){
	    logger.error(" "+ex);
	}
    }
    public void getMasterAddrInfo(String url2){
	//
	String url = url2+"/addresses/verify.php?format=xml&address=";
	String addrStr = "";		
	try{
	    addrStr = java.net.URLEncoder.encode(getAddress(), "UTF-8");
	    addrStr += "+Bloomington";			
	    url += addrStr;			
	    if(debug){
		logger.debug(url);
	    }
	    HandleAddress ha = new HandleAddress(url, debug);
	    List<Address> vec = ha.getAddresses();
	    if(vec != null && vec.size() > 0){
		Address addr = vec.get(0); // we take one 
		lat = addr.getLat();
		lng = addr.getLng();
	    }
	}catch(Exception ex){
	    logger.error(" "+ex);
	}
    }
    public String findRentalByTagNumber(){

	String qq = "", back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	qq = " select distinct ra.registr_id "+	
	    " from eng.mast_address ma, eng.address_location al, "+
	    " eng.mast_address_location_status als, "+
	    " gis.building_address_location bal, "+
	    " gis.buildings b, eng.mast_street_names msn, "+
	    " eng.mast_address_status mas, "+addressTable+" ra "+
	    //
	    " where b.building_id = bal.building_id "+
	    " and bal.location_id = al.location_id "+
	    " and al.street_address_id = ma.street_address_id "+
	    " and al.location_id = als.location_id "+
	    " and ma.street_id = msn.street_id "+
	    " and ma.street_address_id = mas.street_address_id "+
	    " and msn.street_name_type = 'STREET' "+
	    " and al.subunit_id is null "+
	    // using location_id and street_address_id
	    // instead of the address details
	    " and ra.location_id=al.location_id "+
	    " and al.street_address_id = ra.street_address_id "+
	    " and mas.status_code = '1' "+
	    " and als.status_code = '1' "+
	    " and b.gis_tag = ?";
	/*
	//
	// the old query with address components
	//
	" and ma.street_number = ra.street_num "+
	" and (TRIM(msn.street_direction_code) = "+
	" ra.street_dir or ra.street_dir is null) "+
	" and UPPER(msn.street_name) = ra.street_name "+
	" and UPPER(msn.street_type_suffix_code) = ra.street_type "+
	" and (TRIM(msn.post_direction_suffix_code) = "+
	" ra.post_dir or msn.post_direction_suffix_code is null) "+
	" and mas.status_code = '1' "+
	" and als.status_code = '1' "+
	" and b.gis_tag = ?";
	*/
	/*
	  qq = " select distinct ra.registr_id "+	
	  " from eng.mast_address ma, eng.address_location al, "+
	  " eng.mast_address_location_status als, "+
	  " gis.building_address_location bal, "+
	  " gis.buildings b, eng.mast_street_names msn, "+
	  " eng.mast_address_status mas, ce.address2 ra "+
	  //
	  " where b.building_id = bal.building_id "+
	  " and bal.location_id = al.location_id "+
	  " and al.street_address_id = ma.street_address_id "+
	  " and al.location_id = als.location_id "+
	  " and ma.street_id = msn.street_id "+
	  " and ma.street_address_id = mas.street_address_id "+
	  " and msn.street_name_type = 'STREET' "+
	  " and al.subunit_id is null "+
	  " and ma.street_number = ra.street_num "+
	  " and (TRIM(msn.street_direction_code) = "+
	  " ra.street_dir or ra.street_dir is null) "+
	  " and UPPER(msn.street_name) = ra.street_name "+
	  " and UPPER(msn.street_type_suffix_code) = ra.street_type "+
	  " and (TRIM(msn.post_direction_suffix_code) = "+
	  " ra.post_dir or msn.post_direction_suffix_code is null) "+
	  " and mas.status_code = '1' "+
	  " and als.status_code = '1' "+
	  " and b.gis_tag = ?";
	  // very old
	  qq = " select distinct r.id "+	
	  " from eng.mast_address ma, eng.address_location al, "+
	  " eng.mast_address_location_status als, "+
	  " gis.building_address_location bal, "+
	  " gis.buildings b, eng.mast_street_names msn, "+
	  " eng.mast_address_status mas, ce.address2 ra, ce.registr r "+
	  " where b.building_id = bal.building_id "+
	  " and bal.location_id = al.location_id "+
	  " and al.street_address_id = ma.street_address_id "+
	  " and al.location_id = als.location_id "+
	  " and ma.street_id = msn.street_id "+
	  " and ma.street_address_id = mas.street_address_id "+
	  " and msn.street_name_type = 'STREET' "+
	  " and al.subunit_id is null "+
	  " and ma.street_number = ra.street_num "+
	  " and (TRIM(msn.street_direction_code) = "+
	  " ra.street_dir or ra.street_dir is null) "+
	  " and UPPER(msn.street_name) = ra.street_name "+
	  " and UPPER(msn.street_type_suffix_code) = ra.street_type "+
	  " and (TRIM(msn.post_direction_suffix_code) = "+
	  " ra.post_dir or msn.post_direction_suffix_code is null) "+
	  " and ra.registr_id = r.id "+
	  " and mas.status_code = '1' "+
	  " and als.status_code = '1' "+
	  " and b.gis_tag = ?";
	*/
	if(tag.equals("")){
	    back = "No tag number specified";
	    return back;
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }
	    if(debug)
		logger.debug(qq);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,tag);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		rid = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    logger.error(" "+ex+" "+qq);
	    back += ex+" "+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    //
    public String toString(){

	return getAddress();
    }
		
}

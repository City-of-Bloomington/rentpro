package rental.utils;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.sql.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.naming.directory.*;
import javax.naming.*;
import java.net.URL;
import java.security.MessageDigest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;


public class Helper{

    final static long serialVersionUID = 370L;
    public final static String dirArr[] = {"","E","N","S","W"};

    public final static String sudIdArr[] = { "",
	"A", "B", "D", "F", 
	"L", "R", "S", "U"};

    public final static String sudArr[] = { "",
	"Apartment", "Building", "Dept", "Floor",
	"Lot", "Room", "Suite",  "Unit"};

    public final static String strIdArr[] = {"",
	"AVE", "BND", "BLVD", "BOW","BYP",
	"CTR","CIR", "CT", "CRST",
	"DR",  "EXPY", "LN", "PIKE",
	"PKY", "PL",  "RD", "RDG","RUN", "ST", "TER", 
	"TPKE", "TURN","VLY","WAY"};
    //
    public final static String strArr[] = {"",
	"Avenue","Bend", "Boulevard", "Bow","Bypass",
	"Center","Circle", "Court","Crest",
	"Drive", "Expressway", "Lane", "Pike" ,
	"Parkway" ,"Place" ,"Road" ,"Ridge","Run","Street", "Terrace",
	"Turnpike","Turn","Valley","Way"};
    public final static String phoneTypes[] = {"Home","Work","Cell","Emergency"};
    public final static String bedsArr[] = {"Eff","1","2","3","4","5","6","7","8","9","10"};
    public final static String buildTypes[] = {"","Single-Family","Multi-Family"};	
    //
    // xhtmlHeader.inc
    public final static String xhtmlHeaderInc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"+
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    //
    // Non static variables
    //
    static int rc_cnt = 0;


		
    static Logger logger = LogManager.getLogger(Helper.class);
    boolean debug = false;
    String [] deptIdArr = null;
    String [] deptArr = null;
    public final static Map<String, String>       mimeTypes = new HashMap<>();
    static {
        mimeTypes.put("image/gif",       "gif");
        mimeTypes.put("image/jpeg",      "jpg");
        mimeTypes.put("image/png",       "png");
        mimeTypes.put("image/tiff",      "tiff");
        mimeTypes.put("image/bmp",       "bmp");
        mimeTypes.put("text/plain",      "txt");
        mimeTypes.put("audio/x-wav",     "wav");
        mimeTypes.put("application/pdf", "pdf");
        mimeTypes.put("audio/midi",      "mid");
        mimeTypes.put("video/mpeg",      "mpeg");
        mimeTypes.put("video/mp4",       "mp4");
        mimeTypes.put("video/x-ms-asf",  "asf");
        mimeTypes.put("video/x-ms-wmv",  "wmv");
        mimeTypes.put("video/x-msvideo", "avi");
        mimeTypes.put("text/html",       "html");

        mimeTypes.put("application/mp4",               "mp4");
        mimeTypes.put("application/x-shockwave-flash", "swf");
        mimeTypes.put("application/msword",            "doc");
        mimeTypes.put("application/xml",               "xml");
        mimeTypes.put("application/vnd.ms-excel",      "xls");
        mimeTypes.put("application/vnd.ms-powerpoint", "ppt");
        mimeTypes.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
    }		

    //
    // basic constructor
    public Helper(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
    public final static String findFileType(File file)
    {
        String fileType = "";
        try {
            String pp   = file.getAbsolutePath();
            Path   path = Paths.get(pp);
            fileType = Files.probeContentType(path);
            System.err.println(fileType);
        }
        catch (Exception ex) {
            System.err.println(" fle type excep " + ex);
        }
        return fileType;
    }
    public final static String getFileExtension(File file)
    {
        String ext = "";
        try {
            // name does not include path
            String name     = file.getName();
            String pp       = file.getAbsolutePath();
            Path   path     = Paths.get(pp);
            String fileType = Files.probeContentType(path);
            if (fileType != null) {
                // application/pdf
                if (fileType.endsWith("pdf")) {
                    ext = "pdf";
                }
                // image/jpeg
                else if (fileType.endsWith("jpeg")) {
                    ext = "jpg";
                }
                // image/gif
                else if (fileType.endsWith("gif")) {
                    ext = "gif";
                }
                // image/bmp
                else if (fileType.endsWith("bmp")) {
                    ext = "bmp";
                }
                // application/msword
                else if (fileType.endsWith("msword")) {
                    ext = "doc";
                }
                // application/vnd.ms-excel
                else if (fileType.endsWith("excel")) {
                    ext = "csv";
                }
                // application/vnd.openxmlformats-officedocument.wordprocessingml.document
                else if (fileType.endsWith(".document")) {
                    ext = "docx";
                }
                // text/plain
                else if (fileType.endsWith("plain")) {
                    ext = "txt";
                }
                // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
                else if (fileType.endsWith(".sheet")) {
                    ext = "xlsx";
                }
                // audio/wav
                else if (fileType.endsWith("wav")) {
                    ext = "wav";
                }
                // text/xml
                else if (fileType.endsWith("xml")) {
                    ext = "xml";
                }
                else if (fileType.endsWith("html")) {
                    ext = "html";
                }
                // video/mng
                else if (fileType.endsWith("mng")) {
                    ext = "mng";
                }
                else if (fileType.endsWith("mpeg")) {
                    ext = "mpg";
                }
                // video/mp4
                else if (fileType.endsWith("mp4")) {
                    ext = "mp4";
                }
                else if (fileType.endsWith("avi")) {
                    ext = "avi";
                }
                else if (fileType.endsWith("mov")) {
                    ext = "mov";
                }
                // quick time video
                else if (fileType.endsWith("quicktime")) {
                    ext = "qt";
                }
                else if (fileType.endsWith("wmv")) {
                    ext = "wmv";
                }
                else if (fileType.endsWith("asf")) {
                    ext = "asf";
                }
                // flash video
                else if (fileType.endsWith("flash")) {
                    ext = "swf";
                }
                else if (fileType.startsWith("image")) {
                    ext = "jpg";
                } // if non of the above we check the file name
                else if (name.indexOf(".") > -1) {
                    ext = name.substring(name.lastIndexOf(".") + 1);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println(" fle ext excep " + e);
        }
        return ext;
    }		
    //
    public final static String getHashCodeOf(String buffer){

	String key = "Apps Secret Key "+getToday();
	byte[] out = performDigest(buffer.getBytes(),buffer.getBytes());
	String ret = bytesToHex(out);
	return ret;
	// System.err.println(ret);

    }
    //
    public final static String readUrl(String strUrl) throws Exception {

	URL url = new URL(strUrl);
        InputStream is = url.openStream();
        BufferedReader in = new BufferedReader (new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = in.readLine()) != null) {
	    // 
            sb.append(line);
	    sb.append("\n");
        }
        return sb.toString();
    }
    //
    public final static byte[] performDigest(byte[] buffer, byte[] key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return md5.digest(key);
        } catch (Exception e) {
	    System.err.println(e);
	    logger.error(e);
        }
        return null;
    }
    //
    public final static String bytesToHex(byte in[]) {
	byte ch = 0x00;
	int i = 0; 
	if (in == null || in.length <= 0)
	    return null;
	String pseudo[] = {"0", "1", "2",
	    "3", "4", "5", "6", "7", "8",
	    "9", "A", "B", "C", "D", "E",
	    "F"};
	StringBuffer out = new StringBuffer(in.length * 2);
	while (i < in.length) {
			
	    ch = (byte) (in[i] & 0xF0); // Strip off high nibble
	    ch = (byte) (ch >>> 4);
	    // shift the bits down
	    ch = (byte) (ch & 0x0F);    
	    // must do this is high order bit is on!
	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    i++;
	}
	String rslt = new String(out);
	return rslt;
    }    
    //
    /**
     * Adds escape character before certain characters
     *
     */
    public final static String escapeIt(String s) {
	StringBuffer safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	while (c < len) {                           
	    if (safe.charAt(c) == '\'' ||
		safe.charAt(c) == '"') {
		safe.insert(c, '\\');
		c += 2;
		len = safe.length();
	    }
	    else {
		c++;
	    }
	}
	return safe.toString();
    }
    public final static boolean isDirection(String val){
	if(val != null && !val.equals("")){
	    String str = "."+val.toUpperCase()+".";
	    String allDirs = ".N.S.W.E.NORTH.SOUTH.EAST.WEST.";
	    if(allDirs.indexOf(str) > -1)
		return true;
	}
	return false;
    }
    /**
     * getting rid of comma and & in names
     */
    public final static String strClean(String str){
	String ret="";
		
	if(str != null && !str.trim().equals("")){
	    str = str.trim();
	    int len = str.length();			
	    int ind = str.indexOf(",");
	    if(ind > -1){
		if(ind == 0){ // in the beginning
		    ret = str.substring(1);
		}
		else{
		    ret = str.substring(0,ind);
		}
	    }
	    else{
		ret = str;
				
	    }
	    if(!ret.equals("")){
		str = ret;
	    }
	    //			
	    len = str.length();			
	    ind = str.indexOf("&");
	    if(ind > -1){
		if(ind == 0){ // in the beginning
		    ret = str.substring(1);
		}
		else{
		    ret = str.substring(0,ind);
		}
	    }
	    else{
		ret = str;
				
	    }
	    if(ret.toUpperCase().equals("AND")){
		ret = "";
	    }
	}
	return ret;
    }
    //
    // users are used to enter comma in numbers such as xx,xxx.xx
    // as we can not save this in the DB as a valid number
    // so we remove it
    //
    public final static String cleanNumber(String s) {

	if(s == null) return null;
	String ret = "";
	int len = s.length();
	int c = 0;
	int ind = s.indexOf(",");
	if(ind > -1){
	    ret = s.substring(0,ind);
	    if(ind < len)
		ret += s.substring(ind+1);
	}
	else
	    ret = s;
	return ret;
    }
    /**
     * replaces the special chars that has certain meaning in html or xml
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final static String replaceSpecialChars(String s) {
	//
	char ch[] ={'\'','\"','>','<','&',':','/'};
	String entity[] = {"&#39;","&34;","&62;","&#60;","&#38;","&#58;","&#47;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String encodeForXML(String s) {
	return splitter(s);
    }
    /*
     * We want to skip the characters that are already encoded
     */
	  
    public final static String splitter(String str){
	String str2 = "";
	if(str.length() > 0){
	    String [] words = str.split(" ");
	    for(String word:words){
		if(word.startsWith("&") && word.endsWith(";")){
		    if(!str2.equals("")) str2 += " ";
		    str2 += word;
		}
		else{
		    if(!str2.equals("")) str2 += " ";
		    str2 += replaceSpecialChars(word);
		}
	    }
	}
	return str2;
    }
    /**
     * adds another apostrify to the string if there is any next to it
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final static String doubleApostrify(String s) {
	StringBuffer apostrophe_safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	while (c < len) {                           
	    if (apostrophe_safe.charAt(c) == '\'') {
		apostrophe_safe.insert(c, '\'');
		c += 2;
		len = apostrophe_safe.length();
	    }
	    else {
		c++;
	    }
	}
	return apostrophe_safe.toString();
    }
    public final static String replaceSlash(String s) {
	StringBuffer str_safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	while (c < len) {                           
	    if (str_safe.charAt(c) == '\\') {
		str_safe.setCharAt(c, '/');
		c++;
	    }
	    else {
		c++;
	    }
	}
	return str_safe.toString();
    }	
    /**
     * Connect to Oracle database
     *
     * @param dbStr database connect string
     * @param dbUser database user string
     * @param dbPass database password string
     */
    public final static Connection databaseConnect(String dbStr, 
						   String dbUser, 
						   String dbPass) {
	Connection con=null;
	try {
	    Class.forName("oracle.jdbc.driver.OracleDriver");
	    con = DriverManager.getConnection(dbStr,
					      dbUser,
					      dbPass);
	    String qq = "set schema rental";
	    PreparedStatement pstmt = con.prepareStatement(qq);
	    pstmt.executeUpdate();
	}
	catch (Exception sqle) {
	    System.err.println(sqle);
	    logger.error(sqle);
	}
	return con;
    }
    public final static Connection getConnection(){
		
	Connection con = null;
	boolean noPass = true;
	int trials = 0;
	do{
	    trials++;
	    try{
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/oracle_rent");
		con = ds.getConnection();
		String qq = "ALTER SESSION SET CURRENT_SCHEMA=rental";
		PreparedStatement pstmt = con.prepareStatement(qq);
		pstmt.executeUpdate();
		rc_cnt++;
		logger.debug("Got con "+rc_cnt+" at "+trials);
		noPass = false;
		pstmt.close();
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}while(noPass && trials < 4);
		
	return con;
    }
    public final static Connection getLegalConnection(){
		
	Connection con = null;
	boolean noPass = true;
	int trials = 0;
	do{
	    trials++;
	    try{
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL_legals");
		con = ds.getConnection();
		rc_cnt++;
		logger.debug("Got con "+rc_cnt+" at "+trials);
		noPass = false;
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}while(noPass && trials < 4);
		
	return con;
    }	
    //
    public final static Connection databaseConnect(String dbStr){
	//
	Connection con = null;
	try {
	    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	    //
	    // mysql
	    con = DriverManager.getConnection(dbStr);
	    if(con == null){
		System.err.println("Could not connect");
	    }
	}
	catch (Exception sqle){
	    System.err.println(sqle);
	    logger.error(sqle);
	}
	return con;
    }	
    /**
     * Disconnect the database and related statements and result sets
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Connection con,
						Statement stmt,
						ResultSet rs) {
	try {
	    if(rs != null){
		try{
		    rs.close();
		    rs = null;
		}catch(Exception ex){};
	    }
	    if(stmt != null){
		try{
		    stmt.close();
		    stmt = null;
		}catch(Exception ex){};				
	    }
	    if(con != null){
		try{
		    con.close();
		    con = null;
		}catch(Exception ex){};
	    }
	    logger.debug("Closed con "+rc_cnt);
	    rc_cnt--;
	    if(rc_cnt < 0) rc_cnt = 0;
	}
	catch (Exception e) {
	    System.err.println(e);
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }
    public final static void doClean(Connection con,
				     PreparedStatement stmt,
				     ResultSet rs) {
	try {
	    if(rs != null){
		try{
		    rs.close();
		    rs = null;
		}catch(Exception ex){};
	    }
	    if(stmt != null){
		try{
		    stmt.close();
		    stmt = null;
		}catch(Exception ex){};				
	    }
	    if(con != null){
		try{
		    con.close();
		    con = null;
		}catch(Exception ex){};
	    }
	    logger.debug("Closed con "+rc_cnt);
	    rc_cnt--;
	}
	catch (Exception e) {
	    System.err.println(e);
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }
    public final static void databaseDisconnect(Connection con,
						ResultSet rs,
						Statement... stmt) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null){
		for(Statement one:stmt){
		    if(one != null)
			one.close();
		    one = null;
		}
	    }
	    if(con != null) con.close();
	    con = null;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { }
		rs = null;
	    }
	    if (stmt != null) {
		try {
		    for(Statement one:stmt){										
			if(one != null)
			    one.close(); 
			one = null;
		    }
		} catch (SQLException e) { }
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { }
		con = null;
	    }
	}
    }
    public final static String getFileExtensionFromName(String name)
    {
        String ext = "";
        try {
            if (name.indexOf(".") > -1) {
                ext = name.substring(name.lastIndexOf(".") + 1);
            }
        }
        catch (Exception ex) {

        }
        return ext;
    }				
    /**
     * Write the number in bbbb.bb format needed for currency.
     * = toFixed(2)
     * @param dd the input double number
     * @returns the formated number as string
     */
    public final static String formatNumber(double dd){
	//
	String str = ""+dd;
	String ret="";
	int l = str.length();
	int i = str.indexOf('.');
	int r = i+3;  // required length to keep only two decimal
	try{
	    if(i > -1 && r<l){
		ret = str.substring(0,r);
	    }
	    else{
		ret = str;
	    }
	}catch(Exception ex){
	    logger.error(ex);
	}
	return ret;
    }

    /**
     * format a number in the two decimal point format.
     *
     * useful for currency ouput.
     * @param that the input string that contains a number.
     * @return the formated number
     */
    public final static String formatNumber(String that){

	int ind = that.indexOf(".");
	int len = that.length();
	String str = "";
	if(that.equals("")){
	    str = "0.00";
	}
	else if(ind == -1){  // whole integer
	    str = that + ".00";
	}
	else if(len-ind == 2){  // one decimal
	    str = that + "0";
	}
	else if(len - ind > 3){ // more than two
	    try{
		str = that.substring(0,ind+3);
	    }
	    catch(Exception ex){
		System.err.println(ex);
		logger.error(ex);
	    }
	}
	else str = that;

	return str;
    }
    public final static void printFiles(PrintWriter out,
				 String url,
				 List<RentalFile> files){
	if(files == null || files.size() == 0) return;
	out.println("<table>");
	out.println("<tr><th>Date</th><th>Added by</th><th>File Name</th><th>Notes<th></tr>");
	for(RentalFile one:files){
	    out.println("<tr><td>"+one.getDate()+"</td>"+
			"<td>"+one.getAddedBy()+"</td>"+
			"<td><a href=\""+url+"RentalFileServ?action=download&id="+one.getId()+"\">"+one.getOldName()+"</a></td>"+
			"<td>"+one.getNotes()+"</td></tr>");
	}
	out.println("</table>");
    }				

    //
    // main page banner
    //
    public final static String banner(String url){

	String banner = "<head>\n"+
	    "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n"+
	    "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n"+
	    "<link rel=\"SHORTCUT ICON\" href=\"/favicon.ico\" />\n"+
	    "<style type=\"text/css\" media=\"screen\">\n"+
	    "@import url(\"/skins/default/skin.css\");\n"+
	    "</style>\n"+
	    "<style type=\"text/css\" media=\"print\">@import url(\"/skins/default/print.css\");</style>\n"+
	    "<script src=\"/functions.js\" type=\"text/javascript\"></script>\n"+
	    "<title>RiskTrack - City of Bloomington, Indiana</title>\n"+
	    "</head>\n"+
	    "<body>\n"+
	    "<div id=\"banner\">\n"+
	    "<h1><a href=\""+url+"RiskTrack\">RiskTrack</a></h1><h2>City of Bloomington, Indiana</h2>\n"+
	    "</div>";
	return banner;
    }
    //
    public final static String menuBar(String url, boolean logged){
	String menu = "<div class=\"menuBar\">\n"+
	    "<a href=\""+url+"RiskTrack\">Home</a>\n"+
	    "<a href=\""+url+"RiskTrack/status.html\">Status</a>\n";
	if(logged){
	    menu += "<a href=\""+url+"RiskTrack/Logout\">Logout</a>\n";
	}
	menu += "</div>\n";
	return menu;
    }
    //
    // Non static methods and variables
    //
    public String[] getDeptIdArr(){
	return deptIdArr;
    }
    public String[] getDeptArr(){
	return deptArr;
    }
    //
    public final static String getToday(){

	String day="",month="",year="";
	Calendar current_cal = Calendar.getInstance();
	int mm =  (current_cal.get(Calendar.MONTH)+1);
	int dd =   current_cal.get(Calendar.DATE);
	year = ""+ current_cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static String getNextWeekStartDate(){

	String day="",month="",year="";
	Calendar cal = Calendar.getInstance();
	int dayInt = cal.get(Calendar.DAY_OF_WEEK); // Sunday 1; Saturday 7
	cal.add(Calendar.DATE, 9-dayInt); // next Monday
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.get(Calendar.DATE);
	year = ""+ cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static String getNextWeekEndDate(){

	String day="",month="",year="";
	Calendar cal = Calendar.getInstance();
	int dayInt = cal.get(Calendar.DAY_OF_WEEK); // Sunday 1; Saturday 7
	cal.add(Calendar.DATE, 14-dayInt); // next Friday
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.get(Calendar.DATE);
	year = ""+ cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }		
		
    // yy could be positive or negative
    public final static String getDateYearsFromNow(int yy){

	String day="",month="",year="";
	Calendar current_cal = Calendar.getInstance();
	current_cal.add(Calendar.YEAR, yy);
	int mm =  (current_cal.get(Calendar.MONTH)+1);
	int dd =   current_cal.get(Calendar.DATE);
	year = ""+ current_cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }		
    /**
     * given certain date in mm/dd/yyyy find the date by adding 
     * certain number of days from this date
     */
    public final static String addDaysToDate(String date, int days){
				
	String day="",month="",year="";
	int dd = 0, mm =0, yy=0;				
	String new_date = "";
	if(date == null || date.length() != 10){
	    return new_date;
	}

	try{
	    month = date.substring(0,2);
	    day = date.substring(3,5);
	    year = date.substring(6);
	    mm = Integer.parseInt(month);
	    dd = Integer.parseInt(day);
	    yy = Integer.parseInt(year);
	}
	catch(Exception ex){
	    System.err.println(ex);
	}
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.MONTH, mm - 1);
	cal.set(Calendar.DATE, dd);
	cal.set(Calendar.YEAR, yy);
	cal.add(Calendar.DATE, days);
	mm = cal.get(Calendar.MONTH)+1;
	dd = cal.get(Calendar.DATE);
	yy = cal.get(Calendar.YEAR);				
	if(mm < 10){
	    new_date = "0";
	}
	new_date += mm+"/";
	if(dd < 10){
	    new_date += "0";
	}
	new_date += ""+dd+"/"+yy;
	return new_date;
    }				
    //
    // initial cap a word
    //
    public final static String initCapWord(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){
		ret = str_in.substring(0,1).toUpperCase()+
		    str_in.substring(1).toLowerCase();
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	// System.err.println("initcap "+str_in+" "+ret);
	return ret;
    }
    //
    // init cap a phrase
    //
    public final static String initCap(String str_in){
	String ret = "";
	if(str_in != null){
	    if(str_in.indexOf(" ") > -1){
		String[] str = str_in.split("\\s"); // any space character
		for(int i=0;i<str.length;i++){
		    if(i > 0) ret += " ";
		    ret += initCapWord(str[i]);
		}
	    }
	    else
		ret = initCapWord(str_in);// it is only one word
	}
	return ret;
    }
    /**
     * compose the date its original components.
     *
     * @param mm string the month item
     * @param dd string the day item
     * @param yy string the year item
     * @param yyyy string the current year 
     * @param c_mm string the current month
     * @retuns ret the composed date
     */
    public final static String composeDate(String mm,String dd, String yy, 
				    int c_mm,int yyyy){

	String ret="";
	// if only year is set
	if(!yy.equals(""))
	    if(mm.equals("")) mm = "1";
	//
	// if day is set we use current month
	if(!dd.equals(""))
	    if(mm.equals(""))mm = ""+c_mm; 
	if(!mm.equals("")){
	    ret = mm+"/";
	    if(!dd.equals("")){
		ret += dd;    PrintWriter os = null;
	    }
	    else{
		ret += "1"; // first day of month
	    }
	    ret +="/";
	    if(!yy.equals("")){
		ret += yy;
	    }
	    else{
		ret += ""+yyyy; // default to this year
	    }
	}
	return ret;
    }
	
    public final static String replaceQuote(String e) {

	String str = "";
	int len = e.length();
	int c = 0;
	while (c < len) {    
	    if(e.charAt(c) == '"') str += "&#34;";
	    else str += e.charAt(c);
	    c++;
	}
	return str;

    }

    public final static String escapeQuote(String s) {
	StringBuffer apostrophe_safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	while (c < len) {                           
	    if (apostrophe_safe.charAt(c) == '"') {
		apostrophe_safe.insert(c, '\\');
		c += 2;
		len = apostrophe_safe.length();
	    }
	    else {
		c++;
	    }
	}
	return apostrophe_safe.toString();
    }
    //
    // check multiple emails separated by comma
    //
    public final static boolean isValidEmail(String email){
	//		
	if(email == null)
	    return false;
	if(email.indexOf("@") == -1){ // it is part of regex anyway
	    return false;
	}
	if(email.indexOf(",") > 0){
	    //
	    // multiple emails
	    //
	    String strs[] = email.split(",");
	    for(String str: strs){
		if(!isValidOneEmail(str)) return false;
	    }
	}
	else{
	    if(!isValidOneEmail(email)) return false;
	}
	return true;
    }
	
    public final static boolean isValidOneEmail(String email){
	//		
	//Initialize reg ex for email.  
	String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
	CharSequence inputStr = email;  
	//Make the comparison case-insensitive.  
	Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
	Matcher matcher = pattern.matcher(inputStr);  
	if(matcher.matches()){  
	    return true;  
	}  
	return false;  
    }

    public final static String fillTo40(String str){
	int len = str.length();
	int maxSize = 45;
	String str2 = str;
	if(len > 40) str2 += "  ";
	else {
	    for(int i=len; i<maxSize; i++)
		str2 += " ";
	}
	return str2;
    }
    //
    // these files has the following
    // 0k, 1k, 2k or 2001,2002,
    public final static String getFileDir(String file_name){
	//
	String ret = null;
	String [] tap = {"0k\\","1k\\","2k\\","3k\\","4k\\","5k\\",
	    "6k\\","7k\\","8k\\","9k\\"};
	int id = -1, value=-1;
	if(!(file_name == null || file_name.equals(""))){
	    if(file_name.indexOf("\\") > -1 || file_name.indexOf("/") > -1){
		// do not do anything
	    }
	    else{
		String str = file_name.substring(0,4);
		try{
		    id = Integer.parseInt(file_name.substring(0,1));
		    for(int i=4;i>0;i--){
			try{
			    value = Integer.parseInt(file_name.substring(0,i));
			    break;  
			}catch(Exception ex){  // just ignore
			    // System.err.println(ex);
			}
		    }
		}catch(Exception ex){
		    logger.error(ex);
		}
		if(id > -1 && value > 0){
		    if(value < 1000) id = 0;
		    // otherwise the id represents the group
		    ret = tap[id];
		}
	    }
	}
	return  ret;

    }
    public final static void writeStructUnits(PrintWriter out,
				       StructureList structs){
	if(structs == null || structs.size() < 1) return;
		
	String all="", cell="";
	if(structs.hasUninspections()){
	    cell = "<td><b>Uninspected?</b></td>";
	}
	out.println("<table border width='80%'>"+
		    "<tr>"+
		    "<td><b>Structure</b></td>"+
		    "<td><b>Units </b></td>"+						
		    "<td><b>Bedrooms</b> (per Unit)</td>"+
		    "<td><b>Efficiency</b> (per Unit)</td>"+
		    "<td><b>Occupancy Load</b>(per Unit)</td>"+
		    cell+
		    "</tr>");		
				
	int totalUnits = 0, totalBeds = 0, totalLoad = 0;
	int totalEffs = 0;
	for(Structure strc: structs){
	    UnitList units = strc.getUnits();
	    for(Unit unit:units){
		all += "<tr><td align=center>"+strc.getIdentifier()+"</td>";
		all += "<td align=right>"+unit.getUnits()+" ";
		all += unit.isSleepRoom()?"HR":"";
		all += "</td>";
		totalUnits += unit.getUnits();
		int beds = unit.getBedrooms();
		if(beds > 0){
		    all += "<td align=right>"+beds;
		    all += (unit.isSleepRoom()?" SR":"");
		    all += "</td>";
		    all += "<td>&nbsp;</td>";
		    totalBeds += unit.getUnits() * beds;
		}
		else{
		    all += "<td>&nbsp;</td>";
		    all += "<td>1</td>";
		    totalEffs += unit.getUnits();
		}
		all += "<td align=right>"+unit.getOccLoad()+"</td>";
		if(!cell.equals("")){
		    String uninsp = "&nbsp;";
		    if(unit.isUninspected()){
			uninsp = "(Uninspected)";
		    }
		    all += "<td>"+uninsp+"</td>";					
		}
		totalLoad += unit.getUnits() * unit.getOccLoad();
				
		all += "</tr>";
	    }
	}
	if(totalUnits > 1){
	    all += "<tr><td>Total</td>"+
		"<td align=right>"+totalUnits+"</td>"+
		"<td align=right>"+totalBeds+"</td>"+
		"<td align=right>"+(totalEffs == 0 ? "&nbsp;": totalEffs )+"</td>"+
		"<td align=right>"+totalLoad+"</td>";
	    if(!cell.equals("")){
		all += "<td>&nbsp;</td>";
	    }
	    all += "</tr>";
	}
	all += "</table>";
	out.println(all);
    }
    public final static void writeInspections(PrintWriter out,
				       String url,
				       InspectionList inspects){
	String [] titles = 
	    {"ID",
	     "Inspection Date",
	     "Type",
	     "Compliance Date",
	     "Inspected by", 
	     "Violations",
	     "Smoke Detectors",
	     "Life Safety",
	     "Time Reporting Status"};
	if(inspects != null && inspects.size() > 0){
	    int totalViol=0, totalSmk=0, totalSfty=0;			
	    out.println("<table border>");
	    out.println("<tr>");
	    for(int i=0; i<titles.length; i++){
		out.println("<th>"+titles[i]+"</th>");
	    }
	    out.println("</tr>");			
	    for(Inspection one:inspects){
		out.println("<tr>");
		String viols = one.getViolations();
		String smokes = one.getSmookDetectors();
		String safety = one.getLifeSafety();
		String type = one.getInspectionTypeName();
		out.println("<td><a href=\""+url+"InspectFileServ?id="+one.getId()+"\">"+one.getId()+"</a></td>");
		out.println("<td>"+one.getInspectionDate()+"</td>");
		out.println("<td>"+type+"</td>");
		out.println("<td>"+one.getComplianceDate()+"&nbsp;</td>");
		out.println("<td>"+((one.getInspector() != null)? one.getInspector():"")+"&nbsp;</td>");
		out.println("<td align=\"right\">"+viols+"&nbsp;</td>");
		out.println("<td align=\"right\">"+one.getSmookDetectors()+"&nbsp;</td>");
		out.println("<td align=\"right\">"+one.getLifeSafety()+"&nbsp;</td>");
		out.println("<td>"+one.getTimeStatus()+"</td>");
		//
		// we count only the recycle violations
		//
		if(type.equals("Cycle")){
		    if(!viols.equals("")){
			try{
			    totalViol += Integer.parseInt(viols);
			}catch(Exception ex){
			    logger.error(" invalid int "+viols);
			}
		    }
		    if(!smokes.equals("")){
			try{
			    totalSmk += Integer.parseInt(smokes);
			}catch(Exception ex){
			    logger.error(" invalid int "+smokes);
			}
		    }
		    if(!safety.equals("")){
			try{
			    totalSfty += Integer.parseInt(safety);
			}catch(Exception ex){
			    logger.error(" invalid int "+safety);
			}
		    }
		}				
		out.println("</tr>");
	    }
	    if(totalViol > 0 || totalSmk > 0 || totalSfty > 0){
		out.println("<tr><td colspan=\"5\">Total</td>"+
			    "<td align=\"right\">"+totalViol+"&nbsp;</td>"+
			    "<td align=\"right\">"+totalSmk+"&nbsp;</td>"+
			    "<td align=\"right\">"+totalSfty+"&nbsp;</td>"+
			    "</tr>");
	    }
	    out.println("</table>");
	}
    }
    public final static void writeVariances(PrintWriter out,
				     String url,
				     VarianceList variances){

	if(variances == null || variances.size() == 0) return;
	//
	String [] titles = {"ID","Date","Variance"};		
	out.println("<table border>");
	out.println("<tr>");
	for(int i=0; i<titles.length; i++){
	    out.println("<th>"+titles[i]+"</th>");
	}
	out.println("</tr>");
	for(Variance var:variances){
	    out.println("<tr>");
	    String date = var.getDate();
	    String vid = var.getId();
	    if(date == null || date.equals("")) date = "&nbsp;";
			
	    out.println("<td><a href=\""+url+
			"VarianceServ?"+
			"id="+var.getRegistrId()+"&vid="+var.getId()+"&action=zoom\">"+var.getId()+"</a></td>");
	    out.println("<td>"+date+"</td>");
	    out.println("<td>"+var.getText()+"</td>");
	    out.println("</tr>");
	}
	out.println("</table>");
    }
    public final static void writeRentalNotes(PrintWriter out,
				       List<RentalNote> ones){

	if(ones == null || ones.size() == 0) return;
	//
	out.println("<table border=\"1\" width=\"100%\">");
	out.println("<tr><th>Date</th><th>Notes</th><th>By</th></tr>");
	for(RentalNote one:ones){
	    String date = one.getNote_date();
	    String by = "";
	    if(one.getUser() !=null){
		by = one.getUser().getFullName();
	    }
	    out.println("<tr>"+
			"<td>&nbsp;"+one.getNote_date()+"</td>"+
			"<td>"+one.getNotes()+"</td>"+
			"<td>"+by+"&nbsp;</td></tr>");
	}
	out.println("</table>");
    }		

    public final static void writeWebCss(PrintWriter out,
					    String url
					    ){
	if(out != null && !url.isEmpty()){    
	    out.println("<link rel=\"stylesheet\" href=\""+url+"css/jquery-ui.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n");
	    out.println("<link rel=\"stylesheet\" href=\""+url+"css/jquery.ui.theme.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n");
	    out.println("<link rel=\"stylesheet\" href=\""+url+"css/menu_style.css\" />");
	    out.println("<style>");
	    out.println(".ui-datepicker-prev .ui-icon, .ui-datepicker-next .ui-icon { ");
	    out.println(" background-image: url(\"js/images/ui-icons_228ef1_256x240.png\");");
	    out.println("} "); 
	    out.println("</style>");
	    
	}
    }
    public final static void writeTopMenu(PrintWriter out,
					  String url
					  ){
	if(out != null && !url.isEmpty()){
	    out.println("<center>");
	    out.println("<h3>HAND Dept - RentPro</h3>");
	    out.println("<div id=\"div_top\">");
	    out.println("<ul id=\"ul_top\">");
	    /**
	    out.println("<li><a href=\""+url+"Rental\">New Rental</a></li>");
	    out.println("<li><a href=\""+url+"OwnerServ\">New Owner</a></li>");
	    out.println("<li><a href=\""+url+"Mailer\">Owners Emailer</a></li>")
	    out.println("<li><a href=\""+url+"ExpireMailer\">Permit Expire Emailer</a></li>");;
	    out.println("<li><a href=\""+url+"CycleEmailList\">Cycle Owner Emailer</a></li>");
	    out.println("<li><a href=\""+url+"PostCard\">Post Cards</a></li>");	    
	     */
	    out.println("<li><a href=\""+url+"Browse\">Rentals</a></li>");
	    out.println("<li><a href=\""+url+"OwnerBrowse\">Owners</a></li>");
	    out.println("<li><a href=\""+url+"ReportMenu\">Reports</a></li>");
	    out.println("<li><a href=\""+url+"PeriodReport\">Stats Reports</a></li>");	    
	    out.println("<li><a href=\""+url+"logout\">Logout</a></li>");
	    
	    out.println("</ul>");
	    out.println("</div>");
	    out.println("</center>");	    
	}
    }
    public final static void writeWebFooter(PrintWriter out,
					    String url
					    ){
	if(out != null && !url.isEmpty()){
	    out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-3.6.1.min.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-ui.min-1.13.2.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\""+url+"js/jqAreYouSure.js\"></script>");
	    String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
	    out.println("<script>");
	    out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	    out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	    out.println("$(function() { ");
	    out.println("$('#form_id').areYouSure(); ");
	    out.println("}); ");			
	    out.println("</script>");
	}
    }
    public final static void writeBills(PrintWriter out,
					String url,
					BillList bills
					){
	String titles[] = { "Bill ID",
			    "Issue Date",
			    "Due Date",
			    "Total",
			    "Balance",
			    "Status"};
	String rTitles[] = { "Receipt No.",
			     "Received Date",
			     "Received Sum"};
	if(bills != null && bills.size() > 0){
	    for(Bill bl:bills){
		out.println("<table border>");
		out.println("<caption>Bill</caption>");
		out.println("<tr>");
		for(int i=0; i<titles.length;i++){
		    out.println("<th>"+titles[i]+"</th>");
		}
		out.println("</tr>");
		
		out.println("<tr>");
		String str = bl.getBid();
		String id = bl.getId();
		if(!str.equals("")){
		    out.println("<td><a href="+url+"BillServ?"+
				"action=zoom&bid="+str+
				"&id="+id+
				">"+str+
				"</a></td>");
		}
		else{
		    out.println("<td>&nbsp;</td>");
		}
		str = bl.getIssue_date();
		out.println("<td>&nbsp;"+str+"</td>");
		str = bl.getDue_date();
		out.println("<td>&nbsp;"+str+"</td>");
		str = ""+bl.getTotal();
		out.println("<td>&nbsp;"+str+"</td>");
		str = ""+bl.getBalance();
		out.println("<td>&nbsp;"+str+"</td>");		
		str = bl.getStatus();
		out.println("<td>&nbsp;"+str+"</td>");
		out.println("</tr>");
		out.println("</table>");
		ReceiptList rlist = bl.getReceipts();
		if(rlist != null && rlist.size() > 0){
		    out.println("<table border>");
		    out.println("<caption>Receipts</caption>");
		    out.println("<tr>");
		    for(int i=0; i<rTitles.length;i++){
			out.println("<th>"+rTitles[i]+"</th>");
		    }
		    out.println("</tr>");
		    for(Receipt rc:rlist){
			out.println("<tr>");
			str = rc.getRid();
			if(!str.equals("")){
			    out.println("<td><a href="+url+"ReceiptServ?"+
					"action=zoom&rid="+str+">"+str+
					"</a></td>");
			}
			else
			    out.println("<td>&nbsp;</td>");
			str = rc.getRec_date();	
			out.println("<td>&nbsp;"+str+"</td>");
			str = ""+rc.getRec_sum();	
			out.println("<td>&nbsp;"+str+"</td>");
			out.println("</tr>");
		    }
		    out.println("</table>");
		}
	    }
	}
    }
    
}























































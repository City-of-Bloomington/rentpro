package rental.web;
import java.net.URI;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.utils.*;

public class TopServlet extends HttpServlet {
    static String url = "",
	checkAddrUrl="https://apps.bloomington.in.gov/master_address";
    static String url2="", // url+"images/rent/" for images
	image_url=""; // /srv/webapps/rentpro/images/rent/ for pdf
    // need to change to /srv/data/rentpro/images/
    //              we also need /srv/data/rentpro/mail/
    //                           /srv/data/rentpro/attachments/
    static String googleKey="";
    static String cookieName="", cookieValue="";
    static String server_path="", // /mnt/mounts/rental/
	file_path="", // J:&#92;departments&#92;HAND&#92;common&#92;Rental&#92;
	attachment_path="", // /srv/data/rentpro/attachments/
	pc_path=""; // local hard drive to download files 
    static long maxImageSize=10000000; //10 meg
    static long maxDocSize=10000000;
    static String legalContact="wheelech";
    static String emailStr = "bloomington.in.gov";
    
    static String caseTypeServiceUrl="https://apps.bloomington.in.gov/legaltrack/TypeService?deptId=3";
    static boolean debug = false, test=false, activeMail=false;
    static Configuration config = null;
    static Logger logger = LogManager.getLogger(TopServlet.class);
    static ServletContext context = null;
    static String endpoint_logout_uri = ""; 
    public void init(ServletConfig conf){
	try{
	    context = conf.getServletContext();
	    url = context.getInitParameter("url");
	    url2 = context.getInitParameter("url2");	    
	    String str = context.getInitParameter("debug");
	    if(str != null && str.equals("true")) debug = true;
	    str = context.getInitParameter("checkAddrUrl");
	    if(str != null) checkAddrUrl = str;
	    str = context.getInitParameter("server_path");
	    if(str != null) server_path = str;
	    str = context.getInitParameter("image_url");
	    if(str != null) image_url = str;
	    str = context.getInitParameter("file_path");
	    if(str != null) file_path = str;
	    pc_path = file_path;
	    str = context.getInitParameter("attachment_path");
	    if(str != null) attachment_path = str;
	    str = context.getInitParameter("legalContact");
	    if(str != null) legalContact = str;
	    str = context.getInitParameter("maxImageSize");
	    if(str != null){
		try{
		    maxImageSize = Long.parseLong(str);
		}catch(Exception ex){}
	    }
	    str = context.getInitParameter("maxDocSize");
	    if(str != null){
		try{
		    maxDocSize = Long.parseLong(str);
		}catch(Exception ex){}
	    }
	    str = context.getInitParameter("caseTypeServiceUrl");
	    if(str != null)
		caseTypeServiceUrl = str;	    
	    str = context.getInitParameter("cookieName");
	    if(str != null)
		cookieName = str;
	    str = context.getInitParameter("cookieValue");
	    if(str != null)
		cookieValue = str;
	    str  = context.getInitParameter("endpoint_logout_uri");
	    if(str != null)
		endpoint_logout_uri = str;
	    String username = context.getInitParameter("adfs_username");
	    String auth_end_point = context.getInitParameter("auth_end_point");
	    String token_end_point = context.getInitParameter("token_end_point");
	    String callback_uri = context.getInitParameter("callback_uri");
	    String client_id = context.getInitParameter("client_id");
	    String client_secret = context.getInitParameter("client_secret");
	    String scope = context.getInitParameter("scope");
	    String discovery_uri = context.getInitParameter("discovery_uri");
	    config = new
		Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, username);
	    // System.err.println(config.toString());
	}catch(Exception ex){
	    System.err.println(" top init "+ex);
	    logger.error(" "+ex);
	}
    }

}

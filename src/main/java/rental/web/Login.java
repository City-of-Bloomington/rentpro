package rental.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;
// change to /Login for CAS
@WebServlet(urlPatterns = {"/CasLogin"})
public class Login extends TopServlet{
    final static long serialVersionUID = 560L;
    static Logger logger = LogManager.getLogger(Login.class);
    /**
     * Generates the login form for all users.
     *
     * @param req the request 
     * @param res the response
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	String username = "", ipAddress = "", message="", id="", rid="";
	String source = "", action="";
	boolean found = false;
	
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Enumeration<String> values = req.getParameterNames();
	String name= "";
	String value = "";
	while (values.hasMoreElements()) {
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	    if (name.equals("id"))
		id = value;
	    else if (name.equals("source"))
		source = value;
	    else if (name.equals("action"))
		action = value;
	    else if (name.equals("rid"))
		rid = value;			
			
	}
	String userid = null;
	HttpSession session = null;
	session = req.getSession(false);
	AttributePrincipal principal = null;				
	if (req.getUserPrincipal() != null) {
	    principal = (AttributePrincipal) req.getUserPrincipal();
	    userid = principal.getName();
	}
	if(userid == null || userid.isEmpty()){
	    userid = req.getRemoteUser();
	}
	if(userid != null){
	    User user = getUser(userid);
	    if(session != null && user != null && user.userExists()){
		session.setAttribute("user",user);
		String url2 = url+"IntroPage?"; // default
		if(!source.equals(""))
		    url2 =	url+source+"?";
		if(!action.equals(""))
		    url2 += "&action="+action;
		if(!id.equals("")){
		    url2 += "&id="+id;
		}
		if(!rid.equals("")){
		    url2 += "&rid="+rid;
		}				
		out.println("<head><title></title><META HTTP-EQUIV=\""+
			    "refresh\" CONTENT=\"0; URL=" + url2 +
			    "\"></head>");
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		return;
	    }
	}
	else{
	    logger.error("Can not get userid");
	}
	out.println("<head><title>Rental</title></head>");
	out.println("<body><center>");
	out.println("<p><font color=red>Unauthorized access, check with IT"+
		    ", or try again later.</font></p>");
	out.println("</center>");
	out.println("</body>");
	out.println("</html>");
	out.flush();
    }
    //
    void setCookie(HttpServletRequest req, 
		   HttpServletResponse res){ 
	Cookie cookie = null;
	boolean found = false;
	Cookie[] cookies = req.getCookies();
	if(cookies != null){
	    for(int i=0;i<cookies.length;i++){
		String name = cookies[i].getName();
		if(name.equals(cookieName)){
		    found = true;
		}
	    }
	}
	//
	// if not found create one with 0 time to live;
	//
	if(!found){
	    cookie = new Cookie(cookieName,cookieValue);
	    res.addCookie(cookie);
	}
    }
    /**
     * Procesesses the login and check for authontication.
     * 
     * @param username
     */		
    User getUser(String username){

	boolean success = true;
	User user = null;
	User user2 = new User(debug, username);
	String back = user2.doSelect();
	if(back.equals("")){
	    user = user2;
	}
	return user;
    }


}























































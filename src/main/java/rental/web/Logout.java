package rental.web;
import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(urlPatterns = {"/Logout"})
public class Logout extends TopServlet{

    final static long serialVersionUID = 570L;
    /**
     * Deletes the sesion info.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Enumeration<String> values = req.getParameterNames();
	String name= "";
	String value = "";
	String username = "";
	String message = "";
	//    
	while (values.hasMoreElements()) {
	    name = values.nextElement().trim();
	    value = req.getParameter(name);
	    if (name.equals("message"))
		message = value;
	}	
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    session.invalidate();
	}
	res.sendRedirect(endpoint_logout_uri);
	return;
    }

}























































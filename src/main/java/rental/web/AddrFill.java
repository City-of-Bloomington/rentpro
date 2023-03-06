package rental.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/AddrFill"})
public class AddrFill extends TopServlet{

    final static long serialVersionUID = 30L;
    static Logger logger = LogManager.getLogger(AddrFill.class);
    /**
     * Generates the request form.
     * It also handles the view, add and update operations (except deletion).
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     * @see Rental
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	boolean success = true;
	String action="", message="";
	String id="";
	String all = "", all2="";
	AddressList addrList = null;
	List<Address> addresses = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("action")){
		action = value;
	    }
	}
	if(!action.equals("")){	
	    //
	    addrList = new AddressList(debug);
	    addrList.setInvalid(); // we want invalid ones only
	    //
	    // delete the previous run data
	    //
	    // addrList.deletePrev();
	    // addrList.setLimit("500");
	    String back = addrList.lookFor();
	    int j1=1, j2=1;
	    if(!back.equals("")){
		message += " Could not retreive data "+back;
		success = false;
	    }
	    else{
		String addrUrl = "http://apps.bloomington.in.gov/master_address";
		addresses = addrList.getAddresses();
		for(Address one:addresses){
		    try{
			Thread.sleep(200);
		    }catch(Exception ex){
			System.err.println(ex);
		    }
		    if(one.isLegit() && one.hasMasterAddressInfo(addrUrl)){
			back = one.addMastAddrInfo();
			if(back.equals("")){
			    id = one.getId();							
			    String location_id = one.getLocation_id();
			    String street_address_id = one.getStreet_address_id();

			    String streetAddress = one.getStreetAddress();
			    all += "<tr><td>"+(j1++)+"</td><td>"+id+"</td><td>"+location_id+"</td><td>"+street_address_id+"</td><td>"+streetAddress+"</td></tr>";
			}
		    }
		    else{
			all2 += "<li> "+(j2++)+" "+one.getAddress()+"</li>";
		    }
		}
	    }
	}
	//
	out.println("<html><head><title>Address</title>");
	Helper.writeWebCss(out, url);	
	out.println(" </head><body>");
	//
	Helper.writeTopMenu(out, url);	
	out.println("<h2><center> Address Filling Process</h2>");
	if(!message.equals("")){
	    out.println("<h3>"+message+"</h3>");
	}
	out.println("<form name=myForm method=post>");
	//
	if(!action.equals("")){
	    out.println("<table><caption>Found Addresses </caption>");
	    out.println(all);
	    out.println(" </table>");
	    if(!all2.equals("")){
		out.println("<h3>Not found addresses</h3>");
		out.println("<ul>");
		out.println(all2);
		out.println("</ul>");				
	    }
	}
	out.println("<input type=submit name=action value=Submit>");
	out.println("</form>");
	out.print("</body></html>");
	out.close();
    }

}























































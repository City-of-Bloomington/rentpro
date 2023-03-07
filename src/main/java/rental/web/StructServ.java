package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/StructServ"})
public class StructServ extends TopServlet{

    String bgcolor = "";
    final static long serialVersionUID = 920L;
    static Logger logger = LogManager.getLogger(StructServ.class);
    /**
     * Generates the Item form and processes view, add, update and delete
     * operations.
     *
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String rid="";
	String struct[] = null;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value, action="", tag="", message="";
	boolean success = true;
	int structures = 0, oldStructures = 0;
	Enumeration<String> values = req.getParameterNames();

	String [] vals;
	String [] delItem = null;

	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("rid")){
		rid = value;
	    }
	    else if (name.equals("delItem")){
		delItem = vals;  // array
	    }
	    else if (name.equals("tag")){
		tag = value;
	    }
	    else if (name.equals("struct")){
		struct = vals;
	    }
	    else if (name.equals("structures")){
		try{
		    structures = Integer.parseInt(value);
		}catch(Exception ex){}
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	// 
	// Prepare the database connection
	//
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=StructServ&rid="+rid;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=StructServ&rid="+rid;
	    res.sendRedirect(str);
	    return; 
	}
	con = Helper.getConnection();
	if(con == null){
	    success = false;
	    message += " could not connect to database";
	    logger.error(message);
	}
	if(success){
	    try{
		stmt = con.createStatement();
		String qq = " select r.structures from registr r "+
		    " where r.id = "+rid;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    oldStructures = rs.getInt(1);
		}
		// System.err.println(" structs "+oldStructures+" "+structures);
		if(structures > 0 && structures != oldStructures){
		    qq = "update registr set structures="+structures+
			" where id = "+rid;
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		}
		if(structures == 0){
		    structures = oldStructures;					
		}

	    }
	    catch(Exception ex){
		success = false;
		message += ex;
		logger.error(message);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);		
	    }
	}
	if(action.equals("Submit")){  // Save
	    //
	    if(struct != null){
		for(String str: struct){
		    if(!str.equals("")){
			Structure st = new Structure(debug);
			st.setRid(rid);
			st.setIdentifier(str);
			String back = st.doSave();
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
		    }
		}
	    }
	}
	if(!action.equals("") && user.canDelete()){
	    if(delItem != null){
		for(String str: delItem){
		    Structure st = new Structure(debug, str);
		    String back = st.doDelete();
		    if(!back.equals("")){
			message += back;
			success = false;
		    }
		}
	    }
	}
	out.println("<html><head><title>Rental Buildings</title>          ");
	Helper.writeWebCss(out, url);
	out.println("<script type='text/javascript'>                      ");
	out.println("  function validateForm(){	                          ");
	out.println("  return true;                                       ");
	out.println(" }	         			                              ");
	out.println("  function validateDelete(){	                      ");
	out.println("   var x = false;                                    ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   return x;                                         ");
	out.println("  }			        	                          ");
	out.println("  function firstFocus(){                             ");
	out.println("  }			       	                              ");
	out.println(" </script>				                              ");
    	out.println(" </head><body onload=\"firstFocus()\" >              ");
	Helper.writeTopMenu(out, url);	
	out.println("<center><h2>Structures/Buildings</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3><font color=green>"+message+"</font></h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	out.println("<input type=hidden name=rid value=\""+rid+"\">");
	out.println("<table border width=95%>");
	out.println("<tr><td align=center bgcolor="+bgcolor+">");
	//
	// table of old items
	out.println("<tr><td><table width=70%>");
	out.println("<tr><td align=center><b>"+
		    "Structures/Buildings in this Permit "+
		    "</b></td></tr>");
	out.println("<tr><td><b>Rental: </b>"+
		    "<a href="+url+"Rental?id="+rid+
		    "&action=zoom&tag="+tag+">"+rid+"</a></td></tr>");
	out.println("<tr><td><b>Structures count:</b>");
	out.println("<input type=text name=structures value='"+structures+"' "+
		    " size=3 maxlength=3 />Change this number if you want to add more buildings</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td align=left><table width=60%>");
	//
	// Check if there are units belonging to this permit
	// if so, list them first.
	//
	StructureList list = null;
	list = new StructureList(debug, rid);
	String back = list.find();
	if(!back.equals("")){
	    message += back;
	}
	int jj=1;
	if(list != null && list.size() > 0){
	    /**
	    out.println("<tr><td>Check to Delete</td>"+
			"<td><b>Structure</b></td>"+
			"<td><b>Identifier</b></td>"+
			"<td valign=bottom><b>Edit</b></td>"+
			"</tr>");
	    */
	    for(Structure st: list){
		out.println("<tr><td>");
		out.println("<input type=checkbox name=delItem "+
			    "value="+st.getId()+"></td>");
		out.println("<td>");
		out.println(jj+"</td>");
		out.println("<td id="+st.getId()+">");
		out.println(st.getIdentifier()+"</td>");
		if(user.canEdit() || user.isInspector()){
		    /**
		    out.println("<td><input type=button value=\"Edit\" "+
				"onclick=\"window.open('"+url+"EditStructure?"+
				"&action=zoom&id="+st.getId()+"','Structures',"+
				"'toolbar=0,location=0,"+
				"directories=0,status=0,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=450,height=300');\"></td>");
		    */
		}
		out.println("</tr>");
		jj++;
	    }
	}
	boolean showStar = false;
	if(tag.equals("")){
	    int maxStruct = structures;
	    if(list != null && list.size() > 0){
		maxStruct = structures - list.size();
		if(maxStruct > 0) showStar = true;
	    }
	    for(int i=0;i<maxStruct;i++){
		out.println("<tr><td>&nbsp;</td>");
		out.println("<td>"+(jj)+"</td>"+
			    "<td><input name=struct size=20 "+
			    " maxlength=30>*</td>");
		out.println(" <td></td></tr>");
		jj++;				
	    }
	}
	out.println("</table></td></tr>");
	out.println("<tr><td>");
	if(showStar){
	    out.println("* Identifiers could be 1,2, .., A, B, .., or building addresses <br />");
	}
	out.println("Note: you can specify Sleeping Room (SR) when you enter unit data </td></tr>");
	out.println("<tr><td valign=top align=center><table>");
	if(user.canEdit() || user.isInspector()){
	    /**
	    out.println("<tr><td valign=top><input "+
			"type=submit name=action value=Submit></td>");
	    if(list.size() > 0){ // make sure we have structures first
		out.println("<td><input type=button value='Add/Edit Units' "+
			    "onclick=\"document.location='"+url+
			    "UnitServ?rid="+rid+"'\" /></td>");
	    }
	    out.println("</tr>");
	    */
	}
	out.println("</table>");
	out.println("</td></tr></table>");
	out.println("</form>");
    	out.println("<br />");
	if(list.size() > 0){
	    if(list.getTotalUnits() > 0){
		out.println("<h4>Current Buildings & Units</h4>");
		Helper.writeStructUnits(out, list);
	    }
	}
    	out.println("<br />");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    
}
























































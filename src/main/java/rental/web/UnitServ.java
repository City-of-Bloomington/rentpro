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

@WebServlet(urlPatterns = {"/UnitServ"})
public class UnitServ extends TopServlet{

    String bgcolor = "";
    final static long serialVersionUID = 990L;
    static Logger logger = LogManager.getLogger(UnitServ.class);
	
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
	String rid="", structures="";
	String sid[] = {"","","",""};	// structure id
	String units[] = {"","","",""};		
	String beds[] = {"","","",""};
	String occLoad[] = {"","","",""};
	String sleepRoom[] = {"","","",""};
	String uninspected[] ={"","","",""};
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value, action="", tag="", message="";
	boolean success = true;

	Enumeration<String> values = req.getParameterNames();

	String [] vals;
	String [] delItem = null;
	StructureList structs = null;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("rid")){
		rid = value;  // rid
	    }
	    else if (name.equals("delItem")){
		delItem = vals;  // array
	    }
	    else if (name.equals("tag")){
		tag =value;
	    }
	    else if (name.equals("units0")){
		units[0] =value;
	    }
	    else if (name.equals("units1")){
		units[1] =value;
	    }
	    else if (name.equals("units2")){
		units[2] =value;
	    }
	    else if (name.equals("units3")){
		units[3] =value;
	    }
	    else if (name.equals("occLoad0")){
		occLoad[0] =value;
	    }
	    else if (name.equals("occLoad1")){
		occLoad[1] =value;
	    }
	    else if (name.equals("occLoad2")){
		occLoad[2] =value;
	    }
	    else if (name.equals("occLoad3")){
		occLoad[3] =value;
	    }
	    else if (name.equals("uninspected0")){
		uninspected[0] =value;
	    }
	    else if (name.equals("uninspected1")){
		uninspected[1] =value;
	    }
	    else if (name.equals("uninspected2")){
		uninspected[2] =value;
	    }
	    else if (name.equals("uninspected3")){
		uninspected[3] =value;
	    }			
	    else if (name.equals("beds0")){
		beds[0] =value;
	    }
	    else if (name.equals("beds1")){
		beds[1] =value;
	    }
	    else if (name.equals("beds2")){
		beds[2] =value;
	    }
	    else if (name.equals("beds3")){
		beds[3] =value;
	    }
	    else if (name.equals("sleepRoom0")){
		sleepRoom[0] =value;
	    }
	    else if (name.equals("sleepRoom1")){
		sleepRoom[1] =value;
	    }
	    else if (name.equals("sleepRoom2")){
		sleepRoom[2] =value;
	    }
	    else if (name.equals("sleepRoom3")){
		sleepRoom[3] =value;
	    }			
	    else if (name.equals("sid0")){
		sid[0] =value;
	    }
	    else if (name.equals("sid1")){
		sid[1] =value;
	    }
	    else if (name.equals("sid2")){
		sid[2] =value;
	    }
	    else if (name.equals("sid3")){
		sid[3] =value;
	    }
	    else if (name.equals("action")){ 
		// Delete, Process
		action = value;  
	    }
	}
	//
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
		if(structures.equals("")){
		    String qq = " select r.structures from registr r "+
			" where r.id = "+rid;
		    if(debug){
			logger.debug(qq);
			rs = stmt.executeQuery(qq);
			if(rs.next()){
			    String str = rs.getString(1);
			    if(str != null)
				structures = str;
			}
		    }
		}
	    }
	    else{
		success = false;
		message += " could not connect to database";
		logger.error(message);
	    }
			
	}
	catch(Exception ex){
	    success = false;
	    message += " could not connect to database "+ex;
	    logger.error(ex);
	}
	Helper.databaseDisconnect(con, stmt, rs);		
	if(action.equals("Submit")){  // Save
	    //
	    String qq="", query="", str="";
	    try{
		for(int i=0;i<4;i++){
		    if(!(sid[i].equals("") ||
			 units[i].equals("") || 
			 beds[i].equals("") ||
			 occLoad[i].equals(""))){
			Unit unit = new Unit(debug);
			unit.setSid(sid[i]);
			unit.setUnits(units[i]);
			unit.setBedrooms(beds[i]);
			unit.setOccLoad(occLoad[i]);
			unit.setSleepRoom(sleepRoom[i]);
			unit.setUninspected(uninspected[i]);
			String back = unit.doSave();
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
			else{
			    if(i == 0)  // one time is enough
				message = "Data Saved Successfully";
			}
		    }
		}
	    }
	    catch(Exception ex){
		success = false;
		message += "Error Saving records "+ex;
		logger.error(ex+":"+qq);
	    }
	}
	if(!action.equals("")){
	    String qq = "";
	    if(delItem != null){
		for(int i=0;i<delItem.length;i++){
		    if(!delItem[i].equals("")){
			Unit uu = new Unit(debug, delItem[i]);
			String back = uu.doDelete();
			if(back.equals("")){
			    if(i == 0)
				message = "Deleted Successfully";
			}
			else{
			    success = false;
			    message += "Error Deleting records "+back;
			    logger.error(back);
			}
		    }
		}
	    }
	}
	if(true){
	    structs = new StructureList(debug, rid);
	    String back = structs.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	//
	// Check if there are units belonging to this permit
	// if so, list them first.
	//
	out.println("<html><head><title>Units</title>                   ");
	Helper.writeWebCss(out, url);
	out.println("<script type='text/javascript'>                    ");
	out.println("  function validateForm(){	                        ");
	// 
	// checking numeric values
	//
	out.println("  with(document.myForm){ ");
	for(int i=0;i<4;i++){
	    out.println("if(occLoad"+i+".value.length > 0){               ");
	    out.println("   if(isNaN(occLoad"+i+".value)){                ");
	    out.println(" alert(\"Occupation Load Not a valid number \"); ");
	    out.println("   return false; }}                              ");
	    out.println("if(units"+i+".value.length > 0){                 ");
	    out.println("   if(isNaN(units"+i+".value)){                  ");
	    out.println(" alert(\"Units not a valid number \");           ");
	    out.println("   return false; }}                              ");
	    out.println("if(beds"+i+".value.length > 0){                  ");
	    out.println("   if(isNaN(beds"+i+".value)){                   ");
	    out.println(" alert(\"Bedrooms not a valid number \");        ");
	    out.println("   return false; }}                              ");
	}
	out.println("	}	         			                          ");
	out.println("  return true;                                       ");
	out.println(" }	         			                              ");
	out.println("  function validateDelete(){	                      ");
	out.println("   var x = false;                                    ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   return x;                                         ");
	out.println("  }			        	                          ");
	out.println("  function firstFocus(){                             ");
	out.println("  }			       	                              ");
	out.println("  function updateTotals(){                           ");
	out.println("  var ut = document.getElementById('iUnits').value;  ");
	out.println("  var bt = document.getElementById('iBeds').value;   ");
	out.println("  var lt = document.getElementById('iLoad').value;   ");
	out.println("  var et = document.getElementById('iEff').value;   ");	
	out.println("  for(var i=0;i<4;i++){                              "); 
	out.println("    var u = eval(document.getElementById('units'+i).value); ");

	out.println("    var ind = eval(document.getElementById('beds'+i).selectedIndex); ");
	out.println("    var b = eval(document.getElementById('beds'+i).options[ind].value); ");
	out.println("    var l = eval(document.getElementById('occLoad'+i).value); ");
	out.println("    if(u && u > 0 && !isNaN(u)){     ");
	out.println("       ut = ut*1+ 1*u;               ");
	out.println("       if(b && b > 0 && !isNaN(b)){  ");
	out.println("         bt = bt*1+b*u;              ");
	out.println("       }                             ");
	out.println("       if(l && l > 0 && !isNaN(l)){  ");
	out.println("         lt = lt*1+l*u;              ");
	out.println("       }                             ");
	out.println("       if(ind == 0) et = (et*1+u);   "); // add 1 to eff
	out.println("     }                               ");
	out.println("   }                                 ");
	out.println("  if(et == 0) et = '';          ");	
	out.println("  document.getElementById('tUnits').innerHTML=ut  ");
	out.println("  document.getElementById('tBeds').innerHTML=bt;  ");
	out.println("  document.getElementById('tLoad').innerHTML=lt;  ");
	out.println("  document.getElementById('tEff').innerHTML=et;  ");	
	out.println(" }			       	                               ");		
	out.println(" </script>				                           ");
    	out.println(" </head><body onload=\"firstFocus()\" >           ");
	Helper.writeTopMenu(out, url);
	out.println("<center><h2>Units Info</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3><font color=green>"+message+
			    "</font></h3>");
	    else
		out.println("<h3><font color=red>"+ message+
			    "</font></h3>");
	}
	//
	// Add/Edit record
	//
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	out.println("<input type=hidden name=rid value=\""+rid+"\">");
	out.println("<table border width=95%>");
	out.println("<tr><td align=center bgcolor="+bgcolor+">");
	//
	// table of old items
	out.println("<tr><td><table width=70%>");
	out.println("<tr><td align=center><b>"+
		    "Units Associated with this "+
		    "Permit</b></td></tr>");
	out.println("<tr><td><b>Rental: </b>"+
		    "<a href="+url+"Rental?id="+rid+
		    "&action=zoom&tag="+tag+">Back to "+
		    " Rental: "+rid+"</a></td></tr>");
	out.println("<tr><td><b>Structures (Buildings):</b> ");
	out.println(structures+"</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td><font color=green>");
	out.println("* The suffix RH and SR will be added automatically when 'Rooming House' flag is checked.<br />");
	out.println("Note: You may add group of four at a time.");
	out.println("</font></td></tr>");
	out.println("<tr><td align=center><table width=100%>");
	//
	// Table of units
	//
	int jj=1, totalUnits = 0, totalBeds = 0,totalLoad = 0, totalEffs = 0;
	out.println("<tr><td></td><td></td>"+
		    "<td><b>Structure</b></td>"+
		    "<td><b>Units Count</b></td>"+
		    "<td><b>Rooming House?</b></td>"+					
		    "<td><b>Bedrooms Count</b></td>"+
		    "<td><b>Efficiency</b></td>"+
		    "<td><b>Occupancy Load</b></td>"+
		    "<td><b>Uninspected?</b></td>"+
		    "<td valign=bottom><b>Edit</b></td>"+
		    "</tr>");		
	//		if(list != null && list.size() > 0){
	if(structs != null){
	    for(Structure strc: structs){
		UnitList list = strc.getUnits();
		for(Unit unit: list){
		    out.println("<tr>");
		    out.println("<td>");
		    out.println(jj+"</td>");
		    out.println("<td>");
		    out.println("<input type=checkbox name=delItem "+
				"value="+unit.getId()+"></td>");
		    out.println("<td>");
		    out.println(strc.getIdentifier());
		    out.println("</td>");
		    out.println("<td><span id="+unit.getId()+1+">");
		    out.println(unit.getUnits());
		    totalUnits += unit.getUnits();
		    out.println("</span>");
		    out.println("<span id="+unit.getId()+0+">");
		    out.println(unit.isSleepRoom()?"RH":"");
		    out.println("</span></td>");
		    out.println("<td><span id="+unit.getId()+"00>");
		    out.println(unit.isSleepRoom()?"Yes":"&nbsp;"); // RH
		    out.println("</span></td>");
		    int bedCnt = unit.getBedrooms();
		    if(bedCnt == 0){  // efficiency 
			out.println("<td><span id="+unit.getId()+2+">");
			out.println("&nbsp;</span>");
			out.println("<span id="+unit.getId()+4+">");
			out.println("</span>");	 // SR
			out.println("</td>");
			out.println("<td><span id="+unit.getId()+3+">1</span></td>");					
		    }
		    else{
			out.println("<td><span id="+unit.getId()+2+">");	
			out.println(bedCnt+"</span>");
			out.println("<span id="+unit.getId()+4+">");
			out.println(unit.isSleepRoom()?"SR":"");
			out.println("</span>");					
			out.println("</td>");
			out.println("<td><span id="+unit.getId()+3+">&nbsp;</spna></td>");	
		    }
		    totalBeds += unit.getUnits() * unit.getBedrooms();
		    totalEffs += unit.getUnits() * ((unit.getBedrooms() == 0) ?  1 : 0);
		    // out.println("<td>&nbsp;</td>"); // SR not used here
		    out.println("<td><span id="+unit.getId()+5+">");
		    out.println(unit.getOccLoad());
		    totalLoad += unit.getUnits() * unit.getOccLoad();
		    out.println("</span></td>");
		    out.println("<td><span id="+unit.getId()+6+">");
		    if(unit.isUninspected()){
			out.print("(Uninspected)");
		    }
		    else{
			out.print("&nbsp;");
		    }
		    out.println("</span></td>");					
		    if(user.canEdit() || user.isInspector()){
			out.println("<td><input type=button "+
				    "value=\"Edit Unit\" "+
				    "onclick=\"window.open('"+url+
				    "EditUnit?sid="+strc.getId()+
				    "&action=zoom&id="+unit.getId()+"','Group',"+
				    "'toolbar=0,location=0,"+
				    "directories=0,status=0,menubar=1,"+
				    "scrollbars=1,top=100,left=100,"+
				    "resizable=1,width=450,height=400');\">");
			out.println("</td>");
		    }
		    out.println("</tr>");
		    jj++;
		}
	    }
	}
	if(tag.equals("")){
	    for(int i=0;i<4;i++){
		out.println("<tr>");
		out.println("<td>"+jj+"</td>");
		out.println("<td>&nbsp;</td>");
		out.println("<td>");
		out.println("<select name=\"sid"+i+"\">");
		for(Structure st: structs){
		    out.println("<option value='"+st.getId()+"'>"+st.getIdentifier()+"</option>");
		}
		out.println("</select></td>");
		// Units
		out.println("<td><input name=\"units"+i+"\" size=\"3\" "+
			    " id=\"units"+i+"\" value=\"\" maxlength=\"3\" "+
			    " onchange=\"updateTotals();\" />*</td>");
		out.println("<td>");
		out.println("<input type=checkbox name=\"sleepRoom"+i+"\" "+
			    " value=\"y\">");		
		out.println("Yes</td>");
		//
		// bedrooms
		out.println("<td><select name=\"beds"+i+"\""+
			    " id=\"beds"+i+"\" onchange=\"updateTotals();\" >");
		for(int j=0;j<Helper.bedsArr.length;j++){
		    out.println("<option value=\""+j+"\">"+Helper.bedsArr[j]+
				"</option>");
		}
		out.println("</select></td>");
		out.println("<td>&nbsp;</td>"); // eff
		//
		// Occupation load
		out.println("<td><input name=occLoad"+i+
			    " size=3 id=occLoad"+i+" value='' "+
			    " onchange='updateTotals();' "+ 
			    " maxlength=3 /></td>");

		out.println("<td>");
		out.println("<input type=checkbox name=\"uninspected"+i+"\" "+
			    " value=\"y\">");		
		out.println("Yes</td>");
		out.println("<td>&nbsp;</td>");
		out.println("</tr>");
		jj++;				
	    }
	    out.println("<tr><td></td><td></td><td align=left><b>Total</b></td>");
	    out.println("<td id=tUnits>"+totalUnits+"</td><td>&nbsp;</td>");
	    out.println("<td id=tBeds>"+totalBeds+"</td>");
	    out.println("<td id=tEff>"+(totalEffs == 0 ? "&nbsp;":totalEffs)+"</td>");
	    // out.println("<td>&nbsp;</td>"); // SR
	    out.println("<td id=tLoad>"+totalLoad+"</td>");
	    out.println("</tr>");
	}
	out.println("</table></td></tr>");
	out.println("<tr><td valign=top align=right><table>");
	/**
	if(user.canEdit() || user.isInspector()){
	    out.println("<tr><td valign=top><input "+
			"type=\"submit\" name=\"action\" value=\"Submit\" /></td></tr>");
	}
	*/
	out.println("<input type=hidden name=iUnits id=iUnits value='"+totalUnits+"' />");
	out.println("<input type=hidden name=iBeds id=iBeds value='"+totalBeds+"' />");
	out.println("<input type=hidden name=iLoad id=iLoad value='"+totalLoad+"' />");
	out.println("<input type=hidden name=iEff id=iEff value='"+totalEffs+"' />");
		
	out.println("</form>");
	out.println("</table></center>");
	out.println("</td></tr></table>");
    	out.println("<br>");
    	out.println("<br>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    
}
























































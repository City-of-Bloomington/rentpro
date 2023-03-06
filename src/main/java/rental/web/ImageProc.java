package rental.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;


@SuppressWarnings("unchecked")
@WebServlet(urlPatterns = {"/ImageProc"})
public class ImageProc extends TopServlet{

    final static long serialVersionUID = 390L;
    int maxImageSize = 2000000, maxDocSize=5000000;
    String [] inspIdArr = null;
    String [] inspArr = null;
    String [] typeIdArr = null;
    String [] typeArr = null;
    static Logger logger = LogManager.getLogger(ImageProc.class);
    String currentDay = "";
    int seq = 100;
    public static String[] allmonths = {"\n","JAN","FEB","MAR",
	"APR","MAY","JUN",
	"JUL","AUG","SEP",
	"OCT","NOV","DEC"};

    static final String MONTH_SELECT = "<option>JAN\n" + 
	"<option>FEB\n" + 
	"<option>MAR\n" + 
	"<option>APR\n" + 
	"<option>MAY\n" + 
	"<option>JUN\n" + 
	"<option>JUL\n" + 
	"<option>AUG\n" + 
	"<option>SEP\n" + 
	"<option>OCT\n" + 
	"<option>NOV\n" + 
	"<option>DEC\n" + 
	"</select>";

    /**
     * Generates the main upload or view image form.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
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
	boolean success = true,
	    sizeLimitExceeded = false;
	String saveDirectory ="",image_path="";
	String newFile = "";
	String action="", image_date="", image_file="";
	String id="", rid="", notes="", action2="";
	
	String message = "";
	int maxMemorySize = 5000000; // 5 MB , int of bytes
	int maxRequestSize = 5000000; // 5 MB
	String [] vals;
	User user = null;
	HttpSession session = null;
	long sizeInBytes = 0;
	int access = 0, oldSeq = seq;
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 
	String today = Helper.getToday();
	String yy = today.substring(8,10); // last two digits of the year
	String month = today.substring(0,2);
	String day = today.substring(3,5);
	//
	if(url.indexOf("10.50.103") == -1){
	    // image_path = "/srv/webapps/rentpro/images/rent/";
	    saveDirectory =image_path+yy+"/";
	}
	else{
	    saveDirectory =image_path+yy+"\\\\";
	}
	// System.err.println("image_path "+image_path);
	// 
	// we have to make sure that this directory exits
	// if not we create it
	//
	File myDir = new File(saveDirectory);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	if(!currentDay.equals(today)){
	    currentDay = today;
	    seq = 100;
	    oldSeq = seq;
	}
	else {
	    oldSeq = seq;
	    seq++;
	}
	// newFile = "rent"+month+day+seq+".jpg";
	newFile = "rent"+month+day+seq; // no extension 
	// boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	//
	// Create a factory for disk-based file items
	// DiskFileItemFactory factory = new DiskFileItemFactory();
	//
	// Set factory constraints
	// factory.setSizeThreshold(maxMemorySize);
	//
	// if not set will use system temp directory
	// factory.setRepository(fileDirectory); 
	//
	// Create a new file upload handler
	// ServletFileUpload upload = new ServletFileUpload(factory);
	// ServletFileUpload upload = new ServletFileUpload();
	//
	// Set overall request size constraint
	// upload.setSizeMax(maxRequestSize);
	//
	String content_type = req.getContentType();		
	String ext = "";
	List<FileItem> items = null;
	boolean actionSet = false;
	MediaFile media = new MediaFile(debug);
	try{
	    if(content_type != null && content_type.startsWith("multipart")){
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//
		// Set factory constraints
		factory.setSizeThreshold(maxMemorySize);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxRequestSize);
		items = upload.parseRequest(req);
		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = iter.next();
		    if (item.isFormField()) {
			//
			// process form fields
			//
			name = item.getFieldName();
			value = item.getString();
			if (name.equals("id")){  
			    id = value;
			    media.setId(value);
			}
			else if (name.equals("notes")) {
			    media.setNotes(value);
			}
			else if (name.equals("rid")){ 
			    rid =value;
			    media.setRid(value);
			}
			else if (name.equals("image_file")) {
			    image_file =value.replace('+',' ');
			    media.setName(image_file);
			}
			else if (name.equals("image_date")) {
			    image_date =value;
			    media.setDate(value);
			}
			else if(name.equals("action")){
			    // we want the first (which is the last in the array);
			    if(!actionSet){
				actionSet = true;
				if(value.equals("New")) action = "";
				else action = value;
			    }
			}
		    }
		    else {
			//
			// process uploaded item/items
			//
			String fieldName = item.getFieldName();
			String contentType = item.getContentType();
			// boolean isInMemory = item.isInMemory();
			// sizeInBytes = item.getSize();
			String fileName = item.getName();
			String filename = "";
			if (fileName != null) {
			    filename = FilenameUtils.getName(fileName);
			    String extent = "";
			    ext = "jpg";
			    if(filename.indexOf(".") > -1){
				extent = filename.substring(filename.lastIndexOf(".")).toLowerCase();
				if(extent.startsWith(".jp"))
				    ext = "jpg";
				else if(extent.startsWith(".gif"))
				    ext = "gif";
				else if(extent.startsWith(".png"))
				    ext = "png";
				else if(extent.startsWith(".pdf"))
				    ext = "pdf";
				else if(extent.startsWith(".txt"))
				    ext = "txt";
				else if(extent.startsWith(".xcl"))
				    ext = "xcl";
				else if(extent.startsWith(".doc"))
				    ext = "doc";
				else if(extent.startsWith(".htm"))
				    ext = "html";
				else
				    ext = "jpg";
			    }
			    //newFile = newFile + ext;
			    //
			    // create the file on the hard drive and save it
			    //
			    if("jpg_png_gif".indexOf(ext) > -1){
				if(sizeInBytes > maxImageSize) 
				    sizeLimitExceeded = true;
			    }
			    else {
				if(sizeInBytes > maxDocSize) 
				    sizeLimitExceeded = true;
			    }
			    if(sizeLimitExceeded){
				message = " File Uploaded exceeds size limits "+
				    sizeInBytes;	
			    }
			    else{
				newFile = media.checkAndSetFileName(newFile);
				newFile = newFile+"."+ext;
				//System.err.println("save dir "+saveDirectory);
				File file = new File(saveDirectory, newFile);
				item.write(file);
				media.setName(newFile);
			    }
			}
		    }
		} // end while
	    }
	    else{ // regular url
		Enumeration<String> values = req.getParameterNames();
		while (values.hasMoreElements()){
		    name = values.nextElement().trim();
		    vals = req.getParameterValues(name);
		    value = vals[vals.length-1].trim();	
		    if (name.equals("id")){  
			id = value;
			media.setId(value);
		    }
		    if (name.equals("rid")){  
			rid = value;
			media.setRid(value);
		    }
		    if (name.equals("action")){  
			action = value;
		    }					
		    else if (name.equals("notes")) {
			notes = value.trim();
			media.setNotes(value);
		    }
		}
	    }
	}
	catch(Exception ex){
	    success = false;
	    message = "Error "+ex;
	    logger.error(message);
	}
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=ImageProc&rid="+rid;
		if(!id.equals("")){
		    str += "&id="+id;
		}
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=ImageProc&rid="+rid;
	    if(!id.equals("")){
		str += "&id="+id;
	    }			
	    res.sendRedirect(str);
	    return; 
	}
		
	//
	// if(im_file == null) im_file = "";
	//
	if(action.equals("zoom") || action.equals("Edit") ||
	   (action.equals("") && !id.equals(""))){
	    //
	    String back = media.doSelect();
	    if(!back.equals("")){
		success = false;
		message = "Could not get data ";
	    }
	}
	else if(action.equals("Save") && !sizeLimitExceeded){
	    //
	    image_file = newFile; 
	    media.setName(image_file);
	    if(user.canEdit() || user.isInspector()){				
		String back = media.doSave();
		if(!back.equals("")){
		    success = false;
		    message = "Could not save ";
		}
	    }
	}
	else if(action.equals("Delete")){
	    //
	    // we need the image year (directory) where the image 
	    // is actually saved
	    //
	    if(user.canDelete()){				
		String back = media.doDelete();
		if(!back.equals("")){
		    success = false;
		    message = "Could not delete ";
		}
		else{
		    media = new MediaFile(debug);
		    media.setRid(rid);
		    id="";
		}
	    }
	}
	else{ // new and ""
	    media = new MediaFile(debug);
	    media.setRid(rid);
	    image_date = Helper.getToday();
	    id="";
	}
	//
	out.println("<html><head><title>Rental Images</title>");
	Helper.writeWebCss(out, url);
	out.println("<script language=Javascript>");
	out.println("  function validateForm(){		                 ");
	out.println(" if (document.myForm.notes){                        ");
	out.println(" if ((document.myForm.notes.value.length>500)){     ");
	out.println("  alert(\"In notes field more than 500 "+
		    "characters are entered\");	                         ");
	out.println("  document.myForm.notes.value = "+
		    "document.myForm.notes.value.substr(0,500);          "); 
	out.println("  	  document.myForm.notes.focus();                 ");
	out.println("     return false;				       	 ");
	out.println("	}}						 ");
       	out.println("     return true;				         ");
	out.println("	}	         			         ");
	//
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                                   ");
	out.println("   x = confirm(\"Are you sure you want to delete "+
		    "this record\");");
	out.println("     return x;                                      ");
	out.println("	}						 ");
	out.println("  function processZoom(idate,iname,idm) {   ");
	out.println("   document.iForm.image_file.value=iname;   ");
	out.println("   document.iForm.image_date.value=idate;   ");
	out.println("   document.iForm.idim.value=idm;           ");
	out.println("   document.iForm.submit();                 ");
	out.println("      }                                     ");   
	out.println("/*                                          ");           
	out.println(" * Returns a new XMLHttpRequest object, or  ");
	out.println(" * false if this browser doesn't support it ");
	out.println(" */                                         ");
	out.println(" function newXMLHttpRequest() {             ");
	out.println("  var req = false;                          ");
	out.println("  if (window.XMLHttpRequest) {              ");
	//
	// Create XMLHttpRequest object in non-Microsoft browsers
	//
	out.println("    req = new XMLHttpRequest();             ");
	out.println("  } else if (window.ActiveXObject) {        ");
	//
	// Create XMLHttpRequest via MS ActiveX
	//
	out.println("    try {                                    ");
	//
	// Try to create XMLHttpRequest in later versions
	// of Internet Explorer
	//
	out.println(" req = new ActiveXObject(\"Msxml2.XMLHTTP\"); ");
	out.println("    } catch (e1) {                            ");
	//
	// Failed to create required ActiveXObject
	//
	out.println("   try {                                       ");
	//
        // Try version supported by older versions
        // of Internet Explorer
	//
	out.println("  req = new ActiveXObject(\"Microsoft.XMLHTTP\");");
	out.println("   } catch (e2) {                                ");
	out.println("   req = false;                                  ");
	//
        // Unable to create an XMLHttpRequest with ActiveX
	//
	out.println("      } }}                                       ");
	out.println("  return req;                                    ");
	out.println("   }                                             ");
	/**
	 * AJAX implementation 
	 * check if the image file already exists in the system to 
	 * avoid overwriting problem that was reported recently
	 */
	out.println(" var lastUpdate = 0 ;                     ");
	out.println(" 	function checkImage() {                ");
	out.println(" alert(\"Calling checkImage \");  ");
	out.println(" var imFile=document.iForm.im_file.value; ");
	out.println(" if(imFile.length == 0){                  ");
	out.println(" alert(\"Need to enter the file name\");  ");
	out.println(" return false; } // or just return        ");
	//
	// let the user  know that we are checking the file name 
	//
	out.println(" document.iForm.action.value=\"Checking Image\"; ");
	//
	// Obtain an XMLHttpRequest instance
	//
	out.println("   var req = newXMLHttpRequest();                 ");
	out.println("  if(!req){                                       ");
	out.println("  alert(\"Error initializing XMLHttpRequest!\");  ");
	out.println("  return; }                                       ");
	//
	// Set the handler function to receive callback notifications
	// from the request object
	//
	out.println("var handlFunction=getReadyStateHandler(req,updateInfo);");
	out.println("   req.onreadystatechange = handlFunction;      ");
	// 
	// Open an HTTP POST connection to the shopping cart servlet.
	// Third parameter specifies request is asynchronous.
	//
	out.println(" req.open(\"POST\", \""+url+"imgServiceChk\",true);");
	//
	// Specify that the body of the request contains form data
	//
	out.println("   req.setRequestHeader(\"Content-Type\",  ");
	out.println("  \"application/x-www-form-urlencoded\");  ");
	//
	// Send form encoded data stating that I want to add the 
	// specified item to the cart.
	//
	out.println("   req.send(\"action=check&imgfile=\"+escape(imFile)); ");
	out.println(" }                                          ");
	//
	/**
	 * Returns a function that waits for the specified XMLHttpRequest
	 * to complete, then passes its XML response
	 * to the given handler function.
	 * req - The XMLHttpRequest whose state is changing
	 * responseXmlHandler - Function to pass the XML response to
	 */
	out.println(" function getReadyStateHandler(req,responseXmlHandler){");
	//
	// Return an anonymous function that listens to the 
	// XMLHttpRequest instance
	out.println(" return function () {          ");
	//
	// If the request's status is "complete"
	out.println(" if (req.readyState == 4) {    ");
	//      
	// Check that a successful server response was received
	out.println(" if (req.status == 200) {       ");
	//
        // Pass the XML payload of the response to the 
        // handler function
	out.println("  responseXmlHandler(req.responseXML); ");//updateInfo
	out.println(" } else if (request.status == 404) {      ");
	out.println(" alert (\"Requested URL is not found.\"); ");
	out.println(" } else if (request.status == 403) {      ");
	out.println("  alert(\"Access denied.\");              ");
	out.println("  } else {                                ");
        // An HTTP problem has occurred
	out.println("   alert(\"HTTP error: \"+req.status);   ");
	out.println("    }                                    ");
	out.println("   }                                     ");
	out.println("  }                                      ");
	out.println(" }                                       ");
	out.println(" function updateInfo(respXML) {          ");
	//
	// Get the root "respond" element from the document
	//
	out.println(" var resp=respXML.getElementsByTagName(\"respond\")[0];");
	//
	// Check that a more recent respond document hasn't been processed
	// already
	//
	out.println(" var generated = resp.getAttribute(\"generated\"); ");
	out.println(" if (generated > lastUpdate) {                     ");
	out.println("   lastUpdate = generated;                         ");
	//
	out.println(" var answer = resp.getElementsByTagName(\"answer\");");
	//
	// Extract the text nodes from the name 
	out.println(" var result=answer.getElementsByTagName(\"result\")[0].firstChild.nodeValue; ");
	out.println(" if(result == \"Ok\"){                  "); 
	out.println("      document.iForm.submit();          ");
	out.println(" }                                      ");
	out.println(" else {                                 ");
	out.println("   alert(\"Invalid image file name\");  "); 
	out.println("      }                                 ");
	out.println("    }                                   ");
	out.println("  }                                     ");
	out.println(" </script>		                     ");
	out.println(" </head><body>                          ");
	out.println(" <center><h3>Rental Attachments</h3>");
	Helper.writeTopMenu(out, url);	
	//
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color='red'>"+message+"</font></h3>");
	}
	//
	out.println("<table border width=80%>");
	//
	out.println("<tr><td>");
	out.println("<form name=myForm method=post "+
		    "ENCTYPE=\"multipart/form-data\">");
	//
	if(!id.equals(""))
	    out.println("<input type=hidden name=id value="+id+">");
	if(!rid.equals(""))
	    out.println("<input type=hidden name=rid value="+rid+">");
	//
	// 1st block
	//
	out.println("<table width=100%>");
	if(user.canEdit() || user.isInspector()){
	    out.println("<tr><td align=\"right\"><b>Rental:</b></td><td align=\"left\"><a href=\""+url+"Rental?action=zoom&id="+rid+ "\">"+rid+"</a></td></tr>");			
	    if(action.equals("zoom")){
		if(media.hasDate())
		    out.println("<tr><td align=\"right\"><b>File Date: </b></td><td align=\"left\">"+media.getDate()+"</td></tr>");
		else
		    out.println("<tr><td align=\"right\"><b>File Date: </b></td><td align=\"left\">"+today+"</td></tr>");
		String str = url2+media.get2DigitYear()+
		    "/"+media.getName();
		if(media.hasName() && media.hasDate()){
		    out.println("<tr><td align=\"right\"><b>File Name: </b></td><td align=\"left\">"+media.getName()+"</td></tr>");
		    out.println("<tr><td colspan=\"2\" align=\"center\">");

		    if(media.isImage()){
			out.println("<a href=\"#\" onClick=\"window.open('"+str+
				    "','Picture',"+
				    "'toolbar=0,location=0,"+
				    "directories=0,status=0,menubar=1,scrollbars=2'"+
				    ")\">");

			out.println("<img src=\""+str+"\">");
			out.println("</a></td></tr>");
			out.println("<tr><td colspan=\"2\" align=\"center\"><font color=\"green\" "+
				    "size=\"-1\">"+
				    "Click on the image to enlarge.</font></td></tr>");
		    }
		    else{
			out.println("<a href='"+str+"'>");
			out.println(media.getName());
			out.println("</a></td></tr>");
			out.println("<tr><td colspan=\"2\" align=\"center\"><font color=\"green\" "+
				    "size=\"-1\">"+
				    "Click on the link to open.</font></td></tr>");
		    }
		    if(media.hasNotes()){
			out.println("<tr><td align=\"right\">Notes:</td><td align=\"left\"> "+media.getNotes()+"</td></tr>");
		    }
		    out.println("</table></td></tr>");
		    out.println("<tr><td><table width=\"100%\">");
		    out.println("<tr><td align=\"right\">  "+
				"<input type=submit "+
				"name=action "+
				"value=Edit>&nbsp;&nbsp;&nbsp;"+
				"</td></tr>"); 
		    out.println("</form>");
		    out.println("</table></td></tr>");
		}
	    }
	    else {
		if(id.equals("")){
		    out.println("<tr><td colspan=\"2\"><font color=green>"+
				"To upload a new attachment to Rental System,<br> "+
				" If it is an image, download it to your computer"+
				" from your camera to a specific folder.<br />"+
				" Rental system will ReName the file based on "+
				" current date and extension. <br />"+
				" Click on the Browse button to locate the file"+
				" on your computer to upload it. "+
				" After clicking on Save, a link to the upload "+
				" file will be provided with the new name.<br />"+
				" Supported images files are of type 'jpg','gif' "+
				" and  'png' up to 2 MB. Word document of type "+
				" .doc (MS Word), pdf, html and txt are also "+
				" supported for a max of 5 MB of size "+
				" <br>");
		    out.println("</font></td></tr>");
		    out.println("<tr><th>Image/Document File: </th><td>"); 
		    out.println("<input type=file name=im_file "+
				" size=30></td></tr>");
		}
		else{
		    out.println("<tr><td align=\"right\">Image/Document File: </td><td align=\"left\">");
		    out.println(media.getName());
		    out.println("</td></tr>");
		}
		out.println("<tr><th colspan=\"2\">Notes <font color=green "+
			    "size=-1> up to 500 characters</font></th></tr>");
		out.println("<tr><td colspan=\"2\">");				
		out.println("<textarea rows=5 cols=60 wrap name=notes>");
		out.println(media.getNotes());
		out.println("</textarea>");
		out.println("</td></tr>");
		out.println("</table></td></tr>");
		out.println("<tr><td><table width=\"100%\">");				
		if(id.equals("")){
		    out.println("<tr><td align=center>  "+
				"<input type=submit "+
				"name=action "+
				"value=Save>&nbsp;&nbsp;&nbsp;"+
				"</td></tr>");
		    out.println("</form>");
		}
		else {  // save, update, zoom
		    out.println("<tr><td valign=top "+
				"align=right>");
		    out.println("<input type=submit name=action "+
				"value=New>");
		    if(user.isAdmin()){
			out.println("</form></td>");
			out.println("<td valign=top align=right>");					
			out.println("<form name=delForm method=post ENCTYPE=\""+
				    "multipart/form-data\" "+
				    "onSubmit=\"return validateDelete()\">");
						
			out.println("<input type=hidden name=id value="+id+">");
			out.println("<input type=hidden name=rid value="+rid+">");
			out.println("<input type=submit name=action "+
				    "value=Delete>");
			out.println("</form></td>");					
		    }
		}
		out.println("</table></td></tr>");
		out.println("</table><br>");
	    }
	}
	//
	// send what we have so far
	//
	out.flush();
	if(!rid.equals("")){
	    boolean foundImage = false;
	    MediaFileList mfl = new MediaFileList(debug, rid);
	    String back = mfl.find();
	    if(back.equals("") && mfl.size() > 0){
		String [] titles = {"Date",
		    "File Name",
		    "notes"};
		out.println("<table border>");
		out.println("<tr><th>Date</th><th>File Name</th><th>Notes</th></tr>");
		out.println("</tr>");
		for(MediaFile one:mfl){
		    image_file = one.getName();
		    id = one.getId();
		    if(image_file.equals("")) continue;
		    out.println("<tr><td><a href=\""+url+"ImageProc?action=zoom&id="+one.getId()+"&rid="+one.getRid()+"\">"+one.getDate()+"</a></td>");
		    out.println("<td><a href=\""+url2+
				one.get2DigitYear()+
				"/"+one.getName()+"\">");					
		    if(one.isImage()){
			out.println("<img width=\"100\" src=\""+url2+
				    one.get2DigitYear()+
				    "/"+one.getName()+
				    "\">");
			foundImage = true;
		    }
		    else{
			out.println(one.getName());
		    }
		    out.println("</a></td>");
		    out.println("<td>"+one.getNotes()+"&nbsp;</td>");
		    out.println("</tr>");
		}
		if(foundImage){
		    out.println("<tr><td colspan=\"3\"><font size=\"-1\" color=\"green\">Click on image to enlarge</font></td></tr>");
		}
		out.println("</table>");
				
	    }
	}
	//
	out.println("<li><a href="+url+"Logout target=_top>Log Out</a>");
	out.print("</body></html>");
	out.close();
    }
	
}























































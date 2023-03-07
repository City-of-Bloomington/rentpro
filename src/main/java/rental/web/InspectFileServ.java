package rental.web;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.naming.*;
import javax.naming.directory.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.io.*;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/InspectFileServ"})
@SuppressWarnings("unchecked")
public class InspectFileServ extends TopServlet{

    final static long serialVersionUID = 420L;
    static Logger logger = LogManager.getLogger(InspectFileServ.class);
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    //
    List<Item> inspectTypes = null;

    /**
     * Generates the inspection main form with the view of
     * previously entered information.
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
	String action="";

	String id="", rental_id="", has_affidavit="",
	    insp_file="", inspection_date="", time_spent="",
	    variance="", dyy="", dmm="", report="", compliance_date="";
	String violations="", smook_detectors="", life_safety="";
	// 
	// the threshold date where the inspection files started being
	// classed in year/month subdirectories, while all the old files
	// will stay in the same original directory
	//
	boolean success = true;
	String message="";
	String filePath = "";
	// 
	// for inspection report
	String insp_id="";

	String [] vals;
	Hashtable<String, String> inspMap = new Hashtable<String, String>();
	//
	String saveDirectory = "";
	String fileName = "";
	String content_type = req.getContentType();
	List<FileItem> items = null;
	Inspection inspect = new Inspection(debug);
	try{
	    if(content_type != null && content_type.startsWith("multipart")){
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		items = upload.parseRequest(req);
		Iterator<FileItem> iter = items.iterator();
		File toSaveFile = null;
		boolean inTempFile = false;
		while (iter.hasNext()) {
		    FileItem item = iter.next();
		    if (item.isFormField()) {
			//
			// process form fields
			//
			name = item.getFieldName();
			value = item.getString();
			if (name.equals("id")){   // insp_id
			    id = value;
			    inspect.setId(id);
			    inspect.doSelect();
			    fileName = inspect.getInspFileName();
			    saveDirectory = findDiretory(inspect, url);
			    //
			    if(inTempFile){
				toSaveFile.renameTo(new File(saveDirectory, fileName));
			    }
			}
			else if(name.equals("compliance_date")){
			    compliance_date = value;
			}
			else if(name.equals("rental_id")){
			    rental_id = value;
			}						
			else if(name.equals("violations")){
			    violations = value;
			}
			else if(name.equals("smook_detectors")){
			    smook_detectors = value;
			}
			else if(name.equals("life_safety")){
			    life_safety = value;
			}
			else if(name.equals("time_spent")){
			    time_spent = value;
			}
			else if(name.equals("has_affidavit")){
			    has_affidavit = value;
			}												
			else if(name.equals("action")){
			    action = value;
			}
		    }
		    else { // streem
			//
			// process uploaded item/items
			//
			String fieldName = item.getFieldName(); // form field
			// String contentType = item.getContentType();
			// boolean isInMemory = item.isInMemory();
			// sizeInBytes = item.getSize();
			String fname = item.getName(); // file name
			String filename = "";
			//
			// if no name is entered, no file is uploaded
			// in this case do nothing
			if (fname != null && !fname.equals("")){
			    filename = FilenameUtils.getName(fname);
			    // System.err.println("filename "+filename);
			    //
			    // create the file on the hard drive and save it
			    //
			    // if we knew the file name then we save directly
			    //
			    logger.debug(" Uploaded file "+filename);
			    if(!fileName.equals("")){
				toSaveFile = new File(saveDirectory, fileName);
				if(debug){
				    logger.debug(" Saved file "+fileName);
				    logger.debug(" Saved dir "+saveDirectory);
				}
			    }
			    else{ // we save to a temporary file first and
				// then we rename the file
				String tempFileName = genTempFile();
				String tempDirectory = (url.indexOf("10.50.") == -1)? server_path: pc_path;
				toSaveFile = new File(tempDirectory, tempFileName);
				inTempFile = true;
				if(debug){
				    logger.debug("Saved Tmp file "+tempFileName);
				    logger.debug("Saved Tmp dir "+tempDirectory);
				}
			    }
			    item.write(toSaveFile);
			    message = "Uploaded successfully";
			}
		    }
		}
	    }
	    else{
		Enumeration<String> values = req.getParameterNames();
		while (values.hasMoreElements()){
		    name = values.nextElement().trim();
		    vals = req.getParameterValues(name);
		    if(vals == null) continue;
		    value = vals[vals.length-1].trim();	
		    if (name.equals("rental_id")){
			rental_id = value;
		    }
		    else if (name.equals("id")){
			id = value;
			inspect.setId(id);
			inspect.doSelect();
			rental_id = inspect.getRegistrId();
		    }
		    else if (name.equals("compliance_date")){
			compliance_date = value;
		    }
		    else if (name.equals("violations")){
			violations = value;
		    }
		    else if (name.equals("smook_detectors")){
			smook_detectors = value;
		    }
		    else if (name.equals("life_safety")){
			life_safety = value;
		    }
		    else if (name.equals("time_spent")){
			time_spent = value;
		    }
		    else if (name.equals("has_affidavit")){
			has_affidavit = value;
		    }										
		    else if(name.equals("action")){
			action = value;  
		    }
		}
	    }
	}
	catch(Exception ex){
	    success = false;
	    message = "Error "+ex;
	    logger.error(message);
	}		
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=InspectFileServ&rental_id="+rental_id;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=InspectFileServ&rental_id="+rental_id;			
	    res.sendRedirect(str);
	    return; 
	}
	// File file = new File(pc_path);
	if(true){
	    String str="",str2="";
	    if(inspectTypes == null){
		InspectTypeList itl = new InspectTypeList(debug);
		str = itl.find();
		if(str.equals("")){
		    inspectTypes = itl.getInspectTypes();
		}
	    }
	}
	if(inspectTypes != null){
	    for(Item item: inspectTypes){
		inspMap.put(item.getId(),item.getName());
	    }
	}
	if(action.equals("Update")){
	    String old_comp_date = inspect.getComplianceDate();
	    if(old_comp_date.equals("") ||
	       !old_comp_date.equals(compliance_date)){
		inspect.setComplianceDate(compliance_date);
	    }
	    inspect.setViolations(violations);
	    inspect.setSmookDetectors(smook_detectors);
	    inspect.setLifeSafety(life_safety);
	    inspect.setTimeSpent(time_spent);
	    inspect.setHasAffidavit(has_affidavit);
	    String back = inspect.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		message += "Updated Successfully";
	    }
	}
	//        
	if(true){
	    String back = inspect.doSelect();
	    if(!back.equals("")){
		message += " could not retrieve inspection data "+back;
		success = false;
	    }
	    rental_id = inspect.getRegistrId();
	    insp_file = inspect.getInspFile();
	    if(insp_file != null && !insp_file.equals("")){
		String str = Helper.getFileDir(insp_file);
		if(url.indexOf("50.103.") == -1){
		    if(str != null)
			filePath = server_path+Helper.replaceSlash(str+insp_file);
		    else
			filePath = server_path+Helper.replaceSlash(insp_file);
		}
		else{
		    if(str != null)
			filePath = pc_path+str+insp_file;
		    else
			filePath = pc_path+insp_file;
		}
	    }
	}
	if(action.equals("download")){
	    try{
		doDownload(req, res, filePath);
		return;
	    }catch(Exception ex){
		message += " file not found "+ex;
	    }
	}
	out.println("<html><head><title>Rental Inspection</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                 ");
	out.println("    return true;                                ");
	out.println("    }                                           ");
	out.println(" </script>		                                 ");
	out.println(" </head><body>                                  ");
	//
	out.println("<center>");
	Helper.writeTopMenu(out, url);
	out.println("<h2>View/Update Inspection</h2>");		
	if(!message.equals("")){
	    if(success)
		out.println("<h2>"+message+"</h2>");
	    else
		out.println("<h2><font color=red>"+message+"</font></h2>");	
	}
	if(!id.equals("")){
	    out.println("<form name=\"myForm\" method=\"post\""+
			" ENCTYPE=\"multipart/form-data\" >");
	    out.println("<input type=\"hidden\" name=\"rental_id\" value=\""+rental_id+"\" />");
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"inspection_type\" value=\""+inspect.getInspectionType()+"\" />");
	    out.println("<table border=\"1\" width=\"90%\">");
	    out.println("<tr><td>");
	    //
	    // 1st block
	    //
	    out.println("If you want to View or Edit the inspection file, do the following.");			
	    out.println("<ul>");
	    out.println("<li>Download the inspection file below to your PC by clicking on the link below.</li>");
	    out.println("<li>Make sure you know where the file is saved.</li>");
	    out.println("<li>Double click on the downloaded file</li>");
	    out.println("<li>Edit or Update any changes you want to do.</li>");
	    out.println("<li>Do save the file (file => Save).</li>");
	    out.println("<li>When done, upload the file by clicking on the Browse button below to locate the file.</li>");
	    out.println("<li>Click on Submit, make sure no errors are reported.</li>");
	    out.println("<li>If you do not want to do any changes (view only), you do not need to upload the file.</li>");
	    out.println("<li>Inspection duration time is the number of hours spent in inspection such as (2.50) hrs.</li>");
	    out.println("<li>Time reporting status will change to 'Completed' when the inspection duration time is entered </li>");
	    out.println("<li>You can update the violations, smoke detectors and life safety, then click on Update.</li>");
	    out.println("<li>You can upload sanned document, images and any related file using 'Add Attachment' button </li>");						
	    out.println("</ul>");
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=90%>");
	    out.println("<tr><td align=\"right\" width=\"40%\"><b>Rental: </b></td><td><a href=\""+url+"Rental?action=zoom&id="+rental_id+"\">"+rental_id+"</a></td></tr>");
	    out.println("<tr><td align=\"right\"><b>Inspection Date</b><td>");
	    out.println(inspect.getInspectionDate()+"</td></tr>");
	    out.println("<tr><td align=\"right\"><b>");
	    out.println("Inspection&nbsp;Type:</b></td><td>");
	    if(inspectTypes != null){
		out.println(inspMap.get(inspect.getInspectionType()));
	    }
	    out.println("</td></tr>");
	    if(user.canEdit() || user.isInspector()){
		out.println("<tr><td align=\"right\"><b>Complied:</b></td><td>"); //
		out.println("<input type=\"text\" maxlength=\"10\" size=\"10\""+
			    " name=\"compliance_date\" id=\"compliance_date\" "+
			    " class=\"date\" "+
			    " value=\""+inspect.getComplianceDate()+"\" />");
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Violations:</b></td><td>"); //
		out.println("<input type=\"text\" maxlength=\"4\" size=\"3\""+
			    " name=\"violations\" "+
			    " value=\""+inspect.getViolations()+"\" />");
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Smoke Detectors:</b></td><td>"); //
		out.println("<input type=\"text\" maxlength=\"4\" size=\"3\""+
			    " name=\"smook_detectors\" "+
			    " value=\""+inspect.getSmookDetectors()+"\" />");
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Life Safety:</b></td><td>"); //
		out.println("<input type=\"text\" maxlength=\"4\" size=\"3\""+
			    " name=\"life_safety\" "+
			    " value=\""+inspect.getLifeSafety()+"\" />");
		out.println("<tr><td align=\"right\"><b>Landlord Has Affidavit:</b></td><td>"); //
		out.println("<select name=\"has_affidavit\">");
		for(int i=0;i<InspectionServ.affidavitArr.length;i++){
		    if(inspect.getHasAffidavit().equals(InspectionServ.affidavitArr[i]))
			out.println("<option selected=\"selected\">"+InspectionServ.affidavitArr[i]+"</option>\n");
		    else
			out.println("<option>"+InspectionServ.affidavitArr[i]+"</option>\n");
		}
		out.println("</select></td></tr>");		
		if(inspect.getInspectionType().equals("CYCL")){
		    out.println("<tr><td align=\"right\"><b>Inspection Duration:</b></td><td>");
		    out.println("<input name=\"time_spent\" size=\"5\" maxlength=\"5\" value=\""+
				inspect.getTimeSpent()+"\" />(hrs dd.dd format)</td></tr>");
		    out.println("<tr><td align=\"right\"><b>Time Reporting Status:</b></td><td>");
		    out.println(inspect.getTimeStatus()+"</td></tr>");
		}
	    }
	    else{
		out.println("<tr><td align=\"right\"><b>Complied:</b></td><td>"); 
		out.println(inspect.getComplianceDate());
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Violations:</b></td><td>");
		out.println(inspect.getViolations());
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Smoke Detectors:</b></td><td>");
		out.println(inspect.getSmookDetectors());
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Life Safety:</b></td><td>");
		out.println(inspect.getLifeSafety());
		out.println("  </td></tr>");
		out.println("<tr><td align=\"right\"><b>Landlord Has Affidavit:</b></td><td>");
		out.println(inspect.getHasAffidavit());
		out.println("  </td></tr>");								
								
	    }
	    out.println("<tr><td align=\"right\"><b>Inspected by:</b></td><td>");
	    out.println((inspect.getInspector() != null ? inspect.getInspector():""));
	    out.println("</td></tr>");
	    if(!insp_file.equals("")){
		out.println("<tr><td align=\"right\"><b>Inspection File:</b></td><td>"); 
		if(user.hasRole("Inspect") ||
		   user.hasRole("Edit")){
		    out.println("Click <a href=\""+url+  
				"InspectFileServ?id="+id+"&rental_id="+rental_id+"&action=download\"> "+inspect.getInspFileName()+"</a> to download </td></tr>");
		}
		out.println("<tr><td align=\"right\"><b>Updated File:</b></td><td>"); 	
		out.println(" Click  <input type=file name=upfile size=20 /> to upload it (Note:This file will replace the old one)");
		out.println("</td></tr>");
	    }
	    out.println("<tr><td align=\"right\"><b>Heat Source:</b></td><td>");
	    out.println(inspect.getHeatSrc());
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"right\"><b>Number of Stories:</b></td><td>");
	    out.println(inspect.getStoryCnt());
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"right\"><b>Foundation Type:</b></td><td>");
	    out.println(inspect.getFoundation());
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"right\"><b>Attic Access:</b> </td><td>");
	    out.println(inspect.getAttic());
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"right\"><b>Accessory Structure:</b> </td><td>");
	    out.println(inspect.getAccessory());
	    out.println("</td></tr>");
	    out.println("<tr><td align=\"right\" valign=top><b>Notes:</b> </td><td>");
	    out.println(inspect.getComments());
	    out.println("</td></tr>");
	    out.println("</table>");
	    out.println("<tr><td><table width=\"90%\">");
	    out.println("<tr><td align=\"right\">");
	    // out.println("<input type=\"submit\" name=\"action\" value=\"Update\" />");
	    out.println("<td valign=\"top\" align=\"center\">");
	    /**
	    out.println("<input type=\"button\" name=\"action\" "+
			"onclick=\"document.location='"+url+
			"RentalFileServ?rental_id="+rental_id+"'\" "+
			"value=\"Add Attachment\" />&nbsp;&nbsp;");
	    */
	    out.println("</td>");			
	    // out.println("<td align=\"right\">");
	    // out.println("<button onclick=\"window.location='"+url+"InspectionServ?rental_id="+rental_id+"';return false;\">New Inspection</button></td>");
	    out.println("</tr>");			
	    out.println("</table></td></tr>");
	    out.println("</table><br />");
	    out.println("</form>");
	    String [] titles = 
		{"ID","Inspection Date","Type",
		 "Compliance Date",
		 "Inspected by", 
		 "Violations",
		 "Smoke Detectors",
		 "Life Safety",
		 "Time Reporting Status"
		};
	    List<Inspection> inspects = null;
	    InspectionList il = new InspectionList(debug, rental_id);
	    String back = il.find();
	    List<Inspection> inspcts = il.getInspections();
	    if(inspcts == null || inspcts.size() == 0){
		out.println("No inspection done yet <br>");
	    }
	    else {
		int totalViol=0, totalSmk=0, totalSfty=0;				
		out.println("<table border>");
		out.println("<tr>");
		for(int i=0; i<titles.length; i++){
		    out.println("<td>"+titles[i]+"</td>");
		}
		out.println("</tr>");			
		for(Inspection one:inspcts){
		    out.println("<tr>");
		    filePath = pc_path;
		    String viols = one.getViolations();
		    String smokes = one.getSmookDetectors();
		    String safety = one.getLifeSafety();
		    out.println("<td><a href=\""+url+"InspectFileServ?id="+one.getId()+"\">"+one.getId()+"</a></td>");
		    out.println("<td>"+one.getInspectionDate()+"</td>");
		    out.println("<td>"+inspMap.get(one.getInspectionType())+
				"</td>");
		    out.println("<td>"+one.getComplianceDate()+"&nbsp;</td>");
		    out.println("<td>"+((one.getInspector() != null)? one.getInspector():"")+"&nbsp;</td>");
		    out.println("<td align=\"right\">"+viols+"&nbsp;</td>");
		    out.println("<td align=\"right\">"+one.getSmookDetectors()+"&nbsp;</td>");
		    out.println("<td align=\"right\">"+one.getLifeSafety()+"&nbsp;</td>");
		    out.println("<td>"+one.getTimeStatus()+"</td>");
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
		    out.println("</tr>");
		}
		if(totalViol > 0){
		    out.println("<tr><td colspan=\"5\">Total</td>"+
				"<td align=\"right\">"+totalViol+"&nbsp;</td>"+
				"<td align=\"right\">"+totalSmk+"&nbsp;</td>"+
				"<td align=\"right\">"+totalSfty+"&nbsp;</td>"+
				"<td>&nbsp;</td>"+
				"</tr>");
		}
		out.println("</table>");
	    }
	    Helper.writeWebFooter(out, url);
	    out.println("<br />");
	}
	//
	out.flush();
	out.close();
    }

    void doDownload(HttpServletRequest request,
		    HttpServletResponse response,
		    String inspFile){
		
	BufferedInputStream input = null;
	BufferedOutputStream output = null;
	try{
	    //
	    // Decode the file name (might contain spaces and on) and prepare file object.
	    // File file = new File(filePath, URLDecoder.decode(inspFile, "UTF-8"));
	    File file = new File(inspFile);
	    // Check if file actually exists in filesystem.
	    if (!file.exists()) {
		// Do your thing if the file appears to be non-existing.
		// Throw an exception, or send 404, or show default/warning page, or just ignore it.
		response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
		return;
	    }
	    //
	    // Get content type by filename.
	    String contentType = context.getMimeType(file.getName());
	    //
	    // To add new content types, add new mime-mapping entry in web.xml.
	    if (contentType == null) {
		contentType = "application/octet-stream";
	    }
	    //			
	    // Init servlet response.
	    response.reset();
	    response.setBufferSize(DEFAULT_BUFFER_SIZE);
	    response.setContentType(contentType);
	    response.setHeader("Content-Length", String.valueOf(file.length()));
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			
	    // Prepare streams.
	    //
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
	}
	catch(Exception ex){
	    logger.error(ex);
        } finally {
	    close(output);
            close(input);
        }
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
    //
    // construct the directory from the inspection date
    //
    String findDiretory(Inspection inspect, String url){
	String directory = "", mm="", yy="";
	String date = inspect.getInspectionDate();
	if(date != null && !date.equals("")){
	    mm = date.substring(0,2);  // nonth
	    yy = date.substring(6,10); // year
	}
	if(url.indexOf("10.50.") == -1){
	    directory = server_path+yy+"/"+mm+"/";
	}
	else{
	    directory = pc_path+yy+"\\"+mm+"\\";
	}
	return directory;
    }
    //
    String genTempFile(){
	String tempName = "";
	Random rand = new Random();
	int jj = rand.nextInt(900) + 100; // 3 digits
	int jj2 = rand.nextInt(90) + 10; // 2 digits
	String date = Helper.getToday();
	String mm = date.substring(0,2);  // nonth
	String yy = date.substring(8,10); // year
	tempName = "tmp"+yy+"_"+mm+"_"+jj+"_"+jj2+".doc";
	return tempName;
    }
}























































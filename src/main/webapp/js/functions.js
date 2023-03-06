/* A handy function for doing pop-up confirmations when deleting something */
function deleteConfirmation(url)
{
	if (confirm("Are you really sure you want to delete this?\n\nOnce deleted it will be gone forever."))
	{
		document.location.href=url;
		return true;
	}
	else { return false; }
}


/* The following function creates an XMLHttpRequest object useful for doing AJAX stuff */
function getXMLHttpRequestObject()
{
	var request_o;
	var browser = navigator.appName;

	if (browser == "Microsoft Internet Explorer") { request_o = new ActiveXObject("Microsoft.XMLHTTP"); }
	else { request_o = new XMLHttpRequest(); }

	return request_o;
}

function getFormValues(form)
{
	var params = "";

	for(var i=0; i<form.elements.length; i++)
	{
		switch(form.elements[i].type)
		{
			case "text":
				params += form.elements[i].name + "=" + escape(form.elements[i].value) + ";";
			break;

			case "select-one":
				params += form.elements[i].name + "=" + form.elements[i].options[form.elements[i].selectedIndex].value + ";";
			break;
		}
	}

	//Remove the trailing semicolon before returning
	return params.substr(0,(params.length - 1));
}
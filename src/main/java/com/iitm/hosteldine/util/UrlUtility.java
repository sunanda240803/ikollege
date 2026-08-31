package com.iitm.hosteldine.util;

import jakarta.servlet.http.HttpServletRequest;

public interface UrlUtility {
	
	static String getBaseURL(HttpServletRequest request) {
//	    String scheme = request.getScheme(); // http or https
//	    String serverName = request.getServerName(); // localhost or domain name
//	    int serverPort = request.getServerPort(); // port number
//	    String contextPath = request.getContextPath(); // application context path

	    // Construct the base URL
        //	    baseURL.append(scheme).append("://").append(serverName);
//	    if (serverPort != 80 && serverPort != 443) { // Append port if not default
//	        baseURL.append(":").append(serverPort);
//	    }

        return Utility.getDomainUrl(request);
	}
	
}

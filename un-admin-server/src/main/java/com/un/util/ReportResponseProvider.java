package com.un.util;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ReportResponseProvider {

	public static ResponseEntity<InputStreamResource> getResponse(HttpServletResponse response,
			byte[] fileStreamContent, String fileName) {
		if (fileStreamContent == null)
			return null;
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileStreamContent);
		InputStreamResource inputStreamResource = new InputStreamResource(byteArrayInputStream, "UTF-8");

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentLength(fileStreamContent.length);
		responseHeaders.setContentType(
				MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
		ResponseEntity<InputStreamResource> responseEntity = new ResponseEntity<InputStreamResource>(
				inputStreamResource, responseHeaders, HttpStatus.OK);
		response.addHeader("file-name", fileName);
		return responseEntity;
	}

}

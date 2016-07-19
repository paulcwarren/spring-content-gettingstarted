package com.emc.spring.content.gs.webdav.resources;

import org.springframework.data.mongodb.core.mapping.Document;

import com.emc.spring.content.commons.annotations.Content;
import com.emc.spring.content.commons.annotations.ContentId;
import com.emc.spring.content.commons.annotations.ContentLength;
import com.emc.spring.content.commons.annotations.MimeType;

@Document
@Content
public class File extends com.emc.spring.content.gs.webdav.resources.Object {

	@ContentId
	private String contentId;
	@ContentLength
	private long contentLength;
	@MimeType
	private String mimeType;

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}

package spring.content.gs.webdav.resources;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import com.emc.spring.content.commons.annotations.Content;
import com.emc.spring.content.commons.annotations.ContentId;
import com.emc.spring.content.commons.annotations.ContentLength;
import com.emc.spring.content.commons.annotations.MimeType;

@Entity
@Content
public class File extends spring.content.gs.webdav.resources.Object {

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

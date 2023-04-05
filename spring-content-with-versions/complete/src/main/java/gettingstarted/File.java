package gettingstarted;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.versions.AncestorId;
import org.springframework.versions.AncestorRootId;
import org.springframework.versions.LockOwner;
import org.springframework.versions.SuccessorId;
import org.springframework.versions.VersionLabel;
import org.springframework.versions.VersionNumber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private Date created = new Date();
	private String summary;

	@ContentId private String contentId;
	@ContentLength private long contentLength;
	private String contentMimeType = "text/plain";

	@LockOwner
	private String lockOwner;

	@AncestorId
	private Long ancestorId;

	@AncestorRootId
	private Long ancestralRootId;

	@SuccessorId
	private Long successorId;

	@VersionNumber
	private String version;

	@VersionLabel private String label;

	public File(File f) {
		this.name = f.getName();
		this.summary = f.getSummary();
		this.contentId = f.getContentId();
		this.contentLength = f.getContentLength();
		this.contentMimeType = f.getContentMimeType();
	}
}
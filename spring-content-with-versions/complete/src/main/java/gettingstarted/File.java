package gettingstarted;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.versions.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

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
	private String mimeType = "text/plain";

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
	}
}
package spring.content.gs.webdav.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

	List<File> findAllByParent(Folder parent);
	
	List<File> findAllByParentIsNull();
}

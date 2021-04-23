package gettingstarted;


import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {

//	List<Folder> findAllByParent(Folder parent);

}

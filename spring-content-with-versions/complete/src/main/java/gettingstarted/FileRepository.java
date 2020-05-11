package gettingstarted;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.versions.LockingAndVersioningRepository;

public interface FileRepository extends JpaRepository<File, Long>, LockingAndVersioningRepository<File, Long> {

}

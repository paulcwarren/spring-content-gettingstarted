package spring.content.gs.webdav.resources;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Folder extends Object {

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private Set<Object> children = new HashSet<>();

	public Set<Object> getChildren() {
        return children;
    }

    public void setBooks(Set<Object> children) {
        this.children = children;
    }

}

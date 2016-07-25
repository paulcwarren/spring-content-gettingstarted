package spring.content.gs.webdav.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import io.milton.annotations.ChildrenOf;
import io.milton.annotations.Delete;
import io.milton.annotations.Get;
import io.milton.annotations.MakeCollection;
import io.milton.annotations.Move;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Root;
import io.milton.http.exceptions.BadRequestException;

@ResourceController
public class RootController  {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RootController.class);

    @Inject
    private FolderRepository folders; 

    @Inject
    private FileRepository files; 

    @Inject
    private FileContentRepository contents; 

    @Root
    public RootController getRoot() {
        return this;
    }
    
    public Long getId() {
    	return Long.MAX_VALUE;
    }
    
    public String getName() {
    	return "spring-webdav";
    }
    
    @ChildrenOf
    public List<Object> getRootFolders(RootController root) {
        List<Object> children = new ArrayList<>();
    	children.addAll(folders.findAllByParentIdIsNull());
    	children.addAll(files.findAllByParentIsNull());
    	return children;
    }
    
    @MakeCollection
    public Folder createRootFolder(RootController root, String newName) {
        Folder c = new Folder();
        c.setCreated(new Date());
        c.setModified(new Date());
        c.setName(newName);
        return folders.save(c);
    }

    @Move
    public void move(Folder folder, RootController newParent, String newName) {
        folder.setName(newName);
        folders.save(folder);
    }
    
    @PutChild
    public File createFile(RootController root, String newName, byte[] body) throws BadRequestException {
    	File newFile = new File();
    	newFile.setCreated(new Date());
    	newFile.setModified(new Date());
    	newFile.setName(newName);
    	newFile = files.save(newFile);
    	
    	contents.setContent(newFile, new ByteArrayInputStream(body));
    	newFile = files.save(newFile);
    	
		return newFile;
    }
    
    @PutChild
    public File createFile(File file, byte[] body) throws BadRequestException {
    	contents.setContent(file, new ByteArrayInputStream(body));
    	return files.save(file);
    }
    
    @Get
    public void get(File file, OutputStream out) {
		try {
			IOUtils.copy(contents.getContent(file), out);
		} catch (IOException ioe) {
			log.error(String.format("Unexpected error streaming content for file %s", file.getName()), ioe);
		}
    }
    
    @Move
    public void move(File file, RootController newParent, String newName) {
        file.setName(newName);
        files.save(file);
    }
    
    @Delete
    public void delete(File file) {
    	contents.unsetContent(file);
    	files.delete(file);
    }
}

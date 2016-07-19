package com.emc.spring.content.gs.webdav.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.milton.annotations.ChildrenOf;
import io.milton.annotations.Delete;
import io.milton.annotations.MakeCollection;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.http.exceptions.BadRequestException;

@ResourceController
public class FolderController  {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FolderController.class);

    @Inject
    private FolderRepository folders; 

    @Inject
    private FileRepository files; 

    @Inject
    private FileContentRepository contents; 

    public String getName() {
    	return "folder";
    }
    
    @MakeCollection
    public Folder createFolder(Folder parent, String newName) {
        Folder newFolder = new Folder();
        newFolder.setParent(parent);
        newFolder.setCreated(new Date());
        newFolder.setModified(new Date());
        newFolder.setName(newName);
        return folders.save(newFolder);
    }

    @ChildrenOf
    public List<Folder> getFolders(Folder folder) {
    	List<Folder> children = new ArrayList<>();
    	children.addAll(folders.findAllByParent(folder));
    	return children;
    }
    
    @ChildrenOf
    public List<File> getFiles(Folder folder) {
    	List<File> children = new ArrayList<>();
    	children.addAll(files.findAllByParent(folder));
    	return children;
    }

    @PutChild
    public File createFile(Folder parent, String newName, InputStream content) throws BadRequestException {
    	File newFile = new File();
    	newFile.setParent(parent);
    	newFile.setModified(new Date());
    	newFile.setName(newName);
    	newFile = files.save(newFile);
    	
    	contents.setContent(newFile, content);
    	newFile = files.save(newFile);
    	
		return newFile;
    }
    
    @Delete
    public void delete(Folder folder) {
    	folders.delete(folder);
    }
}

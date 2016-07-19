package com.emc.spring.content.gs.webdav;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.emc.spring.content.gs.webdav.Application;
import com.emc.spring.content.gs.webdav.resources.File;
import com.emc.spring.content.gs.webdav.resources.FileContentRepository;
import com.emc.spring.content.gs.webdav.resources.FileRepository;
import com.emc.spring.content.gs.webdav.resources.Folder;
import com.emc.spring.content.gs.webdav.resources.FolderRepository;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration   
@Ignore
public class Cleanup {

	@Autowired
	private FolderRepository cabinetsRepo;
	
	@Autowired
	private FileRepository filesRepo;
	
	@Autowired
	private FileContentRepository contentsRepo;
	
	@Test
	public void deleteFolders() {
        List<Folder> cabinets = cabinetsRepo.findAll();
        for (Folder cabinet : cabinets) {
        	cabinetsRepo.delete(cabinet);
        }
        cabinets = cabinetsRepo.findAll();
        assertThat(cabinets.size(), is(0));
	}

	@Test
	public void deleteFiles() {
        List<File> files = filesRepo.findAll();
        for (File file : files) {
        	contentsRepo.unsetContent(file);
        	filesRepo.delete(file);
        }
        files = filesRepo.findAll();
        assertThat(files.size(), is(0));
	}
}

package com.javarush.jira.bugtracking.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;

import com.javarush.jira.AbstractControllerTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AttachmentControllerTest extends AbstractControllerTest{

	static final String TEMP_DIR_NAME = "test-file.txt";
	
	@Autowired 
	private TestTransactionalService service;
	
	@TempDir
	Path tempDir;
	
	@Test
	void shouldDeleteFileOnTransactionRollback() throws IOException{
		
		Path file = Files.createFile(tempDir.resolve(TEMP_DIR_NAME));
		
		log.info("File :{} exists: {}", file.toString(), Files.exists(file));
		
		assertThat(Files.exists(file)).isTrue();
		
		try {
			service.runTransactionalMethodThatFails(file);
		} catch (RuntimeException ignored) {
			log.info(ignored.getMessage());
		}
		
		log.info("After runtime exception. File {} exists: {}", file.toString(), Files.exists(file));
		
		assertThat(Files.exists(file)).isFalse();
	}
	

	
	

}

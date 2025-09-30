package com.javarush.jira.bugtracking.attachment;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestTransactionalService {
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Transactional
	void runTransactionalMethodThatFails(Path file) {

		publisher.publishEvent(new AttachmentCreatedEvent(file));
		 
		throw new RuntimeException("Forsed ROLLBACK");
		
	}
}

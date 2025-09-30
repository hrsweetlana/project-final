package com.javarush.jira.bugtracking.attachment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class AttachmentListeners {
	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void handleRollback(AttachmentCreatedEvent event) {
		log.info("in handleRollback() method");
		FileUtil.delete(event.attachmentPath().toString());
	}
}

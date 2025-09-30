package com.javarush.jira.bugtracking.attachment;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.javarush.jira.bugtracking.ObjectType;
import com.javarush.jira.login.AuthUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {
    private final AttachmentRepository repository;
    private final ApplicationEventPublisher eventPublisher;
	
	@Transactional
	public Attachment saveAttachment(MultipartFile file, ObjectType type,
            Long objectId, AuthUser authUser) {
        log.debug("upload file {} to folder {}", file.getOriginalFilename(), type.toString().toLowerCase());
        String path = FileUtil.getPath(type.toString());
        Attachment attachment = new Attachment(null, path, objectId, type, authUser.id(), file.getOriginalFilename());
        Attachment created = repository.save(attachment);
        String fileName = attachment.id() + "_" + file.getOriginalFilename();
        attachment.setFileLink(FileUtil.getNormalizedName(attachment.getFileLink() + fileName));
        FileUtil.upload(file, path, fileName);
        eventPublisher.publishEvent(new AttachmentCreatedEvent(FileUtil.getAttachmentPath(path, fileName)));
        return created;
	}
}

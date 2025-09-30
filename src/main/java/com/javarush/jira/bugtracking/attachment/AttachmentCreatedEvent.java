package com.javarush.jira.bugtracking.attachment;

import java.nio.file.Path;

import com.javarush.jira.common.AppEvent;

public record AttachmentCreatedEvent(Path attachmentPath) implements AppEvent{

}

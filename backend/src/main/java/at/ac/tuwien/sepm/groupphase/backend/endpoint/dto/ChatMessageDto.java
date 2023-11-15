package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;

import java.sql.Timestamp;

public class ChatMessageDto {

    private String content;
    private Long sender;
    private Long receiver;
    private ChatType chatType;
    private Timestamp timestamp;

    public String getContent() {
        return content;
    }

    public ChatMessageDto setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getSender() {
        return sender;
    }

    public ChatMessageDto setSender(Long sender) {
        this.sender = sender;
        return this;
    }

    public Long getReceiver() {
        return receiver;
    }

    public ChatMessageDto setReceiver(Long receiver) {
        this.receiver = receiver;
        return this;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public ChatMessageDto setChatType(ChatType chatType) {
        this.chatType = chatType;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public ChatMessageDto setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}

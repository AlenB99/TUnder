package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;

import java.util.List;

public class ChatDetailDto {

    private long id;
    private ChatType type;
    private String name;
    private String imageUrl;
    private List<DetailedStudentDto> members;
    private ChatMessageDto lastMessage;

    public long getId() {
        return id;
    }

    public ChatDetailDto setId(long id) {
        this.id = id;
        return this;
    }

    public ChatType getType() {
        return type;
    }

    public ChatDetailDto setType(ChatType type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public ChatDetailDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ChatDetailDto setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public List<DetailedStudentDto> getMembers() {
        return members;
    }

    public ChatDetailDto setMembers(List<DetailedStudentDto> members) {
        this.members = members;
        return this;
    }

    public ChatMessageDto getLastMessage() {
        return lastMessage;
    }

    public ChatDetailDto setLastMessage(ChatMessageDto lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }
}

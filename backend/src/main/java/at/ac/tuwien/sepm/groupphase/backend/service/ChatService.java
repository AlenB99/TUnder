package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;

import java.sql.Timestamp;
import java.util.List;

public interface ChatService {

    /**
     * Save the message and get the users which are associated with the chat.
     *
     * @param messageDto the message data
     * @return the users which are associated with the chat.
     * @throws NotAuthorizedException in case the sender is not allowed to send to the receiver
     */
    List<SimpleStudentDto> handleMessageAndGetReceivers(ChatMessageDto messageDto) throws NotAuthorizedException;

    /**
     * Get the chats which are associated with the user.
     *
     * @param username the username of the user
     * @return a list of chat details
     */
    List<ChatDetailDto> getChats(String username);

    /**
     * Get a maximum of {@code limit} messages of the group chat before {@code timestamp}.
     *
     * @param username the user which wants to read the group chat history
     * @param id the id of the group chat
     * @param timestamp the timestamp to cut off newer messages
     * @param limit the limit of the result
     * @return a maximum of {@code limit} messages of the chat which are older than the {@code timestamp}
     */
    List<ChatMessageDto> getHistoryForGroup(String username, long id, Timestamp timestamp, int limit) throws NotAuthorizedException;

    /**
     * Get a maximum of {@code limit} messages of the individual chat before {@code timestamp}.
     *
     * @param username the user which wants to read the individual chat history
     * @param id the id of the chat
     * @param timestamp the timestamp to cut off newer messages
     * @param limit the limit of the result
     * @return a maximum of {@code limit} messages of the chat which are older than the {@code timestamp}
     */
    List<ChatMessageDto> getHistoryForIndividual(String username, long id, Timestamp timestamp, int limit) throws NotAuthorizedException;

}

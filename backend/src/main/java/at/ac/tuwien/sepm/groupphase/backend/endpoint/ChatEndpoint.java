package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/chat")
public class ChatEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final int MIN_LIMIT = 25;
    private static final int MAX_LIMIT = 250;
    private final ChatService chatService;

    public ChatEndpoint(ChatService chatService) {
        this.chatService = chatService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/list")
    @Operation(summary = "Get all matches of student", security = @SecurityRequirement(name = "apiKey"))
    public List<ChatDetailDto> getMatches(Authentication authentication) {
        LOGGER.info("GET /api/v1/chat/list");
        String username = (String) authentication.getPrincipal();
        return chatService.getChats(username);
    }

    @Secured("ROLE_USER")
    @GetMapping("/messages/group/{id}")
    @Operation(summary = "Get messages of a group chat", security = @SecurityRequirement(name = "apiKey"))
    public List<ChatMessageDto> getGroupChatMessages(@PathVariable long id,
                                                     @RequestParam(value = "from", required = false) String from,
                                                     @RequestParam(value = "limit", required = false) Integer limit,
                                                     Authentication authentication) throws NotAuthorizedException {
        if (from == null) {
            from = formatter.format(ZonedDateTime.now());
        }
        if (limit == null) {
            limit = MAX_LIMIT;
        } else if (limit < MIN_LIMIT) {
            limit = MIN_LIMIT;
        }
        LOGGER.info("GET /api/v1/chat/messages/group/{}?from={}&limit={}", id, from, limit);

        String username = (String) authentication.getPrincipal();
        Timestamp timestamp = Timestamp.from(Instant.from(formatter.parse(from)));
        return chatService.getHistoryForGroup(username, id, timestamp, limit);
    }

    @Secured("ROLE_USER")
    @GetMapping("/messages/individual/{id}")
    @Operation(summary = "Get messages of an individual chat", security = @SecurityRequirement(name = "apiKey"))
    public List<ChatMessageDto> getIndividualChatMessages(@PathVariable long id,
                                                          @RequestParam(value = "from", required = false) String from,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          Authentication authentication) throws NotAuthorizedException {
        if (from == null) {
            from = formatter.format(ZonedDateTime.now());
        }
        if (limit == null) {
            limit = MAX_LIMIT;
        } else if (limit < MIN_LIMIT) {
            limit = MIN_LIMIT;
        }
        LOGGER.info("GET /api/v1/chat/messages/individual/{}?from={}&limit={}", id, from, limit);
        String username = (String) authentication.getPrincipal();
        Timestamp timestamp = Timestamp.from(Instant.from(formatter.parse(from)));
        return chatService.getHistoryForIndividual(username, id, timestamp, limit);
    }
}

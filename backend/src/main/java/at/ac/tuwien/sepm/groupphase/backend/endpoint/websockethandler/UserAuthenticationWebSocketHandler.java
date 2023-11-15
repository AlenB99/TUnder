package at.ac.tuwien.sepm.groupphase.backend.endpoint.websockethandler;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;

@Component
public class UserAuthenticationWebSocketHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HashSet<WebSocketSession> notAuthenticated = new HashSet<>();
    private final SecurityProperties securityProperties;
    private final ChatMessageWebSocketHandler delegate;

    public UserAuthenticationWebSocketHandler(SecurityProperties securityProperties, ChatMessageWebSocketHandler delegate) {
        this.securityProperties = securityProperties;
        this.delegate = delegate;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        LOGGER.trace("New WebSocket connected: {}", session);
        notAuthenticated.add(session);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {

        if (!notAuthenticated.contains(session)) {
            delegate.handleMessage(session, message);
            return;
        }

        LOGGER.trace("Try to authenticate WebSocket: {}, {}", session, message);
        if (!(message instanceof TextMessage)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        String token = (String) message.getPayload();
        if (!token.startsWith(securityProperties.getAuthTokenPrefix())) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();

        long id;
        try {
            id = Long.parseLong(claims.get("id").toString());
        } catch (NumberFormatException nfe) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        delegate.afterConnectionEstablished(session);
        delegate.userAuthenticated(id, session);
        notAuthenticated.remove(session);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        if (!notAuthenticated.remove(session)) {
            delegate.afterConnectionClosed(session, closeStatus);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return delegate.supportsPartialMessages();
    }
}

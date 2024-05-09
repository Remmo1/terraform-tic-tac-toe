package tic.tac.toe.game;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Configuration
public class SocketIOConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        config.setAuthorizationListener(this::authenticateUser);
        return new SocketIOServer(config);
    }

    private AuthorizationResult authenticateUser(HandshakeData handshakeData) {
        var webClient = WebClient.builder().baseUrl("http://localhost:8000/validate")
                .defaultHeader("Authorization", "Bearer " + handshakeData.getHttpHeaders().get("token")).build();
        try {
            var response = webClient.get().retrieve().toEntity(String.class).block();
            if (response == null || !response.hasBody()) {
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
            return response.getBody().equals("OK") ? AuthorizationResult.SUCCESSFUL_AUTHORIZATION : AuthorizationResult.FAILED_AUTHORIZATION;

        } catch (WebClientResponseException e) {
            e.printStackTrace();
        }
        return AuthorizationResult.FAILED_AUTHORIZATION;
    }

}

package tic.tac.toe.game;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SocketIOConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setAuthorizationListener(this::validateToken);
        config.setHostname(host);
        config.setPort(port);

        return new SocketIOServer(config);
    }

    private AuthorizationResult validateToken(HandshakeData data) {
        var accessToken = data.getHttpHeaders().get("Authorization");
        if (accessToken == null || accessToken.isEmpty())
            return AuthorizationResult.FAILED_AUTHORIZATION;
        WebClient webClient = WebClient.builder().baseUrl("http:/" + data.getLocal().toString().replace("8080", "8000")).build();
        webClient.get().header("Authorization", "Bearer " + accessToken);
        return AuthorizationResult.SUCCESSFUL_AUTHORIZATION;
    }

}

package com.wexec.pointsense.config;

import com.wexec.pointsense.websocket.SimulationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimulationWebSocketHandler simulationHandler;

    public WebSocketConfig(SimulationWebSocketHandler simulationHandler) {
        this.simulationHandler = simulationHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simulationHandler, "/ws/simulate")
                .setAllowedOriginPatterns("*");
    }
}

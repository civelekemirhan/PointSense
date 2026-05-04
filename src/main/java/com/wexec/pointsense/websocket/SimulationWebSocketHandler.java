package com.wexec.pointsense.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wexec.pointsense.dto.AgentTickDTO;
import com.wexec.pointsense.dto.PositionDTO;
import com.wexec.pointsense.dto.SimulationResponseDTO;
import com.wexec.pointsense.service.SimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ws://host/ws/simulate
 *
 * Her bağlantı bir agent oturumunu temsil eder.
 * İstemci her tick'te AgentTickDTO JSON'u gönderir,
 * sunucu SimulationResponseDTO JSON'u döner.
 */
@Component
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SimulationWebSocketHandler.class);

    private final SimulationService simulationService;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public SimulationWebSocketHandler(SimulationService simulationService,
                                      ObjectMapper objectMapper) {
        this.simulationService = simulationService;
        this.objectMapper      = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket bağlandı: {} (toplam: {})", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root;
        try {
            root = objectMapper.readTree(message.getPayload());
        } catch (Exception e) {
            sendError(session, "Geçersiz JSON: " + e.getMessage());
            return;
        }

        String messageType = root.path("type").asText();
        if ("position_update".equalsIgnoreCase(messageType)) {
            handlePositionUpdate(session, root);
            return;
        }

        AgentTickDTO tick;
        try {
            tick = objectMapper.treeToValue(root, AgentTickDTO.class);
        } catch (Exception e) {
            sendError(session, "Geçersiz AgentTickDTO: " + e.getMessage());
            return;
        }

        SimulationResponseDTO response = simulationService.processTick(tick);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void handlePositionUpdate(WebSocketSession session, JsonNode root) throws Exception {
        String mapName = root.path("map_name").asText(null);
        if (mapName == null || mapName.isBlank()) {
            sendError(session, "map_name alanı zorunludur");
            return;
        }

        PositionDTO position;
        try {
            position = objectMapper.treeToValue(root.path("agent_pos"), PositionDTO.class);
        } catch (Exception e) {
            sendError(session, "Geçersiz agent_pos: " + e.getMessage());
            return;
        }

        if (position == null) {
            sendError(session, "agent_pos alanı zorunludur");
            return;
        }

        simulationService.saveAgentPosition(mapName, position);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                "status", "position_saved",
                "map_name", mapName,
                "agent_pos", position
        ))));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket kapandı: {} — {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable ex) {
        log.error("WebSocket transport hatası [{}]: {}", session.getId(), ex.getMessage());
        sessions.remove(session.getId());
    }

    private void sendError(WebSocketSession session, String message) throws Exception {
        session.sendMessage(new TextMessage(
                objectMapper.writeValueAsString(Map.of("error", message))));
    }
}

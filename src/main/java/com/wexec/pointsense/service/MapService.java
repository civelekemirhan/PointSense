package com.wexec.pointsense.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wexec.pointsense.dto.GameMapDTO;
import com.wexec.pointsense.dto.GridSizeDTO;
import com.wexec.pointsense.dto.ObstaclesDTO;
import com.wexec.pointsense.dto.PositionDTO;
import com.wexec.pointsense.entity.GameMapEntity;
import com.wexec.pointsense.repository.GameMapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MapService {

    private static final Logger log = LoggerFactory.getLogger(MapService.class);

    private final GameMapRepository repo;
    private final ObjectMapper jackson;

    public MapService(GameMapRepository repo, ObjectMapper jackson) {
        this.repo    = repo;
        this.jackson = jackson;
    }

    public void save(GameMapDTO map) {
        repo.save(toEntity(map));
    }

    public Optional<GameMapDTO> findByName(String name) {
        return repo.findById(name).map(this::toDTO);
    }

    public Optional<GameMapDTO> findLatest() {
        return repo.findAll()
                   .stream()
                   .findFirst()
                   .map(this::toDTO);
    }

    public List<String> listNames() {
        return repo.findAll()
                   .stream()
                   .map(GameMapEntity::getMapName)
                   .collect(Collectors.toList());
    }

    public boolean delete(String name) {
        if (!repo.existsById(name)) return false;
        repo.deleteById(name);
        return true;
    }

    // ---- DTO <-> Entity dönüşümleri ----

    private GameMapEntity toEntity(GameMapDTO dto) {
        String obstaclesJson = null;
        if (dto.obstacles() != null) {
            try {
                obstaclesJson = jackson.writeValueAsString(dto.obstacles());
            } catch (JsonProcessingException e) {
                log.warn("Obstacles serialize edilemedi: {}", e.getMessage());
            }
        }
        return new GameMapEntity(
                dto.mapName(),
                dto.gridSize().x(), dto.gridSize().y(),
                dto.startPos().x(), dto.startPos().y(),
                dto.targetPos().x(), dto.targetPos().y(),
                obstaclesJson
        );
    }

    private GameMapDTO toDTO(GameMapEntity e) {
        ObstaclesDTO obstacles = null;
        if (e.getObstaclesJson() != null) {
            try {
                obstacles = jackson.readValue(e.getObstaclesJson(), ObstaclesDTO.class);
            } catch (JsonProcessingException ex) {
                log.warn("Obstacles deserialize edilemedi: {}", ex.getMessage());
            }
        }
        return new GameMapDTO(
                e.getMapName(),
                new GridSizeDTO(e.getGridSizeX(), e.getGridSizeY()),
                new PositionDTO(e.getStartPosX(), e.getStartPosY()),
                new PositionDTO(e.getTargetPosX(), e.getTargetPosY()),
                obstacles
        );
    }
}

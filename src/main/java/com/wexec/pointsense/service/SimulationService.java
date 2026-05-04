package com.wexec.pointsense.service;

import com.wexec.pointsense.dto.AgentTickDTO;
import com.wexec.pointsense.dto.GameMapDTO;
import com.wexec.pointsense.dto.GridSizeDTO;
import com.wexec.pointsense.dto.PositionDTO;
import com.wexec.pointsense.dto.SimulationResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private final MapService mapService;
    private final Normalizer normalizer;
    private final ModelInferenceService modelInference;
    private final DynamicObstacleService obstacleService;

    public SimulationService(MapService mapService,
                             Normalizer normalizer,
                             ModelInferenceService modelInference,
                             DynamicObstacleService obstacleService) {
        this.mapService      = mapService;
        this.normalizer      = normalizer;
        this.modelInference  = modelInference;
        this.obstacleService = obstacleService;
    }

    public SimulationResponseDTO processTick(AgentTickDTO tick) {
        if (tick.mapName() != null && tick.agentPos() != null) {
            saveAgentPosition(tick.mapName(), tick.agentPos());
        }

        GameMapDTO map = resolveMap(tick);
        float[] normalizedInput = normalizer.normalize(tick, map);
        ModelInferenceService.InferenceResult result = modelInference.infer(normalizedInput);
        var nextObstacles = obstacleService.computeNext(tick.dynamicObstaclesCurrent(), map);

        return new SimulationResponseDTO(
                result.action(),
                result.actionLabel(),
                result.qValues(),
                normalizedInput,
                nextObstacles
        );
    }

    public void saveAgentPosition(String mapName, PositionDTO position) {
        mapService.updateStartPosition(mapName, position);
    }

    private GameMapDTO resolveMap(AgentTickDTO tick) {
        if (tick.mapName() != null && !tick.mapName().isBlank()) {
            Optional<GameMapDTO> found = mapService.findByName(tick.mapName());
            if (found.isPresent()) return found.get();
            log.warn("Harita bulunamadı: {}, son kayıtlı haritaya geçiliyor.", tick.mapName());
        }
        return mapService.findLatest().orElseGet(this::defaultMap);
    }

    /** Model ve harita olmadan da test edilebilmesi için dahili varsayılan harita */
    private GameMapDTO defaultMap() {
        return new GameMapDTO(
                "default",
                new GridSizeDTO(800, 600),
                new PositionDTO(50, 50),
                new PositionDTO(750, 550),
                null
        );
    }
}

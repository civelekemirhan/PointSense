package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SimulationResponseDTO(
        /** 0=Sol, 1=Sağ, 2=Yukarı, 3=Aşağı */
        @JsonProperty("action")                 int action,
        @JsonProperty("action_label")           String actionLabel,
        @JsonProperty("q_values")               float[] qValues,
        @JsonProperty("normalized_input")       float[] normalizedInput,
        @JsonProperty("next_dynamic_obstacles") List<DynamicObstacleCurrentDTO> nextDynamicObstacles
) {}

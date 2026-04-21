package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameMapDTO(
        @NotBlank
        @JsonProperty("map_name")  String mapName,

        @NotNull
        @JsonProperty("grid_size") GridSizeDTO gridSize,

        @NotNull
        @JsonProperty("start_pos") PositionDTO startPos,

        @NotNull
        @JsonProperty("target_pos") PositionDTO targetPos,

        @JsonProperty("obstacles") ObstaclesDTO obstacles
) {}

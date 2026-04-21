package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** WebSocket tick'inde gelen anlık dinamik engel durumu */
public record DynamicObstacleCurrentDTO(
        @JsonProperty("id")  int id,
        @JsonProperty("pos") PositionDTO pos,
        @JsonProperty("v")   VelocityDTO v
) {}

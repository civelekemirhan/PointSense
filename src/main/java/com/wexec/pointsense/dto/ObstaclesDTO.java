package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public record ObstaclesDTO(
        @JsonProperty("static")  List<StaticObstacleDTO> staticObstacles,
        @JsonProperty("dynamic") List<DynamicObstacleDTO> dynamic
) {}

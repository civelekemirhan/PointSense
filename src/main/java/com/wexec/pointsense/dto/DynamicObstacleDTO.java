package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public record DynamicObstacleDTO(
        @JsonProperty("id")       int id,
        @JsonProperty("pos")      PositionDTO pos,
        @JsonProperty("velocity") VelocityDTO velocity,
        @JsonProperty("range")    List<Double> range,
        @JsonProperty("type")     String type
) {}

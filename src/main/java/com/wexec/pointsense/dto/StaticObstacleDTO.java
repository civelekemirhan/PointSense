package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StaticObstacleDTO(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y,
        @JsonProperty("w") double w,
        @JsonProperty("h") double h
) {}

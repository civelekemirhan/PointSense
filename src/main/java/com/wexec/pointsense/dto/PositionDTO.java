package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PositionDTO(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {}

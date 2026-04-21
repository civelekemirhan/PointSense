package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VelocityDTO(
        @JsonProperty("vx") double vx,
        @JsonProperty("vy") double vy
) {}

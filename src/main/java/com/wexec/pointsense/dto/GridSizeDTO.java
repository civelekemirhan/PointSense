package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GridSizeDTO(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {}

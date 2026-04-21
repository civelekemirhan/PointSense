package com.wexec.pointsense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** İstemciden her karede (tick) gelen veri */
public record AgentTickDTO(
        @JsonProperty("agent_pos")                 PositionDTO agentPos,
        @JsonProperty("agent_velocity")            VelocityDTO agentVelocity,
        @JsonProperty("sensor_data")               List<Double> sensorData,
        @JsonProperty("dynamic_obstacles_current") List<DynamicObstacleCurrentDTO> dynamicObstaclesCurrent,
        @JsonProperty("map_name")                  String mapName
) {}

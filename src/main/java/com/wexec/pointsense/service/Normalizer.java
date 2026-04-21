package com.wexec.pointsense.service;

import com.wexec.pointsense.dto.AgentTickDTO;
import com.wexec.pointsense.dto.GameMapDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ham simülasyon girdilerini [0, 1] aralığına normalize eder.
 *
 * Girdi vektörü (12 eleman):
 *   [0]  agent_x  / grid_w
 *   [1]  agent_y  / grid_h
 *   [2]  target_x / grid_w
 *   [3]  target_y / grid_h
 *   [4..11] sensor_data (zaten 0-1 beklenir; clamp uygulanır)
 */
@Component
public class Normalizer {

    public float[] normalize(AgentTickDTO tick, GameMapDTO map) {
        double gridW = map.gridSize().x();
        double gridH = map.gridSize().y();

        float[] input = new float[12];
        input[0] = clamp01(tick.agentPos().x() / gridW);
        input[1] = clamp01(tick.agentPos().y() / gridH);
        input[2] = clamp01(map.targetPos().x() / gridW);
        input[3] = clamp01(map.targetPos().y() / gridH);

        List<Double> sensors = tick.sensorData();
        int count = (sensors != null) ? Math.min(sensors.size(), 8) : 0;
        for (int i = 0; i < 8; i++) {
            input[4 + i] = (i < count) ? clamp01(sensors.get(i).floatValue()) : 0f;
        }
        return input;
    }

    private float clamp01(double v) {
        return (float) Math.max(0.0, Math.min(1.0, v));
    }
}

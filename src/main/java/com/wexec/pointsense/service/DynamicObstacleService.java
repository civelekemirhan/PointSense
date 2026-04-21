package com.wexec.pointsense.service;

import com.wexec.pointsense.dto.DynamicObstacleCurrentDTO;
import com.wexec.pointsense.dto.DynamicObstacleDTO;
import com.wexec.pointsense.dto.GameMapDTO;
import com.wexec.pointsense.dto.PositionDTO;
import com.wexec.pointsense.dto.VelocityDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dinamik engellerin bir sonraki karedeki konumunu hesaplar.
 * Desteklenen tip: "ping-pong" (yatay veya dikey menzil içinde gidip gelme).
 */
@Service
public class DynamicObstacleService {

    public List<DynamicObstacleCurrentDTO> computeNext(
            List<DynamicObstacleCurrentDTO> current,
            GameMapDTO map) {

        if (current == null || current.isEmpty()) return List.of();

        Map<Integer, DynamicObstacleDTO> defs = buildDefinitionMap(map);
        List<DynamicObstacleCurrentDTO> next = new ArrayList<>(current.size());

        for (DynamicObstacleCurrentDTO obs : current) {
            DynamicObstacleDTO def = defs.get(obs.id());
            next.add(def == null ? obs : stepPingPong(obs, def));
        }
        return next;
    }

    // ---- private ----

    private Map<Integer, DynamicObstacleDTO> buildDefinitionMap(GameMapDTO map) {
        Map<Integer, DynamicObstacleDTO> m = new HashMap<>();
        if (map.obstacles() == null || map.obstacles().dynamic() == null) return m;
        for (DynamicObstacleDTO d : map.obstacles().dynamic()) {
            m.put(d.id(), d);
        }
        return m;
    }

    /**
     * Ping-pong: engel tanımlı menzil sınırına çarpınca hız yönü tersine döner.
     * Menzil, yatay (vx != 0) veya dikey (vy != 0) eksene göre uygulanır.
     */
    private DynamicObstacleCurrentDTO stepPingPong(
            DynamicObstacleCurrentDTO current,
            DynamicObstacleDTO def) {

        double px = current.pos().x();
        double py = current.pos().y();
        double vx = current.v().vx();
        double vy = current.v().vy();

        double rangeMin = def.range().get(0);
        double rangeMax = def.range().get(1);

        double nx = px + vx;
        double ny = py + vy;
        double nvx = vx;
        double nvy = vy;

        if (vx != 0 && (nx <= rangeMin || nx >= rangeMax)) {
            nvx = -vx;
            nx = Math.max(rangeMin, Math.min(rangeMax, nx));
        }
        if (vy != 0 && (ny <= rangeMin || ny >= rangeMax)) {
            nvy = -vy;
            ny = Math.max(rangeMin, Math.min(rangeMax, ny));
        }

        return new DynamicObstacleCurrentDTO(
                current.id(),
                new PositionDTO(nx, ny),
                new VelocityDTO(nvx, nvy)
        );
    }
}

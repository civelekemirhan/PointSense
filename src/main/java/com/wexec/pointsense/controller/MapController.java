package com.wexec.pointsense.controller;

import com.wexec.pointsense.dto.GameMapDTO;
import com.wexec.pointsense.service.MapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    /** POST /maps/save — Haritayı kaydeder veya üzerine yazar */
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> save(@Valid @RequestBody GameMapDTO map) {
        mapService.save(map);
        return ResponseEntity.ok(Map.of(
                "status",   "saved",
                "map_name", map.mapName()
        ));
    }

    /** GET /maps — Kayıtlı harita isimlerini listeler */
    @GetMapping
    public ResponseEntity<Collection<String>> list() {
        return ResponseEntity.ok(mapService.listNames());
    }

    /** GET /maps/{name} — Belirtilen haritayı döner */
    @GetMapping("/{name}")
    public ResponseEntity<GameMapDTO> get(@PathVariable String name) {
        return mapService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /maps/{name} — Belirtilen haritayı siler */
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String name) {
        if (!mapService.delete(name)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", "deleted", "map_name", name));
    }
}

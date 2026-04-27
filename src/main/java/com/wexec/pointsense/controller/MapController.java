package com.wexec.pointsense.controller;

import com.wexec.pointsense.dto.GameMapDTO;
import com.wexec.pointsense.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/maps")
@Tag(name = "Maps", description = "Harita kaydetme, listeleme, görüntüleme ve silme endpointleri")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    /** POST /maps/save — Haritayı kaydeder veya üzerine yazar */
    @PostMapping("/save")
    @Operation(summary = "Harita kaydet", description = "Yeni harita kaydeder; aynı isim varsa mevcut kaydı günceller")
    public ResponseEntity<Map<String, String>> save(@Valid @RequestBody GameMapDTO map) {
        mapService.save(map);
        return ResponseEntity.ok(Map.of(
                "status",   "saved",
                "map_name", map.mapName()
        ));
    }

    /** GET /maps — Kayıtlı harita isimlerini listeler */
    @GetMapping
    @Operation(summary = "Haritaları listele", description = "Sistemde kayıtlı tüm harita isimlerini döner")
    public ResponseEntity<Collection<String>> list() {
        return ResponseEntity.ok(mapService.listNames());
    }

    /** GET /maps/{name} — Belirtilen haritayı döner */
    @GetMapping("/{name}")
    @Operation(summary = "Harita getir", description = "Verilen isimdeki haritayı döner, yoksa 404")
    public ResponseEntity<GameMapDTO> get(@PathVariable String name) {
        return mapService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /maps/{name} — Belirtilen haritayı siler */
    @DeleteMapping("/{name}")
    @Operation(summary = "Harita sil", description = "Verilen isimdeki haritayı siler, yoksa 404")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String name) {
        if (!mapService.delete(name)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", "deleted", "map_name", name));
    }
}

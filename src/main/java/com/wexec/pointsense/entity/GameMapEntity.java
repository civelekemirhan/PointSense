package com.wexec.pointsense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * game_maps tablosu.
 *
 * Konum/boyut alanları düz sütun olarak tutulur.
 * Engeller (obstacles) JSON text olarak saklanır — sorgu gerektirmez,
 * servis katmanında Jackson ile serialize/deserialize edilir.
 */
@Entity
@Table(name = "game_maps")
public class GameMapEntity {

    @Id
    @Column(name = "map_name", length = 100)
    private String mapName;

    @Column(name = "grid_size_x", nullable = false)
    private double gridSizeX;

    @Column(name = "grid_size_y", nullable = false)
    private double gridSizeY;

    @Column(name = "start_pos_x", nullable = false)
    private double startPosX;

    @Column(name = "start_pos_y", nullable = false)
    private double startPosY;

    @Column(name = "target_pos_x", nullable = false)
    private double targetPosX;

    @Column(name = "target_pos_y", nullable = false)
    private double targetPosY;

    /** ObstaclesDTO'nun JSON temsili */
    @Column(name = "obstacles_json", columnDefinition = "TEXT")
    private String obstaclesJson;

    // ---- constructors ----

    public GameMapEntity() {}

    public GameMapEntity(String mapName,
                         double gridSizeX, double gridSizeY,
                         double startPosX, double startPosY,
                         double targetPosX, double targetPosY,
                         String obstaclesJson) {
        this.mapName       = mapName;
        this.gridSizeX     = gridSizeX;
        this.gridSizeY     = gridSizeY;
        this.startPosX     = startPosX;
        this.startPosY     = startPosY;
        this.targetPosX    = targetPosX;
        this.targetPosY    = targetPosY;
        this.obstaclesJson = obstaclesJson;
    }

    // ---- getters & setters ----

    public String getMapName()           { return mapName; }
    public void setMapName(String v)     { this.mapName = v; }

    public double getGridSizeX()         { return gridSizeX; }
    public void setGridSizeX(double v)   { this.gridSizeX = v; }

    public double getGridSizeY()         { return gridSizeY; }
    public void setGridSizeY(double v)   { this.gridSizeY = v; }

    public double getStartPosX()         { return startPosX; }
    public void setStartPosX(double v)   { this.startPosX = v; }

    public double getStartPosY()         { return startPosY; }
    public void setStartPosY(double v)   { this.startPosY = v; }

    public double getTargetPosX()        { return targetPosX; }
    public void setTargetPosX(double v)  { this.targetPosX = v; }

    public double getTargetPosY()        { return targetPosY; }
    public void setTargetPosY(double v)  { this.targetPosY = v; }

    public String getObstaclesJson()         { return obstaclesJson; }
    public void setObstaclesJson(String v)   { this.obstaclesJson = v; }
}

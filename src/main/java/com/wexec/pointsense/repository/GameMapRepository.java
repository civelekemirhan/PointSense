package com.wexec.pointsense.repository;

import com.wexec.pointsense.entity.GameMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMapRepository extends JpaRepository<GameMapEntity, String> {}

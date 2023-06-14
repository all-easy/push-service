package ru.all_easy.push.shape.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShapeRepository extends JpaRepository<ShapeEntity, ShapeId> {

    ShapeEntity findShapeEntityByRoomTokenAndShape(String roomToken, Shape shape);

    Set<ShapeEntity> findByRoomToken(String roomToken);
}

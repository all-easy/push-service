package ru.all_easy.push.shape.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ShapeRepository extends JpaRepository<ShapeEntity, ShapeId> {

    ShapeEntity findShapeEntityByRoomTokenAndShape(String roomToken, Shape shape);

    Set<ShapeEntity> findByRoomToken(String roomToken);

}

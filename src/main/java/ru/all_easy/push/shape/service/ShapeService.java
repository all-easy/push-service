package ru.all_easy.push.shape.service;

import org.springframework.stereotype.Service;
import ru.all_easy.push.shape.repository.ShapeEntity;
import ru.all_easy.push.shape.repository.ShapeRepository;

import java.util.Set;

@Service
public class ShapeService {

    private final ShapeRepository repository;

    public ShapeService(ShapeRepository repository) {
        this.repository = repository;
    }

    public Set<ShapeEntity> findAllShapesInRoom(String roomToken) {
        return repository.findByRoomToken(roomToken);
    }

    public ShapeEntity findShape(ShapeInfo shapeInfo) {
        return repository.findShapeEntityByRoomTokenAndShape(
                shapeInfo.roomToken(), shapeInfo.shape());
    }

    public ShapeEntity save(ShapeEntity shapeEntity) {
        return repository.save(shapeEntity);
    }
}

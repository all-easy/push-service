package ru.all_easy.push.shape.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "shape")
@IdClass(ShapeId.class)
public class ShapeEntity {

    private String roomToken;
    private String userUid;
    private Shape shape;

    @Id
    public String getRoomToken() {
        return roomToken;
    }

    @Id
    public String getUserUid() {
        return userUid;
    }

    @Column(name = "shape")
    @Enumerated(EnumType.STRING)
    public Shape getShape() {
        return shape;
    }

    public ShapeEntity setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        return this;
    }

    public ShapeEntity setUserUid(String userUid) {
        this.userUid = userUid;
        return this;
    }

    public ShapeEntity setShape(Shape shape) {
        this.shape = shape;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShapeEntity that = (ShapeEntity) o;
        return roomToken.equals(that.roomToken) && userUid.equals(that.userUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomToken, userUid);
    }
}

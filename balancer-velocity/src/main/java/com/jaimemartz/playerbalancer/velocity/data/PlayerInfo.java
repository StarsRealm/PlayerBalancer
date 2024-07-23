package com.jaimemartz.playerbalancer.velocity.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import lombok.*;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@Entity("player_info")
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfo implements Comparable<PlayerInfo> {
    @Id
    ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    String name;
    @Indexed(options = @IndexOptions(unique = true))
    UUID uuid;

    @Override
    public int compareTo(@NotNull PlayerInfo o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PlayerInfo that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}

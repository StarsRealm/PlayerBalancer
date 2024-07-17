package com.jaimemartz.playerbalancer.velocity.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity("player_info")
@Accessors(fluent = true)
@Jacksonized
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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
}

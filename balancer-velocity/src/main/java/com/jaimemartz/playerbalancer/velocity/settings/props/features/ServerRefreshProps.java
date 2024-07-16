package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


@ConfigSerializable
@Data
public class ServerRefreshProps {
    @Setting
    private boolean enabled;

    @Setting
    private int delay;

    @Setting
    private int interval;
}

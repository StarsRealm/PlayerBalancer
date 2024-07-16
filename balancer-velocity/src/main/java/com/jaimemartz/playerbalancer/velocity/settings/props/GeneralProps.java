package com.jaimemartz.playerbalancer.velocity.settings.props;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


@ConfigSerializable
@Data
public class GeneralProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean silent;

    @Setting(value = "auto-reload")
    private boolean autoReload;

    @Setting(value = "plugin-messaging")
    private boolean pluginMessaging;

    @Setting(value = "redis-bungee")
    private boolean redisBungee;

    @Setting
    private String version;
}

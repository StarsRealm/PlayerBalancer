package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import com.jaimemartz.playerbalancer.velocity.ping.PingTactic;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


import java.util.List;

@ConfigSerializable
@Data
public class ServerCheckerProps {
    @Setting
    private boolean enabled;

    @Setting
    private PingTactic tactic;

    @Setting
    private int attempts;

    @Setting
    private int interval;

    @Setting
    private int timeout;

    @Setting(value = "marker-descs")
    private List<String> markerDescs;

    @Setting(value = "debug-info")
    private boolean debug;
}

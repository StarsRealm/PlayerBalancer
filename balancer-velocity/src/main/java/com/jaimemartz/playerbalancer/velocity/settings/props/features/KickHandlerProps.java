package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


import java.util.List;
import java.util.Map;

@ConfigSerializable
@Data
public class KickHandlerProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean inverted;

    @Setting
    private List<String> reasons;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting(value = "force-principal")
    private boolean forcePrincipal;

    @Setting
    private Map<String, String> rules;

    @Setting(value = "debug-info")
    private boolean debug;
}

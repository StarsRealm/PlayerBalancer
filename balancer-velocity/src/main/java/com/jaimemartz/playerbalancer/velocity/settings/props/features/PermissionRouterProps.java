package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


import java.util.Map;

@ConfigSerializable
@Data
public class PermissionRouterProps {
    @Setting
    private boolean enabled;

    @Setting
    private Map<String, Map<String, String>> rules;
}

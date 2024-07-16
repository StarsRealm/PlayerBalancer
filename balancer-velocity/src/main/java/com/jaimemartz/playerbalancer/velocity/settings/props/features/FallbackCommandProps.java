package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import com.jaimemartz.playerbalancer.velocity.settings.props.shared.CommandProps;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@Data
public class FallbackCommandProps {
    @Setting
    private boolean enabled;

    @Setting
    private CommandProps command;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting(value = "prevent-same-section")
    private boolean preventSameSection;

    @Setting
    private Map<String, String> rules;
}

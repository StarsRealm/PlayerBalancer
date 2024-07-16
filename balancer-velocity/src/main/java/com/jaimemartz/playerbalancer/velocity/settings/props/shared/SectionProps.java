package com.jaimemartz.playerbalancer.velocity.settings.props.shared;

import com.jaimemartz.playerbalancer.velocity.connection.ProviderType;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


import java.util.List;

@ConfigSerializable
@Data
public class SectionProps {
    @Setting
    private ProviderType provider;

    @Setting
    private String alias;

    @Setting(value = "parent")
    private String parentName;

    @Setting(value = "servers")
    private List<String> serverEntries;

    @Setting(value = "section-command")
    private CommandProps commandProps;

    @Setting(value = "section-server")
    private String serverName;
}

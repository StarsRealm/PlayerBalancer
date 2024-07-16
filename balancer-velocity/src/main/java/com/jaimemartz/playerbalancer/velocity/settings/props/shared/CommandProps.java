package com.jaimemartz.playerbalancer.velocity.settings.props.shared;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


import java.util.Collections;
import java.util.List;

@ConfigSerializable
@Data
public class CommandProps {
    @Setting
    private String name;

    @Setting
    private String permission;

    @Setting
    private List<String> aliases;

    public String getPermission() {
        if (permission != null) {
            return permission;
        } else {
            return "";
        }
    }

    public List<String> getAliases() {
        if (aliases != null) {
            return aliases;
        } else {
            return Collections.emptyList();
        }
    }

    public String[] getAliasesArray() {
        if (aliases != null) {
            return aliases.toArray(new String[aliases.size()]);
        } else {
            return new String[] {};
        }
    }
}

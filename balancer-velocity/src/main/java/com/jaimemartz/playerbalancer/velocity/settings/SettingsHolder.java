package com.jaimemartz.playerbalancer.velocity.settings;

import com.jaimemartz.playerbalancer.velocity.settings.props.FeaturesProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.GeneralProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.MessagesProps;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;


@ConfigSerializable
@Data
public class SettingsHolder {
    @Setting(value = "general")
    private GeneralProps generalProps;

    @Setting(value = "messages")
    private MessagesProps messagesProps;

    @Setting(value = "features")
    private FeaturesProps featuresProps;
}

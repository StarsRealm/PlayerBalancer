package com.jaimemartz.playerbalancer.velocity.listeners;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.velocity.data.PlayerInfo;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import org.bson.UuidRepresentation;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServerConnectListener {
    private final PlayerBalancer plugin;
    private final Datastore datastore;
    private final MongoClient mongoClient;
    private static final TreeSet<PlayerInfo> playerInfoCache = new TreeSet<>();

    public ServerConnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString("mongodb://root:wyd3#monster@localhost:27017"))
                .build();

        mongoClient = MongoClients.create(settings);
        ClassLoader classLoader = ServerConnectListener.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        datastore = Morphia.createDatastore(mongoClient, MorphiaConfig.load(), classLoader);
        try (var iter = datastore.find(PlayerInfo.class).iterator()) {
            iter.forEachRemaining(playerInfoCache::add);
        }
    }

    @Subscribe
    public void onJoin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        String username = player.getUsername();
        PlayerInfo build = new PlayerInfo(null, username, uniqueId);
        if (playerInfoCache.contains(build)) {
            return;
        }
        datastore.save(build);
    }

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        RegisteredServer target = event.getOriginalServer();

        if (PlayerLocker.isLocked(player))
            return;

        ServerSection section = getSection(player, target);

        if (section == null)
            return;

        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Consumer<Boolean> callback) {
                if (plugin.getSectionManager().isReiterative(section)) {
                    ServerAssignRegistry.assignTarget(player, section, server);
                }

                plugin.getProxyServer().getServer(server.getName()).ifPresent(registeredServer -> {
                    event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer));
                    callback.accept(true);
                });
            }
        }.execute();
    }

    private ServerSection getSection(Player player, RegisteredServer target) {
        ServerSection section = plugin.getSectionManager().getByServer(target);

        if (section != null) {
            // Checks only for servers (not the section server)
            if (!target.equals(section.getServer())) {
                if (plugin.getSectionManager().isDummy(section)) {
                    return null;
                }

                if (player.hasPermission("playerbalancer.bypass")) {
                    MessageUtils.send(player, plugin.getSettings().getMessagesProps().getBypassMessage());
                    return null;
                }

                ServerConnection serverConnection = player.getCurrentServer().orElse(null);
                if (serverConnection != null && section.getServers().contains(serverConnection.getServer())) {
                    if (plugin.getSectionManager().isReiterative(section)) {
                        ServerAssignRegistry.assignTarget(player, section, target.getServerInfo());
                    }
                    return null;
                }
            }
        }

        return section;
    }

    public TreeMap<String, UUID> getAllServerPlayer() {
        TreeMap<String, UUID> map = new TreeMap<>();
        for (PlayerInfo playerInfo : playerInfoCache) {
            map.put(playerInfo.getName(), playerInfo.getUuid());
        }
        return map;
    }

    public void close() {
        mongoClient.close();
        playerInfoCache.clear();
    }
}

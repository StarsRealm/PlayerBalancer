package com.jaimemartz.playerbalancer.velocity.listeners;

import com.google.gson.Gson;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.lettuce.core.*;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.time.Duration;
import java.util.*;

public class RedisEventListener implements AutoCloseable {
    final RedisClient redisClient;
    final StatefulRedisPubSubConnection<String, String> connection;
    public static final String res = "playerbalancer:getallplayer";
    public static final String to = "playerbalancer:setallplayer";

    public RedisEventListener(PlayerBalancer plugin) {
        redisClient = RedisClient.create(RedisURI.builder(RedisURI.create("redis://localhost")).withTimeout(Duration.ofSeconds(3)).build());
        connection = redisClient.connectPubSub();
        final RedisPubSubAsyncCommands<String, String> async = connection.async();
        final Gson gson = new Gson();
        connection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                if (channel.equals(res)) {
                    async.publish(to, gson.toJson(plugin.getServerConnectListener().getAllServerPlayer()));
                }
            }

            @Override
            public void message(String pattern, String channel, String message) {

            }

            @Override
            public void subscribed(String channel, long count) {

            }

            @Override
            public void psubscribed(String pattern, long count) {

            }

            @Override
            public void unsubscribed(String channel, long count) {

            }

            @Override
            public void punsubscribed(String pattern, long count) {

            }
        });

        async.subscribe(res);
    }

    @Override
    public void close() {
        connection.close();
        redisClient.shutdown();
    }
}

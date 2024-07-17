package com.jaimemartz.playerbalanceraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import it.unimi.dsi.fastutil.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class RedisEventListener implements AutoCloseable {
    final RedisClient redisClient;
    final StatefulRedisPubSubConnection<String, String> connection;
    public static final String to = "playerbalancer:getallplayer";
    public static final String res = "playerbalancer:setallplayer";

    private static final AtomicReference<List<Pair<String, UUID>>> result = new AtomicReference<>(null);
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public RedisEventListener() {
        redisClient = RedisClient.create(RedisURI.create("redis://localhost"));
        connection = redisClient.connectPubSub();
        final Gson gson = new Gson();
        final RedisPubSubCommands<String, String> sync = connection.sync();

        connection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                if (channel.equals(res)) {
                    List<String> strings = gson.fromJson(message, new TypeToken<>() {
                    });
                    List<Pair<String, UUID>> r = strings.stream().map(p -> {
                        String[] split = p.split(":");
                        String name = split[0];
                        UUID uuid = UUID.fromString(split[1]);
                        return Pair.of(name, uuid);
                    }).toList();
                    result.set(r);
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
        sync.subscribe(res);
    }

    public CompletableFuture<List<Pair<String, UUID>>> getAllServerPlayer(int page, int size) {
        final RedisPubSubCommands<String, String> sync = connection.sync();
        if (result.get() == null) {
            sync.publish(to, "");
        }
        return CompletableFuture.supplyAsync(() -> {
            while (result.get() == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int startIndex = page * size;
            int endIndex = startIndex + size;
            List<Pair<String, UUID>> allPlayers = result.get();
            endIndex = Math.min(endIndex, allPlayers.size());
            if (startIndex < allPlayers.size()) {
                allPlayers = allPlayers.subList(startIndex, endIndex);
            } else {
                allPlayers = new ArrayList<>();
            }
            return allPlayers;
        }, executorService);
    }

    public void setDirty() {
        final RedisPubSubCommands<String, String> sync = connection.sync();
        result.set(null);
        sync.publish(to, "");
    }

    @Override
    public void close() throws Exception {
        connection.close();
        redisClient.shutdown();
        executorService.shutdownNow();
    }
}

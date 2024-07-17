package com.jaimemartz.playerbalanceraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

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

    private static final AtomicReference<List<Map.Entry<String, UUID>>> result = new AtomicReference<>(null);
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
                    List<Map.Entry<String, UUID>> r = gson.fromJson(message, new TypeToken<>() {
                    });
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

    public CompletableFuture<List<Map.Entry<String, UUID>>> getAllServerPlayer(int page, int size) {
        final RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish(to, "");
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
            List<Map.Entry<String, UUID>> allPlayers = result.get();
            if (startIndex < allPlayers.size()) {
                allPlayers = allPlayers.subList(startIndex, endIndex);
            } else {
                allPlayers = new ArrayList<>();
            }
            for (int i = allPlayers.size(); i < size; i++) {
                allPlayers.add(null);
            }
            return allPlayers;
        }, executorService);
    }

    @Override
    public void close() throws Exception {
        connection.close();
        redisClient.shutdown();
        executorService.shutdownNow();
    }
}

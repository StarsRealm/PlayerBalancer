package com.jaimemartz.playerbalanceraddon;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RedisEventListener implements AutoCloseable {
    final RedisClient redisClient;
    final StatefulRedisPubSubConnection<String, String> connection;
    public static final String to = "playerbalancer:getallplayer";
    public static final String res = "playerbalancer:setallplayer";

    private static final AtomicReference<TreeMap<String, UUID>> result = new AtomicReference<>(null);
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public RedisEventListener() {
        redisClient = RedisClient.create(RedisURI.builder(RedisURI.create("redis://localhost")).withTimeout(Duration.ofSeconds(3)).build());
        connection = redisClient.connectPubSub();
        final Gson gson = new Gson();
        final RedisPubSubAsyncCommands<String, String> async = connection.async();

        connection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                if (channel.equals(res)) {
                    try {
                        TreeMap<String, UUID> r = gson.fromJson(message, new TypeToken<>() {
                        });
                        result.set(r);
                    } catch (JsonSyntaxException ignore) {
                        result.set(null);
                    }
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
            TreeMap<String, UUID> allPlayers = result.get();
            List<Map.Entry<String, UUID>> list = allPlayers.sequencedEntrySet().stream().collect(Collectors.toList());
            endIndex = Math.min(endIndex, list.size());
            if (startIndex < list.size()) {
                list = list.subList(startIndex, endIndex);
            } else {
                list = new ArrayList<>();
            }
            for (int i = list.size(); i < size; i++) {
                list.add(null);
            }
            return list;
        }, executorService);
    }

    public CompletableFuture<TreeMap<String, UUID>> getAllServerPlayer() {
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
            return result.get();
        }, executorService);
    }

    @Override
    public void close() throws Exception {
        connection.close();
        redisClient.shutdown();
        executorService.shutdownNow();
    }
}

package com.le.pool.redis;

import com.le.pool.tool.ConnectionPoolConfig;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

public class RedisConnectionPoolTest {

    @Before
    public void before() throws Exception {

        Thread th = new Thread(new Runnable() {

            private ServerSocket serverSocket;

            @Override
            public void run() {

                try {
                    serverSocket = new ServerSocket(RedisConfig.DEFAULT_PORT);

                    serverSocket.accept();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        th.setDaemon(true);
        th.start();
    }

    @Test
    public void test() throws Exception {

        Properties prop = new Properties();
        prop.setProperty(RedisConfig.ADDRESS_PROPERTY,
                RedisConfig.DEFAULT_HOST + ":" + RedisConfig.DEFAULT_PORT);
        prop.setProperty(RedisConfig.CONN_TIMEOUT_PROPERTY,
                RedisConfig.DEFAULT_TIMEOUT + "");
        prop.setProperty(RedisConfig.SO_TIMEOUT_PROPERTY,
                RedisConfig.DEFAULT_TIMEOUT + "");
        prop.setProperty(RedisConfig.DATABASE_PROPERTY,
                RedisConfig.DEFAULT_DATABASE + "");

        try {
            RedisConnectionPool pool = new RedisConnectionPool(
                    RedisConfig.DEFAULT_HOST, RedisConfig.DEFAULT_PORT);
            pool.close();
        } catch (Exception e) {
        }

        try {
            RedisConnectionPool pool = new RedisConnectionPool(
                    new ConnectionPoolConfig(), prop);
            pool.close();
        } catch (Exception e) {
        }

        try {
            RedisConnectionPool pool = new RedisConnectionPool(prop);

            pool.close();
        } catch (Exception e) {
        }

        RedisConnectionPool pool = new RedisConnectionPool(
                RedisConfig.DEFAULT_HOST, RedisConfig.DEFAULT_PORT);

        try {
            pool.getConnection();
        } catch (Exception e) {
        }

        try {

            Jedis jedis = pool.getConnection();

            pool.returnConnection(jedis);

            jedis.disconnect();

            try {
                jedis.auth("");
            } catch (Exception e) {
            }

            pool.returnConnection(jedis);

        } catch (Exception e) {
        }

        try {
            Jedis jedis = pool.getConnection();

            pool.invalidateConnection(jedis);

        } catch (Exception e) {

        }

        pool.returnConnection(null);

        pool.invalidateConnection(null);

        pool.close();
    }
}

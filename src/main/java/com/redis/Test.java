/**package com.redis;

import redis.clients.jedis.Jedis;

public class Test {

    public static void main(String[] args) {

        // test1();

        // test2();

        // test3();

        test4();

    }

    public static void test1() {

        Jedis jedis = new Jedis("192.168.43.129", 6379); jedis.close();

    }

    public static void test2() {

        try (Jedis jedis = new Jedis("192.168.43.129", 6379);) {
            jedis.set("test", "lbwnb"); System.out.println(jedis.get("test"));
        }

    }

    public static void test3() {

        try (Jedis jedis = new Jedis("192.168.43.129", 6379)) {

            jedis.hset("hhh", "name", "sxc"); jedis.hset("hhh", "sex", "19");
            jedis.hgetAll("hhh").forEach((k, v) -> System.out.println(k + ": " + v));

        }

    }

    public static void test4() {

        try (Jedis jedis = new Jedis("192.168.43.129", 6379)) {

            jedis.lpush("mylist", "111", "222", "333");
            jedis.lrange("mylist", 0, -1).forEach(System.out::println);

        }

    }

}**/
package com.project.myservice.infrastructure.common

import com.project.myservice.domain.common.CacheRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisCacheRepository(
    val redisTemplate: RedisTemplate<String, String>
) : CacheRepository {

    override fun save(key: String, value: String, ttl: Long) =
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS)

    override fun find(key: String): String? = redisTemplate.opsForValue().get(key)

    override fun delete(key: String): Boolean = redisTemplate.delete(key)
}
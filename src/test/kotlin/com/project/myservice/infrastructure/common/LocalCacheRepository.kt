package com.project.myservice.infrastructure.common

import com.project.myservice.domain.common.CacheRepository

class LocalCacheRepository : CacheRepository {

    val data = mutableMapOf<String, String>()

    override fun save(key: String, value: String, ttl: Long) {
        this.data[key] = value
    }

    override fun find(key: String): String? =
        if (data.containsKey(key)) data[key] else null

    override fun delete(key: String): Boolean =
        data.remove(key)
            .run {
                this != null
            }
}
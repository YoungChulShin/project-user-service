package com.project.myservice.domain.common

interface CacheRepository {

    fun save(key: String, value: String, ttl: Long)

    fun find(key: String): String?

    fun delete(key: String): Boolean
}
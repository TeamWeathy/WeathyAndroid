package team.weathy.api

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

interface ApiSerializer<T> : JsonSerializer<T>, JsonDeserializer<T>
package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val prompt: String,
    val aspectRatio: String,
    val durationSeconds: Int,
    val videoUri: String,
    val thumbnailUri: String?,
    val providerName: String,
    val isDemo: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

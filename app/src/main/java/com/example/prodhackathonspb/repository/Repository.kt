package com.example.prodhackathonspb.repository

import com.example.prodhackathonspb.network.ServerStatusService
import javax.inject.Inject

class Repository @Inject constructor(private val service: ServerStatusService) {
    suspend fun checkStatus(): Boolean {
        return runCatching {
            service.getStatus()
        }.fold(onSuccess = {
            true
        }, onFailure = {
            false
        })
    }
}
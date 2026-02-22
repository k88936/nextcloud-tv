package top.k88936.nextcloud_tv.data.network

import io.ktor.client.HttpClient
import top.k88936.nextcloud_tv.data.local.Credentials

interface INextcloudClient {
    fun getClient(): HttpClient?
    fun getCredentials(): Credentials?
}

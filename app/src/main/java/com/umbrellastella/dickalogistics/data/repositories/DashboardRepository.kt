package com.umbrellastella.dickalogistics.data.repositories

import com.umbrellastella.dickalogistics.data.models.DashboardResponse
import com.umbrellastella.dickalogistics.data.RetrofitClient
import retrofit2.Response

class DashboardRepository {

    suspend fun obtenerEstadisticasDashboard(): Result<DashboardResponse> {
        return try {
            val respuesta = RetrofitClient.apiService.obtenerEstadisticas()
            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Error al obtener estadísticas: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
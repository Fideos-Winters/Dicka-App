package com.umbrellastella.dickalogistics.data

import com.umbrellastella.dickalogistics.data.models.*
import com.umbrellastella.dickalogistics.home.ClienteFormState
import com.umbrellastella.dickalogistics.home.ProductoFormState
import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface DickaApiService {
    // Auth & Dashboard

    @POST("api/login") // <-- Solo "login", no "api/login"
    suspend fun hacerLogin(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/dashboard/estadisticas")
    suspend fun obtenerEstadisticas(): Response<DashboardResponse>

    // Inventario & Lotes
    @GET("api/inventario")
    suspend fun obtenerInventarioGeneral(): Response<List<ProductoModel>>

    @GET("api/inventario/{sku}/lotes")
    suspend fun obtenerLotesPorProducto(@Path("sku") sku: String): Response<List<LoteDetalleModel>>

    @GET("api/lotes")
    suspend fun obtenerLotes(): Response<List<LoteEntradaModel>>

    @POST("api/lotes")
    suspend fun guardarLote(@Body request: LoteEntradaModel): Response<Map<String, Any>>

    @PUT("api/lotes/{id}")
    suspend fun actualizarLote(
        @Path("id") id: Int,
        @Body request: LoteEntradaModel
    ): Response<Map<String, Any>>

    // Ubicaciones (Lógica de Ubicar)
//    @GET("api/lote-ubicaciones/filtrar")
//    suspend fun filtrarLotes(@Query("lote") lote: String? = null, @Query("folio") folio: Int? = null): Response<Map<String, List<LoteUbicacionModel>>>

    //    @GET("api/lotes/{id}/recomendar")
//    suspend fun recomendarUbicaciones(@Path("id") loteId: Int): Response<List<String>>
    @GET("api/lotes/{id}/recomendar")
    suspend fun recomendarUbicaciones(@Path("id") loteId: Int): Response<RecomendacionResponse>


    //    @POST("api/registrar-ubicacion")
//    suspend fun registrarUbicacion(@Body request: Map<String, Any>): Response<Map<String, Any>>
    @POST("api/registrar-ubicacion")
    suspend fun registrarUbicacion(@Body request: RegistrarUbicacionRequest): Response<Map<String, Any>>


    @PUT("api/lotes/{id}/terminado")
    suspend fun terminarUbicar(@Path("id") loteId: Int): Response<Unit>

    // Productos
    @GET("api/productos")
    suspend fun obtenerProductos(): Response<List<ProductoResponse>>

    @GET("api/productos/listado-simple")
    suspend fun obtenerProductosListadoSimple(): Response<List<ProductoResponse>>

    @POST("api/productos")
    suspend fun registrarProducto(@Body request: ProductoFormState): Response<Map<String, Any>>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Int,
        @Body request: ProductoFormState
    ): Response<Map<String, Any>>

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int): Response<Map<String, Any>>

    // Clientes
    @GET("api/clientes")
    suspend fun obtenerClientes(): Response<List<ClienteResponse>>

    @GET("api/clientes/listado-simple")
    suspend fun obtenerClientesListado(): Response<List<ClienteListadoItem>>

    @POST("api/clientes")
    suspend fun registrarCliente(@Body request: ClienteFormState): Response<Map<String, Any>>

    @PUT("api/clientes/{id}")
    suspend fun actualizarCliente(
        @Path("id") id: Int,
        @Body request: ClienteFormState
    ): Response<Map<String, Any>>

    @DELETE("api/clientes/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int): Response<Map<String, Any>>

    // Salidas
    @GET("api/salidas")
    suspend fun obtenerSalidas(): Response<List<LoteSalidaModel>>

    @POST("api/registrar-salida")
    suspend fun registrarSalida(@Body request: LoteSalidaModel): Response<SalidaResponse>

    // Usuarios
    @GET("api/usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioResponse>>

    @POST("api/usuarios")
    suspend fun storeUsuario(@Body request: UsuarioRequest): Response<Unit>

    @PUT("api/usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body request: UsuarioRequest): Response<Unit>

    @DELETE("api/usuarios/{id}")
    suspend fun destroyUsuario(@Path("id") id: Int): Response<Unit>

    // Infraestructura
    @GET("api/racks/mapa-completo")
    suspend fun getMapaCompleto(): Response<List<RackResponse>>

    @GET("api/lote-ubicaciones/filtrar")
    suspend fun filtrarLotes(
        @Query("lote") lote: String? = null,
        @Query("folio") folio: Int? = null,
        @Query("page") page: Int? = 1,
        @Query("estado") estado: String? = "ubicados"
    ): Response<LoteRespuestaModel>

    @Streaming // <-- Evita que la app se quede sin memoria (OutOfMemory)
    @GET("api/lotes/{id}/imprimir-etiquetas") // <-- Asegúrate de que la ruta coincida con Laravel (¿lleva /api/ al principio?)
    suspend fun descargarEtiquetasPdf(@Path("id") loteId: Int): Response<ResponseBody>

    @PUT("api/lotes/{id}/recibir")
    suspend fun recibirLote(@Path("id") id: Int): Response<com.google.gson.JsonObject>

    @GET("api/lotes/{id}")
    suspend fun detalleLote(@Path("id") id: Int): Response<LoteDetalleModel>

}
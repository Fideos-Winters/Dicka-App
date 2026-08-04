package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

// 1. RackResponse
data class RackResponse(
    @SerializedName("rack") val rack: String,
    @SerializedName("niveles") val niveles: List<Nivel>
)

// 2. Nivel
data class Nivel(
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("ubicaciones") val ubicaciones: List<PosicionRack>
)

// 3. PosicionRack (Modelo único, renombrado para evitar conflictos)
data class PosicionRack(
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("codigo") val codigo: String = "",
    @SerializedName("estado") val estado: String = "Vacio",
    @SerializedName("lote_id") val loteId: Int? = null,
    @SerializedName("pallet_numero") val palletNumero: Int? = null
)

// 4. LoteDetalle
data class LoteDetalle(
    @SerializedName("lote_id") val loteId: Int?,
    @SerializedName("pallet_numero") val palletNumero: Int?
)

// 5. LoteUbicacionModel
data class LoteUbicacionModel(
    @SerializedName("id") val id: Int,

    @SerializedName("qr_ubicacion", alternate = ["ubi", "ubicacion"]) val qrUbicacion: String?,
    @SerializedName("lote_id") val loteId: Int?,
    @SerializedName("pallet_numero", alternate = ["num_pallet"]) val palletNumero: Int?,
    @SerializedName("lote") val loteInfo: LoteInfoDetalleModel? = null
) {
    // 💡 Si no viene el nombre del producto, muestra el Lote o Folio real
    fun getNombreProducto(): String {
        val nombre = loteInfo?.producto?.nombre?.trim()
        if (!nombre.isNullOrEmpty()) return nombre

        val sku = loteInfo?.producto?.sku?.trim()
        if (!sku.isNullOrEmpty()) return "SKU: $sku"

        val codigo = loteInfo?.codigoLote?.trim()
        if (!codigo.isNullOrEmpty()) return "Lote: $codigo"

        val folio = loteInfo?.folio
        if (folio != null && folio > 0) return "Folio: #$folio"

        return "Lote #${loteId ?: id}"
    }

    fun getSku(): String {
        return loteInfo?.producto?.sku?.trim() ?: ""
    }

    fun getCodigoLote(): String {
        return loteInfo?.codigoLote?.trim() ?: (loteId?.toString() ?: id.toString())
    }

    fun getFolio(): String {
        return loteInfo?.folio?.toString() ?: "S/F"
    }

    fun getTotalPallets(): String {
        return loteInfo?.numPalets?.toString() ?: "1"
    }
}

data class LoteInfoDetalleModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("folio") val folio: Int? = null,
    @SerializedName("lote") val codigoLote: String? = null,
    @SerializedName("caducidad") val caducidad: String? = null,
    @SerializedName("numPalets") val numPalets: Int? = null,
    @SerializedName("piezasPalet") val piezasPalet: Int? = null,
    @SerializedName("producto_id") val productoId: Int? = null,
    @SerializedName("producto") val producto: ProductoInfoModel? = null
)

data class ProductoInfoModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("nombre") val nombre: String? = null
)



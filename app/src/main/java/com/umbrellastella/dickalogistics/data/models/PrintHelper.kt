package com.umbrellastella.dickalogistics.utils // Ajusta el paquete según tu estructura

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import com.umbrellastella.dickalogistics.data.models.PdfDocumentAdapter
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
// IMPORTANTE: Asegúrate de importar tu PdfDocumentAdapter aquí si está en otra carpeta
// import com.umbrellastella.dickalogistics.models.PdfDocumentAdapter

object PrintHelper {

    fun guardarEImprimirPdf(context: Context, body: ResponseBody, loteId: Int) {
        try {
            // 1. Crear un archivo temporal en la memoria caché del celular
            val file = File(context.cacheDir, "etiquetas_lote_$loteId.pdf")
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                val buffer = ByteArray(4096)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }

            // 2. Si se guardó bien, mandar a imprimir
            lanzarGestorDeImpresion(context, file)

        } catch (e: Exception) {
            Log.e("PrintHelper", "Error al guardar el PDF: ${e.message}")
        }
    }

    private fun lanzarGestorDeImpresion(context: Context, pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Impresion_Lote_${pdfFile.name}"

        // Aquí usamos la clase que arreglamos en el paso anterior
        val printAdapter = PdfDocumentAdapter(pdfFile)

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}
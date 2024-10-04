package com.saeo.cyrsaer.uiPantallas

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.google.firebase.database.FirebaseDatabase
import com.saeo.cyrsaer.R
import java.io.FileOutputStream

object AdministradorUtils {

    fun cambiarEstadoCamarero(uid: String, habilitado: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val camareroRef = database.reference.child("usuarios").child(uid)

        camareroRef.child("habilitado").setValue(habilitado)
            .addOnSuccessListener {
                // Estado del camarero actualizado correctamente
            }
            .addOnFailureListener { exception ->
                // Error al actualizar el estado del camarero
                // Manejar el error
            }
    }

    fun imprimirReporte(reportes: Map<String, Double>, context: Context) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${context.getString(R.string.app_name)} Reporte de ventas"

        printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutFailed("Cancelado")
                    return
                }

                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    val outputStream = FileOutputStream(destination?.fileDescriptor)
                    outputStream.write("Reporte de ventas\n".toByteArray())
                    outputStream.write("------------------\n".toByteArray())
                    reportes.forEach { (fecha, total) ->
                        outputStream.write("Ventas del $fecha: $${total}\n".toByteArray())
                    }
                    outputStream.close()

                    callback?.onWriteFinished(pages)
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.toString())
                }
            }

        }, null)
    }

}


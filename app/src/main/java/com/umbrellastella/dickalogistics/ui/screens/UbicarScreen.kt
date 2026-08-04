package com.umbrellastella.dickalogistics.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.umbrellastella.dickalogistics.components.QrCodeAnalyzer
import com.umbrellastella.dickalogistics.data.models.LoteUbicacionModel
import com.umbrellastella.dickalogistics.data.models.QrLoteModel
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.theme.DickaOrange
import com.umbrellastella.dickalogistics.viewmodel.UbicarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicarScreen(
    viewModel: UbicarViewModel = viewModel(),
    onNavigateToMapa: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var loteParaUbicar by remember { mutableStateOf<LoteUbicacionModel?>(null) }

    // Estados del Escáner QR
    var showCameraDialog by remember { mutableStateOf(false) }
    var loteScanneado by remember { mutableStateOf<QrLoteModel?>(null) }
    var showBottomSheetRecomendacionesQr by remember { mutableStateOf(false) }

    // Variables para la ubicación existente
    var showDialogUbicacionExistente by remember { mutableStateOf(false) }
    var ubicacionEncontrada by remember { mutableStateOf("") }
    var showDialogLotePendiente by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // Cargamos los lotes al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarLotes(reset = true)
    }

    // Seleccionamos la lista activa según la pestaña actual
    val listaLotesMostrada = if (viewModel.pestañaActual == "ubicados") {
        viewModel.listaUbicados
    } else {
        viewModel.listaNoUbicados
    }

    val lotesFiltrados = remember(listaLotesMostrada, searchQuery) {
        listaLotesMostrada.filter { lote ->
            val query = searchQuery.trim()
            query.isBlank() ||
                    lote.getNombreProducto().contains(query, ignoreCase = true) ||
                    lote.getSku().contains(query, ignoreCase = true) ||
                    lote.getCodigoLote().contains(query, ignoreCase = true) ||
                    lote.getFolio().contains(query, ignoreCase = true) ||
                    (lote.qrUbicacion?.contains(query, ignoreCase = true) == true) ||
                    (lote.loteId?.toString()?.contains(query) == true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasCameraPermission) {
                        showCameraDialog = true
                    } else {
                        launcherPermission.launch(Manifest.permission.CAMERA)
                    }
                },
                containerColor = DickaOrange,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear QR de Lote"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F4F8))
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable { onNavigateToMapa() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF475569))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Map, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("VER MAPA DE RACK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Pestaña Ubicados
                    BotonFiltro(
                        "Ubicados",
                        "${viewModel.totalUbicados} Lotes",
                        Color(0xFF10B981),
                        Icons.Default.LocationOn,
                        viewModel.pestañaActual == "ubicados",
                        Modifier.weight(1f)
                    ) {
                        viewModel.cambiarPestaña("ubicados")
                    }

                    // Botón Pestaña No Ubicados
                    BotonFiltro(
                        "No Ubicados",
                        "${viewModel.totalNoUbicados} Lotes",
                        Color(0xFFEF4444),
                        Icons.Default.LocationOff,
                        viewModel.pestañaActual == "no_ubicados",
                        Modifier.weight(1f)
                    ) {
                        viewModel.cambiarPestaña("no_ubicados")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar ID de Lote o SKU...") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(lotesFiltrados) { lote ->
                        ItemLote(lote) {
                            loteParaUbicar = lote
                            showMenu = true
                        }
                    }

                    // Botón dinámico para Cargar Más (Paginación por bloques)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { viewModel.cargarMas() },
                                colors = ButtonDefaults.buttonColors(containerColor = DickaBlue)
                            ) {
                                Text("Cargar más lotes...", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // ================= BOTTOM SHEET NORMAL DE ASIGNACIÓN =================
        if (showMenu && loteParaUbicar != null) {
            AsignarUbicacionBottomSheet(
                lote = loteParaUbicar!!,
                viewModel = viewModel,
                onDismiss = { showMenu = false },
                onConfirmar = { ubicacion ->
                    viewModel.asignarUbicacion(loteParaUbicar!!, ubicacion)
                    showMenu = false
                }
            )
        }

        // ================= DIÁLOGO CÁMARA (Lector QR) =================
        if (showCameraDialog) {
            Dialog(
                onDismissRequest = { showCameraDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    val lifecycleOwner = LocalLifecycleOwner.current

                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = androidx.camera.core.Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                                    .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build().also {
                                        it.setAnalyzer(
                                            ContextCompat.getMainExecutor(ctx),
                                            QrCodeAnalyzer { textoQr ->
                                                val qrData = parsearQrJson(textoQr)
                                                if (qrData != null) {
                                                    loteScanneado = qrData
                                                    showCameraDialog = false

                                                    val loteId = qrData.loteId

                                                    if (loteId != null && loteId > 0) {
                                                        // 🟢 Consultamos el estado real al servidor mediante el ID del QR
                                                        viewModel.verificarEstatusLote(loteId) { estadoRealServidor ->
                                                            val esPendiente = estadoRealServidor.equals("Pendiente", ignoreCase = true)

                                                            val estaUbicado = !estadoRealServidor.isNullOrEmpty() &&
                                                                    !estadoRealServidor.equals("SIN UBICAR", ignoreCase = true) &&
                                                                    !estadoRealServidor.equals("No Ubicado", ignoreCase = true) &&
                                                                    !estadoRealServidor.equals("Pendiente", ignoreCase = true) &&
                                                                    !estadoRealServidor.equals("null", ignoreCase = true)

                                                            if (estaUbicado) {
                                                                ubicacionEncontrada = estadoRealServidor
                                                                showBottomSheetRecomendacionesQr = false
                                                                showDialogUbicacionExistente = true
                                                                showDialogLotePendiente = false
                                                            } else if (esPendiente) {
                                                                // 🟡 Laravel confirma que sigue pendiente en rampa
                                                                showBottomSheetRecomendacionesQr = false
                                                                showDialogUbicacionExistente = false
                                                                showDialogLotePendiente = true
                                                            } else {
                                                                // 🔴 Laravel confirma que ya es "No Ubicado", listo para acomodar
                                                                viewModel.cargarRecomendaciones(loteId)
                                                                showDialogUbicacionExistente = false
                                                                showBottomSheetRecomendacionesQr = true
                                                                showDialogLotePendiente = false
                                                            }
                                                        }
                                                    } else {
                                                        android.widget.Toast.makeText(context, "Error al leer ID del lote del QR", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        )
                                    }

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .align(Alignment.Center)
                            .border(3.dp, DickaOrange, RoundedCornerShape(16.dp))
                    )

                    IconButton(
                        onClick = { showCameraDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }
            }
        }

        // ================= BOTTOM SHEET CON RECOMENDACIONES DE QR =================
        if (showBottomSheetRecomendacionesQr && loteScanneado != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheetRecomendacionesQr = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = loteScanneado?.producto ?: "Producto Desconocido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DickaBlue
                    )
                    Text(
                        text = "Lote: ${loteScanneado?.lot} | Pallet N°: ${loteScanneado?.palletNumero ?: 1}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "📍 Racks Sugeridos para Acomodo:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DickaBlue
                    )

                    Spacer(Modifier.height(8.dp))

                    val recomendaciones = viewModel.listaUbicacionesDisponibles

                    if (viewModel.isLoadingRecomendaciones) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = DickaOrange
                        )
                    } else if (recomendaciones.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                        ) {
                            Text(
                                text = "⚠️ Laravel sugirió racks que ya están ocupados. Error de sincronización en BD.",
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        recomendaciones.forEach { ubicacionSugerida ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ubicacionSugerida,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = DickaBlue
                                    )
                                    Button(
                                        onClick = {
                                            val tempLote = LoteUbicacionModel(
                                                id = loteScanneado?.loteId ?: 0,
                                                qrUbicacion = null,
                                                loteId = loteScanneado?.loteId,
                                                palletNumero = loteScanneado?.palletNumero ?: 1
                                            )
                                            viewModel.asignarUbicacion(
                                                lote = tempLote,
                                                ubicacion = ubicacionSugerida
                                            )
                                            showBottomSheetRecomendacionesQr = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DickaOrange)
                                    ) {
                                        Text("Acomodar Aquí")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================= DIÁLOGO LOTE YA UBICADO =================
        if (showDialogUbicacionExistente && loteScanneado != null) {
            AlertDialog(
                onDismissRequest = { showDialogUbicacionExistente = false },
                title = {
                    Text(
                        text = "Lote ya ubicado",
                        fontWeight = FontWeight.Bold,
                        color = DickaBlue
                    )
                },
                text = {
                    Text(
                        text = "El lote ${loteScanneado?.lot} del producto ${loteScanneado?.producto ?: ""} ya se encuentra descansando en la ubicación:\n\n📍 $ubicacionEncontrada",
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showDialogUbicacionExistente = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DickaOrange)
                    ) {
                        Text("Entendido", color = Color.White)
                    }
                },
                containerColor = Color.White
            )
        }

        // ================= DIÁLOGO LOTE PENDIENTE (RESTRICCIÓN) =================
        if (showDialogLotePendiente && loteScanneado != null) {
            AlertDialog(
                onDismissRequest = { showDialogLotePendiente = false },
                title = {
                    Text(
                        text = "Lote no registrado en entrada",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEAB308)
                    )
                },
                text = {
                    Text(
                        text = "El lote ${loteScanneado?.lot} todavía está pendiente de registrar.\n\nPor favor, escanéalo primero en el apartado de Entradas para marcarlo como recibido antes de ubicarlo en un rack.",
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showDialogLotePendiente = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DickaBlue)
                    ) {
                        Text("Entendido", color = Color.White)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

fun parsearQrJson(jsonTexto: String): QrLoteModel? {
    return runCatching {
        Gson().fromJson(jsonTexto, QrLoteModel::class.java)
    }.getOrNull()
}

@Composable
fun BotonFiltro(
    titulo: String,
    subtitulo: String,
    color: Color,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(90.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color else color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, null, tint = if (isSelected) Color.White else color)
            Text(titulo, color = if (isSelected) Color.White else color, fontWeight = FontWeight.Bold)
            Text(subtitulo, color = if (isSelected) Color.White else Color(0xFF64748B), fontSize = 11.sp)
        }
    }
}

@Composable
fun ItemLote(
    lote: LoteUbicacionModel,
    onClick: () -> Unit
) {
    val tituloPrincipal = lote.getNombreProducto()
    val codigoLote = lote.getCodigoLote()
    val folio = lote.getFolio()
    val totalPallets = lote.getTotalPallets()
    val palletNum = lote.palletNumero ?: 1

    val qrUbi = lote.qrUbicacion?.trim() ?: ""
    val estaUbicado = qrUbi.isNotEmpty() &&
            !qrUbi.equals("SIN UBICAR", ignoreCase = true) &&
            !qrUbi.equals("No Ubicado", ignoreCase = true) &&
            !qrUbi.equals("null", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(55.dp)
                    .background(
                        if (estaUbicado) Color(0xFF10B981) else Color(0xFFEF4444),
                        RoundedCornerShape(2.dp)
                    )
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = tituloPrincipal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF00223D)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Folio: $folio  |  Lote: $codigoLote",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "Pallet $palletNum de $totalPallets",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                color = if (estaUbicado) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (estaUbicado) qrUbi else "SIN UBICAR",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = if (estaUbicado) Color(0xFF047857) else Color(0xFFB91C1C)
                )
            }
        }
    }
}
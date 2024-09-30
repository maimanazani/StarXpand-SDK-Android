package com.starmicronics.starxpandsdk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.starmicronics.stario10.InterfaceType
import com.starmicronics.stario10.StarConnectionSettings
import com.starmicronics.stario10.StarPrinter
import com.starmicronics.stario10.starxpandcommand.DocumentBuilder
import com.starmicronics.stario10.starxpandcommand.MagnificationParameter
import com.starmicronics.stario10.starxpandcommand.PrinterBuilder
import com.starmicronics.stario10.starxpandcommand.DrawerBuilder
import com.starmicronics.stario10.starxpandcommand.StarXpandCommandBuilder
import com.starmicronics.stario10.starxpandcommand.printer.*
import com.starmicronics.stario10.starxpandcommand.drawer.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import android.content.Context
import android.graphics.Bitmap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

import com.starmicronics.stario10.starxpandcommand.printer.Alignment
import com.starmicronics.stario10.starxpandcommand.printer.CutType
import com.starmicronics.stario10.starxpandcommand.printer.ImageParameter
import com.starmicronics.stario10.starxpandcommand.printer.InternationalCharacterType
import com.starmicronics.starxpandsdk.R

class PrintingActivity : AppCompatActivity() {

    private val requestCode = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printing)

        val button = findViewById<Button>(R.id.buttonPrinting)
        button.setOnClickListener { onPressPrintButton() }

        // If you are using Android 12 and targetSdkVersion is 31 or later,
        // you have to request Bluetooth permission (Nearby devices permission) to use the Bluetooth printer.
        // https://developer.android.com/about/versions/12/features/bluetooth-permissions
        requestBluetoothPermission()
    }

    private fun onPressPrintButton() {
        val editTextIdentifier = findViewById<EditText>(R.id.editTextIdentifier)
        val identifier = editTextIdentifier.text.toString()

        val spinnerInterfaceType = findViewById<Spinner>(R.id.spinnerInterfaceType)
        val interfaceType = when (spinnerInterfaceType.selectedItem.toString()) {
            "LAN" -> InterfaceType.Lan
            "Bluetooth" -> InterfaceType.Bluetooth
            "USB" -> InterfaceType.Usb
            else -> return
        }

        val settings = StarConnectionSettings(interfaceType, identifier)
        val printer = StarPrinter(settings, applicationContext)

        // If you are using Android 12 and targetSdkVersion is 31 or later,
        // you have to request Bluetooth permission (Nearby devices permission) to use the Bluetooth printer.
        // https://developer.android.com/about/versions/12/features/bluetooth-permissions
        if (interfaceType == InterfaceType.Bluetooth || settings.autoSwitchInterface) {
            if (!hasBluetoothPermission()) {
                Log.d("Printing", "PERMISSION ERROR: You have to allow Nearby devices to use the Bluetooth printer.")
                return
            }
        }

        val logo = BitmapFactory.decodeResource(resources, R.drawable.logo_01)

        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.Default + job)

        scope.launch {
            try {
                // TSP100III series and TSP100IIU+ do not support actionPrintText because these products are graphics-only printers.
                // Please use the actionPrintImage method to create printing data for these products.
                // For other available methods, please also refer to "Supported Model" of each method.
                // https://star-m.jp/products/s_print/sdk/starxpand/manual/ja/android-kotlin-api-reference/stario10-star-xpand-command/printer-builder/action-print-image.html
                val builder = StarXpandCommandBuilder()
                builder.addDocument(
                    DocumentBuilder()
                        // To open a cash drawer, comment out the following code.
//                      .addDrawer(
//                          DrawerBuilder()
//                              .actionOpen(OpenParameter())
//                      )
                        .settingPrintableArea(72.0)
                        .addPrinter(
                            PrinterBuilder()
                                .actionPrintImage(ImageParameter(logo, 406))
                                .styleInternationalCharacter(InternationalCharacterType.Usa)
                                .styleCharacterSpace(0.0)
                                .styleAlignment(Alignment.Center)


                                .actionPrintImage(
                                    createImageParameterFromText(
                                        "Star Clothing Boutique\n" +
                                                "123 Star Road\n" +
                                                "City, State 12345\n" +
                                                "\n" +
                                                "Date:MM/DD/YYYY Time:HH:MM PM\n" +
                                                "------------------------------------------------\n" +
                                                "\n" +
                                                "SKU       Description   Total\n" +
                                                "300678566 PLAIN T-SHIRT 10.99\n" +
                                                "300692003 BLACK DENIM   29.99\n" +
                                                "300651148 BLUE DENIM    29.99\n" +
                                                "300642980 STRIPED DRESS 49.99\n" +
                                                "300638471 BLACK BOOTS   35.99\n" +
                                                "\n" +
                                                "Subtotal               156.95\n" +
                                                "Tax                      0.00\n" +
                                                "-----------------------------\n" +
                                                "\n" +
                                                "Total                 $156.95\n" +
                                                "-----------------------------\n" +
                                                "\n" +
                                                "Charge\n" +
                                                "156.95\n" +
                                                "Visa XXXX-XXXX-XXXX-0123\n" +
                                                "\n"
                                    ))
                                .actionCut(CutType.Full)
                        )
                )
                val commands = builder.getCommands()

                printer.openAsync().await()
                printer.printAsync(commands).await()

                Log.d("Printing", "Success")
            } catch (e: Exception) {
                Log.d("Printing", "Error: ${e}")
            } finally {
                printer.closeAsync().await()
            }
        }
    }

    private fun createImageParameterFromText(text: String):ImageParameter{
        val width = 384
        val typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        val bitmap = createBitmapFromText(text,22,width,typeface);
        return ImageParameter(bitmap,width)
    }

    private fun createBitmapFromText(
        text: String,textSize: Int,width: Int,typeface: Typeface?): Bitmap {
        val paint = Paint()
        val bitmap: Bitmap
        paint.textSize = textSize.toFloat()
        paint.typeface = typeface
        paint.getTextBounds(text, 0, text.length, Rect())
        val textPaint = TextPaint(paint)
        val builder = StaticLayout.Builder.obtain(text,0,text.length,textPaint,width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f,1f)
            .setIncludePad(false)

        val staticLayout = builder.build()

        // Create bitmap
        bitmap = Bitmap.createBitmap(
            staticLayout.width,
            staticLayout.height,
            Bitmap.Config.ARGB_8888
        )

        // Create canvas
        val canvas: Canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.translate(0f, 0f)
        staticLayout.draw(canvas)
        return bitmap

    }

    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return
        }

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                ), requestCode
            )
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true
        }

        return checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }
}
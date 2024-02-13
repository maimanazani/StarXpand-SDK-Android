package com.starmicronics.starxpandsdk.printingsamples

import android.content.Context
import android.graphics.BitmapFactory
import com.starmicronics.stario10.starxpandcommand.DocumentBuilder
import com.starmicronics.stario10.starxpandcommand.MagnificationParameter
import com.starmicronics.stario10.starxpandcommand.PrinterBuilder
import com.starmicronics.stario10.starxpandcommand.StarXpandCommandBuilder
import com.starmicronics.stario10.starxpandcommand.PageModeBuilder
import com.starmicronics.stario10.starxpandcommand.printer.Alignment
import com.starmicronics.stario10.starxpandcommand.printer.CutType
import com.starmicronics.stario10.starxpandcommand.printer.PageModeAreaParameter
import com.starmicronics.stario10.starxpandcommand.printer.PageModeImageParameter
import com.starmicronics.stario10.starxpandcommand.printer.TextAlignment
import com.starmicronics.stario10.starxpandcommand.printer.TextParameter
import com.starmicronics.stario10.starxpandcommand.printer.TextWidthParameter
import com.starmicronics.starxpandsdk.R

class LabelSample31_Sale50percentOff_Template {
    companion object {
        fun createLabelTemplate(context: Context): String {
            val backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.label_sample32_sale_50off_background)
            val builder = StarXpandCommandBuilder()
            builder.addDocument(
                DocumentBuilder()
                    .settingPrintableArea(72.0)
                    .addPrinter(
                        PrinterBuilder()
                            .styleAlignment(Alignment.Center)
                            .addPageMode(
                                PageModeAreaParameter(72.0, 68.0),
                                PageModeBuilder()
                                    .actionPrintImage(PageModeImageParameter(backgroundBitmap, 0.0, 0.0, 590))
                                    .styleHorizontalPositionTo(7.0)
                                    .styleMagnification(MagnificationParameter(4,4))
                                    .styleVerticalPositionTo(42.0)
                                    .actionPrintText(
                                        "\${note}\n",
                                        TextParameter()
                                            .setWidth(
                                                10,
                                                TextWidthParameter()
                                                    .setAlignment(TextAlignment.Center)
                                            )
                                    )
                            )
                            .actionCut(CutType.Partial)
                    )
            )
            return builder.getCommands()
        }
    }
}
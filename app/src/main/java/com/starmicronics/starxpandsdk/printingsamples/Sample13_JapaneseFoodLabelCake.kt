package com.starmicronics.starxpandsdk.printingsamples

import android.content.Context
import android.graphics.BitmapFactory
import com.starmicronics.stario10.starxpandcommand.DocumentBuilder
import com.starmicronics.stario10.starxpandcommand.PrinterBuilder
import com.starmicronics.stario10.starxpandcommand.PageModeBuilder
import com.starmicronics.stario10.starxpandcommand.StarXpandCommandBuilder
import com.starmicronics.stario10.starxpandcommand.printer.*
import com.starmicronics.starxpandsdk.R

class Sample13_JapaneseFoodLabelCake {
    companion object {
        fun createJapaneseFoodLabelCake(context: Context): String {
            val plasticBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sample13_japanese_food_label_cake_plastic)

            val builder = StarXpandCommandBuilder()
            builder.addDocument(
                DocumentBuilder()
                    .addPrinter(
                        PrinterBuilder()
                            // モデルにより対応する文字エンコーディング指定APIが異なります。
                            // 下記ページのSupported Modelを参照し、ご利用のモデルが対応するAPIを使用してください。
                            // https://star-m.jp/products/s_print/sdk/starxpand/manual/ja/android-kotlin-api-reference/stario10-star-xpand-command/printer-builder/style-cjk-character-priority.html
                            .styleCjkCharacterPriority(listOf(CjkCharacterType.Japanese))
                            // https://star-m.jp/products/s_print/sdk/starxpand/manual/ja/android-kotlin-api-reference/stario10-star-xpand-command/printer-builder/style-second-priority-character-encoding.html
                            //.styleSecondPriorityCharacterEncoding(CharacterEncodingType.Japanese)

                            .addPageMode(
                                PageModeAreaParameter(48.0, 56.0),
                                PageModeBuilder()
                                    .actionPrintRectangle(
                                        PageModeRectangleParameter(0.0, 3.5, 40.0, 52.0)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(0.0, 7.5, 40.0, 7.5)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(0.0, 23.5, 40.0, 23.5)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(0.0, 27.5, 40.0, 27.5)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(0.0, 31.5, 40.0, 31.5)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(0.0, 43.5, 40.0, 43.5)
                                    )
                                    .actionPrintRuledLine(
                                        PageModeRuledLineParameter(12.5, 3.5, 12.5, 55.5)
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .styleVerticalPositionTo(0.0)
                                    .actionPrintText(
                                        "品　名"
                                    )
                                    .styleHorizontalPositionTo(17.0)
                                    .actionPrintText(
                                        "ロールケーキ\n"
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .actionPrintText(
                                        "名　称"
                                    )
                                    .styleHorizontalPositionTo(22.0)
                                    .actionPrintText(
                                        "洋菓子\n"
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .actionPrintText(
                                        "原材料名"
                                    )
                                    .addPageMode(
                                        PageModeAreaParameter(26.0, 16.0)
                                            .setX(13.0)
                                            .setY(8.0),
                                        PageModeBuilder()
                                            .actionPrintText(
                                                "卵、砂糖、小麦粉、ショートニング、油脂、ベーキングパウダー\n"
                                            )
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .styleVerticalPositionTo(24.0)
                                    .actionPrintText(
                                        "内容量"
                                    )
                                    .styleHorizontalPositionTo(23.0)
                                    .actionPrintText(
                                        "1個\n"
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .actionPrintText(
                                        "消費期限"
                                    )
                                    .styleHorizontalPositionTo(13.0)
                                    .actionPrintText(
                                        "2010年01月31日\n"
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .actionPrintText(
                                        "保存方法"
                                    )
                                    .addPageMode(
                                        PageModeAreaParameter(26.0, 12.0)
                                            .setX(13.0)
                                            .setY(32.0),
                                        PageModeBuilder()
                                            .actionPrintText(
                                                "直射日光・高温多湿を避けて、保存してください。"
                                            )
                                    )
                                    .styleHorizontalPositionTo(0.5)
                                    .styleVerticalPositionTo(44.0)
                                    .actionPrintText(
                                        "製造者"
                                    )
                                    .addPageMode(
                                        PageModeAreaParameter(26.0, 12.0)
                                            .setX(13.0)
                                            .setY(44.0),
                                        PageModeBuilder()
                                            .actionPrintText(
                                                "スターショップ\n" +
                                                        "XXX県XXX市XXX町123-1\n" +
                                                        "TEL 1234-567-890"
                                            )
                                    )
                                    .actionPrintImage(
                                        PageModeImageParameter(plasticBitmap, 41.0, 0.0, 48)
                                    )
                                    .stylePrintDirection(PageModePrintDirection.BottomToTop)
                                    .styleHorizontalPositionTo(6.0)
                                    .styleVerticalPositionTo(42.0)
                                    .actionPrintBarcode(
                                        BarcodeParameter("0123456789012", BarcodeSymbology.Jan13)
                                            .setBarDots(3)
                                            .setHeight(3.0)
                                            .setPrintHri(true)
                                    )
                            )
                            .actionCut(CutType.Partial)
                    )
            )

            return builder.getCommands()
        }
    }
}
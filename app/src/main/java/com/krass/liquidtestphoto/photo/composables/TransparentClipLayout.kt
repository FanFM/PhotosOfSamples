package com.krass.liquidtestphoto.photo.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

@Composable
fun TransparentClipLayout(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    width: Dp,
    height: Dp,
    offsetY: Dp,
    crop: Boolean = false,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    val offsetInPx: Float
    val widthInPx: Float
    val heightInPx: Float

    with(LocalDensity.current) {
        offsetInPx = offsetY.toPx()
        widthInPx = width.toPx()
        heightInPx = height.toPx()
    }

    BoxWithConstraints(modifier) {

        val composableWidth = constraints.maxWidth
        val composableHeight = constraints.maxHeight


        val widthRatio = imageBitmap.width / composableWidth.toFloat()
        val heightRatio = imageBitmap.height / composableHeight.toFloat()

        val rectDraw = remember {
            Rect(
                offset = Offset(
                    x = (composableWidth - widthInPx) / 2f,
                    y = offsetInPx
                ),
                size = Size(widthInPx, heightInPx)
            )
        }

        val rectCrop by remember {
            mutableStateOf(
                IntRect(
                    offset = IntOffset(
                        (rectDraw.left * widthRatio).toInt(),
                        (rectDraw.top * heightRatio).toInt()
                    ),
                    size = IntSize(
                        (rectDraw.width * widthRatio).toInt(),
                        (rectDraw.height * heightRatio).toInt()
                    )
                )
            )
        }

        LaunchedEffect(crop) {
            if (crop) {
                delay(500)
                val croppedBitmap = Bitmap.createBitmap(
                    imageBitmap.asAndroidBitmap(),
                    rectCrop.left,
                    rectCrop.top,
                    rectCrop.width,
                    rectCrop.height
                ).asImageBitmap()

                onCropSuccess(croppedBitmap)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {

            if (!crop) {
                drawImage(
                    image = imageBitmap,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                if (!crop) {
                    // Destination
                    drawRect(Color(0x77000000))

                    // Source
                    drawRoundRect(
                        topLeft = rectDraw.topLeft,
                        size = rectDraw.size,
                        cornerRadius = CornerRadius(30f, 30f),
                        color = Color.Transparent,
                        blendMode = BlendMode.Clear
                    )

                } else {
                    // Destination
                    drawRoundRect(
                        topLeft = rectDraw.topLeft,
                        size = rectDraw.size,
                        cornerRadius = CornerRadius(30f, 30f),
                        color = Color.Red,
                    )

                    // Source
                    drawImage(
                        image = imageBitmap,
                        dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                        blendMode = BlendMode.SrcIn
                    )
                }

                restoreToCount(checkPoint)
            }
        }
    }
}
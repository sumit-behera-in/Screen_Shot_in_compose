package apps.sumit.myapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.DateFormat.getDateInstance
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import apps.sumit.myapplication.ui.theme.MyApplicationTheme
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme() {
                val screenshotState = rememberScreenshotState()

                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                )
                {
                    ScreenshotBox(screenshotState = screenshotState) {
                        Box(
                            Modifier
                                .fillMaxSize(0.5f)
                                .background(color = MaterialTheme.colorScheme.onBackground),contentAlignment = Alignment.Center){
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                                Image(painter = painterResource(id = R.drawable.x), contentDescription = "")
                                Text(text = "Hello",color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            screenshotState.capture()
                            screenshotState.bitmap?.let {bitmap ->
                                try {
                                    val filename =  getDateInstance().format(Date()) + ".png"
                                    var fos: FileOutputStream?
                                    val resolver = contentResolver
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        val contentValues = ContentValues().apply {
                                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                        }

                                        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                        fos = imageUri?.let { resolver.openOutputStream(it) } as FileOutputStream?
                                    } else {
                                        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        val image = File(imagesDir, filename)
                                        fos = FileOutputStream(image)
                                    }

                                    fos?.use {
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                    }

                                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + filename)
                                    val contentUri: Uri = Uri.fromFile(file)
                                    mediaScanIntent.data = contentUri
                                    sendBroadcast(mediaScanIntent)
                                    Toast.makeText(this@MainActivity,"Screenshot saved",Toast.LENGTH_SHORT).show()
                                }catch (_:IOException){
                                    Toast.makeText(this@MainActivity,"Screenshot failed",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text(text = "Take Screenshot")
                    }

                }
            }
        }
    }
}

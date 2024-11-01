package com.krass.liquidtestphoto.main

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.krass.liquidtestphoto.BaseActivity
import com.krass.liquidtestphoto.SamplesNames
import com.krass.liquidtestphoto.main.composables.CameraPreviewScreen
import com.krass.liquidtestphoto.main.composables.App
import com.krass.liquidtestphoto.main.composables.MachinesList
import com.krass.liquidtestphoto.models.Images
import com.krass.liquidtestphoto.ui.theme.MainTheme


class MainActivity : BaseActivity() {

//    private val images = mutableStateListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        Images.getStoredImages(this)
    }

    @Composable
    fun MainPreview(){
        val machines = remember { SamplesNames.getInstance().getMachines(this) }
        App(onClick = { button ->
            when(button){
                "camera" -> {
                    if (handleCameraPermission()) {
                        setCameraPreview()
                    }
                }
                "newFolder" -> {
                    addNewFolder()
                    Images.getStoredImages(this)
                }
                "machines" -> {
                    setContent{
                        MainTheme {
                            MachinesList(machines, LocalLifecycleOwner.current, onStart = {
                            }, onStop = {
                                setContent {
                                    SamplesNames.storeMachines(this, machines)
                                    MainPreview()
                                }
                            })
                        }
                    }
                }
            }
        }, Images.getInstance().getImages())
    }

    private fun addNewFolder(){
        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var folder = sharedPref.getInt("folder", 0)
        with (sharedPref.edit()) {
            putInt("folder", ++folder)
            apply()
        }
        Toast.makeText(this, "Folder Sample $folder created", Toast.LENGTH_SHORT).show()
    }

    private fun setCameraPreview() {
        setContent {
            MainTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraPreviewScreen(LocalLifecycleOwner.current, onStart = {
//                        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()
                    }, onStop = {
//                        Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
                        setContent {
                            MainPreview()
                        }
                    })
                }
            }
        }
    }
}
package com.krass.photosofsamples.main.composables

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.krass.photosofsamples.R
import com.krass.photosofsamples.SamplesNames
import com.krass.photosofsamples.models.Images
import com.krass.photosofsamples.photo.PhotoActivity
import com.krass.photosofsamples.ui.theme.MainTheme

lateinit var images: MutableList<Uri>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onClick: (String) -> Unit, _images: MutableList<Uri>) {
    images = _images
    val navController = rememberNavController()
    val cameraIcon = ImageVector.vectorResource(R.drawable.photo_camera)
    val newFolderIcon = ImageVector.vectorResource(R.drawable.create_new_folder)
    val machinesListIcon = ImageVector.vectorResource(R.drawable.view_list)
    val cameraDesc = stringResource(R.string.camera)
    val newFolderDesc = stringResource(R.string.add_new_folder)
    val machinesListDesc = stringResource(R.string.machines)

    val context = LocalContext.current

    val buttons = remember {
        listOf(
            Triple(newFolderDesc, false, newFolderIcon),
            Triple(cameraDesc, true, cameraIcon),
            Triple(machinesListDesc, false, machinesListIcon)
        )
    }

    val defaultScreen = remember { buttons.first() }

    MainTheme {
        Scaffold(
            topBar = {
                var showMenu by remember { mutableStateOf(false) }
                CenterAlignedTopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
//                    modifier = Modifier.height(48.dp),
                    title =
                    {
                        Text(stringResource(R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = {
                            showMenu = !showMenu
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
//                        DropdownMenu(
//                            expanded = showMenu,
//                            onDismissRequest = { showMenu = false }
//                        ) {
//                            DropdownMenuItem(
//                                onClick = {
//                                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
//                                },
//                                text = { Text("Settings") }
//                            )
//
//                            DropdownMenuItem(
//                                onClick = {
//                                    Toast.makeText(context, "Contact Us", Toast.LENGTH_SHORT).show()
//                                },
//                                text = { Text("Contact Us") }
//                            )
//                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    navController = navController,
                    buttons = buttons,
                    defaultScreen = defaultScreen,
                    onClick
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = defaultScreen.first,
                route = "Route"
            ) {
                for (button in buttons) {
                    composable(button.first) {
//                        when (button.first) {
//                            cameraDesc -> MainScreenContent(padding)
//                            machinesListDesc -> MachinesList(padding)
//                        }
                        MainScreenContent(padding)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    buttons: List<Triple<String, Boolean, ImageVector>>,
    defaultScreen: Triple<String, Boolean, ImageVector>,
    onClick: (String) -> Unit
) {
    val cameraDesc = stringResource(R.string.camera)
    val newFolderDesc = stringResource(R.string.add_new_folder)
    val machinesDesc = stringResource(R.string.machines)
    var selected by rememberSaveable {
        mutableStateOf(defaultScreen.first)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigate: (route: String) -> Unit = { route ->
        selected = route

        if (currentDestination == null || currentDestination.route != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true

                restoreState = true
            }
        }
    }

    val background = MaterialTheme.colorScheme.surfaceContainer

    val navigationBarHeight = 140.dp
    val density = LocalDensity.current
    val bottomBarHeightPx = WindowInsets.navigationBars.getBottom(density)

    val bottomBarHeight = with(density) { bottomBarHeightPx.toDp() }

    val arenaButtonSize = 96.dp
    val arenaButtonRadius = arenaButtonSize / 2
    val arenaButtonBorderSize = 8.dp

    val canvasHeight = navigationBarHeight - arenaButtonRadius + arenaButtonBorderSize

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(navigationBarHeight)
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .align(Alignment.BottomCenter)
            .clickable(enabled = false, onClick = {
                Log.d("Canvas", "Canvas clicked")
            })
        ) {
            val rect = Rect(center = Offset(x = size.width / 2, y = 0f), arenaButtonRadius.toPx())
            val path = Path()
            path.addOval(rect)

            clipPath(path = path, clipOp = ClipOp.Difference) {
                drawRect(color = background)
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .padding(bottom = bottomBarHeight)
            .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                for (button in buttons) {
                    if (button.second) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(26.dp))
//                            Text( text = button.first)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {

                                    when (button.first) {
                                        newFolderDesc -> onClick("newFolder")
                                        cameraDesc -> onClick("camera")
                                        machinesDesc -> onClick("machines")
                                    }


//                                    navigate(button.first)

                                    Log.d("Canvas", "Item ${button.first} clicked")
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                imageVector = button.third,
                                contentDescription = button.first
                            )

                            Text(text = button.first)
                        }
                    }
                }
            }
        }

        val padding = (canvasHeight - arenaButtonRadius) + arenaButtonBorderSize
        val circleSize = arenaButtonSize - arenaButtonBorderSize * 2

        val centerButton = remember {
            buttons.first { it.second }
        }

        Box(modifier = Modifier
            .padding(bottom = padding)
            .size(circleSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .align(Alignment.BottomCenter)
            .clickable {
//                navigate(centerButton.first)
                Log.d("Canvas", "Circle clicked")
                onClick("camera")
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(arenaButtonRadius),
                imageVector = centerButton.third,
                contentDescription = centerButton.first
            )
        }
    }
}

@Composable
private fun MainScreenContent(padding: PaddingValues) {
    val context = LocalContext.current
    var imageForDelete = remember { mutableStateOf(Uri.EMPTY) }
    val dialogMessage = remember { mutableStateOf("") }
    if(dialogMessage.value.isNotEmpty()){
        TwoButtonsAlertDialog(dialogMessage = dialogMessage, onConfirm = {
            SamplesNames.deleteFile(imageForDelete.value, context)
            Images.getStoredImages(context)
        })
    }
    LazyColumn(contentPadding = padding) {
        images.forEachIndexed { index, uri ->
            if (index % 2 == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .border(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Box() {
                                AsyncImage(
                                    model = images[index],
                                    contentDescription = images[index].path,
                                    Modifier.clickable(onClick = {
                                        val iUri = images[index]
                                        val intent = Intent(context, PhotoActivity::class.java)
                                        intent.putExtra("uri", iUri.toString())
                                        intent.putExtra("mode_edit", true)
                                        startActivity(context, intent, null)
                                    })
                                )
                                IconButton(
                                    onClick = {
                                        imageForDelete.value = images[index]
                                        dialogMessage.value =
                                            "Do you want delete sample " + images[index].path.toString().split("/").last() + "?"
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .width(40.dp)
                                        .height(40.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                    )
                                }
                            }
                            images[index].path?.let {
                                it.split("/").last().let { it1 ->
                                    Text(
                                        text = it1,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(2.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        if (index + 1 < images.size) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Box() {
                                    AsyncImage(
                                        model = images[index + 1],
                                        contentDescription = images[index + 1].path,
                                        Modifier.clickable(onClick = {
                                            val iUri = images[index + 1]
                                            val intent = Intent(context, PhotoActivity::class.java)
                                            intent.putExtra("uri", iUri.toString())
                                            intent.putExtra("mode_edit", true)
                                            startActivity(context, intent, null)
                                        })
                                    )
                                    IconButton(
                                        onClick = {
                                            imageForDelete.value = images[index + 1]
                                            dialogMessage.value =
                                                "Do you want delete sample " + images[index + 1].path.toString().split("/").last() + "?"
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .width(40.dp)
                                            .height(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.delete),
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                        )
                                    }
                                }
                                images[index + 1].path?.let {
                                    it.split("/").last().let { it1 ->
                                        Text(
                                            text = it1,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
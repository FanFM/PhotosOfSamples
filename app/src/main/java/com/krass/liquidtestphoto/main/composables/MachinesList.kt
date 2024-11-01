package com.krass.liquidtestphoto.main.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.krass.liquidtestphoto.R
import com.krass.liquidtestphoto.SamplesNames


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinesList(
    machines: SnapshotStateList<Pair<String, String>>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current, // Send the 'started' analytics event
    onStart: () -> Unit,
    onStop: () -> Unit
){

    val context = LocalContext.current
    val currentOnStart by rememberUpdatedState(onStart)
    val currentOnStop by rememberUpdatedState(onStop)
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                currentOnStop()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

//    val tmp = machines.sortedBy { (key, value) -> key }.toMutableStateList()
//    machines.clear()
//    machines.addAll(machines.sortedBy { (key, value) -> key }.toMutableStateList())

//    val data = remember { mutableStateListOf<Pair<String, String>>() }



    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(stringResource(R.string.machines_and_hosts))
                },
                navigationIcon = {
                    IconButton(onClick = { currentOnStop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        machines.add(0, Pair("", ""))
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        SamplesNames.storeMachines(context, machines)
                        currentOnStop()
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.save),
                            contentDescription = "Save",
                            tint = Color.White
                        )
                    }
                }
            )
        },

    ) { innerPadding ->
        val scrollState = rememberLazyListState()
        LaunchedEffect(machines.size) {
            if (machines.isNotEmpty()) scrollState.animateScrollToItem(0)
        }
        LazyColumn(
            modifier = Modifier.background(Color.White).padding(innerPadding).fillMaxWidth(),
            state = scrollState
            ) {
            itemsIndexed(machines) { index, item ->
                val datum = machines[index]
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        value = datum.first,
                        onValueChange = { machines[index] = machines[index].copy(first = it) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).padding(2.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                    Box(modifier = Modifier.weight(1f)
                        .padding(2.dp),) {
                        TextField(
                            value = datum.second,
                            onValueChange = { machines[index] = machines[index].copy(second = it) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )
                        IconButton(
                            onClick = {
                                machines.removeAt(index)
                            },
                            modifier = Modifier.align(Alignment.CenterEnd).width(40.dp).height(40.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = "Delete",
                                tint = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
    BackHandler {
        currentOnStop()
    }
}


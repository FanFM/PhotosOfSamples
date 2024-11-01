package com.krass.liquidtestphoto.photo.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krass.liquidtestphoto.R
import com.krass.liquidtestphoto.SamplesNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(initialState: Boolean, onItemClick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(initialState) }
    val machines = SamplesNames.getInstance().getMachines(LocalContext.current)
    var selectedOption by remember { mutableIntStateOf(0) }

    ExposedDropdownMenuBox(
//        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = SamplesNames.toShortMachineName(machines[selectedOption].first),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.choose_option)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.padding(16.dp).menuAnchor(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            machines.forEachIndexed {index, selectionOption ->
                DropdownMenuItem(
                    text = { Text(SamplesNames.toShortMachineName(selectionOption.first)) },
                    onClick = {
                        onItemClick(index)
                        selectedOption = index
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun DropdownPreview(){
    Dropdown(initialState = true, onItemClick = {})
}
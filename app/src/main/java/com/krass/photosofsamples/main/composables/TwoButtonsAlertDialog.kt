package com.krass.photosofsamples.main.composables


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoButtonsAlertDialog(dialogMessage: MutableState<String>, onConfirm: () -> Unit) {
    if (dialogMessage.value.isNotEmpty()) {
        BasicAlertDialog(
            onDismissRequest = {
                dialogMessage.value = ""
            }) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = dialogMessage.value
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row{
                        TextButton(
                            onClick = { dialogMessage.value = "" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                dialogMessage.value = ""
                                onConfirm()
                                      },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}
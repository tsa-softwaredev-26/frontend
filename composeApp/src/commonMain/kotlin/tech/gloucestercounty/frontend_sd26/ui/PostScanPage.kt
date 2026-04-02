package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import tech.gloucestercounty.frontend_sd26.AudioRecorder
import tech.gloucestercounty.frontend_sd26.api.BaseAPI


@Composable
fun PostScanPage(path: String) {
    // the post scan page shows after a scan is completed and the server responds to it
    var items by remember { mutableStateOf<List<JsonObject>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // helper that re-fetches the item list from the server and updates state
    fun refreshItems() {
        scope.launch {
            val response = BaseAPI.getItems()
            items = response["items"]?.jsonArray?.map { it.jsonObject } ?: emptyList()
            loading = false
        }
    }

    // get the items and put them in the items variable
    LaunchedEffect(Unit) { refreshItems() }

    Scaffold(
        floatingActionButton = { AudioRecorder.FAB() } // audio controller fab
    ) { innerPaddings ->
        // lazy column only renders visible items
        LazyColumn(
            modifier = Modifier.padding(innerPaddings).padding(horizontal = 8.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // show taken photo at the top
            item {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    "file://$path",
                    "Preview of taken image",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // wait until loading from api is done
            if (loading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // render a card for each item, passing callbacks to handle delete/rename
                items(items) { item ->
                    ItemCard(
                        item = item,
                        onDelete = { label ->
                            scope.launch {
                                BaseAPI.delItems(label)
                                refreshItems() // refresh list after deletion
                            }
                        },
                        onRename = { old, new ->
                            scope.launch {
                                BaseAPI.renItems(old, new)
                                refreshItems() // refresh list after rename
                            }
                        }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: JsonObject,
    onDelete: (label: String) -> Unit,
    onRename: (old: String, new: String) -> Unit
) {
    // creates a card that shows the info on a single item

    // grab info about the item
    val label = item["label"]?.jsonPrimitive?.content ?: "Unknown"
    val confidence = item["confidence"]?.jsonPrimitive?.content?.toDoubleOrNull()
    val ocrText = item["ocr_text"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
    val attrs = item["visual_attributes"]?.jsonObject

    // track whether the rename or delete dialogs are open
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // create card ui
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            // top row: label, confidence, and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (confidence != null) {
                        Text(
                            "${(confidence * 100).toInt()}% confidence",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    // rename button
                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(Icons.Rounded.Edit, "Rename item")
                    }
                    // delete button
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.Delete, "Delete item", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // display image attributes, if applicable
            if (attrs != null) {
                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    attrs["dominant_color"]?.jsonPrimitive?.content?.let { LabeledValue("Color", it) }
                    attrs["shape"]?.jsonPrimitive?.content?.let { LabeledValue("Shape", it) }
                    attrs["texture"]?.jsonPrimitive?.content?.let { LabeledValue("Texture", it) }
                }
            }

            // show ocr text, if applicable
            if (ocrText != null) {
                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                Text("OCR: $ocrText", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // rename dialog — shown when the edit button is pressed
    if (showRenameDialog) {
        var newName by remember { mutableStateOf(label) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Item") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New label") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    onRename(label, newName) // call rename with old and new labels
                }) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    // delete confirmation dialog — shown when the delete button is pressed
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete \"$label\"?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(label) // call delete with the item's label
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// labels for the picture
@Composable
private fun LabeledValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

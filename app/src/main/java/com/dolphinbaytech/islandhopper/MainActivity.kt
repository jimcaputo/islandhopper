package com.dolphinbaytech.islandhopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter

import com.dolphinbaytech.islandhopper.ui.theme.IslandHopperTheme


class MainActivity : ComponentActivity() {

    val mvm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FerryAPI.create(applicationContext)
        IslandHopper.create(mvm)
        IslandHopper.updateSchedules()

        setContent {
            IslandHopperTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Island Hopper")
                }
            )
        }
    ) { innerPadding ->
        IslandHopper(innerPadding)
    }
}

@Composable
fun IslandHopper(innerPadding: PaddingValues) {
    val mvm = IslandHopper.mvm

    Column(
        modifier = Modifier.padding(innerPadding)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TerminalControls()
        Spacer(modifier = Modifier.height(12.dp))
        DateControls()
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "Depart")
            Text(modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "Arrive")
            Text(modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "Duration")
            Text(modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "Ferry")
        }

        for (schedule in mvm.scheduleList) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                var departTime = schedule.departTime.format(DateTimeFormatter.ofPattern("h:mm"))
                departTime += if (schedule.departTime.hour < 12) "am" else "pm"
                var arriveTime = schedule.arriveTime.format(DateTimeFormatter.ofPattern("h:mm"))
                arriveTime += if (schedule.arriveTime.hour < 12) "am" else "pm"
                val duration = schedule.duration.toString() + "min"

                Text(modifier = Modifier.weight(1f), fontSize = 18.sp, text = departTime)
                Text(modifier = Modifier.weight(1f), fontSize = 18.sp, text = arriveTime)
                Text(modifier = Modifier.weight(1f), fontSize = 18.sp, text = duration)
                Text(modifier = Modifier.weight(1f), fontSize = 18.sp, text = schedule.vesselName)
            }
        }
    }
}

@Composable
fun TerminalControls() {
    val mvm = IslandHopper.mvm

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 6.dp)
        ) {
            DepartPicker()
        }
        IconButton(
            onClick = {
                mvm.setTerminals(depart = mvm.arrive, arrive = mvm.depart)
                IslandHopper.updateSchedules()
            }
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Back"
            )
        }
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 6.dp)
        ) {
            ArrivePicker()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartPicker() {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = IslandHopper.mvm.depart.name,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Depart") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Terminals.forEach { terminal ->
                DropdownMenuItem(
                    text = { Text(text = terminal.name, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        IslandHopper.mvm.depart = terminal
                        expanded = false
                        IslandHopper.updateSchedules()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrivePicker() {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = IslandHopper.mvm.arrive.name,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Arrive") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Terminals.forEach { terminal ->
                DropdownMenuItem(
                    text = { Text(text = terminal.name, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        IslandHopper.mvm.arrive = terminal
                        expanded = false
                        IslandHopper.updateSchedules()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun DateControls() {
    val mvm = IslandHopper.mvm
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(onDismiss = { showDatePicker = false })
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            enabled = (mvm.dateMillis > mvm.todayMillis),
            onClick = {
                mvm.prevDay()
                IslandHopper.updateSchedules()
            }
        ) {
            Text(text = "Prev")
        }
        TextButton(onClick = { showDatePicker = true }) {
            Text(
                fontSize = 18.sp,
                color = Color.Blue,
                text = mvm.getFormattedDate())
        }
        Button(
            onClick = {
                mvm.nextDay()
                IslandHopper.updateSchedules()
            }
        ) {
            Text(text = "Next")
        }
    }

}

@Composable
fun DatePickerModal(onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return IslandHopper.mvm.showDay(utcMilli = utcTimeMillis)
            }
        }
    )

    datePickerState.selectedDateMillis = IslandHopper.mvm.dateMillis

    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                IslandHopper.mvm.dateMillis = datePickerState.selectedDateMillis!!
                IslandHopper.updateSchedules()
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

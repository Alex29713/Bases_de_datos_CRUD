package com.example.basesdedatos.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.basesdedatos.DAO.UserDao
import com.example.basesdedatos.Model.User
import com.example.basesdedatos.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf(listOf<User>()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showListDialog by remember { mutableStateOf(false) } // Controla el diálogo de lista

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campos de texto redondeados
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para registrar usuario
        Button(onClick = {
            val user = User(nombre = nombre, apellido = apellido, edad = edad.toIntOrNull() ?: 0)
            scope.launch {
                withContext(Dispatchers.IO) {
                    userRepository.insert(user)
                }
            }
            nombre = ""
            apellido = ""
            edad = ""
            Toast.makeText(context, "Usuario Registrado", Toast.LENGTH_SHORT).show()
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE86200EE), // Fondo morado oscuro
                contentColor = Color.White          // Texto en blanco
            )
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para mostrar ventana emergente con lista de usuarios
        Button(onClick = {
            scope.launch {
                users = withContext(Dispatchers.IO) {
                    userRepository.getAllUsers()
                }
                showListDialog = true // Mostrar la ventana emergente
            }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50), // Fondo rojo
                contentColor = Color.White          // Texto en blanco
            )
        ){
            Text(text = "Usuarios")
        }

        // Ventana emergente con lista de usuarios, editar y eliminar
        if (showListDialog) {
            AlertDialog(
                onDismissRequest = { showListDialog = false },
                title = { Text(text = "Usuarios Registrados") },
                text = {
                    Column {
                        // Dropdown para seleccionar usuario
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { expanded = !expanded },
                            colors = CardDefaults.cardColors(),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
                                Text(text = if (selectedUserId != null) {
                                    "Usuario ID: ${selectedUserId} seleccionado"
                                } else {
                                    "Seleccionar Usuario"
                                })
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                users.forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text("${user.nombre} ${user.apellido}, Edad: ${user.edad}") },
                                        onClick = {
                                            selectedUserId = user.id
                                            nombre = user.nombre
                                            apellido = user.apellido
                                            edad = user.edad.toString()
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFEEEEFF),  // Fondo más claro para resaltar
                                focusedIndicatorColor = Color(0xFF0000FF),  // Indicador enfocado en azul
                                unfocusedIndicatorColor = Color(0xFFCCCCFF) // Indicador no enfocado más suave
                            )
                        )

                        TextField(
                            value = apellido,
                            onValueChange = { apellido = it },
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFEEEEFF),  // Fondo más claro para resaltar
                                focusedIndicatorColor = Color(0xFF0000FF),  // Indicador enfocado en azul
                                unfocusedIndicatorColor = Color(0xFFCCCCFF) // Indicador no enfocado más suave
                            )
                        )

                        TextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFEEEEFF),  // Fondo más claro para resaltar
                                focusedIndicatorColor = Color(0xFF0000FF),  // Indicador enfocado en azul
                                unfocusedIndicatorColor = Color(0xFFCCCCFF) // Indicador no enfocado más suave
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))


                        // Botones de Editar y Eliminar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TextButton(onClick = {
                                if (selectedUserId == null) {
                                    // Mostrar mensaje de error si no se ha seleccionado un usuario
                                    Toast.makeText(context, "Por favor, seleccione un usuario primero", Toast.LENGTH_SHORT).show()
                                } else {
                                    selectedUserId?.let { userId ->
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                userRepository.updateById(
                                                    userId,
                                                    nombre,
                                                    apellido,
                                                    edad.toIntOrNull() ?: 0
                                                )
                                            }
                                            users = withContext(Dispatchers.IO) {
                                                userRepository.getAllUsers() // Refrescar la lista
                                            }
                                        }
                                        Toast.makeText(
                                            context,
                                            "Usuario Actualizado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showEditDialog = false
                                    }
                                }
                            }) {
                                Text("Actualizar")
                            }

                            TextButton(onClick = {
                                if (selectedUserId == null) {
                                    // Mostrar mensaje de error si no se ha seleccionado un usuario
                                    Toast.makeText(context, "Por favor, seleccione un usuario primero", Toast.LENGTH_SHORT).show()
                                } else {
                                    selectedUserId?.let { userId ->
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                userRepository.deleteById(userId)
                                            }
                                            users = withContext(Dispatchers.IO) {
                                                userRepository.getAllUsers() // Refrescar la lista
                                            }
                                        }
                                        Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                        // Limpiar campos y cerrar diálogos
                                        nombre = ""
                                        apellido = ""
                                        edad = ""
                                        showDeleteDialog = false
                                        showListDialog = false
                                    }
                                }
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Limpiar campos cuando se confirme el cierre del diálogo
                        nombre = ""
                        apellido = ""
                        edad = ""
                        showListDialog = false
                    }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // Diálogos de confirmación para eliminar y editar (igual que antes)
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text(text = "Confirmar Edición") },
                text = { Text(text = "¿Está seguro de que desea actualizar este usuario?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedUserId?.let { userId ->
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    userRepository.updateById(userId, nombre, apellido, edad.toIntOrNull() ?: 0)
                                }
                            }
                            // Mostrar mensaje de éxito y limpiar los campos
                            Toast.makeText(context, "Usuario Actualizado", Toast.LENGTH_SHORT).show()
                            nombre = ""
                            apellido = ""
                            edad = ""
                            showEditDialog = false
                            showListDialog = false // Cerrar también el menú de lista
                        }
                    }) {
                        Text("Actualizar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Confirmar Eliminación") },
                text = { Text(text = "¿Está seguro de que desea eliminar este usuario?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedUserId?.let { userId ->
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    userRepository.deleteById(userId)
                                }
                            }
                            // Mostrar mensaje de éxito y limpiar los campos
                            Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                            nombre = ""
                            apellido = ""
                            edad = ""
                            showDeleteDialog = false
                            showListDialog = false // Cerrar también el menú de lista
                        }
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

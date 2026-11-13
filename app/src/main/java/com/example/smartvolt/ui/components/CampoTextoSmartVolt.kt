package com.example.smartvolt.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors

/** Campo de formulario con el mismo estilo utilizado en gestión de faenas y usuarios. */
@Composable
fun CampoTextoSmartVolt(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    ejemplo: String,
    habilitado: Boolean = true,
    unaLinea: Boolean = true,
    lineasMinimas: Int = 1,
    lineasMaximas: Int = 3,
    transformacionVisual: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        placeholder = { Text(ejemplo) },
        enabled = habilitado,
        singleLine = unaLinea,
        minLines = lineasMinimas,
        maxLines = if (unaLinea) 1 else lineasMaximas,
        visualTransformation = transformacionVisual,
        shape = RoundedCornerShape(14.dp),
        colors = smartVoltTextFieldColors()
    )
}

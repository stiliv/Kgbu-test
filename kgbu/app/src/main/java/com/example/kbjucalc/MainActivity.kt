package com.example.kbjucalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kbjucalc.ui.theme.Theme_KbjuCalc

data class NutritionData(
    val calories: String = "",
    val protein: String = "",
    val fat: String = "",
    val carbs: String = ""
)

data class CalculationResult(
    val totalCalories: Double = 0.0,
    val proteinPercentage: Double = 0.0,
    val fatPercentage: Double = 0.0,
    val carbsPercentage: Double = 0.0
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme_KbjuCalc {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KbjuApp()
                }
            }
        }
    }
}

@Composable
fun KbjuApp() {
    val navController = rememberNavController()
    val nutritionData = remember { mutableStateOf(NutritionData()) }
    val calculationResult = remember { mutableStateOf(CalculationResult()) }

    NavHost(
        navController = navController,
        startDestination = "input"
    ) {
        composable("input") {
            InputScreen(
                nutritionData = nutritionData.value,
                onNutritionDataChange = { nutritionData.value = it },
                onNext = { navController.navigate("calculate") }
            )
        }
        composable("calculate") {
            CalculateScreen(
                nutritionData = nutritionData.value,
                onCalculate = { result ->
                    calculationResult.value = result
                    navController.navigate("result")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("result") {
            ResultScreen(
                result = calculationResult.value,
                onBack = { navController.popBackStack() },
                onReset = {
                    nutritionData.value = NutritionData()
                    calculationResult.value = CalculationResult()
                    navController.popBackStack("input", inclusive = false)
                }
            )
        }
    }
}

@Composable
fun InputScreen(
    nutritionData: NutritionData,
    onNutritionDataChange: (NutritionData) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.input_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nutritionData.calories,
            onValueChange = { onNutritionDataChange(nutritionData.copy(calories = it)) },
            label = { Text(stringResource(R.string.calories_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nutritionData.protein,
            onValueChange = { onNutritionDataChange(nutritionData.copy(protein = it)) },
            label = { Text(stringResource(R.string.protein_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nutritionData.fat,
            onValueChange = { onNutritionDataChange(nutritionData.copy(fat = it)) },
            label = { Text(stringResource(R.string.fat_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nutritionData.carbs,
            onValueChange = { onNutritionDataChange(nutritionData.copy(carbs = it)) },
            label = { Text(stringResource(R.string.carbs_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Button(
            onClick = onNext,
            enabled = nutritionData.calories.isNotBlank() &&
                    nutritionData.protein.isNotBlank() &&
                    nutritionData.fat.isNotBlank() &&
                    nutritionData.carbs.isNotBlank()
        ) {
            Text(stringResource(R.string.next_button))
        }
    }
}

@Composable
fun CalculateScreen(
    nutritionData: NutritionData,
    onCalculate: (CalculationResult) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Введённые данные:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("Калории: ${nutritionData.calories} ккал")
        Text("Белки: ${nutritionData.protein} г")
        Text("Жиры: ${nutritionData.fat} г")
        Text("Углеводы: ${nutritionData.carbs} г")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val calories = nutritionData.calories.toDoubleOrNull() ?: 0.0
                val protein = nutritionData.protein.toDoubleOrNull() ?: 0.0
                val fat = nutritionData.fat.toDoubleOrNull() ?: 0.0
                val carbs = nutritionData.carbs.toDoubleOrNull() ?: 0.0

                // Расчет процентов от общей калорийности (1 г белка/углеводов = 4 ккал, 1 г жиров = 9 ккал)
                val proteinCal = protein * 4.0
                val fatCal = fat * 9.0
                val carbsCal = carbs * 4.0
                val totalCal = proteinCal + fatCal + carbsCal

                val proteinPct = if (totalCal > 0) (proteinCal / totalCal * 100) else 0.0
                val fatPct = if (totalCal > 0) (fatCal / totalCal * 100) else 0.0
                val carbsPct = if (totalCal > 0) (carbsCal / totalCal * 100) else 0.0

                onCalculate(
                    CalculationResult(
                        totalCalories = totalCal,
                        proteinPercentage = proteinPct,
                        fatPercentage = fatPct,
                        carbsPercentage = carbsPct
                    )
                )
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(stringResource(R.string.calculate_button))
        }

        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.back_button))
        }
    }
}

@Composable
fun ResultScreen(
    result: CalculationResult,
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.result_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.total_calories, "%.1f".format(result.totalCalories)),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = stringResource(R.string.protein_percentage, "%.1f".format(result.proteinPercentage)),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.fat_percentage, "%.1f".format(result.fatPercentage)),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.carbs_percentage, "%.1f".format(result.carbsPercentage)),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back_button))
            }
            Button(onClick = onReset) {
                Text(stringResource(R.string.reset_button))
            }
        }
    }
}
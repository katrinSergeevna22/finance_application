package com.example.myfinanceapplication.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityStatisticsBinding
import com.example.myfinanceapplication.utils.navigationForNavigationView
import com.example.myfinanceapplication.viewModel.StatisticsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {
    lateinit var binding: ActivityStatisticsBinding
    lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]

        lifecycle.coroutineScope.launch {
            setupUI()
        }
    }

    private fun setupUI() {
        binding.apply {
            initIncomeStatistics()
            initExpenseStatistics()
            initGoalsStatistics()

            initEditOfIncome()
            initEditOfExpenses()
            initEditOfProgressOfGoal()

            toolbar.setNavigationOnClickListener {
                drawerGoal.openDrawer(GravityCompat.START)
            }

            navigationView.setNavigationItemSelectedListener {
                startActivity(
                    navigationForNavigationView(
                        context = this@StatisticsActivity,
                        itemId = it.itemId
                    )
                )
                true
            }
        }
    }

    private fun initIncomeStatistics() {
        setupPieChart(binding.pieChartIncome)

        lifecycle.coroutineScope.launch {
            viewModel.incomesLiveData.observeForever { incomesList ->
                Log.d("katrin_stat", incomesList.toString())
                if (incomesList.isNotEmpty()) {
                    val incomesListOfPair =
                        incomesList?.filter { it.category != null }
                            ?.associateBy({ it.category ?: "" }, { it.moneyCost }) ?: mapOf()
                    Log.d("katrin_stat_map", incomesListOfPair.toString())
                    updateChart(incomesListOfPair, pieChart = binding.pieChartIncome)
                }
            }
        }
    }

    private fun initExpenseStatistics() {
        setupPieChart(binding.pieChartExpense)

        lifecycle.coroutineScope.launch {
            viewModel.expensesLiveData.observeForever { expensesList ->
                Log.d("katrin_stat", expensesList.toString())
                if (expensesList.isNotEmpty()) {
                    val expensesListOfPair =
                        expensesList?.filter { it.category != null }
                            ?.associateBy({ it.category ?: "" }, { it.moneyCost }) ?: mapOf()
                    Log.d("katrin_stat_map", expensesListOfPair.toString())
                    updateChart(expensesListOfPair, pieChart = binding.pieChartExpense)
                }
            }
        }
    }

    private fun initGoalsStatistics() {
        setupPieChart(binding.pieChartGoals)

        lifecycle.coroutineScope.launch {
            viewModel.goalsLiveData.observeForever { goalsList ->
                Log.d("katrin_stat", goalsList.toString())
                if (goalsList.isNotEmpty()) {
                    val goalsListOfPair =
                        goalsList?.filter { it.category != null }
                            ?.associateBy({ it.category ?: "" }, { it.moneyGoal }) ?: mapOf()
                    Log.d("katrin_stat_map", goalsListOfPair.toString())
                    updateChart(goalsListOfPair, pieChart = binding.pieChartGoals)

                    initGoalsProgressStatistics()
                }
            }
        }
    }

    private fun initGoalsProgressStatistics() {
        setupPieChart(binding.pieChartProgressOfGoals)
        updateChart(viewModel.getProgressOfGoals(), pieChart = binding.pieChartProgressOfGoals)
    }

    private fun initEditOfIncome() {
        viewModel.incomesLiveData.observeForever { incomes ->
            val incomesMap = incomes.associateBy({ it.date }, { it.moneyCost.toFloat() })
            setupLineChart(
                dataMap = incomesMap,
                lineChart = binding.lineChartIncome,
                label = "Доходы"
            )
        }
    }

    private fun initEditOfExpenses() {
        viewModel.expensesLiveData.observeForever { expenses ->
            val expensesMap = expenses.associateBy({ it.date }, { it.moneyCost.toFloat() })
            setupLineChart(
                dataMap = expensesMap,
                lineChart = binding.lineChartExpense,
                label = "Расходы"
            )
        }
    }

    private fun initEditOfProgressOfGoal() {
        viewModel.expensesLiveData.observeForever { expenses ->
            val expensesMap = expenses.filter { it.goal != "" }
                .associateBy({ it.date }, { it.moneyCost.toFloat() })
            setupLineChart(
                dataMap = expensesMap,
                lineChart = binding.lineChartProgressOfGoals,
                label = "Отложено на достижение целей",
            )
        }
    }

    private fun setupPieChart(pieChart: PieChart) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // Установка радиуса дыры в центре
        pieChart.holeRadius = 2f // Уменьшенный радиус дыры
        pieChart.transparentCircleRadius = 2f // Уменьшенный радиус прозрачного круга

        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400)

        // Настройка легенды
        pieChart.legend.isEnabled = true
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL // Вертикальная ориентация
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // Внизу
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // По центру
        pieChart.legend.setDrawInside(false) // Не рисовать внутри графика

        // Увеличение шрифта легенды
        pieChart.legend.textSize = 14f // Увеличение шрифта до 14sp
    }

    private fun setupLineChart(
        dataMap: Map<String, Float>,
        lineChart: LineChart,
        label: String,
    ) {
        val entries = mutableListOf<Entry>()

        var index = 0
        for ((date, amount) in dataMap.entries.sortedBy { it.key }) {
            entries.add(Entry(index.toFloat(), amount))
            index++
        }

        val dataSet = LineDataSet(entries, label)
        dataSet.color = resources.getColor(R.color.dark_violet) // Задайте цвет линии
        dataSet.valueTextColor = resources.getColor(R.color.color_of_text) // Цвет текста значений
        dataSet.valueTextSize = 14f

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val date = dataMap.keys.sorted()[value.toInt()]
                return date
            }
        }

        lineChart.xAxis.textSize = 14f
        lineChart.axisLeft.textSize = 14f
        lineChart.axisLeft.axisMinimum = 0f // Минимальное значение по оси Y
        lineChart.axisRight.isEnabled = false // Отключаем правую ось Y
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Позиция оси X

        lineChart.legend.setDrawInside(false)
        lineChart.legend.textSize = 14f // Увеличенный размер шрифта легенды

        // Установка отступа для легенды (измените 8 на нужное значение в dp)
        val legendOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
        lineChart.setExtraOffsets(0f, 0f, 0f, legendOffset)

        lineChart.invalidate() // Обновляем график
    }

    private fun updateChart(mapOfEntities: Map<String, Long>, pieChart: PieChart) {
        val entries = ArrayList<PieEntry>()

        mapOfEntities.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Категории")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Цвета для разных категорий
        val colors = listOf(
            Color.parseColor("#FF6384"),
            Color.parseColor("#36A2EB"),
            Color.parseColor("#FFCE56"),
            Color.parseColor("#4BC0C0"),
            Color.parseColor("#9966FF")
        )
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data
        pieChart.invalidate()

    }
}
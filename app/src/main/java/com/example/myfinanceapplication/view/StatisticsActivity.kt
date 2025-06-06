package com.example.myfinanceapplication.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityStatisticsBinding
import com.example.myfinanceapplication.model.Cost
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

//            val periods = arrayOf("За последний год", "За последний месяц", "За последнюю неделю")
//            val spinnerAdapter = ArrayAdapter(this@StatisticsActivity, android.R.layout.simple_spinner_item, periods)
//            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerLineChartIncome.adapter = spinnerAdapter


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

    private fun filterData(selectedPeriod: String, originalList: List<Cost>): MutableList<Cost> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        val filteredList = mutableListOf<Cost>()

        when (selectedPeriod) {
            "За последний год" -> {
                calendar.add(Calendar.YEAR, -1)
                val yearAgo = calendar.time
                originalList.forEach {
                    val itemDate = sdf.parse(it.date)
                    if (itemDate != null && itemDate in yearAgo..currentDate) {
                        filteredList.add(it)
                    }
                }
            }

            "За последний месяц" -> {
                calendar.add(Calendar.MONTH, -1)
                val monthAgo = calendar.time
                originalList.forEach {
                    val itemDate = sdf.parse(it.date)
                    if (itemDate != null && itemDate in monthAgo..currentDate) {
                        filteredList.add(it)
                    }
                }
                Log.d("katrin_filter", filteredList.toString())
            }

            "За последнюю неделю" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                val weekAgo = calendar.time
                originalList.forEach {
                    val itemDate = sdf.parse(it.date)
                    if (itemDate != null && itemDate in weekAgo..currentDate) {
                        filteredList.add(it)
                    }
                }
            }

            else -> filteredList.addAll(originalList)
        }

        Toast.makeText(this, "Отображаются данные: $selectedPeriod", Toast.LENGTH_SHORT).show()
        return filteredList
    }

    private fun initIncomeStatistics() {
        setupPieChart(binding.pieChartIncome)

        lifecycle.coroutineScope.launch {
            viewModel.incomesLiveData.observeForever { incomesList ->
                Log.d("katrin_stat", incomesList.toString())
                if (incomesList.isNotEmpty()) {
                    val incomesListOfPair =
                        incomesList?.filter { it.category != null }
                            ?.associateBy({ it.category ?: "" }, { it.moneyCost.toLong() }) ?: mapOf()
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
                            ?.associateBy({ it.category ?: "" }, { it.moneyCost.toLong() }) ?: mapOf()
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
        val periods = resources.getStringArray(R.array.categoriesTimeForDiagram)
        viewModel.incomesLiveData.observeForever { incomes ->
            binding.spinnerLineChartIncome.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val incomesMap =
                            filterData(periods[position], incomes).associateBy(
                                { it.date },
                                { it.moneyCost.toFloat() })
                        setupLineChart(
                            dataMap = incomesMap,
                            lineChart = binding.lineChartIncome,
                            label = "Доходы"
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            val incomesMap =
                filterData(periods[0], incomes).associateBy({ it.date }, { it.moneyCost.toFloat() })
            setupLineChart(
                dataMap = incomesMap,
                lineChart = binding.lineChartIncome,
                label = "Доходы"
            )
        }
    }

    private val dateComparator: Comparator<String> = Comparator { date1, date2 ->
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate1 = LocalDate.parse(date1, dateFormatter)
        val localDate2 = LocalDate.parse(date2, dateFormatter)
        localDate1.compareTo(localDate2)
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

        pieChart.setDrawCenterText(false) // Отключаем текст в центре
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400)

        // Настройка легенды
        pieChart.legend.isEnabled = true
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL // Вертикальная ориентация
        pieChart.legend.verticalAlignment =
            Legend.LegendVerticalAlignment.CENTER // По центру по вертикали
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // Справа
        pieChart.legend.setDrawInside(false) // Не рисовать внутри графика

        // Увеличение шрифта легенды
        pieChart.legend.textSize = 14f // Увеличение шрифта до 14sp

        // Установка заголовка для легенды
        pieChart.legend.formSize = 14f // Размер формы в легенде
        pieChart.legend.formToTextSpace = 10f // Расстояние между формой и текстом легенды
        pieChart.legend.yEntrySpace = 10f // Расстояние между записями легенды

        // Отключаем отображение текстов категорий на диаграмме
        pieChart.setDrawEntryLabels(false) // Отключаем подписи категорий на диаграмме
    }

    private fun setupLineChart(
        dataMap: Map<String, Float>,
        lineChart: LineChart,
        label: String,
    ) {
        if (dataMap.isNotEmpty()) {
            // Сортируем данные по дате
            val sortedDates = dataMap.keys.sortedWith(dateComparator)
            val entries = mutableListOf<Entry>()

            // Создаем точки данных с индексами от 0 до size-1
            sortedDates.forEachIndexed { index, date ->
                entries.add(Entry(index.toFloat(), dataMap[date] ?: 0f))
            }

            val dataSet = LineDataSet(entries, label).apply {
                color = resources.getColor(R.color.dark_violet)
                valueTextColor = resources.getColor(R.color.color_of_text)
                valueTextSize = 14f
                setDrawValues(true)
                lineWidth = 2f
                setDrawCircles(true)
                circleRadius = 4f
                circleHoleRadius = 2f
                valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        return entry?.y?.toInt().toString()
                    }
                }
            }

            lineChart.apply {
                // Настройка отступов для крайних точек
                val extraOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16f,
                    resources.displayMetrics
                )

                // Рассчитываем отступы в зависимости от количества точек
                val xOffset = if (entries.size > 1) extraOffset else 0f
                setExtraOffsets(0f, 0f, xOffset, 0f)

                data = LineData(dataSet)

                xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < sortedDates.size) {
                                sortedDates[index]
                            } else {
                                ""
                            }
                        }
                    }
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f // Одна метка на точку
                    setAvoidFirstLastClipping(true) // Предотвращаем обрезание меток
                    labelCount = entries.size // Устанавливаем точное количество меток
                    textSize = 14f
                    setDrawGridLines(false)
                }

                axisLeft.apply {
                    textSize = 14f
                    axisMinimum = 0f
                    granularity = 1f
                }

                axisRight.isEnabled = false

                legend.apply {
                    setDrawInside(false)
                    textSize = 14f
                }

                // Дополнительные настройки для лучшего отображения
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                description.isEnabled = false

                // Автоматическое масштабирование по оси X
                setVisibleXRangeMaximum(entries.size.toFloat())
                moveViewToX(entries.last().x)

                // Настройка анимации
                animateX(1000)

                invalidate()
            }
        }
    }

    private fun updateChart(mapOfEntities: Map<String, Long>, pieChart: PieChart) {
        val entries = ArrayList<PieEntry>()

        mapOfEntities.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "")
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
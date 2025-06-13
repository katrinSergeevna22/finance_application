package com.example.myfinanceapplication.view

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {
    lateinit var binding: ActivityStatisticsBinding
    lateinit var viewModel: StatisticsViewModel
    lateinit var periods: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]

        periods = resources.getStringArray(R.array.categoriesTimeForDiagram)
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
        binding.apply {
            setupPieChart(pieChartIncome)

            lifecycle.coroutineScope.launch {
                viewModel.incomesLiveData.observeForever { incomesList ->
                    pieChartIncome.visibility = View.VISIBLE
                    tvEmptyPieChartIncome.visibility = View.GONE
                    if (incomesList.isNotEmpty()) {
                        val incomesListOfPair =
                            incomesList?.filter { it.category != null }
                                ?.associateBy({ it.category ?: "" }, { it.moneyCost.toLong() })
                                ?: mapOf()
                        updateChart(incomesListOfPair, pieChart = pieChartIncome)
                    } else {
                        pieChartIncome.visibility = View.GONE
                        tvEmptyPieChartIncome.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initExpenseStatistics() {
        binding.apply {
            setupPieChart(pieChartExpense)
            lifecycle.coroutineScope.launch {
                viewModel.expensesLiveData.observeForever { expensesList ->
                    if (expensesList.isNotEmpty()) {
                        pieChartExpense.visibility = View.VISIBLE
                        tvEmptyPieChartExpense.visibility = View.GONE
                        val expensesListOfPair =
                            expensesList?.filter { it.category != null }
                                ?.associateBy({ it.category ?: "" }, { it.moneyCost.toLong() })
                                ?: mapOf()
                        updateChart(expensesListOfPair, pieChart = pieChartExpense)
                    } else {
                        pieChartExpense.visibility = View.GONE
                        tvEmptyPieChartExpense.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initGoalsStatistics() {
        binding.apply {
            setupPieChart(pieChartGoals)
            lifecycle.coroutineScope.launch {
                viewModel.goalsLiveData.observeForever { goalsList ->
                    if (goalsList.isNotEmpty()) {
                        pieChartGoals.visibility = View.VISIBLE
                        tvEmptyPieChartGoals.visibility = View.GONE

                        pieChartProgressOfGoals.visibility = View.VISIBLE
                        tvEmptyPieChartProgressOfGoals.visibility = View.GONE

                        val goalsListOfPair =
                            goalsList?.filter { it.category != null }
                                ?.associateBy({ it.category ?: "" }, { it.moneyGoal.toLong() })
                                ?: mapOf()

                        updateChart(goalsListOfPair, pieChart = pieChartGoals)

                        initGoalsProgressStatistics()
                    } else {
                        pieChartGoals.visibility = View.GONE
                        tvEmptyPieChartGoals.visibility = View.VISIBLE

                        pieChartProgressOfGoals.visibility = View.GONE
                        tvEmptyPieChartProgressOfGoals.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initGoalsProgressStatistics() {
        setupPieChart(binding.pieChartProgressOfGoals)
        updateChart(viewModel.getProgressOfGoals(), pieChart = binding.pieChartProgressOfGoals)
    }

    private fun filterData(
        selectedPeriod: String,
        originalList: List<Cost>,
    ): MutableList<Cost> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        val filteredList = mutableListOf<Cost>()

        when (selectedPeriod) {
            periods[0] -> {
                calendar.add(Calendar.YEAR, -1)
                val yearAgo = calendar.time
                originalList.forEach {
                    val itemDate = sdf.parse(it.date)
                    if (itemDate != null && itemDate in yearAgo..currentDate) {
                        filteredList.add(it)
                    }
                }
            }

            periods[1] -> {
                calendar.add(Calendar.MONTH, -1)
                val monthAgo = calendar.time
                originalList.forEach {
                    val itemDate = sdf.parse(it.date)
                    if (itemDate != null && itemDate in monthAgo..currentDate) {
                        filteredList.add(it)
                    }
                }
            }

            periods[2] -> {
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
        return filteredList
    }

    private fun initEditOfIncome() {
        binding.apply {
            viewModel.incomesLiveData.observeForever { incomes ->
                spinnerLineChartIncome.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            setupLineChart(
                                incomes = filterData(periods[position], incomes),
                                lineChart = lineChartIncome,
                                period = periods[position],
                                label = "Доходы"
                            )
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                setupLineChart(
                    incomes = filterData(periods[0], incomes),
                    lineChart = lineChartIncome,
                    period = periods[0],
                    label = "Доходы"
                )
            }
        }
    }

    private fun setupLineChart(
        incomes: List<Cost>,
        lineChart: LineChart,
        period: String,
        label: String
    ) {
        lineChart.clear()

        val periods = resources.getStringArray(R.array.categoriesTimeForDiagram)
        val entries: List<Entry>
        val xAxisLabels: List<String>
        val descriptionText: String

        when (period) {
            periods[0] -> {
                // 1. Группируем доходы по месяцам (0-based месяц)
                val monthlyData = incomes.groupBy { cost ->
                    val calendar = Calendar.getInstance().apply {
                        time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cost.date)
                            ?: Date()
                    }
                    "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}" // 0-based
                }.mapValues { (_, costsInMonth) ->
                    val totalAmount = costsInMonth.sumOf { it.moneyCost }
                    val latestDate = costsInMonth.maxByOrNull {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)?.time
                            ?: 0L
                    }?.date
                    Pair(totalAmount, latestDate)
                }

                // 2. Создаем 12 месяцев (от текущего назад)
                val allMonths = (0..11).map { monthOffset ->
                    Calendar.getInstance().apply {
                        add(Calendar.MONTH, -11 + monthOffset)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }.time
                }

                // 3. Подготавливаем данные для графика (используем 0-based месяц)
                entries = allMonths.mapIndexed { index, monthStart ->
                    val calendar = Calendar.getInstance().apply { time = monthStart }
                    val monthKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"
                    val (totalAmount, latestDate) = monthlyData[monthKey] ?: Pair(0.0, null)

                    Entry(index.toFloat(), totalAmount.toFloat(), latestDate ?: monthStart)
                }

                xAxisLabels = allMonths.map {
                    SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(it)
                }

                descriptionText = "$label за последний год"
            }

            periods[1] -> {
                // 1. Получаем текущий месяц и год
                val currentCalendar = Calendar.getInstance()
                val currentMonth = currentCalendar.get(Calendar.MONTH)
                val currentYear = currentCalendar.get(Calendar.YEAR)

                // 2. Фильтруем доходы за текущий месяц
                val monthIncomes = incomes.filter { cost ->
                    try {
                        val costDate =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cost.date)
                        val costCalendar =
                            Calendar.getInstance().apply { time = costDate ?: Date() }
                        costCalendar.get(Calendar.MONTH) == currentMonth &&
                                costCalendar.get(Calendar.YEAR) == currentYear
                    } catch (e: Exception) {
                        false
                    }
                }.sortedBy {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)?.time ?: 0L
                }

                // 3. Если данных нет, показываем нулевое значение
                if (monthIncomes.isEmpty()) {
                    entries = listOf(Entry(0f, 0f, currentCalendar.time))
                    xAxisLabels = listOf("Нет данных")
                } else {
                    // 4. Создаем накопленные суммы по дням
                    val dailySums = monthIncomes.groupBy { cost ->
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cost.date)?.time
                            ?: 0L
                    }.mapValues { (_, costs) ->
                        costs.sumOf { it.moneyCost }
                    }.toSortedMap()

                    // 5. Подготавливаем данные для графика
                    entries = dailySums.entries.mapIndexed { index, entry ->
                        Entry(
                            index.toFloat(),
                            entry.value.toFloat(),
                            Date(entry.key)
                        )
                    }

                    xAxisLabels = dailySums.keys.map {
                        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it))
                    }
                }
                descriptionText = "$label за текущий месяц"
            }

            periods[2] -> {
                // 1. Устанавливаем диапазон дат (последние 7 дней)
                val calendar = Calendar.getInstance().apply {
                    time = Date() // Текущая дата
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val endDate = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 дней включая сегодня
                val startDate = calendar.time

                // 2. Фильтруем и сортируем данные
                val weeklyIncomes = incomes.filter { cost ->
                    try {
                        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .parse(cost.date) ?: return@filter false
                        date in startDate..endDate
                    } catch (e: Exception) {
                        false
                    }
                }.sortedBy {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)?.time ?: 0L
                }

                // 3. Создаем полный диапазон дат (7 дней)
                val allDays = (0..6).map { dayOffset ->
                    Calendar.getInstance().apply {
                        time = endDate
                        add(Calendar.DAY_OF_YEAR, -dayOffset)
                    }.time.time
                }.reversed() // Сортировка от старых к новым

                // 4. Группируем по дням и суммируем (без накопления)
                val dailySums = allDays.associateWith { dayTimestamp ->
                    val sum = weeklyIncomes.filter { cost ->
                        val costDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .parse(cost.date)?.time ?: 0L
                        costDate == dayTimestamp
                    }.sumOf { it.moneyCost }

                    sum
                }

                // 5. Подготавливаем данные для графика
                if (dailySums.isEmpty()) {
                    entries = listOf(Entry(0f, 0f, endDate))
                    xAxisLabels = listOf("Нет данных")
                } else {
                    entries = allDays.mapIndexed { index, dayTimestamp ->
                        Entry(
                            index.toFloat(),
                            dailySums[dayTimestamp]?.toFloat() ?: 0f,
                            Date(dayTimestamp)
                        )
                    }

                    xAxisLabels = allDays.map { timestamp ->
                        SimpleDateFormat("E\ndd.MM", Locale.getDefault())
                            .format(Date(timestamp))
                    }
                }

                descriptionText = "$label за последнюю неделю"
            }

            else -> {
                entries = emptyList()
                xAxisLabels = emptyList()
                descriptionText = ""
            }
        }

        // Настройка оси X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xAxisLabels.getOrNull(value.toInt()) ?: ""
            }
        }

        // Настройка оси Y
        lineChart.axisRight.isEnabled = false

        // Создаем DataSet
        val dataSet = LineDataSet(entries, label).apply {
            color = resources.getColor(R.color.dark_violet)
            valueTextColor = resources.getColor(R.color.color_of_text)
            valueTextSize = 14f
            setDrawValues(true)
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 4f
            circleColors = listOf(resources.getColor(R.color.violet))
            circleHoleRadius = 2f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        lineChart.apply {
            // Устанавливаем данные и обновляем график
            data = LineData(dataSet)
            description.text = descriptionText

            val extraOffset = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                resources.displayMetrics
            )

            // Рассчитываем отступы в зависимости от количества точек
            val xOffset = if (entries.size > 1) extraOffset else 0f
            setExtraOffsets(0f, 0f, xOffset, 0f)

            xAxis.apply {
                textSize = 12f
            }
            legend.textSize = 12f

            setPinchZoom(true)

            // Настройка анимации
            animateX(1000)

            invalidate()
        }
    }

    private fun initEditOfExpenses() {
        binding.apply {
            viewModel.expensesLiveData.observeForever { expenses ->
                spinnerLineChartExpense.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            setupLineChart(
                                incomes = filterData(periods[position], expenses),
                                lineChart = lineChartExpense,
                                period = periods[position],
                                label = "Расходы"
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }

                setupLineChart(
                    incomes = filterData(periods[0], expenses),
                    lineChart = lineChartExpense,
                    period = periods[0],
                    label = "Расходы"
                )
            }
        }
    }

    private fun initEditOfProgressOfGoal() {
        binding.apply {
            viewModel.expensesLiveData.observeForever { expenses ->
                spinnerLineChartGoals.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            setupLineChart(
                                incomes = filterData(
                                    periods[position],
                                    expenses.filter { it.goal != "" }),
                                lineChart = lineChartProgressOfGoals,
                                period = periods[position],
                                label = "Отложено на достижение целей",
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }

                setupLineChart(
                    incomes = filterData(periods[0], expenses.filter { it.goal != "" }),
                    lineChart = lineChartProgressOfGoals,
                    period = periods[0],
                    label = "Отложено на достижение целей"
                )
            }
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
            "#FF6384".toColorInt(),
            "#36A2EB".toColorInt(),
            "#FFCE56".toColorInt(),
            "#4BC0C0".toColorInt(),
            "#9966FF".toColorInt(),
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
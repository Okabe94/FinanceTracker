package com.software.financetracker.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

internal object DatabaseSeeder : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        insertCategories(db)
        insertExpenses(db)
        insertIncome(db)
        insertGoals(db)
        insertExchangeRates(db)
        insertInvestments(db)
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun insertCategory(
        db: SupportSQLiteDatabase,
        id: Long,
        name: String,
        colorArgb: Int,
        iconKey: String,
        monthlyLimitCop: Long?
    ) {
        val limit = monthlyLimitCop?.toString() ?: "NULL"
        db.execSQL(
            "INSERT INTO categories (id, name, colorArgb, iconKey, monthlyLimitCop, updatedAt) VALUES ($id, '${name.replace("'", "''")}', $colorArgb, '$iconKey', $limit, 0)"
        )
    }

    private fun insertExpense(
        db: SupportSQLiteDatabase,
        categoryId: Long,
        amountCop: Long,
        description: String,
        date: String
    ) {
        db.execSQL(
            "INSERT INTO expenses (categoryId, amountCop, description, date) VALUES ($categoryId, $amountCop, '${description.replace("'", "''")}', '$date')"
        )
    }

    private fun insertIncome(
        db: SupportSQLiteDatabase,
        amountCop: Long,
        source: String,
        date: String,
        notes: String = ""
    ) {
        db.execSQL(
            "INSERT INTO income (amountCop, source, date, notes) VALUES ($amountCop, '${source.replace("'", "''")}', '$date', '${notes.replace("'", "''")}')"
        )
    }

    private fun insertGoal(
        db: SupportSQLiteDatabase,
        name: String,
        targetAmountCop: Long,
        currentAmountCop: Long,
        deadlineDate: String,
        colorArgb: Int,
        isAchieved: Boolean
    ) {
        val achieved = if (isAchieved) 1 else 0
        db.execSQL(
            "INSERT INTO goals (name, targetAmountCop, currentAmountCop, deadlineDate, colorArgb, isAchieved) VALUES ('${name.replace("'", "''")}', $targetAmountCop, $currentAmountCop, '$deadlineDate', $colorArgb, $achieved)"
        )
    }

    private fun insertExchangeRate(
        db: SupportSQLiteDatabase,
        fromCurrency: String,
        toCurrency: String,
        rate: Double,
        updatedDate: String
    ) {
        db.execSQL(
            "INSERT INTO exchange_rates (fromCurrency, toCurrency, rate, updatedDate) VALUES ('$fromCurrency', '$toCurrency', $rate, '$updatedDate')"
        )
    }

    private fun insertInvestment(
        db: SupportSQLiteDatabase,
        id: Long,
        name: String,
        currency: String,
        colorArgb: Int,
        iconKey: String,
        annualRatePercent: Double?,
        maturityDate: String?,
        createdDate: String,
        targetValueMinorUnits: Long?,
        targetDate: String?
    ) {
        val rate = annualRatePercent?.toString() ?: "NULL"
        val maturity = if (maturityDate != null) "'$maturityDate'" else "NULL"
        val target = targetValueMinorUnits?.toString() ?: "NULL"
        val tDate = if (targetDate != null) "'$targetDate'" else "NULL"
        db.execSQL(
            "INSERT INTO investments (id, name, currency, colorArgb, iconKey, annualRatePercent, maturityDate, createdDate, targetValueMinorUnits, targetDate) VALUES ($id, '${name.replace("'", "''")}', '$currency', $colorArgb, '$iconKey', $rate, $maturity, '$createdDate', $target, $tDate)"
        )
    }

    private fun insertInvestmentEntry(
        db: SupportSQLiteDatabase,
        investmentId: Long,
        entryType: String,
        amountMinorUnits: Long,
        date: String,
        notes: String = ""
    ) {
        db.execSQL(
            "INSERT INTO investment_entries (investmentId, entryType, amountMinorUnits, date, notes) VALUES ($investmentId, '$entryType', $amountMinorUnits, '$date', '${notes.replace("'", "''")}')"
        )
    }

    // ── categories ───────────────────────────────────────────────────────────

    private fun insertCategories(db: SupportSQLiteDatabase) {
        insertCategory(db, 1, "Alimentación", 0xFFEF5350.toInt(), "restaurant", 800_000L)
        insertCategory(db, 2, "Transporte",   0xFF42A5F5.toInt(), "directions_car", 300_000L)
        insertCategory(db, 3, "Entretenimiento", 0xFFAB47BC.toInt(), "sports_esports", 200_000L)
        insertCategory(db, 4, "Salud",        0xFF66BB6A.toInt(), "local_hospital", null)
        insertCategory(db, 5, "Hogar",        0xFFFFA726.toInt(), "home", 1_500_000L)
        insertCategory(db, 6, "Ropa",         0xFFEC407A.toInt(), "checkroom", 400_000L)
        insertCategory(db, 7, "Café",         0xFF8D6E63.toInt(), "local_cafe", 150_000L)
    }

    // ── expenses (Jun 2025 – May 2026) ───────────────────────────────────────

    private fun insertExpenses(db: SupportSQLiteDatabase) {

        // ── Jun 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  92_000, "Mercado semanal",       "2025-06-04")
        insertExpense(db, 1,  35_000, "Almuerzo restaurante",  "2025-06-11")
        insertExpense(db, 1,  78_000, "Mercado semanal",       "2025-06-18")
        insertExpense(db, 1,  42_000, "Cena de cumpleaños",    "2025-06-25")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-06-02")
        insertExpense(db, 2,  30_000, "Uber a reunión",        "2025-06-17")
        insertExpense(db, 2,  80_000, "Gasolina",              "2025-06-28")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-06-01")
        insertExpense(db, 3,  45_000, "Partido de fútbol",     "2025-06-22")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-06-01")
        insertExpense(db, 5, 160_000, "Servicios públicos",    "2025-06-10")
        insertExpense(db, 6, 120_000, "Camisetas y bermudas",  "2025-06-14")
        insertExpense(db, 7,   7_500, "Café de la mañana",     "2025-06-03")
        insertExpense(db, 7,  22_000, "Café con amigos",       "2025-06-19")
        insertExpense(db, 7,   8_000, "Café en el trabajo",    "2025-06-26")

        // ── Jul 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  88_000, "Mercado semanal",       "2025-07-02")
        insertExpense(db, 1,  55_000, "Cena especial",         "2025-07-12")
        insertExpense(db, 1,  95_000, "Mercado quincenal",     "2025-07-18")
        insertExpense(db, 1,  28_000, "Domicilio pizza",       "2025-07-27")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-07-04")
        insertExpense(db, 2,  85_000, "Gasolina",              "2025-07-19")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-07-01")
        insertExpense(db, 3, 120_000, "Salida de camping",     "2025-07-20")
        insertExpense(db, 4,  55_000, "Consulta general",      "2025-07-08")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-07-01")
        insertExpense(db, 5, 175_000, "Servicios públicos",    "2025-07-10")
        insertExpense(db, 5,  65_000, "Elementos de aseo",     "2025-07-22")
        insertExpense(db, 7,   7_000, "Café mañanero",         "2025-07-07")
        insertExpense(db, 7,  15_000, "Café frío",             "2025-07-15")
        insertExpense(db, 7,   7_500, "Café en el trabajo",    "2025-07-28")

        // ── Aug 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  98_000, "Mercado mensual",       "2025-08-06")
        insertExpense(db, 1,  45_000, "Almuerzo de negocios",  "2025-08-13")
        insertExpense(db, 1,  72_000, "Mercado semanal",       "2025-08-22")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-08-01")
        insertExpense(db, 2,  40_000, "Uber al aeropuerto",    "2025-08-15")
        insertExpense(db, 2,  82_000, "Gasolina",              "2025-08-26")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-08-01")
        insertExpense(db, 3,  80_000, "Concierto",             "2025-08-09")
        insertExpense(db, 3,  25_000, "Cine con amigos",       "2025-08-23")
        insertExpense(db, 4,  38_000, "Medicamentos",          "2025-08-14")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-08-01")
        insertExpense(db, 5, 168_000, "Servicios públicos",    "2025-08-10")
        insertExpense(db, 6, 250_000, "Zapatos de cuero",      "2025-08-17")
        insertExpense(db, 6,  85_000, "Camisa formal",         "2025-08-17")
        insertExpense(db, 7,   8_000, "Café mañanero",         "2025-08-05")
        insertExpense(db, 7,  28_000, "Café con clientes",     "2025-08-19")
        insertExpense(db, 7,   7_000, "Café tarde",            "2025-08-27")

        // ── Sep 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1, 105_000, "Mercado mensual",       "2025-09-03")
        insertExpense(db, 1,  38_000, "Almuerzo restaurante",  "2025-09-10")
        insertExpense(db, 1,  68_000, "Mercado semanal",       "2025-09-20")
        insertExpense(db, 1,  35_000, "Domicilio sushi",       "2025-09-27")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-09-01")
        insertExpense(db, 2,  35_000, "Uber nocturno",         "2025-09-18")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-09-01")
        insertExpense(db, 3,  60_000, "Videojuego nuevo",      "2025-09-12")
        insertExpense(db, 4, 150_000, "Odontólogo limpieza",   "2025-09-05")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-09-01")
        insertExpense(db, 5, 172_000, "Servicios públicos",    "2025-09-10")
        insertExpense(db, 5,  48_000, "Bombillos y accesorios","2025-09-24")
        insertExpense(db, 7,   7_500, "Café de la mañana",     "2025-09-04")
        insertExpense(db, 7,  18_000, "Café y pastelería",     "2025-09-16")
        insertExpense(db, 7,   8_000, "Café en reunión",       "2025-09-25")

        // ── Oct 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  88_000, "Mercado quincenal",     "2025-10-01")
        insertExpense(db, 1,  42_000, "Almuerzo cumpleaños",   "2025-10-11")
        insertExpense(db, 1,  92_000, "Mercado quincenal",     "2025-10-18")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-10-03")
        insertExpense(db, 2,  88_000, "Gasolina",              "2025-10-15")
        insertExpense(db, 2,  25_000, "Parqueadero mensual",   "2025-10-20")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-10-01")
        insertExpense(db, 3,  45_000, "Bolos y diversión",     "2025-10-05")
        insertExpense(db, 3,  35_000, "Entradas al cine",      "2025-10-25")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-10-01")
        insertExpense(db, 5, 185_000, "Servicios públicos",    "2025-10-10")
        insertExpense(db, 5, 120_000, "Mantenimiento nevera",  "2025-10-22")
        insertExpense(db, 6, 200_000, "Ropa de temporada",     "2025-10-08")
        insertExpense(db, 7,   7_500, "Café mañanero",         "2025-10-02")
        insertExpense(db, 7,  20_000, "Café con familia",      "2025-10-14")
        insertExpense(db, 7,   8_000, "Café y jugo",           "2025-10-28")

        // ── Nov 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1, 115_000, "Mercado mensual",       "2025-11-05")
        insertExpense(db, 1,  65_000, "Cena de noviembre",     "2025-11-14")
        insertExpense(db, 1,  78_000, "Mercado semanal",       "2025-11-22")
        insertExpense(db, 1,  55_000, "Domicilio especial",    "2025-11-29")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-11-03")
        insertExpense(db, 2,  82_000, "Gasolina",              "2025-11-18")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-11-01")
        insertExpense(db, 3,  90_000, "Cena con amigos",       "2025-11-08")
        insertExpense(db, 3,  55_000, "Videojuego Black Friday","2025-11-28")
        insertExpense(db, 4,  42_000, "Medicamentos",          "2025-11-12")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-11-01")
        insertExpense(db, 5, 178_000, "Servicios públicos",    "2025-11-10")
        insertExpense(db, 6, 350_000, "Ropa Black Friday",     "2025-11-28")
        insertExpense(db, 7,   7_500, "Café de la mañana",     "2025-11-04")
        insertExpense(db, 7,   9_000, "Café y muffin",         "2025-11-19")
        insertExpense(db, 7,  25_000, "Café especial navidad", "2025-11-25")

        // ── Dec 2025 ──────────────────────────────────────────────────────────
        insertExpense(db, 1, 180_000, "Mercado navideño",      "2025-12-10")
        insertExpense(db, 1,  95_000, "Cena extra navidad",    "2025-12-18")
        insertExpense(db, 1, 120_000, "Mercado Año Nuevo",     "2025-12-27")
        insertExpense(db, 1,  65_000, "Domicilio nochevieja",  "2025-12-31")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2025-12-02")
        insertExpense(db, 2,  90_000, "Gasolina viajes",       "2025-12-20")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2025-12-01")
        insertExpense(db, 3, 200_000, "Cena navideña restaurante","2025-12-24")
        insertExpense(db, 3, 150_000, "Año Nuevo fiesta",      "2025-12-31")
        insertExpense(db, 4,  80_000, "Exámenes de rutina",    "2025-12-05")
        insertExpense(db, 5, 900_000, "Arriendo",              "2025-12-01")
        insertExpense(db, 5, 195_000, "Servicios públicos",    "2025-12-10")
        insertExpense(db, 5, 250_000, "Decoración navideña",   "2025-12-08")
        insertExpense(db, 6, 180_000, "Ropa navideña",         "2025-12-12")
        insertExpense(db, 7,   9_000, "Café navideño",         "2025-12-05")
        insertExpense(db, 7,  35_000, "Café y torta",          "2025-12-22")
        insertExpense(db, 7,  10_000, "Café tinto",            "2025-12-29")

        // ── Jan 2026 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  85_000, "Mercado enero",         "2026-01-07")
        insertExpense(db, 1,  42_000, "Almuerzo de negocios",  "2026-01-14")
        insertExpense(db, 1,  90_000, "Mercado semanal",       "2026-01-21")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2026-01-03")
        insertExpense(db, 2,  82_000, "Gasolina",              "2026-01-17")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2026-01-01")
        insertExpense(db, 3,  48_000, "Cine estreno",          "2026-01-10")
        insertExpense(db, 4,  65_000, "Consulta médica",       "2026-01-15")
        insertExpense(db, 5, 900_000, "Arriendo",              "2026-01-01")
        insertExpense(db, 5, 162_000, "Servicios públicos",    "2026-01-10")
        insertExpense(db, 7,   7_500, "Café mañanero",         "2026-01-08")
        insertExpense(db, 7,  15_000, "Té y pastel",           "2026-01-20")
        insertExpense(db, 7,   7_500, "Café en el trabajo",    "2026-01-27")

        // ── Feb 2026 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  28_000, "Almuerzo en restaurante","2026-02-05")
        insertExpense(db, 1,  85_000, "Mercado semanal",       "2026-02-12")
        insertExpense(db, 1,  65_000, "Cena familiar",         "2026-02-20")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2026-02-03")
        insertExpense(db, 2,  35_000, "Uber al aeropuerto",    "2026-02-18")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2026-02-01")
        insertExpense(db, 3,  30_000, "Entradas al cine",      "2026-02-14")
        insertExpense(db, 4,  45_000, "Medicamentos",          "2026-02-08")
        insertExpense(db, 4,  60_000, "Consulta médica",       "2026-02-25")
        insertExpense(db, 5, 900_000, "Arriendo",              "2026-02-01")
        insertExpense(db, 5, 180_000, "Servicios públicos",    "2026-02-10")
        insertExpense(db, 6,  75_000, "Camisetas básicas",     "2026-02-15")
        insertExpense(db, 7,   7_500, "Café de la mañana",     "2026-02-04")
        insertExpense(db, 7,  25_000, "Café con amigos",       "2026-02-18")

        // ── Mar 2026 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  45_000, "Domicilio pizza",       "2026-03-03")
        insertExpense(db, 1, 120_000, "Mercado mensual",       "2026-03-15")
        insertExpense(db, 1,  38_000, "Almuerzo de negocios",  "2026-03-22")
        insertExpense(db, 2,  80_000, "Gasolina",              "2026-03-05")
        insertExpense(db, 2,  45_000, "Revisión técnica",      "2026-03-20")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2026-03-01")
        insertExpense(db, 3,  80_000, "Concierto",             "2026-03-08")
        insertExpense(db, 4,  35_000, "Vitaminas",             "2026-03-10")
        insertExpense(db, 5, 900_000, "Arriendo",              "2026-03-01")
        insertExpense(db, 5, 165_000, "Servicios públicos",    "2026-03-10")
        insertExpense(db, 5,  55_000, "Implementos de aseo",   "2026-03-18")
        insertExpense(db, 6, 180_000, "Zapatos deportivos",    "2026-03-08")
        insertExpense(db, 7,   6_500, "Café en el trabajo",    "2026-03-06")
        insertExpense(db, 7,  18_000, "Té y pasteles",         "2026-03-21")

        // ── Apr 2026 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  95_000, "Cena de cumpleaños",    "2026-04-08")
        insertExpense(db, 1,  78_000, "Mercado semanal",       "2026-04-18")
        insertExpense(db, 2,  50_000, "Recarga tarjeta SITP",  "2026-04-02")
        insertExpense(db, 2,  25_000, "Uber nocturno",         "2026-04-15")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2026-04-01")
        insertExpense(db, 3,  50_000, "Videojuego",            "2026-04-05")
        insertExpense(db, 4, 120_000, "Exámenes de laboratorio","2026-04-05")
        insertExpense(db, 4, 180_000, "Odontólogo",            "2026-04-22")
        insertExpense(db, 5, 900_000, "Arriendo",              "2026-04-01")
        insertExpense(db, 5, 195_000, "Servicios públicos",    "2026-04-10")
        insertExpense(db, 6,  95_000, "Pantalón jeans",        "2026-04-12")
        insertExpense(db, 7,   7_000, "Café mañanero",         "2026-04-09")
        insertExpense(db, 7,  12_000, "Café frío",             "2026-04-24")

        // ── May 2026 ──────────────────────────────────────────────────────────
        insertExpense(db, 1,  22_000, "Comida rápida",         "2026-05-02")
        insertExpense(db, 1, 110_000, "Mercado mensual",       "2026-05-10")
        insertExpense(db, 2,  85_000, "Gasolina",              "2026-05-05")
        insertExpense(db, 2,  15_000, "Parqueadero",           "2026-05-12")
        insertExpense(db, 3,  17_900, "Netflix mensual",       "2026-05-01")
        insertExpense(db, 3,  35_000, "Bolos con amigos",      "2026-05-08")
        insertExpense(db, 4,  28_000, "Medicamentos",          "2026-05-07")
        insertExpense(db, 5, 900_000, "Arriendo",              "2026-05-01")
        insertExpense(db, 5,  80_000, "Plomero",               "2026-05-08")
        insertExpense(db, 6,  45_000, "Ropa interior",         "2026-05-03")
        insertExpense(db, 6, 150_000, "Chaqueta",              "2026-05-14")
        insertExpense(db, 7,   7_500, "Café",                  "2026-05-06")
    }

    // ── income (Jun 2025 – May 2026) ─────────────────────────────────────────

    private fun insertIncome(db: SupportSQLiteDatabase) {
        // Salario mensual
        insertIncome(db, 4_200_000, "Salario", "2025-06-25")
        insertIncome(db, 4_200_000, "Salario", "2025-07-25")
        insertIncome(db, 4_200_000, "Salario", "2025-08-25")
        insertIncome(db, 4_200_000, "Salario", "2025-09-25")
        insertIncome(db, 4_200_000, "Salario", "2025-10-25")
        insertIncome(db, 4_200_000, "Salario", "2025-11-25")
        insertIncome(db, 4_500_000, "Salario", "2025-12-20", "Prima navideña incluida")
        insertIncome(db, 4_200_000, "Salario", "2026-01-25")
        insertIncome(db, 4_200_000, "Salario", "2026-02-25")
        insertIncome(db, 4_200_000, "Salario", "2026-03-25")
        insertIncome(db, 4_200_000, "Salario", "2026-04-25")
        insertIncome(db, 4_200_000, "Salario", "2026-05-25")

        // Proyectos freelance
        insertIncome(db, 1_200_000, "Freelance", "2025-07-14", "Proyecto diseño web")
        insertIncome(db,   900_000, "Freelance", "2025-10-08", "Consultoría técnica")
        insertIncome(db, 1_500_000, "Freelance", "2026-02-18", "Desarrollo app móvil")

        // Intereses CDT (trimestrales)
        insertIncome(db,  95_833, "Ingresos pasivos", "2025-09-15", "Intereses CDT Bancolombia")
        insertIncome(db,  95_833, "Ingresos pasivos", "2025-12-15", "Intereses CDT Bancolombia")
        insertIncome(db,  95_833, "Ingresos pasivos", "2026-03-15", "Intereses CDT Bancolombia")
    }

    // ── goals ────────────────────────────────────────────────────────────────

    private fun insertGoals(db: SupportSQLiteDatabase) {
        insertGoal(db, "Fondo de emergencia", 15_000_000,  8_500_000, "2027-01-31", 0xFF1565C0.toInt(), false)
        insertGoal(db, "Vacaciones Europa",   12_000_000,  4_800_000, "2026-12-31", 0xFF7B1FA2.toInt(), false)
        insertGoal(db, "MacBook Pro",          8_500_000,  8_500_000, "2025-09-30", 0xFF2E7D32.toInt(), true)
    }

    // ── exchange rates ────────────────────────────────────────────────────────

    private fun insertExchangeRates(db: SupportSQLiteDatabase) {
        insertExchangeRate(db, "USD", "COP", 4150.0, "2026-05-27")
        insertExchangeRate(db, "EUR", "COP", 4580.0, "2026-05-27")
        insertExchangeRate(db, "GBP", "COP", 5320.0, "2026-05-27")
    }

    // ── investments (Jun 2025 – May 2026) ─────────────────────────────────────

    private fun insertInvestments(db: SupportSQLiteDatabase) {
        // CDT Bancolombia — COP, 11.5% EA, vence jun 2026
        insertInvestment(
            db, id = 1, name = "CDT Bancolombia", currency = "COP",
            colorArgb = 0xFF1565C0.toInt(), iconKey = "account_balance",
            annualRatePercent = 11.5, maturityDate = "2026-06-15",
            createdDate = "2025-06-15",
            targetValueMinorUnits = 11_150_000L, targetDate = "2026-06-15"
        )
        insertInvestmentEntry(db, 1, "CASH_INJECTION", 10_000_000, "2025-06-15", "Apertura CDT")
        insertInvestmentEntry(db, 1, "VALUE_SNAPSHOT",  10_287_500, "2025-09-15")
        insertInvestmentEntry(db, 1, "VALUE_SNAPSHOT",  10_575_000, "2025-12-15")
        insertInvestmentEntry(db, 1, "VALUE_SNAPSHOT",  10_862_500, "2026-03-15")
        insertInvestmentEntry(db, 1, "VALUE_SNAPSHOT",  11_092_800, "2026-05-28")

        // ETF S&P 500 — USD (minor units = centavos de dólar)
        insertInvestment(
            db, id = 2, name = "ETF S&P 500", currency = "USD",
            colorArgb = 0xFF2E7D32.toInt(), iconKey = "trending_up",
            annualRatePercent = null, maturityDate = null,
            createdDate = "2025-07-01",
            targetValueMinorUnits = null, targetDate = null
        )
        insertInvestmentEntry(db, 2, "CASH_INJECTION",  80_000, "2025-07-01", "Primera compra")
        insertInvestmentEntry(db, 2, "VALUE_SNAPSHOT",  84_200, "2025-09-30")
        insertInvestmentEntry(db, 2, "CASH_INJECTION",  40_000, "2025-10-01")
        insertInvestmentEntry(db, 2, "VALUE_SNAPSHOT", 129_500, "2025-12-31")
        insertInvestmentEntry(db, 2, "CASH_INJECTION",  40_000, "2026-01-02")
        insertInvestmentEntry(db, 2, "VALUE_SNAPSHOT", 181_000, "2026-03-31")
        insertInvestmentEntry(db, 2, "CASH_INJECTION",  40_000, "2026-04-01")
        insertInvestmentEntry(db, 2, "DIVIDEND",            850, "2026-04-15", "Dividendo trimestral")
        insertInvestmentEntry(db, 2, "VALUE_SNAPSHOT", 218_000, "2026-05-28")

        // Pensión Voluntaria — COP, 8.5% EA, aportes mensuales
        insertInvestment(
            db, id = 3, name = "Pensión Voluntaria", currency = "COP",
            colorArgb = 0xFFE65100.toInt(), iconKey = "savings",
            annualRatePercent = 8.5, maturityDate = null,
            createdDate = "2025-06-01",
            targetValueMinorUnits = null, targetDate = null
        )
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-06-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-07-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-08-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-09-01")
        insertInvestmentEntry(db, 3, "VALUE_SNAPSHOT", 2_038_500, "2025-09-01", "Valoración Q3 2025")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-10-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-11-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2025-12-01")
        insertInvestmentEntry(db, 3, "VALUE_SNAPSHOT", 4_120_200, "2025-12-01", "Valoración Q4 2025")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2026-01-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2026-02-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2026-03-01")
        insertInvestmentEntry(db, 3, "VALUE_SNAPSHOT", 6_215_800, "2026-03-01", "Valoración Q1 2026")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2026-04-01")
        insertInvestmentEntry(db, 3, "CASH_INJECTION", 500_000, "2026-05-01")
        insertInvestmentEntry(db, 3, "VALUE_SNAPSHOT", 7_250_300, "2026-05-28", "Valoración actual")
    }
}

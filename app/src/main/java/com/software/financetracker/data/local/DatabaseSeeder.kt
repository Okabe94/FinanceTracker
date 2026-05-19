package com.software.financetracker.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

internal object DatabaseSeeder : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        insertCategories(db)
        insertExpenses(db)
    }

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
            "INSERT INTO categories (id, name, colorArgb, iconKey, monthlyLimitCop) VALUES ($id, '${name.replace("'", "''")}', $colorArgb, '$iconKey', $limit)"
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

    private fun insertCategories(db: SupportSQLiteDatabase) {
        insertCategory(db, 1, "Alimentación", 0xFFEF5350.toInt(), "restaurant", 800_000L)
        insertCategory(db, 2, "Transporte", 0xFF42A5F5.toInt(), "directions_car", 300_000L)
        insertCategory(db, 3, "Entretenimiento", 0xFFAB47BC.toInt(), "sports_esports", 200_000L)
        insertCategory(db, 4, "Salud", 0xFF66BB6A.toInt(), "local_hospital", null)
        insertCategory(db, 5, "Hogar", 0xFFFFA726.toInt(), "home", 1_500_000L)
        insertCategory(db, 6, "Ropa", 0xFFEC407A.toInt(), "checkroom", 400_000L)
        insertCategory(db, 7, "Café", 0xFF8D6E63.toInt(), "local_cafe", 150_000L)
    }

    private fun insertExpenses(db: SupportSQLiteDatabase) {
        // Alimentación
        insertExpense(db, 1, 28_000, "Almuerzo en restaurante", "2026-02-05")
        insertExpense(db, 1, 85_000, "Mercado semanal", "2026-02-12")
        insertExpense(db, 1, 65_000, "Cena familiar", "2026-02-20")
        insertExpense(db, 1, 45_000, "Domicilio pizza", "2026-03-03")
        insertExpense(db, 1, 120_000, "Mercado mensual", "2026-03-15")
        insertExpense(db, 1, 38_000, "Almuerzo de negocios", "2026-03-22")
        insertExpense(db, 1, 95_000, "Cena de cumpleaños", "2026-04-08")
        insertExpense(db, 1, 78_000, "Mercado semanal", "2026-04-18")
        insertExpense(db, 1, 22_000, "Comida rápida", "2026-05-02")
        insertExpense(db, 1, 110_000, "Mercado mensual", "2026-05-10")

        // Transporte
        insertExpense(db, 2, 50_000, "Recarga tarjeta SITP", "2026-02-03")
        insertExpense(db, 2, 35_000, "Uber al aeropuerto", "2026-02-18")
        insertExpense(db, 2, 80_000, "Gasolina", "2026-03-05")
        insertExpense(db, 2, 45_000, "Revisión técnica", "2026-03-20")
        insertExpense(db, 2, 50_000, "Recarga tarjeta SITP", "2026-04-02")
        insertExpense(db, 2, 25_000, "Uber nocturno", "2026-04-15")
        insertExpense(db, 2, 85_000, "Gasolina", "2026-05-05")
        insertExpense(db, 2, 15_000, "Parqueadero", "2026-05-12")

        // Entretenimiento
        insertExpense(db, 3, 17_900, "Netflix mensual", "2026-02-01")
        insertExpense(db, 3, 30_000, "Entradas al cine", "2026-02-14")
        insertExpense(db, 3, 80_000, "Concierto", "2026-03-08")
        insertExpense(db, 3, 17_900, "Netflix mensual", "2026-03-01")
        insertExpense(db, 3, 50_000, "Videojuego", "2026-04-05")
        insertExpense(db, 3, 17_900, "Netflix mensual", "2026-04-01")
        insertExpense(db, 3, 17_900, "Netflix mensual", "2026-05-01")
        insertExpense(db, 3, 35_000, "Bolos con amigos", "2026-05-08")

        // Salud
        insertExpense(db, 4, 45_000, "Medicamentos", "2026-02-08")
        insertExpense(db, 4, 60_000, "Consulta médica", "2026-02-25")
        insertExpense(db, 4, 35_000, "Vitaminas", "2026-03-10")
        insertExpense(db, 4, 120_000, "Exámenes de laboratorio", "2026-04-05")
        insertExpense(db, 4, 180_000, "Odontólogo", "2026-04-22")
        insertExpense(db, 4, 28_000, "Medicamentos", "2026-05-07")

        // Hogar
        insertExpense(db, 5, 900_000, "Arriendo", "2026-02-01")
        insertExpense(db, 5, 180_000, "Servicios públicos", "2026-02-10")
        insertExpense(db, 5, 900_000, "Arriendo", "2026-03-01")
        insertExpense(db, 5, 165_000, "Servicios públicos", "2026-03-10")
        insertExpense(db, 5, 55_000, "Implementos de aseo", "2026-03-18")
        insertExpense(db, 5, 900_000, "Arriendo", "2026-04-01")
        insertExpense(db, 5, 195_000, "Servicios públicos", "2026-04-10")
        insertExpense(db, 5, 900_000, "Arriendo", "2026-05-01")
        insertExpense(db, 5, 80_000, "Plomero", "2026-05-08")

        // Ropa
        insertExpense(db, 6, 75_000, "Camisetas básicas", "2026-02-15")
        insertExpense(db, 6, 180_000, "Zapatos deportivos", "2026-03-08")
        insertExpense(db, 6, 95_000, "Pantalón jeans", "2026-04-12")
        insertExpense(db, 6, 45_000, "Ropa interior", "2026-05-03")
        insertExpense(db, 6, 150_000, "Chaqueta", "2026-05-14")

        // Café
        insertExpense(db, 7, 7_500, "Café de la mañana", "2026-02-04")
        insertExpense(db, 7, 25_000, "Café con amigos", "2026-02-18")
        insertExpense(db, 7, 6_500, "Café en el trabajo", "2026-03-06")
        insertExpense(db, 7, 18_000, "Té y pasteles", "2026-03-21")
        insertExpense(db, 7, 7_000, "Café mañanero", "2026-04-09")
        insertExpense(db, 7, 12_000, "Café frío", "2026-04-24")
        insertExpense(db, 7, 7_500, "Café", "2026-05-06")
    }
}

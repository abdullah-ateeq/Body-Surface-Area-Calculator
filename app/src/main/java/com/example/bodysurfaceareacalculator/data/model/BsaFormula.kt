package com.example.bodysurfaceareacalculator.data.model

enum class BsaFormula(
    val displayName: String,
    val formulaExpression: String,
    val description: String,
    val citation: String
) {
    MOSTELLER(
        displayName = "Mosteller",
        formulaExpression = "BSA (m²) = √((Height(cm) × Weight(kg)) / 3600)",
        description = "A widely utilized and validated formula for estimating body surface area, known for its clinical simplicity and accuracy.",
        citation = "Mosteller RD. Simplified calculation of body-surface area. N Engl J Med. 1987;317(17):1098."
    ),
    DU_BOIS(
        displayName = "Du Bois",
        formulaExpression = "BSA (m²) = 0.007184 × Height(cm)^0.725 × Weight(kg)^0.425",
        description = "One of the earliest established clinical formulas for estimating body surface area, introduced by Du Bois and Du Bois in 1916.",
        citation = "Du Bois D, Du Bois EF. A formula to estimate the approximate surface area if height and weight be known. Arch Intern Med. 1916;17(6):863–871."
    )
}

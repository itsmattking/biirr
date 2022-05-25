package me.mking.biirr.domain.entities

data class Beer(
    val id: Int,
    val name: String,
    val tagLine: String,
    val imageUrl: String?,
    val description: String,
    val alcoholByVolume: Double,
    val ibu: Ibu = Ibu.Unknown
) {
    sealed class Ibu(open val value: Double) {
        data class Smooth(override val value: Double) : Ibu(value)
        data class Bitter(override val value: Double) : Ibu(value)
        data class HipsterPlus(override val value: Double) : Ibu(value)
        object Unknown : Ibu(value = 0.0)
    }

    companion object {
        fun ibuFrom(value: Double) = when {
            value <= 20.0 -> Ibu.Smooth(value)
            value <= 50.0 -> Ibu.Bitter(value)
            value <= 100.0 -> Ibu.HipsterPlus(value)
            else -> Ibu.Unknown
        }
    }
}

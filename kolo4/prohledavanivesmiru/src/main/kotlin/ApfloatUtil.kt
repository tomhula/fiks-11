import org.apfloat.Apfloat
import org.apfloat.ApfloatMath
import org.apfloat.Apint
import java.math.RoundingMode

const val PRECISION = 200L

operator fun Apfloat.plus(other: Apfloat): Apfloat = this.add(other)
operator fun Apfloat.minus(other: Apfloat): Apfloat = this.subtract(other)
operator fun Apfloat.times(other: Apfloat): Apfloat = this.multiply(other)
operator fun Apfloat.div(other: Apfloat): Apfloat = this.ensurePrecision().divide(other.ensurePrecision())

operator fun Apfloat.plus(other: Float): Apfloat = this + other.apf
operator fun Apfloat.minus(other: Float): Apfloat = this - other.apf
operator fun Apfloat.times(other: Float): Apfloat = this * other.apf
operator fun Apfloat.div(other: Float): Apfloat = this / other.apf

operator fun Float.plus(other: Apfloat): Apfloat = this.apf + other
operator fun Float.minus(other: Apfloat): Apfloat = this.apf - other
operator fun Float.times(other: Apfloat): Apfloat = this.apf * other
operator fun Float.div(other: Apfloat): Apfloat = this.apf / other

operator fun Apint.plus(other: Apint): Apint = this.add(other)
operator fun Apint.minus(other: Apint): Apint = this.subtract(other)
operator fun Apint.times(other: Apint): Apint = this.multiply(other)
operator fun Apint.div(other: Apint): Apint = this.divide(other)

operator fun Apint.plus(other: Long): Apint = this.add(Apint(other))
operator fun Apint.minus(other: Long): Apint = this.subtract(Apint(other))
operator fun Apint.times(other: Long): Apint = this.multiply(Apint(other))
operator fun Apint.div(other: Long): Apint = this.divide(Apint(other))



operator fun Long.plus(other: Apint): Apint = Apint(this).add(other)
operator fun Long.minus(other: Apint): Apint = Apint(this).subtract(other)
operator fun Long.times(other: Apint): Apint = Apint(this).multiply(other)
operator fun Long.div(other: Apint): Apint = Apint(this).divide(other)

operator fun Apfloat.plus(other: Apint): Apfloat = this.add(other.ensurePrecision())
operator fun Apfloat.minus(other: Apint): Apfloat = this.subtract(other.ensurePrecision())
operator fun Apfloat.times(other: Apint): Apfloat = this.multiply(other.ensurePrecision())
operator fun Apfloat.div(other: Apint): Apfloat = this.divide(other.ensurePrecision())

operator fun Long.plus(other: Apfloat): Apfloat = Apint(this) + other
operator fun Long.minus(other: Apfloat): Apfloat = Apint(this) - other
operator fun Long.times(other: Apfloat): Apfloat = Apint(this) * other
operator fun Long.div(other: Apfloat): Apfloat = Apint(this) / other

operator fun Int.plus(other: Apfloat): Apfloat = this.toLong() + other
operator fun Int.minus(other: Apfloat): Apfloat = this.toLong() - other
operator fun Int.times(other: Apfloat): Apfloat = this.toLong() * other
operator fun Int.div(other: Apfloat): Apfloat = this.toLong() / other

operator fun Int.plus(other: Apint): Apint = this.toLong() + other
operator fun Int.minus(other: Apint): Apint = this.toLong() - other
operator fun Int.times(other: Apint): Apint = this.toLong() * other
operator fun Int.div(other: Apint): Apint = this.toLong() / other

operator fun Apfloat.plus(other: Long): Apfloat = this.add(Apint(other))
operator fun Apfloat.minus(other: Long): Apfloat = this.subtract(Apint(other))
operator fun Apfloat.times(other: Long): Apfloat = this.multiply(Apint(other))
operator fun Apfloat.div(other: Long): Apfloat = this.divide(Apint(other))

operator fun Apfloat.unaryMinus(): Apfloat = this.negate()
operator fun Apint.unaryMinus(): Apint = this.negate()

fun atan2(y: Apfloat, x: Apfloat): Apfloat = ApfloatMath.atan2(y.ensurePrecision(), x.ensurePrecision())
fun cos(x: Apfloat): Apfloat = ApfloatMath.cos(x.ensurePrecision())
fun sin(x: Apfloat): Apfloat = ApfloatMath.sin(x.ensurePrecision())
fun acos(x: Apfloat): Apfloat = ApfloatMath.acos(x.ensurePrecision())
fun sqrt(x: Apfloat): Apfloat = ApfloatMath.sqrt(x.ensurePrecision())
fun ceil(x: Apfloat): Apint = ApfloatMath.ceil(x)
fun floor(x: Apfloat): Apint = ApfloatMath.floor(x)
fun round(x: Apfloat): Apint = ApfloatMath.roundToInteger(x, RoundingMode.HALF_UP)
fun abs(x: Apfloat): Apfloat = ApfloatMath.abs(x)

fun Apfloat.ensurePrecision(): Apfloat = this.precision(PRECISION)

operator fun Apfloat.rem(other: Apfloat): Apfloat = this.mod(other)
operator fun Apfloat.rem(other: Long): Apfloat = this.mod(Apint(other))

val Float.apf: Apfloat
    get() = Apfloat(this, PRECISION)

val Double.apf: Apfloat
    get() = Apfloat(this, PRECISION)

val Int.apf: Apfloat
    get() = Apfloat(this.toLong(), PRECISION)

val Long.apf: Apfloat
    get() = Apfloat(this, PRECISION)

val Long.api: Apint
    get() = Apint(this)

val Int.api: Apint
    get() = Apint(this.toLong())

val API_ONE: Apint = Apint.ONE
val API_ZERO: Apint = Apint.ZERO
val APF_PI: Apfloat = ApfloatMath.pi(PRECISION)

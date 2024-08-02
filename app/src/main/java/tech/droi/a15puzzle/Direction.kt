package tech.droi.a15puzzle

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
    companion object {
        fun fromInt(value: Int) = entries.toTypedArray().first { it.ordinal == value }
    }
}
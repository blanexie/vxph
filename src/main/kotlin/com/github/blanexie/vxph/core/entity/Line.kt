package com.github.blanexie.vxph.core.entity

class Line(
    val outPoint: String,
    val end: String,
    val endInPoint: String
) {


    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false;
        }
        return if (other is Line) {
            outPoint == other.outPoint && end == other.end && endInPoint == other.endInPoint;
        } else false
    }

    override fun hashCode(): Int {
        return "$outPoint-$end-$endInPoint".hashCode()
    }
}
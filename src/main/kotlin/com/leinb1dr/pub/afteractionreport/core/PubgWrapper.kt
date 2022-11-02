package com.leinb1dr.pub.afteractionreport.core

data class PubgWrapper(val data:Array<PubgData>?=null, val included:Array<PubgData>?=null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PubgWrapper

        if (!data.contentEquals(other.data)) return false
        if (included != null) {
            if (other.included == null) return false
            if (!included.contentEquals(other.included)) return false
        } else if (other.included != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + (included?.contentHashCode() ?: 0)
        return result
    }


}

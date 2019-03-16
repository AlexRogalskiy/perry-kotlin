package com.beust.perry

object Urls {
    const val SUMMARIES = "/summaries"
    fun summaries(n: Any? = null) = f(SUMMARIES, n)

    const val CYCLES = "/cycles"
    fun cycles(n: Any? = null)  = f(CYCLES, n)

    private fun f(constant: String, n: Any? = null)  = if (n != null) "$constant/$n" else "/$constant"
}
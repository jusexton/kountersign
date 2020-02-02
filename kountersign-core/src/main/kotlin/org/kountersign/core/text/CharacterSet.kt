package org.kountersign.core.text

/**
 * All lower case english letters.
 *
 * abcdefghijklmnopqrstuvwxyz
 */
@JvmField
val LOWER_CASE: Set<Char> = "abcdefghijklmnopqrstuvwxyz".toSet()


/**
 * All upper case english letters.
 *
 * ABCDEFGHIJKLMNOPQRSTUVWXYZ
 */
@JvmField
val UPPER_CASE: Set<Char> = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toSet()


/**
 * All english letters.
 *
 * abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
 */
@JvmField
val LETTERS = LOWER_CASE union UPPER_CASE


/**
 * All digits
 *
 * 0123456789
 */
@JvmField
val DIGITS: Set<Char> = "0123456789".toSet()

/**
 * All symbols
 *
 * <>,./?:;"'[]{}_-+=`~\
 */
@JvmField
val PUNCTUATION: Set<Char> = "<>,./?:;\"\'[]{}_-+=`~\\".toSet()

/**
 * Whitespace value
 */
@JvmField
val WHITESPACE: Set<Char> = setOf(' ')

/**
 * All type able values
 *
 * abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<>,./?:;"'[]{}_-+=`~\
 */
@JvmField
val ALL: Set<Char> = LETTERS union DIGITS union PUNCTUATION union WHITESPACE

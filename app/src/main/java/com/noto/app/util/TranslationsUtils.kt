package com.noto.app.util

import android.content.Context
import com.noto.app.R
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.Translation
import com.noto.app.domain.model.Translation.Translator
import java.text.Collator

val Translation.Companion.Default
    get() = listOf(
        Translation(
            language = Language.Arabic,
            iconId = R.drawable.ic_saudi_arabia,
            translators = listOf(
                Translator(R.string.arabic_proofreader, R.string.arabic_proofreader_url),
            ),
        ),
        Translation(
            language = Language.Turkish,
            iconId = R.drawable.ic_turkey,
            translators = listOf(
                Translator(R.string.turkish_translator, R.string.turkish_translator_url),
                Translator(R.string.turkish_proofreader, R.string.turkish_proofreader_url),
            ),
        ),
        Translation(
            language = Language.SimplifiedChinese,
            iconId = R.drawable.ic_china,
            translators = listOf(
                Translator(R.string.simplified_chinese_translator, R.string.simplified_chinese_translator_url),
            ),
        ),
        Translation(
            language = Language.Lithuanian,
            iconId = R.drawable.ic_lithuania,
            translators = listOf(
                Translator(R.string.lithuanian_translator, R.string.lithuanian_translator_url),
            ),
        ),
        Translation(
            language = Language.Czech,
            iconId = R.drawable.ic_czech,
            translators = listOf(
                Translator(R.string.czech_translator, R.string.czech_translator_url),
            ),
        ),
        Translation(
            language = Language.Italian,
            iconId = R.drawable.ic_italy,
            translators = listOf(
                Translator(R.string.italian_translator, R.string.italian_translator_url),
                Translator(R.string.italian_translator2, R.string.italian_translator2_url),
            ),
        ),
        Translation(
            language = Language.French,
            iconId = R.drawable.ic_france,
            translators = listOf(
                Translator(R.string.french_translator, R.string.french_translator_url),
                Translator(R.string.french_translator2, R.string.french_translator2_url),
            ),
        ),
        Translation(
            language = Language.Spanish,
            iconId = R.drawable.ic_spain,
            translators = listOf(
                Translator(R.string.spanish_translator, R.string.spanish_translator_url),
            ),
        ),
        Translation(
            language = Language.German,
            iconId = R.drawable.ic_germany,
            translators = listOf(
                Translator(R.string.german_translator, null),
            ),
        ),
        Translation(
            language = Language.Portuguese,
            iconId = R.drawable.ic_portugal,
            translators = listOf(
                Translator(R.string.portuguese_translator, null),
            ),
        ),
        Translation(
            language = Language.Russian,
            iconId = R.drawable.ic_russia,
            translators = listOf(
                Translator(R.string.russian_translator, R.string.russian_translator_url),
            ),
        ),
        Translation(
            language = Language.Korean,
            iconId = R.drawable.ic_korea,
            translators = listOf(
                Translator(R.string.korean_translator, R.string.korean_translator_url),
            ),
        ),
    )

fun Translation.Companion.Comparator(context: Context): Comparator<Translation> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    return compareBy(collator) {
        val localizedContext = context.localize(it.language)
        localizedContext.stringResource(it.language.toStringResourceId())
    }
}

fun Translator.Companion.Comparator(context: Context): Comparator<Translator> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    return compareBy(collator) { context.stringResource(it.nameId) }
}
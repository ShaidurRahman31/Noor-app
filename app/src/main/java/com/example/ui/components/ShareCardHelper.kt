package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.model.DuaItem

object ShareCardHelper {

    fun formatDuaForShare(dua: DuaItem): String {
        return buildString {
            append("✨ নূর — ইসলামিক দোয়া ও আজকার ✨\n\n")
            append("📌 ${dua.titleBn}\n")
            append("────────────────────────\n")
            append("📖 আরবি:\n${dua.arabicText}\n\n")
            append("🗣️ উচ্চারণ:\n${dua.transliterationBn}\n\n")
            append("🤲 বাংলা অর্থ:\n${dua.meaningBn}\n\n")
            append("📚 সূত্র ও রেফারেন্স:\n${dua.reference}\n")
            if (dua.referenceEdition.isNotEmpty()) {
                append("(${dua.referenceEdition})\n")
            }
            append("🔍 মান: ${dua.authenticityGrade}\n")
            if (dua.contextOrOccasion.isNotEmpty()) {
                append("💡 ফজিলত/আমল: ${dua.contextOrOccasion}\n")
            }
            append("\n📲 Noor — Islamic Duas & Adhkar অ্যাপ থেকে সংগৃহীত।")
        }
    }

    fun copyToClipboard(context: Context, dua: DuaItem) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Noor Dua", formatDuaForShare(dua))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "দোয়াটি ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
    }

    fun shareDua(context: Context, dua: DuaItem) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, formatDuaForShare(dua))
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "দোয়া শেয়ার করুন"))
    }
}

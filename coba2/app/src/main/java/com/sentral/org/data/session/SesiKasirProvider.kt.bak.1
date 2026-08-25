package com.sentral.org.data.session

import com.sentral.org.data.dao.KasirDao
import com.sentral.org.data.dao.ShiftDao
import kotlinx.coroutines.flow.first

data class SesiKasir(
    val kasirId: Long,
    val namaKasir: String,
    val shiftId: Long,
)

interface SesiKasirProvider {
    /** Null berarti belum ada kasir aktif / shift terbuka -> UI wajib mengarahkan buka shift. */
    suspend fun sesiAktif(): SesiKasir?
}

/** SEMENTARA: kasir aktif pertama + shift terbukanya. Diganti begitu fitur login PIN jadi. */
class DevSesiKasirProvider(
    private val kasirs: KasirDao,
    private val shifts: ShiftDao,
) : SesiKasirProvider {
    override suspend fun sesiAktif(): SesiKasir? {
        val kasir = kasirs.observeAktif().first().firstOrNull() ?: return null
        val shift = shifts.getOpenForKasir(kasir.id) ?: return null
        return SesiKasir(kasir.id, kasir.nama, shift.id)
    }
}
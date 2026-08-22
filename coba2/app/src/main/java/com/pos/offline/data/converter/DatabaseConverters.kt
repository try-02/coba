package com.pos.offline.data.converter

import androidx.room3.ColumnTypeConverter
import com.pos.offline.data.model.*

object DatabaseConverters {
    @ColumnTypeConverter fun statusKeranjangToString(v: StatusKeranjang) = v.name
    @ColumnTypeConverter fun stringToStatusKeranjang(v: String) = enumValueOf<StatusKeranjang>(v)
    @ColumnTypeConverter fun statusTransaksiToString(v: StatusTransaksi) = v.name
    @ColumnTypeConverter fun stringToStatusTransaksi(v: String) = enumValueOf<StatusTransaksi>(v)
    @ColumnTypeConverter fun statusShiftToString(v: StatusShift) = v.name
    @ColumnTypeConverter fun stringToStatusShift(v: String) = enumValueOf<StatusShift>(v)
    @ColumnTypeConverter fun metodePembayaranToString(v: MetodePembayaran) = v.name
    @ColumnTypeConverter fun stringToMetodePembayaran(v: String) = enumValueOf<MetodePembayaran>(v)
    @ColumnTypeConverter fun jenisDiskonToString(v: JenisDiskon) = v.name
    @ColumnTypeConverter fun stringToJenisDiskon(v: String) = enumValueOf<JenisDiskon>(v)
    @ColumnTypeConverter fun tujuanStokToString(v: TujuanStokPengembalian) = v.name
    @ColumnTypeConverter fun stringToTujuanStok(v: String) = enumValueOf<TujuanStokPengembalian>(v)
    @ColumnTypeConverter fun jenisPersediaanToString(v: JenisPergerakanPersediaan) = v.name
    @ColumnTypeConverter fun stringToJenisPersediaan(v: String) = enumValueOf<JenisPergerakanPersediaan>(v)
    @ColumnTypeConverter fun jenisKasToString(v: JenisPergerakanKas) = v.name
    @ColumnTypeConverter fun stringToJenisKas(v: String) = enumValueOf<JenisPergerakanKas>(v)
}

// AppModule.kt (Buat file baru di root package atau package di)
package com.pos.offline.di

import com.pos.offline.data.PosDatabase
import com.pos.offline.data.PosDatabaseFactory
import com.pos.offline.data.repository.*
import com.pos.offline.data.repository.impl.*
import com.pos.offline.data.service.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // 1. Core & Database (Menggunakan androidContext agar tidak terjadi memory leak)[span_5](start_span)[span_5](end_span)[span_6](start_span)[span_6](end_span)[span_7](start_span)[span_7](end_span)
    single { PosDatabaseFactory.create(androidContext()) }[span_8](start_span)[span_8](end_span)
    single<TransactionRunner> { RoomTransactionRunner(get()) } 
    
    // 2. DAOs
    single { get<PosDatabase>().produkDao() }
    single { get<PosDatabase>().persediaanDao() }
    single { get<PosDatabase>().pergerakanPersediaanDao() }
    single { get<PosDatabase>().kasirDao() }
    single { get<PosDatabase>().shiftDao() }
    single { get<PosDatabase>().pergerakanKasDao() }
    single { get<PosDatabase>().keranjangDao() }
    single { get<PosDatabase>().itemKeranjangDao() }
    single { get<PosDatabase>().transaksiDao() }
    single { get<PosDatabase>().itemTransaksiDao() }
    single { get<PosDatabase>().pembayaranDao() }
    single { get<PosDatabase>().returDao() }
    single { get<PosDatabase>().printerDao() }
    single { get<PosDatabase>().profilTokoDao() }

    // 3. Repositories (Binding interface ke implementasi)[span_9](start_span)[span_9](end_span)
    single<ProdukRepository> { OfflineProdukRepository(get()) }
    single<PersediaanRepository> { OfflinePersediaanRepository(get(), get()) }
    single<KasirRepository> { OfflineKasirRepository(get()) }
    single<ShiftRepository> { OfflineShiftRepository(get()) }
    single<CartRepository> { OfflineCartRepository(get(), get()) }
    single<TransaksiRepository> { OfflineTransaksiRepository(get(), get(), get()) }
    single<ReturRepository> { OfflineReturRepository(get()) }
    single<PrinterRepository> { OfflinePrinterRepository(get()) }
    single<ProfilTokoRepository> { OfflineProfilTokoRepository(get()) }

    // 4. Domain Services (Gunakan factory agar mendapat instance segar jika dibutuhkan, atau single jika stateless)[span_10](start_span)[span_10](end_span)
    factory { InventoryMutationService(get(), get()) }
    factory { PersediaanService(get(), get(), get(), get()) }
    // Sesuaikan parameter CartService dengan abstraksi TransactionRunner dari perbaikan sebelumnya
    factory { CartService(get(), get(), get(), get()) } 
    factory { ShiftService(get(), get(), get(), get()) }
    factory { CheckoutService(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { ReturService(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { VoidService(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    // 5. ViewModels[span_11](start_span)[span_11](end_span)
    viewModel { CheckoutViewModel(get()) }
}

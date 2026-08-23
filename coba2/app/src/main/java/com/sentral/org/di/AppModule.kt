package com.sentral.org.di

import com.sentral.org.data.PosDatabase
import com.sentral.org.data.PosDatabaseFactory
import com.sentral.org.data.repository.*
import com.sentral.org.data.repository.impl.*
import com.sentral.org.data.service.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // 1. Core & Database
    single {
        PosDatabaseFactory.create(androidContext())
    }

    single<PosWriteService> {
        RoomTransactionRunner(get())
    }

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

    // 3. Repositories
    single<ProdukRepository> {
        OfflineProdukRepository(get())
    }

    single<PersediaanRepository> {
        OfflinePersediaanRepository(get(), get())
    }

    single<KasirRepository> {
        OfflineKasirRepository(get())
    }

    single<ShiftRepository> {
        OfflineShiftRepository(get())
    }

    single<CartRepository> {
        OfflineCartRepository(get(), get())
    }

    single<TransaksiRepository> {
        OfflineTransaksiRepository(get(), get(), get())
    }

    single<ReturRepository> {
        OfflineReturRepository(get())
    }

    single<PrinterRepository> {
        OfflinePrinterRepository(get())
    }

    single<ProfilTokoRepository> {
        OfflineProfilTokoRepository(get())
    }

    // 4. Domain Services
    factory {
        InventoryMutationService(get(), get())
    }

    factory {
        PersediaanService(get(), get(), get(), get())
    }

    factory {
        CartService(get(), get(), get(), get())
    }

    factory {
        ShiftService(get(), get(), get(), get())
    }

    factory {
        CheckoutService(
            get(), get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(), get()
        )
    }

    factory {
        ReturService(
            get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get()
        )
    }

    factory {
        VoidService(
            get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get()
        )
    }

    // 5. ViewModels
    viewModel {
        CheckoutViewModel(get())
    }
}
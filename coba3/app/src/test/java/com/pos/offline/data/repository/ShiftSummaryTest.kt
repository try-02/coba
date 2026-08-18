package com.pos.offline.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftSummaryTest {
    @Test
    fun grossProfit_hariNormal_tanpaRetur() {
        val s =
            ShiftSummary(
                startingCash = 100_000L, // Ditambah L di belakang angka
                cashRevenue = 239_000L,
                qrisRevenue = 0L,
                totalCost = 160_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
            )
        assertEquals(79_000L, s.grossProfit)
        assertEquals(339_000L, s.expectedCashInDrawer)
    }

    @Test
    fun grossProfit_harusTurunSetelahRetur_bukanNaik() {
        val sebelumRetur =
            ShiftSummary(
                startingCash = 100_000L,
                cashRevenue = 239_000L,
                qrisRevenue = 0L,
                totalCost = 160_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
            )
        val sesudahRetur =
            ShiftSummary(
                startingCash = 100_000L,
                cashRevenue = 239_000L,
                qrisRevenue = 0L,
                totalCost = 160_000L,
                restockedReturnsCost = 20_000L,
                cashRefunds = 50_000L,
            )
        assertTrue(
            "Laba Kotor harus TURUN setelah retur, bukan naik",
            sesudahRetur.grossProfit < sebelumRetur.grossProfit,
        )
        assertEquals(49_000L, sesudahRetur.grossProfit)
    }

    @Test
    fun warrantyExchangeCost_dikreditKembali_grossProfitTurunSesuaiBiayaGaransi() {
        val tanpaGaransi =
            ShiftSummary(
                startingCash = 0L,
                cashRevenue = 100_000L,
                qrisRevenue = 0L,
                totalCost = 70_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
            )
        val denganGaransiDeltaNol =
            ShiftSummary(
                startingCash = 0L,
                cashRevenue = 100_000L,
                qrisRevenue = 0L,
                totalCost = 70_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
                warrantyExchangeCost = 20_000L,
            )
        assertEquals(30_000L, tanpaGaransi.grossProfit)
        assertEquals(10_000L, denganGaransiDeltaNol.grossProfit)
    }

    @Test
    fun expectedCashInDrawer_tidakTerpengaruhRefundQris() {
        val s =
            ShiftSummary(
                startingCash = 100_000L,
                cashRevenue = 100_000L,
                qrisRevenue = 50_000L,
                totalCost = 0L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
                qrisRefunds = 30_000L,
            )
        assertEquals(200_000L, s.expectedCashInDrawer)
    }

    @Test
    fun grossProfit_turunKarenaRefundQris_meskiLaciTidakBerubah() {
        val s =
            ShiftSummary(
                startingCash = 0L,
                cashRevenue = 100_000L,
                qrisRevenue = 0L,
                totalCost = 70_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
                qrisRefunds = 30_000L,
            )
        assertEquals(0L, s.grossProfit)
        assertEquals(100_000L, s.expectedCashInDrawer)
    }

    @Test
    fun expectedCashInDrawer_berkurangKarenaQrisCashChangeOut() {
        val s =
            ShiftSummary(
                startingCash = 100_000L,
                cashRevenue = 0L,
                qrisRevenue = 150_000L,
                totalCost = 0L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
                qrisCashChangeOut = 50_000L,
            )
        assertEquals(50_000L, s.expectedCashInDrawer)
    }

    @Test
    fun qrisRevenue_tidakDoubleCounting_saatAdaKembalianTunai() {
        val s =
            ShiftSummary(
                startingCash = 0L,
                cashRevenue = 0L,
                qrisRevenue = 150_000L,
                totalCost = 120_000L,
                restockedReturnsCost = 0L,
                cashRefunds = 0L,
                qrisCashChangeOut = 50_000L,
            )
        assertEquals(30_000L, s.grossProfit)
        assertEquals(-50_000L, s.expectedCashInDrawer)
    }
}

PRAGMA foreign_keys = ON;

CREATE TABLE produk (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nama TEXT NOT NULL,
  sku TEXT NOT NULL,
  barcode TEXT,
  harga INTEGER NOT NULL,
  harga_modal INTEGER NOT NULL,
  kategori TEXT NOT NULL,
  aktif INTEGER NOT NULL,
  dibuat_pada INTEGER NOT NULL,
  diperbarui_pada INTEGER NOT NULL
);
CREATE UNIQUE INDEX unik_produk_sku ON produk(sku);
CREATE UNIQUE INDEX unik_produk_barcode ON produk(barcode);
CREATE INDEX indeks_produk_nama ON produk(nama);
CREATE INDEX indeks_produk_kategori ON produk(kategori);

CREATE TABLE persediaan (
  produk_id INTEGER PRIMARY KEY NOT NULL,
  jumlah INTEGER NOT NULL,
  jumlah_rusak INTEGER NOT NULL,
  diperbarui_pada INTEGER NOT NULL,
  FOREIGN KEY(produk_id) REFERENCES produk(id) ON DELETE RESTRICT ON UPDATE NO ACTION
);

CREATE TABLE kasir (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nama TEXT NOT NULL,
  pin_hash TEXT,
  aktif INTEGER NOT NULL,
  dibuat_pada INTEGER NOT NULL
);
CREATE INDEX indeks_kasir_nama ON kasir(nama);

CREATE TABLE shift (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  kasir_id INTEGER NOT NULL,
  nama_kasir TEXT NOT NULL,
  status TEXT NOT NULL,
  kas_awal INTEGER NOT NULL,
  dimulai_pada INTEGER NOT NULL,
  kas_diharapkan INTEGER,
  kas_aktual INTEGER,
  selisih_kas INTEGER,
  ditutup_pada INTEGER,
  catatan TEXT NOT NULL,
  FOREIGN KEY(kasir_id) REFERENCES kasir(id) ON DELETE RESTRICT ON UPDATE NO ACTION
);
CREATE INDEX indeks_shift_kasir ON shift(kasir_id);
CREATE INDEX indeks_shift_status ON shift(status);
CREATE INDEX indeks_shift_dimulai ON shift(dimulai_pada);
CREATE INDEX indeks_shift_ditutup ON shift(ditutup_pada);

CREATE TABLE keranjang (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nama TEXT NOT NULL,
  status TEXT NOT NULL,
  kasir_id INTEGER NOT NULL,
  nama_kasir TEXT NOT NULL,
  dibuat_pada INTEGER NOT NULL,
  diperbarui_pada INTEGER NOT NULL,
  ditahan_pada INTEGER,
  diselesaikan_pada INTEGER,
  dibatalkan_pada INTEGER,
  FOREIGN KEY(kasir_id) REFERENCES kasir(id) ON DELETE RESTRICT ON UPDATE NO ACTION
);
CREATE INDEX indeks_keranjang_status ON keranjang(status);
CREATE INDEX indeks_keranjang_kasir ON keranjang(kasir_id);
CREATE INDEX indeks_keranjang_diperbarui ON keranjang(diperbarui_pada);

CREATE TABLE item_keranjang (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  keranjang_id INTEGER NOT NULL,
  produk_id INTEGER NOT NULL,
  nama_produk TEXT NOT NULL,
  harga_satuan INTEGER NOT NULL,
  jumlah INTEGER NOT NULL,
  ditambahkan_pada INTEGER NOT NULL,
  diperbarui_pada INTEGER NOT NULL,
  FOREIGN KEY(keranjang_id) REFERENCES keranjang(id) ON DELETE CASCADE,
  FOREIGN KEY(produk_id) REFERENCES produk(id) ON DELETE RESTRICT
);
CREATE INDEX indeks_item_keranjang_keranjang ON item_keranjang(keranjang_id);
CREATE UNIQUE INDEX unik_item_keranjang_produk ON item_keranjang(keranjang_id, produk_id);

CREATE TABLE transaksi (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nomor_transaksi TEXT NOT NULL,
  kasir_id INTEGER NOT NULL,
  nama_kasir TEXT NOT NULL,
  shift_id INTEGER NOT NULL,
  dibuat_pada INTEGER NOT NULL,
  subtotal INTEGER NOT NULL,
  diskon INTEGER NOT NULL,
  pajak INTEGER NOT NULL,
  total INTEGER NOT NULL,
  jenis_diskon TEXT NOT NULL,
  nilai_diskon INTEGER NOT NULL,
  status TEXT NOT NULL,
  dibatalkan_pada INTEGER,
  alasan_pembatalan TEXT,
  adalah_tukar_garansi INTEGER NOT NULL,
  FOREIGN KEY(kasir_id) REFERENCES kasir(id) ON DELETE RESTRICT,
  FOREIGN KEY(shift_id) REFERENCES shift(id) ON DELETE RESTRICT
);
CREATE UNIQUE INDEX unik_transaksi_nomor ON transaksi(nomor_transaksi);
CREATE INDEX indeks_transaksi_kasir ON transaksi(kasir_id);
CREATE INDEX indeks_transaksi_shift ON transaksi(shift_id);
CREATE INDEX indeks_transaksi_waktu ON transaksi(dibuat_pada);
CREATE INDEX indeks_transaksi_status ON transaksi(status);

CREATE TABLE item_transaksi (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  transaksi_id INTEGER NOT NULL,
  produk_id INTEGER,
  nama_produk TEXT NOT NULL,
  harga_satuan INTEGER NOT NULL,
  jumlah INTEGER NOT NULL,
  total_baris INTEGER NOT NULL,
  diskon_item INTEGER NOT NULL,
  harga_modal INTEGER NOT NULL,
  FOREIGN KEY(transaksi_id) REFERENCES transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(produk_id) REFERENCES produk(id) ON DELETE SET NULL
);
CREATE INDEX indeks_item_transaksi_transaksi ON item_transaksi(transaksi_id);
CREATE INDEX indeks_item_transaksi_produk ON item_transaksi(produk_id);

CREATE TABLE pembayaran (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  transaksi_id INTEGER NOT NULL,
  metode TEXT NOT NULL,
  jumlah INTEGER NOT NULL,
  diterima INTEGER,
  kembalian INTEGER,
  referensi TEXT,
  dibuat_pada INTEGER NOT NULL,
  FOREIGN KEY(transaksi_id) REFERENCES transaksi(id) ON DELETE RESTRICT
);
CREATE INDEX indeks_pembayaran_transaksi ON pembayaran(transaksi_id);

CREATE TABLE pengembalian (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  transaksi_id INTEGER NOT NULL,
  transaksi_pengganti_id INTEGER,
  dikembalikan_pada INTEGER NOT NULL,
  kasir_id INTEGER,
  shift_id INTEGER,
  nama_kasir TEXT NOT NULL,
  jumlah_pengembalian INTEGER NOT NULL,
  metode_pengembalian TEXT NOT NULL,
  catatan TEXT NOT NULL,
  adalah_tukar_garansi INTEGER NOT NULL,
  FOREIGN KEY(transaksi_id) REFERENCES transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(transaksi_pengganti_id) REFERENCES transaksi(id) ON DELETE SET NULL,
  FOREIGN KEY(kasir_id) REFERENCES kasir(id) ON DELETE SET NULL,
  FOREIGN KEY(shift_id) REFERENCES shift(id) ON DELETE SET NULL
);
CREATE INDEX indeks_pengembalian_transaksi ON pengembalian(transaksi_id);
CREATE INDEX indeks_pengembalian_pengganti ON pengembalian(transaksi_pengganti_id);
CREATE INDEX indeks_pengembalian_kasir ON pengembalian(kasir_id);
CREATE INDEX indeks_pengembalian_shift ON pengembalian(shift_id);
CREATE INDEX indeks_pengembalian_waktu ON pengembalian(dikembalikan_pada);

CREATE TABLE item_pengembalian (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  pengembalian_id INTEGER NOT NULL,
  item_transaksi_id INTEGER NOT NULL,
  produk_id INTEGER,
  nama_produk TEXT NOT NULL,
  harga_satuan INTEGER NOT NULL,
  jumlah_dikembalikan INTEGER NOT NULL,
  jumlah_refund INTEGER NOT NULL,
  tujuan_stok TEXT NOT NULL,
  FOREIGN KEY(pengembalian_id) REFERENCES pengembalian(id) ON DELETE RESTRICT,
  FOREIGN KEY(item_transaksi_id) REFERENCES item_transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(produk_id) REFERENCES produk(id) ON DELETE SET NULL
);
CREATE INDEX indeks_item_pengembalian_pengembalian ON item_pengembalian(pengembalian_id);
CREATE INDEX indeks_item_pengembalian_item_transaksi ON item_pengembalian(item_transaksi_id);
CREATE INDEX indeks_item_pengembalian_produk ON item_pengembalian(produk_id);
CREATE UNIQUE INDEX unik_item_pengembalian ON item_pengembalian(pengembalian_id, item_transaksi_id);

CREATE TABLE pergerakan_persediaan (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  produk_id INTEGER NOT NULL,
  jenis TEXT NOT NULL,
  perubahan_jumlah INTEGER NOT NULL,
  perubahan_jumlah_rusak INTEGER NOT NULL,
  saldo_jumlah_sebelum INTEGER NOT NULL,
  saldo_jumlah_setelah INTEGER NOT NULL,
  saldo_rusak_sebelum INTEGER NOT NULL,
  saldo_rusak_setelah INTEGER NOT NULL,
  transaksi_id INTEGER,
  item_transaksi_id INTEGER,
  pengembalian_id INTEGER,
  item_pengembalian_id INTEGER,
  shift_id INTEGER,
  keterangan TEXT NOT NULL,
  dibuat_pada INTEGER NOT NULL,
  FOREIGN KEY(produk_id) REFERENCES produk(id) ON DELETE RESTRICT,
  FOREIGN KEY(transaksi_id) REFERENCES transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(item_transaksi_id) REFERENCES item_transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(pengembalian_id) REFERENCES pengembalian(id) ON DELETE RESTRICT,
  FOREIGN KEY(item_pengembalian_id) REFERENCES item_pengembalian(id) ON DELETE RESTRICT,
  FOREIGN KEY(shift_id) REFERENCES shift(id) ON DELETE RESTRICT
);
CREATE INDEX indeks_pergerakan_produk_waktu ON pergerakan_persediaan(produk_id, dibuat_pada);
CREATE INDEX indeks_pergerakan_transaksi ON pergerakan_persediaan(transaksi_id);
CREATE INDEX indeks_pergerakan_item_transaksi ON pergerakan_persediaan(item_transaksi_id);
CREATE INDEX indeks_pergerakan_pengembalian ON pergerakan_persediaan(pengembalian_id);
CREATE INDEX indeks_pergerakan_item_pengembalian ON pergerakan_persediaan(item_pengembalian_id);
CREATE INDEX indeks_pergerakan_shift ON pergerakan_persediaan(shift_id);

CREATE TABLE pergerakan_kas (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  shift_id INTEGER NOT NULL,
  jenis TEXT NOT NULL,
  jumlah_delta INTEGER NOT NULL,
  transaksi_id INTEGER,
  pengembalian_id INTEGER,
  keterangan TEXT NOT NULL,
  dibuat_pada INTEGER NOT NULL,
  FOREIGN KEY(shift_id) REFERENCES shift(id) ON DELETE RESTRICT,
  FOREIGN KEY(transaksi_id) REFERENCES transaksi(id) ON DELETE RESTRICT,
  FOREIGN KEY(pengembalian_id) REFERENCES pengembalian(id) ON DELETE RESTRICT
);
CREATE INDEX indeks_pergerakan_kas_shift ON pergerakan_kas(shift_id);
CREATE INDEX indeks_pergerakan_kas_transaksi ON pergerakan_kas(transaksi_id);
CREATE INDEX indeks_pergerakan_kas_pengembalian ON pergerakan_kas(pengembalian_id);
CREATE INDEX indeks_pergerakan_kas_waktu ON pergerakan_kas(dibuat_pada);

CREATE TABLE printer (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nama TEXT NOT NULL,
  tipe_koneksi TEXT NOT NULL,
  is_default INTEGER NOT NULL,
  prioritas INTEGER NOT NULL,
  karakter_per_baris INTEGER NOT NULL,
  lebar_kertas TEXT NOT NULL,
  mendukung_status INTEGER NOT NULL,
  alamat_bluetooth TEXT,
  alamat_wifi TEXT,
  port_wifi INTEGER,
  usb_vendor_id INTEGER,
  usb_product_id INTEGER,
  dibuat_pada INTEGER NOT NULL,
  gagal_status_berturut INTEGER NOT NULL,
  dinonaktifkan_otomatis INTEGER NOT NULL
);
CREATE INDEX indeks_printer_default ON printer(is_default);
CREATE INDEX indeks_printer_prioritas ON printer(prioritas);

CREATE TABLE profil_toko (
  id INTEGER PRIMARY KEY NOT NULL,
  nama_toko TEXT NOT NULL,
  alamat TEXT NOT NULL,
  catatan_footer TEXT NOT NULL,
  logo_uri TEXT,
  cetak_otomatis INTEGER NOT NULL
);
INSERT INTO profil_toko(id,nama_toko,alamat,catatan_footer,logo_bytes,cetak_otomatis)
VALUES(1,'','','',NULL,0);

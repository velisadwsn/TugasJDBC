import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.sql.Timestamp;

// Superclass untuk barang
class Barang {
    protected String kodeBarang;
    protected String namaBarang;
    protected double hargaBarang;

    public Barang(String kodeBarang, String namaBarang, double hargaBarang) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
    }

    public double getHargaBarang() {
        return hargaBarang;
    }

    public void displayInfo() {
        System.out.println("Kode Barang: " + kodeBarang);
        System.out.println("Nama Barang: " + namaBarang);
        System.out.println("Harga Barang: Rp " + hargaBarang);
    }
}

// Subclass yang mengatur pembelian barang (inheritance)
class Pembelian extends Barang {
    private int jumlahBeli;

    public Pembelian(String kodeBarang, String namaBarang, double hargaBarang, int jumlahBeli) {
        super(kodeBarang, namaBarang, hargaBarang); // Memanggil constructor superclass
        this.jumlahBeli = jumlahBeli;
    }

    public double hitungTotal() {
        return jumlahBeli * hargaBarang; // Menghitung total harga
    }

    @Override
    public void displayInfo() {
        super.displayInfo(); // Memanggil method displayInfo dari superclass
        System.out.println("Jumlah Beli: " + jumlahBeli);
        System.out.println("Total Harga: Rp " + hitungTotal());
    }
}

//main class
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Login section
        System.out.println("+-----------------------------------------------------+");
        System.out.println("                     Log in");
        System.out.println("+-----------------------------------------------------+");

        String storedUsername = "rae";
        String storedPassword = "1006";
        String storedCaptcha = "12345";

        boolean loginSuccessful = false;
        while (!loginSuccessful) {
            try {
                System.out.print("Username : ");
                String username = scanner.nextLine().trim(); // Menggunakan trim() untuk menghapus spasi tambahan

                System.out.print("Password : ");
                String password = scanner.nextLine();

                System.out.print("Captcha  : ");
                String captcha = scanner.nextLine();

                // Validasi login (menggunakan String method equalsIgnoreCase untuk captcha)
                if (username.equals(storedUsername) && password.equals(storedPassword) && captcha.equalsIgnoreCase(storedCaptcha)) {
                    loginSuccessful = true;
                    System.out.println("Login berhasil!");
                } else {
                    throw new IllegalArgumentException("Login gagal, silakan coba lagi.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("+-----------------------------------------------------+\n");
        }

    
        try {
            System.out.println("Pilih operasi CRUD:");
            System.out.println("1. Create");
            System.out.println("2. Read");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.print("Pilihan: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer

            switch (choice) {
                case 1:
                    createData(scanner);
                    break;
                case 2:
                    readData();
                    break;
                case 3:
                    updateData(scanner);
                    break;
                case 4:
                    deleteData(scanner);
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        } finally {
            scanner.close(); // Menutup Scanner
        }
        
    }

    private static void createData(Scanner scanner) {
        try {
            System.out.print("No. Faktur   : ");
            String noFaktur = scanner.nextLine();

            System.out.print("Kode Barang  : ");
            String kodeBarang = scanner.nextLine().toUpperCase();

            System.out.print("Nama Barang  : ");
            String namaBarang = scanner.nextLine();

            System.out.print("Harga Barang : ");
            double hargaBarang = scanner.nextDouble();

            System.out.print("Jumlah Beli  : ");
            int jumlahBeli = scanner.nextInt();

            scanner.nextLine(); // Membersihkan buffer
            System.out.print("Kasir        : ");
            String namaKasir = scanner.nextLine();

            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(now);

            double totalHarga = hargaBarang * jumlahBeli;

            saveToDatabase(noFaktur, kodeBarang, namaBarang, hargaBarang, jumlahBeli, totalHarga, namaKasir, formattedDate);
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membuat data: " + e.getMessage());
        }
    }

    private static void readData() {
        String url = "jdbc:postgresql://localhost:5432/supermarket";
        String user = "postgres";
        String password = "dec15may";

        String selectQuery = "SELECT * FROM transaksi";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            System.out.println("\nData Transaksi:");
            while (resultSet.next()) {
                System.out.println("No. Faktur   : " + resultSet.getString("no_faktur"));
                System.out.println("Kode Barang  : " + resultSet.getString("kode_barang"));
                System.out.println("Nama Barang  : " + resultSet.getString("nama_barang"));
                System.out.println("Harga Barang : Rp " + resultSet.getDouble("harga_barang"));
                System.out.println("Jumlah Beli  : " + resultSet.getInt("jumlah_beli"));
                System.out.println("Total Harga  : Rp " + resultSet.getDouble("total_harga"));
                System.out.println("Kasir        : " + resultSet.getString("nama_kasir"));
                System.out.println("Tanggal Waktu: " + resultSet.getString("tanggal_waktu"));
                System.out.println("--------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error saat membaca data: " + e.getMessage());
        }
    }

    private static void updateData(Scanner scanner) {
        try {
            System.out.print("Masukkan No. Faktur yang akan diupdate: ");
            String noFaktur = scanner.nextLine();

            System.out.print("Kode Barang Baru: ");
            String kodeBarang = scanner.nextLine();

            System.out.print("Nama Barang Baru: ");
            String namaBarang = scanner.nextLine();

            System.out.print("Harga Barang Baru: ");
            double hargaBarang = scanner.nextDouble();

            System.out.print("Jumlah Beli Baru: ");
            int jumlahBeli = scanner.nextInt();

            scanner.nextLine(); // Membersihkan buffer

            double totalHarga = hargaBarang * jumlahBeli;

            String url = "jdbc:postgresql://localhost:5432/supermarket";
            String user = "postgres";
            String password = "dec15may";

            String updateQuery = "UPDATE transaksi SET kode_barang = ?, nama_barang = ?, harga_barang = ?, jumlah_beli = ?, total_harga = ? WHERE no_faktur = ?";

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setString(1, kodeBarang);
                preparedStatement.setString(2, namaBarang);
                preparedStatement.setDouble(3, hargaBarang);
                preparedStatement.setInt(4, jumlahBeli);
                preparedStatement.setDouble(5, totalHarga);
                preparedStatement.setString(6, noFaktur);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Data berhasil diperbarui.");
                } else {
                    System.out.println("No. Faktur tidak ditemukan.");
                }

            } catch (SQLException e) {
                System.out.println("Error saat mengupdate data: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat mengupdate data: " + e.getMessage());
        }
    }

    private static void deleteData(Scanner scanner) {
        try {
            System.out.print("Masukkan No. Faktur yang akan dihapus: ");
            String noFaktur = scanner.nextLine();

            String url = "jdbc:postgresql://localhost:5432/supermarket";
            String user = "postgres";
            String password = "dec15may";

            String deleteQuery = "DELETE FROM transaksi WHERE no_faktur = ?";

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

                preparedStatement.setString(1, noFaktur);

                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Data berhasil dihapus.");
                } else {
                    System.out.println("No. Faktur tidak ditemukan.");
                }

            } catch (SQLException e) {
                System.out.println("Error saat menghapus data: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat menghapus data: " + e.getMessage());
        }
    }

    private static void saveToDatabase(String noFaktur, String kodeBarang, String namaBarang, double hargaBarang, int jumlahBeli, double totalHarga, String namaKasir, String tanggalWaktu) {
        String url = "jdbc:postgresql://localhost:5432/supermarket";
        String user = "postgres"; 
        String password = "dec15may"; 

        String insertQuery = "INSERT INTO transaksi (no_faktur, kode_barang, nama_barang, harga_barang, jumlah_beli, total_harga, nama_kasir, tanggal_waktu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, noFaktur);
            preparedStatement.setString(2, kodeBarang);
            preparedStatement.setString(3, namaBarang);
            preparedStatement.setDouble(4, hargaBarang);
            preparedStatement.setInt(5, jumlahBeli);
            preparedStatement.setDouble(6, totalHarga);
            preparedStatement.setString(7, namaKasir);
            preparedStatement.setString(8, tanggalWaktu);

            Timestamp timestamp = Timestamp.valueOf(tanggalWaktu.replace("formattedDate", "timestamp"));
            preparedStatement.setTimestamp(8, timestamp);
           
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data berhasil disimpan ke database.");
            }

        } catch (SQLException e) {
            System.out.println("Error saat menyimpan data: " + e.getMessage());
        }
    }
}
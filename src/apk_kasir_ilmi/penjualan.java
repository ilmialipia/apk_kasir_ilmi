
package apk_kasir_ilmi;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;


public class penjualan extends javax.swing.JFrame {
private DefaultTableModel model=null;
   private PreparedStatement stat;
   private ResultSet rs;
   koneksi k = new koneksi();
   
    public penjualan() {
        initComponents();
        k.connect();
        tabelkosongdetail();
        tabelpenjualan();
        tabelkosongpelanggan();
        tabelkosongproduk();
        utama();
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        txttglpenjualan.setText(s.format(date));
        txttotalharga.setText("0");
        txtnamapelanggan.requestFocus();
    }

    public void tabelkosongdetail() {
        //Create Table
        model = new DefaultTableModel();
        tabeldetailpenjualan.setModel(model);
        model.addColumn("Penjualan ID");
        model.addColumn("Produk ID");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Sub Total");
    }

    public void tabelpenjualan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from penjualan");
            this.rs = this.stat.executeQuery();
            tabelpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void tabelkosongproduk() {
        //Create Table
        model = new DefaultTableModel();
        tabelproduk.setModel(model);
        model.addColumn("Produk ID");
        model.addColumn("Nama Produk");
        model.addColumn("Harga");
        model.addColumn("Stok");
    }

    public void tabelkosongpelanggan() {
        //Create Table
        model = new DefaultTableModel();
        tabelpelanggan.setModel(model);
        model.addColumn("Pelanggan ID");
        model.addColumn("Nama PLG");
        model.addColumn("Alamat");
        model.addColumn("No. TLP");
    }

    public void utama() {
        txtpelangganid.setText("");
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
        autonumber();
    }

    public void clear() {
        txtpelangganid.setText("");
        txtnamapelanggan.setText("");
        txttotalharga.setText("0");
        txTampil.setText("0");
    }

    class FPenjualan {

        int PenjualanID, PelangganID, TtlHarga;
        String TanggalPenjualan;

        public FPenjualan() {
            this.PenjualanID = 0;
            this.TanggalPenjualan = txttglpenjualan.getText();
            this.TtlHarga = Integer.parseInt(txttotalharga.getText());
            this.PelangganID = 0;
        }
    }

    public void refreshTable() {
        try {
            this.stat = k.getCon().prepareStatement("select * from detailpenjualan");
            this.rs = this.stat.executeQuery();
            tabeldetailpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void refresTabelPelanggan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from Pelanggan where PelangganID like '%" + txtnamapelanggan.getText() + "%' OR NamaPelanggan like '%" + txtnamapelanggan.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelpelanggan.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    private void autonumber() {
        try {
            Connection c = k.getCon();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM penjualan ORDER BY PenjualanID DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("PenjualanID").substring(2);
                String TR = "" + (Integer.parseInt(NoFaktur) + 1);
                String Nol = "";

                if (TR.length() == 1) {
                    Nol = "000";
                } else if (TR.length() == 2) {
                    Nol = "00";
                } else if (TR.length() == 3) {
                    Nol = "0";
                } else if (TR.length() == 4) {
                    Nol = "";
                }
                txtpenjualanid.setText("TR" + Nol + TR);
            } else {
                txtpenjualanid.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    public void inputpenjualan() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        String penjID = txtpenjualanid.getText();
        String tglpenj = txttglpenjualan.getText();
        String idPLG = txtpelangganid.getText();
        String total = txttotalharga.getText();

        try {
            Connection c = k.getCon();
            String sql = "INSERT INTO penjualan VALUES (?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, penjID);
            p.setString(2, tglpenj);
            p.setString(3, total);
            p.setString(4, idPLG);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("simpan penjualan error");
        }

        try {
            Connection c = k.getCon();
            int baris = tabeldetailpenjualan.getRowCount();

            for (int i = 0; i < baris; i++) {
                String sql = "INSERT INTO detailpenjualan (PenjualanID, ProdukID, JumlahProduk, Harga, Subtotal) VALUES ('" + tabeldetailpenjualan.getValueAt(i, 0) + "','" + tabeldetailpenjualan.getValueAt(i, 1) + "','" + tabeldetailpenjualan.getValueAt(i, 2) + "','" + tabeldetailpenjualan.getValueAt(i, 3) + "','" + tabeldetailpenjualan.getValueAt(i, 4) + "')";
                PreparedStatement p = c.prepareStatement(sql);
                p.executeUpdate();
                p.close();
            }
        } catch (Exception e) {
            System.out.println("simpan detail penjualan error");
        }
        clear();
        utama();
        autonumber();
        kosong();
        txTampil.setText("Rp. 0");
    }

    public void refreshTabelProduk() {
        try {
            this.stat = k.getCon().prepareStatement("select * from produk where NamaProduk like '%" + txtnamaproduk.getText() + "%' OR ProdukID like '%" + txtnamaproduk.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelproduk.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    public void totalHarga() {
        int jumlahBaris = tabeldetailpenjualan.getRowCount();
        int totalHarga = 0;
        int jumlahProduk, hargaProduk;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 2).toString());
            hargaProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 3).toString());
            totalHarga = totalHarga + (jumlahProduk * hargaProduk);
        }
        txttotalharga.setText(String.valueOf(totalHarga));
        txTampil.setText("Rp " + totalHarga + ",00");
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        model.addRow(new Object[]{
            txtpenjualanid.getText(),
            txtprodukid.getText(),
            txtjumlahproduk.getText(),
            txtharga.getText(),
            txtsubtotal.getText()
        });
    }

    public void tambahDetailPenjualan() {
        int jumlah, harga, total;

        jumlah = Integer.valueOf(txtjumlahproduk.getText());
        harga = Integer.valueOf(txtharga.getText());
        total = jumlah * harga;

        txtsubtotal.setText(String.valueOf(total));

        loadData();
        totalHarga();
        clear2();
        txtnamaproduk.requestFocus();
    }

    public void clear2() {
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtjumlahproduk = new javax.swing.JTextField();
        txtpenjualanid = new javax.swing.JTextField();
        txttglpenjualan = new javax.swing.JTextField();
        txtnamaproduk = new javax.swing.JTextField();
        txtharga = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtnamapelanggan = new javax.swing.JTextField();
        txtpelangganid = new javax.swing.JTextField();
        txttotalharga = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txTampil = new javax.swing.JTextField();
        txtsubtotal = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabeldetailpenjualan = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        txtprodukid = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelpelanggan = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelproduk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabelpenjualan = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
        jLabel11.setText("PENJUALAN");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel1.setText("Nama Produk");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel2.setText("PenjualanID");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jLabel3.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel3.setText("TotalHarga");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, -1, 60));

        jLabel4.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel4.setText("Nama Pelanggan");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, 60));

        jLabel5.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel5.setText("Harga");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, 60));

        txtjumlahproduk.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtjumlahproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtjumlahprodukKeyTyped(evt);
            }
        });
        getContentPane().add(txtjumlahproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 130, -1));

        txtpenjualanid.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        getContentPane().add(txtpenjualanid, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 130, -1));

        txttglpenjualan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txttglpenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttglpenjualanActionPerformed(evt);
            }
        });
        getContentPane().add(txttglpenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 60, 150, -1));

        txtnamaproduk.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtnamaproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamaprodukKeyReleased(evt);
            }
        });
        getContentPane().add(txtnamaproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 130, -1));

        txtharga.setBackground(new java.awt.Color(255, 204, 255));
        txtharga.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtharga.setEnabled(false);
        getContentPane().add(txtharga, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 210, 130, -1));

        jLabel6.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel6.setText("Jumlah");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 60));

        jLabel7.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel7.setText("Tabel Penjualan");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 290, -1, 60));

        jLabel8.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel8.setText("Subtotal");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 190, -1, 60));

        txtnamapelanggan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtnamapelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnamapelangganActionPerformed(evt);
            }
        });
        txtnamapelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamapelangganKeyReleased(evt);
            }
        });
        getContentPane().add(txtnamapelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 130, -1));

        txtpelangganid.setBackground(new java.awt.Color(255, 204, 255));
        txtpelangganid.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtpelangganid.setEnabled(false);
        txtpelangganid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpelangganidActionPerformed(evt);
            }
        });
        getContentPane().add(txtpelangganid, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, 130, -1));

        txttotalharga.setBackground(new java.awt.Color(255, 204, 204));
        txttotalharga.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txttotalharga.setEnabled(false);
        txttotalharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttotalhargaActionPerformed(evt);
            }
        });
        getContentPane().add(txttotalharga, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 260, 160, -1));

        jButton1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton1.setText("DELETE DETAIL PENJUALAN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 320, 230, -1));

        jButton2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton2.setText("Tambah Pelanggan");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 160, -1));

        txTampil.setBackground(new java.awt.Color(255, 204, 255));
        txTampil.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txTampil.setText("Rp.0");
        txTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txTampilActionPerformed(evt);
            }
        });
        getContentPane().add(txTampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 480, 110, -1));

        txtsubtotal.setBackground(new java.awt.Color(255, 204, 204));
        txtsubtotal.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtsubtotal.setEnabled(false);
        txtsubtotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsubtotalActionPerformed(evt);
            }
        });
        getContentPane().add(txtsubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 220, 160, -1));

        jButton3.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton3.setText("Tambah Produk");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 170, 160, -1));

        jButton4.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton4.setText("CETAK NOTA");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 480, -1, -1));

        tabeldetailpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabeldetailpenjualan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabeldetailpenjualanKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tabeldetailpenjualan);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 580, 90));

        jButton5.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton5.setText("INPUT DETAIL PENJUALAN");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 210, -1));

        jButton6.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton6.setText("INPUT");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 480, -1, -1));

        txtprodukid.setBackground(new java.awt.Color(255, 204, 255));
        txtprodukid.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txtprodukid.setEnabled(false);
        txtprodukid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtprodukidActionPerformed(evt);
            }
        });
        getContentPane().add(txtprodukid, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, 130, -1));

        jLabel9.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel9.setText("TanggalPenjualan");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, -1, 60));

        tabelpelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelpelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelpelangganMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabelpelanggan);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 60, -1, 90));

        jLabel10.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel10.setText("Tabel Pelanggan");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, -1, 60));

        tabelproduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelproduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelprodukMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabelproduk);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 200, -1, 90));

        jLabel12.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel12.setText("Tabel Produk");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 150, -1, 60));

        tabelpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(tabelpenjualan);

        getContentPane().add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 340, 450, 90));

        setSize(new java.awt.Dimension(1108, 579));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txttglpenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttglpenjualanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttglpenjualanActionPerformed

    private void txtnamapelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnamapelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamapelangganActionPerformed

    private void txtpelangganidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpelangganidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpelangganidActionPerformed

    private void txttotalhargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttotalhargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttotalhargaActionPerformed

    private void txTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTampilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txTampilActionPerformed

    private void txtsubtotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsubtotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsubtotalActionPerformed

    private void txtprodukidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprodukidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprodukidActionPerformed

    private void tabeldetailpenjualanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabeldetailpenjualanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tabeldetailpenjualanKeyPressed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    inputpenjualan();
    tabelpenjualan();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void tabelprodukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelprodukMouseClicked
        //todo add your handling code here
        int r =tabelproduk.getSelectedRow();
        String ProdukID = tabelproduk.getValueAt(r, 0).toString();
        String NamaPrd = tabelproduk.getValueAt(r, 1).toString();
        String Hrg = tabelproduk.getValueAt(r, 2).toString();
        String Tgl = tabelproduk.getValueAt(r, 3).toString();
        
        txtprodukid.setText(ProdukID);
        txtnamaproduk.setText(NamaPrd);
        txtharga.setText(Hrg);
        txtjumlahproduk.requestFocus();
    }//GEN-LAST:event_tabelprodukMouseClicked

    private void txtnamapelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamapelangganKeyReleased
        refresTabelPelanggan();
        txtpelangganid.setText("");
    }//GEN-LAST:event_txtnamapelangganKeyReleased

    private void tabelpelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelpelangganMouseClicked
         //tabel produk di klik
        int r =tabelpelanggan.getSelectedRow();
        String PlgID = tabelpelanggan.getValueAt(r, 0).toString();
        String NamaPlg = tabelpelanggan.getValueAt(r, 1).toString();
        String Alamat= tabelpelanggan.getValueAt(r, 2).toString();
        String Tlp = tabelpelanggan.getValueAt(r, 3).toString();
        
        txtpelangganid.setText(PlgID);
        txtnamapelanggan.setText(NamaPlg);
        txtnamaproduk.requestFocus();
    }//GEN-LAST:event_tabelpelangganMouseClicked

    private void txtnamaprodukKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamaprodukKeyReleased
        refreshTabelProduk();
        txtprodukid.setText("");
    }//GEN-LAST:event_txtnamaprodukKeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        tambahDetailPenjualan();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       //HAPUS DATA TABEL DETAIL PENJUALAN
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        int row = tabeldetailpenjualan.getSelectedRow();
        model.removeRow(row);
        totalHarga();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // buka form pelanggan
        pelanggan a=new pelanggan();
        a.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        produk a = new produk();
        a.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtjumlahprodukKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtjumlahprodukKeyTyped
       // validasi hanya input angka
        char karakter = evt.getKeyChar();
        if (!(((karakter >= '0') && (karakter <= '9') || (karakter == KeyEvent.VK_BACK_SPACE) || (karakter == KeyEvent.VK_DELETE)))) {
            JOptionPane.showMessageDialog(null, "Hanya boleh input angka");
            evt.consume();
        }
    }//GEN-LAST:event_txtjumlahprodukKeyTyped

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // KODING CETAK NOTA
        try {
            File namafile = new File("src/report1.jasper");
            JasperPrint jp = JasperFillManager.fillReport(namafile.getPath(), null, k.getCon());
            JasperViewer.viewReport(jp,false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "GAGAL");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new penjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable tabeldetailpenjualan;
    private javax.swing.JTable tabelpelanggan;
    private javax.swing.JTable tabelpenjualan;
    private javax.swing.JTable tabelproduk;
    private javax.swing.JTextField txTampil;
    public static javax.swing.JTextField txtharga;
    public static javax.swing.JTextField txtjumlahproduk;
    public static javax.swing.JTextField txtnamapelanggan;
    public static javax.swing.JTextField txtnamaproduk;
    public static javax.swing.JTextField txtpelangganid;
    private javax.swing.JTextField txtpenjualanid;
    public static javax.swing.JTextField txtprodukid;
    private javax.swing.JTextField txtsubtotal;
    private javax.swing.JTextField txttglpenjualan;
    private javax.swing.JTextField txttotalharga;
    // End of variables declaration//GEN-END:variables
}
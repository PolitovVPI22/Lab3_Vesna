package org.dstu.db;

import org.dstu.util.CsvReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DbWorker {
    public static void populateFromFile(String fileName) {
        List<String[]> strings = CsvReader.readCsvFile(fileName, ";");
        Connection conn = DbConnection.getConnection();
        try {
            Statement cleaner = conn.createStatement();
            System.out.println(cleaner.executeUpdate("DELETE FROM student"));
            System.out.println(cleaner.executeUpdate("DELETE FROM teacher"));
            PreparedStatement studentSt = conn.prepareStatement(
                    "INSERT INTO student (last_name, first_name, middle_name, grp, programme) " +
                            "VALUES (?, ?, ?, ?, ?)");
            PreparedStatement teacherSt = conn.prepareStatement(
                    "INSERT INTO teacher (last_name, first_name, middle_name, dept, grade, experience) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");

            for (String[] line: strings) {
                if (line[0].equals("0")) {
                    studentSt.setString(1, line[1]);
                    studentSt.setString(2, line[2]);
                    studentSt.setString(3, line[3]);
                    studentSt.setString(4, line[4]);
                    studentSt.setString(5, line[5]);
                    studentSt.addBatch();
                } else {
                    teacherSt.setString(1, line[1]);
                    teacherSt.setString(2, line[2]);
                    teacherSt.setString(3, line[3]);
                    teacherSt.setString(4, line[4]);
                    teacherSt.setString(5, line[5]);
                    teacherSt.setInt(6, Integer.parseInt(line[6]));
                    teacherSt.addBatch();
                }
            }
            int[] stRes = studentSt.executeBatch();
            int[] teacherRes = teacherSt.executeBatch();
            for (int num: stRes) {
                System.out.println(num);
            }

            for (int num: teacherRes) {
                System.out.println(num);
            }
            cleaner.close();
            studentSt.close();
            teacherSt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void demoQuery() {
        Connection conn = DbConnection.getConnection();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM teacher WHERE experience > 20");
            while (rs.next()) {
                System.out.print(rs.getString("last_name"));
                System.out.print(" ");
                System.out.print(rs.getString("first_name"));
                System.out.print(" ");
                System.out.println(rs.getString("experience"));
            }
            rs.close();
            st.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void dirtyReadDemo() {
        Runnable first = () -> {
            Connection conn1 = DbConnection.getNewConnection();
            if (conn1 != null) {
                try {
                    conn1.setAutoCommit(false);
                    conn1.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                    Statement upd = conn1.createStatement();
                    upd.executeUpdate("UPDATE student SET grp='МИН21' WHERE grp='МИН11'");
                    Thread.sleep(2000);
                    conn1.rollback();
                    upd.close();
                    Statement st = conn1.createStatement();
                    System.out.println("In the first thread:");
                    ResultSet rs = st.executeQuery("SELECT * FROM student");
                    while (rs.next()) {
                        System.out.println(rs.getString("grp"));
                    }
                    st.close();
                    rs.close();
                    conn1.close();
                } catch (SQLException | InterruptedException throwables) {
                    throwables.printStackTrace();
                }
            }
        };

        Runnable second = () -> {
            Connection conn2 = DbConnection.getNewConnection();
            if (conn2 != null) {
                try {
                    Thread.sleep(500);
                    conn2.setAutoCommit(false);
                    conn2.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                    Statement st = conn2.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM student");
                    while (rs.next()) {
                        System.out.println(rs.getString("grp"));
                    }
                    rs.close();
                    st.close();
                    conn2.close();
                } catch (SQLException | InterruptedException throwables) {
                    throwables.printStackTrace();
                }
            }
        };
        Thread th1 = new Thread(first);
        Thread th2 = new Thread(second);
        th1.start();
        th2.start();
    }
}

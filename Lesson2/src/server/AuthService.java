package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chaykin Ivan
 * @version 13.10.2019
 */

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Lesson2/userTestDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String login, String pass, String nick) {
        String sql = String.format("INSERT INTO userTable (login, password, nickname) " +
                "VALUES ('%s', '%s', '%s')", login, pass.hashCode(), nick);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getNickByLoginAndPass(String login, String pass) {

        String sql = String.format("select nickname, password FROM userTable where" +
                " login = '%s'", login);
        try {
            int myHash = pass.hashCode();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                String nick = rs.getString(1);
                int dbHash = rs.getInt(2);
                System.out.println(myHash + " = " + dbHash);
                if(myHash == dbHash) {
                    return nick;
                }
                // / return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addBlackListSQL (String blocker, String blocking) {
        String sql = String.format("INSERT INTO blacklist (Blocker, Blocking) " +
                "VALUES ('%s', '%s')", blocker, blocking);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeBlackListSQL (String blocker, String blocking) {
        String sql = String.format("DELETE FROM blacklist WHERE Blocker = '%s' AND Blocking = '%s'", blocker, blocking);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> blackListFromSQL (String blocker) {
        List<String> blackList = new ArrayList<>();
        String sql = String.format("SELECT Blocking FROM blacklist where" +
                " Blocker = '%s'", blocker);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                blackList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blackList;
    }

    public static boolean validLoginNick (String loginOrNick, String name) {
        if (name.length() > 0) {
            String sql = String.format("SELECT %s FROM userTable where %s = '%s'", loginOrNick, loginOrNick, name);
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(sql);
                return !rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }


    }

    public static boolean passwordVerification(String password) {
        System.out.println(password);
        Pattern p = Pattern.compile("([0-9A-za-z]).{7,}");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    //
    // Блок смены ника
    //

    public static String changeNick(String newnick, String oldnick) {

        String sql = String.format("UPDATE userTable SET nickname = '%s' WHERE" +
                " nickname = '%s'", newnick, oldnick);
        try {
            //stmt.executeQuery(sql);
            stmt.execute(sql);
            return newnick;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //
    // Блок заполнения БД с историей сообщений
    //

    public static void addHistorySQL (String nick, String msg) {
        String sql = String.format("INSERT INTO history (nick, Msg) " +
                "VALUES ('%s', '%s')", nick, msg);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //
    // Считывание БД с историей сообщений
    //
    public static void getHistorySQL (ClientHandler client) {
        String sql = String.format("SELECT nick, Msg FROM history");

        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                client.sendMsg(rs.getString(1) + ": " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}

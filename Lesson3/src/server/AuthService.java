package server;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chaykin Ivan
 * @version 17.10.2019
 */

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Lesson3/userTestDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String login, String nick, String pass) {
        String sql = String.format("INSERT INTO userTable (login, password, nickname) " +
                "VALUES ('%s', '%s', '%s')", login.trim(), pass.trim().hashCode(), nick.trim());
        try {
            return stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {

        String sql = String.format("select nickname, password FROM userTable where" +
                " login = '%s'", login.trim());
        try {
            int myHash = pass.trim().hashCode();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                String nick = rs.getString(1);
                int dbHash = rs.getInt(2);
                if(myHash == dbHash) {
                    return nick;
                }
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
            String sql = String.format("SELECT %s FROM userTable where %s = '%s'", loginOrNick, loginOrNick, name.trim());
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
        Pattern p = Pattern.compile("([0-9A-za-z]).{7,}");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    //
    // Блок смены ника
    //

    public static String changeNick(String newNick, String oldNick) {
        if (newNick.length() > 0 && oldNick.length() > 0) {
            String sql = String.format("UPDATE userTable SET nickname = '%s' WHERE" +
                    " nickname = '%s'", newNick.trim(), oldNick.trim());
            try {
                stmt.execute(sql);
                return newNick.trim();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //
    // Блок заполнения БД с историей сообщений
    //

    public static void addHistorySQL (String nick, String msg) {
        String sql = String.format("INSERT INTO history (nick, Msg, pubDate) " +
                "VALUES ('%s', '%s', '%d')", nick, msg, Instant.now().getEpochSecond());
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

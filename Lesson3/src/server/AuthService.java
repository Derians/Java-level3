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

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Lesson3/userTestDB.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String login, String nick, String pass) {
        if (!login.equals("") && !pass.equals("")) {
            String sql = String.format("INSERT INTO userTable (login, password, nickname) " +
                    "VALUES ('%s', '%s', '%s')", login.trim(), pass.trim().hashCode(), nick.trim());
            try {
                return stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        if (!login.equals("") && !pass.equals("")) {
            String sql = String.format("select nickname, password FROM userTable where" +
                    " login = '%s'", login.trim());
            try {
                int myHash = pass.trim().hashCode();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String nick = rs.getString(1);
                    int dbHash = rs.getInt(2);
                    if (myHash == dbHash) {
                        return nick;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
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
        if (!blocker.equals("") && !blocking.equals("")) {
            String sql = String.format("INSERT INTO blacklist (Blocker, Blocking) " +
                    "VALUES ('%s', '%s')", blocker.trim(), blocking.trim());
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeBlackListSQL (String blocker, String blocking) {
        if (!blocker.equals("") && !blocking.equals("")) {
            String sql = String.format("DELETE FROM blacklist WHERE Blocker = '%s' " +
                    "AND Blocking = '%s'", blocker.trim(), blocking.trim());
            try {
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    stmt.execute(sql);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> blackListFromSQL (String blocker) {
        if (!blocker.equals("")) {
            List<String> blackList = new ArrayList<>();
            String sql = String.format("SELECT Blocking FROM blacklist where" +
                    " Blocker = '%s'", blocker);
            try {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    blackList.add(rs.getString(1));
                }
                return blackList;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean validLoginNick (String loginOrNick, String name) {
        if (!name.equals("")) {
            String sql = String.format("SELECT %s FROM userTable where %s = '%s'",
                    loginOrNick, loginOrNick, name.trim());
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(sql);
                return !rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean passwordVerification(String password) {
        if (!password.equals("")) {
            Pattern p = Pattern.compile("([0-9A-za-z]).{7,}");
            Matcher m = p.matcher(password.trim());
            return m.matches();
        }
        return false;
    }

    //
    // Блок смены ника
    //

    public static String changeNick(String newNick, String oldNick) {
        if (!newNick.equals("") && !oldNick.equals("")) {
            String sql = String.format("UPDATE userTable SET nickname = '%s' WHERE" +
                    " nickname = '%s'", newNick.trim(), oldNick.trim());
            try {
                stmt.execute(sql);
                return newNick.trim();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //
    // Блок заполнения БД с историей сообщений
    //

    public static void addHistorySQL (String nick, String msg) {
        if (!nick.equals("") && !msg.equals("")) {
            String sql = String.format("INSERT INTO history (nick, Msg, pubDate) " +
                    "VALUES ('%s', '%s', '%d')", nick.trim(), msg.trim(), Instant.now().getEpochSecond());
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

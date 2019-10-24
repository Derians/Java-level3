package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Chaykin Ivan
 * @version 17.10.2019
 */
public class ClientHandler {

    private int TIME_OUT = 120;

    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private List<String> blackList;

    private long a;

    public String getNick() {
        return nick;
    }

    String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            executorService.execute(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/addUser")) {
                            String[] tokens = str.split(" ");
                            if (tokens.length == 4) {
                                if (AuthService.validLoginNick("login", tokens[1])) {
                                    if (AuthService.validLoginNick("nickname", tokens[2])) {
                                        if ((AuthService.passwordVerification(tokens[3]))) {
                                            if (AuthService.addUser(tokens[1], tokens[2], tokens[3])) {
                                                sendMsg("/clearRegFields");
                                                server.printLog("Клиент " + tokens[2] + " зарегистрировался");
                                                sendMsg("Регистрация прошла успешно");
                                            } else {
                                                sendMsg("Регистрация не удалась");
                                            }
                                        } else {
                                            sendMsg("/errorReg = Некорректный пароль");
                                        }

                                    } else {
                                        sendMsg("/errorReg = Некорректный или занятый ник");
                                    }

                                } else {
                                    sendMsg("/errorReg = Некорректный или занятый логин");
                                }
                            }
                        }
                        if (str.startsWith("/auth")) {
                            String[] tokens = str.split(" ");
                            if (tokens.length == 3) {
                                String newNick = AuthService.getNickByLoginAndPass(tokens[1].trim(), tokens[2].trim());

                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        sendMsg("/authOk");
                                        nick = newNick;
                                        server.subscribe(this);
                                        System.out.println("Клиент " + nick + " подключился");
                                        blackList = AuthService.blackListFromSQL(nick);
                                        // Считывание истории сообщений
                                        AuthService.getHistorySQL(this);
                                        //
                                        break;
                                    } else {
                                        sendMsg("Учетная запись уже используется");
                                    }
                                } else {
                                    sendAuthError("Неверный логин/пароль");
                                }
                            }
                        }
                    }

                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            if (str.startsWith("/w ")) {
                                String[] tokens = str.split(" ", 3);
                                server.sendPersonalMsg(this, tokens[1], tokens[2]);
                            }

                            if (str.startsWith("/blackList ")) {
                                String[] tokens = str.split(" ");
                                if (blackList.contains(tokens[1])) {
                                    sendMsg("Пользователь " + tokens[1] + " уже внесен в черный список");
                                } else {
                                    blackList.add(tokens[1]);
                                    AuthService.addBlackListSQL(nick, tokens[1]);
                                    sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                                }
                            }
                            if (str.startsWith("/unBlackList ")) {
                                String[] tokens = str.split(" ");
                                if (blackList.contains(tokens[1])) {
                                    blackList.remove(tokens[1]);
                                    AuthService.removeBlackListSQL(nick, tokens[1]);
                                    sendMsg("Пользователь " + tokens[1] + " удален из черного список");
                                } else {
                                    sendMsg("Пользователя " + tokens[1] + " нет в черном списке");
                                }
                            }
                            //
                            // Блок смены ника
                            //
                            if (str.startsWith("/nick")) {
                                String[] tokens = str.split(" ");
                                if (tokens[1].equals(nick)) {
                                    sendMsg("Ваши новый и старый ники совпадают");
                                } else if (AuthService.validLoginNick("nickname", tokens[1])) {
                                    nick = AuthService.changeNick(tokens[1], nick);
                                    sendMsg("Вы сменили ник на " + nick);
                                    server.broadcastClientList();
                                } else {
                                    sendMsg(tokens[1] + " используется другим пользователем");
                                }
                            }
                        } else {
                            if (!str.equals("")) {
                                server.broadcastMsg(this, nick + ": " + str);
                                AuthService.addHistorySQL(nick, str);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.printLog("Клиент " + nick + " отключился");
                    server.unsubscribe(this);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAuthError(String error) {
        try {
            out.writeUTF("/errorAuth = " + error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMsg(String msg) {
        if (!msg.equals("")) {
            try {
                out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

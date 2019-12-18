//package com.demo.zhengtu;

//import com.demo.docket.GreetingServer;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameServer extends JFrame {

    private ServerSocket serverSocket;
    private static final int port = 9090;
    private JTextArea textArea;

    private static ConcurrentHashMap<String, Integer> heroLevel = new ConcurrentHashMap<>();

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void init() {
        setSize(600, 500);
        textArea = new JTextArea();
        textArea.setAlignmentX(30);
        textArea.setAlignmentX(30);
        textArea.setBackground(Color.GREEN);
        textArea.setSize(280, 400);
        this.add(textArea);
        this.setTitle("征途游戏服务器");
        this.setVisible(true);
    }

    public void fillArea(String content) {
        this.textArea.append(content + "\r\n");
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 2000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(15));

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        GameServer gameServer = new GameServer();
        gameServer.init();
        gameServer.fillArea("服务器启动在" + port + "端口监听");
        while (true) {
            //监听客户端连接
            socket = gameServer.getServerSocket().accept();
            threadPool.execute(new ClientHandler(socket, gameServer));
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private GameServer gameServer;

        public ClientHandler(Socket socket, GameServer gameServer) {
            this.socket = socket;
            this.gameServer = gameServer;
        }

        @Override
        public void run() {
            DataInputStream dis = null;
            DataOutputStream dos = null;
            Random random = new Random();
            int upgrade = 300;

            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    String input = dis.readUTF();
                    String[] array = input.split(":");
                    String hero = array[0];
                    String cmd = array[1];
                    Integer count = heroLevel.get(hero);
                    if (count == null) {
                        heroLevel.put(hero, 0);
                    }

                    if (cmd.equals("kill")) {
                        int reduce = random.nextInt(80);
                        int curCount = heroLevel.get(hero);
                        curCount = curCount + reduce;
                        String up="";
                        if (curCount >= 300) {
                            up="升级了！！！";
                            heroLevel.put(hero, 0);
                        } else
                            heroLevel.put(hero, curCount);

                        String content = "英雄 " + hero + " 打怪 ，掉落了" + reduce + " 滴血 "+up;
                        this.gameServer.fillArea(content);
                        dos.writeUTF(reduce+"");
                        dos.flush();
                    }
                } catch (IOException e) {

                }
            }
        }
    }

}

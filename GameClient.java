//package com.demo.zhengtu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9090);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis=new DataInputStream(socket.getInputStream());
        String heroName = "Hero1";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(" 请输入: ");
            String line = scanner.nextLine();
            dos.writeUTF(heroName+":"+line);
            String input=dis.readUTF();
            System.out.println("来自服务器，掉落了  "+input+"  滴血");

        }
    }
}

package edu.spbu.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  static private Socket clientSocket; //сокет для общения
  private static BufferedReader in; // поток чтения из сокета
  private static BufferedWriter out; // поток записи в сокет

  public static void main(String[] args) {
    try {
      int port = 8080;
      ServerSocket server = new ServerSocket(port); //северсокет прослушивает порт 8080
      System.out.println("Waiting for connection...");
      while (true) {
        clientSocket = server.accept();  // становимся в ожидание подключения к сокету под именем - "clientSocket" на серверной стороне
        System.out.println("Connection accepted.");
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
        System.out.println(word);
        out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
        out.flush(); // выталкиваем все из буфера
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
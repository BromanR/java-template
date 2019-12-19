package edu.spbu.server;

import java.io.*;
import java.net.*;

public class Client {
  static private Socket clientSocket;
  private static BufferedReader reader; // нам нужен ридер читающий с консоли
  private static BufferedReader in; // поток чтения из сокета
  private static BufferedWriter out; // поток записи в сокет


  public static void main(String[] args) throws IOException {
    try {
      while (true) {
        clientSocket = new Socket("localhost", 8080);
        reader = new BufferedReader(new InputStreamReader(System.in));
        // читать соообщения с сервера
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // писать туда же
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        System.out.println("Вы что-то хотели сказать? Введите это здесь:");
        String word = reader.readLine(); // ждём пока клиент что-нибудь
        // не напишет в консоль
        if (word.equals("quit")) break;
        out.write(word + "\n"); // отправляем сообщение на сервер
        out.flush();
        String serverWord = in.readLine(); // ждём, что скажет сервер
        System.out.println(serverWord); // получив - выводим на экран
      }
      System.out.println("Клиент был закрыт...");
      clientSocket.close();
      in.close();
      out.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
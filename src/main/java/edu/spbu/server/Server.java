package edu.spbu.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {

  static private Socket connection;
  static private OutputStream output;
  static private InputStream input;

  public static void main(String[] args) {
    try {
      int port = 8080;
      ServerSocket server = new ServerSocket(port);
      System.out.println("Waiting for connection...");
      while (true) {
        connection = server.accept();
        System.out.println("Connection accepted.");

        output = connection.getOutputStream();
        System.out.println("DataOutputStream  created");

        input = connection.getInputStream();
        System.out.println("DataInputStream created");

        String filePath = receiveData(); //получает запрос клиента
        sendData(filePath);              // отправляет ответ клиенту
        server.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //отправляет ответ
  private static void sendData(String filePath) throws IOException {
    File file = new File(filePath);
    if (file.exists()) {
      try (FileReader fileRead = new FileReader(file)) {

        output.flush();
        BufferedReader reader = new BufferedReader(fileRead);
        StringBuilder text = new StringBuilder();
        String i = reader.readLine();

        //чтение из файла
        while (i != null) {
          text.append(i);
          i = reader.readLine();
        }

        String content = text.toString();
        String message = "HTTP/1.1 200 OK\r\n" +
            "Server: ServerName\r\n" +
            "Content-Type: text/html\r\n" +
            "Connection: close\r\n\r\n" + content;
        output.write(message.getBytes());
        System.out.println("Ответ отправлен");
        output.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      output.write("HTTP/1.1 404\r\n\r\n".getBytes());
    }

  }

  //получает запрос
  private static String receiveData() {

    String filePath;
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    System.out.println("Запрос принят");
    try {
      String line;
      if ((line = reader.readLine()) != null) {
     //   System.out.println(line);
        String[] st = line.split(" ");
        System.out.println(Arrays.toString(st));
        if (st[1].length() > 0) {
          filePath = st[1].substring(1);
          System.out.println("Запрос принят. Ищем файл: " + filePath);
          //печать запроса клиента
//                    String str;
//                    while((str=reader.readLine()).length()!=0)
//                        System.out.println(str);

          return filePath;
        }
      } else System.out.println("Где-то ошибка");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return "Empty";
  }
}
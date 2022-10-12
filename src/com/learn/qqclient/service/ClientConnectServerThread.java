package com.learn.qqclient.service;

import com.learn.common.Message;
import com.learn.common.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    //该线程需要持有socket
    private Socket socket;

    //构造器可以接收一个socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;

    }

    @Override
    public void run() {
        //因为Thead需要在后台和服务器通信，因此我们while循环
       while (true){
           try {
               System.out.println("客户端线程，等待读取服务器发送的消息");
               ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
               Message m = (Message)ois.readObject();//如果服务器没有发送Message对象，线程会阻塞在这里
               //后面需要用到message
               //判断这个message类型，然后做相应的业务处理
               //如果读取到的是服务端返回的在线用户列表
               if(m.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                   //取出在线列表
                   //规定
                   String[] onlineUser = m.getContent().split(" ");
                   System.out.println("========当前在线用户列表======");
                   for (int i = 0; i < onlineUser.length; i++) {
                       System.out.println("用户：" + onlineUser[i]);

                   }
               }else if(m.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                   //把从服务器端转发的消息，提示到控制台即可
                   System.out.println("\n" + m.getSender() + "对" +m.getGetter() +"说:" + m.getContent());
               }else if(m.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                   //显示在客户端的控制台
                   System.out.println("\n" + m.getSender() + "对大家说：" + m.getContent());
               }else if(m.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                   System.out.println("\n" + m.getSender() + "给" + m.getGetter() + "发文件" +
                           m.getSrc() + "到我的电脑目录" + m.getDest());
                   //取出message的文件数组，通过文件输出流写道磁盘
                   FileOutputStream fileOutputStream = new FileOutputStream(m.getDest());
                   fileOutputStream.write(m.getFileBytes());
                   fileOutputStream.close();
                   System.out.println("\n 保存成功");
               }
               else {
                   System.out.println("其他类型的message，暂时不处理");
               }


           } catch (Exception e) {
               e.printStackTrace();
           }

       }
    }

    //为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}

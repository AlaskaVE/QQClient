package com.learn.qqclient.service;

import com.learn.common.Message;
import com.learn.common.MessageType;
import com.learn.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登陆验证和注册等功能
 */
public class UserClientService {
    //根据userId 和pwd 到服务器验证用户是否合法
    private User u = new User();//因为我们可能在其他地方使用user信息，因此设成成员属性
    //因为socket在其他地方也可能使用，因此做成属性
    private Socket socket;

    public boolean checkUser(String userId,String pwd){
        boolean b = false;
        //创建user对象
        u.setUserId(userId);
        u.setPasswd(pwd);

        try {
            //连接到服务器，发送u对象
            socket = new Socket(InetAddress.getByName("169.254.73.238"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送User对象

            //读取从服务端回送的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message m = (Message)ois.readObject();

            if(m.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){
                //创建一个和服务器端保持通信的线程->创建一个类 ClientConnectServiceThread
                ClientConnectServerThread clientConnectServiceThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServiceThread.start();
                //为了客户端的一个扩展，将线程放入到集合中管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServiceThread);
                b = true;

            }else {
                //如果登陆失败，不能创建和服务器通信的线程,关闭socket
                socket.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    //向服务端请求在线用户列表
    public void onlineFriendList(){
        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器
        //应该得到当前线程的socket 对应的ObjectOutputStream对象
        //先从管理线程的集合中，通过userId，得到这个线程
        //然后得到当前线程的socket对应的ObjectOutputStream对象
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    //编写方法，退出客户端，并给服务端发送一个退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定是哪个客户端退出
        //发送message
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId())
                            .getSocket().getOutputStream());//适用于一个客户端多个soket的方法
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出系统");
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

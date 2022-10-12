package com.learn.qqclient.service;

import com.learn.common.Message;
import com.learn.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 该类/对象，提供和消息相关的服务方法
 */
public class MessageClientService {
    /**
     *
     * @param content 内容
     * @param senderId 发送用户Id
     * @param getter  接收用户Id
     */
    public void sendMessageToOne(String content,String senderId,String getter){
        //构建Message
        Message message = new Message();
        message.setGetter(getter);
        message.setContent(content);
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.getSendTime(new Date().toString());//发送时间设置到message对象
        System.out.println(senderId + "对" + getter + "说" + content);
        //发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                            .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param content 发送内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content,String senderId){
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setContent(content);
        message.getSendTime(new Date().toString());
        System.out.println(senderId + "对大家说:" + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                            .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

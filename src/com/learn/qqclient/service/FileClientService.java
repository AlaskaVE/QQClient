package com.learn.qqclient.service;

import com.learn.common.Message;
import com.learn.common.MessageType;

import java.io.*;

/**
 * 该类/对象 完成文件的传输服务
 */
public class FileClientService {
    /**
     *
     * @param src 源文件
     * @param dest 把该文件发送到对方的哪个目录
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendFileToOne(String src,String dest,String senderId,String getterId){
        //读取src文件 --->message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            //将文件对应的字节数组设置到message
            message.setFileBytes(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\n" + senderId +"给" + getterId + "发送文件" + src + "到对方的电脑目录" + dest);
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

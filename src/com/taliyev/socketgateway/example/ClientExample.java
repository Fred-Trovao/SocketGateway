package com.taliyev.socketgateway.example;

import com.taliyev.socketgateway.exception.SocketGatewayException;
import com.taliyev.socketgateway.sender.SocketClient;
import com.taliyev.socketgateway.util.Client;

/*
 * Author: toghrul
 * Date: 08/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class ClientExample {

    public static void main(String[] args) {
        Client client = new Client();
        client.setHost("10.7.24.10");
        //client.setHost("10.40.24.5");
        client.setPort(3300);

        SocketClient socketClient = new SocketClient(client);
        socketClient.setStartCommand("PROCESS CaiServer1 CONNECTED...");
        //socketClient.setStartCommand("PROCESS cai3300 CONNECTED...");
        socketClient.connect();

        try {
            System.out.println("1: " + socketClient.getWelcomeMessage());
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }

        sleep();

        socketClient.sendCommand("");
        //socketClient.sendCommandWithResponse("");
        try {
            socketClient.readResponse();
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }
        //socketClient.readResponse();
        //socketClient.sendCommand("");

        //System.out.println("2: " + socketClient.readTillEnd());

        sleep();

        try {
            socketClient.sendCommandWithResponse("LOGIN: ituser:Md2017!@;");
            //socketClient.sendCommand("LOGIN: ituser:2014md!@#K;");
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }

        sleep();

        socketClient.ping();

        try {
            socketClient.readResponse();
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }

        sleep();

        try {
            socketClient.sendCommandWithResponse("GET:HLRSUB: MSISDN,994508391602;");
            //socketClient.sendCommand("GET:HLRSUB: MSISDN,994502310473;");
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }

        sleep();

        try {
            socketClient.sendCommandWithResponse("LOGOUT;");
        } catch (SocketGatewayException ex) {
            socketClient.closeSocket();
            ex.printStackTrace();
            return;
        }

        sleep();

        socketClient.closeSocket();
    }

    private static void sleep() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }

}

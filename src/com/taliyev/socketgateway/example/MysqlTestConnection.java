package com.taliyev.socketgateway.example;

import com.taliyev.socketgateway.db.mysql.MySQLConnection;
import com.taliyev.socketgateway.util.CastingOperations;
import com.taliyev.socketgateway.util.DBParams;
import com.taliyev.socketgateway.util.ServerParams;

import java.util.Date;

/*
 * Author: toghrul
 * Date: 12/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class MysqlTestConnection {

    public static void main(String[] args) {

        DBParams dbParams = new DBParams();

        dbParams.setUsername("root");
        dbParams.setPassword("Tgr@2310473");
        dbParams.setHost("10.3.2.10");
        dbParams.setPort(3306);
        dbParams.setDatabaseName("hlr_hss");
        dbParams.setDatabaseType("MySQL");

        ServerParams.setDbParams(dbParams);

        MySQLConnection conn = new MySQLConnection();

        try {

            for (int i = 0; i < 12; i++) {
                Integer id = conn.getNextRequestId();
                System.out.println("Request id: " + id);

                conn.insert(id, "test", new Date(), new Date(), "resp1", "Ok");
                System.out.println("Request registered with id: " + id);
            }

            conn.registerResponse("ema15", null, -1, "RESP:0;", new Date(), new Date());
            System.out.println("Response registered in EMA 15 ...");

            conn.registerResponse("ema16", null, -1, "RESP:1;", new Date(), new Date());
            System.out.println("Response registered in EMA 15 ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

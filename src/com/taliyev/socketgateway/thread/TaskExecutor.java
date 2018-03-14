package com.taliyev.socketgateway.thread;

import com.taliyev.socketgateway.exception.SocketGatewayException;
import com.taliyev.socketgateway.util.ConstantsEnum;

import java.util.Date;

/*
 * Author: toghrul
 * Date: 14/01/18
 * Project: SocketGateway
 * Email: togrul88@gmail.com
 */
public class TaskExecutor implements Runnable {

    // Action type
    private ConstantsEnum actionType;

    // Request Parameters
    private String request;
    private Date requestStartTime;
    private Date requestFinishTime;
    private String requestResponse;
    private String requestStatus;

    // Response parameters
    private String responseNodeType;
    private Integer responseId;
    private Integer responseRequestId;
    private String response;
    private Date responseStartTime;
    private Date responseFinishTime;

    // MySQL
    public TaskExecutor() {

    }

    // Set executor parameters for registering request
    public void setRequestParameters(String request, Date startTime, Date finishTime, String response, String status) {
        this.actionType = ConstantsEnum.EXECUTOR_PARAM_INSERT_REQUEST;
        this.request = request;
        this.requestStartTime = startTime;
        this.requestFinishTime = finishTime;
        this.requestResponse = response;
        this.requestStatus = status;
    }

    // Set executor parameters for registering request
    public void setRequestParameters(String nodeType, Integer id, Integer requestId, String response, Date startTime, Date finishTime) {
        this.actionType = ConstantsEnum.EXECUTOR_PARAM_INSERT_RESPONSE;
        this.responseNodeType = nodeType;
        this.responseId = id;
        this.responseRequestId = requestId;
        this.response = response;
        this.responseStartTime = startTime;
        this.responseFinishTime = finishTime;
    }


    @Override
    public void run() {

    }
}

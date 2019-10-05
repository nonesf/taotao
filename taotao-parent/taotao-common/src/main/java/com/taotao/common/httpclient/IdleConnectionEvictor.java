package com.taotao.common.httpclient;

import org.apache.http.conn.HttpClientConnectionManager;

public class IdleConnectionEvictor extends Thread {
    private  final HttpClientConnectionManager connectionManager;
    private volatile boolean shutdown;

    public IdleConnectionEvictor(HttpClientConnectionManager hm){
        this.connectionManager = hm ;
        //启动当前线程
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // 关闭失效的连接
                    connectionManager.closeExpiredConnections();
                }
            }
        } catch (InterruptedException ex) {
            // 结束
        }
    }
}

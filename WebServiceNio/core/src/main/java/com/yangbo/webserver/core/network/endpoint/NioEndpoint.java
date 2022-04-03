package com.yangbo.webserver.core.network.endpoint;

import com.yangbo.webserver.core.network.connector.nio.IdleConnectionCleaner;
import com.yangbo.webserver.core.network.connector.nio.NioAcceptor;
import com.yangbo.webserver.core.network.connector.nio.NioPoller;
import com.yangbo.webserver.core.network.dispatcher.NioDispatcher;
import com.yangbo.webserver.core.network.wrapper.NioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yangbo
 * @Date: 2022-03-19-17:03
 * @Description: NIO
 */
@Slf4j
public class NioEndpoint extends Endpoint {
    private int pollerCount =Math.min(2,Runtime.getRuntime().availableProcessors());

    private ServerSocketChannel server;
    private volatile boolean isRunning = true;
    private NioAcceptor nioAcceptor;
    private NioDispatcher nioDispatcher;
    private List<NioPoller> nioPollers;

    //poller轮询器
    private AtomicInteger pollerRotater = new AtomicInteger(0);

    //1min
    private int keepAliveTime = 6 * 1000;

    //针对keepAlive  如果长时间没有数据连接  则关闭
    private IdleConnectionCleaner cleaner;

    public int getKeepAliveTime(){
        return keepAliveTime;
    }


    //-------------------------初始化--------------------------------------
    private void initDispatcherServlet(){nioDispatcher = new NioDispatcher();}

    private void initServletSocket() throws IOException{
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8888));
        //设置阻塞模式
        server.configureBlocking(true);
    }

    private void initPoller() throws Exception {
        nioPollers = new ArrayList<>(pollerCount);
        for (int i = 0; i < pollerCount; i++) {
            String pollerName = "NioPoller-"+i;
            NioPoller nioPoller = new NioPoller(this,pollerName);
            Thread pollerThread = new Thread(nioPoller,pollerName);
            //设置为守护线程  ------
            pollerThread.setDaemon(true);
            pollerThread.start();
            nioPollers.add(nioPoller);
        }
    }
    
    //初始化acceptor
    private void initAcceptor(){
        this.nioAcceptor = new NioAcceptor(this);
        Thread thread = new Thread(nioAcceptor,"NioAcceptor");
        thread.setDaemon(true);
        thread.start();
    }
    
    //初始化IdleSocketCleaner
    private void initIdleSocketCleaner(){
        cleaner = new IdleConnectionCleaner(nioPollers);
        cleaner.start();
    }
    
    //-------------------------初始化结束--------------------------------------


    @Override
    public void start(int port) {
        try {
            initDispatcherServlet();
            initServletSocket();
            initPoller();
            initAcceptor();
            initIdleSocketCleaner();
            log.info("服务器启动");
        }catch (Exception e){
            e.printStackTrace();
            log.info("服务器启动失败");
            close();
        }
    }

    @Override
    public void close() {
        isRunning = false;
        cleaner.shutdown();
        for (NioPoller nioPoller:nioPollers
             ) {
            try {
                nioPoller.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        nioDispatcher.shutdown();
        try {
            server.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    
    /**
     * 调用dispatcher，处理这个读已就绪的客户端连接
     * @param socketWrapper
     */
    public void execute(NioSocketWrapper socketWrapper) {
        nioDispatcher.doDispatcher(socketWrapper);
    }
    

    public SocketChannel acceptor() throws IOException {
        return server.accept();
    }

    public void registerToPoller(SocketChannel socketChannel) throws IOException {
        server.configureBlocking(false);
        getPoller().register(socketChannel,true);
        server.configureBlocking(true);
    }

    /**
     * 轮询Poller，实现负载均衡
     * @return
     */
    private NioPoller getPoller() {
        int idx = Math.abs(pollerRotater.incrementAndGet()) % nioPollers.size();
        return nioPollers.get(idx);
    }

    public boolean isRunning() {
        return isRunning;
    }
}

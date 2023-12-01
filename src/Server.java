import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Server {
public static Map<String,Socket> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8081);
        while(true)
        {
            //建立连接
            Socket socket = server.accept();
            System.out.println("连接成功");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            String str = in.readLine();
            map.put(str,socket);

            for(String s:Server.map.keySet())
            {
                PrintWriter o = new PrintWriter(new BufferedWriter(new OutputStreamWriter(map.get(s).getOutputStream())),true);
                o.println(str + "已进入聊天室");
            }
            String name = "member-";
            for(String s:Server.map.keySet())
            {
                name += s+"-";
            }
            for(String s:Server.map.keySet())
            {
                PrintWriter o = new PrintWriter(new BufferedWriter(new OutputStreamWriter(map.get(s).getOutputStream())),true);
                System.out.println(name);
                o.println(name);
            }

            ServerThread serverThread = new ServerThread(socket,str);
            serverThread.start();
        }
    }
}


class ServerThread extends Thread
{
    private Socket socket;
    private String name;

    public ServerThread(Socket socket,String name) {
        this.socket = socket;
        this.name = name;
    }

    public void run() {
        BufferedReader in = null;
        try {
            //无限向客户端读取客户端的数据
            while(true) {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                String msg;
                int i = 0;
                while((msg = in.readLine())!=null) {
                    if (msg.equals("$END")) {
                        socket.close();
                        System.out.println(name + "关闭了链接");
                        Server.map.remove(name);
                        String name = "member-";
                        //有人离开，右侧栏需要修改

                        for(String s:Server.map.keySet())
                        {
                            name += s+"-";
                        }
                        for(String s:Server.map.keySet())
                        {
                            PrintWriter o = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Server.map.get(s).getOutputStream())),true);
                            System.out.println(name);
                            o.println(name);
                        }
                        return;
                    }
                    System.out.println("要发送群发消息" + msg);
                    for (String s : Server.map.keySet()) {
                        PrintWriter o = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Server.map.get(s).getOutputStream())), true);
                        if(i==0) {
                            o.println(name + ":" + msg);
                        }
                        else
                        {
                            o.println(msg);
                        }
                    }
                    i++;
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //输出msg
    }
}

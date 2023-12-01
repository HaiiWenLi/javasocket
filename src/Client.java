import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client{
    static Socket socket ;
    JPanel jp1 = new JPanel();
    JPanel jp2 = new JPanel();
    JPanel jp3 = new JPanel();
    JPanel jp4 = new JPanel();
    JPanel jp5 = new JPanel();
    JPanel jp6 = new JPanel();
    JLabel jl1;
    JLabel jl2;
    JTextArea ja1;
    JTextArea ja2;
    JTextArea jt;
    JButton jb;
    public Client(String s) throws HeadlessException, IOException {
        socket =  new Socket("127.0.0.1",8081);
        JFrame jf = new JFrame();
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println("$END");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        jf.setBounds(500,200,1000,800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.BOTH;
        jf.setLayout(gbl);
        gbl.columnWidths = new int[]{0,0};
        gbl.rowHeights = new int[]{0,0,0};
        gbl.columnWeights = new double[]{0.7,0.3};
        gbl.rowWeights = new double[]{0.05,0.6,0.35};
        gbc.fill=GridBagConstraints.BOTH;
        panel1();
        panel2();
        panel3();
        panel4();
        panel5();
        panel6();
        Add_Component(jf,gbl,jp1,gbc,0,0);
        Add_Component(jf,gbl,jp2,gbc,1,0);
        Add_Component(jf,gbl,jp3,gbc,0,1);
        Add_Component(jf,gbl,jp4,gbc,1,1);
        Add_Component(jf,gbl,jp5,gbc,0,2);
        Add_Component(jf,gbl,jp6,gbc,1,2);
        jf.setVisible(true);

    }


    public static void Add_Component(JFrame jfr,GridBagLayout gbl,Component comp,GridBagConstraints gbc,int gridx,int gridy)
    {
        gbc.fill=GridBagConstraints.BOTH;
        gbc.gridx=gridx;
        gbc.gridy=gridy;
        gbl.setConstraints(comp, gbc);
        jfr.add(comp);
    }
    private void panel1()
    {
        jl1 = new JLabel("聊天记录",JLabel.CENTER);
        jp1.add(jl1,BorderLayout.CENTER);
    }
    private void panel2()
    {
        jl2 = new JLabel("成员列表");
        jp2.add(jl2,BorderLayout.CENTER);
    }
    private void panel3()
    {
        ja1 = new JTextArea("*******************************************************欢迎来到聊天室*************************************************\n",30,65);

        ja1.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(ja1);
        ja1.setEditable(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jp3.add(scroll);
    }
    private void panel4()
    {
        ja2 = new JTextArea(30,35);
        ja2.setEditable(false);
        jp4.add(ja2);
    }
    private void panel5()
    {
        jt = new JTextArea();
        jt.setPreferredSize(new Dimension (590,200));
        jp5.add(jt);
    }
    private void panel6()
    {
        jb = new JButton("发送");
        ButtonListener blistener = new ButtonListener();
        jb.addActionListener(blistener);
        jp6.setLayout(null);
        jp6.add(jb);
        jb.setBounds(0, 150, 150, 50);
    }

    class ButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = jt.getText();
            jt.setText("");
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.print(msg+"\n");
                out.flush();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }



        }
    }

    public static void main(String[] args) throws IOException {
        String input = JOptionPane.showInputDialog("请输入昵称:");
        Client c = new Client(input);
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        out.println(input);
        ReadThread rthread = new ReadThread(socket,c.ja1,c.ja2);
        rthread.start();






    }
}

class ReadThread extends Thread
{
    private Socket socket;
    JTextArea ja1;
    JTextArea ja2;

    public ReadThread(Socket socket, JTextArea ja1, JTextArea ja2) {
        this.socket = socket;
        this.ja1 = ja1;
        this.ja2 = ja2;
    }

    @Override
    public void run() {
        while(true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                String recieve;
                while((recieve = in.readLine())!=null) {
                    System.out.println("收到了信息：" + recieve);
                    String[] split = recieve.split("-");
                    if (split[0].equals("member")) {
                        ja2.setText("");
                        for (int i = 1; i < split.length; i++) {
                            ja2.append(split[i] + "\n");
                        }
                    } else {
                        ja1.append("\n");
                        for (int i = 0; i < split.length; i++) {
                            ja1.append(split[i]);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
//
//class WriteThread extends Thread
//{
//    private Socket socket;
//    JTextArea jt;
//    JButton jb;
//
//    public WriteThread(Socket socket, JTextArea jt, JButton jb) {
//        this.socket = socket;
//        this.jt = jt;
//        this.jb = jb;
//    }
//
//
//
//    @Override
//    public void run() {
//
//    }
//}


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.text.DefaultCaret;
import java.util.Vector;

class ServerThread implements Runnable {
    public Socket socket;
    static Vector<Socket> v1=new Vector<Socket>();
    public ServerThread (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            v1.addElement(socket);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str = br.readLine();
                System.out.println(str);
                gui.appendText(str);
                if(str.split("\\:")[1].equals("bye")){
                    socket.close();
                    System.out.println(str.split(":")[0]+" 退出了聊天室");
                    gui.appendText(str.split(":")[0]+" 退出了聊天室");
                    v1.removeElement(socket);
                }
                for(Socket socket:v1) {
                    try {
                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        pw.println(str);
                        pw.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendText(String str) {
        System.out.println(gui.getName() + ":" + str);
        for(Socket socket:v1) {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println(gui.getName() + ":" + str);
                pw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

public class chatServer {
    public static void main(String args[]) throws Exception {
        gui.gui();
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("服务器启动成功");
        while (true) {
            Socket socket= serverSocket.accept();
            System.out.println("上线通知： " + socket.getInetAddress() + ":" +socket.getPort());
            new Thread(new ServerThread(socket)).start();
        }
    }
}

class gui {
    //文本区域
    final static JTextArea textArea = new JTextArea(25, 10);
    final static JTextField textField = new JTextField("服务器",5);
    final static JTextField textField1 = new JTextField(15);
    static JScrollPane panel = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


    public static void gui()   {
        JFrame jf = new JFrame("聊天程序服务端");
        jf.setSize(500, 500);

        //文本框
        JPanel panel1 = new JPanel();
        JLabel jLabel1 = new JLabel();
        jLabel1.setText("昵称：");

        JLabel jLabel2 = new JLabel();
        jLabel2.setText("内容：");

        panel1.add(jLabel1);
        panel1.add(textField);
        panel1.add(jLabel2);
        panel1.add(textField1);
        JButton btn = new JButton("发送");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerThread.sendText(textField1.getText());
                gui.appendText("服务器："+textField1.getText());
            }
        });
        panel1.add(btn);
        Box vBox = Box.createVerticalBox();
        vBox.add(panel);
        vBox.add(panel1);

        jf.setContentPane(vBox);
        jf.setVisible(true);
    }

    public static void appendText(String str){
        gui.textArea.append(str+"\n");
    }

    public static String getName() {
        return textField.getText();
    }
}


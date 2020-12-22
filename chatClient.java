import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class chatClient {
    static PrintWriter pw ;
    static Socket socket;


    public static void main(String args[]) throws Exception {
        guiClient.gui();
        Socket socket = new Socket("localhost", 9999);
        chatClient.socket=socket;
        System.out.println("连接成功");
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        chatClient.pw=pw;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str = br.readLine();
                System.out.println(str);
                guiClient.appendText(str);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }


    }

    public static void sendText(String str) {
        try{
            System.out.println(guiClient.getName() + ":" + str);
            pw.println(guiClient.getName() +":"+str);
            pw.flush();
            if(str.split(":")[0].equals("bye")){
                socket.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

class guiClient {
    //文本区域
    final static JTextArea textArea = new JTextArea(25, 10);
    static JScrollPane panel = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    final static JTextField textField = new JTextField(5);
    final static JTextField textField1 = new JTextField(15);

    public static void gui() {
        JFrame jf = new JFrame("聊天室客户端");
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
                chatClient.sendText(textField1.getText());
            }
        });
        panel1.add(btn);
        Box vBox = Box.createVerticalBox();
        vBox.add(panel);
        vBox.add(panel1);

        jf.setContentPane(vBox);
        jf.setVisible(true);
    }

    public static void appendText(String str) {
        guiClient.textArea.append(str + "\n");
    }

    public static String getName() {
        return textField.getText();
    }
}
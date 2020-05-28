import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.ByteBuffer;

public class Sender extends Application {

    // Stage window;
    // Button button;
    // Socket socket;
    // Scanner scanner = new Scanner(System.in);
    // int portVal;
    // String ipVal;

    Stage window;
    // Button button;
    // Socket socket;
    // BufferedReader in;
    // PrintWriter out;
    // Scanner scanner = new Scanner(System.in);
    String ipVal;
    int sndPortVal;
    int rcvPortVal;
    String filenameVal;
    int mdsVal;
    int timeoutVal;
    BufferedReader r;

    // boolean connect = false;
    // boolean been = false;

    public static void main(String[] args) throws Exception {

        launch(args);

    }

    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Sender Window");

        GridPane grid = new GridPane();
        grid.setGridLinesVisible(false);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label ipLbl = new Label("IP Address: ");
        GridPane.setConstraints(ipLbl, 0, 0);

        TextField ipInput = new TextField();
        ipInput.setPromptText("IP Address");
        GridPane.setConstraints(ipInput, 1, 0);
        GridPane.setColumnSpan(ipInput, 3);
        ipInput.setPrefSize(150, 1);

        Label sndLbl = new Label("Sender Port: ");
        GridPane.setConstraints(sndLbl, 0, 1);

        TextField sndPortInput = new TextField();
        sndPortInput.setPromptText("Port #");
        GridPane.setConstraints(sndPortInput, 1, 1);
        sndPortInput.setPrefSize(50, 1);

        Label rcvLbl = new Label("Receiver Port: ");
        GridPane.setConstraints(rcvLbl, 2, 1);

        TextField rcvPortInput = new TextField();
        rcvPortInput.setPromptText("Port #");
        GridPane.setConstraints(rcvPortInput, 3, 1);
        rcvPortInput.setPrefSize(50, 1);

        Label fileLbl = new Label("File Name: ");
        GridPane.setConstraints(fileLbl, 0, 2);

        TextField fileNameInput = new TextField();
        fileNameInput.setPromptText("Output File Name");
        GridPane.setConstraints(fileNameInput, 1, 2);
        GridPane.setColumnSpan(fileNameInput, 3);
        fileNameInput.setPrefSize(150, 1);

        Label sizeLbl = new Label("Max Size: ");
        GridPane.setConstraints(sizeLbl, 0, 3);

        TextField maxSizeInput = new TextField();
        maxSizeInput.setPromptText("(datagram size)");
        GridPane.setConstraints(maxSizeInput, 1, 3);
        maxSizeInput.setPrefSize(100, 1);

        Label timeLbl = new Label("Timeout: ");
        GridPane.setConstraints(timeLbl, 2, 3);

        TextField timeoutInput = new TextField();
        timeoutInput.setPromptText("(microseconds)");
        GridPane.setConstraints(timeoutInput, 3, 3);
        timeoutInput.setPrefSize(100, 1);

        Label transLbl = new Label("Total Transmission Time: ");
        GridPane.setConstraints(transLbl, 0, 4);
        GridPane.setColumnSpan(transLbl, 2);

        TextField transOutput = new TextField();
        transOutput.setEditable(false);
        GridPane.setConstraints(transOutput, 0, 5);
        transOutput.setPrefSize(50, 1);

        Button transferButton = new Button("TRANSFER");
        GridPane.setConstraints(transferButton, 3, 5);

        grid.getChildren().addAll(fileLbl, transOutput, transLbl, ipLbl, transferButton, timeLbl, sizeLbl, rcvLbl,
                sndLbl, timeoutInput, maxSizeInput, fileNameInput, rcvPortInput, sndPortInput, ipInput);
        Scene scene = new Scene(grid, 400, 250);
        window.setScene(scene);
        window.show();

        transferButton.setOnAction(e -> {
            // ipVal = String.valueOf(ipInput.getText());

            // filenameVal = String.valueOf(fileNameInput.getText());
            // sndPortVal = Integer.parseInt(String.valueOf(sndPortInput.getText()));
            // rcvPortVal = Integer.parseInt(String.valueOf(rcvPortInput.getText()));
            // mdsVal = Integer.parseInt(String.valueOf(maxSizeInput.getText()));
            // timeoutVal = Integer.parseInt(String.valueOf(timeoutInput.getText()));

            DatagramSocket skt;
            FileInputStream fis;
            // FileWriter fw;
            try {
                skt = new DatagramSocket();
                int sendSocket = 6788;
                int mds = 20;
                boolean reliable = true;
                int timeout = 10;
                String ip = "127.0.0.1";
                InetAddress host = InetAddress.getByName(ip);
                String fname = "text.txt";

                RandomAccessFile file = new RandomAccessFile(fname, "rw");
                long fileLen = file.length();
                String temp = "";
                System.out.println(fileLen);
                while (fileLen % mds != 0) {
                    file.seek(fileLen);
                    file.writeBytes(" ");
                    fileLen = file.length();
                    // System.out.println(fileLen);
                }
                file.close();
                file = new RandomAccessFile(fname, "r");

                // System.out.println(fileLen);

                byte[] filebyte = new byte[(int) file.length()];

                file.readFully(filebyte);
                file.close();
                List<byte[]> fbyteArr = divideArray(filebyte, mds);
                // System.out.println(Arrays.toString(fbyteArr));
                String maxq = "" + fbyteArr.size();
                String synmsg = sendSocket + " " + mds + " " + reliable + " " + maxq + " a";
                byte[] synmsgb = synmsg.getBytes();
                DatagramPacket syn = new DatagramPacket(synmsgb, synmsgb.length, host, sendSocket);
                skt.send(syn);

                byte[] buffer = new byte[mds + 2];
                DatagramPacket acksyn = new DatagramPacket(buffer, buffer.length);
                skt.receive(acksyn);

                int recSocket = acksyn.getPort();
                String ackmsg = "ack #";
                DatagramPacket ack = new DatagramPacket(buffer, buffer.length);
                buffer = new byte[mds + 3];
                ByteArrayOutputStream outputStream;
                String seqnum;
                byte[] seqbyte;
                byte[] recmsg = new byte[20];
                boolean ackbool = false;
                for (int i = 0; i < fbyteArr.size(); i++) {
                    System.out.println("current i=" + i);
                    seqnum = "" + i;
                    seqbyte = seqnum.getBytes();
                    outputStream = new ByteArrayOutputStream();
                    outputStream.write(seqbyte);
                    outputStream.write(fbyteArr.get(i));
                    byte[] datamsg = outputStream.toByteArray();
                    DatagramPacket datasend = new DatagramPacket(datamsg, datamsg.length, host, sendSocket);
                    DatagramPacket datarec = new DatagramPacket(recmsg, recmsg.length);
                    skt.send(datasend);
                    skt.setSoTimeout(1 * 1000);
                    // fix the timeout properly
                    while (true) {
                        try {
                            skt.receive(datarec);
                            break;
                        } catch (SocketTimeoutException exp) {
                            skt.send(datasend);
                        }
                    }
                }

            } catch (Exception exp) {

            }

        });

    }

    public static List<byte[]> divideArray(byte[] source, int chunksize) {

        List<byte[]> result = new ArrayList<byte[]>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }

        return result;
    }

    public static byte[] shortToByte(short value) {
        byte[] bytes = new byte[2];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putShort(value);
        return buffer.array();
    }
}

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

public class Receiver extends Application {

    Stage window;
    String ipVal;
    int sndPortVal;
    int rcvPortVal;
    String filenameVal;
    String protocolVal;
    boolean open = true;

    public static void main(String args[]) throws Exception {

        launch(args);

    }

    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Receiver Window");

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
        fileNameInput.setPromptText("Input File Name");
        GridPane.setConstraints(fileNameInput, 1, 2);
        GridPane.setColumnSpan(fileNameInput, 3);
        fileNameInput.setPrefSize(150, 1);

        ChoiceBox protocolType = new ChoiceBox();
        protocolType.getItems().add("Reliable");
        protocolType.getItems().add("Unreliable");
        GridPane.setConstraints(protocolType, 3, 4);
        protocolType.setPrefSize(70, 2);

        Label protoLbl = new Label("Protocol Type: ");
        GridPane.setConstraints(protoLbl, 2, 4);

        Label pktLbl = new Label("# of packets received: ");
        GridPane.setConstraints(pktLbl, 0, 4);
        GridPane.setColumnSpan(pktLbl, 2);

        TextField pktOutput = new TextField();
        pktOutput.setPromptText("0 packets");
        pktOutput.setEditable(false);
        GridPane.setConstraints(pktOutput, 0, 5);
        pktOutput.setPrefSize(50, 1);

        Button okButton = new Button("Ok");
        GridPane.setConstraints(okButton, 3, 5);

        grid.getChildren().addAll(okButton, protocolType, protoLbl, fileLbl, pktOutput, pktLbl, ipLbl, rcvLbl, sndLbl,
                fileNameInput, rcvPortInput, sndPortInput, ipInput);
        Scene scene = new Scene(grid, 350, 220);
        window.setScene(scene);
        window.show();

        okButton.setOnAction(e -> {
            // ipVal = String.valueOf(ipInput.getText());
            // sndPortVal = Integer.parseInt(String.valueOf(sndPortInput.getText()));
            // rcvPortVal = Integer.parseInt(String.valueOf(rcvPortInput.getText()));
            // filenameVal = String.valueOf(fileNameInput.getText());
            // protocolVal = String.valueOf(protocolType.getValue());

            ipVal = "127.0.0.1";
            sndPortVal = 1000;
            rcvPortVal = 9999;
            filenameVal = "output.txt";
            protocolVal = "reliable";

            try {
                DatagramSocket ds = new DatagramSocket(rcvPortVal);

                byte[] b1 = new byte[1024];

                DatagramPacket dp = new DatagramPacket(b1, b1.length);
                ds.receive(dp);
                String str = new String(dp.getData());

                System.out.println("Received: " + str);

                byte[] b2 = (str + "").getBytes();
                InetAddress ia = InetAddress.getLocalHost();
                DatagramPacket dp1 = new DatagramPacket(b2, b2.length, ia, dp.getPort());

                ds.send(dp1);

            } catch (Exception error) {
                System.out.println("error!");
            }

        });

    }
}
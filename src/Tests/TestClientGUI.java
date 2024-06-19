import InterfacesGraficas.ClienteGUI;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

final public class TestClientGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Socket clientSocket = new Socket("localhost", 12000);
                new ClienteGUI(clientSocket, "TestUser");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao conectar ao servidor.");
            }
        });
    }
}

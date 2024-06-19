package InterfacesGraficas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

final public class ClienteGUI extends JFrame implements ActionListener {

    private JTextField inputTextField;
    private JTextArea chatArea;
    private JButton sendButton;
    private Socket clientSocket;
    private PrintStream outputStream;
    private String nome;
    private String lastSentMessage = "";
    private static final int MAX_MESSAGE_LENGTH = 2000;

    public ClienteGUI(Socket clientSocket, String nome) {
        this.clientSocket = clientSocket;
        this.nome = nome;

        try {
            outputStream = new PrintStream(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar o fluxo de saída.");
            return;
        }

        if (outputStream == null) {
            System.err.println("Erro: fluxo de saída não inicializado corretamente.");
            return;
        }

        initComponents();
        startMessageListener();
    }

    private void initComponents() {
        setTitle("JavaZap Cliente - " + nome);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        URL iconUrl = getClass().getResource("/Resources/icon.png");
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            setIconImage(icon.getImage());
        } else {
            System.err.println("Ícone não encontrado!");
        }

        PlanoDeFundo contentPane = new PlanoDeFundo("/Resources/clientGUI2.jpeg"); //Errei de propósito para testar o modo dark

        contentPane.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        if (!contentPane.isImageLoaded()) {
            chatArea.setBackground(Color.BLACK);
            chatArea.setForeground(Color.GREEN);
        }

        JScrollPane scrollPane = new JScrollPane(chatArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputTextField = new JTextField();
        sendButton = new JButton("Enviar");
        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(this);
        inputTextField.addActionListener(this);
        inputPanel.add(inputTextField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        contentPane.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
        setVisible(true);
        inputTextField.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton || e.getSource() == inputTextField) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String message = inputTextField.getText().trim();

        if (message.isEmpty()) {
            return;
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            JOptionPane.showMessageDialog(this,
                    "A mensagem excede o tamanho máximo permitido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        message = String.format("%s|%s[%s]: %s", LocalDate.now(), formattedTime, nome, message);
        chatArea.append(message + "\n");
        lastSentMessage = message;

        try {
            outputStream.println(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao enviar mensagem.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        inputTextField.setText("");
    }

    private void startMessageListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (!message.equals(lastSentMessage)) {
                            chatArea.append(message + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

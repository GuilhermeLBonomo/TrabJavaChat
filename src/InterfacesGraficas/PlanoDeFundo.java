package InterfacesGraficas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PlanoDeFundo extends JPanel {
    private Image backgroundImage;
    private boolean imageLoaded = false;

    public PlanoDeFundo(String imagePath) {
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                backgroundImage = new ImageIcon(imageUrl).getImage();
                imageLoaded = true;
            } else {
                System.err.println("Erro ao carregar imagem de fundo: Imagem não encontrada.");
            }
        } catch (Exception e) {
            System.err.printf("Erro ao carregar imagem de fundo: %s", e.getMessage());
        }
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

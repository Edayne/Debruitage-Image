import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;

public class Main {

	// ImageIO.read(PerformanceTest.class.getResource("donnees/lena.jpg"));
	public static void main(String[] args) throws IOException {
		Photo lena = new Photo("donnees/lena.png");

        //Bruitage de l'image
		lena.noising( 20);

        //Affichage d'une image
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhotoBruitee())));
        frame.pack();
        frame.setVisible(true);

        //Extraction des patchs et vectorisation
        List<int[][]> listPatches = lena.extractPatchs( 20);
        List<int[]> listVectPatch = lena.vectorPatchs(listPatches);

        
    }
}

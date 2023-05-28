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

public class MainTest {

	public static void main(String[] args) {
		ACP outilsACP = new ACP();
		
		System.out.println("1");
		ArrayList<int[]> listeVect = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			int[] vect = new int[3];
			for (int index = 0; index < 3; index++) {
				vect[index] = index;
			}
			listeVect.add(vect);
		}
		
		System.out.println("2");
		ArrayList<double[]> listeVectDouble = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			double[] vect = new double[3];
			for (int index = 0; index < 3; index++) {
				vect[index] = index;
				System.out.println(vect[index]);
			}
			listeVectDouble.add(vect);
		}
		
		double[][] base = new double[3][3];
		for (int i = 0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (i==j) {
					base[i][j] = 1.0;
				} else {
					base[i][j] = 0.0;
				}
			}
		}
		double[][] proj = outilsACP.proj(base, listeVectDouble);

		for (int i = 0; i < proj.length; i++) {
			System.out.print("[");
			for (int j = 0; j < proj[0].length; j++) {
				System.out.print(proj[i][j] + ", ");
			}
			System.out.println("]");
		}
	}

}

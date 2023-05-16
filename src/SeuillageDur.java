import java.util.ArrayList;

public class SeuillageDur{

    public ArrayList<Double> seuillageDur(double seuil, ArrayList<Double> coefficients){
        ArrayList<Double> resultat = new ArrayList<Double>(); //créer tableau résultat de même taille que coefficients et qui va stocker le résultat du seuillage dur
        int n = coefficients.size();
        
        for (int i = 0; i < n; i++){ // On parcours tous les itérés du tableau 
            if(Math.abs(coefficients.get(i)) <= seuil){
                resultat.add(0.0); //Si valeur absolue du coefficients est inférieure au seuil alors notre coefficients va valoir 
            }else{
                resultat.add(coefficients.get(i)); // Sinon il reste inchangé
            }
        }
        return resultat;
    }
}
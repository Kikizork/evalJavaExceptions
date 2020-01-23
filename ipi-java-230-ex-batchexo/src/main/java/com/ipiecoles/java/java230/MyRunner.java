package com.ipiecoles.java.java230;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.exceptions.TechnicienException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.model.Manager;
import com.ipiecoles.java.java230.model.Technicien;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;


// Pjvilloud@protonmail.com Eval exception : ipi U'Dev 2020 par Frédéric Enée @Kikizork pour @pjvilloud

@Component
public class MyRunner implements CommandLineRunner {

	/* Pair, Triplets viennent de javatupples : https://www.javatuples.org/maveninfo.html
	 * Comme j'ai fais du python en auto-apprentissage à CGI et que j'étais gavé du manque de fléxibilité 
	 * des HashMap j'ai utilisé ça c'est pas mal !
	 */
	 
    private static final String REGEX_MATRICULE = "^[MTC][0-9]{5}$";
    private static final Map <String, Triplet<Integer, String, Integer>> TYPES 
    		= new HashMap<String, Triplet<Integer, String, Integer>>()
    		{{put("M", new Triplet<Integer, String, Integer>(0, "manager", 5));
    		put("T", new Triplet<Integer, String, Integer>(1, "technicien", 7));
    		put("C", new Triplet<Integer, String, Integer>(2, "commercial", 7));
    		}};
    private static final String REGEX_NOM = ".*";
    private static final String REGEX_PRENOM = ".*";
    private static final Map<String, Integer> CHANGE = new HashMap<String,Integer>()
    		{{put(TECHNICIEN, 7);put(MANAGER, 5);put(COMMERCIAL, 7);}};
    private static final String REGEX_MATRICULE_MANAGER = "^M[0-9]{5}$";
    private static final String MANAGER = "manager";
    private static final String COMMERCIAL = "commercial";
    private static final String TECHNICIEN = "technicien";

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<Employe>();
    private Map <Integer, Triplet<Technicien, String, String[]>> technicienTemps = 
    		new HashMap<Integer, Triplet<Technicien, String, String[]>>();
    private Integer count = 1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* main fonction : appelle les différents composants du programme. 
     * Lit en entrée un fichier csv contenant des employés à insérer en bdd.
     * Produit une list d'employés prêts à être insérés en bdd mais ne le fait pas 
     * pour éviter des erreurs sur les contraintes d'unicité et pourrir la bdd.
     * Génére via catch throw une liste des lignes non conformes au format objet avec un message correspondant
     * */
    @Override
    public void run(String... strings) throws Exception {
        String fileName = "employes.csv";
        try {
	        List<Employe> inserts = readFile(fileName);
	        /* Pour gérer le cas d'un technicien dont le manager n'est pas en bdd mais va l'être dans le batch
	         * en cours. Insert le technicien en fin de liste.
	         */
	        for (Map.Entry<Integer, Triplet<Technicien, String, String[]>> technicienTemp : technicienTemps.entrySet()) {
	        	// Juste envie de montrer que je connais le .stream() pour le plaisir !
	        	if(inserts.stream()
	        	.filter(insert -> insert.getMatricule().equals(technicienTemp.getValue().getValue(1)))
	        	.count() != 0) {        		
	        		inserts.add((Employe) technicienTemp.getValue().getValue(0));
	        	} else {
	        		System.out.println("Ligne "+technicienTemp.getKey()+" : le manager de matricule "
	        				+technicienTemp.getValue().getValue(1)
	        				+"n'a pas été trouvé dans le fichier ou en base de données => "
	        				+String.join(",", (CharSequence[]) technicienTemp.getValue().getValue(2)));
	        	}
	        	
	        }
	        System.out.println("Les employés crées sont :");
	        for (Employe insert : inserts) {
	        	System.out.println(insert);
        } // Gére probablement les 2 exceptions les plus probables en cas de mauvaise lecture de fichier
        } catch (FileNotFoundException e) {
        	System.out.println("Erreur : le fichier "+fileName+" n'a pas pu être trouvé");
        } catch (IOException i) {
        	System.out.println("Erreur : le fichier"+fileName+" n'a pas pu être lu");
        }
        
        //readFile(strings[0]);
        
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        try {
        stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        for(String ligne : stream.collect(Collectors.toList())) {
        	try {
        	processLine(ligne);
        	}catch(BatchException exception) {
        		System.out.println("Ligne "+count+" : "+exception.getMessage());
        	}
        	count++;
        }
        return employes;
        } catch (IOException i) {
        	System.out.println("Erreur lors de la lecture de la ligne "+count);
        	return null;
        }
    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws BatchException {
        String [] splitLignes = ligne.split(",");
        Integer type = -1;
        if (splitLignes.length == 0) {
        	throw new BatchException("erreur de lecture : ligne vide");
        }
        if (!TYPES.containsKey(splitLignes[0].substring(0,1))) {
        	throw new BatchException("Type d'employé inconnu : "
        	+splitLignes[0].substring(0,1)+" => "+ligne);
        }
        if (!splitLignes[0].matches(REGEX_MATRICULE)) {
        	throw new BatchException("la chaîne "+splitLignes[0]+ 
        	" ne respecte pas l'expression régulière ^[MTC][0-9]{5}$ => "+ligne);        	
        }
        // 
        checkLength(splitLignes, (String) TYPES.get(splitLignes[0].substring(0,1)).getValue(1), 
        		(Integer)TYPES.get(splitLignes[0].substring(0,1)).getValue(2));
        type = (Integer) TYPES.get(splitLignes[0].substring(0,1)).getValue(0);
        switch (type) {
        	case 0 : 
        		try {
        			processManager(splitLignes);
        			} catch (BatchException b) {
                		throw new BatchException(b.getMessage()+ " => "+ligne);
                	}
        		break;
        	case 1 :
        		try {
    				processTechnicien(splitLignes); 
    			} catch (BatchException b) {
    	    		throw new BatchException(b.getMessage()+ " => "+ligne);
    	    	} catch (TechnicienException e) {
					// ça n'arrive jamais mais java veut ça !
					e.printStackTrace();
				}
        		break;
        	case 2 :
        		try {
            		processCommercial(splitLignes);
            	} catch (BatchException b) {
            		throw new BatchException(b.getMessage()+ " => "+ligne);
            	}
        		break;
        	default : 
        		throw new BatchException("Une erreur de lecture de ligne a eu lieu => "+ligne);
        }  
    }

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String [] ligneCommercial) throws BatchException {
    	Double ca = 0d;
    	Integer perf = 0;
    	try {
    		ca = checkDouble(ligneCommercial[5]);
    	} catch (BatchException b) {
    		throw new BatchException("Le chiffre d'affaire du commercial est incorrect : "+ligneCommercial[5]);
    	}
    	try {
    		perf = checkInteger(ligneCommercial[6]);
    	} catch (BatchException b) {
    		throw new BatchException("La performance du commercial est incorrecte : "+ligneCommercial[6]);
    	}			
            Commercial c = new Commercial(ligneCommercial[0], ligneCommercial[1], ligneCommercial[2], 
            		checkDate(ligneCommercial[3]), checkDouble(ligneCommercial[4]), 
            		ca, perf);
            employes.add(c);       
    }

    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String [] ligneManager) throws BatchException {
    	Manager m = new Manager(ligneManager[1],ligneManager[2],ligneManager[0],
    			checkDate(ligneManager[3]),checkDouble(ligneManager[4]), null);
	    employes.add(m);
    	}
    

    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     * @throws TechnicienException 
     */
    private void processTechnicien(String [] ligneTechnicien) throws BatchException, TechnicienException {
    	int grade = 0;
    	try {
    		grade = checkInteger(ligneTechnicien[5]);
    	} catch (BatchException b) {
    		throw new BatchException("Le grade du technicien est incorrect : "+ligneTechnicien[5]);
    	}
    	if (grade < 1 || grade > 5) {
    		throw new BatchException("Le grade doit être compris entre 1 et 5 : "+grade);
    	}
    	if (!ligneTechnicien[6].matches(REGEX_MATRICULE_MANAGER)) {
    		throw new BatchException("la chaine "+ligneTechnicien[6]+ " ne respecte pas l'expression régulière ^M[0-9]{5}$");
    	} 
    	if (managerRepository.findByMatricule(ligneTechnicien[6]) != null) { 
    		Technicien t = new Technicien(ligneTechnicien[1],ligneTechnicien[2],ligneTechnicien[0],
    				checkDate(ligneTechnicien[3]),checkDouble(ligneTechnicien[4]),grade);
    		employes.add(t);
    	} else {
    		Technicien t = new Technicien(ligneTechnicien[1],ligneTechnicien[2],ligneTechnicien[0],
        			checkDate(ligneTechnicien[3]),checkDouble(ligneTechnicien[4]),grade);    		
    		technicienTemps.put(count, new Triplet<Technicien, String, String[]>
    				(t, ligneTechnicien[6], ligneTechnicien));
    	}
    }
    
    private LocalDate checkDate(String date) throws BatchException{
    	DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");	
        try {
        	LocalDate temp = LocalDate.parse(date, formatter);
        	return temp;
        } catch (Exception e) {
            throw new BatchException((date+" ne respecte pas le format de date dd/MM/yyyy"));
        }
    }
    
    private void checkLength(String [] lignes, String type, Integer typeLength) throws BatchException{
    	if (lignes.length != typeLength) {
    		throw new BatchException("La ligne "+type+" ne contient pas "
    	    	    +typeLength+" éléments mais "+lignes.length);
    	}
    }
    
    public static Double checkDouble(String s) throws BatchException{
        try { 
            return Double.parseDouble(s); 
        } catch(Exception e) { 
        	throw new BatchException(s+ " n'est pas un nombre valide pour un salaire");
        } 
    }
    public static Integer checkInteger (String s) throws BatchException{
    	try {
    		return Integer.parseInt(s);
    	} catch(Exception e) {
    		throw new BatchException("");
    	}
    }
}

# Exercices sur les algos et les exceptions

Le but de ce TP est de mettre en place les algorithmes nécessaires afin de pouvoir intégrer ce fichier CSV en BDD : 

```
M87654,durand,jacques,04/06/2013,1200.5
T98765,dupont,pierre,12/03/2003,1700.5,5,M87654
C32154,aubert,michel,05/09/2018,2200.5,150000.0,100
```

En particulier, il faudra faire attention à : 
- ne pas utiliser de `System.out.println`, utiliser les ```logger``` à la place.
- ce que le programme ne plante pas lorsqu'on essaye d'intégrer les fichiers de tests qui contiennent des lignes incorrectes, les exceptions et problèmes doivent être gérés correctement, seuls des affichages avec le ```logger.error``` sont tolérés.

Avant de commencer, il est nécessaire de supprimer le ```throws Exception``` de la méthode ```run``` et de ```readFile```

Essayer de gérer toutes les erreurs possibles auxquelles vous pensez en utilisant les exceptions.
Si une erreur est rencontrée sur une ligne, on doit afficher avec le logger en error :
 
```Ligne [X] : [message d'erreur] => [ligne problématique]```

Ex : ```Ligne 2 : 99/06/2013 ne respecte pas le format de date dd/MM/yyyy => M87654,durand,jacques,99/06/2013,1200.5```

Si une erreur est rencontrée sur une ligne, on n'affiche que la première erreur et on passe à la ligne suivante.

Essayer de ne pas écrire de méthodes trop longues, ne pas hésiter à créer des méthodes privées et essayer d'avoir le moins de duplication de code possible.

Fichiers à tester : Vérifier que toutes ces lignes ne font pas planter le programme et que la bonne erreur est levée à chaque fois

```
M87654,durand,jacques,04/06/2013,1200.5
T98765,dupont,pierre,12/03/2003,1700.5,5,M87654
T98764,dupont,pierre,12/03/2003,1700.5,5,M00001
C32154,aubert,michel,05/09/2018,2200.5,150000,100
XXXXXX,durand,jacques,04/06/2013,1200.5
M12,durand,jacques,04/06/2013,1200.5
M12345,durand,jacques,
M12345,durand,jacques,04/06/2013,1200.5,dfgdfg,dfgdfg
M12345,durand,jacques,04/99/2013,1200.5
M12345,durand,jacques,04/06/2013,sdf
C12,aubert,michel,05/09/2018,2200.5,150000.0,100
C12345,aubert,michel,05/09/2018,2200.5,150000.0
C12345,aubert,michel,05/09/2018,2200.5,150000.0,100,dfgdfg,dfgdfg
C12345,aubert,michel,05/09/XX,2200.5,150000.0,100
C12345,aubert,michel,05/09/2018,xxx,150000.0,100
C12345,aubert,michel,05/09/2018,2200.5,xxx,100
C12345,aubert,michel,05/09/2018,2200.5,150000.0,xxx
T12,dupont,pierre,12/03/2003,1700.5,5,M00001
T12345,dupont,pierre,12/03/2003,1700.5,5
T12345,dupont,pierre,12/03/2003,1700.5,5,M00001,dfgdfg,dfgdfg
T12345,dupont,pierre,12/03/XX,1700.5,5,M00001
T12345,dupont,pierre,12/03/2003,xxx,5,M00001
T12345,dupont,pierre,12/03/2003,1700.5,9,M00001
T12345,dupont,pierre,12/03/2003,1700.5,X,M00001
T12345,dupont,pierre,12/03/2003,1700.5,5,xxx
T12345,dupont,pierre,12/03/2003,1700.5,5,M99999
```

Produit :

```
Ligne 5 : Type d'employé inconnu : X => XXXXXX,durand,jacques,04/06/2013,1200.5
Ligne 6 : la chaîne M12 ne respecte pas l'expression régulière ^[MTC][0-9]{5}$ => M12,durand,jacques,04/06/2013,1200.5
Ligne 7 : La ligne manager ne contient pas 5 éléments mais 3 => M12345,durand,jacques,
Ligne 8 : La ligne manager ne contient pas 5 éléments mais 7 => M12345,durand,jacques,04/06/2013,1200.5,dfgdfg,dfgdfg
Ligne 9 : 04/99/2013 ne respecte pas le format de date dd/MM/yyyy => M12345,durand,jacques,04/99/2013,1200.5
Ligne 10 : sdf n'est pas un nombre valide pour un salaire => M12345,durand,jacques,04/06/2013,sdf
Ligne 11 : la chaîne C12 ne respecte pas l'expression régulière ^[MTC][0-9]{5}$ => C12,aubert,michel,05/09/2018,2200.5,150000.0,100
Ligne 12 : La ligne commercial ne contient pas 7 éléments mais 6 => C12345,aubert,michel,05/09/2018,2200.5,150000.0
Ligne 13 : La ligne commercial ne contient pas 7 éléments mais 9 => C12345,aubert,michel,05/09/2018,2200.5,150000.0,100,dfgdfg,dfgdfg
Ligne 14 : 05/09/XX ne respecte pas le format de date dd/MM/yyyy => C12345,aubert,michel,05/09/XX,2200.5,150000.0,100
Ligne 15 : xxx n'est pas un nombre valide pour un salaire => C12345,aubert,michel,05/09/2018,xxx,150000.0,100
Ligne 16 : Le chiffre d'affaire du commercial est incorrect : xxx => C12345,aubert,michel,05/09/2018,2200.5,xxx,100
Ligne 17 : La performance du commercial est incorrecte : xxx => C12345,aubert,michel,05/09/2018,2200.5,150000.0,xxx
Ligne 18 : la chaîne T12 ne respecte pas l'expression régulière ^[MTC][0-9]{5}$ => T12,dupont,pierre,12/03/2003,1700.5,5,M00001
Ligne 19 : La ligne technicien ne contient pas 7 éléments mais 6 => T12345,dupont,pierre,12/03/2003,1700.5,5
Ligne 20 : La ligne technicien ne contient pas 7 éléments mais 9 => T12345,dupont,pierre,12/03/2003,1700.5,5,M00001,dfgdfg,dfgdfg
Ligne 21 : 12/03/XX ne respecte pas le format de date dd/MM/yyyy => T12345,dupont,pierre,12/03/XX,1700.5,5,M00001
Ligne 22 : xxx n'est pas un nombre valide pour un salaire => T12345,dupont,pierre,12/03/2003,xxx,5,M00001
Ligne 23 : Le grade doit être compris entre 1 et 5 : 9, technicien : Technicien{grade=null} Employe{nom='null', prenom='null', matricule='null', dateEmbauche=null, salaire=1480.27} => T12345,dupont,pierre,12/03/2003,1700.5,9,M00001
Ligne 24 : Le grade du technicien est incorrect : X => T12345,dupont,pierre,12/03/2003,1700.5,X,M00001
Ligne 25 : la chaîne xxx ne respecte pas l'expression régulière ^M[0-9]{5}$ => T12345,dupont,pierre,12/03/2003,1700.5,5,xxx
Ligne 26 : Le manager de matricule M99999 n'a pas été trouvé dans le fichier ou en base de données => T12345,dupont,pierre,12/03/2003,1700.5,5,M99999
```

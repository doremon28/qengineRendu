### =============== Code Source ===============
https://github.com/doremon28/qengineRendu


### =============== How to start =============== 
Vous trouverez dans ce dossier:
1.  qengineRendu.rar: c'est le code source de l'application du moteur RDF
2.  qengineRendu_jar.rar: le fichier jar exécutable de l'application du moteur RDF
3.  qengineJenaRendu.rar: c'est le code source de l'application du Jenna


* Pour exécuter le programme, exécutez simplement le fichier jar "JDTParserProjects.jar" avec la commande: 
- java -jar .\qengineRendu.jar -d <Fichier des données> -q <Dossier des requetes> -o <Dossier de sortie pour générer les resultat statique> -e <Dossier de sortie pour générer les resultat des requetes>
- exemple : java -jar .\qengineRendu.jar -d D:\java\master-nosql\500K\data_500k.nt -q D:\java\master-nosql\500k\queries -o D:\java\master-nosql\g -e D:\java\master-nosql\g

### =============== IMPORTANT ===============
* Le fichier jar "qengineRendu.jar" doit imperativement rester dans la même dossier avec ses dépendances

* Pour exécuter le programme depuis le projet directement, vous pouvz l'exécuter la classe "MainTest" dans le package "package qengineRendu.program.MainTest" avec la configuration manuelle des argument.



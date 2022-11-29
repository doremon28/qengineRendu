package qengineRendu.program;

import qengineRendu.program.parser.Parser;
import qengineRendu.program.utils.Dictionary;
import qengineRendu.program.utils.FilePath;
import java.io.IOException;


public class MainTest {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
            FilePath fileManagement = new FilePath("D:\\java\\master-nosql\\qengineRendu\\data\\");
            Parser parser = new Parser(fileManagement);
            parser.parse();
            Dictionary dictionary = new Dictionary();
            System.out.println("*************Dictionary*************");
            dictionary.getDictionary().forEach((k, v) -> System.out.println(k + " " + v));


            System.out.println("*************SPO indexes*************");
            dictionary.getSPOindexes().forEach((k, v) -> System.out.println(k + " " + v));

            System.out.println("*************Search by subject and predicate*************");
            String returnedValue = dictionary.searchFromDictionaryByIndexesObjects("http://db.uwaterloo.ca/~galuc/wsdbm/User0", "http://schema.org/birthDate");
            System.out.println(returnedValue);
        }

}

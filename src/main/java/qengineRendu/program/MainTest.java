package qengineRendu.program;

import qengineRendu.program.parser.Parser;
import qengineRendu.program.parser.QueryParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.TypeIndex;

import java.io.IOException;


public class MainTest {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {


            FilePath fileManagement = new FilePath("C:\\Users\\SCD UM\\Documents\\M2 GL\\qengineRendu\\data\\");
            Parser parser = new Parser(fileManagement);
            parser.parse();
            IDictionaryIndexesService dictionaryIndexesService = new DictionaryIndexesServiceImpl();

            System.out.println("*************Dictionary*************");
            dictionaryIndexesService.getDictionary().forEach((k, v) -> System.out.println(k + " " + v));


            System.out.println("*************SPO indexes*************");
            dictionaryIndexesService.getIndexesByType(TypeIndex.SPO).forEach((k, v) -> System.out.println(k + " " + v));

//            System.out.println("*************Search by subject and predicate*************");
//            String returnedValue = dictionaryIndexesService.searchFromDictionaryByIndexesObjects(
//            "http://db.uwaterloo.ca/~galuc/wsdbm/User0",
//            "http://schema.org/birthDate");
//            System.out.println(returnedValue);

            QueryParser queryParser = new QueryParser(fileManagement);
            queryParser.parse(2);
        }

}

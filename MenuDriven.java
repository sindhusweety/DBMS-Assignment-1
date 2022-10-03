import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.io.*;
import java.util.function.UnaryOperator;

public class MenuDriven {

    private String input;
    private String CURRENT_DIR = System.getProperty("user.dir");
    private Path STORAGE_DIR = Path.of(CURRENT_DIR + "/DB");
    private Path DBLOG = Path.of(STORAGE_DIR + "/dbFiles.txt");
    private Path TABLENAME;

    FileWriter tb;



    static String CREATE = "CREATE";
    static String INSERT = "INSERT";
    static String PRINTFILE = "PRINTFILE";
    static String REMOVE = "REMOVE";
    static String EXIT = "EXIT";

    private Integer MAX = 0;
    private Integer NO_COLS = 0;
    ArrayList<String> tb_columns = new ArrayList<String>();
    ArrayList<ArrayList> tableRecords = new ArrayList<ArrayList>();

    Dictionary dictionary = new Hashtable();
    Hashtable<String, ArrayList<ArrayList>> dataFileObj = new Hashtable<String, ArrayList<ArrayList>>();


    public void readerFun(){

        try {
        BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));
        this.input = objReader.readLine();
        }
        catch (Exception e){
            this.exceptionFun();
        }
    }
    protected static HashMap<Character, String> mappingOperations(){
        HashMap<Character, String> opObj = new HashMap<Character, String>();
        opObj.put('c', CREATE);
        opObj.put('i', INSERT);
        opObj.put('p', PRINTFILE);
        opObj.put('r', REMOVE);
        opObj.put('e', EXIT);
        return opObj;
    }

    public void exceptionFun(){
        System.out.println("-------------------------------------------------");
        System.out.println("*******Something went wrong. Please try Again*****");
        System.out.println("---------------------------------------------------");
        this.homepage();
    }


    public void dataLog(){
        try{

            BufferedReader new_data = new BufferedReader(Files.newBufferedReader(this.DBLOG));
            String line;
            int count = 0;
            while ((line = new_data.readLine()) != null){
                if (count >= 1){
                    String[] l = line.split(",");
                    String fname = l[0].strip();
                    String cname = l[1].strip();
                    String size = l[2].strip();

                    if (this.dataFileObj.containsKey(fname)){

                        this.dataFileObj.get(fname).get(0).add(cname);
                        this.dataFileObj.get(fname).get(1).add(size);

                    }
                    else{
                        ArrayList<String> lcname = new ArrayList<String>();
                        lcname.add(cname);
                        ArrayList<String> lsize = new ArrayList<String>();
                        lsize.add(size);
                        this.dataFileObj.put(fname, new ArrayList<>());
                        this.dataFileObj.get(fname).add(lcname);
                        this.dataFileObj.get(fname).add(lsize);
                        //System.out.println(this.dataFileObj);

                    }

                }
                count += 1;
            }
        }
        catch (Exception e){
            this.exceptionFun();
        }
    }
    //---------------------------------------------------------------------------------------------------
    //------------------------------CREATION------------------------------------------------------------
    public void createTable(){
        try {
            System.out.println("Enter table name: ");
            this.readerFun();

            //System.out.println(this.CURRENT_DIR +" "+ this.STORAGE_DIR);

            if (Files.exists(this.STORAGE_DIR) == false){
               Files.createDirectory(this.STORAGE_DIR);
            }
            if (Files.exists(this.DBLOG) == false){
                Files.createFile(this.DBLOG);
                Files.writeString(this.DBLOG, "file name, column name, size"+'\n');
            }
            this.dataLog();

            if (this.input.strip().isEmpty() == false){
                if ((this.dataFileObj.containsKey(this.input)) == false){
                    String tname = this.input.strip();
                    this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".db");
                    Files.createFile(this.TABLENAME);
                    while (this.input.strip().isEmpty() == false){
                        System.out.println("Enter column name and its length:");
                        this.readerFun();

                        while (this.tb_columns.contains(this.input.split(" ")[0].strip())){
                            System.out.println("------------------------------------------------------------");
                            System.out.println("Column name already exists. Please try Again...");
                            System.out.println("------------------------------------------------------------");
                            System.out.println("Enter column name and its length:");
                            this.readerFun();

                        }
                        if (this.input.strip().isEmpty() == false){
                            String[] splnc = this.input.split(" ");

                            //added
                            while (splnc.length != 2){
                                System.out.println("-----------------------------------------------------------------");
                                System.out.println("Invalid Input. So, Please try Again");
                                System.out.println("-----------------------------------------------------------------");
                                System.out.println("Enter column name and its length:");
                                this.readerFun();
                                splnc = this.input.split(" ");


                            }
                            int chk_size = Integer.parseInt(this.input.split(" ")[1].strip());
                            while (chk_size <= 0){
                                System.out.println("-----------------------------------------------------------------");
                                System.out.println("Entered column's size should be above 0");
                                System.out.println("-----------------------------------------------------------------");
                                System.out.println("Enter column name and its length:");
                                this.readerFun();
                                splnc = this.input.split(" ");
                                chk_size = Integer.parseInt(this.input.split(" ")[1].strip());

                            }


                            ArrayList<String> colname = new ArrayList<>();
                            ArrayList<String> sze = new ArrayList<>();


                            colname.add(splnc[0].strip());
                            sze.add(splnc[1].strip());

                            if (this.dataFileObj.containsKey(tname)){
                                this.tb_columns.add(splnc[0].strip());
                                this.dataFileObj.get(tname).get(0).add(splnc[0].strip());
                                this.dataFileObj.get(tname).get(1).add(splnc[1].strip());
                            }
                            else{
                                this.dataFileObj.put(tname, new ArrayList<>());
                                this.tb_columns.add(splnc[0].strip());
                                this.dataFileObj.get(tname).add(colname);
                                this.dataFileObj.get(tname).add(sze);
                            }


                        }
                    }
                    if (this.input.isEmpty()){
                        System.out.println("------------------------------------------------------------");
                        System.out.println(this.dataFileObj);
                        this.writeDBRec();
                        this.tableColumnsWriter();
                        System.out.println("************Table is created Successfully********************");
                        System.out.println("------------------------------------------------------------");
                        //homepage();
                        this.dataFileObj.clear(); //must be cleared
                        this.tb_columns.clear();
                        System.out.println("If you would like to create more tables, Type 'Yes' or 'No' (y/n):");
                        this.readerFun();
                        if (this.input.toLowerCase().startsWith("y")){
                            System.out.println("------------------------------------------------------------");

                            this.createTable();
                        }
                        else{
                            this.redirectToHome();
                        }

                    }

                }
                else{
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Table name already exists. So, Please try AGAIN ");
                    System.out.println("------------------------------------------------------------");
                    this.createTable();
                }
            }
            else {
                this.inValidTableNameMsg();
                this.createTable();
            }


        }
        catch (Exception e){
            this.exceptionFun();
        }
    }
    //---------------------------------------------------------------------------------------------------------

    public void writeDBRec()  {
        try{

            FileWriter bw = new FileWriter(this.DBLOG.toFile());

            String newLine = System.getProperty("line.separator");

            String inputstream ="file name, column name, size"+ newLine;
            bw.write(inputstream);

            for (String key : this.dataFileObj.keySet() ){
                ArrayList<ArrayList> v1 = this.dataFileObj.get(key).get(0);
                ArrayList<ArrayList> v2 = this.dataFileObj.get(key).get(1);
                for (int i = 0; i < (v1.size()); i++){
                    //System.out.println(key + "  "+ v1.get(i)+"  "+ v2.get(i));
                    inputstream =key + ","+ v1.get(i)+","+ v2.get(i)+newLine;
                    bw.write(inputstream);

                }
            }
            bw.close();
            this.dataFileObj.clear(); //must be cleared

        }
        catch (Exception e){
            this.exceptionFun();
        }



    }
    public void tableColumnsWriter(){

        try {
            tb = new FileWriter(this.TABLENAME.toFile());
            String newLine = System.getProperty("line.separator");
            String tbcolumns = this.tb_columns.get(0);

            for (int i = 1; i<this.tb_columns.size(); i++){
                tbcolumns +="," + this.tb_columns.get(i);
            }
            tbcolumns = tbcolumns+newLine;

            System.out.println(tbcolumns);
            tb.write(tbcolumns);
            tb.close();
            this.tb_columns.clear();

        }
        catch (Exception e){
            this.exceptionFun();
        }

    }

    public void tableReplace(){
        try {
            tb = new FileWriter(this.TABLENAME.toFile());
            String newLine = System.getProperty("line.separator");
            String tbcolumns = "";
            for (int i = 0; i<this.tableRecords.size(); i++){
                String subtbcolumns = String.valueOf( this.tableRecords.get(i).get(0));
                for (int x = 1;  x <  this.tableRecords.get(i).size(); x++ ){
                    subtbcolumns += ","+this.tableRecords.get(i).get(x);
                }
                tbcolumns = subtbcolumns+newLine ;
                tb.write(tbcolumns);
            }

            tb.close();
            this.tb_columns.clear();
            this.tableRecords.clear();


        }

        catch (Exception e){
            this.exceptionFun();
        }
    }

    public void tableRecordWriter(){
        try {

            BufferedReader new_data = new BufferedReader(Files.newBufferedReader(this.TABLENAME));
            String line;
            while ((line = new_data.readLine())  != null){
                String[] l = line.split(",");
                ArrayList<String> al = new ArrayList<String>();
                for (int i = 0; i < l.length; i++){
                    al.add(l[i]);
                }
                this.tableRecords.add(al);

            }
            this.tableRecords.add(this.tb_columns);
            //System.out.println(this.tableRecords);
            this.tableReplace();
            //System.out.println(this.tableRecords);

        }
        catch (Exception e){
            this.exceptionFun();
        }
    }
    //-----------------------------------------------------------------------------------------
    //---------------------------INSERTION------------------------------------------------------
    public void insertTable(){
        try{

            System.out.println("-----------------------------------------------------------------");
            System.out.println("Choose Table name:");
            if (this.dataFileObj.isEmpty()){
                this.dataLog();
            }
            this.readerFun();

            if (this.input.strip().isEmpty() == false){
                if (this.dataFileObj.containsKey(this.input.strip())){

                    this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".db");

                    ArrayList<ArrayList> value = this.dataFileObj.get(this.input.strip());
                    for (int i=0; i< value.get(0).size(); i++){

                        String cols = String.valueOf(value.get(0).get(i));
                        Integer sze = Integer.valueOf((String) value.get(1).get(i));
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println("Enter "+value.get(0).get(i)+":");

                        this.readerFun();
                        Integer  input_size = this.input.strip().length();

                        while (input_size > sze){
                            System.out.println("-----------------------------------------------------------------");
                            System.out.println("Entered value's length is larger. So, Please try again");
                            System.out.println("The width of this particular column("+cols+") is :"+value.get(1).get(i));
                            System.out.println("Enter "+cols+":");
                            System.out.println("-----------------------------------------------------------------");
                            this.readerFun();
                            input_size = this.input.strip().length();

                        }

                        this.tb_columns.add(this.input.strip());
                    }

                    System.out.println("------------------------------------------------------------");

                    this.tableRecordWriter();
                    this.tb_columns.clear();
                    this.tableRecords.clear();

                    System.out.println("************Record is inserted Successfully********************");
                    System.out.println("------------------------------------------------------------");
                    System.out.println("If you would like to create more tables, Type 'Yes' or 'No' (y/n):");
                    this.readerFun();
                    if (this.input.toLowerCase().startsWith("y")){
                        System.out.println("------------------------------------------------------------");
                        this.insertTable();
                    }
                    else{
                        this.redirectToHome();
                    }
                }
                else{
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Table name doesn't exist. So, Please try AGAIN ");
                    System.out.println("------------------------------------------------------------");
                    this.insertTable();
                }

            }
            else{
                this.inValidTableNameMsg();
                this.insertTable();
            }

        }
        catch (Exception e){
            this.exceptionFun();
        }

    }

    public void inValidTableNameMsg(){
        System.out.println("------------------------------------------------------------");
        System.out.println("You have entered invalid table name. So, Please try AGAIN ");
        System.out.println("------------------------------------------------------------");
    }

    public void redirectToHome(){

        System.out.println("------------------------------------------------------------");
        System.out.println("Redirecting to Home Page..");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        this.homepage();

    }
    public String eachRec(ArrayList<String> rec){
        String record = "|";

        for (String cell : rec){
            int no_char = cell.strip().length();
            String spaces = " ";
            int count = 0;
            while (count < (this.MAX-no_char-2)){
                spaces += " ";
                count += 1;
            }
            record +=" " +cell.strip()+ spaces+"|";
        }

        return record;
    }
    public String dashingFun(){
        String dashes = "";
        int count = 0;
        while (count < ((this.MAX * this.NO_COLS)+this.NO_COLS+1)){
            dashes += "-";
            count += 1;
        }

        return dashes;
    }
    public void displayTable(){
        try{
            String dashes = dashingFun();
            for (int i=0; i < this.tableRecords.size(); i++){
                if (i == 0){
                    System.out.println(dashes);
                    String record = this.eachRec(this.tableRecords.get(0));
                    System.out.println(record);
                    System.out.println(dashes);
                }
                else{
                    String record = this.eachRec(this.tableRecords.get(i));
                    System.out.println(record);
                    System.out.println(dashes);
                }

            }
        }
        catch (Exception e){
            this.exceptionFun();
        }
    }
    public void loadTableRec(){
        try{

            BufferedReader new_data = new BufferedReader(Files.newBufferedReader(this.TABLENAME));
            String line;
            while ((line = new_data.readLine())  != null) {
                String[] l = line.split(",");

                ArrayList<String> al = new ArrayList<String>();
                for (int i = 0; i < l.length; i++) {
                    al.add(l[i].strip()); //added strp
                }
                this.tableRecords.add(al);
            }

        }
        catch (Exception e){
            this.exceptionFun();
        }
    }

    public void tableProcessing(){
        this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".db");

        ArrayList<ArrayList> value = this.dataFileObj.get(this.input.strip());
        for (int i=0; i< value.get(0).size(); i++) {

            String cols = String.valueOf(value.get(0).get(i));
            Integer sze = Integer.valueOf((String) value.get(1).get(i));
            this.MAX = Math.max(sze, this.MAX);

        }

        this.NO_COLS = value.get(0).size();
        this.loadTableRec();
        System.out.println(this.input.strip()+".db");
        this.displayTable();
        this.tableRecords.clear();
        this.dataFileObj.clear();
        this.MAX = 0;
        this.NO_COLS = 0;
    }
    //-----------------------------------------------------------------------------------------------------------------
    public void printFile(){
        try{
            this.dataFileObj.clear();
            this.tableRecords.clear();
            this.tb_columns.clear();
            System.out.println("Enter All to disaply all tables or Enter table name to display: ");
            this.readerFun();
            this.dataLog();

            if (this.input.strip().isEmpty() == false){
                if (this.dataFileObj.containsKey(this.input.strip())){

                    this.tableProcessing();
                    this.dataFileObj.clear();
                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    System.out.println("------------------------------------------------------------");
                    System.out.println(" Type 'Yes' or 'No' (y/n) to Continue:");
                    this.readerFun();
                    if (this.input.toLowerCase().startsWith("y")){
                        System.out.println("------------------------------------------------------------");
                        this.printFile();
                    }
                    else{
                        this.redirectToHome();
                    }


                } else if (this.input.strip().toLowerCase().startsWith("all")){
                    File f = new File(this.STORAGE_DIR.toUri());
                    for (File path : f.listFiles()){
                        String file = path.getName();
                        if (file.endsWith(".db")){
                            String input = (file.split(".d")[0]);
                            this.input = input;
                            this.dataFileObj.clear();
                            this.dataLog();
                            this.tableProcessing();
                        }
                    }
                    this.dataFileObj.clear();
                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    System.out.println("------------------------------------------------------------");
                    System.out.println(" Type 'Yes' or 'No' (y/n) to Continue:");
                    this.readerFun();
                    if (this.input.toLowerCase().startsWith("y")){
                        System.out.println("------------------------------------------------------------");
                        this.printFile();
                    }
                    else{
                        this.redirectToHome();
                    }

                }else{
                    this.inValidTableNameMsg();
                    this.printFile();

                }

            }
            else{

                this.inValidTableNameMsg();
                this.printFile();

            }

        }
        catch (Exception e){
            this.exceptionFun();
        }
    }

    public void listArrToHashMap(){
        this.dataFileObj.clear();
        ArrayList<String> value;
        for (int i = 0; i < this.tableRecords.size(); i++){
            String  tname = String.valueOf(this.tableRecords.get(i).get(0));
            String filename = String.valueOf(this.tableRecords.get(i).get(1));
            String size = String.valueOf(this.tableRecords.get(i).get(2));
            ArrayList<String> colname = new ArrayList<>();
            ArrayList<String> sze = new ArrayList<>();
            colname.add(filename.strip());
            sze.add(size.strip());

            if (this.dataFileObj.containsKey(tname)){
                this.tb_columns.add(filename.strip());
                this.dataFileObj.get(tname).get(0).add(filename.strip());
                this.dataFileObj.get(tname).get(1).add(size.strip());
            }
            else{
                this.dataFileObj.put(tname, new ArrayList<>());
                this.tb_columns.add(filename.strip());
                this.dataFileObj.get(tname).add(colname);
                this.dataFileObj.get(tname).add(sze);
            }
        }
        System.out.println(dataFileObj);




    }

    public void hashMapToListArr(String filename, String colname){
        this.tableRecords.clear();
        this.tb_columns.clear();
        for (String key : this.dataFileObj.keySet()){
            for (int i=0; i < this.dataFileObj.get(key).get(0).size(); i++){
                ArrayList<String> inputstream = new ArrayList<>();
                String recColn = String.valueOf( this.dataFileObj.get(key).get(0).get(i)).strip();
                String recSize = String.valueOf(this.dataFileObj.get(key).get(1).get(i)).strip();
                if ((filename.equals(key)) & (colname.equals(recColn)) ){
                    inputstream.add("#"+key);
                    inputstream.add(recColn);
                    inputstream.add(recSize);
                }
                else{
                    inputstream.add(key);
                    inputstream.add(recColn);
                    inputstream.add(recSize);
                }
                this.tableRecords.add(inputstream);

            }


        }

    }
    public void removeChoiceOrMenuCall(){
        System.out.println("------------------------------------------------------------");
        System.out.println(" Type 'Yes' or 'No' (y/n) to Continue:");
        this.readerFun();
        if (this.input.toLowerCase().startsWith("y")){
            System.out.println("------------------------------------------------------------");
            this.removeObject();
        }
        else{
            this.redirectToHome();
        }
    }
    public void removeObject(){
        try{
            this.tableRecords.clear();
            this.tb_columns.clear();
            this.dataFileObj.clear();
            System.out.println("Enter the table to perform remove operation: ");
            this.readerFun();
            this.dataLog();
            if (this.input.toLowerCase().strip().startsWith("dbfiles")){
               System.out.println("Enter the file's name:");
               this.readerFun();
               Boolean chkfilename = this.dataFileObj.containsKey(this.input.strip());
               while (chkfilename == false){
                   System.out.println("Entered Filename not found. Please try Again");
                   System.out.println("Enter the file's name");
                   this.readerFun();
                   chkfilename = this.dataFileObj.containsKey(this.input.strip());
               }
               String filename = this.input.strip();
               ArrayList<ArrayList> value = this.dataFileObj.get(this.input.strip());


               System.out.println("Enter column's name");
               this.readerFun();
               Boolean chkcolname =value.get(0).contains(this.input.strip());
               while (chkcolname == false){
                    System.out.println("Entered column not found. Please try Again");
                    System.out.println("Enter the column's name");
                    this.readerFun();
                    chkcolname =value.get(0).contains(this.input.strip());
                }
               String column = this.input.strip();
               this.hashMapToListArr(filename, column);
               this.listArrToHashMap();
               this.writeDBRec();

               this.dataFileObj.clear();
               this.tableRecords.clear();
               this.removeChoiceOrMenuCall();

            } else if (this.dataFileObj.containsKey(this.input.strip())){
                this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".db");
                this.tableRecords.clear();
                this.tb_columns.clear();
                this.loadTableRec();
                System.out.println(this.tableRecords);
                System.out.println("Enter "+this.tableRecords.get(0).get(0) +":");
                this.readerFun();
                //String ipStream = this.input.strip();
                this.tb_columns.add(this.input.strip());
                for (int i=1; i< this.tableRecords.get(0).size(); i++ ){

                    System.out.println("Enter "+this.tableRecords.get(0).get(i) +":");
                    this.readerFun();
                    //ipStream+=","+this.input.strip();
                    this.tb_columns.add(this.input.strip());

                }
                //this.tb_columns.add(ipStream);
                int rec_index = this.tableRecords.indexOf(this.tb_columns);
                if (rec_index >= 0 ){
                    //System.out.println(this.tableRecords.indexOf(this.tb_columns));
                    this.tableRecords.remove(rec_index);
                    ArrayList<String> temp = new ArrayList<>();
                    for (int i =0; i<this.tb_columns.size(); i++){
                        if (i==0){
                            temp.add("#"+this.tb_columns.get(i));
                        }
                        else {
                            temp.add(this.tb_columns.get(i));
                        }
                    }
                    this.tableRecords.add(temp);
                    System.out.println(tableRecords);
                    this.tableReplace();
                    //System.out.println(tableRecords);
                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    this.removeChoiceOrMenuCall();
                }
                else{
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Values not found. So, Please try AGAIN ");
                    System.out.println("------------------------------------------------------------");
                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    this.dataFileObj.clear();
                    this.removeObject();
                }

            } else{

                this.inValidTableNameMsg();
                this.removeObject();

            }

        }
        catch (Exception e)
        {
            this.exceptionFun();
        }
    }
    //--------------------------------------------------------------------------------------------
    public void exitOperation(){

        try {
            File f = new File(this.STORAGE_DIR.toUri());
            for (File path : f.listFiles()) {
                String file = path.getName();
                if (file.endsWith(".db")) {
                    String input = (file.split(".d")[0]);
                    this.input = input;
                    this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".db");

                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    this.dataFileObj.clear();

                    this.loadTableRec();
                    for (int i =0; i<this.tableRecords.size(); i++){
                        if (this.tableRecords.get(i).get(0).toString().startsWith("#")){
                            this.tableRecords.remove(i);
                        }
                    }

                    System.out.println(tableRecords);
                    this.tableReplace();
                    //System.out.println(tableRecords);
                    this.tableRecords.clear();
                    this.tb_columns.clear();


                }
                else{
                    String input = (file.split(".t")[0]);
                    this.input = input;
                    this.TABLENAME =  Path.of(this.STORAGE_DIR + "/" + this.input.strip() + ".txt");
                    this.tableRecords.clear();
                    this.tb_columns.clear();
                    this.dataFileObj.clear();

                    this.loadTableRec();

                    for (int i =0; i<this.tableRecords.size(); i++){
                        if (this.tableRecords.get(i).get(0).toString().startsWith("#")){
                            this.tableRecords.remove(i);
                        }
                    }
                    System.out.println(tableRecords);
                    this.tableReplace();
                    //System.out.println(tableRecords);
                    this.tableRecords.clear();
                    this.tb_columns.clear();

                }


            }

        }
        catch (Exception e){
        }
    }


    //------------------------------------------------------------------------------------------
    public void homepage(){
        try {
            System.out.println("You are on the menu");
            System.out.println("c - CREATE \t" + "i - INSERT \t" +
                    "p - PRINTFILE \t" +
                    "r - REMOVE \t" +
                    "e - EXIT");
            BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("type anyone of operations to perform  (c/i/p/r/e) & click ENTER: ");
            String operation = objReader.readLine();
            char op_symbol = operation.strip().toLowerCase().charAt(0);
            HashMap<Character, String> mapOp = mappingOperations();
            if (mapOp.get(op_symbol) == null){
                System.out.println("-------------------------------------------------------");
                System.out.println("**** Chosen Wrong symbol & redirected to Homepage AGAIN *****");
                System.out.println("-------------------------------------------------------");
                this.homepage();

            }
            else{
                System.out.println((mapOp.get(op_symbol)) + " operation has been chosen");
                //System.out.println((Operations.CREATE).getClass().getName());
                if (mapOp.get(op_symbol) == CREATE){
                    this.createTable();

                }
                else if (mapOp.get(op_symbol) == INSERT){
                    this.insertTable();

                }
            else if (mapOp.get(op_symbol) == PRINTFILE){
                    this.printFile();

                }
                else if (mapOp.get(op_symbol) == REMOVE){
                    this.removeObject();

                }
                else if (mapOp.get(op_symbol) == EXIT){
                    this.exitOperation();

                }


            }
        }
        catch (Exception e){
            this.exceptionFun();

        }
    }




    public static void main(String[] args){
        MenuDriven menuobj = new MenuDriven();
        menuobj.homepage();

    }
}

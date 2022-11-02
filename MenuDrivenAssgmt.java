
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class MenuDrivenAssgmt {

    private String input;
    static final String CURRENT_DIR = System.getProperty("user.dir");
    static final Path STORAGE_DIR = Path.of(CURRENT_DIR + "/javafiles");
    static final String DB_URL = STORAGE_DIR + "/stuff.accdb";

    static final String CREATE = "CREATE";
    static final String INSERT = "INSERT";

    static final String REMOVE = "REMOVE";
    static final String PRINTTABLE = "PRINTTABLE";

    static  final String INCLUDE_BOOK = "INCLUDE_BOOK";
    static  final String INCLUDE_USER = "INCLUDE_USER";
    static  final String LIST_LIBRARY = "LIST_LIBRARY";
    static  final String CHECKOUT = "CHECKOUT";
    static  final String RETURN_BOOK = "RETURN_BOOK";
    static  final String EXIT = "EXIT";
    Connection conn;
    DatabaseMetaData databaseMetaData;
    Statement statement;
    ResultSet rs;
    ResultSetMetaData rsmd;

    HashSet<String> ltables = new HashSet<>();
    ArrayList<String>  generic_arrlist = new ArrayList<String>();

    public void readerFun() {

        try {
            BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));
            this.input = objReader.readLine();
        }
        catch (IOException e){
            this.exceptionFun();
        }
    }

    protected static HashMap<Character, String> mappingOperations(){
        HashMap<Character, String> opObj = new HashMap<Character, String>();
        opObj.put('A', CREATE);
        opObj.put('B', INSERT);
        opObj.put('C', REMOVE);
        opObj.put('D', PRINTTABLE);
        opObj.put('1', INCLUDE_BOOK);
        opObj.put('2', INCLUDE_USER);
        opObj.put('3', LIST_LIBRARY);
        opObj.put('4', CHECKOUT);
        opObj.put('5', RETURN_BOOK);
        opObj.put('6', EXIT);
        return opObj;
    }

    public void createDB()  {
        try{

        if (!Files.exists(STORAGE_DIR)){
            Files.createDirectory(STORAGE_DIR);
        }
        if (!Files.exists(Path.of(DB_URL))){
            File f = new File(DB_URL);
            Database db = new DatabaseBuilder(f).setFileFormat(Database.FileFormat.V2000).create();
            db.close();
        }

        }
        catch (IOException e){
            System.out.println("SQL error occured   : "+ e );
            this.exceptionFun();
        }
    }
    public void dbConnection() {
         try
         {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            this.conn =DriverManager.getConnection(
                    "jdbc:ucanaccess://"+DB_URL);

            this.statement = this.conn.createStatement();


        }
         catch (SQLException | ClassNotFoundException e) {
             System.out.println("------------------------------------------------------------");
             System.out.println("SQL error occured   : "+ e );
             System.out.println("------------------------------------------------------------");
             this.exceptionFun();
         }
    }

    public void fetchTableNames() {
        try {
            databaseMetaData = this.conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});

            while (resultSet.next()) {
                ltables.add( resultSet.getString("TABLE_NAME"));
            }

        }
        catch (SQLException e) {
            System.out.println("SQL error occured   : "+ e );
            this.exceptionFun();
        }

    }

    public void exceptionFun(){
        System.out.println("-------------------------------------------------");
        System.out.println("*******Something went wrong. Please try Again*****");
        System.out.println("---------------------------------------------------");
        this.menuPage();
    }

    public void createTable() {

        try {
            this.generic_arrlist.clear();
            this.fetchTableNames();
            System.out.println(this.ltables);
            System.out.println("Enter table name: ");
            this.readerFun();
            if (!this.input.isBlank()) {

                while (this.ltables.contains(this.input.strip())) {
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Entered table name already exists in the DB & Please try Again. ");
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Enter table name: ");
                    this.readerFun();
                }
                this.generic_arrlist.add(this.input.strip());
                while (!this.input.isBlank()) {
                    System.out.println("Enter column name:");
                    this.readerFun();
                    while (this.generic_arrlist.contains(this.input.strip())) {
                        System.out.println("------------------------------------------------------------");
                        System.out.println("Entered column name already exists in the DB & Please try Again. ");
                        System.out.println("------------------------------------------------------------");
                        System.out.println("Enter column name:");
                        this.readerFun();
                    }
                    if (!this.input.strip().isBlank()){
                        this.generic_arrlist.add(this.input.strip());
                    }

                }
                String sql = "CREATE TABLE " + this.generic_arrlist.get(0) + " ( ";
                for (int i = 1; i < this.generic_arrlist.size()-1; i++) {
                    sql += this.generic_arrlist.get(i) + " VARCHAR(255), ";
                }
                sql += this.generic_arrlist.get(this.generic_arrlist.size()-1)+ " VARCHAR(255))";
                System.out.println(sql);
                this.statement.executeUpdate(sql);
                this.conn.commit();
                System.out.println("------------------------------------------------------------");
                System.out.println("Successfully created table");
                System.out.println("------------------------------------------------------------");
                this.generic_arrlist.clear();
                System.out.println("------------------------------------------------------------");
                System.out.println("If you would like to create more tables, Type 'Yes' or 'No' (y/n):");
                System.out.println("------------------------------------------------------------");
                this.readerFun();
                if (this.input.toLowerCase().startsWith("y")){
                    System.out.println("------------------------------------------------------------");
                    this.createTable();
                }
                else{
                    this.redirectToHome();
                }

            } else {
                this.emptyInputMsg();
                this.createTable();
            }
        }
        catch (SQLException e) {
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }

    }

    public void emptyInputMsg(){
        System.out.println("------------------------------------------------------------");
        System.out.println("Entered Empty input & Please try again");
        System.out.println("------------------------------------------------------------");
    }
    public void sqlErrorMsg(SQLException e){
        System.out.println("------------------------------------------------------------");
        System.out.println("SQL error occured   : "+ e );
        System.out.println("------------------------------------------------------------");
    }

    public void insertTable(){
        try{
            this.generic_arrlist.clear();
            this.fetchTableNames();
            System.out.println(this.ltables);
            System.out.println("Enter table name: ");
            this.readerFun();
            if (! this.input.isBlank()){
                if (this.ltables.contains(this.input.strip())){

                    String[] strArr = this.check_insert();
                    String table_name = strArr[0];
                    String insert_sql = strArr[1];

                    if (rs.next()){
                        System.out.println("------------------------------------------------------------");
                        System.out.println("Record already exists in the "+table_name+" table & Please try Again");
                        System.out.println("------------------------------------------------------------");
                        this.insertTable();
                    }
                    else{
                        this.statement.executeUpdate(insert_sql);
                        this.conn.commit();
                        this.successInclusionMsg();
                        System.out.println(" Type 'Yes' or 'No' (y/n) to insert more:");
                        this.readerFun();
                        if (this.input.toLowerCase().startsWith("y")){
                            System.out.println("------------------------------------------------------------");
                            this.insertTable();
                        }
                        else{
                            this.redirectToHome();
                        }
                    }

                }
                else{
                    this.tableDsnotExMsg();
                    this.insertTable();
                }

            }
            else {
                this.emptyInputMsg();
                this.insertTable();
            }

        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }
    }
    public void successInclusionMsg(){

        System.out.println("------------------------------------------------------------");
        System.out.println("Successfully included");
        System.out.println("------------------------------------------------------------");
        this.generic_arrlist.clear();
        System.out.println("------------------------------------------------------------");
    }

    public void tableDsnotExMsg(){
        System.out.println("------------------------------------------------------------");
        System.out.println("Table doesn't exist & Please try again");
        System.out.println("------------------------------------------------------------");
    }

    public void removeRecords(){
        try{

            this.generic_arrlist.clear();
            this.fetchTableNames();
            System.out.println(this.ltables);
            if (! this.input.isBlank()){
                if (this.ltables.contains(this.input.strip())){
                    String sql = "SELECT * FROM "+this.input.strip();
                    this.rs = this.statement.executeQuery(sql);
                    this.rsmd = this.rs.getMetaData();

                    String table_name = this.input.strip();


                    System.out.println("Enter "+table_name+"'s "+this.rsmd.getColumnName(1)+":");
                    this.readerFun();

                    String delete_sql = "DELETE FROM "+table_name+" WHERE "+this.rsmd.getColumnName(1)+" ='"+this.input+"'";
                    String check_query = "SELECT * FROM "+ table_name+" WHERE "+this.rsmd.getColumnName(1)+" ='"+this.input+"'";
                    int count = 2;
                    while (count <= this.rsmd.getColumnCount()){
                        System.out.println("Enter "+table_name+"'s "+this.rsmd.getColumnName(count)+":");
                        this.readerFun();
                        delete_sql+=" AND "+this.rsmd.getColumnName(count)+" = '"+this.input.strip()+"'";
                        check_query+=" AND "+this.rsmd.getColumnName(count)+" = '"+this.input.strip()+"'";

                        count += 1;

                    }

                    //System.out.println(delete_sql);
                    //System.out.println(check_query);

                    this.rs = this.statement.executeQuery(check_query);

                    if (!rs.next()){
                        System.out.println("------------------------------------------------------------");
                        System.out.println("Record doesn't even exist in "+table_name+" table to DELETE & Please try Again");
                        System.out.println("------------------------------------------------------------");
                        this.removeRecords();
                    }
                    else{
                        this.statement.executeUpdate(delete_sql);
                        this.conn.commit();
                        System.out.println("------------------------------------------------------------");
                        System.out.println("Successfully deleted");
                        System.out.println("------------------------------------------------------------");

                        this.generic_arrlist.clear();
                        System.out.println(" Type 'Yes' or 'No' (y/n) to delete more records:");
                        this.readerFun();
                        if (this.input.toLowerCase().startsWith("y")){
                            System.out.println("------------------------------------------------------------");
                            this.removeRecords();
                        }
                        else{
                            this.redirectToHome();
                        }

                    }
                }
                else{
                    this.tableDsnotExMsg();
                    this.removeRecords();
                }

            }
            else {
                this.emptyInputMsg();
                this.removeRecords();
            }

        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }
    }
    public void printTable(){
        try{
            this.generic_arrlist.clear();
            this.fetchTableNames();
            System.out.println(this.ltables);
            System.out.println("Enter table name: ");
            this.readerFun();
            if (! this.input.isBlank()){
                if (this.ltables.contains(this.input.strip())){
                    String sql = "SELECT * FROM "+this.input.strip();
                    this.rs = this.statement.executeQuery(sql);
                    this.rsmd = this.rs.getMetaData();

                    String table_name = this.input.strip();

                    System.out.println("------------------------------------------------------------");
                    System.out.println(table_name);
                    DBTablePrinter.printTable(this.conn, table_name);
                    System.out.println("------------------------------------------------------------");

                    this.generic_arrlist.clear();
                    System.out.println(" Type 'Yes' or 'No' (y/n) to continue printing:");
                    this.readerFun();
                    if (this.input.toLowerCase().startsWith("y")){
                        System.out.println("------------------------------------------------------------");
                        this.printTable();
                    }
                    else{
                        this.redirectToHome();
                    }
                }
                else{
                    this.tableDsnotExMsg();
                    this.printTable();
                }

            }
            else {
                this.emptyInputMsg();
                this.printTable();
            }

        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }


    }
    public String[] check_insert(){
        try{
            String sql = "SELECT * FROM "+this.input.strip();
            this.rs = this.statement.executeQuery(sql);
            this.rsmd = this.rs.getMetaData();

            String table_name = this.input.strip();


            System.out.println("Enter "+table_name+"'s "+this.rsmd.getColumnName(1)+":");
            this.readerFun();

            String insert_sql = "INSERT INTO "+table_name+" VALUES ('"+this.input+"'";
            String check_query = "SELECT * FROM "+ table_name+" WHERE "+this.rsmd.getColumnName(1)+" ='"+this.input+"'";
            int count = 2;
            while (count <= this.rsmd.getColumnCount()){
                System.out.println("Enter "+table_name+"'s "+this.rsmd.getColumnName(count)+":");
                this.readerFun();
                //cross check
                 if (table_name.equals("books")  & this.rsmd.getColumnName(count).strip().equals("callNo")){
                    String callNo = this.input.strip();
                    String callNoCheck = "SELECT * FROM books WHERE callNo='"+callNo+"'";
                    this.rs = this.statement.executeQuery(callNoCheck);
                    boolean rs_status = this.rs.next();
                    System.out.println(rs_status);
                    while (rs_status ){
                        System.out.println("callNo already exists");
                        System.out.println("Enter book's call#:");
                        this.readerFun();
                        callNo = this.input.strip();
                        callNoCheck = "SELECT * FROM books WHERE callNo='"+callNo+"'";
                        this.rs = this.statement.executeQuery(callNoCheck);
                        rs_status = this.rs.next();
                    }
                    insert_sql+=",'"+callNo+"'";
                    check_query+=" AND "+this.rsmd.getColumnName(count)+" = '"+callNo+"'";

                }
                else{
                    insert_sql+=",'"+this.input.strip()+"'";
                    check_query+=" AND "+this.rsmd.getColumnName(count)+" = '"+this.input.strip()+"'";
                }


                count += 1;

            }
            insert_sql+=")";
            String[] strArr ={table_name, insert_sql};
            //System.out.println(insert_sql);
            //System.out.println(check_query);
            this.rs = this.statement.executeQuery(check_query);
            return strArr;
        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }

        return new String[0];
    }

    public String[] checkCallNo(String table_name, String SSN){
        try {
            System.out.println("Enter book's call#:");
            this.readerFun();
            String callNo = this.input;
            String callNoCheck = "SELECT * FROM books WHERE callNo='"+callNo+"'";
            this.rs = this.statement.executeQuery(callNoCheck);
            String callNo_user_discretion = "y";
            boolean rs_status = !this.rs.next();
            boolean callNo_user_discretion_status = callNo_user_discretion.strip().toLowerCase().startsWith("y");
            while (rs_status & callNo_user_discretion_status){

                System.out.println("Entered callNo doesn't exist & please try again.");
                System.out.println("do you want to continue trying (Y/n)?:");
                this.readerFun();
                callNo_user_discretion = this.input.toLowerCase();
                callNo_user_discretion_status = callNo_user_discretion.strip().toLowerCase().startsWith("y");
                if (this.input.toLowerCase().startsWith("y")){
                    System.out.println("Enter book's call#:");
                    this.readerFun();
                    callNo = this.input.strip();
                    callNoCheck = "SELECT * FROM books WHERE callNo='"+callNo+"'";
                    this.rs = this.statement.executeQuery(callNoCheck);
                    rs_status = !this.rs.next();

                }
                else{
                    this.redirectToHome();
                    callNo_user_discretion_status = false;

                }
            }

            callNoCheck = "SELECT * FROM checkedOutBooks WHERE callNo='"+callNo+"'";
            this.rs = this.statement.executeQuery(callNoCheck);
            callNo_user_discretion = "y";
            rs_status = this.rs.next();
            callNo_user_discretion_status = callNo_user_discretion.strip().toLowerCase().startsWith("y");
            while (rs_status & callNo_user_discretion_status){

                System.out.println("You have already checked out this book.");
                System.out.println("do you want to continue trying (Y/n)?:");
                this.readerFun();
                callNo_user_discretion = this.input.toLowerCase();
                callNo_user_discretion_status = callNo_user_discretion.strip().toLowerCase().startsWith("y");
                if (this.input.toLowerCase().startsWith("y")){
                    System.out.println("Enter book's call#:");
                    this.readerFun();
                    callNo = this.input.strip();
                    callNoCheck = "SELECT * FROM books WHERE callNo='"+callNo+"'";
                    this.rs = this.statement.executeQuery(callNoCheck);
                    rs_status = this.rs.next();

                }
                else{
                    this.redirectToHome();
                    callNo_user_discretion_status = false;

                }

            }

            String insert_checkout = "INSERT INTO "+table_name+" VALUES ('"+SSN+"','"+callNo+"')";
            String chkOtBCheck = "SELECT * FROM "+table_name+ " WHERE SSN='"+SSN+"' AND callNo='"+callNo+"'";
            this.rs = this.statement.executeQuery(chkOtBCheck);
            String[] strArrCall = {callNo, insert_checkout};
            return strArrCall;
        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }
        return new String[0];
    }

    public void generic_insertion(){
        try{
            String table_name = this.input.strip();
            this.generic_arrlist.clear();
            this.fetchTableNames();
            System.out.println(this.ltables);
            if (! this.input.isBlank()){
                if (this.ltables.contains(this.input.strip())){
                    if (table_name.equals("checkedOutBooks")){

                        System.out.println("Enter person's SSN:");
                        this.readerFun();
                        String SSN = this.input.strip();
                        String ssnSQLCheck = "SELECT * FROM people WHERE SSN='"+ SSN+"'";
                        this.rs = this.statement.executeQuery(ssnSQLCheck);
                        String ssn_user_discretion = "y";

                        boolean rs_status = !this.rs.next();
                        boolean ssn_user_discretion_status = ssn_user_discretion.strip().toLowerCase().startsWith("y");

                        while (rs_status & ssn_user_discretion_status){

                            System.out.println("Entered SSN doesn't exist & please try again.");
                            System.out.println("do you want to continue trying (Y/n)?:");
                            this.readerFun();
                            ssn_user_discretion = this.input.toLowerCase();
                            ssn_user_discretion_status = ssn_user_discretion.strip().toLowerCase().startsWith("y");
                            if (this.input.toLowerCase().startsWith("y")){
                                System.out.println("Enter person's SSN:");
                                this.readerFun();
                                SSN = this.input.strip();
                                ssnSQLCheck = "SELECT * FROM people WHERE SSN='"+ SSN+"'";
                                this.rs = this.statement.executeQuery(ssnSQLCheck);
                                rs_status = !this.rs.next();
                            }
                            else{

                                this.redirectToHome();
                                ssn_user_discretion_status = false;

                            }

                        }
                        String[] strArrChk=this.checkCallNo(table_name, SSN);
                        String callNo = strArrChk[0];
                        String insert_checkout = strArrChk[1];

                        if (this.rs.next()){
                            System.out.println("------------------------------------------------------------");
                            System.out.println("This Person already checked out this book (callNo: "+callNo+ "). Please try Again");
                            System.out.println("------------------------------------------------------------");
                            this.input = table_name;
                            this.generic_insertion();
                        }
                        else{

                            this.statement.executeUpdate(insert_checkout);
                            this.conn.commit();
                            this.successInclusionMsg();
                            System.out.println("More books checked out (Y/n) ?");
                            this.readerFun();
                            String user_discretion = this.input.strip().toLowerCase();
                            //-------------------------------------------------------------------------------------
                            while (user_discretion.startsWith("y")) {

                                strArrChk = this.checkCallNo(table_name, SSN);
                                callNo = strArrChk[0];
                                insert_checkout = strArrChk[1];
                                if (this.rs.next()) {
                                    System.out.println("------------------------------------------------------------");
                                    System.out.println("This Person already checked out this book (callNo: "+callNo+  "). Please try Again");
                                    System.out.println("------------------------------------------------------------");
                                    System.out.println("More books checked out (Y/n) ?");
                                    this.readerFun();
                                    user_discretion = this.input.strip().toLowerCase();
                                } else {

                                    this.statement.executeUpdate(insert_checkout);
                                    this.conn.commit();
                                    this.successInclusionMsg();
                                    System.out.println("More books checked out (Y/n) ?");
                                    this.readerFun();
                                    user_discretion = this.input.strip().toLowerCase();
                                }
                            }
                            this.redirectToHome();
                        }
                    }
                    else{
                        String[] strArr = this.check_insert();
                        table_name = strArr[0];
                        String insert_sql = strArr[1];
                        if (rs.next()){
                            System.out.println("------------------------------------------------------------");
                            System.out.println("Record already exists in the "+table_name+" table & Please try Again");
                            System.out.println("------------------------------------------------------------");
                            this.input = table_name;
                            this.generic_insertion();
                        }
                        else{
                            this.statement.executeUpdate(insert_sql);
                            this.conn.commit();
                            this.successInclusionMsg();
                            System.out.println(" Type 'Yes' or 'No' (y/n) to include more in "+table_name +" table:");
                            this.readerFun();
                            if (this.input.toLowerCase().startsWith("y")){
                                System.out.println("------------------------------------------------------------");
                                this.input = table_name;
                                this.generic_insertion();
                            }
                            else{
                                this.redirectToHome();
                            }
                        }

                    }
                }
                else{
                    this.tableDsnotExMsg();
                    this.input = table_name;
                    this.generic_insertion();
                }

            }
            else {
                this.emptyInputMsg();
                this.input = table_name;
                this.generic_insertion();
            }

        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }

    }
    public void redirectToHome() {

        System.out.println("------------------------------------------------------------");
        System.out.println("Redirecting to Home Page..");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        this.menuPage();
    }

    public void listLibraryUser(String view_sql){
        try{
            this.statement.executeQuery(view_sql);
            this.conn.commit();
            //this.rs = this.statement.executeQuery("SELECT * FROM library_users");
            //System.out.println(this.rs.next());
        }
        catch (SQLException e){
            this.sqlErrorMsg(e);
            this.exceptionFun();
        }
    }

    public void closeDBConnection()
    {
        try{
            this.conn.close();
        }
        catch (Exception e){
            System.out.println("SQL error occured   : "+ e );
            this.exceptionFun();
        }

    }

    public void menuPage() {

        this.createDB();
        this.dbConnection();
        System.out.println("You are on the menu pagek");
        System.out.println("A - Create a table \n" + "B - Include an entry \n" +
                "C - Delete an entry/ entries \n" +
                "D - List a table \n" +
                "1 - Include a new book \n" +
                "2 - Include a new user \n" +
                "3 - List library users with books they checked out \n" +
                "4 - Check out a book \n" +
                "5 - Return a book \n" +
                "6 - Exit");

        System.out.println("type anyone of above mentioned options & click ENTER: ");
        this.readerFun();
        char op_symbol = this.input.strip().toUpperCase().charAt(0);
        HashMap<Character, String> mapOp = mappingOperations();
        if (mapOp.get(op_symbol) == null){
            System.out.println("-------------------------------------------------------");
            System.out.println("**** Chosen Wrong symbol & redirected to Homepage AGAIN *****");
            System.out.println("-------------------------------------------------------");
            this.menuPage();

        }
        else{
            System.out.println((mapOp.get(op_symbol)) + " operation has been chosen");
            if (mapOp.get(op_symbol) == CREATE){
                this.createTable();
            }
            else if (mapOp.get(op_symbol) == INSERT){
                this.insertTable();
            }
            else if (mapOp.get(op_symbol) == REMOVE){
                System.out.println("Enter table name: ");
                this.readerFun();
                this.removeRecords();
            }
            else if (mapOp.get(op_symbol) == PRINTTABLE){
                this.printTable();
            }
            else if (mapOp.get(op_symbol) == INCLUDE_BOOK){
                this.input = "books";
                this.generic_insertion();

            }
            else if (mapOp.get(op_symbol) == INCLUDE_USER){
                this.input = "people";
                this.generic_insertion();
            }
            else if (mapOp.get(op_symbol) == LIST_LIBRARY){
                String view_sql = "SELECT people.name, books.author, books.title FROM people JOIN checkedOutBooks " +
                                //" WHERE people.SNN = checkedOutBooks.SNN AND books.callNo = checkedOutBooks.callNo";
                        " ON PEOPLE.SNN = checkedOutBooks.SNN";
                //"CREATE VIEW \"SCHEMA\".LIBRARY_USERS AS " +
                this.listLibraryUser(view_sql);



            }
            else if (mapOp.get(op_symbol) == CHECKOUT){
                this.input="checkedOutBooks";
                this.generic_insertion();

            }
            else if (mapOp.get(op_symbol) == RETURN_BOOK){
                this.input="checkedOutBooks";
                this.removeRecords();

            }
            else if (mapOp.get(op_symbol) == EXIT){
                System.out.println("Successfully Logged Out");
                System.exit(1);
            }
        }

        this.closeDBConnection();
        System.out.println("closed");
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        MenuDrivenAssgmt dbObj = new MenuDrivenAssgmt();
        dbObj.menuPage();

    }
}

public class MainClass {

    public static void main(String[] args) throws Exception {
        AnalisadorLexico lexico = new AnalisadorLexico(); //inicializa analisador lexico 
        Token token = null;
        try {
            do {
                token = lexico.proximoToken();  // obtem em loop todos os tokens do arquivo passado para analisador lexico
                if (token != null)  System.out.print(token.tipo);
                if (token != null)  System.out.print(token.texto);
            } while (token != null); // ate que nao exista mais tokens a serem lidos
        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }
}

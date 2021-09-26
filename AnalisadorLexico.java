import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalisadorLexico {

    private int estado;
    private boolean ehFinalDoArquivo = false;
    private boolean devolverC = false;
    private char caractereDevolvido;
    private char[] conteudoNeto;  /*retirar essa merda pra manda no verde */
    private int posicao;  /*retirar essa merda pra manda no verde */
    private int linhas;

    /*    Construtor do analisador lexico    */
    public AnalisadorLexico()  {
        try {
            var conteudo = new String(Files.readAllBytes(Paths.get("input.in")), StandardCharsets.UTF_8);
            conteudoNeto = conteudo.toCharArray();
            posicao = 0;
            linhas = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*retirar essa merda toda do construtor pra manda no verde */
    }

    /*Metodo responsavel pela leitura e identificação dos tokens
     que estão no vetor de conteudo do arquivo de entrada*/
    public Token proximoToken() {
        estado = 0;
        ehFinalDoArquivo();/*retirar essa merda pra manda no verde */
        if (ehFinalDoArquivo) return null;/*retirar essa merda pra manda no verde */
        int estadoFinal = 1;
        String textoTokenAtual = "";
        char atual;
        while (true ) {
            try {
                atual = nextChar();
            } catch (Exception e) {
                throw new RuntimeException(linhas + "\n caractere invalido.");
            }
            
            if (ehFinalDoArquivo) return null;
            
            switch (estado) {
                case 0: 
                    if (atual == '!') {
                        estado = 6;
                    } else if (atual == '>') {
                        estado = 5;
                    }  else if (atual == '<') {
                        estado = 4;
                    } else if (atual == '|') {
                        estado = 3;
                    } else if (atual == '&') {
                        estado = 2;
                    } else if (ehTokenValido(atual)) {
                        estado = estadoFinal;
                    } else if (atual == '-') {
                        estado = 7;
                    } else if (atual == '.') {
                        estado = 12;
                    } else if (ehNumero(atual) && atual != '0') {
                        estado = 8;
                    } else if (atual == '0') {
                        estado = 9;
                    } else if (ehChar(atual) || atual == '_') {
                        estado = 14;
                    } else if (atual == '\"') {
                        estado = 15;
                    } else if (atual == '\'') { 
                        estado = 16;
                    } else if (ehEspaco(atual)) { 
                        textoTokenAtual = "";
                        estado = 0;
                    } else {
                        throw new RuntimeException("line 79");
                    }
                    if (estado > 0) textoTokenAtual += atual;
                    break;
        
                case 2:
                    if (atual == '&') {
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_AND);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else if (atual != '&') {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                    break;

                case 3:
                    if (atual == '|') {
                        estado = estadoFinal;
                    } else if (atual != '|') {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                    break;
                case 4:
                    if (atual == '=') { /*<=*/
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_MENORIGUAL);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else if (atual == '-') { /*<-*/ 
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_ATRIBUICAO);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else { /*<*/
                        devolver(atual);
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_MENOR);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                case 5:
                    if (atual == '=') { /*>=*/
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_MAIORIGUAL);
                        token.setTexto(textoTokenAtual);
                        return token;
                 
                    } else { /*>*/
                        devolver(atual);
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_MAIOR);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                case 6:
                    if (atual == '=') { /*!=*/
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_EXCLAMACAOIGUAL);
                        token.setTexto(textoTokenAtual);
                        return token;
                 
                    } else { /*!*/
                        devolver(atual);
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_EXCLAMACAO);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                case 7:
                    if (atual == '.') { /*-.*/
                        textoTokenAtual += atual;
                        estado = 12;

                    } else if (ehNumero(atual)) { /*-4*/
                        textoTokenAtual += atual;
                        estado = 8;
                 
                    } else { /*!*/
                        devolver(atual);
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_MENOS);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                    break;
                case 8: 
                    if (ehNumero(atual)) { /*continua no estado 8 enquanto vier mais numeros*/
                        textoTokenAtual += atual;
                        estado = 8;

                    } else  if (atual == '.') { /*recebe ponto entao vai para estado de numero real*/
                        textoTokenAtual += atual;
                        estado = 12;

                    } else { /*numero inteiro positivo ou negativo*/
                        devolver(atual);
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_INT);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                    break;
                case 9:    
                    if (ehNumero(atual)) { /*01*/
                        textoTokenAtual += atual;
                        estado = 8;
                    } else if (atual == '.') { /*0.*/
                        textoTokenAtual += atual;
                        estado = 12;
                    } else if (atual == 'x' || atual == 'X') { /*0x hexadecimal*/
                        textoTokenAtual += atual;
                        estado = 10;
                    } else {
                        estado = estadoFinal;
                        devolver(atual);
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_INT);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                    break;
                case 10:
                    if (ehCharHexa(atual)){
                        textoTokenAtual += atual;
                        estado = 11;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                case 11:
                    if (ehCharHexa(atual)){
                        textoTokenAtual += atual;
                        estado = estadoFinal;  
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_HEXA);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    } 
                case 12:
                    if (ehNumero(atual)){
                        textoTokenAtual += atual;
                        estado = 13;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                case 13:
                    if (ehNumero(atual)){
                        textoTokenAtual += atual;
                        estado = 13;
                    } else {
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        devolver(atual);
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_DOUBLE);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                case 14:
                    if (ehNumero(atual) || ehChar(atual) || atual == '.' || atual == '_') {
                        textoTokenAtual += atual;
                        estado = 14;
                    } else {
                        estado = estadoFinal;
                        devolver(atual);
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_ID);
                        token.setTexto(textoTokenAtual);
                        return token;
                    }
                case 15:
                    if ( ehNumero(atual) || ehChar(atual) || ehValidoString(atual) ) {
                        textoTokenAtual += atual;
                        estado = 15;
                    } else if (atual == '"'){
                        textoTokenAtual += (char)'$';
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_STRING);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                    break;
                case 16:
                    if ( ehNumero(atual) || ehChar(atual) || ehValidoString(atual) || atual == '\n' || atual == '"' ) {
                        textoTokenAtual += atual;
                        estado = 17;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                    break;
                case 17:
                    if (atual == '\'' ) {
                        textoTokenAtual += atual;
                        estado = estadoFinal;
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_CHAR);
                        token.setTexto(textoTokenAtual);
                        return token;
                    } else {
                        throw new RuntimeException(linhas + "\ncaractere invalido.");
                    }
                case 18:
                    if (atual == '/') {
                        textoTokenAtual += atual;
                        estado = 18;         
                    } else {
                        estado = estadoFinal;
                        devolver(atual);
                        Token token = new Token();
                        token.setTipo(Token.CODIGO_COMENTARIO);
                        token.setTexto(textoTokenAtual);
                        return token; 
                    }        
            }
         
        }
    }

    private boolean ehCharHexa(char c) {
        return ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) || ehNumero(c);
    }

    private boolean ehValidoString (char c) {
        return (ehChar(c) || ehNumero(c) ||  c == ' ' || c == '_' || c == '.' || c == ',' || c == ';' || 
                c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
                c == '+' || c == '-' || c == '\'' || c == '|' || c == '\\' || c == '/' ||
                c == '&' || c == '?' || c == '%' || c == '?'|| c == '!' || c == '>' || c == '<' || c == '=' );
    }

    /*Metodos privados para facilitar na identificação dos tokens */
    private boolean ehTokenValido (char c) {
        return (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' 
            || c == ';' || c == ',' || c == '=' || c == '+'  || c == '=' 
            || c == '%' || c == '?' );
    } 

    private boolean ehNumero (char c) {
        return c >= '0' && c <= '9';
    } 

    private boolean ehChar (char c) {
        return ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' );
    }

    public boolean ehOperador (char c) {
        return c == '<' || c == '>' || c == '=' || c == '!' ||  c == '&' ||  c == '|' ;
    }

    public boolean ehEspaco (char c) {
        return c == ' ' || c == '\n' || (int) c == 13 || c == '\t';
    }   

    /*Metodo privado responsavel por retornar proximo caractere a ser interpretado
        devolve caractere não utilizado em token anterior caso haja
        determina quando o fim da leitura de token
    */
    private char nextChar () throws IOException {
        if (devolverC) {
            devolverC = false;
            return caractereDevolvido;
        } else {
            //int caractere = System.in.read();
            int caractere = nextCharFile(); /*retirar essa merda pra manda no verde */
            contaLinhas(caractere);
            if (caractere == -1) {
                ehFinalDoArquivo = true;
            }
            return (char) caractere;
        }
    }


    private void contaLinhas(int c) {
        if (c == '\n') {
            linhas++;
        } 
    }

    /*Metodo privado responsavel por devolver o caractere quando não é utilizado no token atual */
    private void devolver(char caractere) {
        devolverC = true;
        caractereDevolvido = caractere;
    }

    /*retirar essa merda pra manda no verde */
    private char nextCharFile ()  {
        return conteudoNeto[posicao++];
    }
    /*retirar essa merda pra manda no verde */
    private boolean  ehFinalDoArquivo  () {
        return posicao == conteudoNeto.length;
    }
}


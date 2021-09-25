import java.io.IOException;

public class AnalisadorLexico {

    private int estado;
    private boolean ehFinalDoArquivo = false;
    private boolean devolverC = false;
    private char caractereDevolvido;

    /*    Construtor do analisador lexico    */
    public AnalisadorLexico() {}

    /*Metodo responsavel pela leitura e identificação dos tokens
     que estão no vetor de conteudo do arquivo de entrada*/
    public Token proximoToken() {
        estado = 0;
        int estadoFinal = 1;
        String textoTokenAtual = "";
        while (true ) {
            try {
                char atual = nextChar();
                if (ehFinalDoArquivo) return null;
                textoTokenAtual += atual;
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
                        } else if (ehValido(atual)) {
                            estado = estadoFinal;
                        } else if (atual == '-') {
                            estado = 12;
                        } else if (atual == '.') {
                            estado = 13;
                        } else if (ehNumero(atual) && atual != '0') {
                            estado = 8;
                        } else if (atual == '0') {
                            estado = 9;
                        } else if (ehChar(atual) || atual == '_') {
                            estado = 15;
                        } else if (atual == '\"') {
                            estado = 16;
                        } else if (atual == '\'') {
                            estado = 17;
                        } else if (ehEspaco(atual)) {
                            estado = 0;
                        } else {
                            throw new RuntimeException("line 67");
                        }
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
                            estado = estadoFinal;
                            //TODO: oque acontece caso apareca só um & / 
                        } else {
                            throw new RuntimeException("line 98");
                        }
                        break;
    
                    case 3:
                        if (atual == '|') {
                            estado = estadoFinal;
                        } else if (atual != '|') {
                            estado = estadoFinal;
                            //devolver
                        } else {
                            throw new RuntimeException("line 108");
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
                        
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
         
        }
    }

    /*Metodos privados para facilitar na identificação dos tokens */
    private boolean ehValido (char c) {
        return (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' 
            || c == ';' || c == ',' || c == '=' || c == '+' || c == '-' || c == '=' 
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
        return c == ' ' || c == '\n';
    }   

    /*Metodo privado responsavel por retornar proximo caractere a ser interpretado
        devolve caractere não utilizado em token anterior caso haja
        determina quando o fim da leitura de token
    */
    private char nextChar () throws IOException {
        if (devolverC) {
            return caractereDevolvido;
        } else {
            int caractere = System.in.read();
            if (caractere == -1) {
                ehFinalDoArquivo = true;
            }
            return (char) caractere;
        }
    }

    /*Metodo privado responsavel por devolver o caractere quando não é utilizado no token atual */
    private void devolver(char caractere) {
        devolverC = true;
        caractereDevolvido = caractere;
    }
}


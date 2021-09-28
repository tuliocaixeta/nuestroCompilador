import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


class Compilador {
    
    public static class Token {

        public static final byte CODIGO_AND = 1; 
        public static final byte CODIGO_MENORIGUAL = 2;
        public static final byte CODIGO_ATRIBUICAO = 3;
        public static final byte CODIGO_MENOR = 4;
        public static final byte CODIGO_MAIORIGUAL = 5;
        public static final byte CODIGO_MAIOR = 6;
        public static final byte CODIGO_EXCLAMACAOIGUAL = 7;
        public static final byte CODIGO_EXCLAMACAO = 8;
        public static final byte CODIGO_MENOS = 9;
        public static final byte CODIGO_BARRA = 10;
        public static final byte CODIGO_PIPE = 11;
        public static final byte CODIGO_IGUAL = 12;
        public static final byte CODIGO_ABREPARENTESES = 13;
        public static final byte CODIGO_FECHAPARENTESES = 14;
        public static final byte CODIGO_VIRGULA = 15;
        public static final byte CODIGO_MAIS = 16;
        public static final byte CODIGO_ASTERISCO = 17;
        public static final byte CODIGO_PONTOVIRGULA = 18;
        public static final byte CODIGO_ABRECHAVES = 19;
        public static final byte CODIGO_FECHACHAVES = 20;
        public static final byte CODIGO_ABRECOCHETES = 21;
        public static final byte CODIGO_FECHACOCHETES = 22;

        public static final byte CODIGO_CONST = 23;
        public static final byte CODIGO_CHAR = 24;
        public static final byte CODIGO_FLOAT = 25;
        public static final byte CODIGO_INT = 26;
        public static final byte CODIGO_ELSE = 27;
        public static final byte CODIGO_WHILE = 28;
        public static final byte CODIGO_IF = 29;
        public static final byte CODIGO_READLN = 30;
        public static final byte CODIGO_DIV = 31;
        public static final byte CODIGO_WRITELN = 32;
        public static final byte CODIGO_MOD = 33;
        public static final byte CODIGO_WRITE = 34;

        public static final byte CODIGO_ID = 35;
        public static final byte CODIGO_CONSTANTE = 36;


        public byte numToken;
        public String texto;
        public String tipo;
        public int endereco;
        public int tamanho = 0;

    
        public Token  (byte numToken , String texto) {
            super();
            this.numToken = numToken;
            this.texto = texto;
        }

        public Token  (byte numToken , String texto, int endereco) {
            super();
            this.numToken = numToken;
            this.texto = texto;
        }
    
        public Token() {
            super();
        }
    
        public void setNumToken (byte numToken) {
            this.numToken = numToken;
        }
    
        public void setTexto (String texto) {
            this.texto = texto;
        }

        public void setTipo (String tipo) {
            this.tipo = tipo;
        }
        
    
        public byte getNumToken () {
            return numToken;
        }
    
        public String getTexto () {
            return texto;
        }

        public String getTipo () {
            return tipo;
        }
    }
        
    public static class AnalisadorLexico {

        private int estado;
        private boolean ehFinalDoArquivo = false;
        private boolean devolverC = false;
        private char caractereDevolvido;
        private char[] conteudoNeto;  /*retirar essa merda pra manda no verde */
        private int posicao;  /*retirar essa merda pra manda no verde */
        private int linhas; 
        public TabelaSimbolo tabelaSimbolo;
        /*    Construtor do analisador lexico    */
        public AnalisadorLexico(TabelaSimbolo tabelaSimbolo)  {
            try {
                var conteudo = new String(Files.readAllBytes(Paths.get("input.in")), StandardCharsets.UTF_8);
                conteudoNeto = conteudo.toCharArray();
                posicao = 0;
                this.tabelaSimbolo = tabelaSimbolo;
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
          
          
            int estadoFinal = 1;
            String textoTokenAtual = "";
            char atual;
            while (true ) {
                try {
                    atual = nextChar();
                } catch (Exception e) {
                    throw new RuntimeException(linhas + "\n caractere invalido.");
                }
                if ( ehFinalDoArquivo()) {/*retirar essa merda pra manda no verde */
                    if (estado > 1) {
                        throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
                    } else if (estado == 0) {
                        System.out.print(linhas + " linhas compiladas.");
                    }
                    return null;
                }
                if (ehFinalDoArquivo) {
                    if (estado > 1) {
                        throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
                    } else if (estado == 0) {
                        System.out.print(linhas + " linhas compiladas.");
                    }
                    return null;
                }
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
                            textoTokenAtual += atual;
                            Token token = geraTokenReservado(atual);
                            token.texto = textoTokenAtual;
                            return retornar(token);  
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
                        } else if (atual == '/') { 
                            estado = 18;
                        } else if (!ehValido(atual)){
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        } else {
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+atual+"].");
                        }
                        if (estado > 1) textoTokenAtual += atual;
                        break;
            
                    case 2:
                        if (atual == '&') {
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_AND);
                            token.setTexto(textoTokenAtual);
                            return retornar(token);
                        } else if (atual != '&') {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
    
                    case 3:
                        if (atual == '|') {
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_PIPE);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        } else if (atual != '|') {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 4:
                        if (atual == '=') { /*<=*/
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_MENORIGUAL);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        } else if (atual == '-') { /*<-*/ 
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_ATRIBUICAO);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        } else { /*<*/
                            devolver(atual);
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_MENOR);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        }
                    case 5:
                        if (atual == '=') { /*>=*/
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_MAIORIGUAL);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                     
                        } else { /*>*/
                            devolver(atual);
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_MAIOR);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        }
                    case 6:
                        if (atual == '=') { /*!=*/
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_EXCLAMACAOIGUAL);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                     
                        } else { /*!*/
                            devolver(atual);
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_EXCLAMACAO);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
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
                            token.setNumToken(Token.CODIGO_MENOS);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
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
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_inteiro");
                            return retornar(token); 
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
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_inteiro");
                            return retornar(token); 
                        }
                        break;
                    case 10:
                        if (ehCharHexa(atual)){
                            textoTokenAtual += atual;
                            estado = 11;
                        } else if (ehValido(atual)){
                            textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        } else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                    case 11:
                        if (ehCharHexa(atual)){
                            textoTokenAtual += atual;
                            estado = estadoFinal;  
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_caractere");
                            return retornar(token); 
                        } else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        } 
                    case 12:
                        if (ehNumero(atual)){
                            textoTokenAtual += atual;
                            estado = 13;
                        } else if (ehValido(atual)){
                            textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 13:
                        if (ehNumero(atual)){
                            textoTokenAtual += atual;
                            estado = 13;
                        } else {
                            estado = estadoFinal;
                            devolver(atual);
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_float");
                            return retornar(token); 
                        }
                        break;
                    case 14: // continua concatenando caracteres de Identificador caso seja verdadeiro
                        if (ehNumero(atual) || ehChar(atual) || atual == '.' || atual == '_') {
                            textoTokenAtual += atual;
                            estado = 14;
                        } else {  // Retorna token ID ou token palavra reservada
                            estado = estadoFinal;
                            devolver(atual);
                            return definirTokenSeTokenIdEhPalavraReservada(textoTokenAtual);
                        }
                        break;
                    case 15:
                        if ( ehNumero(atual) || ehChar(atual) || ehValidoString(atual) ) {
                            textoTokenAtual += atual;
                            estado = 15;
                        } else if (atual == '"'){
                            textoTokenAtual += (char)'$';
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_string");
                            return retornar(token); 
                        } else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 16:
                        if ( ehNumero(atual) || ehChar(atual) || ehValido(atual)  ) {
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
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_caractere");
                            return retornar(token); 
                        } else if (!ehValido(atual)){
                            textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                    case 18: 
                        if (atual == '*') {
                            textoTokenAtual += atual;
                            estado = 19;         
                        } else  {
                            estado = estadoFinal;
                            devolver(atual);
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_BARRA);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        }    
                        break;  
                    case 19:
                        if (atual == '*') {
                            textoTokenAtual += atual;
                            estado = 20;         
                        } else {
                            textoTokenAtual += atual;
                            estado = 19;
                        }
                        break;
                    case 20:
                        if (atual == '/') {
                            textoTokenAtual = "";
                            estado = 0;
                        } else {
                            textoTokenAtual += atual;
                            estado = 19;
                        }     
                        break;
                }
            }
        }
    
        private Token definirTokenSeTokenIdEhPalavraReservada(String lexema) {
            Token token = tabelaSimbolo.tabela.get(lexema);
            if (token != null) return token;
            else {
                return tabelaSimbolo.inserirIdentificador(new Token(Token.CODIGO_ID, lexema));
            }
        }

        private Token retornar(Token token) {
            Token tokenObtido = tabelaSimbolo.tabela.get(token.texto);
            if (tokenObtido != null) {
                return tokenObtido;
            } else {
                return tabelaSimbolo.inserirConstante(token);
            }
        }

        private Token geraTokenReservado(char atual) {
            Token token = new Token();
            switch (atual){
                case '=':
                    token.setNumToken(Token.CODIGO_IGUAL);
                    break;
                case '(':
                    token.setNumToken(Token.CODIGO_ABREPARENTESES);
                    break;
                case ')':
                    token.setNumToken(Token.CODIGO_FECHAPARENTESES);
                    break;
                case ',':
                    token.setNumToken(Token.CODIGO_VIRGULA);
                    break;
                case '+':
                    token.setNumToken(Token.CODIGO_MAIS);
                    break;
                case '*':
                    token.setNumToken(Token.CODIGO_ASTERISCO);
                    break;
                case ';':
                    token.setNumToken(Token.CODIGO_PONTOVIRGULA);
                    break;
                case '{':
                    token.setNumToken(Token.CODIGO_ABRECHAVES);
                    break;
                case '}':
                    token.setNumToken(Token.CODIGO_FECHACHAVES);
                    break;
                case '[':
                    token.setNumToken(Token.CODIGO_ABRECOCHETES);
                    break;
                case ']':
                    token.setNumToken(Token.CODIGO_FECHACOCHETES);
                    break;
    
            }
            return token;
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
    
        private boolean ehValido (char c) {
            return (ehChar(c) || ehNumero(c) ||  c == ' ' || c == '_' || c == '.' || c == ',' || c == ';' || 
                    c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
                    c == '+' || c == '-' || c == '\'' || c == '|' || c == '\\' || c == '/' || c == '\n' || c == '"' ||
                    c == '&' || c == '?' || c == '%' || c == '?'|| c == '!' || c == '>' || c == '<' || c == '=' );
        }
    
        /*Metodos privados para facilitar na identificação dos tokens */
        private boolean ehTokenValido (char c) {
            return (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' 
                || c == ';' || c == ',' || c == '=' || c == '+'   
                || c == '%' || c == '*'  );
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
                int caractere = System.in.read();
                //int caractere = nextCharFile(); /*retirar essa merda pra manda no verde */
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
    public static class TabelaSimbolo {
        int endereco = 0;
        public HashMap<String, Token> tabela = new HashMap<>();
        public TabelaSimbolo() {
            super();
            tabela.put("const", new Token(Token.CODIGO_CONST, "const", endereco++));
            tabela.put("int", new Token(Token.CODIGO_INT, "int", endereco++));
            tabela.put("char", new Token(Token.CODIGO_CHAR, "char", endereco++));
            tabela.put("while", new Token(Token.CODIGO_WHILE, "while", endereco++));
            tabela.put("if", new Token(Token.CODIGO_IF, "if", endereco++));
            tabela.put("float", new Token(Token.CODIGO_FLOAT, "float", endereco++));
            tabela.put("else", new Token(Token.CODIGO_ELSE, "else", endereco++));
            tabela.put("readln", new Token(Token.CODIGO_READLN, "readln", endereco++));
            tabela.put("div", new Token(Token.CODIGO_DIV, "div", endereco++));
            tabela.put("write", new Token(Token.CODIGO_WRITE, "write", endereco++));
            tabela.put("writeln", new Token(Token.CODIGO_WRITELN, "writeln", endereco++));
            tabela.put("mod", new Token(Token.CODIGO_MOD, "mod", endereco++));
            tabela.put("&&", new Token(Token.CODIGO_AND, "&&", endereco++));
            tabela.put("||", new Token(Token.CODIGO_PIPE, "||", endereco++));
            tabela.put("!", new Token(Token.CODIGO_EXCLAMACAO, "!", endereco++));
            tabela.put("<-", new Token(Token.CODIGO_ATRIBUICAO, "<-", endereco++));
            tabela.put("=", new Token(Token.CODIGO_IGUAL, "=", endereco++));
            tabela.put("(", new Token(Token.CODIGO_ABREPARENTESES, "(", endereco++));
            tabela.put(")", new Token(Token.CODIGO_FECHAPARENTESES, ")", endereco++));
            tabela.put("<", new Token(Token.CODIGO_MENOR, "<", endereco++));
            tabela.put(">", new Token(Token.CODIGO_MAIOR, ">", endereco++));
            tabela.put("!=", new Token(Token.CODIGO_EXCLAMACAOIGUAL, "!=", endereco++));
            tabela.put(">=", new Token(Token.CODIGO_MAIORIGUAL, ">=", endereco++));
            tabela.put("<=", new Token(Token.CODIGO_MENORIGUAL, "<=", endereco++));
            tabela.put(",", new Token(Token.CODIGO_VIRGULA, ",", endereco++));
            tabela.put("+", new Token(Token.CODIGO_MAIS, "+", endereco++));
            tabela.put("-", new Token(Token.CODIGO_MENOS, "-", endereco++));
            tabela.put("*", new Token(Token.CODIGO_ASTERISCO, "*", endereco++));
            tabela.put("/", new Token(Token.CODIGO_BARRA, "/", endereco++));
            tabela.put(";", new Token(Token.CODIGO_PONTOVIRGULA, ";", endereco++));
            tabela.put("{", new Token(Token.CODIGO_ABRECHAVES, "{", endereco++));
            tabela.put("}", new Token(Token.CODIGO_FECHACHAVES, "}", endereco++));
            tabela.put("[", new Token(Token.CODIGO_ABRECOCHETES, "[", endereco++));
            tabela.put("]", new Token(Token.CODIGO_FECHACOCHETES, "]", endereco++));
        }

        public Token inserirConstante (Token token) {
            token.endereco = endereco++; //todo: conferir se esta gravando no proximo
            tabela.put(token.texto, token);
            return tabela.get(token.texto);
        }

        public Token inserirIdentificador (Token token) {
            token.endereco = endereco++;
            tabela.put(token.texto, token);
            return tabela.get(token.texto);
        }

        // todo: metodo recuperar endereço de simbolo
    }


    
    public static void main(String[] args) throws Exception {
        TabelaSimbolo tabelaSimbolo = new TabelaSimbolo();
        AnalisadorLexico lexico = new AnalisadorLexico(tabelaSimbolo); //inicializa analisador lexico 
        Token token = null;
        try {
            do {
                token = lexico.proximoToken();  // obtem em loop todos os tokens do arquivo passado para analisador lexico
                if (token != null)  System.out.println("token = [" +token.numToken +" , "+ token.texto+" , "+ token.tipo+"]");
            } while (token != null); // ate que nao exista mais tokens a serem lidos
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

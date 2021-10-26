/**  COMPONENTES DO GRUPO
 * Túlio Gomes Caixeta Antunes
 * Guilherme Silva Rabelo      */  


import java.io.IOException;
/*import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;   CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE*/ 
import java.util.HashMap;

class Compilador {
    public static int linhas;
    public static int contadorPrecisao;
    public static class AnalisadorSintatico {

        AnalisadorLexico lexico;
        TabelaSimbolo tabela;
        Token token; 

        public AnalisadorSintatico() {
            tabela = new TabelaSimbolo();
            lexico = new AnalisadorLexico(tabela);
            token = lexico.proximoToken();
        }

        /* Verifica se token recebido é esperado  caso seja diferente de fim de arquivo*/
        public void casaToken(byte numTokenEsperado) {
            if( token != null ){
                if( token.getNumToken() == numTokenEsperado){
                    token = lexico.proximoToken();   /*Chama o proximo lexema identificado pelo Analisador Lexico*/ 
                } else {/* se token esperado não é o que foi identificado retorna erro*/
                    throw new RuntimeException(linhas + "\ntoken nao esperado ["+token.texto+"].");  
                }   
            } else { /* se fim de arquivo retorna erro */
                throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
            }
        }

        public void S () { /*Inicio da Gramatica {Declaracao U Comandos}*/
            if (token == null) throw new RuntimeException( linhas + " linhas compiladas."); 
            while (token != null) {
                while (token != null && (ehDeclaracao()) || token != null && token.getNumToken() == Token.CODIGO_CONST ) {
                    declaracao(); 
                } 
                while (token != null && (ehComando() || token.getNumToken() == Token.CODIGO_PONTOVIRGULA ||  token.getNumToken() == Token.CODIGO_ABRECHAVES)) {
                    comandos();
                } 
            }
        }

        private boolean ehDeclaracao() { /*Verifica se token é inicio de uma declaração */
            return token.getNumToken() == Token.CODIGO_INT || token.getNumToken() == Token.CODIGO_FLOAT ||
                     token.getNumToken() == Token.CODIGO_CHAR || token.getNumToken() == Token.CODIGO_STRING;
        }

        private void declaracao() {
            if (token != null) {
                if (ehDeclaracao() ){  /** Forma declarações para : ("char" | "string" | "int" | "float") Lista-de-ids ";" */
                    casaToken(token.getNumToken());
                    casaToken(Token.CODIGO_ID);
                    if (token != null && token.getNumToken() == Token.CODIGO_ATRIBUICAO ) {
                        atrubuicaoDeclaracao();
                    }
                     /** Caso seja lista de id : ID [<- constante]{"," ID  [<- constante]}* */
                    if (token != null && token.getNumToken() == Token.CODIGO_VIRGULA ) {
                       
                        while ( token != null && token.getNumToken() == Token.CODIGO_VIRGULA ) {
                            casaToken(Token.CODIGO_VIRGULA);
                            casaToken(Token.CODIGO_ID);
                            if (token != null && token.getNumToken() == Token.CODIGO_ATRIBUICAO ) {
                                atrubuicaoDeclaracao();
                            }
                        }
                    }
                    casaToken(Token.CODIGO_PONTOVIRGULA);
                    if (token == null) {
                        throw new RuntimeException( linhas + " linhas compiladas.");
                    }
                /** Forma declarações para const: "const" ID "=" Exp ";" */
                } else if (token != null && token.getNumToken() == Token.CODIGO_CONST ) { 
                    casaToken(Token.CODIGO_CONST);
                    casaToken(Token.CODIGO_ID);
                    casaToken(Token.CODIGO_IGUAL);
                    expressao();
                    casaToken(Token.CODIGO_PONTOVIRGULA);
                    if (token == null) {
                        throw new RuntimeException( linhas + " linhas compiladas.");
                     }
                }
             } else {
                throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
            }
        }

       /** "{" Comando* "}" | Comando */
        private void comandos() { 
            if (token != null) {
                if (token.getNumToken() ==  Token.CODIGO_ABRECHAVES){ /**Bloco de Comando */
                    casaToken(Token.CODIGO_ABRECHAVES);
                    while (token != null &&  ehComando()) {
                       comando();
                    }
                    casaToken(Token.CODIGO_FECHACHAVES);
                  
                } else { /** Comando sem bloco */
                    comando();
                }
                if (token == null) {
                   throw new RuntimeException( linhas + " linhas compiladas.");
                }
            } else {
                throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
            }
        }

        /** Comando + ;   :: [Atribuicao | Repeticao | Teste | Nulo | Leitura | Escrita] ";" */
        private void comando() {
            if (token != null) {
                if (token.getNumToken() ==  Token.CODIGO_ID){ 
                    atribuicao();
                } else if (token.getNumToken() ==  Token.CODIGO_WHILE){
                    repeticao();
                } else if (token.getNumToken() ==  Token.CODIGO_IF){
                    teste();
                } else if (token.getNumToken() ==  Token.CODIGO_READLN){
                    leitura();
                } else if (token.getNumToken() ==  Token.CODIGO_WRITE || token.getNumToken() ==  Token.CODIGO_WRITELN){
                    escrita();
                }
                if (token.getNumToken() == Token.CODIGO_PONTOVIRGULA) { /**Casa ; para todos os tipos de comando */
                    casaToken(Token.CODIGO_PONTOVIRGULA);
                }
               
                if (token == null) { /**Caso EOF sucesso*/
                    throw new RuntimeException( linhas + " linhas compiladas.");
                }
            } else {
                throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
            }
        }

        /** ExpressaoInterna Comparação ExpressaoInterna */
        private void expressao() {
            expressaoInterna(); /**Expressao */
            if (token != null && ehRelacional()) { /** Expressao [ < Expressao] */
                casaToken(token.getNumToken());
                expressaoInterna(); 
            }
        }

        /** Buscar termo com sinal , e se houver casa outro termo : [+|-] T {(+|-|"||") T}*/
        private void expressaoInterna() { 
            if (token != null && token.getNumToken() == Token.CODIGO_MAIS ) {
                casaToken(Token.CODIGO_MAIS);
            } else if (token != null && token.getNumToken() == Token.CODIGO_MENOS ) {
                casaToken(Token.CODIGO_MENOS);
            }
            termo();
            while (token != null && (token.getNumToken() == Token.CODIGO_MAIS ||
                    token.getNumToken() == Token.CODIGO_MENOS || 
                    token.getNumToken() == Token.CODIGO_PIPE )) {
                casaToken(token.getNumToken());
                termo();
            }
        }

        /** Busca fator com operação se houver  {(*|/|&&|div|mod) F} */
        private void termo() {
            fator();
            if (token != null) {
                 /**Enquanto houver mais fatores para comparar */
                while ( token.getNumToken() == Token.CODIGO_ASTERISCO ||token.getNumToken() == Token.CODIGO_BARRA ||
                        token.getNumToken() == Token.CODIGO_AND || token.getNumToken() == Token.CODIGO_DIV ||
                        token.getNumToken() == Token.CODIGO_MOD ) {
                    casaToken(token.getNumToken());
                    if (token != null) { 
                       fator(); 
                    }
                }
            }
        }

        /**  ID["[" Exp "]"] | constante |não F | P | "int" P | "float" P */
        private void fator() {
            if (token.getNumToken() == Token.CODIGO_ID) {
                casaToken(Token.CODIGO_ID);
                if (token.getNumToken() == Token.CODIGO_ABRECOCHETES) { 
                    casaToken(Token.CODIGO_ABRECOCHETES);
                    expressao();
                    casaToken(Token.CODIGO_FECHACOCHETES);
                }
            } else if (token.getNumToken() == Token.CODIGO_CONSTANTE) {
                casaToken(Token.CODIGO_CONSTANTE);
            } else if (token.getNumToken() == Token.CODIGO_EXCLAMACAO) {
                casaToken(Token.CODIGO_EXCLAMACAO);
                fator();
            } else if (token.getNumToken() == Token.CODIGO_ABREPARENTESES) {
                parenteses();
            } else if (token.getNumToken() == Token.CODIGO_INT) {
                casaToken(Token.CODIGO_INT);
                parenteses();
            } else if (token.getNumToken() == Token.CODIGO_FLOAT) {
                casaToken(Token.CODIGO_FLOAT);
                parenteses();
            }else {
                throw new RuntimeException(linhas + "\ntoken nao esperado ["+token.texto+"].");
            }
        } 

        /**Faz Expressao entre parenteses  */
        private void parenteses() {
            casaToken(Token.CODIGO_ABREPARENTESES);
            expressao();
            casaToken(Token.CODIGO_FECHAPARENTESES);
        }

        /* Faz a escrita (write|writeln) "("Exp {"," Exp}*")" */
        private void escrita() {
            casaToken(token.getNumToken());
            casaToken(Token.CODIGO_ABREPARENTESES);
            expressao();
            if (token != null && token.getNumToken() == Token.CODIGO_VIRGULA ) {
                while (token != null && token.getNumToken() == Token.CODIGO_VIRGULA ) {
                    casaToken(Token.CODIGO_VIRGULA);
                    expressao();
                }
            }
            casaToken(Token.CODIGO_FECHAPARENTESES);
        }

        /** Faz a leitura : readln"(" ID | Exp ")" */
        private void leitura() {
            casaToken(Token.CODIGO_READLN);
            casaToken(Token.CODIGO_ABREPARENTESES);
            if (token != null && token.getNumToken() ==  Token.CODIGO_ID){
                casaToken(Token.CODIGO_ID);
            } else {
                expressao();
            }
            casaToken(Token.CODIGO_FECHAPARENTESES);
        }

        /** Faz a teste : if Exp Comandos else Comandos */
        private void teste() {
            casaToken(Token.CODIGO_IF);
            expressao(); 
            comandos();
            casaToken(Token.CODIGO_ELSE);
            comandos();
        }
        /** Faz a repetição : while Exp Comandos */
        private void repeticao() {
            casaToken(Token.CODIGO_WHILE);
            expressao(); 
            comandos();
        }
        /** Faz a escrita : ID ["["Exp"]"] "<-" Exp */
        private void atribuicao() {
            casaToken(Token.CODIGO_ID);
            if (token != null && token.getNumToken() ==  Token.CODIGO_ABRECOCHETES){
                casaToken(Token.CODIGO_ABRECOCHETES);
                expressao(); 
                casaToken(Token.CODIGO_FECHACOCHETES);
            }
            casaToken(Token.CODIGO_ATRIBUICAO);
            expressao();
        }

        private void atrubuicaoDeclaracao() {
            casaToken(Token.CODIGO_ATRIBUICAO);
            casaToken(Token.CODIGO_CONSTANTE);
        }

        private boolean ehComando() {
            return ( token.getNumToken() == Token.CODIGO_ID || token.getNumToken() == Token.CODIGO_ATRIBUICAO ||
            token.getNumToken() == Token.CODIGO_WHILE || token.getNumToken() == Token.CODIGO_IF || 
            token.getNumToken() == Token.CODIGO_PONTOVIRGULA || token.getNumToken() == Token.CODIGO_READLN ||
            token.getNumToken() == Token.CODIGO_WRITE || token.getNumToken() == Token.CODIGO_WRITELN  );
        }

        private boolean ehRelacional() {
            return ( token.getNumToken() == Token.CODIGO_IGUAL || token.getNumToken() == Token.CODIGO_EXCLAMACAOIGUAL ||
            token.getNumToken() == Token.CODIGO_MENOR || token.getNumToken() == Token.CODIGO_MAIOR || 
            token.getNumToken() == Token.CODIGO_MENORIGUAL || token.getNumToken() == Token.CODIGO_MAIORIGUAL);
        }

        public void verificaFinalArquivo() {
            if (lexico.ehFinalDoArquivo) {
                throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
            }
         }
    }

    /* classe que representa um:
    * registro único de escopo global contendo campo para o
    * número do token, lexema, endereço de inserção na tabela (somente para
    * identificadores e palavras reservadas), tipo e tamanho em bytes*/
    public static class Token {

        /**Bytes utilizados para popular tabela de simbolo e casar tokens */
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
        public static final byte CODIGO_STRING = 35;

        public static final byte CODIGO_ID = 36;
        public static final byte CODIGO_CONSTANTE = 37;


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
    /**Analisador Lexico, retorna objeto token contendo lexema, byte de numToken e Tipo para constantes */
    public static class AnalisadorLexico {

        private int estado;
        private boolean ehFinalDoArquivo = false;
        private boolean devolverC = false;
        private char caractereDevolvido;
        // private char[] conteudoNeto;  /*retirar essa merda pra manda no verde */
        // private int posicao;  /*retirar essa merda pra manda no verde */

        public TabelaSimbolo tabelaSimbolo;
        /*    Construtor do analisador lexico    */
        public AnalisadorLexico(TabelaSimbolo tabelaSimbolo)  {
            try {
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE vv
                //  var conteudo = new String(Files.readAllBytes(Paths.get("input.in")), StandardCharsets.UTF_8);
                //  conteudoNeto = conteudo.toCharArray();
                //  posicao = 0;
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
                this.tabelaSimbolo = tabelaSimbolo;
                linhas = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            /*retirar essa merda toda do construtor pra manda no verde */
        }
        public int getlinha () {
            return linhas;
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
                    //System.out.println("estado = [" +estado+"]");
                } catch (Exception e) {
                    throw new RuntimeException(linhas + "\n caractere invalido.");
                }
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE vv
                // if ( ehFinalDoArquivo()) {/*retirar essa merda pra manda no verde */
                //     if (estado >= 16 && estado <= 20) {
                //         throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
                //     } else if (estado > 1 && estado < 16 && estado != 14  && estado != 13  && estado != 8  ) {
                //         throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                //     } else  if (estado == 14) {
                //         return definirTokenSeTokenIdEhPalavraReservada(textoTokenAtual);  
                //     } else if (estado == 8) {
                //         Token token = new Token();
                //         token.setNumToken(Token.CODIGO_CONSTANTE);
                //         token.setTexto(textoTokenAtual);
                //         token.setTipo("tipo_inteiro");
                //         return retornar(token); 
                //     } else if (estado == 13) {
                //         Token token = new Token();
                //         token.setNumToken(Token.CODIGO_CONSTANTE);
                //         token.setTexto(textoTokenAtual);
                //         token.setTipo("tipo_float");
                //         return retornar(token); 
                //     }
                //     return null;
                // }
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
                if (ehFinalDoArquivo) {  // realiza tratativas de quando é final de arquivo, para cada estado do automato
                    if (estado >= 16 && estado <= 20) {
                        throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
                    } else if (estado > 1 && estado < 16 && estado != 14  && estado != 13  && estado != 8  ) {
                        throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                    } else  if (estado == 14) {
                        return definirTokenSeTokenIdEhPalavraReservada(textoTokenAtual);  
                    } else if (estado == 8) {
                        Token token = new Token();
                        token.setNumToken(Token.CODIGO_CONSTANTE);
                        token.setTexto(textoTokenAtual);
                        token.setTipo("tipo_inteiro");
                        return retornar(token); 
                    } else if (estado == 13) {
                        Token token = new Token();
                        token.setNumToken(Token.CODIGO_CONSTANTE);
                        token.setTexto(textoTokenAtual);
                        token.setTipo("tipo_float");
                        return retornar(token); 
                    }
                    return null;
                }
                switch (estado) {
                    case 0: // Estado inicial do automato começa a busca pelo primeiro caractere do lexema
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
                        } else if ((int)atual == 65535) { 
                            return null;
                        } else if (!ehValido(atual)){
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        } else {
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+atual+"].");
                        }
                        if (estado > 1) textoTokenAtual += atual;
                        break;
            
                    case 2: // lexema &&
                        if (atual == '&') {
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_AND);
                            token.setTexto(textoTokenAtual);
                            return retornar(token);
                        
                        } else if (atual != '&') {
                            if (ehValido(atual) || atual == -1 || (int)atual == 65535){
                                //textoTokenAtual += atual;
                                throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                            } else {
                                throw new RuntimeException(linhas + "\ncaractere invalido.");
                            }
                        }
                        break;
    
                    case 3: // Lexema ||
                        if (atual == '|') {
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_PIPE);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        } else if (atual != '|') {
                            if (ehValido(atual)|| atual == -1 || (int)atual == 65535){
                                //textoTokenAtual += atual;
                                throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                            } else {
                                throw new RuntimeException(linhas + "\ncaractere invalido.");
                            }
                        }
                        break;
                    case 4: // Lexemas <= <- <
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
                    case 5: // Lexemas >= >
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
                    case 6: // Lexemas !=  e  !
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
                    case 7: // Lexemas de numero negativo, iniciam com - , INT OU FLOAT
                        if (atual == '.') { /*  -. negativo float   */ 
                            textoTokenAtual += atual;
                            estado = 12;
    
                        } else if (ehNumero(atual)) { /*-4    negativo int */
                            textoTokenAtual += atual;
                            estado = 8;
                     
                        } else { /*   -  lexema "-" (menos) */
                            devolver(atual);
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_MENOS);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        }
                        break;
                    case 8: // Lexema ::  numeros inteiros reais positivos e nulos
                        if (ehNumero(atual)) { /*continua no estado 8 concatenando no lexema enquanto vier mais numeros*/
                            textoTokenAtual += atual;
                            estado = 8;
    
                        } else  if (atual == '.') { /*recebe ponto entao vai para estado de numero real*/
                            textoTokenAtual += atual;
                            estado = 12;
    
                        } else { /*retorna lexema de numero inteiro positivo ou negativo*/
                            devolver(atual);
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_inteiro");
                            return retornar(token); 
                        }
                        break;
                    case 9: /** Lexema numeros iniciados por 0 Zero, hexa, 0.4 (float) , 02131 (int) */   
                        if (ehNumero(atual)) { /*01*/
                            textoTokenAtual += atual;
                            estado = 8;
                        } else if (atual == '.') { /*0.*/
                            textoTokenAtual += atual;
                            estado = 12;
                        } else if (atual == 'x' || atual == 'X') { /*0x hexadecimal*/
                            textoTokenAtual += atual;
                            estado = 10;
                        } else { /**  Retorna token de lexema constante do tipo inteiro*/
                            estado = estadoFinal;
                            devolver(atual);
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_inteiro");
                            return retornar(token); 
                        }
                        break;
                    case 10: //Estado responsavel pelo primeiro digito hexadecimal: Lexema  0xD 
                        if (ehCharHexa(atual)){/*0xD hexadecimal*/
                            textoTokenAtual += atual;
                            estado = 11;
                        } else if (ehValido(atual)|| atual == -1 || (int)atual == 65535){ /*0xT hexadecimal*/
                            //textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        } else { /*0x# hexadecimal*/
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 11:// Estado responsavel pelo segundo digito hexadecimal: Lexema  0xDD
                        if (ehCharHexa(atual)){ /*0xDD hexadecimal*/
                            textoTokenAtual += atual;
                            estado = estadoFinal;  
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_caractere");
                            return retornar(token); 
                        } else if (ehValido(atual)|| atual == -1 || (int)atual == 65535){ /*0xDT hexadecimal*/
                            //textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        } else { /*0xD# hexadecimal*/
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                    case 12: // Estado que concatena primeiro digito apos ponto de numero float : Lexema 5.5
                        if (ehNumero(atual)){
                            textoTokenAtual += atual;
                            estado = 13;
                            contadorPrecisao = 1;
                        } else if (ehValido(atual)|| atual == -1 || (int)atual == 65535){
                            //textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;

                    case 13: // Estado que concatena demais digitos apos ponto de numero float : Lexema 5.546763, ate precisao de 6 casas
                        if (ehNumero(atual) && contadorPrecisao <= 6){
                            textoTokenAtual += atual;
                            estado = 13;
                            contadorPrecisao++;
                        } else { // retorna token constante tipo float
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
                            if (textoTokenAtual.length() > 32)
                                throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        } else {  // Retorna token ID ou token palavra reservada
                            estado = estadoFinal;
                            devolver(atual);
                            return definirTokenSeTokenIdEhPalavraReservada(textoTokenAtual);
                        }
                        break;
                    case 15: // Estado responsavel por lexemas constantes do tipo string
                        if ( ehValidoString(atual) && textoTokenAtual.length() <= 255 ) {
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
                        } else if ( ( ehValido(atual) || atual == -1 || (int)atual == 65535)){
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 16: // Estado em que é lido conteudo do caractere : Lexema constante tipo caractere
                        if (atual != '\n' &&( ehNumero(atual) || ehChar(atual) || ehValido(atual) ) ) {
                            textoTokenAtual += atual;
                            estado = 17;
                        }  else if ( ( atual == '\n' || atual == -1 || (int)atual == 65535)){
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                        break;
                    case 17: // Estado em que é lido a '' que fecha o caractere : Lexema constante tipo caractere
                        if (atual == '\'' ) {
                            textoTokenAtual += atual;
                            estado = estadoFinal;
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_CONSTANTE);
                            token.setTexto(textoTokenAtual);
                            token.setTipo("tipo_caractere");
                            return retornar(token); 
                        } else if (  ehValido(atual) ){//textoTokenAtual += atual;
                            throw new RuntimeException(linhas + "\nlexema nao identificado ["+textoTokenAtual+"].");
                        } else if ( atual == -1 || (int)atual == 65535){
                            throw new RuntimeException(linhas + "\nfim de arquivo nao esperado.");
                        }else {
                            throw new RuntimeException(linhas + "\ncaractere invalido.");
                        }
                    case 18: // Comentario: Obtem /*
                        if (atual == '*') {
                            textoTokenAtual += atual;
                            estado = 19;         
                        } else  if (atual != '*' && ehValido(atual)){
                            estado = estadoFinal;
                            devolver(atual);
                            Token token = new Token();
                            token.setNumToken(Token.CODIGO_BARRA);
                            token.setTexto(textoTokenAtual);
                            return retornar(token); 
                        }    
                        break;  
                    case 19:// Comentario: Obtem /* dwqedwq * 
                        if (atual == '*') { // se for * pode ser fim de comentario
                            textoTokenAtual += atual;
                            estado = 20;         
                        } else { // enquanto nao for * leia os caracteres do comentario
                            textoTokenAtual += atual;
                            estado = 19;
                        }
                        break;
                    case 20: // Fim de comentario
                        if (atual == '/') { // se / entao fim de comentario
                            textoTokenAtual = "";
                            estado = 0;
                        } else if (atual == '*') { // se outro * entao pode ser fim de comentario
                            textoTokenAtual = "";
                            estado = 20;
                        } else { //diferente de / e * entao volta para o estado anterior para continuar concatenando comentario
                            textoTokenAtual += atual;
                            estado = 19;
                        }     
                        break;
                }
            }
        }
        
        // Metodo que checa se token ID ja existe na tabela se simbolo
        // Insere caso nao exista
        private Token definirTokenSeTokenIdEhPalavraReservada(String lexema) {
            Token token = tabelaSimbolo.tabela.get(lexema);
            if (token != null) return token;
            else {
                return tabelaSimbolo.inserirIdentificador(new Token(Token.CODIGO_ID, lexema));
            }
        }
        // Metodo que checa se token CONSTANTE ja existe na tabela se simbolo
        // Insere caso nao exista   
        private Token retornar(Token token) {
            Token tokenObtido = tabelaSimbolo.tabela.get(token.texto);
            if (tokenObtido != null) {
                return tokenObtido;
            } else {
                return tabelaSimbolo.inserirConstante(token);
            }
        }

        /*Metodos privados para facilitar na identificação dos tokens , ve quem é token reservado*/
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
    
        /*Metodos privados para facilitar na identificação dos tokens */
        private boolean ehCharHexa(char c) {
            return ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) || ehNumero(c);
        }
    
        /*Metodos privados para facilitar na identificação dos tokens */
        private boolean ehValidoString (char c) {
            return (ehChar(c) || ehNumero(c) ||  c == ' ' || c == '_' || c == '.' || c == ',' || c == ';' || 
                    c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
                    c == '+' || c == '-' || c == '\'' || c == '|' || c == '\\' || c == '/' ||
                    c == '&' || c == '?' || c == '%' || c == '?'|| c == '!' || c == '>' || c == '<' || c == '=' );
        }

        /*Metodos privados para facilitar na identificação dos tokens */
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

         /*Metodos privados para facilitar na identificação dos tokens */    
        private boolean ehNumero (char c) {
            return c >= '0' && c <= '9';
        } 
    
        /*Metodos privados para facilitar na identificação dos tokens */
        private boolean ehChar (char c) {
            return ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' );
        }
    
        /*Metodos privados para facilitar na identificação dos tokens */
        public boolean ehOperador (char c) {
            return c == '<' || c == '>' || c == '=' || c == '!' ||  c == '&' ||  c == '|' ;
        }
    
        /*Metodos privados para facilitar na identificação dos tokens */
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
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
                //  int caractere = nextCharFile(); 
                //  System.out.println("caractere = [" +caractere+"]");
                //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
                contaLinhas(caractere);
                if (caractere == -1 || caractere == 65535 ) {
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
    
        //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
        // private char nextCharFile ()  {
        //     return conteudoNeto[posicao++];
        // }
        // private boolean  ehFinalDoArquivo  () {
        //     return posicao == conteudoNeto.length ;
        // }
        //  CODIGO  UTILIZADO SOMENTE PARA DEBUGAR LOCALMENTE ^^
    }

    /**Classe da tabela de simbolo Hashmap de num token e lexema */
    public static class TabelaSimbolo {
        int endereco = 0;
        public HashMap<String, Token> tabela = new HashMap<>();
        public TabelaSimbolo() {
            super();
            tabela.put("const", new Token(Token.CODIGO_CONST, "const", endereco++));
            tabela.put("int", new Token(Token.CODIGO_INT, "int", endereco++));
            tabela.put("char", new Token(Token.CODIGO_CHAR, "char", endereco++));
            tabela.put("string", new Token(Token.CODIGO_STRING, "string", endereco++));
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
            token.endereco = endereco++; 
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


    public static void main (String[] args) throws Exception {
        try {
            /**Inicializa Sintatico, chamando primeiro token */
            AnalisadorSintatico sintatico = new AnalisadorSintatico(); 
            /**Chama o inicio da gramatica para validação da sintaxe*/
            sintatico.S();
           
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}

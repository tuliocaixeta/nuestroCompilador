public class Token {
    
    public static final int CODIGO_ID = 1;
    public static final int CODIGO_INT = 2;
    public static final int CODIGO_DOUBLE = 3;
    public static final int CODIGO_AND = 4;
    public static final int CODIGO_MENORIGUAL = 5;
    public static final int CODIGO_ATRIBUICAO = 6;
    public static final int CODIGO_MENOR = 7;
    public static final int CODIGO_MAIORIGUAL = 8;
    public static final int CODIGO_MAIOR = 9;
    public static final int CODIGO_EXCLAMACAOIGUAL = 10;
    public static final int CODIGO_EXCLAMACAO = 11;
    public static final int CODIGO_HEXA = 12;
    public static final int CODIGO_MENOS = 13;
    public static final int CODIGO_STRING = 14;
    public static final int CODIGO_CHAR = 15;
    public static final int CODIGO_COMENTARIO = 16;
    public static final int CODIGO_BARRA = 17;
    public static final int CODIGO_PIPE = 18;
    public static final int CODIGO_IGUAL = 19;
    public static final int CODIGO_ABREPARENTESES = 20;
    public static final int CODIGO_FECHAPARENTESES = 21;
    public static final int CODIGO_VIRGULA = 22;
    public static final int CODIGO_MAIS = 23;
    public static final int CODIGO_ASTERISCO = 24;
    public static final int CODIGO_PONTOVIRGULA = 25;
    public static final int CODIGO_ABRECHAVES = 26;
    public static final int CODIGO_FECHACHAVES = 27;
    public static final int CODIGO_ABRECOCHETES = 28;
    public static final int CODIGO_FECHACOCHETES = 29;

    public int tipo;
    public String texto;

    public Token  (int tipo , String texto) {
        super();
        this.tipo = tipo;
        this.texto = texto;
    }

    public Token() {
        super();
    }

    public void setTipo (int tipo) {
        this.tipo = tipo;
    }

    public void setTexto (String texto) {
        this.texto = texto;
    }

    public int getTipo () {
        return tipo;
    }

    public String getTexto () {
        return texto;
    }
}

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
    public static final int CODIGO_MENOS = 12;
    public static final int CODIGO_STRING = 13;
    public static final int CODIGO_CHAR = 14;
    public static final int CODIGO_COMENTARIO = 15;

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

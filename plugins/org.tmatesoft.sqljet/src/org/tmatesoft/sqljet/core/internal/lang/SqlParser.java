// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 sqljet/src/Sql.g 2010-05-05 12:28:03

  package org.tmatesoft.sqljet.core.internal.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings({"unused", "unchecked"})
public class SqlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALIAS", "BIND", "BIND_NAME", "BLOB_LITERAL", "COLUMN_CONSTRAINT", "COLUMN_EXPRESSION", "COLUMNS", "CONSTRAINTS", "CREATE_INDEX", "CREATE_TABLE", "DROP_INDEX", "DROP_TABLE", "FLOAT_LITERAL", "FUNCTION_LITERAL", "FUNCTION_EXPRESSION", "ID_LITERAL", "IN_VALUES", "IN_TABLE", "INTEGER_LITERAL", "IS_NULL", "NOT_NULL", "OPTIONS", "ORDERING", "SELECT_CORE", "STRING_LITERAL", "TABLE_CONSTRAINT", "TYPE", "TYPE_PARAMS", "SEMI", "EXPLAIN", "QUERY", "PLAN", "DOT", "INDEXED", "BY", "NOT", "OR", "AND", "ESCAPE", "IN", "LPAREN", "COMMA", "RPAREN", "ISNULL", "NOTNULL", "IS", "NULL", "BETWEEN", "EQUALS", "EQUALS2", "NOT_EQUALS", "NOT_EQUALS2", "LIKE", "GLOB", "REGEXP", "MATCH", "LESS", "LESS_OR_EQ", "GREATER", "GREATER_OR_EQ", "SHIFT_LEFT", "SHIFT_RIGHT", "AMPERSAND", "PIPE", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "DOUBLE_PIPE", "TILDA", "COLLATE", "ID", "DISTINCT", "CAST", "AS", "CASE", "ELSE", "END", "WHEN", "THEN", "INTEGER", "FLOAT", "STRING", "BLOB", "CURRENT_TIME", "CURRENT_DATE", "CURRENT_TIMESTAMP", "QUESTION", "COLON", "AT", "RAISE", "IGNORE", "ROLLBACK", "ABORT", "FAIL", "PRAGMA", "ATTACH", "DATABASE", "DETACH", "ANALYZE", "REINDEX", "VACUUM", "REPLACE", "ASC", "DESC", "ORDER", "LIMIT", "OFFSET", "UNION", "ALL", "INTERSECT", "EXCEPT", "SELECT", "FROM", "WHERE", "GROUP", "HAVING", "NATURAL", "LEFT", "OUTER", "INNER", "CROSS", "JOIN", "ON", "USING", "INSERT", "INTO", "VALUES", "DEFAULT", "UPDATE", "SET", "DELETE", "BEGIN", "DEFERRED", "IMMEDIATE", "EXCLUSIVE", "TRANSACTION", "COMMIT", "TO", "SAVEPOINT", "RELEASE", "CONFLICT", "CREATE", "VIRTUAL", "TABLE", "TEMPORARY", "IF", "EXISTS", "CONSTRAINT", "PRIMARY", "KEY", "AUTOINCREMENT", "UNIQUE", "CHECK", "FOREIGN", "REFERENCES", "CASCADE", "RESTRICT", "DEFERRABLE", "INITIALLY", "DROP", "ALTER", "RENAME", "ADD", "COLUMN", "VIEW", "INDEX", "TRIGGER", "BEFORE", "AFTER", "INSTEAD", "OF", "FOR", "EACH", "ROW", "BACKSLASH", "DOLLAR", "QUOTE_DOUBLE", "QUOTE_SINGLE", "APOSTROPHE", "LPAREN_SQUARE", "RPAREN_SQUARE", "UNDERSCORE", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "STRING_ESCAPE_SINGLE", "STRING_ESCAPE_DOUBLE", "STRING_CORE", "STRING_CORE_SINGLE", "STRING_CORE_DOUBLE", "STRING_SINGLE", "STRING_DOUBLE", "ID_START", "ID_CORE", "ID_PLAIN", "ID_QUOTED_CORE", "ID_QUOTED_CORE_SQUARE", "ID_QUOTED_CORE_APOSTROPHE", "ID_QUOTED_SQUARE", "ID_QUOTED_APOSTROPHE", "ID_QUOTED", "FLOAT_EXP", "COMMENT", "LINE_COMMENT", "WS"
    };
    public static final int EXISTS=152;
    public static final int INDEX=171;
    public static final int CURRENT_TIMESTAMP=91;
    public static final int MINUS=69;
    public static final int END=82;
    public static final int ATTACH=101;
    public static final int INTO=131;
    public static final int ABORT=98;
    public static final int V=209;
    public static final int SAVEPOINT=144;
    public static final int NATURAL=122;
    public static final int RENAME=167;
    public static final int REGEXP=58;
    public static final int VIEW=170;
    public static final int ALIAS=4;
    public static final int U=208;
    public static final int ON=128;
    public static final int DOT=36;
    public static final int CONSTRAINT=153;
    public static final int NOT_EQUALS=54;
    public static final int ORDER=110;
    public static final int COLLATE=75;
    public static final int R=205;
    public static final int Q=204;
    public static final int STRING_LITERAL=28;
    public static final int SELECT=117;
    public static final int RPAREN=46;
    public static final int DESC=109;
    public static final int ID_START=221;
    public static final int STRING_CORE_DOUBLE=218;
    public static final int CONFLICT=146;
    public static final int UNION=113;
    public static final int PRIMARY=154;
    public static final int W=210;
    public static final int WHEN=83;
    public static final int FLOAT_LITERAL=16;
    public static final int WS=233;
    public static final int STRING=87;
    public static final int NOTNULL=48;
    public static final int EXCLUSIVE=140;
    public static final int UPDATE=134;
    public static final int FUNCTION_EXPRESSION=18;
    public static final int X=211;
    public static final int SEMI=32;
    public static final int EQUALS=52;
    public static final int PLAN=35;
    public static final int ALTER=166;
    public static final int ELSE=81;
    public static final int COLUMN_EXPRESSION=9;
    public static final int FLOAT_EXP=230;
    public static final int NULL=50;
    public static final int ASTERISK=70;
    public static final int COLON=93;
    public static final int ID_PLAIN=223;
    public static final int HAVING=121;
    public static final int SET=135;
    public static final int J=197;
    public static final int ADD=168;
    public static final int TILDA=74;
    public static final int UNIQUE=157;
    public static final int UNDERSCORE=187;
    public static final int SHIFT_LEFT=64;
    public static final int TYPE=30;
    public static final int INDEXED=37;
    public static final int O=202;
    public static final int PERCENT=72;
    public static final int DATABASE=102;
    public static final int EXPLAIN=33;
    public static final int P=203;
    public static final int FLOAT=86;
    public static final int VALUES=132;
    public static final int RESTRICT=162;
    public static final int CAST=78;
    public static final int EXCEPT=116;
    public static final int ID_QUOTED_CORE_APOSTROPHE=226;
    public static final int QUESTION=92;
    public static final int OR=40;
    public static final int AFTER=174;
    public static final int S=206;
    public static final int DOUBLE_PIPE=73;
    public static final int INTEGER=85;
    public static final int LESS=60;
    public static final int BY=38;
    public static final int RELEASE=145;
    public static final int IS_NULL=23;
    public static final int IGNORE=96;
    public static final int ESCAPE=42;
    public static final int M=200;
    public static final int LPAREN=44;
    public static final int T=207;
    public static final int JOIN=127;
    public static final int CURRENT_DATE=90;
    public static final int GREATER_OR_EQ=63;
    public static final int ID=76;
    public static final int FROM=118;
    public static final int DELETE=136;
    public static final int FAIL=99;
    public static final int DEFERRABLE=163;
    public static final int ID_CORE=222;
    public static final int CURRENT_TIME=89;
    public static final int COMMENT=231;
    public static final int MATCH=59;
    public static final int LIKE=56;
    public static final int COMMIT=142;
    public static final int ID_QUOTED=229;
    public static final int N=201;
    public static final int BACKSLASH=180;
    public static final int IN=43;
    public static final int REINDEX=105;
    public static final int DROP=165;
    public static final int DETACH=103;
    public static final int DROP_INDEX=14;
    public static final int IF=151;
    public static final int FOR=177;
    public static final int DEFAULT=133;
    public static final int VIRTUAL=148;
    public static final int BEFORE=173;
    public static final int BLOB_LITERAL=7;
    public static final int RPAREN_SQUARE=186;
    public static final int STRING_SINGLE=219;
    public static final int IN_VALUES=20;
    public static final int NOT=39;
    public static final int LIMIT=111;
    public static final int LPAREN_SQUARE=185;
    public static final int DROP_TABLE=15;
    public static final int COMMA=45;
    public static final int AS=79;
    public static final int THEN=84;
    public static final int ID_QUOTED_APOSTROPHE=228;
    public static final int FOREIGN=159;
    public static final int STRING_ESCAPE_SINGLE=214;
    public static final int PIPE=67;
    public static final int STRING_DOUBLE=220;
    public static final int D=191;
    public static final int AND=41;
    public static final int TO=143;
    public static final int ROLLBACK=97;
    public static final int QUOTE_DOUBLE=182;
    public static final int TRIGGER=172;
    public static final int CONSTRAINTS=11;
    public static final int BETWEEN=51;
    public static final int STRING_CORE=216;
    public static final int APOSTROPHE=184;
    public static final int PLUS=68;
    public static final int AMPERSAND=66;
    public static final int CREATE_TABLE=13;
    public static final int INTEGER_LITERAL=22;
    public static final int AT=94;
    public static final int INTERSECT=115;
    public static final int DISTINCT=77;
    public static final int CASCADE=161;
    public static final int ID_QUOTED_SQUARE=227;
    public static final int LESS_OR_EQ=61;
    public static final int QUOTE_SINGLE=183;
    public static final int OF=176;
    public static final int DOLLAR=181;
    public static final int A=188;
    public static final int ANALYZE=104;
    public static final int LINE_COMMENT=232;
    public static final int NOT_NULL=24;
    public static final int CASE=80;
    public static final int DEFERRED=138;
    public static final int TABLE=149;
    public static final int C=190;
    public static final int COLUMNS=10;
    public static final int KEY=155;
    public static final int CHECK=158;
    public static final int REFERENCES=160;
    public static final int L=199;
    public static final int AUTOINCREMENT=156;
    public static final int ALL=114;
    public static final int COLUMN=169;
    public static final int INSERT=130;
    public static final int EACH=178;
    public static final int WHERE=119;
    public static final int CREATE=147;
    public static final int PRAGMA=100;
    public static final int USING=129;
    public static final int INITIALLY=164;
    public static final int I=196;
    public static final int QUERY=34;
    public static final int INNER=125;
    public static final int F=193;
    public static final int CREATE_INDEX=12;
    public static final int STRING_ESCAPE_DOUBLE=215;
    public static final int ID_QUOTED_CORE_SQUARE=225;
    public static final int K=198;
    public static final int FUNCTION_LITERAL=17;
    public static final int B=189;
    public static final int GROUP=120;
    public static final int STRING_CORE_SINGLE=217;
    public static final int OPTIONS=25;
    public static final int GREATER=62;
    public static final int TYPE_PARAMS=31;
    public static final int NOT_EQUALS2=55;
    public static final int ORDERING=26;
    public static final int SELECT_CORE=27;
    public static final int LEFT=123;
    public static final int TEMPORARY=150;
    public static final int INSTEAD=175;
    public static final int OUTER=124;
    public static final int ID_LITERAL=19;
    public static final int BIND_NAME=6;
    public static final int VACUUM=106;
    public static final int H=195;
    public static final int SLASH=71;
    public static final int BLOB=88;
    public static final int TABLE_CONSTRAINT=29;
    public static final int IMMEDIATE=139;
    public static final int IS=49;
    public static final int G=194;
    public static final int OFFSET=112;
    public static final int REPLACE=107;
    public static final int EQUALS2=53;
    public static final int ASC=108;
    public static final int ID_QUOTED_CORE=224;
    public static final int BEGIN=137;
    public static final int COLUMN_CONSTRAINT=8;
    public static final int Z=213;
    public static final int IN_TABLE=21;
    public static final int SHIFT_RIGHT=65;
    public static final int EOF=-1;
    public static final int RAISE=95;
    public static final int CROSS=126;
    public static final int ISNULL=47;
    public static final int GLOB=57;
    public static final int Y=212;
    public static final int BIND=5;
    public static final int E=192;
    public static final int TRANSACTION=141;
    public static final int ROW=179;

    // delegates
    // delegators


        public SqlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public SqlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return SqlParser.tokenNames; }
    public String getGrammarFileName() { return "sqljet/src/Sql.g"; }



    // Disable error recovery.
    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);
    }

    // Delegate error reporting to caller.
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[").append(getErrorHeader(e)).append("] ");
        buffer.append(getErrorMessage(e, tokenNames));
        if(e.token!=null) {
          final CharStream stream = e.token.getInputStream();
          if(stream!=null) {
            int size = stream.size();
            if(size>0) {
              buffer.append("\n").append(stream.substring(0, size-1));
            }
          }
        }
        throw new SqlJetParserException(buffer.toString(), e);
    }

    // unquotes identifier
    private String unquoteId(String id) {
      if(id==null) {
        return null;
      }
      int len = id.length();
      if(len==0) {
        return "";
      }
      char first = id.charAt(0);
      char last = id.charAt(len-1);
      switch(first) {
        case '[' :
          first = ']';
        case '\'' :
        case '"' :
        case '`' :
          if(first==last) {
            return id.substring(1,len-1);
          }
        default:
          return id;
      }
    }



    public static class sql_stmt_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sql_stmt_list"
    // sqljet/src/Sql.g:138:1: sql_stmt_list : sql_stmt ( SEMI ( sql_stmt SEMI )* )? EOF ;
    public final SqlParser.sql_stmt_list_return sql_stmt_list() throws RecognitionException {
        SqlParser.sql_stmt_list_return retval = new SqlParser.sql_stmt_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMI2=null;
        Token SEMI4=null;
        Token EOF5=null;
        SqlParser.sql_stmt_return sql_stmt1 = null;

        SqlParser.sql_stmt_return sql_stmt3 = null;


        Object SEMI2_tree=null;
        Object SEMI4_tree=null;
        Object EOF5_tree=null;

        try {
            // sqljet/src/Sql.g:138:14: ( sql_stmt ( SEMI ( sql_stmt SEMI )* )? EOF )
            // sqljet/src/Sql.g:138:16: sql_stmt ( SEMI ( sql_stmt SEMI )* )? EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sql_stmt_in_sql_stmt_list198);
            sql_stmt1=sql_stmt();

            state._fsp--;

            adaptor.addChild(root_0, sql_stmt1.getTree());
            // sqljet/src/Sql.g:138:25: ( SEMI ( sql_stmt SEMI )* )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==SEMI) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // sqljet/src/Sql.g:138:26: SEMI ( sql_stmt SEMI )*
                    {
                    SEMI2=(Token)match(input,SEMI,FOLLOW_SEMI_in_sql_stmt_list201); 
                    // sqljet/src/Sql.g:138:32: ( sql_stmt SEMI )*
                    loop1:
                    do {
                        int alt1=2;
                        alt1 = dfa1.predict(input);
                        switch (alt1) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:138:33: sql_stmt SEMI
                    	    {
                    	    pushFollow(FOLLOW_sql_stmt_in_sql_stmt_list205);
                    	    sql_stmt3=sql_stmt();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, sql_stmt3.getTree());
                    	    SEMI4=(Token)match(input,SEMI,FOLLOW_SEMI_in_sql_stmt_list207); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }
                    break;

            }

            EOF5=(Token)match(input,EOF,FOLLOW_EOF_in_sql_stmt_list215); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sql_stmt_list"

    public static class sql_stmt_itself_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sql_stmt_itself"
    // sqljet/src/Sql.g:140:1: sql_stmt_itself : sql_stmt ( SEMI )? EOF ;
    public final SqlParser.sql_stmt_itself_return sql_stmt_itself() throws RecognitionException {
        SqlParser.sql_stmt_itself_return retval = new SqlParser.sql_stmt_itself_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEMI7=null;
        Token EOF8=null;
        SqlParser.sql_stmt_return sql_stmt6 = null;


        Object SEMI7_tree=null;
        Object EOF8_tree=null;

        try {
            // sqljet/src/Sql.g:140:16: ( sql_stmt ( SEMI )? EOF )
            // sqljet/src/Sql.g:140:18: sql_stmt ( SEMI )? EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sql_stmt_in_sql_stmt_itself223);
            sql_stmt6=sql_stmt();

            state._fsp--;

            adaptor.addChild(root_0, sql_stmt6.getTree());
            // sqljet/src/Sql.g:140:27: ( SEMI )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SEMI) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // sqljet/src/Sql.g:140:28: SEMI
                    {
                    SEMI7=(Token)match(input,SEMI,FOLLOW_SEMI_in_sql_stmt_itself226); 

                    }
                    break;

            }

            EOF8=(Token)match(input,EOF,FOLLOW_EOF_in_sql_stmt_itself231); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sql_stmt_itself"

    public static class sql_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sql_stmt"
    // sqljet/src/Sql.g:142:1: sql_stmt : ( EXPLAIN ( QUERY PLAN )? )? sql_stmt_core ;
    public final SqlParser.sql_stmt_return sql_stmt() throws RecognitionException {
        SqlParser.sql_stmt_return retval = new SqlParser.sql_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EXPLAIN9=null;
        Token QUERY10=null;
        Token PLAN11=null;
        SqlParser.sql_stmt_core_return sql_stmt_core12 = null;


        Object EXPLAIN9_tree=null;
        Object QUERY10_tree=null;
        Object PLAN11_tree=null;

        try {
            // sqljet/src/Sql.g:142:9: ( ( EXPLAIN ( QUERY PLAN )? )? sql_stmt_core )
            // sqljet/src/Sql.g:142:11: ( EXPLAIN ( QUERY PLAN )? )? sql_stmt_core
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:142:11: ( EXPLAIN ( QUERY PLAN )? )?
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // sqljet/src/Sql.g:142:12: EXPLAIN ( QUERY PLAN )?
                    {
                    EXPLAIN9=(Token)match(input,EXPLAIN,FOLLOW_EXPLAIN_in_sql_stmt240); 
                    EXPLAIN9_tree = (Object)adaptor.create(EXPLAIN9);
                    adaptor.addChild(root_0, EXPLAIN9_tree);

                    // sqljet/src/Sql.g:142:20: ( QUERY PLAN )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // sqljet/src/Sql.g:142:21: QUERY PLAN
                            {
                            QUERY10=(Token)match(input,QUERY,FOLLOW_QUERY_in_sql_stmt243); 
                            QUERY10_tree = (Object)adaptor.create(QUERY10);
                            adaptor.addChild(root_0, QUERY10_tree);

                            PLAN11=(Token)match(input,PLAN,FOLLOW_PLAN_in_sql_stmt245); 
                            PLAN11_tree = (Object)adaptor.create(PLAN11);
                            adaptor.addChild(root_0, PLAN11_tree);


                            }
                            break;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_sql_stmt_core_in_sql_stmt251);
            sql_stmt_core12=sql_stmt_core();

            state._fsp--;

            adaptor.addChild(root_0, sql_stmt_core12.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sql_stmt"

    public static class sql_stmt_core_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sql_stmt_core"
    // sqljet/src/Sql.g:144:1: sql_stmt_core : ( pragma_stmt | attach_stmt | detach_stmt | analyze_stmt | reindex_stmt | vacuum_stmt | select_stmt | insert_stmt | update_stmt | delete_stmt | begin_stmt | commit_stmt | rollback_stmt | savepoint_stmt | release_stmt | create_virtual_table_stmt | create_table_stmt | drop_table_stmt | alter_table_stmt | create_view_stmt | drop_view_stmt | create_index_stmt | drop_index_stmt | create_trigger_stmt | drop_trigger_stmt );
    public final SqlParser.sql_stmt_core_return sql_stmt_core() throws RecognitionException {
        SqlParser.sql_stmt_core_return retval = new SqlParser.sql_stmt_core_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.pragma_stmt_return pragma_stmt13 = null;

        SqlParser.attach_stmt_return attach_stmt14 = null;

        SqlParser.detach_stmt_return detach_stmt15 = null;

        SqlParser.analyze_stmt_return analyze_stmt16 = null;

        SqlParser.reindex_stmt_return reindex_stmt17 = null;

        SqlParser.vacuum_stmt_return vacuum_stmt18 = null;

        SqlParser.select_stmt_return select_stmt19 = null;

        SqlParser.insert_stmt_return insert_stmt20 = null;

        SqlParser.update_stmt_return update_stmt21 = null;

        SqlParser.delete_stmt_return delete_stmt22 = null;

        SqlParser.begin_stmt_return begin_stmt23 = null;

        SqlParser.commit_stmt_return commit_stmt24 = null;

        SqlParser.rollback_stmt_return rollback_stmt25 = null;

        SqlParser.savepoint_stmt_return savepoint_stmt26 = null;

        SqlParser.release_stmt_return release_stmt27 = null;

        SqlParser.create_virtual_table_stmt_return create_virtual_table_stmt28 = null;

        SqlParser.create_table_stmt_return create_table_stmt29 = null;

        SqlParser.drop_table_stmt_return drop_table_stmt30 = null;

        SqlParser.alter_table_stmt_return alter_table_stmt31 = null;

        SqlParser.create_view_stmt_return create_view_stmt32 = null;

        SqlParser.drop_view_stmt_return drop_view_stmt33 = null;

        SqlParser.create_index_stmt_return create_index_stmt34 = null;

        SqlParser.drop_index_stmt_return drop_index_stmt35 = null;

        SqlParser.create_trigger_stmt_return create_trigger_stmt36 = null;

        SqlParser.drop_trigger_stmt_return drop_trigger_stmt37 = null;



        try {
            // sqljet/src/Sql.g:145:3: ( pragma_stmt | attach_stmt | detach_stmt | analyze_stmt | reindex_stmt | vacuum_stmt | select_stmt | insert_stmt | update_stmt | delete_stmt | begin_stmt | commit_stmt | rollback_stmt | savepoint_stmt | release_stmt | create_virtual_table_stmt | create_table_stmt | drop_table_stmt | alter_table_stmt | create_view_stmt | drop_view_stmt | create_index_stmt | drop_index_stmt | create_trigger_stmt | drop_trigger_stmt )
            int alt6=25;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // sqljet/src/Sql.g:145:5: pragma_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_pragma_stmt_in_sql_stmt_core261);
                    pragma_stmt13=pragma_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, pragma_stmt13.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:146:5: attach_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attach_stmt_in_sql_stmt_core267);
                    attach_stmt14=attach_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, attach_stmt14.getTree());

                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:147:5: detach_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_detach_stmt_in_sql_stmt_core273);
                    detach_stmt15=detach_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, detach_stmt15.getTree());

                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:148:5: analyze_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_analyze_stmt_in_sql_stmt_core279);
                    analyze_stmt16=analyze_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, analyze_stmt16.getTree());

                    }
                    break;
                case 5 :
                    // sqljet/src/Sql.g:149:5: reindex_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_reindex_stmt_in_sql_stmt_core285);
                    reindex_stmt17=reindex_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, reindex_stmt17.getTree());

                    }
                    break;
                case 6 :
                    // sqljet/src/Sql.g:150:5: vacuum_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_vacuum_stmt_in_sql_stmt_core291);
                    vacuum_stmt18=vacuum_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, vacuum_stmt18.getTree());

                    }
                    break;
                case 7 :
                    // sqljet/src/Sql.g:152:5: select_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_select_stmt_in_sql_stmt_core300);
                    select_stmt19=select_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, select_stmt19.getTree());

                    }
                    break;
                case 8 :
                    // sqljet/src/Sql.g:153:5: insert_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_insert_stmt_in_sql_stmt_core306);
                    insert_stmt20=insert_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, insert_stmt20.getTree());

                    }
                    break;
                case 9 :
                    // sqljet/src/Sql.g:154:5: update_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_update_stmt_in_sql_stmt_core312);
                    update_stmt21=update_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, update_stmt21.getTree());

                    }
                    break;
                case 10 :
                    // sqljet/src/Sql.g:155:5: delete_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_delete_stmt_in_sql_stmt_core318);
                    delete_stmt22=delete_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, delete_stmt22.getTree());

                    }
                    break;
                case 11 :
                    // sqljet/src/Sql.g:156:5: begin_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_begin_stmt_in_sql_stmt_core324);
                    begin_stmt23=begin_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, begin_stmt23.getTree());

                    }
                    break;
                case 12 :
                    // sqljet/src/Sql.g:157:5: commit_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_commit_stmt_in_sql_stmt_core330);
                    commit_stmt24=commit_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, commit_stmt24.getTree());

                    }
                    break;
                case 13 :
                    // sqljet/src/Sql.g:158:5: rollback_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rollback_stmt_in_sql_stmt_core336);
                    rollback_stmt25=rollback_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, rollback_stmt25.getTree());

                    }
                    break;
                case 14 :
                    // sqljet/src/Sql.g:159:5: savepoint_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_savepoint_stmt_in_sql_stmt_core342);
                    savepoint_stmt26=savepoint_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, savepoint_stmt26.getTree());

                    }
                    break;
                case 15 :
                    // sqljet/src/Sql.g:160:5: release_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_release_stmt_in_sql_stmt_core348);
                    release_stmt27=release_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, release_stmt27.getTree());

                    }
                    break;
                case 16 :
                    // sqljet/src/Sql.g:162:5: create_virtual_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_virtual_table_stmt_in_sql_stmt_core357);
                    create_virtual_table_stmt28=create_virtual_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_virtual_table_stmt28.getTree());

                    }
                    break;
                case 17 :
                    // sqljet/src/Sql.g:163:5: create_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_table_stmt_in_sql_stmt_core363);
                    create_table_stmt29=create_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_table_stmt29.getTree());

                    }
                    break;
                case 18 :
                    // sqljet/src/Sql.g:164:5: drop_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_drop_table_stmt_in_sql_stmt_core369);
                    drop_table_stmt30=drop_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, drop_table_stmt30.getTree());

                    }
                    break;
                case 19 :
                    // sqljet/src/Sql.g:165:5: alter_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_alter_table_stmt_in_sql_stmt_core375);
                    alter_table_stmt31=alter_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, alter_table_stmt31.getTree());

                    }
                    break;
                case 20 :
                    // sqljet/src/Sql.g:166:5: create_view_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_view_stmt_in_sql_stmt_core381);
                    create_view_stmt32=create_view_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_view_stmt32.getTree());

                    }
                    break;
                case 21 :
                    // sqljet/src/Sql.g:167:5: drop_view_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_drop_view_stmt_in_sql_stmt_core387);
                    drop_view_stmt33=drop_view_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, drop_view_stmt33.getTree());

                    }
                    break;
                case 22 :
                    // sqljet/src/Sql.g:168:5: create_index_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_index_stmt_in_sql_stmt_core393);
                    create_index_stmt34=create_index_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_index_stmt34.getTree());

                    }
                    break;
                case 23 :
                    // sqljet/src/Sql.g:169:5: drop_index_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_drop_index_stmt_in_sql_stmt_core399);
                    drop_index_stmt35=drop_index_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, drop_index_stmt35.getTree());

                    }
                    break;
                case 24 :
                    // sqljet/src/Sql.g:170:5: create_trigger_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_trigger_stmt_in_sql_stmt_core405);
                    create_trigger_stmt36=create_trigger_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_trigger_stmt36.getTree());

                    }
                    break;
                case 25 :
                    // sqljet/src/Sql.g:171:5: drop_trigger_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_drop_trigger_stmt_in_sql_stmt_core411);
                    drop_trigger_stmt37=drop_trigger_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, drop_trigger_stmt37.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sql_stmt_core"

    public static class schema_create_table_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "schema_create_table_stmt"
    // sqljet/src/Sql.g:174:1: schema_create_table_stmt : ( create_virtual_table_stmt | create_table_stmt );
    public final SqlParser.schema_create_table_stmt_return schema_create_table_stmt() throws RecognitionException {
        SqlParser.schema_create_table_stmt_return retval = new SqlParser.schema_create_table_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.create_virtual_table_stmt_return create_virtual_table_stmt38 = null;

        SqlParser.create_table_stmt_return create_table_stmt39 = null;



        try {
            // sqljet/src/Sql.g:174:25: ( create_virtual_table_stmt | create_table_stmt )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==CREATE) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==VIRTUAL) ) {
                    alt7=1;
                }
                else if ( ((LA7_1>=TABLE && LA7_1<=TEMPORARY)) ) {
                    alt7=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // sqljet/src/Sql.g:174:27: create_virtual_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_virtual_table_stmt_in_schema_create_table_stmt421);
                    create_virtual_table_stmt38=create_virtual_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_virtual_table_stmt38.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:174:55: create_table_stmt
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_create_table_stmt_in_schema_create_table_stmt425);
                    create_table_stmt39=create_table_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, create_table_stmt39.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "schema_create_table_stmt"

    public static class qualified_table_name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualified_table_name"
    // sqljet/src/Sql.g:176:1: qualified_table_name : (database_name= id DOT )? table_name= id ( INDEXED BY index_name= id | NOT INDEXED )? ;
    public final SqlParser.qualified_table_name_return qualified_table_name() throws RecognitionException {
        SqlParser.qualified_table_name_return retval = new SqlParser.qualified_table_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT40=null;
        Token INDEXED41=null;
        Token BY42=null;
        Token NOT43=null;
        Token INDEXED44=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.id_return index_name = null;


        Object DOT40_tree=null;
        Object INDEXED41_tree=null;
        Object BY42_tree=null;
        Object NOT43_tree=null;
        Object INDEXED44_tree=null;

        try {
            // sqljet/src/Sql.g:176:21: ( (database_name= id DOT )? table_name= id ( INDEXED BY index_name= id | NOT INDEXED )? )
            // sqljet/src/Sql.g:176:23: (database_name= id DOT )? table_name= id ( INDEXED BY index_name= id | NOT INDEXED )?
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:176:23: (database_name= id DOT )?
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // sqljet/src/Sql.g:176:24: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_qualified_table_name435);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT40=(Token)match(input,DOT,FOLLOW_DOT_in_qualified_table_name437); 
                    DOT40_tree = (Object)adaptor.create(DOT40);
                    adaptor.addChild(root_0, DOT40_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_qualified_table_name443);
            table_name=id();

            state._fsp--;

            adaptor.addChild(root_0, table_name.getTree());
            // sqljet/src/Sql.g:176:61: ( INDEXED BY index_name= id | NOT INDEXED )?
            int alt9=3;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==INDEXED) ) {
                alt9=1;
            }
            else if ( (LA9_0==NOT) ) {
                alt9=2;
            }
            switch (alt9) {
                case 1 :
                    // sqljet/src/Sql.g:176:62: INDEXED BY index_name= id
                    {
                    INDEXED41=(Token)match(input,INDEXED,FOLLOW_INDEXED_in_qualified_table_name446); 
                    INDEXED41_tree = (Object)adaptor.create(INDEXED41);
                    adaptor.addChild(root_0, INDEXED41_tree);

                    BY42=(Token)match(input,BY,FOLLOW_BY_in_qualified_table_name448); 
                    BY42_tree = (Object)adaptor.create(BY42);
                    adaptor.addChild(root_0, BY42_tree);

                    pushFollow(FOLLOW_id_in_qualified_table_name452);
                    index_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, index_name.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:176:89: NOT INDEXED
                    {
                    NOT43=(Token)match(input,NOT,FOLLOW_NOT_in_qualified_table_name456); 
                    NOT43_tree = (Object)adaptor.create(NOT43);
                    adaptor.addChild(root_0, NOT43_tree);

                    INDEXED44=(Token)match(input,INDEXED,FOLLOW_INDEXED_in_qualified_table_name458); 
                    INDEXED44_tree = (Object)adaptor.create(INDEXED44);
                    adaptor.addChild(root_0, INDEXED44_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "qualified_table_name"

    public static class expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expr"
    // sqljet/src/Sql.g:178:1: expr : or_subexpr ( OR or_subexpr )* ;
    public final SqlParser.expr_return expr() throws RecognitionException {
        SqlParser.expr_return retval = new SqlParser.expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR46=null;
        SqlParser.or_subexpr_return or_subexpr45 = null;

        SqlParser.or_subexpr_return or_subexpr47 = null;


        Object OR46_tree=null;

        try {
            // sqljet/src/Sql.g:178:5: ( or_subexpr ( OR or_subexpr )* )
            // sqljet/src/Sql.g:178:7: or_subexpr ( OR or_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_or_subexpr_in_expr467);
            or_subexpr45=or_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, or_subexpr45.getTree());
            // sqljet/src/Sql.g:178:18: ( OR or_subexpr )*
            loop10:
            do {
                int alt10=2;
                alt10 = dfa10.predict(input);
                switch (alt10) {
            	case 1 :
            	    // sqljet/src/Sql.g:178:19: OR or_subexpr
            	    {
            	    OR46=(Token)match(input,OR,FOLLOW_OR_in_expr470); 
            	    OR46_tree = (Object)adaptor.create(OR46);
            	    root_0 = (Object)adaptor.becomeRoot(OR46_tree, root_0);

            	    pushFollow(FOLLOW_or_subexpr_in_expr473);
            	    or_subexpr47=or_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or_subexpr47.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expr"

    public static class or_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or_subexpr"
    // sqljet/src/Sql.g:180:1: or_subexpr : and_subexpr ( AND and_subexpr )* ;
    public final SqlParser.or_subexpr_return or_subexpr() throws RecognitionException {
        SqlParser.or_subexpr_return retval = new SqlParser.or_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AND49=null;
        SqlParser.and_subexpr_return and_subexpr48 = null;

        SqlParser.and_subexpr_return and_subexpr50 = null;


        Object AND49_tree=null;

        try {
            // sqljet/src/Sql.g:180:11: ( and_subexpr ( AND and_subexpr )* )
            // sqljet/src/Sql.g:180:13: and_subexpr ( AND and_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_and_subexpr_in_or_subexpr482);
            and_subexpr48=and_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, and_subexpr48.getTree());
            // sqljet/src/Sql.g:180:25: ( AND and_subexpr )*
            loop11:
            do {
                int alt11=2;
                alt11 = dfa11.predict(input);
                switch (alt11) {
            	case 1 :
            	    // sqljet/src/Sql.g:180:26: AND and_subexpr
            	    {
            	    AND49=(Token)match(input,AND,FOLLOW_AND_in_or_subexpr485); 
            	    AND49_tree = (Object)adaptor.create(AND49);
            	    root_0 = (Object)adaptor.becomeRoot(AND49_tree, root_0);

            	    pushFollow(FOLLOW_and_subexpr_in_or_subexpr488);
            	    and_subexpr50=and_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_subexpr50.getTree());

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or_subexpr"

    public static class and_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and_subexpr"
    // sqljet/src/Sql.g:182:1: and_subexpr : eq_subexpr ( cond_expr )? ;
    public final SqlParser.and_subexpr_return and_subexpr() throws RecognitionException {
        SqlParser.and_subexpr_return retval = new SqlParser.and_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.eq_subexpr_return eq_subexpr51 = null;

        SqlParser.cond_expr_return cond_expr52 = null;



        try {
            // sqljet/src/Sql.g:182:12: ( eq_subexpr ( cond_expr )? )
            // sqljet/src/Sql.g:182:14: eq_subexpr ( cond_expr )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_eq_subexpr_in_and_subexpr497);
            eq_subexpr51=eq_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, eq_subexpr51.getTree());
            // sqljet/src/Sql.g:182:34: ( cond_expr )?
            int alt12=2;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // sqljet/src/Sql.g:182:34: cond_expr
                    {
                    pushFollow(FOLLOW_cond_expr_in_and_subexpr499);
                    cond_expr52=cond_expr();

                    state._fsp--;

                    root_0 = (Object)adaptor.becomeRoot(cond_expr52.getTree(), root_0);

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and_subexpr"

    public static class cond_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cond_expr"
    // sqljet/src/Sql.g:184:1: cond_expr : ( ( NOT )? match_op match_expr= eq_subexpr ( ESCAPE escape_expr= eq_subexpr )? -> ^( match_op $match_expr ( NOT )? ( ^( ESCAPE $escape_expr) )? ) | ( NOT )? IN LPAREN expr ( COMMA expr )* RPAREN -> ^( IN_VALUES ( NOT )? ^( IN ( expr )+ ) ) | ( NOT )? IN (database_name= id DOT )? table_name= id -> ^( IN_TABLE ( NOT )? ^( IN ^( $table_name ( $database_name)? ) ) ) | ( ISNULL -> IS_NULL | NOTNULL -> NOT_NULL | IS NULL -> IS_NULL | NOT NULL -> NOT_NULL | IS NOT NULL -> NOT_NULL ) | ( NOT )? BETWEEN e1= eq_subexpr AND e2= eq_subexpr -> ^( BETWEEN ( NOT )? ^( AND $e1 $e2) ) | ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+ );
    public final SqlParser.cond_expr_return cond_expr() throws RecognitionException {
        SqlParser.cond_expr_return retval = new SqlParser.cond_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NOT53=null;
        Token ESCAPE55=null;
        Token NOT56=null;
        Token IN57=null;
        Token LPAREN58=null;
        Token COMMA60=null;
        Token RPAREN62=null;
        Token NOT63=null;
        Token IN64=null;
        Token DOT65=null;
        Token ISNULL66=null;
        Token NOTNULL67=null;
        Token IS68=null;
        Token NULL69=null;
        Token NOT70=null;
        Token NULL71=null;
        Token IS72=null;
        Token NOT73=null;
        Token NULL74=null;
        Token NOT75=null;
        Token BETWEEN76=null;
        Token AND77=null;
        Token set78=null;
        SqlParser.eq_subexpr_return match_expr = null;

        SqlParser.eq_subexpr_return escape_expr = null;

        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.eq_subexpr_return e1 = null;

        SqlParser.eq_subexpr_return e2 = null;

        SqlParser.match_op_return match_op54 = null;

        SqlParser.expr_return expr59 = null;

        SqlParser.expr_return expr61 = null;

        SqlParser.eq_subexpr_return eq_subexpr79 = null;


        Object NOT53_tree=null;
        Object ESCAPE55_tree=null;
        Object NOT56_tree=null;
        Object IN57_tree=null;
        Object LPAREN58_tree=null;
        Object COMMA60_tree=null;
        Object RPAREN62_tree=null;
        Object NOT63_tree=null;
        Object IN64_tree=null;
        Object DOT65_tree=null;
        Object ISNULL66_tree=null;
        Object NOTNULL67_tree=null;
        Object IS68_tree=null;
        Object NULL69_tree=null;
        Object NOT70_tree=null;
        Object NULL71_tree=null;
        Object IS72_tree=null;
        Object NOT73_tree=null;
        Object NULL74_tree=null;
        Object NOT75_tree=null;
        Object BETWEEN76_tree=null;
        Object AND77_tree=null;
        Object set78_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
        RewriteRuleTokenStream stream_IS=new RewriteRuleTokenStream(adaptor,"token IS");
        RewriteRuleTokenStream stream_NULL=new RewriteRuleTokenStream(adaptor,"token NULL");
        RewriteRuleTokenStream stream_ESCAPE=new RewriteRuleTokenStream(adaptor,"token ESCAPE");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_NOTNULL=new RewriteRuleTokenStream(adaptor,"token NOTNULL");
        RewriteRuleTokenStream stream_ISNULL=new RewriteRuleTokenStream(adaptor,"token ISNULL");
        RewriteRuleTokenStream stream_BETWEEN=new RewriteRuleTokenStream(adaptor,"token BETWEEN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_match_op=new RewriteRuleSubtreeStream(adaptor,"rule match_op");
        RewriteRuleSubtreeStream stream_eq_subexpr=new RewriteRuleSubtreeStream(adaptor,"rule eq_subexpr");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:185:3: ( ( NOT )? match_op match_expr= eq_subexpr ( ESCAPE escape_expr= eq_subexpr )? -> ^( match_op $match_expr ( NOT )? ( ^( ESCAPE $escape_expr) )? ) | ( NOT )? IN LPAREN expr ( COMMA expr )* RPAREN -> ^( IN_VALUES ( NOT )? ^( IN ( expr )+ ) ) | ( NOT )? IN (database_name= id DOT )? table_name= id -> ^( IN_TABLE ( NOT )? ^( IN ^( $table_name ( $database_name)? ) ) ) | ( ISNULL -> IS_NULL | NOTNULL -> NOT_NULL | IS NULL -> IS_NULL | NOT NULL -> NOT_NULL | IS NOT NULL -> NOT_NULL ) | ( NOT )? BETWEEN e1= eq_subexpr AND e2= eq_subexpr -> ^( BETWEEN ( NOT )? ^( AND $e1 $e2) ) | ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+ )
            int alt22=6;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // sqljet/src/Sql.g:185:5: ( NOT )? match_op match_expr= eq_subexpr ( ESCAPE escape_expr= eq_subexpr )?
                    {
                    // sqljet/src/Sql.g:185:5: ( NOT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==NOT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // sqljet/src/Sql.g:185:5: NOT
                            {
                            NOT53=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr511);  
                            stream_NOT.add(NOT53);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_match_op_in_cond_expr514);
                    match_op54=match_op();

                    state._fsp--;

                    stream_match_op.add(match_op54.getTree());
                    pushFollow(FOLLOW_eq_subexpr_in_cond_expr518);
                    match_expr=eq_subexpr();

                    state._fsp--;

                    stream_eq_subexpr.add(match_expr.getTree());
                    // sqljet/src/Sql.g:185:41: ( ESCAPE escape_expr= eq_subexpr )?
                    int alt14=2;
                    alt14 = dfa14.predict(input);
                    switch (alt14) {
                        case 1 :
                            // sqljet/src/Sql.g:185:42: ESCAPE escape_expr= eq_subexpr
                            {
                            ESCAPE55=(Token)match(input,ESCAPE,FOLLOW_ESCAPE_in_cond_expr521);  
                            stream_ESCAPE.add(ESCAPE55);

                            pushFollow(FOLLOW_eq_subexpr_in_cond_expr525);
                            escape_expr=eq_subexpr();

                            state._fsp--;

                            stream_eq_subexpr.add(escape_expr.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ESCAPE, NOT, match_op, match_expr, escape_expr
                    // token labels: 
                    // rule labels: match_expr, retval, escape_expr
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_match_expr=new RewriteRuleSubtreeStream(adaptor,"rule match_expr",match_expr!=null?match_expr.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_escape_expr=new RewriteRuleSubtreeStream(adaptor,"rule escape_expr",escape_expr!=null?escape_expr.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 185:74: -> ^( match_op $match_expr ( NOT )? ( ^( ESCAPE $escape_expr) )? )
                    {
                        // sqljet/src/Sql.g:185:77: ^( match_op $match_expr ( NOT )? ( ^( ESCAPE $escape_expr) )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_match_op.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_match_expr.nextTree());
                        // sqljet/src/Sql.g:185:100: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, stream_NOT.nextNode());

                        }
                        stream_NOT.reset();
                        // sqljet/src/Sql.g:185:105: ( ^( ESCAPE $escape_expr) )?
                        if ( stream_ESCAPE.hasNext()||stream_escape_expr.hasNext() ) {
                            // sqljet/src/Sql.g:185:105: ^( ESCAPE $escape_expr)
                            {
                            Object root_2 = (Object)adaptor.nil();
                            root_2 = (Object)adaptor.becomeRoot(stream_ESCAPE.nextNode(), root_2);

                            adaptor.addChild(root_2, stream_escape_expr.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_ESCAPE.reset();
                        stream_escape_expr.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:186:5: ( NOT )? IN LPAREN expr ( COMMA expr )* RPAREN
                    {
                    // sqljet/src/Sql.g:186:5: ( NOT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==NOT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // sqljet/src/Sql.g:186:5: NOT
                            {
                            NOT56=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr553);  
                            stream_NOT.add(NOT56);


                            }
                            break;

                    }

                    IN57=(Token)match(input,IN,FOLLOW_IN_in_cond_expr556);  
                    stream_IN.add(IN57);

                    LPAREN58=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_cond_expr558);  
                    stream_LPAREN.add(LPAREN58);

                    pushFollow(FOLLOW_expr_in_cond_expr560);
                    expr59=expr();

                    state._fsp--;

                    stream_expr.add(expr59.getTree());
                    // sqljet/src/Sql.g:186:25: ( COMMA expr )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==COMMA) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:186:26: COMMA expr
                    	    {
                    	    COMMA60=(Token)match(input,COMMA,FOLLOW_COMMA_in_cond_expr563);  
                    	    stream_COMMA.add(COMMA60);

                    	    pushFollow(FOLLOW_expr_in_cond_expr565);
                    	    expr61=expr();

                    	    state._fsp--;

                    	    stream_expr.add(expr61.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                    RPAREN62=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_cond_expr569);  
                    stream_RPAREN.add(RPAREN62);



                    // AST REWRITE
                    // elements: expr, NOT, IN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 186:46: -> ^( IN_VALUES ( NOT )? ^( IN ( expr )+ ) )
                    {
                        // sqljet/src/Sql.g:186:49: ^( IN_VALUES ( NOT )? ^( IN ( expr )+ ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IN_VALUES, "IN_VALUES"), root_1);

                        // sqljet/src/Sql.g:186:61: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, stream_NOT.nextNode());

                        }
                        stream_NOT.reset();
                        // sqljet/src/Sql.g:186:66: ^( IN ( expr )+ )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_IN.nextNode(), root_2);

                        if ( !(stream_expr.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expr.hasNext() ) {
                            adaptor.addChild(root_2, stream_expr.nextTree());

                        }
                        stream_expr.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:187:5: ( NOT )? IN (database_name= id DOT )? table_name= id
                    {
                    // sqljet/src/Sql.g:187:5: ( NOT )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==NOT) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // sqljet/src/Sql.g:187:5: NOT
                            {
                            NOT63=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr591);  
                            stream_NOT.add(NOT63);


                            }
                            break;

                    }

                    IN64=(Token)match(input,IN,FOLLOW_IN_in_cond_expr594);  
                    stream_IN.add(IN64);

                    // sqljet/src/Sql.g:187:13: (database_name= id DOT )?
                    int alt18=2;
                    alt18 = dfa18.predict(input);
                    switch (alt18) {
                        case 1 :
                            // sqljet/src/Sql.g:187:14: database_name= id DOT
                            {
                            pushFollow(FOLLOW_id_in_cond_expr599);
                            database_name=id();

                            state._fsp--;

                            stream_id.add(database_name.getTree());
                            DOT65=(Token)match(input,DOT,FOLLOW_DOT_in_cond_expr601);  
                            stream_DOT.add(DOT65);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_id_in_cond_expr607);
                    table_name=id();

                    state._fsp--;

                    stream_id.add(table_name.getTree());


                    // AST REWRITE
                    // elements: NOT, database_name, table_name, IN
                    // token labels: 
                    // rule labels: database_name, retval, table_name
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 187:51: -> ^( IN_TABLE ( NOT )? ^( IN ^( $table_name ( $database_name)? ) ) )
                    {
                        // sqljet/src/Sql.g:187:54: ^( IN_TABLE ( NOT )? ^( IN ^( $table_name ( $database_name)? ) ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IN_TABLE, "IN_TABLE"), root_1);

                        // sqljet/src/Sql.g:187:65: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, stream_NOT.nextNode());

                        }
                        stream_NOT.reset();
                        // sqljet/src/Sql.g:187:70: ^( IN ^( $table_name ( $database_name)? ) )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_IN.nextNode(), root_2);

                        // sqljet/src/Sql.g:187:75: ^( $table_name ( $database_name)? )
                        {
                        Object root_3 = (Object)adaptor.nil();
                        root_3 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_3);

                        // sqljet/src/Sql.g:187:89: ( $database_name)?
                        if ( stream_database_name.hasNext() ) {
                            adaptor.addChild(root_3, stream_database_name.nextTree());

                        }
                        stream_database_name.reset();

                        adaptor.addChild(root_2, root_3);
                        }

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:190:5: ( ISNULL -> IS_NULL | NOTNULL -> NOT_NULL | IS NULL -> IS_NULL | NOT NULL -> NOT_NULL | IS NOT NULL -> NOT_NULL )
                    {
                    // sqljet/src/Sql.g:190:5: ( ISNULL -> IS_NULL | NOTNULL -> NOT_NULL | IS NULL -> IS_NULL | NOT NULL -> NOT_NULL | IS NOT NULL -> NOT_NULL )
                    int alt19=5;
                    switch ( input.LA(1) ) {
                    case ISNULL:
                        {
                        alt19=1;
                        }
                        break;
                    case NOTNULL:
                        {
                        alt19=2;
                        }
                        break;
                    case IS:
                        {
                        int LA19_3 = input.LA(2);

                        if ( (LA19_3==NULL) ) {
                            alt19=3;
                        }
                        else if ( (LA19_3==NOT) ) {
                            alt19=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 19, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    case NOT:
                        {
                        alt19=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 19, 0, input);

                        throw nvae;
                    }

                    switch (alt19) {
                        case 1 :
                            // sqljet/src/Sql.g:190:6: ISNULL
                            {
                            ISNULL66=(Token)match(input,ISNULL,FOLLOW_ISNULL_in_cond_expr638);  
                            stream_ISNULL.add(ISNULL66);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 190:13: -> IS_NULL
                            {
                                adaptor.addChild(root_0, (Object)adaptor.create(IS_NULL, "IS_NULL"));

                            }

                            retval.tree = root_0;
                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:190:26: NOTNULL
                            {
                            NOTNULL67=(Token)match(input,NOTNULL,FOLLOW_NOTNULL_in_cond_expr646);  
                            stream_NOTNULL.add(NOTNULL67);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 190:34: -> NOT_NULL
                            {
                                adaptor.addChild(root_0, (Object)adaptor.create(NOT_NULL, "NOT_NULL"));

                            }

                            retval.tree = root_0;
                            }
                            break;
                        case 3 :
                            // sqljet/src/Sql.g:190:48: IS NULL
                            {
                            IS68=(Token)match(input,IS,FOLLOW_IS_in_cond_expr654);  
                            stream_IS.add(IS68);

                            NULL69=(Token)match(input,NULL,FOLLOW_NULL_in_cond_expr656);  
                            stream_NULL.add(NULL69);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 190:56: -> IS_NULL
                            {
                                adaptor.addChild(root_0, (Object)adaptor.create(IS_NULL, "IS_NULL"));

                            }

                            retval.tree = root_0;
                            }
                            break;
                        case 4 :
                            // sqljet/src/Sql.g:190:69: NOT NULL
                            {
                            NOT70=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr664);  
                            stream_NOT.add(NOT70);

                            NULL71=(Token)match(input,NULL,FOLLOW_NULL_in_cond_expr666);  
                            stream_NULL.add(NULL71);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 190:78: -> NOT_NULL
                            {
                                adaptor.addChild(root_0, (Object)adaptor.create(NOT_NULL, "NOT_NULL"));

                            }

                            retval.tree = root_0;
                            }
                            break;
                        case 5 :
                            // sqljet/src/Sql.g:190:92: IS NOT NULL
                            {
                            IS72=(Token)match(input,IS,FOLLOW_IS_in_cond_expr674);  
                            stream_IS.add(IS72);

                            NOT73=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr676);  
                            stream_NOT.add(NOT73);

                            NULL74=(Token)match(input,NULL,FOLLOW_NULL_in_cond_expr678);  
                            stream_NULL.add(NULL74);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 190:104: -> NOT_NULL
                            {
                                adaptor.addChild(root_0, (Object)adaptor.create(NOT_NULL, "NOT_NULL"));

                            }

                            retval.tree = root_0;
                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // sqljet/src/Sql.g:191:5: ( NOT )? BETWEEN e1= eq_subexpr AND e2= eq_subexpr
                    {
                    // sqljet/src/Sql.g:191:5: ( NOT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==NOT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // sqljet/src/Sql.g:191:5: NOT
                            {
                            NOT75=(Token)match(input,NOT,FOLLOW_NOT_in_cond_expr689);  
                            stream_NOT.add(NOT75);


                            }
                            break;

                    }

                    BETWEEN76=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_cond_expr692);  
                    stream_BETWEEN.add(BETWEEN76);

                    pushFollow(FOLLOW_eq_subexpr_in_cond_expr696);
                    e1=eq_subexpr();

                    state._fsp--;

                    stream_eq_subexpr.add(e1.getTree());
                    AND77=(Token)match(input,AND,FOLLOW_AND_in_cond_expr698);  
                    stream_AND.add(AND77);

                    pushFollow(FOLLOW_eq_subexpr_in_cond_expr702);
                    e2=eq_subexpr();

                    state._fsp--;

                    stream_eq_subexpr.add(e2.getTree());


                    // AST REWRITE
                    // elements: e2, e1, BETWEEN, NOT, AND
                    // token labels: 
                    // rule labels: e2, e1, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_e2=new RewriteRuleSubtreeStream(adaptor,"rule e2",e2!=null?e2.tree:null);
                    RewriteRuleSubtreeStream stream_e1=new RewriteRuleSubtreeStream(adaptor,"rule e1",e1!=null?e1.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 191:50: -> ^( BETWEEN ( NOT )? ^( AND $e1 $e2) )
                    {
                        // sqljet/src/Sql.g:191:53: ^( BETWEEN ( NOT )? ^( AND $e1 $e2) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_BETWEEN.nextNode(), root_1);

                        // sqljet/src/Sql.g:191:63: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, stream_NOT.nextNode());

                        }
                        stream_NOT.reset();
                        // sqljet/src/Sql.g:191:68: ^( AND $e1 $e2)
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_AND.nextNode(), root_2);

                        adaptor.addChild(root_2, stream_e1.nextTree());
                        adaptor.addChild(root_2, stream_e2.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // sqljet/src/Sql.g:192:5: ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // sqljet/src/Sql.g:192:5: ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+
                    int cnt21=0;
                    loop21:
                    do {
                        int alt21=2;
                        alt21 = dfa21.predict(input);
                        switch (alt21) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:192:6: ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr
                    	    {
                    	    set78=(Token)input.LT(1);
                    	    set78=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=EQUALS && input.LA(1)<=NOT_EQUALS2) ) {
                    	        input.consume();
                    	        root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set78), root_0);
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }

                    	    pushFollow(FOLLOW_eq_subexpr_in_cond_expr745);
                    	    eq_subexpr79=eq_subexpr();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, eq_subexpr79.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt21 >= 1 ) break loop21;
                                EarlyExitException eee =
                                    new EarlyExitException(21, input);
                                throw eee;
                        }
                        cnt21++;
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cond_expr"

    public static class match_op_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "match_op"
    // sqljet/src/Sql.g:195:1: match_op : ( LIKE | GLOB | REGEXP | MATCH );
    public final SqlParser.match_op_return match_op() throws RecognitionException {
        SqlParser.match_op_return retval = new SqlParser.match_op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set80=null;

        Object set80_tree=null;

        try {
            // sqljet/src/Sql.g:195:9: ( LIKE | GLOB | REGEXP | MATCH )
            // sqljet/src/Sql.g:
            {
            root_0 = (Object)adaptor.nil();

            set80=(Token)input.LT(1);
            if ( (input.LA(1)>=LIKE && input.LA(1)<=MATCH) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set80));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "match_op"

    public static class eq_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "eq_subexpr"
    // sqljet/src/Sql.g:197:1: eq_subexpr : neq_subexpr ( ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr )* ;
    public final SqlParser.eq_subexpr_return eq_subexpr() throws RecognitionException {
        SqlParser.eq_subexpr_return retval = new SqlParser.eq_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set82=null;
        SqlParser.neq_subexpr_return neq_subexpr81 = null;

        SqlParser.neq_subexpr_return neq_subexpr83 = null;


        Object set82_tree=null;

        try {
            // sqljet/src/Sql.g:197:11: ( neq_subexpr ( ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr )* )
            // sqljet/src/Sql.g:197:13: neq_subexpr ( ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_neq_subexpr_in_eq_subexpr778);
            neq_subexpr81=neq_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, neq_subexpr81.getTree());
            // sqljet/src/Sql.g:197:25: ( ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr )*
            loop23:
            do {
                int alt23=2;
                alt23 = dfa23.predict(input);
                switch (alt23) {
            	case 1 :
            	    // sqljet/src/Sql.g:197:26: ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr
            	    {
            	    set82=(Token)input.LT(1);
            	    set82=(Token)input.LT(1);
            	    if ( (input.LA(1)>=LESS && input.LA(1)<=GREATER_OR_EQ) ) {
            	        input.consume();
            	        root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set82), root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_neq_subexpr_in_eq_subexpr798);
            	    neq_subexpr83=neq_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, neq_subexpr83.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "eq_subexpr"

    public static class neq_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "neq_subexpr"
    // sqljet/src/Sql.g:199:1: neq_subexpr : bit_subexpr ( ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr )* ;
    public final SqlParser.neq_subexpr_return neq_subexpr() throws RecognitionException {
        SqlParser.neq_subexpr_return retval = new SqlParser.neq_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set85=null;
        SqlParser.bit_subexpr_return bit_subexpr84 = null;

        SqlParser.bit_subexpr_return bit_subexpr86 = null;


        Object set85_tree=null;

        try {
            // sqljet/src/Sql.g:199:12: ( bit_subexpr ( ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr )* )
            // sqljet/src/Sql.g:199:14: bit_subexpr ( ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_bit_subexpr_in_neq_subexpr807);
            bit_subexpr84=bit_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, bit_subexpr84.getTree());
            // sqljet/src/Sql.g:199:26: ( ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr )*
            loop24:
            do {
                int alt24=2;
                alt24 = dfa24.predict(input);
                switch (alt24) {
            	case 1 :
            	    // sqljet/src/Sql.g:199:27: ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr
            	    {
            	    set85=(Token)input.LT(1);
            	    set85=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SHIFT_LEFT && input.LA(1)<=PIPE) ) {
            	        input.consume();
            	        root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set85), root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_bit_subexpr_in_neq_subexpr827);
            	    bit_subexpr86=bit_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, bit_subexpr86.getTree());

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "neq_subexpr"

    public static class bit_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bit_subexpr"
    // sqljet/src/Sql.g:201:1: bit_subexpr : add_subexpr ( ( PLUS | MINUS ) add_subexpr )* ;
    public final SqlParser.bit_subexpr_return bit_subexpr() throws RecognitionException {
        SqlParser.bit_subexpr_return retval = new SqlParser.bit_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set88=null;
        SqlParser.add_subexpr_return add_subexpr87 = null;

        SqlParser.add_subexpr_return add_subexpr89 = null;


        Object set88_tree=null;

        try {
            // sqljet/src/Sql.g:201:12: ( add_subexpr ( ( PLUS | MINUS ) add_subexpr )* )
            // sqljet/src/Sql.g:201:14: add_subexpr ( ( PLUS | MINUS ) add_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_add_subexpr_in_bit_subexpr836);
            add_subexpr87=add_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, add_subexpr87.getTree());
            // sqljet/src/Sql.g:201:26: ( ( PLUS | MINUS ) add_subexpr )*
            loop25:
            do {
                int alt25=2;
                alt25 = dfa25.predict(input);
                switch (alt25) {
            	case 1 :
            	    // sqljet/src/Sql.g:201:27: ( PLUS | MINUS ) add_subexpr
            	    {
            	    set88=(Token)input.LT(1);
            	    set88=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
            	        input.consume();
            	        root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set88), root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_add_subexpr_in_bit_subexpr848);
            	    add_subexpr89=add_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, add_subexpr89.getTree());

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bit_subexpr"

    public static class add_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "add_subexpr"
    // sqljet/src/Sql.g:203:1: add_subexpr : mul_subexpr ( ( ASTERISK | SLASH | PERCENT ) mul_subexpr )* ;
    public final SqlParser.add_subexpr_return add_subexpr() throws RecognitionException {
        SqlParser.add_subexpr_return retval = new SqlParser.add_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set91=null;
        SqlParser.mul_subexpr_return mul_subexpr90 = null;

        SqlParser.mul_subexpr_return mul_subexpr92 = null;


        Object set91_tree=null;

        try {
            // sqljet/src/Sql.g:203:12: ( mul_subexpr ( ( ASTERISK | SLASH | PERCENT ) mul_subexpr )* )
            // sqljet/src/Sql.g:203:14: mul_subexpr ( ( ASTERISK | SLASH | PERCENT ) mul_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_mul_subexpr_in_add_subexpr857);
            mul_subexpr90=mul_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, mul_subexpr90.getTree());
            // sqljet/src/Sql.g:203:26: ( ( ASTERISK | SLASH | PERCENT ) mul_subexpr )*
            loop26:
            do {
                int alt26=2;
                alt26 = dfa26.predict(input);
                switch (alt26) {
            	case 1 :
            	    // sqljet/src/Sql.g:203:27: ( ASTERISK | SLASH | PERCENT ) mul_subexpr
            	    {
            	    set91=(Token)input.LT(1);
            	    set91=(Token)input.LT(1);
            	    if ( (input.LA(1)>=ASTERISK && input.LA(1)<=PERCENT) ) {
            	        input.consume();
            	        root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set91), root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_mul_subexpr_in_add_subexpr873);
            	    mul_subexpr92=mul_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mul_subexpr92.getTree());

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "add_subexpr"

    public static class mul_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mul_subexpr"
    // sqljet/src/Sql.g:205:1: mul_subexpr : con_subexpr ( DOUBLE_PIPE con_subexpr )* ;
    public final SqlParser.mul_subexpr_return mul_subexpr() throws RecognitionException {
        SqlParser.mul_subexpr_return retval = new SqlParser.mul_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE94=null;
        SqlParser.con_subexpr_return con_subexpr93 = null;

        SqlParser.con_subexpr_return con_subexpr95 = null;


        Object DOUBLE_PIPE94_tree=null;

        try {
            // sqljet/src/Sql.g:205:12: ( con_subexpr ( DOUBLE_PIPE con_subexpr )* )
            // sqljet/src/Sql.g:205:14: con_subexpr ( DOUBLE_PIPE con_subexpr )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_con_subexpr_in_mul_subexpr882);
            con_subexpr93=con_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, con_subexpr93.getTree());
            // sqljet/src/Sql.g:205:26: ( DOUBLE_PIPE con_subexpr )*
            loop27:
            do {
                int alt27=2;
                alt27 = dfa27.predict(input);
                switch (alt27) {
            	case 1 :
            	    // sqljet/src/Sql.g:205:27: DOUBLE_PIPE con_subexpr
            	    {
            	    DOUBLE_PIPE94=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_mul_subexpr885); 
            	    DOUBLE_PIPE94_tree = (Object)adaptor.create(DOUBLE_PIPE94);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE94_tree, root_0);

            	    pushFollow(FOLLOW_con_subexpr_in_mul_subexpr888);
            	    con_subexpr95=con_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, con_subexpr95.getTree());

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mul_subexpr"

    public static class con_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "con_subexpr"
    // sqljet/src/Sql.g:207:1: con_subexpr : ( unary_subexpr | unary_op unary_subexpr -> ^( unary_op unary_subexpr ) );
    public final SqlParser.con_subexpr_return con_subexpr() throws RecognitionException {
        SqlParser.con_subexpr_return retval = new SqlParser.con_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.unary_subexpr_return unary_subexpr96 = null;

        SqlParser.unary_op_return unary_op97 = null;

        SqlParser.unary_subexpr_return unary_subexpr98 = null;


        RewriteRuleSubtreeStream stream_unary_op=new RewriteRuleSubtreeStream(adaptor,"rule unary_op");
        RewriteRuleSubtreeStream stream_unary_subexpr=new RewriteRuleSubtreeStream(adaptor,"rule unary_subexpr");
        try {
            // sqljet/src/Sql.g:207:12: ( unary_subexpr | unary_op unary_subexpr -> ^( unary_op unary_subexpr ) )
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // sqljet/src/Sql.g:207:14: unary_subexpr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unary_subexpr_in_con_subexpr897);
                    unary_subexpr96=unary_subexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, unary_subexpr96.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:207:30: unary_op unary_subexpr
                    {
                    pushFollow(FOLLOW_unary_op_in_con_subexpr901);
                    unary_op97=unary_op();

                    state._fsp--;

                    stream_unary_op.add(unary_op97.getTree());
                    pushFollow(FOLLOW_unary_subexpr_in_con_subexpr903);
                    unary_subexpr98=unary_subexpr();

                    state._fsp--;

                    stream_unary_subexpr.add(unary_subexpr98.getTree());


                    // AST REWRITE
                    // elements: unary_op, unary_subexpr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 207:53: -> ^( unary_op unary_subexpr )
                    {
                        // sqljet/src/Sql.g:207:56: ^( unary_op unary_subexpr )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_unary_op.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_unary_subexpr.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "con_subexpr"

    public static class unary_op_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_op"
    // sqljet/src/Sql.g:209:1: unary_op : ( PLUS | MINUS | TILDA | NOT );
    public final SqlParser.unary_op_return unary_op() throws RecognitionException {
        SqlParser.unary_op_return retval = new SqlParser.unary_op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set99=null;

        Object set99_tree=null;

        try {
            // sqljet/src/Sql.g:209:9: ( PLUS | MINUS | TILDA | NOT )
            // sqljet/src/Sql.g:
            {
            root_0 = (Object)adaptor.nil();

            set99=(Token)input.LT(1);
            if ( input.LA(1)==NOT||(input.LA(1)>=PLUS && input.LA(1)<=MINUS)||input.LA(1)==TILDA ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set99));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unary_op"

    public static class unary_subexpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_subexpr"
    // sqljet/src/Sql.g:211:1: unary_subexpr : atom_expr ( COLLATE collation_name= ID )? ;
    public final SqlParser.unary_subexpr_return unary_subexpr() throws RecognitionException {
        SqlParser.unary_subexpr_return retval = new SqlParser.unary_subexpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token collation_name=null;
        Token COLLATE101=null;
        SqlParser.atom_expr_return atom_expr100 = null;


        Object collation_name_tree=null;
        Object COLLATE101_tree=null;

        try {
            // sqljet/src/Sql.g:211:14: ( atom_expr ( COLLATE collation_name= ID )? )
            // sqljet/src/Sql.g:211:16: atom_expr ( COLLATE collation_name= ID )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_atom_expr_in_unary_subexpr937);
            atom_expr100=atom_expr();

            state._fsp--;

            adaptor.addChild(root_0, atom_expr100.getTree());
            // sqljet/src/Sql.g:211:26: ( COLLATE collation_name= ID )?
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // sqljet/src/Sql.g:211:27: COLLATE collation_name= ID
                    {
                    COLLATE101=(Token)match(input,COLLATE,FOLLOW_COLLATE_in_unary_subexpr940); 
                    COLLATE101_tree = (Object)adaptor.create(COLLATE101);
                    root_0 = (Object)adaptor.becomeRoot(COLLATE101_tree, root_0);

                    collation_name=(Token)match(input,ID,FOLLOW_ID_in_unary_subexpr945); 
                    collation_name_tree = (Object)adaptor.create(collation_name);
                    adaptor.addChild(root_0, collation_name_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unary_subexpr"

    public static class atom_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom_expr"
    // sqljet/src/Sql.g:213:1: atom_expr : ( literal_value | bind_parameter | ( (database_name= id DOT )? table_name= id DOT )? column_name= ID -> ^( COLUMN_EXPRESSION ^( $column_name ( ^( $table_name ( $database_name)? ) )? ) ) | name= ID LPAREN ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )? RPAREN -> ^( FUNCTION_EXPRESSION $name ( DISTINCT )? ( $args)* ( ASTERISK )? ) | LPAREN expr RPAREN | CAST LPAREN expr AS type_name RPAREN | CASE (case_expr= expr )? ( when_expr )+ ( ELSE else_expr= expr )? END -> ^( CASE ( $case_expr)? ( when_expr )+ ( $else_expr)? ) | raise_function );
    public final SqlParser.atom_expr_return atom_expr() throws RecognitionException {
        SqlParser.atom_expr_return retval = new SqlParser.atom_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token column_name=null;
        Token name=null;
        Token DOT104=null;
        Token DOT105=null;
        Token LPAREN106=null;
        Token DISTINCT107=null;
        Token COMMA108=null;
        Token ASTERISK109=null;
        Token RPAREN110=null;
        Token LPAREN111=null;
        Token RPAREN113=null;
        Token CAST114=null;
        Token LPAREN115=null;
        Token AS117=null;
        Token RPAREN119=null;
        Token CASE120=null;
        Token ELSE122=null;
        Token END123=null;
        List list_args=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.expr_return case_expr = null;

        SqlParser.expr_return else_expr = null;

        SqlParser.literal_value_return literal_value102 = null;

        SqlParser.bind_parameter_return bind_parameter103 = null;

        SqlParser.expr_return expr112 = null;

        SqlParser.expr_return expr116 = null;

        SqlParser.type_name_return type_name118 = null;

        SqlParser.when_expr_return when_expr121 = null;

        SqlParser.raise_function_return raise_function124 = null;

        SqlParser.expr_return args = null;
         args = null;
        Object column_name_tree=null;
        Object name_tree=null;
        Object DOT104_tree=null;
        Object DOT105_tree=null;
        Object LPAREN106_tree=null;
        Object DISTINCT107_tree=null;
        Object COMMA108_tree=null;
        Object ASTERISK109_tree=null;
        Object RPAREN110_tree=null;
        Object LPAREN111_tree=null;
        Object RPAREN113_tree=null;
        Object CAST114_tree=null;
        Object LPAREN115_tree=null;
        Object AS117_tree=null;
        Object RPAREN119_tree=null;
        Object CASE120_tree=null;
        Object ELSE122_tree=null;
        Object END123_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_CASE=new RewriteRuleTokenStream(adaptor,"token CASE");
        RewriteRuleTokenStream stream_DISTINCT=new RewriteRuleTokenStream(adaptor,"token DISTINCT");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_when_expr=new RewriteRuleSubtreeStream(adaptor,"rule when_expr");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:214:3: ( literal_value | bind_parameter | ( (database_name= id DOT )? table_name= id DOT )? column_name= ID -> ^( COLUMN_EXPRESSION ^( $column_name ( ^( $table_name ( $database_name)? ) )? ) ) | name= ID LPAREN ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )? RPAREN -> ^( FUNCTION_EXPRESSION $name ( DISTINCT )? ( $args)* ( ASTERISK )? ) | LPAREN expr RPAREN | CAST LPAREN expr AS type_name RPAREN | CASE (case_expr= expr )? ( when_expr )+ ( ELSE else_expr= expr )? END -> ^( CASE ( $case_expr)? ( when_expr )+ ( $else_expr)? ) | raise_function )
            int alt38=8;
            alt38 = dfa38.predict(input);
            switch (alt38) {
                case 1 :
                    // sqljet/src/Sql.g:214:5: literal_value
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_value_in_atom_expr957);
                    literal_value102=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, literal_value102.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:215:5: bind_parameter
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_bind_parameter_in_atom_expr963);
                    bind_parameter103=bind_parameter();

                    state._fsp--;

                    adaptor.addChild(root_0, bind_parameter103.getTree());

                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:216:5: ( (database_name= id DOT )? table_name= id DOT )? column_name= ID
                    {
                    // sqljet/src/Sql.g:216:5: ( (database_name= id DOT )? table_name= id DOT )?
                    int alt31=2;
                    alt31 = dfa31.predict(input);
                    switch (alt31) {
                        case 1 :
                            // sqljet/src/Sql.g:216:6: (database_name= id DOT )? table_name= id DOT
                            {
                            // sqljet/src/Sql.g:216:6: (database_name= id DOT )?
                            int alt30=2;
                            alt30 = dfa30.predict(input);
                            switch (alt30) {
                                case 1 :
                                    // sqljet/src/Sql.g:216:7: database_name= id DOT
                                    {
                                    pushFollow(FOLLOW_id_in_atom_expr973);
                                    database_name=id();

                                    state._fsp--;

                                    stream_id.add(database_name.getTree());
                                    DOT104=(Token)match(input,DOT,FOLLOW_DOT_in_atom_expr975);  
                                    stream_DOT.add(DOT104);


                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_id_in_atom_expr981);
                            table_name=id();

                            state._fsp--;

                            stream_id.add(table_name.getTree());
                            DOT105=(Token)match(input,DOT,FOLLOW_DOT_in_atom_expr983);  
                            stream_DOT.add(DOT105);


                            }
                            break;

                    }

                    column_name=(Token)match(input,ID,FOLLOW_ID_in_atom_expr989);  
                    stream_ID.add(column_name);



                    // AST REWRITE
                    // elements: table_name, database_name, column_name
                    // token labels: column_name
                    // rule labels: database_name, retval, table_name
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_column_name=new RewriteRuleTokenStream(adaptor,"token column_name",column_name);
                    RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 216:65: -> ^( COLUMN_EXPRESSION ^( $column_name ( ^( $table_name ( $database_name)? ) )? ) )
                    {
                        // sqljet/src/Sql.g:216:68: ^( COLUMN_EXPRESSION ^( $column_name ( ^( $table_name ( $database_name)? ) )? ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMN_EXPRESSION, "COLUMN_EXPRESSION"), root_1);

                        // sqljet/src/Sql.g:216:88: ^( $column_name ( ^( $table_name ( $database_name)? ) )? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_column_name.nextNode(), root_2);

                        // sqljet/src/Sql.g:216:103: ( ^( $table_name ( $database_name)? ) )?
                        if ( stream_table_name.hasNext()||stream_database_name.hasNext() ) {
                            // sqljet/src/Sql.g:216:103: ^( $table_name ( $database_name)? )
                            {
                            Object root_3 = (Object)adaptor.nil();
                            root_3 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_3);

                            // sqljet/src/Sql.g:216:117: ( $database_name)?
                            if ( stream_database_name.hasNext() ) {
                                adaptor.addChild(root_3, stream_database_name.nextTree());

                            }
                            stream_database_name.reset();

                            adaptor.addChild(root_2, root_3);
                            }

                        }
                        stream_table_name.reset();
                        stream_database_name.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:217:5: name= ID LPAREN ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )? RPAREN
                    {
                    name=(Token)match(input,ID,FOLLOW_ID_in_atom_expr1018);  
                    stream_ID.add(name);

                    LPAREN106=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_atom_expr1020);  
                    stream_LPAREN.add(LPAREN106);

                    // sqljet/src/Sql.g:217:20: ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )?
                    int alt34=3;
                    alt34 = dfa34.predict(input);
                    switch (alt34) {
                        case 1 :
                            // sqljet/src/Sql.g:217:21: ( DISTINCT )? args+= expr ( COMMA args+= expr )*
                            {
                            // sqljet/src/Sql.g:217:21: ( DISTINCT )?
                            int alt32=2;
                            alt32 = dfa32.predict(input);
                            switch (alt32) {
                                case 1 :
                                    // sqljet/src/Sql.g:217:21: DISTINCT
                                    {
                                    DISTINCT107=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_atom_expr1023);  
                                    stream_DISTINCT.add(DISTINCT107);


                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_expr_in_atom_expr1028);
                            args=expr();

                            state._fsp--;

                            stream_expr.add(args.getTree());
                            if (list_args==null) list_args=new ArrayList();
                            list_args.add(args.getTree());

                            // sqljet/src/Sql.g:217:42: ( COMMA args+= expr )*
                            loop33:
                            do {
                                int alt33=2;
                                int LA33_0 = input.LA(1);

                                if ( (LA33_0==COMMA) ) {
                                    alt33=1;
                                }


                                switch (alt33) {
                            	case 1 :
                            	    // sqljet/src/Sql.g:217:43: COMMA args+= expr
                            	    {
                            	    COMMA108=(Token)match(input,COMMA,FOLLOW_COMMA_in_atom_expr1031);  
                            	    stream_COMMA.add(COMMA108);

                            	    pushFollow(FOLLOW_expr_in_atom_expr1035);
                            	    args=expr();

                            	    state._fsp--;

                            	    stream_expr.add(args.getTree());
                            	    if (list_args==null) list_args=new ArrayList();
                            	    list_args.add(args.getTree());


                            	    }
                            	    break;

                            	default :
                            	    break loop33;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:217:64: ASTERISK
                            {
                            ASTERISK109=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_atom_expr1041);  
                            stream_ASTERISK.add(ASTERISK109);


                            }
                            break;

                    }

                    RPAREN110=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_atom_expr1045);  
                    stream_RPAREN.add(RPAREN110);



                    // AST REWRITE
                    // elements: ASTERISK, name, DISTINCT, args
                    // token labels: name
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: args
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"token args",list_args);
                    root_0 = (Object)adaptor.nil();
                    // 217:82: -> ^( FUNCTION_EXPRESSION $name ( DISTINCT )? ( $args)* ( ASTERISK )? )
                    {
                        // sqljet/src/Sql.g:217:85: ^( FUNCTION_EXPRESSION $name ( DISTINCT )? ( $args)* ( ASTERISK )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_EXPRESSION, "FUNCTION_EXPRESSION"), root_1);

                        adaptor.addChild(root_1, stream_name.nextNode());
                        // sqljet/src/Sql.g:217:113: ( DISTINCT )?
                        if ( stream_DISTINCT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DISTINCT.nextNode());

                        }
                        stream_DISTINCT.reset();
                        // sqljet/src/Sql.g:217:123: ( $args)*
                        while ( stream_args.hasNext() ) {
                            adaptor.addChild(root_1, stream_args.nextTree());

                        }
                        stream_args.reset();
                        // sqljet/src/Sql.g:217:130: ( ASTERISK )?
                        if ( stream_ASTERISK.hasNext() ) {
                            adaptor.addChild(root_1, stream_ASTERISK.nextNode());

                        }
                        stream_ASTERISK.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // sqljet/src/Sql.g:218:5: LPAREN expr RPAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAREN111=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_atom_expr1070); 
                    pushFollow(FOLLOW_expr_in_atom_expr1073);
                    expr112=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr112.getTree());
                    RPAREN113=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_atom_expr1075); 

                    }
                    break;
                case 6 :
                    // sqljet/src/Sql.g:219:5: CAST LPAREN expr AS type_name RPAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    CAST114=(Token)match(input,CAST,FOLLOW_CAST_in_atom_expr1082); 
                    CAST114_tree = (Object)adaptor.create(CAST114);
                    root_0 = (Object)adaptor.becomeRoot(CAST114_tree, root_0);

                    LPAREN115=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_atom_expr1085); 
                    pushFollow(FOLLOW_expr_in_atom_expr1088);
                    expr116=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr116.getTree());
                    AS117=(Token)match(input,AS,FOLLOW_AS_in_atom_expr1090); 
                    pushFollow(FOLLOW_type_name_in_atom_expr1093);
                    type_name118=type_name();

                    state._fsp--;

                    adaptor.addChild(root_0, type_name118.getTree());
                    RPAREN119=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_atom_expr1095); 

                    }
                    break;
                case 7 :
                    // sqljet/src/Sql.g:222:5: CASE (case_expr= expr )? ( when_expr )+ ( ELSE else_expr= expr )? END
                    {
                    CASE120=(Token)match(input,CASE,FOLLOW_CASE_in_atom_expr1104);  
                    stream_CASE.add(CASE120);

                    // sqljet/src/Sql.g:222:10: (case_expr= expr )?
                    int alt35=2;
                    alt35 = dfa35.predict(input);
                    switch (alt35) {
                        case 1 :
                            // sqljet/src/Sql.g:222:11: case_expr= expr
                            {
                            pushFollow(FOLLOW_expr_in_atom_expr1109);
                            case_expr=expr();

                            state._fsp--;

                            stream_expr.add(case_expr.getTree());

                            }
                            break;

                    }

                    // sqljet/src/Sql.g:222:28: ( when_expr )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==WHEN) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:222:28: when_expr
                    	    {
                    	    pushFollow(FOLLOW_when_expr_in_atom_expr1113);
                    	    when_expr121=when_expr();

                    	    state._fsp--;

                    	    stream_when_expr.add(when_expr121.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt36 >= 1 ) break loop36;
                                EarlyExitException eee =
                                    new EarlyExitException(36, input);
                                throw eee;
                        }
                        cnt36++;
                    } while (true);

                    // sqljet/src/Sql.g:222:39: ( ELSE else_expr= expr )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==ELSE) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // sqljet/src/Sql.g:222:40: ELSE else_expr= expr
                            {
                            ELSE122=(Token)match(input,ELSE,FOLLOW_ELSE_in_atom_expr1117);  
                            stream_ELSE.add(ELSE122);

                            pushFollow(FOLLOW_expr_in_atom_expr1121);
                            else_expr=expr();

                            state._fsp--;

                            stream_expr.add(else_expr.getTree());

                            }
                            break;

                    }

                    END123=(Token)match(input,END,FOLLOW_END_in_atom_expr1125);  
                    stream_END.add(END123);



                    // AST REWRITE
                    // elements: case_expr, when_expr, else_expr, CASE
                    // token labels: 
                    // rule labels: case_expr, else_expr, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_case_expr=new RewriteRuleSubtreeStream(adaptor,"rule case_expr",case_expr!=null?case_expr.tree:null);
                    RewriteRuleSubtreeStream stream_else_expr=new RewriteRuleSubtreeStream(adaptor,"rule else_expr",else_expr!=null?else_expr.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 222:66: -> ^( CASE ( $case_expr)? ( when_expr )+ ( $else_expr)? )
                    {
                        // sqljet/src/Sql.g:222:69: ^( CASE ( $case_expr)? ( when_expr )+ ( $else_expr)? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_CASE.nextNode(), root_1);

                        // sqljet/src/Sql.g:222:76: ( $case_expr)?
                        if ( stream_case_expr.hasNext() ) {
                            adaptor.addChild(root_1, stream_case_expr.nextTree());

                        }
                        stream_case_expr.reset();
                        if ( !(stream_when_expr.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_when_expr.hasNext() ) {
                            adaptor.addChild(root_1, stream_when_expr.nextTree());

                        }
                        stream_when_expr.reset();
                        // sqljet/src/Sql.g:222:99: ( $else_expr)?
                        if ( stream_else_expr.hasNext() ) {
                            adaptor.addChild(root_1, stream_else_expr.nextTree());

                        }
                        stream_else_expr.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 8 :
                    // sqljet/src/Sql.g:223:5: raise_function
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_raise_function_in_atom_expr1148);
                    raise_function124=raise_function();

                    state._fsp--;

                    adaptor.addChild(root_0, raise_function124.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom_expr"

    public static class when_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "when_expr"
    // sqljet/src/Sql.g:226:1: when_expr : WHEN e1= expr THEN e2= expr -> ^( WHEN $e1 $e2) ;
    public final SqlParser.when_expr_return when_expr() throws RecognitionException {
        SqlParser.when_expr_return retval = new SqlParser.when_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN125=null;
        Token THEN126=null;
        SqlParser.expr_return e1 = null;

        SqlParser.expr_return e2 = null;


        Object WHEN125_tree=null;
        Object THEN126_tree=null;
        RewriteRuleTokenStream stream_THEN=new RewriteRuleTokenStream(adaptor,"token THEN");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // sqljet/src/Sql.g:226:10: ( WHEN e1= expr THEN e2= expr -> ^( WHEN $e1 $e2) )
            // sqljet/src/Sql.g:226:12: WHEN e1= expr THEN e2= expr
            {
            WHEN125=(Token)match(input,WHEN,FOLLOW_WHEN_in_when_expr1158);  
            stream_WHEN.add(WHEN125);

            pushFollow(FOLLOW_expr_in_when_expr1162);
            e1=expr();

            state._fsp--;

            stream_expr.add(e1.getTree());
            THEN126=(Token)match(input,THEN,FOLLOW_THEN_in_when_expr1164);  
            stream_THEN.add(THEN126);

            pushFollow(FOLLOW_expr_in_when_expr1168);
            e2=expr();

            state._fsp--;

            stream_expr.add(e2.getTree());


            // AST REWRITE
            // elements: e1, e2, WHEN
            // token labels: 
            // rule labels: e2, e1, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_e2=new RewriteRuleSubtreeStream(adaptor,"rule e2",e2!=null?e2.tree:null);
            RewriteRuleSubtreeStream stream_e1=new RewriteRuleSubtreeStream(adaptor,"rule e1",e1!=null?e1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 226:38: -> ^( WHEN $e1 $e2)
            {
                // sqljet/src/Sql.g:226:41: ^( WHEN $e1 $e2)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_WHEN.nextNode(), root_1);

                adaptor.addChild(root_1, stream_e1.nextTree());
                adaptor.addChild(root_1, stream_e2.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "when_expr"

    public static class literal_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal_value"
    // sqljet/src/Sql.g:228:1: literal_value : ( INTEGER -> ^( INTEGER_LITERAL INTEGER ) | FLOAT -> ^( FLOAT_LITERAL FLOAT ) | STRING -> ^( STRING_LITERAL STRING ) | BLOB -> ^( BLOB_LITERAL BLOB ) | NULL | CURRENT_TIME -> ^( FUNCTION_LITERAL CURRENT_TIME ) | CURRENT_DATE -> ^( FUNCTION_LITERAL CURRENT_DATE ) | CURRENT_TIMESTAMP -> ^( FUNCTION_LITERAL CURRENT_TIMESTAMP ) );
    public final SqlParser.literal_value_return literal_value() throws RecognitionException {
        SqlParser.literal_value_return retval = new SqlParser.literal_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INTEGER127=null;
        Token FLOAT128=null;
        Token STRING129=null;
        Token BLOB130=null;
        Token NULL131=null;
        Token CURRENT_TIME132=null;
        Token CURRENT_DATE133=null;
        Token CURRENT_TIMESTAMP134=null;

        Object INTEGER127_tree=null;
        Object FLOAT128_tree=null;
        Object STRING129_tree=null;
        Object BLOB130_tree=null;
        Object NULL131_tree=null;
        Object CURRENT_TIME132_tree=null;
        Object CURRENT_DATE133_tree=null;
        Object CURRENT_TIMESTAMP134_tree=null;
        RewriteRuleTokenStream stream_CURRENT_TIME=new RewriteRuleTokenStream(adaptor,"token CURRENT_TIME");
        RewriteRuleTokenStream stream_CURRENT_TIMESTAMP=new RewriteRuleTokenStream(adaptor,"token CURRENT_TIMESTAMP");
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_BLOB=new RewriteRuleTokenStream(adaptor,"token BLOB");
        RewriteRuleTokenStream stream_CURRENT_DATE=new RewriteRuleTokenStream(adaptor,"token CURRENT_DATE");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_FLOAT=new RewriteRuleTokenStream(adaptor,"token FLOAT");

        try {
            // sqljet/src/Sql.g:229:3: ( INTEGER -> ^( INTEGER_LITERAL INTEGER ) | FLOAT -> ^( FLOAT_LITERAL FLOAT ) | STRING -> ^( STRING_LITERAL STRING ) | BLOB -> ^( BLOB_LITERAL BLOB ) | NULL | CURRENT_TIME -> ^( FUNCTION_LITERAL CURRENT_TIME ) | CURRENT_DATE -> ^( FUNCTION_LITERAL CURRENT_DATE ) | CURRENT_TIMESTAMP -> ^( FUNCTION_LITERAL CURRENT_TIMESTAMP ) )
            int alt39=8;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt39=1;
                }
                break;
            case FLOAT:
                {
                alt39=2;
                }
                break;
            case STRING:
                {
                alt39=3;
                }
                break;
            case BLOB:
                {
                alt39=4;
                }
                break;
            case NULL:
                {
                alt39=5;
                }
                break;
            case CURRENT_TIME:
                {
                alt39=6;
                }
                break;
            case CURRENT_DATE:
                {
                alt39=7;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt39=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // sqljet/src/Sql.g:229:5: INTEGER
                    {
                    INTEGER127=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_literal_value1190);  
                    stream_INTEGER.add(INTEGER127);



                    // AST REWRITE
                    // elements: INTEGER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 229:13: -> ^( INTEGER_LITERAL INTEGER )
                    {
                        // sqljet/src/Sql.g:229:16: ^( INTEGER_LITERAL INTEGER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INTEGER_LITERAL, "INTEGER_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:230:5: FLOAT
                    {
                    FLOAT128=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_value1204);  
                    stream_FLOAT.add(FLOAT128);



                    // AST REWRITE
                    // elements: FLOAT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 230:11: -> ^( FLOAT_LITERAL FLOAT )
                    {
                        // sqljet/src/Sql.g:230:14: ^( FLOAT_LITERAL FLOAT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FLOAT_LITERAL, "FLOAT_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_FLOAT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:231:5: STRING
                    {
                    STRING129=(Token)match(input,STRING,FOLLOW_STRING_in_literal_value1218);  
                    stream_STRING.add(STRING129);



                    // AST REWRITE
                    // elements: STRING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 231:12: -> ^( STRING_LITERAL STRING )
                    {
                        // sqljet/src/Sql.g:231:15: ^( STRING_LITERAL STRING )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING_LITERAL, "STRING_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_STRING.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:232:5: BLOB
                    {
                    BLOB130=(Token)match(input,BLOB,FOLLOW_BLOB_in_literal_value1232);  
                    stream_BLOB.add(BLOB130);



                    // AST REWRITE
                    // elements: BLOB
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 232:10: -> ^( BLOB_LITERAL BLOB )
                    {
                        // sqljet/src/Sql.g:232:13: ^( BLOB_LITERAL BLOB )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOB_LITERAL, "BLOB_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_BLOB.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // sqljet/src/Sql.g:233:5: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL131=(Token)match(input,NULL,FOLLOW_NULL_in_literal_value1246); 
                    NULL131_tree = (Object)adaptor.create(NULL131);
                    adaptor.addChild(root_0, NULL131_tree);


                    }
                    break;
                case 6 :
                    // sqljet/src/Sql.g:234:5: CURRENT_TIME
                    {
                    CURRENT_TIME132=(Token)match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_literal_value1252);  
                    stream_CURRENT_TIME.add(CURRENT_TIME132);



                    // AST REWRITE
                    // elements: CURRENT_TIME
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 234:18: -> ^( FUNCTION_LITERAL CURRENT_TIME )
                    {
                        // sqljet/src/Sql.g:234:21: ^( FUNCTION_LITERAL CURRENT_TIME )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_LITERAL, "FUNCTION_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_CURRENT_TIME.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // sqljet/src/Sql.g:235:5: CURRENT_DATE
                    {
                    CURRENT_DATE133=(Token)match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_literal_value1266);  
                    stream_CURRENT_DATE.add(CURRENT_DATE133);



                    // AST REWRITE
                    // elements: CURRENT_DATE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 235:18: -> ^( FUNCTION_LITERAL CURRENT_DATE )
                    {
                        // sqljet/src/Sql.g:235:21: ^( FUNCTION_LITERAL CURRENT_DATE )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_LITERAL, "FUNCTION_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_CURRENT_DATE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 8 :
                    // sqljet/src/Sql.g:236:5: CURRENT_TIMESTAMP
                    {
                    CURRENT_TIMESTAMP134=(Token)match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_literal_value1280);  
                    stream_CURRENT_TIMESTAMP.add(CURRENT_TIMESTAMP134);



                    // AST REWRITE
                    // elements: CURRENT_TIMESTAMP
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 236:23: -> ^( FUNCTION_LITERAL CURRENT_TIMESTAMP )
                    {
                        // sqljet/src/Sql.g:236:26: ^( FUNCTION_LITERAL CURRENT_TIMESTAMP )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION_LITERAL, "FUNCTION_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_CURRENT_TIMESTAMP.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal_value"

    public static class bind_parameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bind_parameter"
    // sqljet/src/Sql.g:239:1: bind_parameter : ( QUESTION -> BIND | QUESTION position= INTEGER -> ^( BIND $position) | COLON name= id -> ^( BIND_NAME $name) | AT name= id -> ^( BIND_NAME $name) );
    public final SqlParser.bind_parameter_return bind_parameter() throws RecognitionException {
        SqlParser.bind_parameter_return retval = new SqlParser.bind_parameter_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token position=null;
        Token QUESTION135=null;
        Token QUESTION136=null;
        Token COLON137=null;
        Token AT138=null;
        SqlParser.id_return name = null;


        Object position_tree=null;
        Object QUESTION135_tree=null;
        Object QUESTION136_tree=null;
        Object COLON137_tree=null;
        Object AT138_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:240:3: ( QUESTION -> BIND | QUESTION position= INTEGER -> ^( BIND $position) | COLON name= id -> ^( BIND_NAME $name) | AT name= id -> ^( BIND_NAME $name) )
            int alt40=4;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // sqljet/src/Sql.g:240:5: QUESTION
                    {
                    QUESTION135=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_bind_parameter1301);  
                    stream_QUESTION.add(QUESTION135);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 240:14: -> BIND
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BIND, "BIND"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:241:5: QUESTION position= INTEGER
                    {
                    QUESTION136=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_bind_parameter1311);  
                    stream_QUESTION.add(QUESTION136);

                    position=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_bind_parameter1315);  
                    stream_INTEGER.add(position);



                    // AST REWRITE
                    // elements: position
                    // token labels: position
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_position=new RewriteRuleTokenStream(adaptor,"token position",position);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 241:31: -> ^( BIND $position)
                    {
                        // sqljet/src/Sql.g:241:34: ^( BIND $position)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BIND, "BIND"), root_1);

                        adaptor.addChild(root_1, stream_position.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:242:5: COLON name= id
                    {
                    COLON137=(Token)match(input,COLON,FOLLOW_COLON_in_bind_parameter1330);  
                    stream_COLON.add(COLON137);

                    pushFollow(FOLLOW_id_in_bind_parameter1334);
                    name=id();

                    state._fsp--;

                    stream_id.add(name.getTree());


                    // AST REWRITE
                    // elements: name
                    // token labels: 
                    // rule labels: retval, name
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name",name!=null?name.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 242:19: -> ^( BIND_NAME $name)
                    {
                        // sqljet/src/Sql.g:242:22: ^( BIND_NAME $name)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BIND_NAME, "BIND_NAME"), root_1);

                        adaptor.addChild(root_1, stream_name.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:243:5: AT name= id
                    {
                    AT138=(Token)match(input,AT,FOLLOW_AT_in_bind_parameter1349);  
                    stream_AT.add(AT138);

                    pushFollow(FOLLOW_id_in_bind_parameter1353);
                    name=id();

                    state._fsp--;

                    stream_id.add(name.getTree());


                    // AST REWRITE
                    // elements: name
                    // token labels: 
                    // rule labels: retval, name
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name",name!=null?name.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 243:16: -> ^( BIND_NAME $name)
                    {
                        // sqljet/src/Sql.g:243:19: ^( BIND_NAME $name)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BIND_NAME, "BIND_NAME"), root_1);

                        adaptor.addChild(root_1, stream_name.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bind_parameter"

    public static class raise_function_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "raise_function"
    // sqljet/src/Sql.g:248:1: raise_function : RAISE LPAREN ( IGNORE | ( ROLLBACK | ABORT | FAIL ) COMMA error_message= STRING ) RPAREN ;
    public final SqlParser.raise_function_return raise_function() throws RecognitionException {
        SqlParser.raise_function_return retval = new SqlParser.raise_function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token error_message=null;
        Token RAISE139=null;
        Token LPAREN140=null;
        Token IGNORE141=null;
        Token set142=null;
        Token COMMA143=null;
        Token RPAREN144=null;

        Object error_message_tree=null;
        Object RAISE139_tree=null;
        Object LPAREN140_tree=null;
        Object IGNORE141_tree=null;
        Object set142_tree=null;
        Object COMMA143_tree=null;
        Object RPAREN144_tree=null;

        try {
            // sqljet/src/Sql.g:248:15: ( RAISE LPAREN ( IGNORE | ( ROLLBACK | ABORT | FAIL ) COMMA error_message= STRING ) RPAREN )
            // sqljet/src/Sql.g:248:17: RAISE LPAREN ( IGNORE | ( ROLLBACK | ABORT | FAIL ) COMMA error_message= STRING ) RPAREN
            {
            root_0 = (Object)adaptor.nil();

            RAISE139=(Token)match(input,RAISE,FOLLOW_RAISE_in_raise_function1374); 
            RAISE139_tree = (Object)adaptor.create(RAISE139);
            root_0 = (Object)adaptor.becomeRoot(RAISE139_tree, root_0);

            LPAREN140=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_raise_function1377); 
            // sqljet/src/Sql.g:248:32: ( IGNORE | ( ROLLBACK | ABORT | FAIL ) COMMA error_message= STRING )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==IGNORE) ) {
                alt41=1;
            }
            else if ( ((LA41_0>=ROLLBACK && LA41_0<=FAIL)) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // sqljet/src/Sql.g:248:33: IGNORE
                    {
                    IGNORE141=(Token)match(input,IGNORE,FOLLOW_IGNORE_in_raise_function1381); 
                    IGNORE141_tree = (Object)adaptor.create(IGNORE141);
                    adaptor.addChild(root_0, IGNORE141_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:248:42: ( ROLLBACK | ABORT | FAIL ) COMMA error_message= STRING
                    {
                    set142=(Token)input.LT(1);
                    if ( (input.LA(1)>=ROLLBACK && input.LA(1)<=FAIL) ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set142));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    COMMA143=(Token)match(input,COMMA,FOLLOW_COMMA_in_raise_function1397); 
                    error_message=(Token)match(input,STRING,FOLLOW_STRING_in_raise_function1402); 
                    error_message_tree = (Object)adaptor.create(error_message);
                    adaptor.addChild(root_0, error_message_tree);


                    }
                    break;

            }

            RPAREN144=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_raise_function1405); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "raise_function"

    public static class type_name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type_name"
    // sqljet/src/Sql.g:250:1: type_name : (names+= ID )+ ( LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN )? -> ^( TYPE ^( TYPE_PARAMS ( $size1)? ( $size2)? ) ( $names)+ ) ;
    public final SqlParser.type_name_return type_name() throws RecognitionException {
        SqlParser.type_name_return retval = new SqlParser.type_name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LPAREN145=null;
        Token COMMA146=null;
        Token RPAREN147=null;
        Token names=null;
        List list_names=null;
        SqlParser.signed_number_return size1 = null;

        SqlParser.signed_number_return size2 = null;


        Object LPAREN145_tree=null;
        Object COMMA146_tree=null;
        Object RPAREN147_tree=null;
        Object names_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_signed_number=new RewriteRuleSubtreeStream(adaptor,"rule signed_number");
        try {
            // sqljet/src/Sql.g:250:10: ( (names+= ID )+ ( LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN )? -> ^( TYPE ^( TYPE_PARAMS ( $size1)? ( $size2)? ) ( $names)+ ) )
            // sqljet/src/Sql.g:250:12: (names+= ID )+ ( LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN )?
            {
            // sqljet/src/Sql.g:250:17: (names+= ID )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                alt42 = dfa42.predict(input);
                switch (alt42) {
            	case 1 :
            	    // sqljet/src/Sql.g:250:17: names+= ID
            	    {
            	    names=(Token)match(input,ID,FOLLOW_ID_in_type_name1415);  
            	    stream_ID.add(names);

            	    if (list_names==null) list_names=new ArrayList();
            	    list_names.add(names);


            	    }
            	    break;

            	default :
            	    if ( cnt42 >= 1 ) break loop42;
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
            } while (true);

            // sqljet/src/Sql.g:250:23: ( LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN )?
            int alt44=2;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // sqljet/src/Sql.g:250:24: LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN
                    {
                    LPAREN145=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_type_name1419);  
                    stream_LPAREN.add(LPAREN145);

                    pushFollow(FOLLOW_signed_number_in_type_name1423);
                    size1=signed_number();

                    state._fsp--;

                    stream_signed_number.add(size1.getTree());
                    // sqljet/src/Sql.g:250:51: ( COMMA size2= signed_number )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==COMMA) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // sqljet/src/Sql.g:250:52: COMMA size2= signed_number
                            {
                            COMMA146=(Token)match(input,COMMA,FOLLOW_COMMA_in_type_name1426);  
                            stream_COMMA.add(COMMA146);

                            pushFollow(FOLLOW_signed_number_in_type_name1430);
                            size2=signed_number();

                            state._fsp--;

                            stream_signed_number.add(size2.getTree());

                            }
                            break;

                    }

                    RPAREN147=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_type_name1434);  
                    stream_RPAREN.add(RPAREN147);


                    }
                    break;

            }



            // AST REWRITE
            // elements: names, size1, size2
            // token labels: 
            // rule labels: size2, size1, retval
            // token list labels: names
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_names=new RewriteRuleTokenStream(adaptor,"token names", list_names);
            RewriteRuleSubtreeStream stream_size2=new RewriteRuleSubtreeStream(adaptor,"rule size2",size2!=null?size2.tree:null);
            RewriteRuleSubtreeStream stream_size1=new RewriteRuleSubtreeStream(adaptor,"rule size1",size1!=null?size1.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 251:1: -> ^( TYPE ^( TYPE_PARAMS ( $size1)? ( $size2)? ) ( $names)+ )
            {
                // sqljet/src/Sql.g:251:4: ^( TYPE ^( TYPE_PARAMS ( $size1)? ( $size2)? ) ( $names)+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE, "TYPE"), root_1);

                // sqljet/src/Sql.g:251:11: ^( TYPE_PARAMS ( $size1)? ( $size2)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE_PARAMS, "TYPE_PARAMS"), root_2);

                // sqljet/src/Sql.g:251:25: ( $size1)?
                if ( stream_size1.hasNext() ) {
                    adaptor.addChild(root_2, stream_size1.nextTree());

                }
                stream_size1.reset();
                // sqljet/src/Sql.g:251:33: ( $size2)?
                if ( stream_size2.hasNext() ) {
                    adaptor.addChild(root_2, stream_size2.nextTree());

                }
                stream_size2.reset();

                adaptor.addChild(root_1, root_2);
                }
                if ( !(stream_names.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_names.hasNext() ) {
                    adaptor.addChild(root_1, stream_names.nextNode());

                }
                stream_names.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type_name"

    public static class signed_number_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "signed_number"
    // sqljet/src/Sql.g:253:1: signed_number : ( PLUS | MINUS )? ( INTEGER | FLOAT ) ;
    public final SqlParser.signed_number_return signed_number() throws RecognitionException {
        SqlParser.signed_number_return retval = new SqlParser.signed_number_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set148=null;
        Token set149=null;

        Object set148_tree=null;
        Object set149_tree=null;

        try {
            // sqljet/src/Sql.g:253:14: ( ( PLUS | MINUS )? ( INTEGER | FLOAT ) )
            // sqljet/src/Sql.g:253:16: ( PLUS | MINUS )? ( INTEGER | FLOAT )
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:253:16: ( PLUS | MINUS )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( ((LA45_0>=PLUS && LA45_0<=MINUS)) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // sqljet/src/Sql.g:
                    {
                    set148=(Token)input.LT(1);
                    if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set148));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            set149=(Token)input.LT(1);
            if ( (input.LA(1)>=INTEGER && input.LA(1)<=FLOAT) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set149));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "signed_number"

    public static class pragma_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pragma_stmt"
    // sqljet/src/Sql.g:256:1: pragma_stmt : PRAGMA (database_name= id DOT )? pragma_name= id ( EQUALS pragma_value | LPAREN pragma_value RPAREN )? -> ^( PRAGMA ^( $pragma_name ( $database_name)? ) ( pragma_value )? ) ;
    public final SqlParser.pragma_stmt_return pragma_stmt() throws RecognitionException {
        SqlParser.pragma_stmt_return retval = new SqlParser.pragma_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PRAGMA150=null;
        Token DOT151=null;
        Token EQUALS152=null;
        Token LPAREN154=null;
        Token RPAREN156=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return pragma_name = null;

        SqlParser.pragma_value_return pragma_value153 = null;

        SqlParser.pragma_value_return pragma_value155 = null;


        Object PRAGMA150_tree=null;
        Object DOT151_tree=null;
        Object EQUALS152_tree=null;
        Object LPAREN154_tree=null;
        Object RPAREN156_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_PRAGMA=new RewriteRuleTokenStream(adaptor,"token PRAGMA");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_pragma_value=new RewriteRuleSubtreeStream(adaptor,"rule pragma_value");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:256:12: ( PRAGMA (database_name= id DOT )? pragma_name= id ( EQUALS pragma_value | LPAREN pragma_value RPAREN )? -> ^( PRAGMA ^( $pragma_name ( $database_name)? ) ( pragma_value )? ) )
            // sqljet/src/Sql.g:256:14: PRAGMA (database_name= id DOT )? pragma_name= id ( EQUALS pragma_value | LPAREN pragma_value RPAREN )?
            {
            PRAGMA150=(Token)match(input,PRAGMA,FOLLOW_PRAGMA_in_pragma_stmt1488);  
            stream_PRAGMA.add(PRAGMA150);

            // sqljet/src/Sql.g:256:21: (database_name= id DOT )?
            int alt46=2;
            alt46 = dfa46.predict(input);
            switch (alt46) {
                case 1 :
                    // sqljet/src/Sql.g:256:22: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_pragma_stmt1493);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT151=(Token)match(input,DOT,FOLLOW_DOT_in_pragma_stmt1495);  
                    stream_DOT.add(DOT151);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_pragma_stmt1501);
            pragma_name=id();

            state._fsp--;

            stream_id.add(pragma_name.getTree());
            // sqljet/src/Sql.g:256:60: ( EQUALS pragma_value | LPAREN pragma_value RPAREN )?
            int alt47=3;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==EQUALS) ) {
                alt47=1;
            }
            else if ( (LA47_0==LPAREN) ) {
                alt47=2;
            }
            switch (alt47) {
                case 1 :
                    // sqljet/src/Sql.g:256:61: EQUALS pragma_value
                    {
                    EQUALS152=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_pragma_stmt1504);  
                    stream_EQUALS.add(EQUALS152);

                    pushFollow(FOLLOW_pragma_value_in_pragma_stmt1506);
                    pragma_value153=pragma_value();

                    state._fsp--;

                    stream_pragma_value.add(pragma_value153.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:256:83: LPAREN pragma_value RPAREN
                    {
                    LPAREN154=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_pragma_stmt1510);  
                    stream_LPAREN.add(LPAREN154);

                    pushFollow(FOLLOW_pragma_value_in_pragma_stmt1512);
                    pragma_value155=pragma_value();

                    state._fsp--;

                    stream_pragma_value.add(pragma_value155.getTree());
                    RPAREN156=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_pragma_stmt1514);  
                    stream_RPAREN.add(RPAREN156);


                    }
                    break;

            }



            // AST REWRITE
            // elements: database_name, pragma_name, pragma_value, PRAGMA
            // token labels: 
            // rule labels: pragma_name, database_name, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_pragma_name=new RewriteRuleSubtreeStream(adaptor,"rule pragma_name",pragma_name!=null?pragma_name.tree:null);
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 257:1: -> ^( PRAGMA ^( $pragma_name ( $database_name)? ) ( pragma_value )? )
            {
                // sqljet/src/Sql.g:257:4: ^( PRAGMA ^( $pragma_name ( $database_name)? ) ( pragma_value )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_PRAGMA.nextNode(), root_1);

                // sqljet/src/Sql.g:257:13: ^( $pragma_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_pragma_name.nextNode(), root_2);

                // sqljet/src/Sql.g:257:28: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:257:45: ( pragma_value )?
                if ( stream_pragma_value.hasNext() ) {
                    adaptor.addChild(root_1, stream_pragma_value.nextTree());

                }
                stream_pragma_value.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pragma_stmt"

    public static class pragma_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pragma_value"
    // sqljet/src/Sql.g:259:1: pragma_value : ( signed_number -> ^( FLOAT_LITERAL signed_number ) | ID -> ^( ID_LITERAL ID ) | STRING -> ^( STRING_LITERAL STRING ) );
    public final SqlParser.pragma_value_return pragma_value() throws RecognitionException {
        SqlParser.pragma_value_return retval = new SqlParser.pragma_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID158=null;
        Token STRING159=null;
        SqlParser.signed_number_return signed_number157 = null;


        Object ID158_tree=null;
        Object STRING159_tree=null;
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_signed_number=new RewriteRuleSubtreeStream(adaptor,"rule signed_number");
        try {
            // sqljet/src/Sql.g:260:2: ( signed_number -> ^( FLOAT_LITERAL signed_number ) | ID -> ^( ID_LITERAL ID ) | STRING -> ^( STRING_LITERAL STRING ) )
            int alt48=3;
            switch ( input.LA(1) ) {
            case PLUS:
            case MINUS:
            case INTEGER:
            case FLOAT:
                {
                alt48=1;
                }
                break;
            case ID:
                {
                alt48=2;
                }
                break;
            case STRING:
                {
                alt48=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // sqljet/src/Sql.g:260:4: signed_number
                    {
                    pushFollow(FOLLOW_signed_number_in_pragma_value1543);
                    signed_number157=signed_number();

                    state._fsp--;

                    stream_signed_number.add(signed_number157.getTree());


                    // AST REWRITE
                    // elements: signed_number
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 260:18: -> ^( FLOAT_LITERAL signed_number )
                    {
                        // sqljet/src/Sql.g:260:21: ^( FLOAT_LITERAL signed_number )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FLOAT_LITERAL, "FLOAT_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_signed_number.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:261:4: ID
                    {
                    ID158=(Token)match(input,ID,FOLLOW_ID_in_pragma_value1556);  
                    stream_ID.add(ID158);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 261:7: -> ^( ID_LITERAL ID )
                    {
                        // sqljet/src/Sql.g:261:10: ^( ID_LITERAL ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID_LITERAL, "ID_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:262:4: STRING
                    {
                    STRING159=(Token)match(input,STRING,FOLLOW_STRING_in_pragma_value1569);  
                    stream_STRING.add(STRING159);



                    // AST REWRITE
                    // elements: STRING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 262:11: -> ^( STRING_LITERAL STRING )
                    {
                        // sqljet/src/Sql.g:262:14: ^( STRING_LITERAL STRING )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING_LITERAL, "STRING_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_STRING.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pragma_value"

    public static class attach_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attach_stmt"
    // sqljet/src/Sql.g:266:1: attach_stmt : ATTACH ( DATABASE )? filename= id AS database_name= id ;
    public final SqlParser.attach_stmt_return attach_stmt() throws RecognitionException {
        SqlParser.attach_stmt_return retval = new SqlParser.attach_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ATTACH160=null;
        Token DATABASE161=null;
        Token AS162=null;
        SqlParser.id_return filename = null;

        SqlParser.id_return database_name = null;


        Object ATTACH160_tree=null;
        Object DATABASE161_tree=null;
        Object AS162_tree=null;

        try {
            // sqljet/src/Sql.g:266:12: ( ATTACH ( DATABASE )? filename= id AS database_name= id )
            // sqljet/src/Sql.g:266:14: ATTACH ( DATABASE )? filename= id AS database_name= id
            {
            root_0 = (Object)adaptor.nil();

            ATTACH160=(Token)match(input,ATTACH,FOLLOW_ATTACH_in_attach_stmt1587); 
            ATTACH160_tree = (Object)adaptor.create(ATTACH160);
            adaptor.addChild(root_0, ATTACH160_tree);

            // sqljet/src/Sql.g:266:21: ( DATABASE )?
            int alt49=2;
            alt49 = dfa49.predict(input);
            switch (alt49) {
                case 1 :
                    // sqljet/src/Sql.g:266:22: DATABASE
                    {
                    DATABASE161=(Token)match(input,DATABASE,FOLLOW_DATABASE_in_attach_stmt1590); 
                    DATABASE161_tree = (Object)adaptor.create(DATABASE161);
                    adaptor.addChild(root_0, DATABASE161_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_attach_stmt1596);
            filename=id();

            state._fsp--;

            adaptor.addChild(root_0, filename.getTree());
            AS162=(Token)match(input,AS,FOLLOW_AS_in_attach_stmt1598); 
            AS162_tree = (Object)adaptor.create(AS162);
            adaptor.addChild(root_0, AS162_tree);

            pushFollow(FOLLOW_id_in_attach_stmt1602);
            database_name=id();

            state._fsp--;

            adaptor.addChild(root_0, database_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attach_stmt"

    public static class detach_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "detach_stmt"
    // sqljet/src/Sql.g:269:1: detach_stmt : DETACH ( DATABASE )? database_name= id ;
    public final SqlParser.detach_stmt_return detach_stmt() throws RecognitionException {
        SqlParser.detach_stmt_return retval = new SqlParser.detach_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DETACH163=null;
        Token DATABASE164=null;
        SqlParser.id_return database_name = null;


        Object DETACH163_tree=null;
        Object DATABASE164_tree=null;

        try {
            // sqljet/src/Sql.g:269:12: ( DETACH ( DATABASE )? database_name= id )
            // sqljet/src/Sql.g:269:14: DETACH ( DATABASE )? database_name= id
            {
            root_0 = (Object)adaptor.nil();

            DETACH163=(Token)match(input,DETACH,FOLLOW_DETACH_in_detach_stmt1610); 
            DETACH163_tree = (Object)adaptor.create(DETACH163);
            adaptor.addChild(root_0, DETACH163_tree);

            // sqljet/src/Sql.g:269:21: ( DATABASE )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==DATABASE) ) {
                int LA50_1 = input.LA(2);

                if ( ((LA50_1>=EXPLAIN && LA50_1<=PLAN)||(LA50_1>=INDEXED && LA50_1<=BY)||(LA50_1>=OR && LA50_1<=ESCAPE)||(LA50_1>=IS && LA50_1<=BETWEEN)||(LA50_1>=COLLATE && LA50_1<=THEN)||LA50_1==STRING||(LA50_1>=CURRENT_TIME && LA50_1<=CURRENT_TIMESTAMP)||(LA50_1>=RAISE && LA50_1<=ROW)) ) {
                    alt50=1;
                }
            }
            switch (alt50) {
                case 1 :
                    // sqljet/src/Sql.g:269:22: DATABASE
                    {
                    DATABASE164=(Token)match(input,DATABASE,FOLLOW_DATABASE_in_detach_stmt1613); 
                    DATABASE164_tree = (Object)adaptor.create(DATABASE164);
                    adaptor.addChild(root_0, DATABASE164_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_detach_stmt1619);
            database_name=id();

            state._fsp--;

            adaptor.addChild(root_0, database_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "detach_stmt"

    public static class analyze_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "analyze_stmt"
    // sqljet/src/Sql.g:272:1: analyze_stmt : ANALYZE (database_or_table_name= id | database_name= id DOT table_name= id )? ;
    public final SqlParser.analyze_stmt_return analyze_stmt() throws RecognitionException {
        SqlParser.analyze_stmt_return retval = new SqlParser.analyze_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ANALYZE165=null;
        Token DOT166=null;
        SqlParser.id_return database_or_table_name = null;

        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;


        Object ANALYZE165_tree=null;
        Object DOT166_tree=null;

        try {
            // sqljet/src/Sql.g:272:13: ( ANALYZE (database_or_table_name= id | database_name= id DOT table_name= id )? )
            // sqljet/src/Sql.g:272:15: ANALYZE (database_or_table_name= id | database_name= id DOT table_name= id )?
            {
            root_0 = (Object)adaptor.nil();

            ANALYZE165=(Token)match(input,ANALYZE,FOLLOW_ANALYZE_in_analyze_stmt1627); 
            ANALYZE165_tree = (Object)adaptor.create(ANALYZE165);
            adaptor.addChild(root_0, ANALYZE165_tree);

            // sqljet/src/Sql.g:272:23: (database_or_table_name= id | database_name= id DOT table_name= id )?
            int alt51=3;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // sqljet/src/Sql.g:272:24: database_or_table_name= id
                    {
                    pushFollow(FOLLOW_id_in_analyze_stmt1632);
                    database_or_table_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_or_table_name.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:272:52: database_name= id DOT table_name= id
                    {
                    pushFollow(FOLLOW_id_in_analyze_stmt1638);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT166=(Token)match(input,DOT,FOLLOW_DOT_in_analyze_stmt1640); 
                    DOT166_tree = (Object)adaptor.create(DOT166);
                    adaptor.addChild(root_0, DOT166_tree);

                    pushFollow(FOLLOW_id_in_analyze_stmt1644);
                    table_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, table_name.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "analyze_stmt"

    public static class reindex_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "reindex_stmt"
    // sqljet/src/Sql.g:275:1: reindex_stmt : REINDEX (database_name= id DOT )? collation_or_table_or_index_name= id ;
    public final SqlParser.reindex_stmt_return reindex_stmt() throws RecognitionException {
        SqlParser.reindex_stmt_return retval = new SqlParser.reindex_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token REINDEX167=null;
        Token DOT168=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return collation_or_table_or_index_name = null;


        Object REINDEX167_tree=null;
        Object DOT168_tree=null;

        try {
            // sqljet/src/Sql.g:275:13: ( REINDEX (database_name= id DOT )? collation_or_table_or_index_name= id )
            // sqljet/src/Sql.g:275:15: REINDEX (database_name= id DOT )? collation_or_table_or_index_name= id
            {
            root_0 = (Object)adaptor.nil();

            REINDEX167=(Token)match(input,REINDEX,FOLLOW_REINDEX_in_reindex_stmt1654); 
            REINDEX167_tree = (Object)adaptor.create(REINDEX167);
            adaptor.addChild(root_0, REINDEX167_tree);

            // sqljet/src/Sql.g:275:23: (database_name= id DOT )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==ID||LA52_0==STRING) ) {
                int LA52_1 = input.LA(2);

                if ( (LA52_1==DOT) ) {
                    alt52=1;
                }
            }
            else if ( ((LA52_0>=EXPLAIN && LA52_0<=PLAN)||(LA52_0>=INDEXED && LA52_0<=BY)||(LA52_0>=OR && LA52_0<=ESCAPE)||(LA52_0>=IS && LA52_0<=BETWEEN)||LA52_0==COLLATE||(LA52_0>=DISTINCT && LA52_0<=THEN)||(LA52_0>=CURRENT_TIME && LA52_0<=CURRENT_TIMESTAMP)||(LA52_0>=RAISE && LA52_0<=ROW)) ) {
                int LA52_2 = input.LA(2);

                if ( (LA52_2==DOT) ) {
                    alt52=1;
                }
            }
            switch (alt52) {
                case 1 :
                    // sqljet/src/Sql.g:275:24: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_reindex_stmt1659);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT168=(Token)match(input,DOT,FOLLOW_DOT_in_reindex_stmt1661); 
                    DOT168_tree = (Object)adaptor.create(DOT168);
                    adaptor.addChild(root_0, DOT168_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_reindex_stmt1667);
            collation_or_table_or_index_name=id();

            state._fsp--;

            adaptor.addChild(root_0, collation_or_table_or_index_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "reindex_stmt"

    public static class vacuum_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "vacuum_stmt"
    // sqljet/src/Sql.g:278:1: vacuum_stmt : VACUUM ;
    public final SqlParser.vacuum_stmt_return vacuum_stmt() throws RecognitionException {
        SqlParser.vacuum_stmt_return retval = new SqlParser.vacuum_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token VACUUM169=null;

        Object VACUUM169_tree=null;

        try {
            // sqljet/src/Sql.g:278:12: ( VACUUM )
            // sqljet/src/Sql.g:278:14: VACUUM
            {
            root_0 = (Object)adaptor.nil();

            VACUUM169=(Token)match(input,VACUUM,FOLLOW_VACUUM_in_vacuum_stmt1675); 
            VACUUM169_tree = (Object)adaptor.create(VACUUM169);
            adaptor.addChild(root_0, VACUUM169_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "vacuum_stmt"

    public static class operation_conflict_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operation_conflict_clause"
    // sqljet/src/Sql.g:284:1: operation_conflict_clause : OR ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE ) ;
    public final SqlParser.operation_conflict_clause_return operation_conflict_clause() throws RecognitionException {
        SqlParser.operation_conflict_clause_return retval = new SqlParser.operation_conflict_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR170=null;
        Token set171=null;

        Object OR170_tree=null;
        Object set171_tree=null;

        try {
            // sqljet/src/Sql.g:284:26: ( OR ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE ) )
            // sqljet/src/Sql.g:284:28: OR ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE )
            {
            root_0 = (Object)adaptor.nil();

            OR170=(Token)match(input,OR,FOLLOW_OR_in_operation_conflict_clause1686); 
            OR170_tree = (Object)adaptor.create(OR170);
            adaptor.addChild(root_0, OR170_tree);

            set171=(Token)input.LT(1);
            if ( (input.LA(1)>=IGNORE && input.LA(1)<=FAIL)||input.LA(1)==REPLACE ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set171));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operation_conflict_clause"

    public static class ordering_term_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ordering_term"
    // sqljet/src/Sql.g:286:1: ordering_term : expr ( ASC | DESC )? -> ^( ORDERING expr ( ASC )? ( DESC )? ) ;
    public final SqlParser.ordering_term_return ordering_term() throws RecognitionException {
        SqlParser.ordering_term_return retval = new SqlParser.ordering_term_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASC173=null;
        Token DESC174=null;
        SqlParser.expr_return expr172 = null;


        Object ASC173_tree=null;
        Object DESC174_tree=null;
        RewriteRuleTokenStream stream_DESC=new RewriteRuleTokenStream(adaptor,"token DESC");
        RewriteRuleTokenStream stream_ASC=new RewriteRuleTokenStream(adaptor,"token ASC");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // sqljet/src/Sql.g:286:14: ( expr ( ASC | DESC )? -> ^( ORDERING expr ( ASC )? ( DESC )? ) )
            // sqljet/src/Sql.g:286:16: expr ( ASC | DESC )?
            {
            pushFollow(FOLLOW_expr_in_ordering_term1713);
            expr172=expr();

            state._fsp--;

            stream_expr.add(expr172.getTree());
            // sqljet/src/Sql.g:286:82: ( ASC | DESC )?
            int alt53=3;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // sqljet/src/Sql.g:286:83: ASC
                    {
                    ASC173=(Token)match(input,ASC,FOLLOW_ASC_in_ordering_term1718);  
                    stream_ASC.add(ASC173);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:286:89: DESC
                    {
                    DESC174=(Token)match(input,DESC,FOLLOW_DESC_in_ordering_term1722);  
                    stream_DESC.add(DESC174);


                    }
                    break;

            }



            // AST REWRITE
            // elements: expr, ASC, DESC
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 287:1: -> ^( ORDERING expr ( ASC )? ( DESC )? )
            {
                // sqljet/src/Sql.g:287:4: ^( ORDERING expr ( ASC )? ( DESC )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ORDERING, "ORDERING"), root_1);

                adaptor.addChild(root_1, stream_expr.nextTree());
                // sqljet/src/Sql.g:287:20: ( ASC )?
                if ( stream_ASC.hasNext() ) {
                    adaptor.addChild(root_1, stream_ASC.nextNode());

                }
                stream_ASC.reset();
                // sqljet/src/Sql.g:287:27: ( DESC )?
                if ( stream_DESC.hasNext() ) {
                    adaptor.addChild(root_1, stream_DESC.nextNode());

                }
                stream_DESC.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ordering_term"

    public static class operation_limited_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operation_limited_clause"
    // sqljet/src/Sql.g:289:1: operation_limited_clause : ( ORDER BY ordering_term ( COMMA ordering_term )* )? LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? ;
    public final SqlParser.operation_limited_clause_return operation_limited_clause() throws RecognitionException {
        SqlParser.operation_limited_clause_return retval = new SqlParser.operation_limited_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token limit=null;
        Token offset=null;
        Token ORDER175=null;
        Token BY176=null;
        Token COMMA178=null;
        Token LIMIT180=null;
        Token set181=null;
        SqlParser.ordering_term_return ordering_term177 = null;

        SqlParser.ordering_term_return ordering_term179 = null;


        Object limit_tree=null;
        Object offset_tree=null;
        Object ORDER175_tree=null;
        Object BY176_tree=null;
        Object COMMA178_tree=null;
        Object LIMIT180_tree=null;
        Object set181_tree=null;

        try {
            // sqljet/src/Sql.g:289:25: ( ( ORDER BY ordering_term ( COMMA ordering_term )* )? LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? )
            // sqljet/src/Sql.g:290:3: ( ORDER BY ordering_term ( COMMA ordering_term )* )? LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )?
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:290:3: ( ORDER BY ordering_term ( COMMA ordering_term )* )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==ORDER) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // sqljet/src/Sql.g:290:4: ORDER BY ordering_term ( COMMA ordering_term )*
                    {
                    ORDER175=(Token)match(input,ORDER,FOLLOW_ORDER_in_operation_limited_clause1752); 
                    ORDER175_tree = (Object)adaptor.create(ORDER175);
                    adaptor.addChild(root_0, ORDER175_tree);

                    BY176=(Token)match(input,BY,FOLLOW_BY_in_operation_limited_clause1754); 
                    BY176_tree = (Object)adaptor.create(BY176);
                    adaptor.addChild(root_0, BY176_tree);

                    pushFollow(FOLLOW_ordering_term_in_operation_limited_clause1756);
                    ordering_term177=ordering_term();

                    state._fsp--;

                    adaptor.addChild(root_0, ordering_term177.getTree());
                    // sqljet/src/Sql.g:290:27: ( COMMA ordering_term )*
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( (LA54_0==COMMA) ) {
                            alt54=1;
                        }


                        switch (alt54) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:290:28: COMMA ordering_term
                    	    {
                    	    COMMA178=(Token)match(input,COMMA,FOLLOW_COMMA_in_operation_limited_clause1759); 
                    	    COMMA178_tree = (Object)adaptor.create(COMMA178);
                    	    adaptor.addChild(root_0, COMMA178_tree);

                    	    pushFollow(FOLLOW_ordering_term_in_operation_limited_clause1761);
                    	    ordering_term179=ordering_term();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, ordering_term179.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop54;
                        }
                    } while (true);


                    }
                    break;

            }

            LIMIT180=(Token)match(input,LIMIT,FOLLOW_LIMIT_in_operation_limited_clause1769); 
            LIMIT180_tree = (Object)adaptor.create(LIMIT180);
            adaptor.addChild(root_0, LIMIT180_tree);

            limit=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_operation_limited_clause1773); 
            limit_tree = (Object)adaptor.create(limit);
            adaptor.addChild(root_0, limit_tree);

            // sqljet/src/Sql.g:291:23: ( ( OFFSET | COMMA ) offset= INTEGER )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==COMMA||LA56_0==OFFSET) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // sqljet/src/Sql.g:291:24: ( OFFSET | COMMA ) offset= INTEGER
                    {
                    set181=(Token)input.LT(1);
                    if ( input.LA(1)==COMMA||input.LA(1)==OFFSET ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set181));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    offset=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_operation_limited_clause1786); 
                    offset_tree = (Object)adaptor.create(offset);
                    adaptor.addChild(root_0, offset_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operation_limited_clause"

    public static class select_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "select_stmt"
    // sqljet/src/Sql.g:294:1: select_stmt : select_list ( ORDER BY ordering_term ( COMMA ordering_term )* )? ( LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? )? -> ^( SELECT select_list ( ^( ORDER ( ordering_term )+ ) )? ( ^( LIMIT $limit ( $offset)? ) )? ) ;
    public final SqlParser.select_stmt_return select_stmt() throws RecognitionException {
        SqlParser.select_stmt_return retval = new SqlParser.select_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token limit=null;
        Token offset=null;
        Token ORDER183=null;
        Token BY184=null;
        Token COMMA186=null;
        Token LIMIT188=null;
        Token OFFSET189=null;
        Token COMMA190=null;
        SqlParser.select_list_return select_list182 = null;

        SqlParser.ordering_term_return ordering_term185 = null;

        SqlParser.ordering_term_return ordering_term187 = null;


        Object limit_tree=null;
        Object offset_tree=null;
        Object ORDER183_tree=null;
        Object BY184_tree=null;
        Object COMMA186_tree=null;
        Object LIMIT188_tree=null;
        Object OFFSET189_tree=null;
        Object COMMA190_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_ORDER=new RewriteRuleTokenStream(adaptor,"token ORDER");
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
        RewriteRuleTokenStream stream_LIMIT=new RewriteRuleTokenStream(adaptor,"token LIMIT");
        RewriteRuleTokenStream stream_OFFSET=new RewriteRuleTokenStream(adaptor,"token OFFSET");
        RewriteRuleSubtreeStream stream_ordering_term=new RewriteRuleSubtreeStream(adaptor,"rule ordering_term");
        RewriteRuleSubtreeStream stream_select_list=new RewriteRuleSubtreeStream(adaptor,"rule select_list");
        try {
            // sqljet/src/Sql.g:294:12: ( select_list ( ORDER BY ordering_term ( COMMA ordering_term )* )? ( LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? )? -> ^( SELECT select_list ( ^( ORDER ( ordering_term )+ ) )? ( ^( LIMIT $limit ( $offset)? ) )? ) )
            // sqljet/src/Sql.g:294:14: select_list ( ORDER BY ordering_term ( COMMA ordering_term )* )? ( LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? )?
            {
            pushFollow(FOLLOW_select_list_in_select_stmt1796);
            select_list182=select_list();

            state._fsp--;

            stream_select_list.add(select_list182.getTree());
            // sqljet/src/Sql.g:295:3: ( ORDER BY ordering_term ( COMMA ordering_term )* )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==ORDER) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // sqljet/src/Sql.g:295:4: ORDER BY ordering_term ( COMMA ordering_term )*
                    {
                    ORDER183=(Token)match(input,ORDER,FOLLOW_ORDER_in_select_stmt1801);  
                    stream_ORDER.add(ORDER183);

                    BY184=(Token)match(input,BY,FOLLOW_BY_in_select_stmt1803);  
                    stream_BY.add(BY184);

                    pushFollow(FOLLOW_ordering_term_in_select_stmt1805);
                    ordering_term185=ordering_term();

                    state._fsp--;

                    stream_ordering_term.add(ordering_term185.getTree());
                    // sqljet/src/Sql.g:295:27: ( COMMA ordering_term )*
                    loop57:
                    do {
                        int alt57=2;
                        int LA57_0 = input.LA(1);

                        if ( (LA57_0==COMMA) ) {
                            alt57=1;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:295:28: COMMA ordering_term
                    	    {
                    	    COMMA186=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt1808);  
                    	    stream_COMMA.add(COMMA186);

                    	    pushFollow(FOLLOW_ordering_term_in_select_stmt1810);
                    	    ordering_term187=ordering_term();

                    	    state._fsp--;

                    	    stream_ordering_term.add(ordering_term187.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop57;
                        }
                    } while (true);


                    }
                    break;

            }

            // sqljet/src/Sql.g:296:3: ( LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )? )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==LIMIT) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // sqljet/src/Sql.g:296:4: LIMIT limit= INTEGER ( ( OFFSET | COMMA ) offset= INTEGER )?
                    {
                    LIMIT188=(Token)match(input,LIMIT,FOLLOW_LIMIT_in_select_stmt1819);  
                    stream_LIMIT.add(LIMIT188);

                    limit=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt1823);  
                    stream_INTEGER.add(limit);

                    // sqljet/src/Sql.g:296:24: ( ( OFFSET | COMMA ) offset= INTEGER )?
                    int alt60=2;
                    int LA60_0 = input.LA(1);

                    if ( (LA60_0==COMMA||LA60_0==OFFSET) ) {
                        alt60=1;
                    }
                    switch (alt60) {
                        case 1 :
                            // sqljet/src/Sql.g:296:25: ( OFFSET | COMMA ) offset= INTEGER
                            {
                            // sqljet/src/Sql.g:296:25: ( OFFSET | COMMA )
                            int alt59=2;
                            int LA59_0 = input.LA(1);

                            if ( (LA59_0==OFFSET) ) {
                                alt59=1;
                            }
                            else if ( (LA59_0==COMMA) ) {
                                alt59=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 59, 0, input);

                                throw nvae;
                            }
                            switch (alt59) {
                                case 1 :
                                    // sqljet/src/Sql.g:296:26: OFFSET
                                    {
                                    OFFSET189=(Token)match(input,OFFSET,FOLLOW_OFFSET_in_select_stmt1827);  
                                    stream_OFFSET.add(OFFSET189);


                                    }
                                    break;
                                case 2 :
                                    // sqljet/src/Sql.g:296:35: COMMA
                                    {
                                    COMMA190=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt1831);  
                                    stream_COMMA.add(COMMA190);


                                    }
                                    break;

                            }

                            offset=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt1836);  
                            stream_INTEGER.add(offset);


                            }
                            break;

                    }


                    }
                    break;

            }



            // AST REWRITE
            // elements: ORDER, ordering_term, limit, select_list, offset, LIMIT
            // token labels: limit, offset
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_limit=new RewriteRuleTokenStream(adaptor,"token limit",limit);
            RewriteRuleTokenStream stream_offset=new RewriteRuleTokenStream(adaptor,"token offset",offset);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 297:1: -> ^( SELECT select_list ( ^( ORDER ( ordering_term )+ ) )? ( ^( LIMIT $limit ( $offset)? ) )? )
            {
                // sqljet/src/Sql.g:297:4: ^( SELECT select_list ( ^( ORDER ( ordering_term )+ ) )? ( ^( LIMIT $limit ( $offset)? ) )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_1);

                adaptor.addChild(root_1, stream_select_list.nextTree());
                // sqljet/src/Sql.g:298:22: ( ^( ORDER ( ordering_term )+ ) )?
                if ( stream_ORDER.hasNext()||stream_ordering_term.hasNext() ) {
                    // sqljet/src/Sql.g:298:22: ^( ORDER ( ordering_term )+ )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_ORDER.nextNode(), root_2);

                    if ( !(stream_ordering_term.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_ordering_term.hasNext() ) {
                        adaptor.addChild(root_2, stream_ordering_term.nextTree());

                    }
                    stream_ordering_term.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_ORDER.reset();
                stream_ordering_term.reset();
                // sqljet/src/Sql.g:298:47: ( ^( LIMIT $limit ( $offset)? ) )?
                if ( stream_limit.hasNext()||stream_offset.hasNext()||stream_LIMIT.hasNext() ) {
                    // sqljet/src/Sql.g:298:47: ^( LIMIT $limit ( $offset)? )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_LIMIT.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_limit.nextNode());
                    // sqljet/src/Sql.g:298:62: ( $offset)?
                    if ( stream_offset.hasNext() ) {
                        adaptor.addChild(root_2, stream_offset.nextNode());

                    }
                    stream_offset.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_limit.reset();
                stream_offset.reset();
                stream_LIMIT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "select_stmt"

    public static class select_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "select_list"
    // sqljet/src/Sql.g:301:1: select_list : select_core ( select_op select_core )* ;
    public final SqlParser.select_list_return select_list() throws RecognitionException {
        SqlParser.select_list_return retval = new SqlParser.select_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.select_core_return select_core191 = null;

        SqlParser.select_op_return select_op192 = null;

        SqlParser.select_core_return select_core193 = null;



        try {
            // sqljet/src/Sql.g:301:12: ( select_core ( select_op select_core )* )
            // sqljet/src/Sql.g:302:3: select_core ( select_op select_core )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_select_core_in_select_list1881);
            select_core191=select_core();

            state._fsp--;

            adaptor.addChild(root_0, select_core191.getTree());
            // sqljet/src/Sql.g:302:15: ( select_op select_core )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==UNION||(LA62_0>=INTERSECT && LA62_0<=EXCEPT)) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // sqljet/src/Sql.g:302:16: select_op select_core
            	    {
            	    pushFollow(FOLLOW_select_op_in_select_list1884);
            	    select_op192=select_op();

            	    state._fsp--;

            	    root_0 = (Object)adaptor.becomeRoot(select_op192.getTree(), root_0);
            	    pushFollow(FOLLOW_select_core_in_select_list1887);
            	    select_core193=select_core();

            	    state._fsp--;

            	    adaptor.addChild(root_0, select_core193.getTree());

            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "select_list"

    public static class select_op_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "select_op"
    // sqljet/src/Sql.g:304:1: select_op : ( UNION ( ALL )? | INTERSECT | EXCEPT );
    public final SqlParser.select_op_return select_op() throws RecognitionException {
        SqlParser.select_op_return retval = new SqlParser.select_op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token UNION194=null;
        Token ALL195=null;
        Token INTERSECT196=null;
        Token EXCEPT197=null;

        Object UNION194_tree=null;
        Object ALL195_tree=null;
        Object INTERSECT196_tree=null;
        Object EXCEPT197_tree=null;

        try {
            // sqljet/src/Sql.g:304:10: ( UNION ( ALL )? | INTERSECT | EXCEPT )
            int alt64=3;
            switch ( input.LA(1) ) {
            case UNION:
                {
                alt64=1;
                }
                break;
            case INTERSECT:
                {
                alt64=2;
                }
                break;
            case EXCEPT:
                {
                alt64=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // sqljet/src/Sql.g:304:12: UNION ( ALL )?
                    {
                    root_0 = (Object)adaptor.nil();

                    UNION194=(Token)match(input,UNION,FOLLOW_UNION_in_select_op1896); 
                    UNION194_tree = (Object)adaptor.create(UNION194);
                    root_0 = (Object)adaptor.becomeRoot(UNION194_tree, root_0);

                    // sqljet/src/Sql.g:304:19: ( ALL )?
                    int alt63=2;
                    int LA63_0 = input.LA(1);

                    if ( (LA63_0==ALL) ) {
                        alt63=1;
                    }
                    switch (alt63) {
                        case 1 :
                            // sqljet/src/Sql.g:304:20: ALL
                            {
                            ALL195=(Token)match(input,ALL,FOLLOW_ALL_in_select_op1900); 
                            ALL195_tree = (Object)adaptor.create(ALL195);
                            adaptor.addChild(root_0, ALL195_tree);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:304:28: INTERSECT
                    {
                    root_0 = (Object)adaptor.nil();

                    INTERSECT196=(Token)match(input,INTERSECT,FOLLOW_INTERSECT_in_select_op1906); 
                    INTERSECT196_tree = (Object)adaptor.create(INTERSECT196);
                    adaptor.addChild(root_0, INTERSECT196_tree);


                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:304:40: EXCEPT
                    {
                    root_0 = (Object)adaptor.nil();

                    EXCEPT197=(Token)match(input,EXCEPT,FOLLOW_EXCEPT_in_select_op1910); 
                    EXCEPT197_tree = (Object)adaptor.create(EXCEPT197);
                    adaptor.addChild(root_0, EXCEPT197_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "select_op"

    public static class select_core_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "select_core"
    // sqljet/src/Sql.g:306:1: select_core : SELECT ( ALL | DISTINCT )? result_column ( COMMA result_column )* ( FROM join_source )? ( WHERE where_expr= expr )? ( GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )? )? -> ^( SELECT_CORE ( DISTINCT )? ^( COLUMNS ( result_column )+ ) ( ^( FROM join_source ) )? ( ^( WHERE $where_expr) )? ( ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? ) )? ) ;
    public final SqlParser.select_core_return select_core() throws RecognitionException {
        SqlParser.select_core_return retval = new SqlParser.select_core_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SELECT198=null;
        Token ALL199=null;
        Token DISTINCT200=null;
        Token COMMA202=null;
        Token FROM204=null;
        Token WHERE206=null;
        Token GROUP207=null;
        Token BY208=null;
        Token COMMA210=null;
        Token HAVING212=null;
        SqlParser.expr_return where_expr = null;

        SqlParser.expr_return having_expr = null;

        SqlParser.result_column_return result_column201 = null;

        SqlParser.result_column_return result_column203 = null;

        SqlParser.join_source_return join_source205 = null;

        SqlParser.ordering_term_return ordering_term209 = null;

        SqlParser.ordering_term_return ordering_term211 = null;


        Object SELECT198_tree=null;
        Object ALL199_tree=null;
        Object DISTINCT200_tree=null;
        Object COMMA202_tree=null;
        Object FROM204_tree=null;
        Object WHERE206_tree=null;
        Object GROUP207_tree=null;
        Object BY208_tree=null;
        Object COMMA210_tree=null;
        Object HAVING212_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_DISTINCT=new RewriteRuleTokenStream(adaptor,"token DISTINCT");
        RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
        RewriteRuleTokenStream stream_HAVING=new RewriteRuleTokenStream(adaptor,"token HAVING");
        RewriteRuleTokenStream stream_ALL=new RewriteRuleTokenStream(adaptor,"token ALL");
        RewriteRuleTokenStream stream_SELECT=new RewriteRuleTokenStream(adaptor,"token SELECT");
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleTokenStream stream_FROM=new RewriteRuleTokenStream(adaptor,"token FROM");
        RewriteRuleTokenStream stream_WHERE=new RewriteRuleTokenStream(adaptor,"token WHERE");
        RewriteRuleSubtreeStream stream_result_column=new RewriteRuleSubtreeStream(adaptor,"rule result_column");
        RewriteRuleSubtreeStream stream_join_source=new RewriteRuleSubtreeStream(adaptor,"rule join_source");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_ordering_term=new RewriteRuleSubtreeStream(adaptor,"rule ordering_term");
        try {
            // sqljet/src/Sql.g:306:12: ( SELECT ( ALL | DISTINCT )? result_column ( COMMA result_column )* ( FROM join_source )? ( WHERE where_expr= expr )? ( GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )? )? -> ^( SELECT_CORE ( DISTINCT )? ^( COLUMNS ( result_column )+ ) ( ^( FROM join_source ) )? ( ^( WHERE $where_expr) )? ( ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? ) )? ) )
            // sqljet/src/Sql.g:307:3: SELECT ( ALL | DISTINCT )? result_column ( COMMA result_column )* ( FROM join_source )? ( WHERE where_expr= expr )? ( GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )? )?
            {
            SELECT198=(Token)match(input,SELECT,FOLLOW_SELECT_in_select_core1919);  
            stream_SELECT.add(SELECT198);

            // sqljet/src/Sql.g:307:10: ( ALL | DISTINCT )?
            int alt65=3;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // sqljet/src/Sql.g:307:11: ALL
                    {
                    ALL199=(Token)match(input,ALL,FOLLOW_ALL_in_select_core1922);  
                    stream_ALL.add(ALL199);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:307:17: DISTINCT
                    {
                    DISTINCT200=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_select_core1926);  
                    stream_DISTINCT.add(DISTINCT200);


                    }
                    break;

            }

            pushFollow(FOLLOW_result_column_in_select_core1930);
            result_column201=result_column();

            state._fsp--;

            stream_result_column.add(result_column201.getTree());
            // sqljet/src/Sql.g:307:42: ( COMMA result_column )*
            loop66:
            do {
                int alt66=2;
                alt66 = dfa66.predict(input);
                switch (alt66) {
            	case 1 :
            	    // sqljet/src/Sql.g:307:43: COMMA result_column
            	    {
            	    COMMA202=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_core1933);  
            	    stream_COMMA.add(COMMA202);

            	    pushFollow(FOLLOW_result_column_in_select_core1935);
            	    result_column203=result_column();

            	    state._fsp--;

            	    stream_result_column.add(result_column203.getTree());

            	    }
            	    break;

            	default :
            	    break loop66;
                }
            } while (true);

            // sqljet/src/Sql.g:307:65: ( FROM join_source )?
            int alt67=2;
            alt67 = dfa67.predict(input);
            switch (alt67) {
                case 1 :
                    // sqljet/src/Sql.g:307:66: FROM join_source
                    {
                    FROM204=(Token)match(input,FROM,FOLLOW_FROM_in_select_core1940);  
                    stream_FROM.add(FROM204);

                    pushFollow(FOLLOW_join_source_in_select_core1942);
                    join_source205=join_source();

                    state._fsp--;

                    stream_join_source.add(join_source205.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:307:85: ( WHERE where_expr= expr )?
            int alt68=2;
            alt68 = dfa68.predict(input);
            switch (alt68) {
                case 1 :
                    // sqljet/src/Sql.g:307:86: WHERE where_expr= expr
                    {
                    WHERE206=(Token)match(input,WHERE,FOLLOW_WHERE_in_select_core1947);  
                    stream_WHERE.add(WHERE206);

                    pushFollow(FOLLOW_expr_in_select_core1951);
                    where_expr=expr();

                    state._fsp--;

                    stream_expr.add(where_expr.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:308:3: ( GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )? )?
            int alt71=2;
            alt71 = dfa71.predict(input);
            switch (alt71) {
                case 1 :
                    // sqljet/src/Sql.g:308:5: GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )?
                    {
                    GROUP207=(Token)match(input,GROUP,FOLLOW_GROUP_in_select_core1959);  
                    stream_GROUP.add(GROUP207);

                    BY208=(Token)match(input,BY,FOLLOW_BY_in_select_core1961);  
                    stream_BY.add(BY208);

                    pushFollow(FOLLOW_ordering_term_in_select_core1963);
                    ordering_term209=ordering_term();

                    state._fsp--;

                    stream_ordering_term.add(ordering_term209.getTree());
                    // sqljet/src/Sql.g:308:28: ( COMMA ordering_term )*
                    loop69:
                    do {
                        int alt69=2;
                        alt69 = dfa69.predict(input);
                        switch (alt69) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:308:29: COMMA ordering_term
                    	    {
                    	    COMMA210=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_core1966);  
                    	    stream_COMMA.add(COMMA210);

                    	    pushFollow(FOLLOW_ordering_term_in_select_core1968);
                    	    ordering_term211=ordering_term();

                    	    state._fsp--;

                    	    stream_ordering_term.add(ordering_term211.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);

                    // sqljet/src/Sql.g:308:51: ( HAVING having_expr= expr )?
                    int alt70=2;
                    alt70 = dfa70.predict(input);
                    switch (alt70) {
                        case 1 :
                            // sqljet/src/Sql.g:308:52: HAVING having_expr= expr
                            {
                            HAVING212=(Token)match(input,HAVING,FOLLOW_HAVING_in_select_core1973);  
                            stream_HAVING.add(HAVING212);

                            pushFollow(FOLLOW_expr_in_select_core1977);
                            having_expr=expr();

                            state._fsp--;

                            stream_expr.add(having_expr.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }



            // AST REWRITE
            // elements: where_expr, result_column, having_expr, join_source, WHERE, GROUP, FROM, ordering_term, HAVING, DISTINCT
            // token labels: 
            // rule labels: where_expr, retval, having_expr
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_where_expr=new RewriteRuleSubtreeStream(adaptor,"rule where_expr",where_expr!=null?where_expr.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_having_expr=new RewriteRuleSubtreeStream(adaptor,"rule having_expr",having_expr!=null?having_expr.tree:null);

            root_0 = (Object)adaptor.nil();
            // 309:1: -> ^( SELECT_CORE ( DISTINCT )? ^( COLUMNS ( result_column )+ ) ( ^( FROM join_source ) )? ( ^( WHERE $where_expr) )? ( ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? ) )? )
            {
                // sqljet/src/Sql.g:309:4: ^( SELECT_CORE ( DISTINCT )? ^( COLUMNS ( result_column )+ ) ( ^( FROM join_source ) )? ( ^( WHERE $where_expr) )? ( ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? ) )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT_CORE, "SELECT_CORE"), root_1);

                // sqljet/src/Sql.g:310:15: ( DISTINCT )?
                if ( stream_DISTINCT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DISTINCT.nextNode());

                }
                stream_DISTINCT.reset();
                // sqljet/src/Sql.g:310:27: ^( COLUMNS ( result_column )+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                if ( !(stream_result_column.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_result_column.hasNext() ) {
                    adaptor.addChild(root_2, stream_result_column.nextTree());

                }
                stream_result_column.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:310:53: ( ^( FROM join_source ) )?
                if ( stream_join_source.hasNext()||stream_FROM.hasNext() ) {
                    // sqljet/src/Sql.g:310:53: ^( FROM join_source )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_FROM.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_join_source.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_join_source.reset();
                stream_FROM.reset();
                // sqljet/src/Sql.g:310:74: ( ^( WHERE $where_expr) )?
                if ( stream_where_expr.hasNext()||stream_WHERE.hasNext() ) {
                    // sqljet/src/Sql.g:310:74: ^( WHERE $where_expr)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_WHERE.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_where_expr.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_where_expr.reset();
                stream_WHERE.reset();
                // sqljet/src/Sql.g:311:3: ( ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? ) )?
                if ( stream_having_expr.hasNext()||stream_GROUP.hasNext()||stream_ordering_term.hasNext()||stream_HAVING.hasNext() ) {
                    // sqljet/src/Sql.g:311:3: ^( GROUP ( ordering_term )+ ( ^( HAVING $having_expr) )? )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_GROUP.nextNode(), root_2);

                    if ( !(stream_ordering_term.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_ordering_term.hasNext() ) {
                        adaptor.addChild(root_2, stream_ordering_term.nextTree());

                    }
                    stream_ordering_term.reset();
                    // sqljet/src/Sql.g:311:26: ( ^( HAVING $having_expr) )?
                    if ( stream_having_expr.hasNext()||stream_HAVING.hasNext() ) {
                        // sqljet/src/Sql.g:311:26: ^( HAVING $having_expr)
                        {
                        Object root_3 = (Object)adaptor.nil();
                        root_3 = (Object)adaptor.becomeRoot(stream_HAVING.nextNode(), root_3);

                        adaptor.addChild(root_3, stream_having_expr.nextTree());

                        adaptor.addChild(root_2, root_3);
                        }

                    }
                    stream_having_expr.reset();
                    stream_HAVING.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_having_expr.reset();
                stream_GROUP.reset();
                stream_ordering_term.reset();
                stream_HAVING.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "select_core"

    public static class result_column_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "result_column"
    // sqljet/src/Sql.g:314:1: result_column : ( ASTERISK | table_name= id DOT ASTERISK -> ^( ASTERISK $table_name) | expr ( ( AS )? column_alias= id )? -> ^( ALIAS expr ( $column_alias)? ) );
    public final SqlParser.result_column_return result_column() throws RecognitionException {
        SqlParser.result_column_return retval = new SqlParser.result_column_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASTERISK213=null;
        Token DOT214=null;
        Token ASTERISK215=null;
        Token AS217=null;
        SqlParser.id_return table_name = null;

        SqlParser.id_return column_alias = null;

        SqlParser.expr_return expr216 = null;


        Object ASTERISK213_tree=null;
        Object DOT214_tree=null;
        Object ASTERISK215_tree=null;
        Object AS217_tree=null;
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:315:3: ( ASTERISK | table_name= id DOT ASTERISK -> ^( ASTERISK $table_name) | expr ( ( AS )? column_alias= id )? -> ^( ALIAS expr ( $column_alias)? ) )
            int alt74=3;
            alt74 = dfa74.predict(input);
            switch (alt74) {
                case 1 :
                    // sqljet/src/Sql.g:315:5: ASTERISK
                    {
                    root_0 = (Object)adaptor.nil();

                    ASTERISK213=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_result_column2047); 
                    ASTERISK213_tree = (Object)adaptor.create(ASTERISK213);
                    adaptor.addChild(root_0, ASTERISK213_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:316:5: table_name= id DOT ASTERISK
                    {
                    pushFollow(FOLLOW_id_in_result_column2055);
                    table_name=id();

                    state._fsp--;

                    stream_id.add(table_name.getTree());
                    DOT214=(Token)match(input,DOT,FOLLOW_DOT_in_result_column2057);  
                    stream_DOT.add(DOT214);

                    ASTERISK215=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_result_column2059);  
                    stream_ASTERISK.add(ASTERISK215);



                    // AST REWRITE
                    // elements: table_name, ASTERISK
                    // token labels: 
                    // rule labels: retval, table_name
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 316:32: -> ^( ASTERISK $table_name)
                    {
                        // sqljet/src/Sql.g:316:35: ^( ASTERISK $table_name)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ASTERISK.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_table_name.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:317:5: expr ( ( AS )? column_alias= id )?
                    {
                    pushFollow(FOLLOW_expr_in_result_column2074);
                    expr216=expr();

                    state._fsp--;

                    stream_expr.add(expr216.getTree());
                    // sqljet/src/Sql.g:317:10: ( ( AS )? column_alias= id )?
                    int alt73=2;
                    alt73 = dfa73.predict(input);
                    switch (alt73) {
                        case 1 :
                            // sqljet/src/Sql.g:317:11: ( AS )? column_alias= id
                            {
                            // sqljet/src/Sql.g:317:11: ( AS )?
                            int alt72=2;
                            alt72 = dfa72.predict(input);
                            switch (alt72) {
                                case 1 :
                                    // sqljet/src/Sql.g:317:12: AS
                                    {
                                    AS217=(Token)match(input,AS,FOLLOW_AS_in_result_column2078);  
                                    stream_AS.add(AS217);


                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_id_in_result_column2084);
                            column_alias=id();

                            state._fsp--;

                            stream_id.add(column_alias.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: column_alias, expr
                    // token labels: 
                    // rule labels: retval, column_alias
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_column_alias=new RewriteRuleSubtreeStream(adaptor,"rule column_alias",column_alias!=null?column_alias.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 317:35: -> ^( ALIAS expr ( $column_alias)? )
                    {
                        // sqljet/src/Sql.g:317:38: ^( ALIAS expr ( $column_alias)? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALIAS, "ALIAS"), root_1);

                        adaptor.addChild(root_1, stream_expr.nextTree());
                        // sqljet/src/Sql.g:317:51: ( $column_alias)?
                        if ( stream_column_alias.hasNext() ) {
                            adaptor.addChild(root_1, stream_column_alias.nextTree());

                        }
                        stream_column_alias.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "result_column"

    public static class join_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "join_source"
    // sqljet/src/Sql.g:319:1: join_source : single_source ( join_op single_source ( join_constraint )? )* ;
    public final SqlParser.join_source_return join_source() throws RecognitionException {
        SqlParser.join_source_return retval = new SqlParser.join_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.single_source_return single_source218 = null;

        SqlParser.join_op_return join_op219 = null;

        SqlParser.single_source_return single_source220 = null;

        SqlParser.join_constraint_return join_constraint221 = null;



        try {
            // sqljet/src/Sql.g:319:12: ( single_source ( join_op single_source ( join_constraint )? )* )
            // sqljet/src/Sql.g:319:14: single_source ( join_op single_source ( join_constraint )? )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_single_source_in_join_source2105);
            single_source218=single_source();

            state._fsp--;

            adaptor.addChild(root_0, single_source218.getTree());
            // sqljet/src/Sql.g:319:28: ( join_op single_source ( join_constraint )? )*
            loop76:
            do {
                int alt76=2;
                alt76 = dfa76.predict(input);
                switch (alt76) {
            	case 1 :
            	    // sqljet/src/Sql.g:319:29: join_op single_source ( join_constraint )?
            	    {
            	    pushFollow(FOLLOW_join_op_in_join_source2108);
            	    join_op219=join_op();

            	    state._fsp--;

            	    root_0 = (Object)adaptor.becomeRoot(join_op219.getTree(), root_0);
            	    pushFollow(FOLLOW_single_source_in_join_source2111);
            	    single_source220=single_source();

            	    state._fsp--;

            	    adaptor.addChild(root_0, single_source220.getTree());
            	    // sqljet/src/Sql.g:319:52: ( join_constraint )?
            	    int alt75=2;
            	    alt75 = dfa75.predict(input);
            	    switch (alt75) {
            	        case 1 :
            	            // sqljet/src/Sql.g:319:53: join_constraint
            	            {
            	            pushFollow(FOLLOW_join_constraint_in_join_source2114);
            	            join_constraint221=join_constraint();

            	            state._fsp--;

            	            adaptor.addChild(root_0, join_constraint221.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "join_source"

    public static class single_source_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "single_source"
    // sqljet/src/Sql.g:321:1: single_source : ( (database_name= id DOT )? table_name= ID ( ( AS )? table_alias= ID )? ( INDEXED BY index_name= id | NOT INDEXED )? -> ^( ALIAS ^( $table_name ( $database_name)? ) ( $table_alias)? ( ^( INDEXED ( NOT )? ( $index_name)? ) )? ) | LPAREN select_stmt RPAREN ( ( AS )? table_alias= ID )? -> ^( ALIAS select_stmt ( $table_alias)? ) | LPAREN join_source RPAREN );
    public final SqlParser.single_source_return single_source() throws RecognitionException {
        SqlParser.single_source_return retval = new SqlParser.single_source_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token table_name=null;
        Token table_alias=null;
        Token DOT222=null;
        Token AS223=null;
        Token INDEXED224=null;
        Token BY225=null;
        Token NOT226=null;
        Token INDEXED227=null;
        Token LPAREN228=null;
        Token RPAREN230=null;
        Token AS231=null;
        Token LPAREN232=null;
        Token RPAREN234=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return index_name = null;

        SqlParser.select_stmt_return select_stmt229 = null;

        SqlParser.join_source_return join_source233 = null;


        Object table_name_tree=null;
        Object table_alias_tree=null;
        Object DOT222_tree=null;
        Object AS223_tree=null;
        Object INDEXED224_tree=null;
        Object BY225_tree=null;
        Object NOT226_tree=null;
        Object INDEXED227_tree=null;
        Object LPAREN228_tree=null;
        Object RPAREN230_tree=null;
        Object AS231_tree=null;
        Object LPAREN232_tree=null;
        Object RPAREN234_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_INDEXED=new RewriteRuleTokenStream(adaptor,"token INDEXED");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_select_stmt=new RewriteRuleSubtreeStream(adaptor,"rule select_stmt");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:322:3: ( (database_name= id DOT )? table_name= ID ( ( AS )? table_alias= ID )? ( INDEXED BY index_name= id | NOT INDEXED )? -> ^( ALIAS ^( $table_name ( $database_name)? ) ( $table_alias)? ( ^( INDEXED ( NOT )? ( $index_name)? ) )? ) | LPAREN select_stmt RPAREN ( ( AS )? table_alias= ID )? -> ^( ALIAS select_stmt ( $table_alias)? ) | LPAREN join_source RPAREN )
            int alt83=3;
            alt83 = dfa83.predict(input);
            switch (alt83) {
                case 1 :
                    // sqljet/src/Sql.g:322:5: (database_name= id DOT )? table_name= ID ( ( AS )? table_alias= ID )? ( INDEXED BY index_name= id | NOT INDEXED )?
                    {
                    // sqljet/src/Sql.g:322:5: (database_name= id DOT )?
                    int alt77=2;
                    alt77 = dfa77.predict(input);
                    switch (alt77) {
                        case 1 :
                            // sqljet/src/Sql.g:322:6: database_name= id DOT
                            {
                            pushFollow(FOLLOW_id_in_single_source2131);
                            database_name=id();

                            state._fsp--;

                            stream_id.add(database_name.getTree());
                            DOT222=(Token)match(input,DOT,FOLLOW_DOT_in_single_source2133);  
                            stream_DOT.add(DOT222);


                            }
                            break;

                    }

                    table_name=(Token)match(input,ID,FOLLOW_ID_in_single_source2139);  
                    stream_ID.add(table_name);

                    // sqljet/src/Sql.g:322:43: ( ( AS )? table_alias= ID )?
                    int alt79=2;
                    alt79 = dfa79.predict(input);
                    switch (alt79) {
                        case 1 :
                            // sqljet/src/Sql.g:322:44: ( AS )? table_alias= ID
                            {
                            // sqljet/src/Sql.g:322:44: ( AS )?
                            int alt78=2;
                            int LA78_0 = input.LA(1);

                            if ( (LA78_0==AS) ) {
                                alt78=1;
                            }
                            switch (alt78) {
                                case 1 :
                                    // sqljet/src/Sql.g:322:45: AS
                                    {
                                    AS223=(Token)match(input,AS,FOLLOW_AS_in_single_source2143);  
                                    stream_AS.add(AS223);


                                    }
                                    break;

                            }

                            table_alias=(Token)match(input,ID,FOLLOW_ID_in_single_source2149);  
                            stream_ID.add(table_alias);


                            }
                            break;

                    }

                    // sqljet/src/Sql.g:322:67: ( INDEXED BY index_name= id | NOT INDEXED )?
                    int alt80=3;
                    alt80 = dfa80.predict(input);
                    switch (alt80) {
                        case 1 :
                            // sqljet/src/Sql.g:322:68: INDEXED BY index_name= id
                            {
                            INDEXED224=(Token)match(input,INDEXED,FOLLOW_INDEXED_in_single_source2154);  
                            stream_INDEXED.add(INDEXED224);

                            BY225=(Token)match(input,BY,FOLLOW_BY_in_single_source2156);  
                            stream_BY.add(BY225);

                            pushFollow(FOLLOW_id_in_single_source2160);
                            index_name=id();

                            state._fsp--;

                            stream_id.add(index_name.getTree());

                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:322:95: NOT INDEXED
                            {
                            NOT226=(Token)match(input,NOT,FOLLOW_NOT_in_single_source2164);  
                            stream_NOT.add(NOT226);

                            INDEXED227=(Token)match(input,INDEXED,FOLLOW_INDEXED_in_single_source2166);  
                            stream_INDEXED.add(INDEXED227);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: index_name, table_alias, NOT, INDEXED, database_name, table_name
                    // token labels: table_alias, table_name
                    // rule labels: database_name, index_name, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_table_alias=new RewriteRuleTokenStream(adaptor,"token table_alias",table_alias);
                    RewriteRuleTokenStream stream_table_name=new RewriteRuleTokenStream(adaptor,"token table_name",table_name);
                    RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
                    RewriteRuleSubtreeStream stream_index_name=new RewriteRuleSubtreeStream(adaptor,"rule index_name",index_name!=null?index_name.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 323:3: -> ^( ALIAS ^( $table_name ( $database_name)? ) ( $table_alias)? ( ^( INDEXED ( NOT )? ( $index_name)? ) )? )
                    {
                        // sqljet/src/Sql.g:323:6: ^( ALIAS ^( $table_name ( $database_name)? ) ( $table_alias)? ( ^( INDEXED ( NOT )? ( $index_name)? ) )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALIAS, "ALIAS"), root_1);

                        // sqljet/src/Sql.g:323:14: ^( $table_name ( $database_name)? )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_2);

                        // sqljet/src/Sql.g:323:28: ( $database_name)?
                        if ( stream_database_name.hasNext() ) {
                            adaptor.addChild(root_2, stream_database_name.nextTree());

                        }
                        stream_database_name.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // sqljet/src/Sql.g:323:45: ( $table_alias)?
                        if ( stream_table_alias.hasNext() ) {
                            adaptor.addChild(root_1, stream_table_alias.nextNode());

                        }
                        stream_table_alias.reset();
                        // sqljet/src/Sql.g:323:59: ( ^( INDEXED ( NOT )? ( $index_name)? ) )?
                        if ( stream_index_name.hasNext()||stream_NOT.hasNext()||stream_INDEXED.hasNext() ) {
                            // sqljet/src/Sql.g:323:59: ^( INDEXED ( NOT )? ( $index_name)? )
                            {
                            Object root_2 = (Object)adaptor.nil();
                            root_2 = (Object)adaptor.becomeRoot(stream_INDEXED.nextNode(), root_2);

                            // sqljet/src/Sql.g:323:69: ( NOT )?
                            if ( stream_NOT.hasNext() ) {
                                adaptor.addChild(root_2, stream_NOT.nextNode());

                            }
                            stream_NOT.reset();
                            // sqljet/src/Sql.g:323:74: ( $index_name)?
                            if ( stream_index_name.hasNext() ) {
                                adaptor.addChild(root_2, stream_index_name.nextTree());

                            }
                            stream_index_name.reset();

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_index_name.reset();
                        stream_NOT.reset();
                        stream_INDEXED.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:324:5: LPAREN select_stmt RPAREN ( ( AS )? table_alias= ID )?
                    {
                    LPAREN228=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_single_source2207);  
                    stream_LPAREN.add(LPAREN228);

                    pushFollow(FOLLOW_select_stmt_in_single_source2209);
                    select_stmt229=select_stmt();

                    state._fsp--;

                    stream_select_stmt.add(select_stmt229.getTree());
                    RPAREN230=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_single_source2211);  
                    stream_RPAREN.add(RPAREN230);

                    // sqljet/src/Sql.g:324:31: ( ( AS )? table_alias= ID )?
                    int alt82=2;
                    alt82 = dfa82.predict(input);
                    switch (alt82) {
                        case 1 :
                            // sqljet/src/Sql.g:324:32: ( AS )? table_alias= ID
                            {
                            // sqljet/src/Sql.g:324:32: ( AS )?
                            int alt81=2;
                            int LA81_0 = input.LA(1);

                            if ( (LA81_0==AS) ) {
                                alt81=1;
                            }
                            switch (alt81) {
                                case 1 :
                                    // sqljet/src/Sql.g:324:33: AS
                                    {
                                    AS231=(Token)match(input,AS,FOLLOW_AS_in_single_source2215);  
                                    stream_AS.add(AS231);


                                    }
                                    break;

                            }

                            table_alias=(Token)match(input,ID,FOLLOW_ID_in_single_source2221);  
                            stream_ID.add(table_alias);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: table_alias, select_stmt
                    // token labels: table_alias
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_table_alias=new RewriteRuleTokenStream(adaptor,"token table_alias",table_alias);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 325:3: -> ^( ALIAS select_stmt ( $table_alias)? )
                    {
                        // sqljet/src/Sql.g:325:6: ^( ALIAS select_stmt ( $table_alias)? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALIAS, "ALIAS"), root_1);

                        adaptor.addChild(root_1, stream_select_stmt.nextTree());
                        // sqljet/src/Sql.g:325:26: ( $table_alias)?
                        if ( stream_table_alias.hasNext() ) {
                            adaptor.addChild(root_1, stream_table_alias.nextNode());

                        }
                        stream_table_alias.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:326:5: LPAREN join_source RPAREN
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAREN232=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_single_source2243); 
                    pushFollow(FOLLOW_join_source_in_single_source2246);
                    join_source233=join_source();

                    state._fsp--;

                    adaptor.addChild(root_0, join_source233.getTree());
                    RPAREN234=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_single_source2248); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "single_source"

    public static class join_op_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "join_op"
    // sqljet/src/Sql.g:328:1: join_op : ( COMMA | ( NATURAL )? ( ( LEFT )? ( OUTER )? | INNER | CROSS ) JOIN );
    public final SqlParser.join_op_return join_op() throws RecognitionException {
        SqlParser.join_op_return retval = new SqlParser.join_op_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA235=null;
        Token NATURAL236=null;
        Token LEFT237=null;
        Token OUTER238=null;
        Token INNER239=null;
        Token CROSS240=null;
        Token JOIN241=null;

        Object COMMA235_tree=null;
        Object NATURAL236_tree=null;
        Object LEFT237_tree=null;
        Object OUTER238_tree=null;
        Object INNER239_tree=null;
        Object CROSS240_tree=null;
        Object JOIN241_tree=null;

        try {
            // sqljet/src/Sql.g:329:3: ( COMMA | ( NATURAL )? ( ( LEFT )? ( OUTER )? | INNER | CROSS ) JOIN )
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==COMMA) ) {
                alt88=1;
            }
            else if ( ((LA88_0>=NATURAL && LA88_0<=JOIN)) ) {
                alt88=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }
            switch (alt88) {
                case 1 :
                    // sqljet/src/Sql.g:329:5: COMMA
                    {
                    root_0 = (Object)adaptor.nil();

                    COMMA235=(Token)match(input,COMMA,FOLLOW_COMMA_in_join_op2259); 
                    COMMA235_tree = (Object)adaptor.create(COMMA235);
                    adaptor.addChild(root_0, COMMA235_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:330:5: ( NATURAL )? ( ( LEFT )? ( OUTER )? | INNER | CROSS ) JOIN
                    {
                    root_0 = (Object)adaptor.nil();

                    // sqljet/src/Sql.g:330:5: ( NATURAL )?
                    int alt84=2;
                    int LA84_0 = input.LA(1);

                    if ( (LA84_0==NATURAL) ) {
                        alt84=1;
                    }
                    switch (alt84) {
                        case 1 :
                            // sqljet/src/Sql.g:330:6: NATURAL
                            {
                            NATURAL236=(Token)match(input,NATURAL,FOLLOW_NATURAL_in_join_op2266); 
                            NATURAL236_tree = (Object)adaptor.create(NATURAL236);
                            adaptor.addChild(root_0, NATURAL236_tree);


                            }
                            break;

                    }

                    // sqljet/src/Sql.g:330:16: ( ( LEFT )? ( OUTER )? | INNER | CROSS )
                    int alt87=3;
                    switch ( input.LA(1) ) {
                    case LEFT:
                    case OUTER:
                    case JOIN:
                        {
                        alt87=1;
                        }
                        break;
                    case INNER:
                        {
                        alt87=2;
                        }
                        break;
                    case CROSS:
                        {
                        alt87=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 87, 0, input);

                        throw nvae;
                    }

                    switch (alt87) {
                        case 1 :
                            // sqljet/src/Sql.g:330:17: ( LEFT )? ( OUTER )?
                            {
                            // sqljet/src/Sql.g:330:17: ( LEFT )?
                            int alt85=2;
                            int LA85_0 = input.LA(1);

                            if ( (LA85_0==LEFT) ) {
                                alt85=1;
                            }
                            switch (alt85) {
                                case 1 :
                                    // sqljet/src/Sql.g:330:18: LEFT
                                    {
                                    LEFT237=(Token)match(input,LEFT,FOLLOW_LEFT_in_join_op2272); 
                                    LEFT237_tree = (Object)adaptor.create(LEFT237);
                                    adaptor.addChild(root_0, LEFT237_tree);


                                    }
                                    break;

                            }

                            // sqljet/src/Sql.g:330:25: ( OUTER )?
                            int alt86=2;
                            int LA86_0 = input.LA(1);

                            if ( (LA86_0==OUTER) ) {
                                alt86=1;
                            }
                            switch (alt86) {
                                case 1 :
                                    // sqljet/src/Sql.g:330:26: OUTER
                                    {
                                    OUTER238=(Token)match(input,OUTER,FOLLOW_OUTER_in_join_op2277); 
                                    OUTER238_tree = (Object)adaptor.create(OUTER238);
                                    adaptor.addChild(root_0, OUTER238_tree);


                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:330:36: INNER
                            {
                            INNER239=(Token)match(input,INNER,FOLLOW_INNER_in_join_op2283); 
                            INNER239_tree = (Object)adaptor.create(INNER239);
                            adaptor.addChild(root_0, INNER239_tree);


                            }
                            break;
                        case 3 :
                            // sqljet/src/Sql.g:330:44: CROSS
                            {
                            CROSS240=(Token)match(input,CROSS,FOLLOW_CROSS_in_join_op2287); 
                            CROSS240_tree = (Object)adaptor.create(CROSS240);
                            adaptor.addChild(root_0, CROSS240_tree);


                            }
                            break;

                    }

                    JOIN241=(Token)match(input,JOIN,FOLLOW_JOIN_in_join_op2290); 
                    JOIN241_tree = (Object)adaptor.create(JOIN241);
                    root_0 = (Object)adaptor.becomeRoot(JOIN241_tree, root_0);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "join_op"

    public static class join_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "join_constraint"
    // sqljet/src/Sql.g:332:1: join_constraint : ( ON expr | USING LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN -> ^( USING ( $column_names)+ ) );
    public final SqlParser.join_constraint_return join_constraint() throws RecognitionException {
        SqlParser.join_constraint_return retval = new SqlParser.join_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ON242=null;
        Token USING244=null;
        Token LPAREN245=null;
        Token COMMA246=null;
        Token RPAREN247=null;
        List list_column_names=null;
        SqlParser.expr_return expr243 = null;

        SqlParser.id_return column_names = null;
         column_names = null;
        Object ON242_tree=null;
        Object USING244_tree=null;
        Object LPAREN245_tree=null;
        Object COMMA246_tree=null;
        Object RPAREN247_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_USING=new RewriteRuleTokenStream(adaptor,"token USING");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:333:3: ( ON expr | USING LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN -> ^( USING ( $column_names)+ ) )
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==ON) ) {
                alt90=1;
            }
            else if ( (LA90_0==USING) ) {
                alt90=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }
            switch (alt90) {
                case 1 :
                    // sqljet/src/Sql.g:333:5: ON expr
                    {
                    root_0 = (Object)adaptor.nil();

                    ON242=(Token)match(input,ON,FOLLOW_ON_in_join_constraint2301); 
                    ON242_tree = (Object)adaptor.create(ON242);
                    root_0 = (Object)adaptor.becomeRoot(ON242_tree, root_0);

                    pushFollow(FOLLOW_expr_in_join_constraint2304);
                    expr243=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr243.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:334:5: USING LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN
                    {
                    USING244=(Token)match(input,USING,FOLLOW_USING_in_join_constraint2310);  
                    stream_USING.add(USING244);

                    LPAREN245=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_join_constraint2312);  
                    stream_LPAREN.add(LPAREN245);

                    pushFollow(FOLLOW_id_in_join_constraint2316);
                    column_names=id();

                    state._fsp--;

                    stream_id.add(column_names.getTree());
                    if (list_column_names==null) list_column_names=new ArrayList();
                    list_column_names.add(column_names.getTree());

                    // sqljet/src/Sql.g:334:35: ( COMMA column_names+= id )*
                    loop89:
                    do {
                        int alt89=2;
                        int LA89_0 = input.LA(1);

                        if ( (LA89_0==COMMA) ) {
                            alt89=1;
                        }


                        switch (alt89) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:334:36: COMMA column_names+= id
                    	    {
                    	    COMMA246=(Token)match(input,COMMA,FOLLOW_COMMA_in_join_constraint2319);  
                    	    stream_COMMA.add(COMMA246);

                    	    pushFollow(FOLLOW_id_in_join_constraint2323);
                    	    column_names=id();

                    	    state._fsp--;

                    	    stream_id.add(column_names.getTree());
                    	    if (list_column_names==null) list_column_names=new ArrayList();
                    	    list_column_names.add(column_names.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop89;
                        }
                    } while (true);

                    RPAREN247=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_join_constraint2327);  
                    stream_RPAREN.add(RPAREN247);



                    // AST REWRITE
                    // elements: column_names, USING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: column_names
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_column_names=new RewriteRuleSubtreeStream(adaptor,"token column_names",list_column_names);
                    root_0 = (Object)adaptor.nil();
                    // 334:68: -> ^( USING ( $column_names)+ )
                    {
                        // sqljet/src/Sql.g:334:71: ^( USING ( $column_names)+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_USING.nextNode(), root_1);

                        if ( !(stream_column_names.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_column_names.hasNext() ) {
                            adaptor.addChild(root_1, stream_column_names.nextTree());

                        }
                        stream_column_names.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "join_constraint"

    public static class insert_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "insert_stmt"
    // sqljet/src/Sql.g:337:1: insert_stmt : ( INSERT ( operation_conflict_clause )? | REPLACE ) INTO (database_name= id DOT )? table_name= id ( ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt ) | DEFAULT VALUES ) ;
    public final SqlParser.insert_stmt_return insert_stmt() throws RecognitionException {
        SqlParser.insert_stmt_return retval = new SqlParser.insert_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INSERT248=null;
        Token REPLACE250=null;
        Token INTO251=null;
        Token DOT252=null;
        Token LPAREN253=null;
        Token COMMA254=null;
        Token RPAREN255=null;
        Token VALUES256=null;
        Token LPAREN257=null;
        Token COMMA258=null;
        Token RPAREN259=null;
        Token DEFAULT261=null;
        Token VALUES262=null;
        List list_column_names=null;
        List list_values=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.operation_conflict_clause_return operation_conflict_clause249 = null;

        SqlParser.select_stmt_return select_stmt260 = null;

        SqlParser.id_return column_names = null;
         column_names = null;
        SqlParser.expr_return values = null;
         values = null;
        Object INSERT248_tree=null;
        Object REPLACE250_tree=null;
        Object INTO251_tree=null;
        Object DOT252_tree=null;
        Object LPAREN253_tree=null;
        Object COMMA254_tree=null;
        Object RPAREN255_tree=null;
        Object VALUES256_tree=null;
        Object LPAREN257_tree=null;
        Object COMMA258_tree=null;
        Object RPAREN259_tree=null;
        Object DEFAULT261_tree=null;
        Object VALUES262_tree=null;

        try {
            // sqljet/src/Sql.g:337:12: ( ( INSERT ( operation_conflict_clause )? | REPLACE ) INTO (database_name= id DOT )? table_name= id ( ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt ) | DEFAULT VALUES ) )
            // sqljet/src/Sql.g:337:14: ( INSERT ( operation_conflict_clause )? | REPLACE ) INTO (database_name= id DOT )? table_name= id ( ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt ) | DEFAULT VALUES )
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:337:14: ( INSERT ( operation_conflict_clause )? | REPLACE )
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==INSERT) ) {
                alt92=1;
            }
            else if ( (LA92_0==REPLACE) ) {
                alt92=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // sqljet/src/Sql.g:337:15: INSERT ( operation_conflict_clause )?
                    {
                    INSERT248=(Token)match(input,INSERT,FOLLOW_INSERT_in_insert_stmt2346); 
                    INSERT248_tree = (Object)adaptor.create(INSERT248);
                    adaptor.addChild(root_0, INSERT248_tree);

                    // sqljet/src/Sql.g:337:22: ( operation_conflict_clause )?
                    int alt91=2;
                    int LA91_0 = input.LA(1);

                    if ( (LA91_0==OR) ) {
                        alt91=1;
                    }
                    switch (alt91) {
                        case 1 :
                            // sqljet/src/Sql.g:337:23: operation_conflict_clause
                            {
                            pushFollow(FOLLOW_operation_conflict_clause_in_insert_stmt2349);
                            operation_conflict_clause249=operation_conflict_clause();

                            state._fsp--;

                            adaptor.addChild(root_0, operation_conflict_clause249.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:337:53: REPLACE
                    {
                    REPLACE250=(Token)match(input,REPLACE,FOLLOW_REPLACE_in_insert_stmt2355); 
                    REPLACE250_tree = (Object)adaptor.create(REPLACE250);
                    adaptor.addChild(root_0, REPLACE250_tree);


                    }
                    break;

            }

            INTO251=(Token)match(input,INTO,FOLLOW_INTO_in_insert_stmt2358); 
            INTO251_tree = (Object)adaptor.create(INTO251);
            adaptor.addChild(root_0, INTO251_tree);

            // sqljet/src/Sql.g:337:67: (database_name= id DOT )?
            int alt93=2;
            alt93 = dfa93.predict(input);
            switch (alt93) {
                case 1 :
                    // sqljet/src/Sql.g:337:68: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_insert_stmt2363);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT252=(Token)match(input,DOT,FOLLOW_DOT_in_insert_stmt2365); 
                    DOT252_tree = (Object)adaptor.create(DOT252);
                    adaptor.addChild(root_0, DOT252_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_insert_stmt2371);
            table_name=id();

            state._fsp--;

            adaptor.addChild(root_0, table_name.getTree());
            // sqljet/src/Sql.g:338:3: ( ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt ) | DEFAULT VALUES )
            int alt98=2;
            int LA98_0 = input.LA(1);

            if ( (LA98_0==LPAREN||LA98_0==SELECT||LA98_0==VALUES) ) {
                alt98=1;
            }
            else if ( (LA98_0==DEFAULT) ) {
                alt98=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 98, 0, input);

                throw nvae;
            }
            switch (alt98) {
                case 1 :
                    // sqljet/src/Sql.g:338:5: ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt )
                    {
                    // sqljet/src/Sql.g:338:5: ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )?
                    int alt95=2;
                    int LA95_0 = input.LA(1);

                    if ( (LA95_0==LPAREN) ) {
                        alt95=1;
                    }
                    switch (alt95) {
                        case 1 :
                            // sqljet/src/Sql.g:338:6: LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN
                            {
                            LPAREN253=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_insert_stmt2378); 
                            LPAREN253_tree = (Object)adaptor.create(LPAREN253);
                            adaptor.addChild(root_0, LPAREN253_tree);

                            pushFollow(FOLLOW_id_in_insert_stmt2382);
                            column_names=id();

                            state._fsp--;

                            adaptor.addChild(root_0, column_names.getTree());
                            if (list_column_names==null) list_column_names=new ArrayList();
                            list_column_names.add(column_names.getTree());

                            // sqljet/src/Sql.g:338:30: ( COMMA column_names+= id )*
                            loop94:
                            do {
                                int alt94=2;
                                int LA94_0 = input.LA(1);

                                if ( (LA94_0==COMMA) ) {
                                    alt94=1;
                                }


                                switch (alt94) {
                            	case 1 :
                            	    // sqljet/src/Sql.g:338:31: COMMA column_names+= id
                            	    {
                            	    COMMA254=(Token)match(input,COMMA,FOLLOW_COMMA_in_insert_stmt2385); 
                            	    COMMA254_tree = (Object)adaptor.create(COMMA254);
                            	    adaptor.addChild(root_0, COMMA254_tree);

                            	    pushFollow(FOLLOW_id_in_insert_stmt2389);
                            	    column_names=id();

                            	    state._fsp--;

                            	    adaptor.addChild(root_0, column_names.getTree());
                            	    if (list_column_names==null) list_column_names=new ArrayList();
                            	    list_column_names.add(column_names.getTree());


                            	    }
                            	    break;

                            	default :
                            	    break loop94;
                                }
                            } while (true);

                            RPAREN255=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_insert_stmt2393); 
                            RPAREN255_tree = (Object)adaptor.create(RPAREN255);
                            adaptor.addChild(root_0, RPAREN255_tree);


                            }
                            break;

                    }

                    // sqljet/src/Sql.g:339:5: ( VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN | select_stmt )
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==VALUES) ) {
                        alt97=1;
                    }
                    else if ( (LA97_0==SELECT) ) {
                        alt97=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 97, 0, input);

                        throw nvae;
                    }
                    switch (alt97) {
                        case 1 :
                            // sqljet/src/Sql.g:339:6: VALUES LPAREN values+= expr ( COMMA values+= expr )* RPAREN
                            {
                            VALUES256=(Token)match(input,VALUES,FOLLOW_VALUES_in_insert_stmt2402); 
                            VALUES256_tree = (Object)adaptor.create(VALUES256);
                            adaptor.addChild(root_0, VALUES256_tree);

                            LPAREN257=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_insert_stmt2404); 
                            LPAREN257_tree = (Object)adaptor.create(LPAREN257);
                            adaptor.addChild(root_0, LPAREN257_tree);

                            pushFollow(FOLLOW_expr_in_insert_stmt2408);
                            values=expr();

                            state._fsp--;

                            adaptor.addChild(root_0, values.getTree());
                            if (list_values==null) list_values=new ArrayList();
                            list_values.add(values.getTree());

                            // sqljet/src/Sql.g:339:33: ( COMMA values+= expr )*
                            loop96:
                            do {
                                int alt96=2;
                                int LA96_0 = input.LA(1);

                                if ( (LA96_0==COMMA) ) {
                                    alt96=1;
                                }


                                switch (alt96) {
                            	case 1 :
                            	    // sqljet/src/Sql.g:339:34: COMMA values+= expr
                            	    {
                            	    COMMA258=(Token)match(input,COMMA,FOLLOW_COMMA_in_insert_stmt2411); 
                            	    COMMA258_tree = (Object)adaptor.create(COMMA258);
                            	    adaptor.addChild(root_0, COMMA258_tree);

                            	    pushFollow(FOLLOW_expr_in_insert_stmt2415);
                            	    values=expr();

                            	    state._fsp--;

                            	    adaptor.addChild(root_0, values.getTree());
                            	    if (list_values==null) list_values=new ArrayList();
                            	    list_values.add(values.getTree());


                            	    }
                            	    break;

                            	default :
                            	    break loop96;
                                }
                            } while (true);

                            RPAREN259=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_insert_stmt2419); 
                            RPAREN259_tree = (Object)adaptor.create(RPAREN259);
                            adaptor.addChild(root_0, RPAREN259_tree);


                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:339:64: select_stmt
                            {
                            pushFollow(FOLLOW_select_stmt_in_insert_stmt2423);
                            select_stmt260=select_stmt();

                            state._fsp--;

                            adaptor.addChild(root_0, select_stmt260.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:340:5: DEFAULT VALUES
                    {
                    DEFAULT261=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_insert_stmt2430); 
                    DEFAULT261_tree = (Object)adaptor.create(DEFAULT261);
                    adaptor.addChild(root_0, DEFAULT261_tree);

                    VALUES262=(Token)match(input,VALUES,FOLLOW_VALUES_in_insert_stmt2432); 
                    VALUES262_tree = (Object)adaptor.create(VALUES262);
                    adaptor.addChild(root_0, VALUES262_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "insert_stmt"

    public static class update_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "update_stmt"
    // sqljet/src/Sql.g:343:1: update_stmt : UPDATE ( operation_conflict_clause )? qualified_table_name SET values+= update_set ( COMMA values+= update_set )* ( WHERE expr )? ( operation_limited_clause )? ;
    public final SqlParser.update_stmt_return update_stmt() throws RecognitionException {
        SqlParser.update_stmt_return retval = new SqlParser.update_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token UPDATE263=null;
        Token SET266=null;
        Token COMMA267=null;
        Token WHERE268=null;
        List list_values=null;
        SqlParser.operation_conflict_clause_return operation_conflict_clause264 = null;

        SqlParser.qualified_table_name_return qualified_table_name265 = null;

        SqlParser.expr_return expr269 = null;

        SqlParser.operation_limited_clause_return operation_limited_clause270 = null;

        SqlParser.update_set_return values = null;
         values = null;
        Object UPDATE263_tree=null;
        Object SET266_tree=null;
        Object COMMA267_tree=null;
        Object WHERE268_tree=null;

        try {
            // sqljet/src/Sql.g:343:12: ( UPDATE ( operation_conflict_clause )? qualified_table_name SET values+= update_set ( COMMA values+= update_set )* ( WHERE expr )? ( operation_limited_clause )? )
            // sqljet/src/Sql.g:343:14: UPDATE ( operation_conflict_clause )? qualified_table_name SET values+= update_set ( COMMA values+= update_set )* ( WHERE expr )? ( operation_limited_clause )?
            {
            root_0 = (Object)adaptor.nil();

            UPDATE263=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_update_stmt2442); 
            UPDATE263_tree = (Object)adaptor.create(UPDATE263);
            adaptor.addChild(root_0, UPDATE263_tree);

            // sqljet/src/Sql.g:343:21: ( operation_conflict_clause )?
            int alt99=2;
            int LA99_0 = input.LA(1);

            if ( (LA99_0==OR) ) {
                int LA99_1 = input.LA(2);

                if ( ((LA99_1>=IGNORE && LA99_1<=FAIL)||LA99_1==REPLACE) ) {
                    alt99=1;
                }
            }
            switch (alt99) {
                case 1 :
                    // sqljet/src/Sql.g:343:22: operation_conflict_clause
                    {
                    pushFollow(FOLLOW_operation_conflict_clause_in_update_stmt2445);
                    operation_conflict_clause264=operation_conflict_clause();

                    state._fsp--;

                    adaptor.addChild(root_0, operation_conflict_clause264.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_qualified_table_name_in_update_stmt2449);
            qualified_table_name265=qualified_table_name();

            state._fsp--;

            adaptor.addChild(root_0, qualified_table_name265.getTree());
            SET266=(Token)match(input,SET,FOLLOW_SET_in_update_stmt2453); 
            SET266_tree = (Object)adaptor.create(SET266);
            adaptor.addChild(root_0, SET266_tree);

            pushFollow(FOLLOW_update_set_in_update_stmt2457);
            values=update_set();

            state._fsp--;

            adaptor.addChild(root_0, values.getTree());
            if (list_values==null) list_values=new ArrayList();
            list_values.add(values.getTree());

            // sqljet/src/Sql.g:344:26: ( COMMA values+= update_set )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==COMMA) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // sqljet/src/Sql.g:344:27: COMMA values+= update_set
            	    {
            	    COMMA267=(Token)match(input,COMMA,FOLLOW_COMMA_in_update_stmt2460); 
            	    COMMA267_tree = (Object)adaptor.create(COMMA267);
            	    adaptor.addChild(root_0, COMMA267_tree);

            	    pushFollow(FOLLOW_update_set_in_update_stmt2464);
            	    values=update_set();

            	    state._fsp--;

            	    adaptor.addChild(root_0, values.getTree());
            	    if (list_values==null) list_values=new ArrayList();
            	    list_values.add(values.getTree());


            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);

            // sqljet/src/Sql.g:344:54: ( WHERE expr )?
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==WHERE) ) {
                alt101=1;
            }
            switch (alt101) {
                case 1 :
                    // sqljet/src/Sql.g:344:55: WHERE expr
                    {
                    WHERE268=(Token)match(input,WHERE,FOLLOW_WHERE_in_update_stmt2469); 
                    WHERE268_tree = (Object)adaptor.create(WHERE268);
                    adaptor.addChild(root_0, WHERE268_tree);

                    pushFollow(FOLLOW_expr_in_update_stmt2471);
                    expr269=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr269.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:344:68: ( operation_limited_clause )?
            int alt102=2;
            int LA102_0 = input.LA(1);

            if ( ((LA102_0>=ORDER && LA102_0<=LIMIT)) ) {
                alt102=1;
            }
            switch (alt102) {
                case 1 :
                    // sqljet/src/Sql.g:344:69: operation_limited_clause
                    {
                    pushFollow(FOLLOW_operation_limited_clause_in_update_stmt2476);
                    operation_limited_clause270=operation_limited_clause();

                    state._fsp--;

                    adaptor.addChild(root_0, operation_limited_clause270.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "update_stmt"

    public static class update_set_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "update_set"
    // sqljet/src/Sql.g:346:1: update_set : column_name= id EQUALS expr ;
    public final SqlParser.update_set_return update_set() throws RecognitionException {
        SqlParser.update_set_return retval = new SqlParser.update_set_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS271=null;
        SqlParser.id_return column_name = null;

        SqlParser.expr_return expr272 = null;


        Object EQUALS271_tree=null;

        try {
            // sqljet/src/Sql.g:346:11: (column_name= id EQUALS expr )
            // sqljet/src/Sql.g:346:13: column_name= id EQUALS expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_id_in_update_set2487);
            column_name=id();

            state._fsp--;

            adaptor.addChild(root_0, column_name.getTree());
            EQUALS271=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_update_set2489); 
            EQUALS271_tree = (Object)adaptor.create(EQUALS271);
            adaptor.addChild(root_0, EQUALS271_tree);

            pushFollow(FOLLOW_expr_in_update_set2491);
            expr272=expr();

            state._fsp--;

            adaptor.addChild(root_0, expr272.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "update_set"

    public static class delete_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "delete_stmt"
    // sqljet/src/Sql.g:349:1: delete_stmt : DELETE FROM qualified_table_name ( WHERE expr )? ( operation_limited_clause )? ;
    public final SqlParser.delete_stmt_return delete_stmt() throws RecognitionException {
        SqlParser.delete_stmt_return retval = new SqlParser.delete_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DELETE273=null;
        Token FROM274=null;
        Token WHERE276=null;
        SqlParser.qualified_table_name_return qualified_table_name275 = null;

        SqlParser.expr_return expr277 = null;

        SqlParser.operation_limited_clause_return operation_limited_clause278 = null;


        Object DELETE273_tree=null;
        Object FROM274_tree=null;
        Object WHERE276_tree=null;

        try {
            // sqljet/src/Sql.g:349:12: ( DELETE FROM qualified_table_name ( WHERE expr )? ( operation_limited_clause )? )
            // sqljet/src/Sql.g:349:14: DELETE FROM qualified_table_name ( WHERE expr )? ( operation_limited_clause )?
            {
            root_0 = (Object)adaptor.nil();

            DELETE273=(Token)match(input,DELETE,FOLLOW_DELETE_in_delete_stmt2499); 
            DELETE273_tree = (Object)adaptor.create(DELETE273);
            adaptor.addChild(root_0, DELETE273_tree);

            FROM274=(Token)match(input,FROM,FOLLOW_FROM_in_delete_stmt2501); 
            FROM274_tree = (Object)adaptor.create(FROM274);
            adaptor.addChild(root_0, FROM274_tree);

            pushFollow(FOLLOW_qualified_table_name_in_delete_stmt2503);
            qualified_table_name275=qualified_table_name();

            state._fsp--;

            adaptor.addChild(root_0, qualified_table_name275.getTree());
            // sqljet/src/Sql.g:349:47: ( WHERE expr )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==WHERE) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // sqljet/src/Sql.g:349:48: WHERE expr
                    {
                    WHERE276=(Token)match(input,WHERE,FOLLOW_WHERE_in_delete_stmt2506); 
                    WHERE276_tree = (Object)adaptor.create(WHERE276);
                    adaptor.addChild(root_0, WHERE276_tree);

                    pushFollow(FOLLOW_expr_in_delete_stmt2508);
                    expr277=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr277.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:349:61: ( operation_limited_clause )?
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( ((LA104_0>=ORDER && LA104_0<=LIMIT)) ) {
                alt104=1;
            }
            switch (alt104) {
                case 1 :
                    // sqljet/src/Sql.g:349:62: operation_limited_clause
                    {
                    pushFollow(FOLLOW_operation_limited_clause_in_delete_stmt2513);
                    operation_limited_clause278=operation_limited_clause();

                    state._fsp--;

                    adaptor.addChild(root_0, operation_limited_clause278.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "delete_stmt"

    public static class begin_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "begin_stmt"
    // sqljet/src/Sql.g:352:1: begin_stmt : BEGIN ( DEFERRED | IMMEDIATE | EXCLUSIVE )? ( TRANSACTION )? ;
    public final SqlParser.begin_stmt_return begin_stmt() throws RecognitionException {
        SqlParser.begin_stmt_return retval = new SqlParser.begin_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BEGIN279=null;
        Token set280=null;
        Token TRANSACTION281=null;

        Object BEGIN279_tree=null;
        Object set280_tree=null;
        Object TRANSACTION281_tree=null;

        try {
            // sqljet/src/Sql.g:352:11: ( BEGIN ( DEFERRED | IMMEDIATE | EXCLUSIVE )? ( TRANSACTION )? )
            // sqljet/src/Sql.g:352:13: BEGIN ( DEFERRED | IMMEDIATE | EXCLUSIVE )? ( TRANSACTION )?
            {
            root_0 = (Object)adaptor.nil();

            BEGIN279=(Token)match(input,BEGIN,FOLLOW_BEGIN_in_begin_stmt2523); 
            BEGIN279_tree = (Object)adaptor.create(BEGIN279);
            adaptor.addChild(root_0, BEGIN279_tree);

            // sqljet/src/Sql.g:352:19: ( DEFERRED | IMMEDIATE | EXCLUSIVE )?
            int alt105=2;
            int LA105_0 = input.LA(1);

            if ( ((LA105_0>=DEFERRED && LA105_0<=EXCLUSIVE)) ) {
                alt105=1;
            }
            switch (alt105) {
                case 1 :
                    // sqljet/src/Sql.g:
                    {
                    set280=(Token)input.LT(1);
                    if ( (input.LA(1)>=DEFERRED && input.LA(1)<=EXCLUSIVE) ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set280));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            // sqljet/src/Sql.g:352:55: ( TRANSACTION )?
            int alt106=2;
            int LA106_0 = input.LA(1);

            if ( (LA106_0==TRANSACTION) ) {
                alt106=1;
            }
            switch (alt106) {
                case 1 :
                    // sqljet/src/Sql.g:352:56: TRANSACTION
                    {
                    TRANSACTION281=(Token)match(input,TRANSACTION,FOLLOW_TRANSACTION_in_begin_stmt2539); 
                    TRANSACTION281_tree = (Object)adaptor.create(TRANSACTION281);
                    adaptor.addChild(root_0, TRANSACTION281_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "begin_stmt"

    public static class commit_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "commit_stmt"
    // sqljet/src/Sql.g:355:1: commit_stmt : ( COMMIT | END ) ( TRANSACTION )? ;
    public final SqlParser.commit_stmt_return commit_stmt() throws RecognitionException {
        SqlParser.commit_stmt_return retval = new SqlParser.commit_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set282=null;
        Token TRANSACTION283=null;

        Object set282_tree=null;
        Object TRANSACTION283_tree=null;

        try {
            // sqljet/src/Sql.g:355:12: ( ( COMMIT | END ) ( TRANSACTION )? )
            // sqljet/src/Sql.g:355:14: ( COMMIT | END ) ( TRANSACTION )?
            {
            root_0 = (Object)adaptor.nil();

            set282=(Token)input.LT(1);
            if ( input.LA(1)==END||input.LA(1)==COMMIT ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set282));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // sqljet/src/Sql.g:355:29: ( TRANSACTION )?
            int alt107=2;
            int LA107_0 = input.LA(1);

            if ( (LA107_0==TRANSACTION) ) {
                alt107=1;
            }
            switch (alt107) {
                case 1 :
                    // sqljet/src/Sql.g:355:30: TRANSACTION
                    {
                    TRANSACTION283=(Token)match(input,TRANSACTION,FOLLOW_TRANSACTION_in_commit_stmt2558); 
                    TRANSACTION283_tree = (Object)adaptor.create(TRANSACTION283);
                    adaptor.addChild(root_0, TRANSACTION283_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "commit_stmt"

    public static class rollback_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rollback_stmt"
    // sqljet/src/Sql.g:358:1: rollback_stmt : ROLLBACK ( TRANSACTION )? ( TO ( SAVEPOINT )? savepoint_name= id )? ;
    public final SqlParser.rollback_stmt_return rollback_stmt() throws RecognitionException {
        SqlParser.rollback_stmt_return retval = new SqlParser.rollback_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ROLLBACK284=null;
        Token TRANSACTION285=null;
        Token TO286=null;
        Token SAVEPOINT287=null;
        SqlParser.id_return savepoint_name = null;


        Object ROLLBACK284_tree=null;
        Object TRANSACTION285_tree=null;
        Object TO286_tree=null;
        Object SAVEPOINT287_tree=null;

        try {
            // sqljet/src/Sql.g:358:14: ( ROLLBACK ( TRANSACTION )? ( TO ( SAVEPOINT )? savepoint_name= id )? )
            // sqljet/src/Sql.g:358:16: ROLLBACK ( TRANSACTION )? ( TO ( SAVEPOINT )? savepoint_name= id )?
            {
            root_0 = (Object)adaptor.nil();

            ROLLBACK284=(Token)match(input,ROLLBACK,FOLLOW_ROLLBACK_in_rollback_stmt2568); 
            ROLLBACK284_tree = (Object)adaptor.create(ROLLBACK284);
            adaptor.addChild(root_0, ROLLBACK284_tree);

            // sqljet/src/Sql.g:358:25: ( TRANSACTION )?
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( (LA108_0==TRANSACTION) ) {
                alt108=1;
            }
            switch (alt108) {
                case 1 :
                    // sqljet/src/Sql.g:358:26: TRANSACTION
                    {
                    TRANSACTION285=(Token)match(input,TRANSACTION,FOLLOW_TRANSACTION_in_rollback_stmt2571); 
                    TRANSACTION285_tree = (Object)adaptor.create(TRANSACTION285);
                    adaptor.addChild(root_0, TRANSACTION285_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:358:40: ( TO ( SAVEPOINT )? savepoint_name= id )?
            int alt110=2;
            int LA110_0 = input.LA(1);

            if ( (LA110_0==TO) ) {
                alt110=1;
            }
            switch (alt110) {
                case 1 :
                    // sqljet/src/Sql.g:358:41: TO ( SAVEPOINT )? savepoint_name= id
                    {
                    TO286=(Token)match(input,TO,FOLLOW_TO_in_rollback_stmt2576); 
                    TO286_tree = (Object)adaptor.create(TO286);
                    adaptor.addChild(root_0, TO286_tree);

                    // sqljet/src/Sql.g:358:44: ( SAVEPOINT )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==SAVEPOINT) ) {
                        int LA109_1 = input.LA(2);

                        if ( ((LA109_1>=EXPLAIN && LA109_1<=PLAN)||(LA109_1>=INDEXED && LA109_1<=BY)||(LA109_1>=OR && LA109_1<=ESCAPE)||(LA109_1>=IS && LA109_1<=BETWEEN)||(LA109_1>=COLLATE && LA109_1<=THEN)||LA109_1==STRING||(LA109_1>=CURRENT_TIME && LA109_1<=CURRENT_TIMESTAMP)||(LA109_1>=RAISE && LA109_1<=ROW)) ) {
                            alt109=1;
                        }
                    }
                    switch (alt109) {
                        case 1 :
                            // sqljet/src/Sql.g:358:45: SAVEPOINT
                            {
                            SAVEPOINT287=(Token)match(input,SAVEPOINT,FOLLOW_SAVEPOINT_in_rollback_stmt2579); 
                            SAVEPOINT287_tree = (Object)adaptor.create(SAVEPOINT287);
                            adaptor.addChild(root_0, SAVEPOINT287_tree);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_id_in_rollback_stmt2585);
                    savepoint_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, savepoint_name.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rollback_stmt"

    public static class savepoint_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "savepoint_stmt"
    // sqljet/src/Sql.g:361:1: savepoint_stmt : SAVEPOINT savepoint_name= id ;
    public final SqlParser.savepoint_stmt_return savepoint_stmt() throws RecognitionException {
        SqlParser.savepoint_stmt_return retval = new SqlParser.savepoint_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SAVEPOINT288=null;
        SqlParser.id_return savepoint_name = null;


        Object SAVEPOINT288_tree=null;

        try {
            // sqljet/src/Sql.g:361:15: ( SAVEPOINT savepoint_name= id )
            // sqljet/src/Sql.g:361:17: SAVEPOINT savepoint_name= id
            {
            root_0 = (Object)adaptor.nil();

            SAVEPOINT288=(Token)match(input,SAVEPOINT,FOLLOW_SAVEPOINT_in_savepoint_stmt2595); 
            SAVEPOINT288_tree = (Object)adaptor.create(SAVEPOINT288);
            adaptor.addChild(root_0, SAVEPOINT288_tree);

            pushFollow(FOLLOW_id_in_savepoint_stmt2599);
            savepoint_name=id();

            state._fsp--;

            adaptor.addChild(root_0, savepoint_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "savepoint_stmt"

    public static class release_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "release_stmt"
    // sqljet/src/Sql.g:364:1: release_stmt : RELEASE ( SAVEPOINT )? savepoint_name= id ;
    public final SqlParser.release_stmt_return release_stmt() throws RecognitionException {
        SqlParser.release_stmt_return retval = new SqlParser.release_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token RELEASE289=null;
        Token SAVEPOINT290=null;
        SqlParser.id_return savepoint_name = null;


        Object RELEASE289_tree=null;
        Object SAVEPOINT290_tree=null;

        try {
            // sqljet/src/Sql.g:364:13: ( RELEASE ( SAVEPOINT )? savepoint_name= id )
            // sqljet/src/Sql.g:364:15: RELEASE ( SAVEPOINT )? savepoint_name= id
            {
            root_0 = (Object)adaptor.nil();

            RELEASE289=(Token)match(input,RELEASE,FOLLOW_RELEASE_in_release_stmt2607); 
            RELEASE289_tree = (Object)adaptor.create(RELEASE289);
            adaptor.addChild(root_0, RELEASE289_tree);

            // sqljet/src/Sql.g:364:23: ( SAVEPOINT )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==SAVEPOINT) ) {
                int LA111_1 = input.LA(2);

                if ( ((LA111_1>=EXPLAIN && LA111_1<=PLAN)||(LA111_1>=INDEXED && LA111_1<=BY)||(LA111_1>=OR && LA111_1<=ESCAPE)||(LA111_1>=IS && LA111_1<=BETWEEN)||(LA111_1>=COLLATE && LA111_1<=THEN)||LA111_1==STRING||(LA111_1>=CURRENT_TIME && LA111_1<=CURRENT_TIMESTAMP)||(LA111_1>=RAISE && LA111_1<=ROW)) ) {
                    alt111=1;
                }
            }
            switch (alt111) {
                case 1 :
                    // sqljet/src/Sql.g:364:24: SAVEPOINT
                    {
                    SAVEPOINT290=(Token)match(input,SAVEPOINT,FOLLOW_SAVEPOINT_in_release_stmt2610); 
                    SAVEPOINT290_tree = (Object)adaptor.create(SAVEPOINT290);
                    adaptor.addChild(root_0, SAVEPOINT290_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_release_stmt2616);
            savepoint_name=id();

            state._fsp--;

            adaptor.addChild(root_0, savepoint_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "release_stmt"

    public static class table_conflict_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_conflict_clause"
    // sqljet/src/Sql.g:371:1: table_conflict_clause : ON CONFLICT ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE ) ;
    public final SqlParser.table_conflict_clause_return table_conflict_clause() throws RecognitionException {
        SqlParser.table_conflict_clause_return retval = new SqlParser.table_conflict_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ON291=null;
        Token CONFLICT292=null;
        Token set293=null;

        Object ON291_tree=null;
        Object CONFLICT292_tree=null;
        Object set293_tree=null;

        try {
            // sqljet/src/Sql.g:371:22: ( ON CONFLICT ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE ) )
            // sqljet/src/Sql.g:371:24: ON CONFLICT ( ROLLBACK | ABORT | FAIL | IGNORE | REPLACE )
            {
            root_0 = (Object)adaptor.nil();

            ON291=(Token)match(input,ON,FOLLOW_ON_in_table_conflict_clause2628); 
            CONFLICT292=(Token)match(input,CONFLICT,FOLLOW_CONFLICT_in_table_conflict_clause2631); 
            CONFLICT292_tree = (Object)adaptor.create(CONFLICT292);
            root_0 = (Object)adaptor.becomeRoot(CONFLICT292_tree, root_0);

            set293=(Token)input.LT(1);
            if ( (input.LA(1)>=IGNORE && input.LA(1)<=FAIL)||input.LA(1)==REPLACE ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set293));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_conflict_clause"

    public static class create_virtual_table_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "create_virtual_table_stmt"
    // sqljet/src/Sql.g:375:1: create_virtual_table_stmt : CREATE VIRTUAL TABLE (database_name= id DOT )? table_name= id USING module_name= id ( LPAREN column_def ( COMMA column_def )* RPAREN )? -> ^( CREATE_TABLE ^( OPTIONS VIRTUAL ) ^( $table_name ( $database_name)? ) ^( $module_name) ( ^( COLUMNS ( column_def )+ ) )? ) ;
    public final SqlParser.create_virtual_table_stmt_return create_virtual_table_stmt() throws RecognitionException {
        SqlParser.create_virtual_table_stmt_return retval = new SqlParser.create_virtual_table_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CREATE294=null;
        Token VIRTUAL295=null;
        Token TABLE296=null;
        Token DOT297=null;
        Token USING298=null;
        Token LPAREN299=null;
        Token COMMA301=null;
        Token RPAREN303=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.id_return module_name = null;

        SqlParser.column_def_return column_def300 = null;

        SqlParser.column_def_return column_def302 = null;


        Object CREATE294_tree=null;
        Object VIRTUAL295_tree=null;
        Object TABLE296_tree=null;
        Object DOT297_tree=null;
        Object USING298_tree=null;
        Object LPAREN299_tree=null;
        Object COMMA301_tree=null;
        Object RPAREN303_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_VIRTUAL=new RewriteRuleTokenStream(adaptor,"token VIRTUAL");
        RewriteRuleTokenStream stream_TABLE=new RewriteRuleTokenStream(adaptor,"token TABLE");
        RewriteRuleTokenStream stream_USING=new RewriteRuleTokenStream(adaptor,"token USING");
        RewriteRuleTokenStream stream_CREATE=new RewriteRuleTokenStream(adaptor,"token CREATE");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_column_def=new RewriteRuleSubtreeStream(adaptor,"rule column_def");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:375:26: ( CREATE VIRTUAL TABLE (database_name= id DOT )? table_name= id USING module_name= id ( LPAREN column_def ( COMMA column_def )* RPAREN )? -> ^( CREATE_TABLE ^( OPTIONS VIRTUAL ) ^( $table_name ( $database_name)? ) ^( $module_name) ( ^( COLUMNS ( column_def )+ ) )? ) )
            // sqljet/src/Sql.g:375:28: CREATE VIRTUAL TABLE (database_name= id DOT )? table_name= id USING module_name= id ( LPAREN column_def ( COMMA column_def )* RPAREN )?
            {
            CREATE294=(Token)match(input,CREATE,FOLLOW_CREATE_in_create_virtual_table_stmt2661);  
            stream_CREATE.add(CREATE294);

            VIRTUAL295=(Token)match(input,VIRTUAL,FOLLOW_VIRTUAL_in_create_virtual_table_stmt2663);  
            stream_VIRTUAL.add(VIRTUAL295);

            TABLE296=(Token)match(input,TABLE,FOLLOW_TABLE_in_create_virtual_table_stmt2665);  
            stream_TABLE.add(TABLE296);

            // sqljet/src/Sql.g:375:49: (database_name= id DOT )?
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==ID||LA112_0==STRING) ) {
                int LA112_1 = input.LA(2);

                if ( (LA112_1==DOT) ) {
                    alt112=1;
                }
            }
            else if ( ((LA112_0>=EXPLAIN && LA112_0<=PLAN)||(LA112_0>=INDEXED && LA112_0<=BY)||(LA112_0>=OR && LA112_0<=ESCAPE)||(LA112_0>=IS && LA112_0<=BETWEEN)||LA112_0==COLLATE||(LA112_0>=DISTINCT && LA112_0<=THEN)||(LA112_0>=CURRENT_TIME && LA112_0<=CURRENT_TIMESTAMP)||(LA112_0>=RAISE && LA112_0<=ROW)) ) {
                int LA112_2 = input.LA(2);

                if ( (LA112_2==DOT) ) {
                    alt112=1;
                }
            }
            switch (alt112) {
                case 1 :
                    // sqljet/src/Sql.g:375:50: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_create_virtual_table_stmt2670);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT297=(Token)match(input,DOT,FOLLOW_DOT_in_create_virtual_table_stmt2672);  
                    stream_DOT.add(DOT297);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_create_virtual_table_stmt2678);
            table_name=id();

            state._fsp--;

            stream_id.add(table_name.getTree());
            USING298=(Token)match(input,USING,FOLLOW_USING_in_create_virtual_table_stmt2682);  
            stream_USING.add(USING298);

            pushFollow(FOLLOW_id_in_create_virtual_table_stmt2686);
            module_name=id();

            state._fsp--;

            stream_id.add(module_name.getTree());
            // sqljet/src/Sql.g:376:24: ( LPAREN column_def ( COMMA column_def )* RPAREN )?
            int alt114=2;
            int LA114_0 = input.LA(1);

            if ( (LA114_0==LPAREN) ) {
                alt114=1;
            }
            switch (alt114) {
                case 1 :
                    // sqljet/src/Sql.g:376:25: LPAREN column_def ( COMMA column_def )* RPAREN
                    {
                    LPAREN299=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_create_virtual_table_stmt2689);  
                    stream_LPAREN.add(LPAREN299);

                    pushFollow(FOLLOW_column_def_in_create_virtual_table_stmt2691);
                    column_def300=column_def();

                    state._fsp--;

                    stream_column_def.add(column_def300.getTree());
                    // sqljet/src/Sql.g:376:43: ( COMMA column_def )*
                    loop113:
                    do {
                        int alt113=2;
                        int LA113_0 = input.LA(1);

                        if ( (LA113_0==COMMA) ) {
                            alt113=1;
                        }


                        switch (alt113) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:376:44: COMMA column_def
                    	    {
                    	    COMMA301=(Token)match(input,COMMA,FOLLOW_COMMA_in_create_virtual_table_stmt2694);  
                    	    stream_COMMA.add(COMMA301);

                    	    pushFollow(FOLLOW_column_def_in_create_virtual_table_stmt2696);
                    	    column_def302=column_def();

                    	    state._fsp--;

                    	    stream_column_def.add(column_def302.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop113;
                        }
                    } while (true);

                    RPAREN303=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_create_virtual_table_stmt2700);  
                    stream_RPAREN.add(RPAREN303);


                    }
                    break;

            }



            // AST REWRITE
            // elements: database_name, column_def, module_name, VIRTUAL, table_name
            // token labels: 
            // rule labels: module_name, database_name, retval, table_name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_module_name=new RewriteRuleSubtreeStream(adaptor,"rule module_name",module_name!=null?module_name.tree:null);
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 377:3: -> ^( CREATE_TABLE ^( OPTIONS VIRTUAL ) ^( $table_name ( $database_name)? ) ^( $module_name) ( ^( COLUMNS ( column_def )+ ) )? )
            {
                // sqljet/src/Sql.g:377:6: ^( CREATE_TABLE ^( OPTIONS VIRTUAL ) ^( $table_name ( $database_name)? ) ^( $module_name) ( ^( COLUMNS ( column_def )+ ) )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CREATE_TABLE, "CREATE_TABLE"), root_1);

                // sqljet/src/Sql.g:377:21: ^( OPTIONS VIRTUAL )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONS, "OPTIONS"), root_2);

                adaptor.addChild(root_2, stream_VIRTUAL.nextNode());

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:377:40: ^( $table_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_2);

                // sqljet/src/Sql.g:377:54: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:377:71: ^( $module_name)
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_module_name.nextNode(), root_2);

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:377:87: ( ^( COLUMNS ( column_def )+ ) )?
                if ( stream_column_def.hasNext() ) {
                    // sqljet/src/Sql.g:377:87: ^( COLUMNS ( column_def )+ )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                    if ( !(stream_column_def.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_column_def.hasNext() ) {
                        adaptor.addChild(root_2, stream_column_def.nextTree());

                    }
                    stream_column_def.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_column_def.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "create_virtual_table_stmt"

    public static class create_table_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "create_table_stmt"
    // sqljet/src/Sql.g:380:1: create_table_stmt : CREATE ( TEMPORARY )? TABLE ( IF NOT EXISTS )? (database_name= id DOT )? table_name= id ( LPAREN column_def ( COMMA column_def )* ( COMMA table_constraint )* RPAREN | AS select_stmt ) -> ^( CREATE_TABLE ^( OPTIONS ( TEMPORARY )? ( EXISTS )? ) ^( $table_name ( $database_name)? ) ( ^( COLUMNS ( column_def )+ ) )? ( ^( CONSTRAINTS ( table_constraint )* ) )? ( select_stmt )? ) ;
    public final SqlParser.create_table_stmt_return create_table_stmt() throws RecognitionException {
        SqlParser.create_table_stmt_return retval = new SqlParser.create_table_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CREATE304=null;
        Token TEMPORARY305=null;
        Token TABLE306=null;
        Token IF307=null;
        Token NOT308=null;
        Token EXISTS309=null;
        Token DOT310=null;
        Token LPAREN311=null;
        Token COMMA313=null;
        Token COMMA315=null;
        Token RPAREN317=null;
        Token AS318=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.column_def_return column_def312 = null;

        SqlParser.column_def_return column_def314 = null;

        SqlParser.table_constraint_return table_constraint316 = null;

        SqlParser.select_stmt_return select_stmt319 = null;


        Object CREATE304_tree=null;
        Object TEMPORARY305_tree=null;
        Object TABLE306_tree=null;
        Object IF307_tree=null;
        Object NOT308_tree=null;
        Object EXISTS309_tree=null;
        Object DOT310_tree=null;
        Object LPAREN311_tree=null;
        Object COMMA313_tree=null;
        Object COMMA315_tree=null;
        Object RPAREN317_tree=null;
        Object AS318_tree=null;
        RewriteRuleTokenStream stream_EXISTS=new RewriteRuleTokenStream(adaptor,"token EXISTS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_TABLE=new RewriteRuleTokenStream(adaptor,"token TABLE");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_TEMPORARY=new RewriteRuleTokenStream(adaptor,"token TEMPORARY");
        RewriteRuleTokenStream stream_CREATE=new RewriteRuleTokenStream(adaptor,"token CREATE");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_column_def=new RewriteRuleSubtreeStream(adaptor,"rule column_def");
        RewriteRuleSubtreeStream stream_table_constraint=new RewriteRuleSubtreeStream(adaptor,"rule table_constraint");
        RewriteRuleSubtreeStream stream_select_stmt=new RewriteRuleSubtreeStream(adaptor,"rule select_stmt");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:380:18: ( CREATE ( TEMPORARY )? TABLE ( IF NOT EXISTS )? (database_name= id DOT )? table_name= id ( LPAREN column_def ( COMMA column_def )* ( COMMA table_constraint )* RPAREN | AS select_stmt ) -> ^( CREATE_TABLE ^( OPTIONS ( TEMPORARY )? ( EXISTS )? ) ^( $table_name ( $database_name)? ) ( ^( COLUMNS ( column_def )+ ) )? ( ^( CONSTRAINTS ( table_constraint )* ) )? ( select_stmt )? ) )
            // sqljet/src/Sql.g:380:20: CREATE ( TEMPORARY )? TABLE ( IF NOT EXISTS )? (database_name= id DOT )? table_name= id ( LPAREN column_def ( COMMA column_def )* ( COMMA table_constraint )* RPAREN | AS select_stmt )
            {
            CREATE304=(Token)match(input,CREATE,FOLLOW_CREATE_in_create_table_stmt2746);  
            stream_CREATE.add(CREATE304);

            // sqljet/src/Sql.g:380:27: ( TEMPORARY )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( (LA115_0==TEMPORARY) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // sqljet/src/Sql.g:380:27: TEMPORARY
                    {
                    TEMPORARY305=(Token)match(input,TEMPORARY,FOLLOW_TEMPORARY_in_create_table_stmt2748);  
                    stream_TEMPORARY.add(TEMPORARY305);


                    }
                    break;

            }

            TABLE306=(Token)match(input,TABLE,FOLLOW_TABLE_in_create_table_stmt2751);  
            stream_TABLE.add(TABLE306);

            // sqljet/src/Sql.g:380:44: ( IF NOT EXISTS )?
            int alt116=2;
            int LA116_0 = input.LA(1);

            if ( (LA116_0==IF) ) {
                int LA116_1 = input.LA(2);

                if ( (LA116_1==NOT) ) {
                    alt116=1;
                }
            }
            switch (alt116) {
                case 1 :
                    // sqljet/src/Sql.g:380:45: IF NOT EXISTS
                    {
                    IF307=(Token)match(input,IF,FOLLOW_IF_in_create_table_stmt2754);  
                    stream_IF.add(IF307);

                    NOT308=(Token)match(input,NOT,FOLLOW_NOT_in_create_table_stmt2756);  
                    stream_NOT.add(NOT308);

                    EXISTS309=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_create_table_stmt2758);  
                    stream_EXISTS.add(EXISTS309);


                    }
                    break;

            }

            // sqljet/src/Sql.g:380:61: (database_name= id DOT )?
            int alt117=2;
            int LA117_0 = input.LA(1);

            if ( (LA117_0==ID||LA117_0==STRING) ) {
                int LA117_1 = input.LA(2);

                if ( (LA117_1==DOT) ) {
                    alt117=1;
                }
            }
            else if ( ((LA117_0>=EXPLAIN && LA117_0<=PLAN)||(LA117_0>=INDEXED && LA117_0<=BY)||(LA117_0>=OR && LA117_0<=ESCAPE)||(LA117_0>=IS && LA117_0<=BETWEEN)||LA117_0==COLLATE||(LA117_0>=DISTINCT && LA117_0<=THEN)||(LA117_0>=CURRENT_TIME && LA117_0<=CURRENT_TIMESTAMP)||(LA117_0>=RAISE && LA117_0<=ROW)) ) {
                int LA117_2 = input.LA(2);

                if ( (LA117_2==DOT) ) {
                    alt117=1;
                }
            }
            switch (alt117) {
                case 1 :
                    // sqljet/src/Sql.g:380:62: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_create_table_stmt2765);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT310=(Token)match(input,DOT,FOLLOW_DOT_in_create_table_stmt2767);  
                    stream_DOT.add(DOT310);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_create_table_stmt2773);
            table_name=id();

            state._fsp--;

            stream_id.add(table_name.getTree());
            // sqljet/src/Sql.g:381:3: ( LPAREN column_def ( COMMA column_def )* ( COMMA table_constraint )* RPAREN | AS select_stmt )
            int alt120=2;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==LPAREN) ) {
                alt120=1;
            }
            else if ( (LA120_0==AS) ) {
                alt120=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }
            switch (alt120) {
                case 1 :
                    // sqljet/src/Sql.g:381:5: LPAREN column_def ( COMMA column_def )* ( COMMA table_constraint )* RPAREN
                    {
                    LPAREN311=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_create_table_stmt2779);  
                    stream_LPAREN.add(LPAREN311);

                    pushFollow(FOLLOW_column_def_in_create_table_stmt2781);
                    column_def312=column_def();

                    state._fsp--;

                    stream_column_def.add(column_def312.getTree());
                    // sqljet/src/Sql.g:381:23: ( COMMA column_def )*
                    loop118:
                    do {
                        int alt118=2;
                        alt118 = dfa118.predict(input);
                        switch (alt118) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:381:24: COMMA column_def
                    	    {
                    	    COMMA313=(Token)match(input,COMMA,FOLLOW_COMMA_in_create_table_stmt2784);  
                    	    stream_COMMA.add(COMMA313);

                    	    pushFollow(FOLLOW_column_def_in_create_table_stmt2786);
                    	    column_def314=column_def();

                    	    state._fsp--;

                    	    stream_column_def.add(column_def314.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop118;
                        }
                    } while (true);

                    // sqljet/src/Sql.g:381:43: ( COMMA table_constraint )*
                    loop119:
                    do {
                        int alt119=2;
                        int LA119_0 = input.LA(1);

                        if ( (LA119_0==COMMA) ) {
                            alt119=1;
                        }


                        switch (alt119) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:381:44: COMMA table_constraint
                    	    {
                    	    COMMA315=(Token)match(input,COMMA,FOLLOW_COMMA_in_create_table_stmt2791);  
                    	    stream_COMMA.add(COMMA315);

                    	    pushFollow(FOLLOW_table_constraint_in_create_table_stmt2793);
                    	    table_constraint316=table_constraint();

                    	    state._fsp--;

                    	    stream_table_constraint.add(table_constraint316.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop119;
                        }
                    } while (true);

                    RPAREN317=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_create_table_stmt2797);  
                    stream_RPAREN.add(RPAREN317);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:382:5: AS select_stmt
                    {
                    AS318=(Token)match(input,AS,FOLLOW_AS_in_create_table_stmt2803);  
                    stream_AS.add(AS318);

                    pushFollow(FOLLOW_select_stmt_in_create_table_stmt2805);
                    select_stmt319=select_stmt();

                    state._fsp--;

                    stream_select_stmt.add(select_stmt319.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: EXISTS, table_name, TEMPORARY, select_stmt, table_constraint, column_def, database_name
            // token labels: 
            // rule labels: database_name, retval, table_name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 383:1: -> ^( CREATE_TABLE ^( OPTIONS ( TEMPORARY )? ( EXISTS )? ) ^( $table_name ( $database_name)? ) ( ^( COLUMNS ( column_def )+ ) )? ( ^( CONSTRAINTS ( table_constraint )* ) )? ( select_stmt )? )
            {
                // sqljet/src/Sql.g:383:4: ^( CREATE_TABLE ^( OPTIONS ( TEMPORARY )? ( EXISTS )? ) ^( $table_name ( $database_name)? ) ( ^( COLUMNS ( column_def )+ ) )? ( ^( CONSTRAINTS ( table_constraint )* ) )? ( select_stmt )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CREATE_TABLE, "CREATE_TABLE"), root_1);

                // sqljet/src/Sql.g:383:19: ^( OPTIONS ( TEMPORARY )? ( EXISTS )? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONS, "OPTIONS"), root_2);

                // sqljet/src/Sql.g:383:29: ( TEMPORARY )?
                if ( stream_TEMPORARY.hasNext() ) {
                    adaptor.addChild(root_2, stream_TEMPORARY.nextNode());

                }
                stream_TEMPORARY.reset();
                // sqljet/src/Sql.g:383:40: ( EXISTS )?
                if ( stream_EXISTS.hasNext() ) {
                    adaptor.addChild(root_2, stream_EXISTS.nextNode());

                }
                stream_EXISTS.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:383:49: ^( $table_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_2);

                // sqljet/src/Sql.g:383:63: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:384:3: ( ^( COLUMNS ( column_def )+ ) )?
                if ( stream_column_def.hasNext() ) {
                    // sqljet/src/Sql.g:384:3: ^( COLUMNS ( column_def )+ )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                    if ( !(stream_column_def.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_column_def.hasNext() ) {
                        adaptor.addChild(root_2, stream_column_def.nextTree());

                    }
                    stream_column_def.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_column_def.reset();
                // sqljet/src/Sql.g:384:27: ( ^( CONSTRAINTS ( table_constraint )* ) )?
                if ( stream_table_constraint.hasNext() ) {
                    // sqljet/src/Sql.g:384:27: ^( CONSTRAINTS ( table_constraint )* )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONSTRAINTS, "CONSTRAINTS"), root_2);

                    // sqljet/src/Sql.g:384:41: ( table_constraint )*
                    while ( stream_table_constraint.hasNext() ) {
                        adaptor.addChild(root_2, stream_table_constraint.nextTree());

                    }
                    stream_table_constraint.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_table_constraint.reset();
                // sqljet/src/Sql.g:384:61: ( select_stmt )?
                if ( stream_select_stmt.hasNext() ) {
                    adaptor.addChild(root_1, stream_select_stmt.nextTree());

                }
                stream_select_stmt.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "create_table_stmt"

    public static class column_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_def"
    // sqljet/src/Sql.g:386:1: column_def : name= id_column_def ( type_name )? ( column_constraint )* -> ^( $name ^( CONSTRAINTS ( column_constraint )* ) ( type_name )? ) ;
    public final SqlParser.column_def_return column_def() throws RecognitionException {
        SqlParser.column_def_return retval = new SqlParser.column_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.id_column_def_return name = null;

        SqlParser.type_name_return type_name320 = null;

        SqlParser.column_constraint_return column_constraint321 = null;


        RewriteRuleSubtreeStream stream_type_name=new RewriteRuleSubtreeStream(adaptor,"rule type_name");
        RewriteRuleSubtreeStream stream_column_constraint=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint");
        RewriteRuleSubtreeStream stream_id_column_def=new RewriteRuleSubtreeStream(adaptor,"rule id_column_def");
        try {
            // sqljet/src/Sql.g:386:11: (name= id_column_def ( type_name )? ( column_constraint )* -> ^( $name ^( CONSTRAINTS ( column_constraint )* ) ( type_name )? ) )
            // sqljet/src/Sql.g:386:13: name= id_column_def ( type_name )? ( column_constraint )*
            {
            pushFollow(FOLLOW_id_column_def_in_column_def2861);
            name=id_column_def();

            state._fsp--;

            stream_id_column_def.add(name.getTree());
            // sqljet/src/Sql.g:386:32: ( type_name )?
            int alt121=2;
            alt121 = dfa121.predict(input);
            switch (alt121) {
                case 1 :
                    // sqljet/src/Sql.g:386:32: type_name
                    {
                    pushFollow(FOLLOW_type_name_in_column_def2863);
                    type_name320=type_name();

                    state._fsp--;

                    stream_type_name.add(type_name320.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:386:43: ( column_constraint )*
            loop122:
            do {
                int alt122=2;
                alt122 = dfa122.predict(input);
                switch (alt122) {
            	case 1 :
            	    // sqljet/src/Sql.g:386:43: column_constraint
            	    {
            	    pushFollow(FOLLOW_column_constraint_in_column_def2866);
            	    column_constraint321=column_constraint();

            	    state._fsp--;

            	    stream_column_constraint.add(column_constraint321.getTree());

            	    }
            	    break;

            	default :
            	    break loop122;
                }
            } while (true);



            // AST REWRITE
            // elements: name, type_name, column_constraint
            // token labels: 
            // rule labels: retval, name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name",name!=null?name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 387:1: -> ^( $name ^( CONSTRAINTS ( column_constraint )* ) ( type_name )? )
            {
                // sqljet/src/Sql.g:387:4: ^( $name ^( CONSTRAINTS ( column_constraint )* ) ( type_name )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_name.nextNode(), root_1);

                // sqljet/src/Sql.g:387:12: ^( CONSTRAINTS ( column_constraint )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONSTRAINTS, "CONSTRAINTS"), root_2);

                // sqljet/src/Sql.g:387:26: ( column_constraint )*
                while ( stream_column_constraint.hasNext() ) {
                    adaptor.addChild(root_2, stream_column_constraint.nextTree());

                }
                stream_column_constraint.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:387:46: ( type_name )?
                if ( stream_type_name.hasNext() ) {
                    adaptor.addChild(root_1, stream_type_name.nextTree());

                }
                stream_type_name.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_def"

    public static class column_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint"
    // sqljet/src/Sql.g:389:1: column_constraint : ( CONSTRAINT name= id )? ( column_constraint_pk | column_constraint_not_null | column_constraint_null | column_constraint_unique | column_constraint_check | column_constraint_default | column_constraint_collate | fk_clause ) -> ^( COLUMN_CONSTRAINT ( column_constraint_pk )? ( column_constraint_not_null )? ( column_constraint_null )? ( column_constraint_unique )? ( column_constraint_check )? ( column_constraint_default )? ( column_constraint_collate )? ( fk_clause )? ( $name)? ) ;
    public final SqlParser.column_constraint_return column_constraint() throws RecognitionException {
        SqlParser.column_constraint_return retval = new SqlParser.column_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CONSTRAINT322=null;
        SqlParser.id_return name = null;

        SqlParser.column_constraint_pk_return column_constraint_pk323 = null;

        SqlParser.column_constraint_not_null_return column_constraint_not_null324 = null;

        SqlParser.column_constraint_null_return column_constraint_null325 = null;

        SqlParser.column_constraint_unique_return column_constraint_unique326 = null;

        SqlParser.column_constraint_check_return column_constraint_check327 = null;

        SqlParser.column_constraint_default_return column_constraint_default328 = null;

        SqlParser.column_constraint_collate_return column_constraint_collate329 = null;

        SqlParser.fk_clause_return fk_clause330 = null;


        Object CONSTRAINT322_tree=null;
        RewriteRuleTokenStream stream_CONSTRAINT=new RewriteRuleTokenStream(adaptor,"token CONSTRAINT");
        RewriteRuleSubtreeStream stream_column_constraint_not_null=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_not_null");
        RewriteRuleSubtreeStream stream_column_constraint_unique=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_unique");
        RewriteRuleSubtreeStream stream_column_constraint_check=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_check");
        RewriteRuleSubtreeStream stream_fk_clause=new RewriteRuleSubtreeStream(adaptor,"rule fk_clause");
        RewriteRuleSubtreeStream stream_column_constraint_pk=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_pk");
        RewriteRuleSubtreeStream stream_column_constraint_collate=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_collate");
        RewriteRuleSubtreeStream stream_column_constraint_null=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_null");
        RewriteRuleSubtreeStream stream_column_constraint_default=new RewriteRuleSubtreeStream(adaptor,"rule column_constraint_default");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:389:18: ( ( CONSTRAINT name= id )? ( column_constraint_pk | column_constraint_not_null | column_constraint_null | column_constraint_unique | column_constraint_check | column_constraint_default | column_constraint_collate | fk_clause ) -> ^( COLUMN_CONSTRAINT ( column_constraint_pk )? ( column_constraint_not_null )? ( column_constraint_null )? ( column_constraint_unique )? ( column_constraint_check )? ( column_constraint_default )? ( column_constraint_collate )? ( fk_clause )? ( $name)? ) )
            // sqljet/src/Sql.g:389:20: ( CONSTRAINT name= id )? ( column_constraint_pk | column_constraint_not_null | column_constraint_null | column_constraint_unique | column_constraint_check | column_constraint_default | column_constraint_collate | fk_clause )
            {
            // sqljet/src/Sql.g:389:20: ( CONSTRAINT name= id )?
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // sqljet/src/Sql.g:389:21: CONSTRAINT name= id
                    {
                    CONSTRAINT322=(Token)match(input,CONSTRAINT,FOLLOW_CONSTRAINT_in_column_constraint2892);  
                    stream_CONSTRAINT.add(CONSTRAINT322);

                    pushFollow(FOLLOW_id_in_column_constraint2896);
                    name=id();

                    state._fsp--;

                    stream_id.add(name.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:390:3: ( column_constraint_pk | column_constraint_not_null | column_constraint_null | column_constraint_unique | column_constraint_check | column_constraint_default | column_constraint_collate | fk_clause )
            int alt124=8;
            switch ( input.LA(1) ) {
            case PRIMARY:
                {
                alt124=1;
                }
                break;
            case NOT:
                {
                alt124=2;
                }
                break;
            case NULL:
                {
                alt124=3;
                }
                break;
            case UNIQUE:
                {
                alt124=4;
                }
                break;
            case CHECK:
                {
                alt124=5;
                }
                break;
            case DEFAULT:
                {
                alt124=6;
                }
                break;
            case COLLATE:
                {
                alt124=7;
                }
                break;
            case REFERENCES:
                {
                alt124=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 124, 0, input);

                throw nvae;
            }

            switch (alt124) {
                case 1 :
                    // sqljet/src/Sql.g:390:5: column_constraint_pk
                    {
                    pushFollow(FOLLOW_column_constraint_pk_in_column_constraint2904);
                    column_constraint_pk323=column_constraint_pk();

                    state._fsp--;

                    stream_column_constraint_pk.add(column_constraint_pk323.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:391:5: column_constraint_not_null
                    {
                    pushFollow(FOLLOW_column_constraint_not_null_in_column_constraint2910);
                    column_constraint_not_null324=column_constraint_not_null();

                    state._fsp--;

                    stream_column_constraint_not_null.add(column_constraint_not_null324.getTree());

                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:392:5: column_constraint_null
                    {
                    pushFollow(FOLLOW_column_constraint_null_in_column_constraint2916);
                    column_constraint_null325=column_constraint_null();

                    state._fsp--;

                    stream_column_constraint_null.add(column_constraint_null325.getTree());

                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:393:5: column_constraint_unique
                    {
                    pushFollow(FOLLOW_column_constraint_unique_in_column_constraint2922);
                    column_constraint_unique326=column_constraint_unique();

                    state._fsp--;

                    stream_column_constraint_unique.add(column_constraint_unique326.getTree());

                    }
                    break;
                case 5 :
                    // sqljet/src/Sql.g:394:5: column_constraint_check
                    {
                    pushFollow(FOLLOW_column_constraint_check_in_column_constraint2928);
                    column_constraint_check327=column_constraint_check();

                    state._fsp--;

                    stream_column_constraint_check.add(column_constraint_check327.getTree());

                    }
                    break;
                case 6 :
                    // sqljet/src/Sql.g:395:5: column_constraint_default
                    {
                    pushFollow(FOLLOW_column_constraint_default_in_column_constraint2934);
                    column_constraint_default328=column_constraint_default();

                    state._fsp--;

                    stream_column_constraint_default.add(column_constraint_default328.getTree());

                    }
                    break;
                case 7 :
                    // sqljet/src/Sql.g:396:5: column_constraint_collate
                    {
                    pushFollow(FOLLOW_column_constraint_collate_in_column_constraint2940);
                    column_constraint_collate329=column_constraint_collate();

                    state._fsp--;

                    stream_column_constraint_collate.add(column_constraint_collate329.getTree());

                    }
                    break;
                case 8 :
                    // sqljet/src/Sql.g:397:5: fk_clause
                    {
                    pushFollow(FOLLOW_fk_clause_in_column_constraint2946);
                    fk_clause330=fk_clause();

                    state._fsp--;

                    stream_fk_clause.add(fk_clause330.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: column_constraint_pk, column_constraint_check, column_constraint_unique, column_constraint_not_null, column_constraint_collate, column_constraint_null, name, fk_clause, column_constraint_default
            // token labels: 
            // rule labels: retval, name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name",name!=null?name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 398:1: -> ^( COLUMN_CONSTRAINT ( column_constraint_pk )? ( column_constraint_not_null )? ( column_constraint_null )? ( column_constraint_unique )? ( column_constraint_check )? ( column_constraint_default )? ( column_constraint_collate )? ( fk_clause )? ( $name)? )
            {
                // sqljet/src/Sql.g:398:4: ^( COLUMN_CONSTRAINT ( column_constraint_pk )? ( column_constraint_not_null )? ( column_constraint_null )? ( column_constraint_unique )? ( column_constraint_check )? ( column_constraint_default )? ( column_constraint_collate )? ( fk_clause )? ( $name)? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMN_CONSTRAINT, "COLUMN_CONSTRAINT"), root_1);

                // sqljet/src/Sql.g:399:3: ( column_constraint_pk )?
                if ( stream_column_constraint_pk.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_pk.nextTree());

                }
                stream_column_constraint_pk.reset();
                // sqljet/src/Sql.g:400:3: ( column_constraint_not_null )?
                if ( stream_column_constraint_not_null.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_not_null.nextTree());

                }
                stream_column_constraint_not_null.reset();
                // sqljet/src/Sql.g:401:3: ( column_constraint_null )?
                if ( stream_column_constraint_null.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_null.nextTree());

                }
                stream_column_constraint_null.reset();
                // sqljet/src/Sql.g:402:3: ( column_constraint_unique )?
                if ( stream_column_constraint_unique.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_unique.nextTree());

                }
                stream_column_constraint_unique.reset();
                // sqljet/src/Sql.g:403:3: ( column_constraint_check )?
                if ( stream_column_constraint_check.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_check.nextTree());

                }
                stream_column_constraint_check.reset();
                // sqljet/src/Sql.g:404:3: ( column_constraint_default )?
                if ( stream_column_constraint_default.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_default.nextTree());

                }
                stream_column_constraint_default.reset();
                // sqljet/src/Sql.g:405:3: ( column_constraint_collate )?
                if ( stream_column_constraint_collate.hasNext() ) {
                    adaptor.addChild(root_1, stream_column_constraint_collate.nextTree());

                }
                stream_column_constraint_collate.reset();
                // sqljet/src/Sql.g:406:3: ( fk_clause )?
                if ( stream_fk_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_fk_clause.nextTree());

                }
                stream_fk_clause.reset();
                // sqljet/src/Sql.g:407:3: ( $name)?
                if ( stream_name.hasNext() ) {
                    adaptor.addChild(root_1, stream_name.nextTree());

                }
                stream_name.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint"

    public static class column_constraint_pk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_pk"
    // sqljet/src/Sql.g:409:1: column_constraint_pk : PRIMARY KEY ( ASC | DESC )? ( table_conflict_clause )? ( AUTOINCREMENT )? ;
    public final SqlParser.column_constraint_pk_return column_constraint_pk() throws RecognitionException {
        SqlParser.column_constraint_pk_return retval = new SqlParser.column_constraint_pk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PRIMARY331=null;
        Token KEY332=null;
        Token set333=null;
        Token AUTOINCREMENT335=null;
        SqlParser.table_conflict_clause_return table_conflict_clause334 = null;


        Object PRIMARY331_tree=null;
        Object KEY332_tree=null;
        Object set333_tree=null;
        Object AUTOINCREMENT335_tree=null;

        try {
            // sqljet/src/Sql.g:409:21: ( PRIMARY KEY ( ASC | DESC )? ( table_conflict_clause )? ( AUTOINCREMENT )? )
            // sqljet/src/Sql.g:409:23: PRIMARY KEY ( ASC | DESC )? ( table_conflict_clause )? ( AUTOINCREMENT )?
            {
            root_0 = (Object)adaptor.nil();

            PRIMARY331=(Token)match(input,PRIMARY,FOLLOW_PRIMARY_in_column_constraint_pk3006); 
            PRIMARY331_tree = (Object)adaptor.create(PRIMARY331);
            root_0 = (Object)adaptor.becomeRoot(PRIMARY331_tree, root_0);

            KEY332=(Token)match(input,KEY,FOLLOW_KEY_in_column_constraint_pk3009); 
            // sqljet/src/Sql.g:409:37: ( ASC | DESC )?
            int alt125=2;
            alt125 = dfa125.predict(input);
            switch (alt125) {
                case 1 :
                    // sqljet/src/Sql.g:
                    {
                    set333=(Token)input.LT(1);
                    if ( (input.LA(1)>=ASC && input.LA(1)<=DESC) ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set333));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            // sqljet/src/Sql.g:409:51: ( table_conflict_clause )?
            int alt126=2;
            alt126 = dfa126.predict(input);
            switch (alt126) {
                case 1 :
                    // sqljet/src/Sql.g:409:51: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_column_constraint_pk3021);
                    table_conflict_clause334=table_conflict_clause();

                    state._fsp--;

                    adaptor.addChild(root_0, table_conflict_clause334.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:409:74: ( AUTOINCREMENT )?
            int alt127=2;
            alt127 = dfa127.predict(input);
            switch (alt127) {
                case 1 :
                    // sqljet/src/Sql.g:409:75: AUTOINCREMENT
                    {
                    AUTOINCREMENT335=(Token)match(input,AUTOINCREMENT,FOLLOW_AUTOINCREMENT_in_column_constraint_pk3025); 
                    AUTOINCREMENT335_tree = (Object)adaptor.create(AUTOINCREMENT335);
                    adaptor.addChild(root_0, AUTOINCREMENT335_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_pk"

    public static class column_constraint_not_null_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_not_null"
    // sqljet/src/Sql.g:411:1: column_constraint_not_null : NOT NULL ( table_conflict_clause )? -> ^( NOT_NULL ( table_conflict_clause )? ) ;
    public final SqlParser.column_constraint_not_null_return column_constraint_not_null() throws RecognitionException {
        SqlParser.column_constraint_not_null_return retval = new SqlParser.column_constraint_not_null_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NOT336=null;
        Token NULL337=null;
        SqlParser.table_conflict_clause_return table_conflict_clause338 = null;


        Object NOT336_tree=null;
        Object NULL337_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_NULL=new RewriteRuleTokenStream(adaptor,"token NULL");
        RewriteRuleSubtreeStream stream_table_conflict_clause=new RewriteRuleSubtreeStream(adaptor,"rule table_conflict_clause");
        try {
            // sqljet/src/Sql.g:411:27: ( NOT NULL ( table_conflict_clause )? -> ^( NOT_NULL ( table_conflict_clause )? ) )
            // sqljet/src/Sql.g:411:29: NOT NULL ( table_conflict_clause )?
            {
            NOT336=(Token)match(input,NOT,FOLLOW_NOT_in_column_constraint_not_null3034);  
            stream_NOT.add(NOT336);

            NULL337=(Token)match(input,NULL,FOLLOW_NULL_in_column_constraint_not_null3036);  
            stream_NULL.add(NULL337);

            // sqljet/src/Sql.g:411:38: ( table_conflict_clause )?
            int alt128=2;
            alt128 = dfa128.predict(input);
            switch (alt128) {
                case 1 :
                    // sqljet/src/Sql.g:411:38: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_column_constraint_not_null3038);
                    table_conflict_clause338=table_conflict_clause();

                    state._fsp--;

                    stream_table_conflict_clause.add(table_conflict_clause338.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: table_conflict_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 411:61: -> ^( NOT_NULL ( table_conflict_clause )? )
            {
                // sqljet/src/Sql.g:411:64: ^( NOT_NULL ( table_conflict_clause )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOT_NULL, "NOT_NULL"), root_1);

                // sqljet/src/Sql.g:411:75: ( table_conflict_clause )?
                if ( stream_table_conflict_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_conflict_clause.nextTree());

                }
                stream_table_conflict_clause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_not_null"

    public static class column_constraint_null_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_null"
    // sqljet/src/Sql.g:413:1: column_constraint_null : NULL ( table_conflict_clause )? -> ^( IS_NULL ( table_conflict_clause )? ) ;
    public final SqlParser.column_constraint_null_return column_constraint_null() throws RecognitionException {
        SqlParser.column_constraint_null_return retval = new SqlParser.column_constraint_null_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NULL339=null;
        SqlParser.table_conflict_clause_return table_conflict_clause340 = null;


        Object NULL339_tree=null;
        RewriteRuleTokenStream stream_NULL=new RewriteRuleTokenStream(adaptor,"token NULL");
        RewriteRuleSubtreeStream stream_table_conflict_clause=new RewriteRuleSubtreeStream(adaptor,"rule table_conflict_clause");
        try {
            // sqljet/src/Sql.g:413:23: ( NULL ( table_conflict_clause )? -> ^( IS_NULL ( table_conflict_clause )? ) )
            // sqljet/src/Sql.g:413:25: NULL ( table_conflict_clause )?
            {
            NULL339=(Token)match(input,NULL,FOLLOW_NULL_in_column_constraint_null3055);  
            stream_NULL.add(NULL339);

            // sqljet/src/Sql.g:413:30: ( table_conflict_clause )?
            int alt129=2;
            alt129 = dfa129.predict(input);
            switch (alt129) {
                case 1 :
                    // sqljet/src/Sql.g:413:30: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_column_constraint_null3057);
                    table_conflict_clause340=table_conflict_clause();

                    state._fsp--;

                    stream_table_conflict_clause.add(table_conflict_clause340.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: table_conflict_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 413:53: -> ^( IS_NULL ( table_conflict_clause )? )
            {
                // sqljet/src/Sql.g:413:56: ^( IS_NULL ( table_conflict_clause )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IS_NULL, "IS_NULL"), root_1);

                // sqljet/src/Sql.g:413:66: ( table_conflict_clause )?
                if ( stream_table_conflict_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_conflict_clause.nextTree());

                }
                stream_table_conflict_clause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_null"

    public static class column_constraint_unique_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_unique"
    // sqljet/src/Sql.g:415:1: column_constraint_unique : UNIQUE ( table_conflict_clause )? ;
    public final SqlParser.column_constraint_unique_return column_constraint_unique() throws RecognitionException {
        SqlParser.column_constraint_unique_return retval = new SqlParser.column_constraint_unique_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token UNIQUE341=null;
        SqlParser.table_conflict_clause_return table_conflict_clause342 = null;


        Object UNIQUE341_tree=null;

        try {
            // sqljet/src/Sql.g:415:25: ( UNIQUE ( table_conflict_clause )? )
            // sqljet/src/Sql.g:415:27: UNIQUE ( table_conflict_clause )?
            {
            root_0 = (Object)adaptor.nil();

            UNIQUE341=(Token)match(input,UNIQUE,FOLLOW_UNIQUE_in_column_constraint_unique3074); 
            UNIQUE341_tree = (Object)adaptor.create(UNIQUE341);
            root_0 = (Object)adaptor.becomeRoot(UNIQUE341_tree, root_0);

            // sqljet/src/Sql.g:415:35: ( table_conflict_clause )?
            int alt130=2;
            alt130 = dfa130.predict(input);
            switch (alt130) {
                case 1 :
                    // sqljet/src/Sql.g:415:35: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_column_constraint_unique3077);
                    table_conflict_clause342=table_conflict_clause();

                    state._fsp--;

                    adaptor.addChild(root_0, table_conflict_clause342.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_unique"

    public static class column_constraint_check_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_check"
    // sqljet/src/Sql.g:417:1: column_constraint_check : CHECK LPAREN expr RPAREN ;
    public final SqlParser.column_constraint_check_return column_constraint_check() throws RecognitionException {
        SqlParser.column_constraint_check_return retval = new SqlParser.column_constraint_check_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHECK343=null;
        Token LPAREN344=null;
        Token RPAREN346=null;
        SqlParser.expr_return expr345 = null;


        Object CHECK343_tree=null;
        Object LPAREN344_tree=null;
        Object RPAREN346_tree=null;

        try {
            // sqljet/src/Sql.g:417:24: ( CHECK LPAREN expr RPAREN )
            // sqljet/src/Sql.g:417:26: CHECK LPAREN expr RPAREN
            {
            root_0 = (Object)adaptor.nil();

            CHECK343=(Token)match(input,CHECK,FOLLOW_CHECK_in_column_constraint_check3085); 
            CHECK343_tree = (Object)adaptor.create(CHECK343);
            root_0 = (Object)adaptor.becomeRoot(CHECK343_tree, root_0);

            LPAREN344=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_column_constraint_check3088); 
            pushFollow(FOLLOW_expr_in_column_constraint_check3091);
            expr345=expr();

            state._fsp--;

            adaptor.addChild(root_0, expr345.getTree());
            RPAREN346=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_column_constraint_check3093); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_check"

    public static class numeric_literal_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numeric_literal_value"
    // sqljet/src/Sql.g:419:1: numeric_literal_value : ( INTEGER -> ^( INTEGER_LITERAL INTEGER ) | FLOAT -> ^( FLOAT_LITERAL FLOAT ) );
    public final SqlParser.numeric_literal_value_return numeric_literal_value() throws RecognitionException {
        SqlParser.numeric_literal_value_return retval = new SqlParser.numeric_literal_value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INTEGER347=null;
        Token FLOAT348=null;

        Object INTEGER347_tree=null;
        Object FLOAT348_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_FLOAT=new RewriteRuleTokenStream(adaptor,"token FLOAT");

        try {
            // sqljet/src/Sql.g:420:3: ( INTEGER -> ^( INTEGER_LITERAL INTEGER ) | FLOAT -> ^( FLOAT_LITERAL FLOAT ) )
            int alt131=2;
            int LA131_0 = input.LA(1);

            if ( (LA131_0==INTEGER) ) {
                alt131=1;
            }
            else if ( (LA131_0==FLOAT) ) {
                alt131=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 131, 0, input);

                throw nvae;
            }
            switch (alt131) {
                case 1 :
                    // sqljet/src/Sql.g:420:5: INTEGER
                    {
                    INTEGER347=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_numeric_literal_value3104);  
                    stream_INTEGER.add(INTEGER347);



                    // AST REWRITE
                    // elements: INTEGER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 420:13: -> ^( INTEGER_LITERAL INTEGER )
                    {
                        // sqljet/src/Sql.g:420:16: ^( INTEGER_LITERAL INTEGER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INTEGER_LITERAL, "INTEGER_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:421:5: FLOAT
                    {
                    FLOAT348=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numeric_literal_value3118);  
                    stream_FLOAT.add(FLOAT348);



                    // AST REWRITE
                    // elements: FLOAT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 421:11: -> ^( FLOAT_LITERAL FLOAT )
                    {
                        // sqljet/src/Sql.g:421:14: ^( FLOAT_LITERAL FLOAT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FLOAT_LITERAL, "FLOAT_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_FLOAT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numeric_literal_value"

    public static class signed_default_number_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "signed_default_number"
    // sqljet/src/Sql.g:424:1: signed_default_number : ( PLUS | MINUS ) numeric_literal_value ;
    public final SqlParser.signed_default_number_return signed_default_number() throws RecognitionException {
        SqlParser.signed_default_number_return retval = new SqlParser.signed_default_number_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set349=null;
        SqlParser.numeric_literal_value_return numeric_literal_value350 = null;


        Object set349_tree=null;

        try {
            // sqljet/src/Sql.g:424:22: ( ( PLUS | MINUS ) numeric_literal_value )
            // sqljet/src/Sql.g:424:24: ( PLUS | MINUS ) numeric_literal_value
            {
            root_0 = (Object)adaptor.nil();

            set349=(Token)input.LT(1);
            set349=(Token)input.LT(1);
            if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
                input.consume();
                root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set349), root_0);
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            pushFollow(FOLLOW_numeric_literal_value_in_signed_default_number3145);
            numeric_literal_value350=numeric_literal_value();

            state._fsp--;

            adaptor.addChild(root_0, numeric_literal_value350.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "signed_default_number"

    public static class column_constraint_default_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_default"
    // sqljet/src/Sql.g:427:1: column_constraint_default : DEFAULT ( signed_default_number | literal_value | LPAREN expr RPAREN ) ;
    public final SqlParser.column_constraint_default_return column_constraint_default() throws RecognitionException {
        SqlParser.column_constraint_default_return retval = new SqlParser.column_constraint_default_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DEFAULT351=null;
        Token LPAREN354=null;
        Token RPAREN356=null;
        SqlParser.signed_default_number_return signed_default_number352 = null;

        SqlParser.literal_value_return literal_value353 = null;

        SqlParser.expr_return expr355 = null;


        Object DEFAULT351_tree=null;
        Object LPAREN354_tree=null;
        Object RPAREN356_tree=null;

        try {
            // sqljet/src/Sql.g:427:26: ( DEFAULT ( signed_default_number | literal_value | LPAREN expr RPAREN ) )
            // sqljet/src/Sql.g:427:28: DEFAULT ( signed_default_number | literal_value | LPAREN expr RPAREN )
            {
            root_0 = (Object)adaptor.nil();

            DEFAULT351=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_column_constraint_default3153); 
            DEFAULT351_tree = (Object)adaptor.create(DEFAULT351);
            root_0 = (Object)adaptor.becomeRoot(DEFAULT351_tree, root_0);

            // sqljet/src/Sql.g:427:37: ( signed_default_number | literal_value | LPAREN expr RPAREN )
            int alt132=3;
            alt132 = dfa132.predict(input);
            switch (alt132) {
                case 1 :
                    // sqljet/src/Sql.g:427:38: signed_default_number
                    {
                    pushFollow(FOLLOW_signed_default_number_in_column_constraint_default3157);
                    signed_default_number352=signed_default_number();

                    state._fsp--;

                    adaptor.addChild(root_0, signed_default_number352.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:427:62: literal_value
                    {
                    pushFollow(FOLLOW_literal_value_in_column_constraint_default3161);
                    literal_value353=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, literal_value353.getTree());

                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:427:78: LPAREN expr RPAREN
                    {
                    LPAREN354=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_column_constraint_default3165); 
                    pushFollow(FOLLOW_expr_in_column_constraint_default3168);
                    expr355=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr355.getTree());
                    RPAREN356=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_column_constraint_default3170); 

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_default"

    public static class column_constraint_collate_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "column_constraint_collate"
    // sqljet/src/Sql.g:429:1: column_constraint_collate : COLLATE collation_name= id ;
    public final SqlParser.column_constraint_collate_return column_constraint_collate() throws RecognitionException {
        SqlParser.column_constraint_collate_return retval = new SqlParser.column_constraint_collate_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLLATE357=null;
        SqlParser.id_return collation_name = null;


        Object COLLATE357_tree=null;

        try {
            // sqljet/src/Sql.g:429:26: ( COLLATE collation_name= id )
            // sqljet/src/Sql.g:429:28: COLLATE collation_name= id
            {
            root_0 = (Object)adaptor.nil();

            COLLATE357=(Token)match(input,COLLATE,FOLLOW_COLLATE_in_column_constraint_collate3179); 
            COLLATE357_tree = (Object)adaptor.create(COLLATE357);
            root_0 = (Object)adaptor.becomeRoot(COLLATE357_tree, root_0);

            pushFollow(FOLLOW_id_in_column_constraint_collate3184);
            collation_name=id();

            state._fsp--;

            adaptor.addChild(root_0, collation_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "column_constraint_collate"

    public static class table_constraint_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_constraint"
    // sqljet/src/Sql.g:431:1: table_constraint : ( CONSTRAINT name= id )? ( table_constraint_pk | table_constraint_unique | table_constraint_check | table_constraint_fk ) -> ^( TABLE_CONSTRAINT ( table_constraint_pk )? ( table_constraint_unique )? ( table_constraint_check )? ( table_constraint_fk )? ( $name)? ) ;
    public final SqlParser.table_constraint_return table_constraint() throws RecognitionException {
        SqlParser.table_constraint_return retval = new SqlParser.table_constraint_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CONSTRAINT358=null;
        SqlParser.id_return name = null;

        SqlParser.table_constraint_pk_return table_constraint_pk359 = null;

        SqlParser.table_constraint_unique_return table_constraint_unique360 = null;

        SqlParser.table_constraint_check_return table_constraint_check361 = null;

        SqlParser.table_constraint_fk_return table_constraint_fk362 = null;


        Object CONSTRAINT358_tree=null;
        RewriteRuleTokenStream stream_CONSTRAINT=new RewriteRuleTokenStream(adaptor,"token CONSTRAINT");
        RewriteRuleSubtreeStream stream_table_constraint_pk=new RewriteRuleSubtreeStream(adaptor,"rule table_constraint_pk");
        RewriteRuleSubtreeStream stream_table_constraint_fk=new RewriteRuleSubtreeStream(adaptor,"rule table_constraint_fk");
        RewriteRuleSubtreeStream stream_table_constraint_check=new RewriteRuleSubtreeStream(adaptor,"rule table_constraint_check");
        RewriteRuleSubtreeStream stream_table_constraint_unique=new RewriteRuleSubtreeStream(adaptor,"rule table_constraint_unique");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:431:17: ( ( CONSTRAINT name= id )? ( table_constraint_pk | table_constraint_unique | table_constraint_check | table_constraint_fk ) -> ^( TABLE_CONSTRAINT ( table_constraint_pk )? ( table_constraint_unique )? ( table_constraint_check )? ( table_constraint_fk )? ( $name)? ) )
            // sqljet/src/Sql.g:431:19: ( CONSTRAINT name= id )? ( table_constraint_pk | table_constraint_unique | table_constraint_check | table_constraint_fk )
            {
            // sqljet/src/Sql.g:431:19: ( CONSTRAINT name= id )?
            int alt133=2;
            int LA133_0 = input.LA(1);

            if ( (LA133_0==CONSTRAINT) ) {
                alt133=1;
            }
            switch (alt133) {
                case 1 :
                    // sqljet/src/Sql.g:431:20: CONSTRAINT name= id
                    {
                    CONSTRAINT358=(Token)match(input,CONSTRAINT,FOLLOW_CONSTRAINT_in_table_constraint3193);  
                    stream_CONSTRAINT.add(CONSTRAINT358);

                    pushFollow(FOLLOW_id_in_table_constraint3197);
                    name=id();

                    state._fsp--;

                    stream_id.add(name.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:432:3: ( table_constraint_pk | table_constraint_unique | table_constraint_check | table_constraint_fk )
            int alt134=4;
            switch ( input.LA(1) ) {
            case PRIMARY:
                {
                alt134=1;
                }
                break;
            case UNIQUE:
                {
                alt134=2;
                }
                break;
            case CHECK:
                {
                alt134=3;
                }
                break;
            case FOREIGN:
                {
                alt134=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 134, 0, input);

                throw nvae;
            }

            switch (alt134) {
                case 1 :
                    // sqljet/src/Sql.g:432:5: table_constraint_pk
                    {
                    pushFollow(FOLLOW_table_constraint_pk_in_table_constraint3205);
                    table_constraint_pk359=table_constraint_pk();

                    state._fsp--;

                    stream_table_constraint_pk.add(table_constraint_pk359.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:433:5: table_constraint_unique
                    {
                    pushFollow(FOLLOW_table_constraint_unique_in_table_constraint3211);
                    table_constraint_unique360=table_constraint_unique();

                    state._fsp--;

                    stream_table_constraint_unique.add(table_constraint_unique360.getTree());

                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:434:5: table_constraint_check
                    {
                    pushFollow(FOLLOW_table_constraint_check_in_table_constraint3217);
                    table_constraint_check361=table_constraint_check();

                    state._fsp--;

                    stream_table_constraint_check.add(table_constraint_check361.getTree());

                    }
                    break;
                case 4 :
                    // sqljet/src/Sql.g:435:5: table_constraint_fk
                    {
                    pushFollow(FOLLOW_table_constraint_fk_in_table_constraint3223);
                    table_constraint_fk362=table_constraint_fk();

                    state._fsp--;

                    stream_table_constraint_fk.add(table_constraint_fk362.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: name, table_constraint_check, table_constraint_unique, table_constraint_pk, table_constraint_fk
            // token labels: 
            // rule labels: retval, name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name",name!=null?name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 436:1: -> ^( TABLE_CONSTRAINT ( table_constraint_pk )? ( table_constraint_unique )? ( table_constraint_check )? ( table_constraint_fk )? ( $name)? )
            {
                // sqljet/src/Sql.g:436:4: ^( TABLE_CONSTRAINT ( table_constraint_pk )? ( table_constraint_unique )? ( table_constraint_check )? ( table_constraint_fk )? ( $name)? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TABLE_CONSTRAINT, "TABLE_CONSTRAINT"), root_1);

                // sqljet/src/Sql.g:437:3: ( table_constraint_pk )?
                if ( stream_table_constraint_pk.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_constraint_pk.nextTree());

                }
                stream_table_constraint_pk.reset();
                // sqljet/src/Sql.g:438:3: ( table_constraint_unique )?
                if ( stream_table_constraint_unique.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_constraint_unique.nextTree());

                }
                stream_table_constraint_unique.reset();
                // sqljet/src/Sql.g:439:3: ( table_constraint_check )?
                if ( stream_table_constraint_check.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_constraint_check.nextTree());

                }
                stream_table_constraint_check.reset();
                // sqljet/src/Sql.g:440:3: ( table_constraint_fk )?
                if ( stream_table_constraint_fk.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_constraint_fk.nextTree());

                }
                stream_table_constraint_fk.reset();
                // sqljet/src/Sql.g:441:3: ( $name)?
                if ( stream_name.hasNext() ) {
                    adaptor.addChild(root_1, stream_name.nextTree());

                }
                stream_name.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_constraint"

    public static class table_constraint_pk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_constraint_pk"
    // sqljet/src/Sql.g:443:1: table_constraint_pk : PRIMARY KEY LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )? -> ^( PRIMARY ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? ) ;
    public final SqlParser.table_constraint_pk_return table_constraint_pk() throws RecognitionException {
        SqlParser.table_constraint_pk_return retval = new SqlParser.table_constraint_pk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PRIMARY363=null;
        Token KEY364=null;
        Token LPAREN365=null;
        Token COMMA366=null;
        Token RPAREN367=null;
        List list_indexed_columns=null;
        SqlParser.table_conflict_clause_return table_conflict_clause368 = null;

        SqlParser.id_return indexed_columns = null;
         indexed_columns = null;
        Object PRIMARY363_tree=null;
        Object KEY364_tree=null;
        Object LPAREN365_tree=null;
        Object COMMA366_tree=null;
        Object RPAREN367_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_PRIMARY=new RewriteRuleTokenStream(adaptor,"token PRIMARY");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KEY=new RewriteRuleTokenStream(adaptor,"token KEY");
        RewriteRuleSubtreeStream stream_table_conflict_clause=new RewriteRuleSubtreeStream(adaptor,"rule table_conflict_clause");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:443:20: ( PRIMARY KEY LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )? -> ^( PRIMARY ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? ) )
            // sqljet/src/Sql.g:443:22: PRIMARY KEY LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )?
            {
            PRIMARY363=(Token)match(input,PRIMARY,FOLLOW_PRIMARY_in_table_constraint_pk3263);  
            stream_PRIMARY.add(PRIMARY363);

            KEY364=(Token)match(input,KEY,FOLLOW_KEY_in_table_constraint_pk3265);  
            stream_KEY.add(KEY364);

            LPAREN365=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_table_constraint_pk3269);  
            stream_LPAREN.add(LPAREN365);

            pushFollow(FOLLOW_id_in_table_constraint_pk3273);
            indexed_columns=id();

            state._fsp--;

            stream_id.add(indexed_columns.getTree());
            if (list_indexed_columns==null) list_indexed_columns=new ArrayList();
            list_indexed_columns.add(indexed_columns.getTree());

            // sqljet/src/Sql.g:444:30: ( COMMA indexed_columns+= id )*
            loop135:
            do {
                int alt135=2;
                int LA135_0 = input.LA(1);

                if ( (LA135_0==COMMA) ) {
                    alt135=1;
                }


                switch (alt135) {
            	case 1 :
            	    // sqljet/src/Sql.g:444:31: COMMA indexed_columns+= id
            	    {
            	    COMMA366=(Token)match(input,COMMA,FOLLOW_COMMA_in_table_constraint_pk3276);  
            	    stream_COMMA.add(COMMA366);

            	    pushFollow(FOLLOW_id_in_table_constraint_pk3280);
            	    indexed_columns=id();

            	    state._fsp--;

            	    stream_id.add(indexed_columns.getTree());
            	    if (list_indexed_columns==null) list_indexed_columns=new ArrayList();
            	    list_indexed_columns.add(indexed_columns.getTree());


            	    }
            	    break;

            	default :
            	    break loop135;
                }
            } while (true);

            RPAREN367=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_table_constraint_pk3284);  
            stream_RPAREN.add(RPAREN367);

            // sqljet/src/Sql.g:444:66: ( table_conflict_clause )?
            int alt136=2;
            int LA136_0 = input.LA(1);

            if ( (LA136_0==ON) ) {
                alt136=1;
            }
            switch (alt136) {
                case 1 :
                    // sqljet/src/Sql.g:444:66: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_table_constraint_pk3286);
                    table_conflict_clause368=table_conflict_clause();

                    state._fsp--;

                    stream_table_conflict_clause.add(table_conflict_clause368.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: table_conflict_clause, indexed_columns, PRIMARY
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: indexed_columns
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_indexed_columns=new RewriteRuleSubtreeStream(adaptor,"token indexed_columns",list_indexed_columns);
            root_0 = (Object)adaptor.nil();
            // 445:1: -> ^( PRIMARY ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? )
            {
                // sqljet/src/Sql.g:445:4: ^( PRIMARY ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_PRIMARY.nextNode(), root_1);

                // sqljet/src/Sql.g:445:14: ^( COLUMNS ( $indexed_columns)+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                if ( !(stream_indexed_columns.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_indexed_columns.hasNext() ) {
                    adaptor.addChild(root_2, stream_indexed_columns.nextTree());

                }
                stream_indexed_columns.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:445:43: ( table_conflict_clause )?
                if ( stream_table_conflict_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_conflict_clause.nextTree());

                }
                stream_table_conflict_clause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_constraint_pk"

    public static class table_constraint_unique_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_constraint_unique"
    // sqljet/src/Sql.g:447:1: table_constraint_unique : UNIQUE LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )? -> ^( UNIQUE ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? ) ;
    public final SqlParser.table_constraint_unique_return table_constraint_unique() throws RecognitionException {
        SqlParser.table_constraint_unique_return retval = new SqlParser.table_constraint_unique_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token UNIQUE369=null;
        Token LPAREN370=null;
        Token COMMA371=null;
        Token RPAREN372=null;
        List list_indexed_columns=null;
        SqlParser.table_conflict_clause_return table_conflict_clause373 = null;

        SqlParser.id_return indexed_columns = null;
         indexed_columns = null;
        Object UNIQUE369_tree=null;
        Object LPAREN370_tree=null;
        Object COMMA371_tree=null;
        Object RPAREN372_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_UNIQUE=new RewriteRuleTokenStream(adaptor,"token UNIQUE");
        RewriteRuleSubtreeStream stream_table_conflict_clause=new RewriteRuleSubtreeStream(adaptor,"rule table_conflict_clause");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:447:24: ( UNIQUE LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )? -> ^( UNIQUE ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? ) )
            // sqljet/src/Sql.g:447:26: UNIQUE LPAREN indexed_columns+= id ( COMMA indexed_columns+= id )* RPAREN ( table_conflict_clause )?
            {
            UNIQUE369=(Token)match(input,UNIQUE,FOLLOW_UNIQUE_in_table_constraint_unique3311);  
            stream_UNIQUE.add(UNIQUE369);

            LPAREN370=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_table_constraint_unique3315);  
            stream_LPAREN.add(LPAREN370);

            pushFollow(FOLLOW_id_in_table_constraint_unique3319);
            indexed_columns=id();

            state._fsp--;

            stream_id.add(indexed_columns.getTree());
            if (list_indexed_columns==null) list_indexed_columns=new ArrayList();
            list_indexed_columns.add(indexed_columns.getTree());

            // sqljet/src/Sql.g:448:30: ( COMMA indexed_columns+= id )*
            loop137:
            do {
                int alt137=2;
                int LA137_0 = input.LA(1);

                if ( (LA137_0==COMMA) ) {
                    alt137=1;
                }


                switch (alt137) {
            	case 1 :
            	    // sqljet/src/Sql.g:448:31: COMMA indexed_columns+= id
            	    {
            	    COMMA371=(Token)match(input,COMMA,FOLLOW_COMMA_in_table_constraint_unique3322);  
            	    stream_COMMA.add(COMMA371);

            	    pushFollow(FOLLOW_id_in_table_constraint_unique3326);
            	    indexed_columns=id();

            	    state._fsp--;

            	    stream_id.add(indexed_columns.getTree());
            	    if (list_indexed_columns==null) list_indexed_columns=new ArrayList();
            	    list_indexed_columns.add(indexed_columns.getTree());


            	    }
            	    break;

            	default :
            	    break loop137;
                }
            } while (true);

            RPAREN372=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_table_constraint_unique3330);  
            stream_RPAREN.add(RPAREN372);

            // sqljet/src/Sql.g:448:66: ( table_conflict_clause )?
            int alt138=2;
            int LA138_0 = input.LA(1);

            if ( (LA138_0==ON) ) {
                alt138=1;
            }
            switch (alt138) {
                case 1 :
                    // sqljet/src/Sql.g:448:66: table_conflict_clause
                    {
                    pushFollow(FOLLOW_table_conflict_clause_in_table_constraint_unique3332);
                    table_conflict_clause373=table_conflict_clause();

                    state._fsp--;

                    stream_table_conflict_clause.add(table_conflict_clause373.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: indexed_columns, UNIQUE, table_conflict_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: indexed_columns
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_indexed_columns=new RewriteRuleSubtreeStream(adaptor,"token indexed_columns",list_indexed_columns);
            root_0 = (Object)adaptor.nil();
            // 449:1: -> ^( UNIQUE ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? )
            {
                // sqljet/src/Sql.g:449:4: ^( UNIQUE ^( COLUMNS ( $indexed_columns)+ ) ( table_conflict_clause )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_UNIQUE.nextNode(), root_1);

                // sqljet/src/Sql.g:449:13: ^( COLUMNS ( $indexed_columns)+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                if ( !(stream_indexed_columns.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_indexed_columns.hasNext() ) {
                    adaptor.addChild(root_2, stream_indexed_columns.nextTree());

                }
                stream_indexed_columns.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:449:42: ( table_conflict_clause )?
                if ( stream_table_conflict_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_table_conflict_clause.nextTree());

                }
                stream_table_conflict_clause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_constraint_unique"

    public static class table_constraint_check_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_constraint_check"
    // sqljet/src/Sql.g:451:1: table_constraint_check : CHECK LPAREN expr RPAREN ;
    public final SqlParser.table_constraint_check_return table_constraint_check() throws RecognitionException {
        SqlParser.table_constraint_check_return retval = new SqlParser.table_constraint_check_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHECK374=null;
        Token LPAREN375=null;
        Token RPAREN377=null;
        SqlParser.expr_return expr376 = null;


        Object CHECK374_tree=null;
        Object LPAREN375_tree=null;
        Object RPAREN377_tree=null;

        try {
            // sqljet/src/Sql.g:451:23: ( CHECK LPAREN expr RPAREN )
            // sqljet/src/Sql.g:451:25: CHECK LPAREN expr RPAREN
            {
            root_0 = (Object)adaptor.nil();

            CHECK374=(Token)match(input,CHECK,FOLLOW_CHECK_in_table_constraint_check3357); 
            CHECK374_tree = (Object)adaptor.create(CHECK374);
            root_0 = (Object)adaptor.becomeRoot(CHECK374_tree, root_0);

            LPAREN375=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_table_constraint_check3360); 
            pushFollow(FOLLOW_expr_in_table_constraint_check3363);
            expr376=expr();

            state._fsp--;

            adaptor.addChild(root_0, expr376.getTree());
            RPAREN377=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_table_constraint_check3365); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_constraint_check"

    public static class table_constraint_fk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "table_constraint_fk"
    // sqljet/src/Sql.g:453:1: table_constraint_fk : FOREIGN KEY LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN fk_clause -> ^( FOREIGN ^( COLUMNS ( $column_names)+ ) fk_clause ) ;
    public final SqlParser.table_constraint_fk_return table_constraint_fk() throws RecognitionException {
        SqlParser.table_constraint_fk_return retval = new SqlParser.table_constraint_fk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FOREIGN378=null;
        Token KEY379=null;
        Token LPAREN380=null;
        Token COMMA381=null;
        Token RPAREN382=null;
        List list_column_names=null;
        SqlParser.fk_clause_return fk_clause383 = null;

        SqlParser.id_return column_names = null;
         column_names = null;
        Object FOREIGN378_tree=null;
        Object KEY379_tree=null;
        Object LPAREN380_tree=null;
        Object COMMA381_tree=null;
        Object RPAREN382_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_FOREIGN=new RewriteRuleTokenStream(adaptor,"token FOREIGN");
        RewriteRuleTokenStream stream_KEY=new RewriteRuleTokenStream(adaptor,"token KEY");
        RewriteRuleSubtreeStream stream_fk_clause=new RewriteRuleSubtreeStream(adaptor,"rule fk_clause");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:453:20: ( FOREIGN KEY LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN fk_clause -> ^( FOREIGN ^( COLUMNS ( $column_names)+ ) fk_clause ) )
            // sqljet/src/Sql.g:453:22: FOREIGN KEY LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN fk_clause
            {
            FOREIGN378=(Token)match(input,FOREIGN,FOLLOW_FOREIGN_in_table_constraint_fk3373);  
            stream_FOREIGN.add(FOREIGN378);

            KEY379=(Token)match(input,KEY,FOLLOW_KEY_in_table_constraint_fk3375);  
            stream_KEY.add(KEY379);

            LPAREN380=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_table_constraint_fk3377);  
            stream_LPAREN.add(LPAREN380);

            pushFollow(FOLLOW_id_in_table_constraint_fk3381);
            column_names=id();

            state._fsp--;

            stream_id.add(column_names.getTree());
            if (list_column_names==null) list_column_names=new ArrayList();
            list_column_names.add(column_names.getTree());

            // sqljet/src/Sql.g:453:58: ( COMMA column_names+= id )*
            loop139:
            do {
                int alt139=2;
                int LA139_0 = input.LA(1);

                if ( (LA139_0==COMMA) ) {
                    alt139=1;
                }


                switch (alt139) {
            	case 1 :
            	    // sqljet/src/Sql.g:453:59: COMMA column_names+= id
            	    {
            	    COMMA381=(Token)match(input,COMMA,FOLLOW_COMMA_in_table_constraint_fk3384);  
            	    stream_COMMA.add(COMMA381);

            	    pushFollow(FOLLOW_id_in_table_constraint_fk3388);
            	    column_names=id();

            	    state._fsp--;

            	    stream_id.add(column_names.getTree());
            	    if (list_column_names==null) list_column_names=new ArrayList();
            	    list_column_names.add(column_names.getTree());


            	    }
            	    break;

            	default :
            	    break loop139;
                }
            } while (true);

            RPAREN382=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_table_constraint_fk3392);  
            stream_RPAREN.add(RPAREN382);

            pushFollow(FOLLOW_fk_clause_in_table_constraint_fk3394);
            fk_clause383=fk_clause();

            state._fsp--;

            stream_fk_clause.add(fk_clause383.getTree());


            // AST REWRITE
            // elements: column_names, fk_clause, FOREIGN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: column_names
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_column_names=new RewriteRuleSubtreeStream(adaptor,"token column_names",list_column_names);
            root_0 = (Object)adaptor.nil();
            // 454:1: -> ^( FOREIGN ^( COLUMNS ( $column_names)+ ) fk_clause )
            {
                // sqljet/src/Sql.g:454:4: ^( FOREIGN ^( COLUMNS ( $column_names)+ ) fk_clause )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_FOREIGN.nextNode(), root_1);

                // sqljet/src/Sql.g:454:14: ^( COLUMNS ( $column_names)+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                if ( !(stream_column_names.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_column_names.hasNext() ) {
                    adaptor.addChild(root_2, stream_column_names.nextTree());

                }
                stream_column_names.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_fk_clause.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "table_constraint_fk"

    public static class fk_clause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fk_clause"
    // sqljet/src/Sql.g:456:1: fk_clause : REFERENCES foreign_table= id ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( fk_clause_action )* ( fk_clause_deferrable )? -> ^( REFERENCES $foreign_table ^( COLUMNS ( $column_names)+ ) ( fk_clause_action )* ( fk_clause_deferrable )? ) ;
    public final SqlParser.fk_clause_return fk_clause() throws RecognitionException {
        SqlParser.fk_clause_return retval = new SqlParser.fk_clause_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token REFERENCES384=null;
        Token LPAREN385=null;
        Token COMMA386=null;
        Token RPAREN387=null;
        List list_column_names=null;
        SqlParser.id_return foreign_table = null;

        SqlParser.fk_clause_action_return fk_clause_action388 = null;

        SqlParser.fk_clause_deferrable_return fk_clause_deferrable389 = null;

        SqlParser.id_return column_names = null;
         column_names = null;
        Object REFERENCES384_tree=null;
        Object LPAREN385_tree=null;
        Object COMMA386_tree=null;
        Object RPAREN387_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_REFERENCES=new RewriteRuleTokenStream(adaptor,"token REFERENCES");
        RewriteRuleSubtreeStream stream_fk_clause_deferrable=new RewriteRuleSubtreeStream(adaptor,"rule fk_clause_deferrable");
        RewriteRuleSubtreeStream stream_fk_clause_action=new RewriteRuleSubtreeStream(adaptor,"rule fk_clause_action");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:456:10: ( REFERENCES foreign_table= id ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( fk_clause_action )* ( fk_clause_deferrable )? -> ^( REFERENCES $foreign_table ^( COLUMNS ( $column_names)+ ) ( fk_clause_action )* ( fk_clause_deferrable )? ) )
            // sqljet/src/Sql.g:456:12: REFERENCES foreign_table= id ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )? ( fk_clause_action )* ( fk_clause_deferrable )?
            {
            REFERENCES384=(Token)match(input,REFERENCES,FOLLOW_REFERENCES_in_fk_clause3417);  
            stream_REFERENCES.add(REFERENCES384);

            pushFollow(FOLLOW_id_in_fk_clause3421);
            foreign_table=id();

            state._fsp--;

            stream_id.add(foreign_table.getTree());
            // sqljet/src/Sql.g:456:40: ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )?
            int alt141=2;
            alt141 = dfa141.predict(input);
            switch (alt141) {
                case 1 :
                    // sqljet/src/Sql.g:456:41: LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN
                    {
                    LPAREN385=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_fk_clause3424);  
                    stream_LPAREN.add(LPAREN385);

                    pushFollow(FOLLOW_id_in_fk_clause3428);
                    column_names=id();

                    state._fsp--;

                    stream_id.add(column_names.getTree());
                    if (list_column_names==null) list_column_names=new ArrayList();
                    list_column_names.add(column_names.getTree());

                    // sqljet/src/Sql.g:456:65: ( COMMA column_names+= id )*
                    loop140:
                    do {
                        int alt140=2;
                        int LA140_0 = input.LA(1);

                        if ( (LA140_0==COMMA) ) {
                            alt140=1;
                        }


                        switch (alt140) {
                    	case 1 :
                    	    // sqljet/src/Sql.g:456:66: COMMA column_names+= id
                    	    {
                    	    COMMA386=(Token)match(input,COMMA,FOLLOW_COMMA_in_fk_clause3431);  
                    	    stream_COMMA.add(COMMA386);

                    	    pushFollow(FOLLOW_id_in_fk_clause3435);
                    	    column_names=id();

                    	    state._fsp--;

                    	    stream_id.add(column_names.getTree());
                    	    if (list_column_names==null) list_column_names=new ArrayList();
                    	    list_column_names.add(column_names.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop140;
                        }
                    } while (true);

                    RPAREN387=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_fk_clause3439);  
                    stream_RPAREN.add(RPAREN387);


                    }
                    break;

            }

            // sqljet/src/Sql.g:457:3: ( fk_clause_action )*
            loop142:
            do {
                int alt142=2;
                alt142 = dfa142.predict(input);
                switch (alt142) {
            	case 1 :
            	    // sqljet/src/Sql.g:457:3: fk_clause_action
            	    {
            	    pushFollow(FOLLOW_fk_clause_action_in_fk_clause3445);
            	    fk_clause_action388=fk_clause_action();

            	    state._fsp--;

            	    stream_fk_clause_action.add(fk_clause_action388.getTree());

            	    }
            	    break;

            	default :
            	    break loop142;
                }
            } while (true);

            // sqljet/src/Sql.g:457:21: ( fk_clause_deferrable )?
            int alt143=2;
            alt143 = dfa143.predict(input);
            switch (alt143) {
                case 1 :
                    // sqljet/src/Sql.g:457:21: fk_clause_deferrable
                    {
                    pushFollow(FOLLOW_fk_clause_deferrable_in_fk_clause3448);
                    fk_clause_deferrable389=fk_clause_deferrable();

                    state._fsp--;

                    stream_fk_clause_deferrable.add(fk_clause_deferrable389.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: fk_clause_deferrable, column_names, foreign_table, fk_clause_action, REFERENCES
            // token labels: 
            // rule labels: foreign_table, retval
            // token list labels: 
            // rule list labels: column_names
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_foreign_table=new RewriteRuleSubtreeStream(adaptor,"rule foreign_table",foreign_table!=null?foreign_table.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_column_names=new RewriteRuleSubtreeStream(adaptor,"token column_names",list_column_names);
            root_0 = (Object)adaptor.nil();
            // 458:1: -> ^( REFERENCES $foreign_table ^( COLUMNS ( $column_names)+ ) ( fk_clause_action )* ( fk_clause_deferrable )? )
            {
                // sqljet/src/Sql.g:458:4: ^( REFERENCES $foreign_table ^( COLUMNS ( $column_names)+ ) ( fk_clause_action )* ( fk_clause_deferrable )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_REFERENCES.nextNode(), root_1);

                adaptor.addChild(root_1, stream_foreign_table.nextTree());
                // sqljet/src/Sql.g:458:32: ^( COLUMNS ( $column_names)+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                if ( !(stream_column_names.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_column_names.hasNext() ) {
                    adaptor.addChild(root_2, stream_column_names.nextTree());

                }
                stream_column_names.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:458:58: ( fk_clause_action )*
                while ( stream_fk_clause_action.hasNext() ) {
                    adaptor.addChild(root_1, stream_fk_clause_action.nextTree());

                }
                stream_fk_clause_action.reset();
                // sqljet/src/Sql.g:458:76: ( fk_clause_deferrable )?
                if ( stream_fk_clause_deferrable.hasNext() ) {
                    adaptor.addChild(root_1, stream_fk_clause_deferrable.nextTree());

                }
                stream_fk_clause_deferrable.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fk_clause"

    public static class fk_clause_action_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fk_clause_action"
    // sqljet/src/Sql.g:460:1: fk_clause_action : ( ON ( DELETE | UPDATE | INSERT ) ( SET NULL | SET DEFAULT | CASCADE | RESTRICT ) | MATCH id );
    public final SqlParser.fk_clause_action_return fk_clause_action() throws RecognitionException {
        SqlParser.fk_clause_action_return retval = new SqlParser.fk_clause_action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ON390=null;
        Token set391=null;
        Token SET392=null;
        Token NULL393=null;
        Token SET394=null;
        Token DEFAULT395=null;
        Token CASCADE396=null;
        Token RESTRICT397=null;
        Token MATCH398=null;
        SqlParser.id_return id399 = null;


        Object ON390_tree=null;
        Object set391_tree=null;
        Object SET392_tree=null;
        Object NULL393_tree=null;
        Object SET394_tree=null;
        Object DEFAULT395_tree=null;
        Object CASCADE396_tree=null;
        Object RESTRICT397_tree=null;
        Object MATCH398_tree=null;

        try {
            // sqljet/src/Sql.g:461:3: ( ON ( DELETE | UPDATE | INSERT ) ( SET NULL | SET DEFAULT | CASCADE | RESTRICT ) | MATCH id )
            int alt145=2;
            int LA145_0 = input.LA(1);

            if ( (LA145_0==ON) ) {
                alt145=1;
            }
            else if ( (LA145_0==MATCH) ) {
                alt145=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 145, 0, input);

                throw nvae;
            }
            switch (alt145) {
                case 1 :
                    // sqljet/src/Sql.g:461:5: ON ( DELETE | UPDATE | INSERT ) ( SET NULL | SET DEFAULT | CASCADE | RESTRICT )
                    {
                    root_0 = (Object)adaptor.nil();

                    ON390=(Token)match(input,ON,FOLLOW_ON_in_fk_clause_action3482); 
                    ON390_tree = (Object)adaptor.create(ON390);
                    root_0 = (Object)adaptor.becomeRoot(ON390_tree, root_0);

                    set391=(Token)input.LT(1);
                    if ( input.LA(1)==INSERT||input.LA(1)==UPDATE||input.LA(1)==DELETE ) {
                        input.consume();
                        adaptor.addChild(root_0, (Object)adaptor.create(set391));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // sqljet/src/Sql.g:461:36: ( SET NULL | SET DEFAULT | CASCADE | RESTRICT )
                    int alt144=4;
                    switch ( input.LA(1) ) {
                    case SET:
                        {
                        int LA144_1 = input.LA(2);

                        if ( (LA144_1==NULL) ) {
                            alt144=1;
                        }
                        else if ( (LA144_1==DEFAULT) ) {
                            alt144=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 144, 1, input);

                            throw nvae;
                        }
                        }
                        break;
                    case CASCADE:
                        {
                        alt144=3;
                        }
                        break;
                    case RESTRICT:
                        {
                        alt144=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 144, 0, input);

                        throw nvae;
                    }

                    switch (alt144) {
                        case 1 :
                            // sqljet/src/Sql.g:461:37: SET NULL
                            {
                            SET392=(Token)match(input,SET,FOLLOW_SET_in_fk_clause_action3498); 
                            NULL393=(Token)match(input,NULL,FOLLOW_NULL_in_fk_clause_action3501); 
                            NULL393_tree = (Object)adaptor.create(NULL393);
                            adaptor.addChild(root_0, NULL393_tree);


                            }
                            break;
                        case 2 :
                            // sqljet/src/Sql.g:461:49: SET DEFAULT
                            {
                            SET394=(Token)match(input,SET,FOLLOW_SET_in_fk_clause_action3505); 
                            DEFAULT395=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_fk_clause_action3508); 
                            DEFAULT395_tree = (Object)adaptor.create(DEFAULT395);
                            adaptor.addChild(root_0, DEFAULT395_tree);


                            }
                            break;
                        case 3 :
                            // sqljet/src/Sql.g:461:64: CASCADE
                            {
                            CASCADE396=(Token)match(input,CASCADE,FOLLOW_CASCADE_in_fk_clause_action3512); 
                            CASCADE396_tree = (Object)adaptor.create(CASCADE396);
                            adaptor.addChild(root_0, CASCADE396_tree);


                            }
                            break;
                        case 4 :
                            // sqljet/src/Sql.g:461:74: RESTRICT
                            {
                            RESTRICT397=(Token)match(input,RESTRICT,FOLLOW_RESTRICT_in_fk_clause_action3516); 
                            RESTRICT397_tree = (Object)adaptor.create(RESTRICT397);
                            adaptor.addChild(root_0, RESTRICT397_tree);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:462:5: MATCH id
                    {
                    root_0 = (Object)adaptor.nil();

                    MATCH398=(Token)match(input,MATCH,FOLLOW_MATCH_in_fk_clause_action3523); 
                    MATCH398_tree = (Object)adaptor.create(MATCH398);
                    root_0 = (Object)adaptor.becomeRoot(MATCH398_tree, root_0);

                    pushFollow(FOLLOW_id_in_fk_clause_action3526);
                    id399=id();

                    state._fsp--;

                    adaptor.addChild(root_0, id399.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fk_clause_action"

    public static class fk_clause_deferrable_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fk_clause_deferrable"
    // sqljet/src/Sql.g:464:1: fk_clause_deferrable : ( NOT )? DEFERRABLE ( INITIALLY DEFERRED | INITIALLY IMMEDIATE )? ;
    public final SqlParser.fk_clause_deferrable_return fk_clause_deferrable() throws RecognitionException {
        SqlParser.fk_clause_deferrable_return retval = new SqlParser.fk_clause_deferrable_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NOT400=null;
        Token DEFERRABLE401=null;
        Token INITIALLY402=null;
        Token DEFERRED403=null;
        Token INITIALLY404=null;
        Token IMMEDIATE405=null;

        Object NOT400_tree=null;
        Object DEFERRABLE401_tree=null;
        Object INITIALLY402_tree=null;
        Object DEFERRED403_tree=null;
        Object INITIALLY404_tree=null;
        Object IMMEDIATE405_tree=null;

        try {
            // sqljet/src/Sql.g:464:21: ( ( NOT )? DEFERRABLE ( INITIALLY DEFERRED | INITIALLY IMMEDIATE )? )
            // sqljet/src/Sql.g:464:23: ( NOT )? DEFERRABLE ( INITIALLY DEFERRED | INITIALLY IMMEDIATE )?
            {
            root_0 = (Object)adaptor.nil();

            // sqljet/src/Sql.g:464:23: ( NOT )?
            int alt146=2;
            int LA146_0 = input.LA(1);

            if ( (LA146_0==NOT) ) {
                alt146=1;
            }
            switch (alt146) {
                case 1 :
                    // sqljet/src/Sql.g:464:24: NOT
                    {
                    NOT400=(Token)match(input,NOT,FOLLOW_NOT_in_fk_clause_deferrable3534); 
                    NOT400_tree = (Object)adaptor.create(NOT400);
                    adaptor.addChild(root_0, NOT400_tree);


                    }
                    break;

            }

            DEFERRABLE401=(Token)match(input,DEFERRABLE,FOLLOW_DEFERRABLE_in_fk_clause_deferrable3538); 
            DEFERRABLE401_tree = (Object)adaptor.create(DEFERRABLE401);
            root_0 = (Object)adaptor.becomeRoot(DEFERRABLE401_tree, root_0);

            // sqljet/src/Sql.g:464:42: ( INITIALLY DEFERRED | INITIALLY IMMEDIATE )?
            int alt147=3;
            alt147 = dfa147.predict(input);
            switch (alt147) {
                case 1 :
                    // sqljet/src/Sql.g:464:43: INITIALLY DEFERRED
                    {
                    INITIALLY402=(Token)match(input,INITIALLY,FOLLOW_INITIALLY_in_fk_clause_deferrable3542); 
                    DEFERRED403=(Token)match(input,DEFERRED,FOLLOW_DEFERRED_in_fk_clause_deferrable3545); 
                    DEFERRED403_tree = (Object)adaptor.create(DEFERRED403);
                    adaptor.addChild(root_0, DEFERRED403_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:464:65: INITIALLY IMMEDIATE
                    {
                    INITIALLY404=(Token)match(input,INITIALLY,FOLLOW_INITIALLY_in_fk_clause_deferrable3549); 
                    IMMEDIATE405=(Token)match(input,IMMEDIATE,FOLLOW_IMMEDIATE_in_fk_clause_deferrable3552); 
                    IMMEDIATE405_tree = (Object)adaptor.create(IMMEDIATE405);
                    adaptor.addChild(root_0, IMMEDIATE405_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fk_clause_deferrable"

    public static class drop_table_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "drop_table_stmt"
    // sqljet/src/Sql.g:467:1: drop_table_stmt : DROP TABLE ( IF EXISTS )? (database_name= id DOT )? table_name= id -> ^( DROP_TABLE ^( OPTIONS ( EXISTS )? ) ^( $table_name ( $database_name)? ) ) ;
    public final SqlParser.drop_table_stmt_return drop_table_stmt() throws RecognitionException {
        SqlParser.drop_table_stmt_return retval = new SqlParser.drop_table_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DROP406=null;
        Token TABLE407=null;
        Token IF408=null;
        Token EXISTS409=null;
        Token DOT410=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;


        Object DROP406_tree=null;
        Object TABLE407_tree=null;
        Object IF408_tree=null;
        Object EXISTS409_tree=null;
        Object DOT410_tree=null;
        RewriteRuleTokenStream stream_EXISTS=new RewriteRuleTokenStream(adaptor,"token EXISTS");
        RewriteRuleTokenStream stream_DROP=new RewriteRuleTokenStream(adaptor,"token DROP");
        RewriteRuleTokenStream stream_TABLE=new RewriteRuleTokenStream(adaptor,"token TABLE");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:467:16: ( DROP TABLE ( IF EXISTS )? (database_name= id DOT )? table_name= id -> ^( DROP_TABLE ^( OPTIONS ( EXISTS )? ) ^( $table_name ( $database_name)? ) ) )
            // sqljet/src/Sql.g:467:18: DROP TABLE ( IF EXISTS )? (database_name= id DOT )? table_name= id
            {
            DROP406=(Token)match(input,DROP,FOLLOW_DROP_in_drop_table_stmt3562);  
            stream_DROP.add(DROP406);

            TABLE407=(Token)match(input,TABLE,FOLLOW_TABLE_in_drop_table_stmt3564);  
            stream_TABLE.add(TABLE407);

            // sqljet/src/Sql.g:467:29: ( IF EXISTS )?
            int alt148=2;
            int LA148_0 = input.LA(1);

            if ( (LA148_0==IF) ) {
                int LA148_1 = input.LA(2);

                if ( (LA148_1==EXISTS) ) {
                    alt148=1;
                }
            }
            switch (alt148) {
                case 1 :
                    // sqljet/src/Sql.g:467:30: IF EXISTS
                    {
                    IF408=(Token)match(input,IF,FOLLOW_IF_in_drop_table_stmt3567);  
                    stream_IF.add(IF408);

                    EXISTS409=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_drop_table_stmt3569);  
                    stream_EXISTS.add(EXISTS409);


                    }
                    break;

            }

            // sqljet/src/Sql.g:467:42: (database_name= id DOT )?
            int alt149=2;
            int LA149_0 = input.LA(1);

            if ( (LA149_0==ID||LA149_0==STRING) ) {
                int LA149_1 = input.LA(2);

                if ( (LA149_1==DOT) ) {
                    alt149=1;
                }
            }
            else if ( ((LA149_0>=EXPLAIN && LA149_0<=PLAN)||(LA149_0>=INDEXED && LA149_0<=BY)||(LA149_0>=OR && LA149_0<=ESCAPE)||(LA149_0>=IS && LA149_0<=BETWEEN)||LA149_0==COLLATE||(LA149_0>=DISTINCT && LA149_0<=THEN)||(LA149_0>=CURRENT_TIME && LA149_0<=CURRENT_TIMESTAMP)||(LA149_0>=RAISE && LA149_0<=ROW)) ) {
                int LA149_2 = input.LA(2);

                if ( (LA149_2==DOT) ) {
                    alt149=1;
                }
            }
            switch (alt149) {
                case 1 :
                    // sqljet/src/Sql.g:467:43: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_drop_table_stmt3576);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT410=(Token)match(input,DOT,FOLLOW_DOT_in_drop_table_stmt3578);  
                    stream_DOT.add(DOT410);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_drop_table_stmt3584);
            table_name=id();

            state._fsp--;

            stream_id.add(table_name.getTree());


            // AST REWRITE
            // elements: database_name, table_name, EXISTS
            // token labels: 
            // rule labels: database_name, retval, table_name
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);

            root_0 = (Object)adaptor.nil();
            // 468:1: -> ^( DROP_TABLE ^( OPTIONS ( EXISTS )? ) ^( $table_name ( $database_name)? ) )
            {
                // sqljet/src/Sql.g:468:4: ^( DROP_TABLE ^( OPTIONS ( EXISTS )? ) ^( $table_name ( $database_name)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DROP_TABLE, "DROP_TABLE"), root_1);

                // sqljet/src/Sql.g:468:17: ^( OPTIONS ( EXISTS )? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONS, "OPTIONS"), root_2);

                // sqljet/src/Sql.g:468:27: ( EXISTS )?
                if ( stream_EXISTS.hasNext() ) {
                    adaptor.addChild(root_2, stream_EXISTS.nextNode());

                }
                stream_EXISTS.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:468:36: ^( $table_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_table_name.nextNode(), root_2);

                // sqljet/src/Sql.g:468:50: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "drop_table_stmt"

    public static class alter_table_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alter_table_stmt"
    // sqljet/src/Sql.g:471:1: alter_table_stmt : ALTER TABLE (database_name= id DOT )? table_name= id ( RENAME TO new_table_name= id | ADD ( COLUMN )? column_def ) ;
    public final SqlParser.alter_table_stmt_return alter_table_stmt() throws RecognitionException {
        SqlParser.alter_table_stmt_return retval = new SqlParser.alter_table_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ALTER411=null;
        Token TABLE412=null;
        Token DOT413=null;
        Token RENAME414=null;
        Token TO415=null;
        Token ADD416=null;
        Token COLUMN417=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.id_return new_table_name = null;

        SqlParser.column_def_return column_def418 = null;


        Object ALTER411_tree=null;
        Object TABLE412_tree=null;
        Object DOT413_tree=null;
        Object RENAME414_tree=null;
        Object TO415_tree=null;
        Object ADD416_tree=null;
        Object COLUMN417_tree=null;

        try {
            // sqljet/src/Sql.g:471:17: ( ALTER TABLE (database_name= id DOT )? table_name= id ( RENAME TO new_table_name= id | ADD ( COLUMN )? column_def ) )
            // sqljet/src/Sql.g:471:19: ALTER TABLE (database_name= id DOT )? table_name= id ( RENAME TO new_table_name= id | ADD ( COLUMN )? column_def )
            {
            root_0 = (Object)adaptor.nil();

            ALTER411=(Token)match(input,ALTER,FOLLOW_ALTER_in_alter_table_stmt3614); 
            ALTER411_tree = (Object)adaptor.create(ALTER411);
            adaptor.addChild(root_0, ALTER411_tree);

            TABLE412=(Token)match(input,TABLE,FOLLOW_TABLE_in_alter_table_stmt3616); 
            TABLE412_tree = (Object)adaptor.create(TABLE412);
            adaptor.addChild(root_0, TABLE412_tree);

            // sqljet/src/Sql.g:471:31: (database_name= id DOT )?
            int alt150=2;
            int LA150_0 = input.LA(1);

            if ( (LA150_0==ID||LA150_0==STRING) ) {
                int LA150_1 = input.LA(2);

                if ( (LA150_1==DOT) ) {
                    alt150=1;
                }
            }
            else if ( ((LA150_0>=EXPLAIN && LA150_0<=PLAN)||(LA150_0>=INDEXED && LA150_0<=BY)||(LA150_0>=OR && LA150_0<=ESCAPE)||(LA150_0>=IS && LA150_0<=BETWEEN)||LA150_0==COLLATE||(LA150_0>=DISTINCT && LA150_0<=THEN)||(LA150_0>=CURRENT_TIME && LA150_0<=CURRENT_TIMESTAMP)||(LA150_0>=RAISE && LA150_0<=ROW)) ) {
                int LA150_2 = input.LA(2);

                if ( (LA150_2==DOT) ) {
                    alt150=1;
                }
            }
            switch (alt150) {
                case 1 :
                    // sqljet/src/Sql.g:471:32: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_alter_table_stmt3621);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT413=(Token)match(input,DOT,FOLLOW_DOT_in_alter_table_stmt3623); 
                    DOT413_tree = (Object)adaptor.create(DOT413);
                    adaptor.addChild(root_0, DOT413_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_alter_table_stmt3629);
            table_name=id();

            state._fsp--;

            adaptor.addChild(root_0, table_name.getTree());
            // sqljet/src/Sql.g:471:69: ( RENAME TO new_table_name= id | ADD ( COLUMN )? column_def )
            int alt152=2;
            int LA152_0 = input.LA(1);

            if ( (LA152_0==RENAME) ) {
                alt152=1;
            }
            else if ( (LA152_0==ADD) ) {
                alt152=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 152, 0, input);

                throw nvae;
            }
            switch (alt152) {
                case 1 :
                    // sqljet/src/Sql.g:471:70: RENAME TO new_table_name= id
                    {
                    RENAME414=(Token)match(input,RENAME,FOLLOW_RENAME_in_alter_table_stmt3632); 
                    RENAME414_tree = (Object)adaptor.create(RENAME414);
                    adaptor.addChild(root_0, RENAME414_tree);

                    TO415=(Token)match(input,TO,FOLLOW_TO_in_alter_table_stmt3634); 
                    TO415_tree = (Object)adaptor.create(TO415);
                    adaptor.addChild(root_0, TO415_tree);

                    pushFollow(FOLLOW_id_in_alter_table_stmt3638);
                    new_table_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, new_table_name.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:471:100: ADD ( COLUMN )? column_def
                    {
                    ADD416=(Token)match(input,ADD,FOLLOW_ADD_in_alter_table_stmt3642); 
                    ADD416_tree = (Object)adaptor.create(ADD416);
                    adaptor.addChild(root_0, ADD416_tree);

                    // sqljet/src/Sql.g:471:104: ( COLUMN )?
                    int alt151=2;
                    int LA151_0 = input.LA(1);

                    if ( (LA151_0==COLUMN) ) {
                        alt151=1;
                    }
                    switch (alt151) {
                        case 1 :
                            // sqljet/src/Sql.g:471:105: COLUMN
                            {
                            COLUMN417=(Token)match(input,COLUMN,FOLLOW_COLUMN_in_alter_table_stmt3645); 
                            COLUMN417_tree = (Object)adaptor.create(COLUMN417);
                            adaptor.addChild(root_0, COLUMN417_tree);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_column_def_in_alter_table_stmt3649);
                    column_def418=column_def();

                    state._fsp--;

                    adaptor.addChild(root_0, column_def418.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "alter_table_stmt"

    public static class create_view_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "create_view_stmt"
    // sqljet/src/Sql.g:474:1: create_view_stmt : CREATE ( TEMPORARY )? VIEW ( IF NOT EXISTS )? (database_name= id DOT )? view_name= id AS select_stmt ;
    public final SqlParser.create_view_stmt_return create_view_stmt() throws RecognitionException {
        SqlParser.create_view_stmt_return retval = new SqlParser.create_view_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CREATE419=null;
        Token TEMPORARY420=null;
        Token VIEW421=null;
        Token IF422=null;
        Token NOT423=null;
        Token EXISTS424=null;
        Token DOT425=null;
        Token AS426=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return view_name = null;

        SqlParser.select_stmt_return select_stmt427 = null;


        Object CREATE419_tree=null;
        Object TEMPORARY420_tree=null;
        Object VIEW421_tree=null;
        Object IF422_tree=null;
        Object NOT423_tree=null;
        Object EXISTS424_tree=null;
        Object DOT425_tree=null;
        Object AS426_tree=null;

        try {
            // sqljet/src/Sql.g:474:17: ( CREATE ( TEMPORARY )? VIEW ( IF NOT EXISTS )? (database_name= id DOT )? view_name= id AS select_stmt )
            // sqljet/src/Sql.g:474:19: CREATE ( TEMPORARY )? VIEW ( IF NOT EXISTS )? (database_name= id DOT )? view_name= id AS select_stmt
            {
            root_0 = (Object)adaptor.nil();

            CREATE419=(Token)match(input,CREATE,FOLLOW_CREATE_in_create_view_stmt3658); 
            CREATE419_tree = (Object)adaptor.create(CREATE419);
            adaptor.addChild(root_0, CREATE419_tree);

            // sqljet/src/Sql.g:474:26: ( TEMPORARY )?
            int alt153=2;
            int LA153_0 = input.LA(1);

            if ( (LA153_0==TEMPORARY) ) {
                alt153=1;
            }
            switch (alt153) {
                case 1 :
                    // sqljet/src/Sql.g:474:26: TEMPORARY
                    {
                    TEMPORARY420=(Token)match(input,TEMPORARY,FOLLOW_TEMPORARY_in_create_view_stmt3660); 
                    TEMPORARY420_tree = (Object)adaptor.create(TEMPORARY420);
                    adaptor.addChild(root_0, TEMPORARY420_tree);


                    }
                    break;

            }

            VIEW421=(Token)match(input,VIEW,FOLLOW_VIEW_in_create_view_stmt3663); 
            VIEW421_tree = (Object)adaptor.create(VIEW421);
            adaptor.addChild(root_0, VIEW421_tree);

            // sqljet/src/Sql.g:474:42: ( IF NOT EXISTS )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==IF) ) {
                int LA154_1 = input.LA(2);

                if ( (LA154_1==NOT) ) {
                    alt154=1;
                }
            }
            switch (alt154) {
                case 1 :
                    // sqljet/src/Sql.g:474:43: IF NOT EXISTS
                    {
                    IF422=(Token)match(input,IF,FOLLOW_IF_in_create_view_stmt3666); 
                    IF422_tree = (Object)adaptor.create(IF422);
                    adaptor.addChild(root_0, IF422_tree);

                    NOT423=(Token)match(input,NOT,FOLLOW_NOT_in_create_view_stmt3668); 
                    NOT423_tree = (Object)adaptor.create(NOT423);
                    adaptor.addChild(root_0, NOT423_tree);

                    EXISTS424=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_create_view_stmt3670); 
                    EXISTS424_tree = (Object)adaptor.create(EXISTS424);
                    adaptor.addChild(root_0, EXISTS424_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:474:59: (database_name= id DOT )?
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==ID||LA155_0==STRING) ) {
                int LA155_1 = input.LA(2);

                if ( (LA155_1==DOT) ) {
                    alt155=1;
                }
            }
            else if ( ((LA155_0>=EXPLAIN && LA155_0<=PLAN)||(LA155_0>=INDEXED && LA155_0<=BY)||(LA155_0>=OR && LA155_0<=ESCAPE)||(LA155_0>=IS && LA155_0<=BETWEEN)||LA155_0==COLLATE||(LA155_0>=DISTINCT && LA155_0<=THEN)||(LA155_0>=CURRENT_TIME && LA155_0<=CURRENT_TIMESTAMP)||(LA155_0>=RAISE && LA155_0<=ROW)) ) {
                int LA155_2 = input.LA(2);

                if ( (LA155_2==DOT) ) {
                    alt155=1;
                }
            }
            switch (alt155) {
                case 1 :
                    // sqljet/src/Sql.g:474:60: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_create_view_stmt3677);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT425=(Token)match(input,DOT,FOLLOW_DOT_in_create_view_stmt3679); 
                    DOT425_tree = (Object)adaptor.create(DOT425);
                    adaptor.addChild(root_0, DOT425_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_create_view_stmt3685);
            view_name=id();

            state._fsp--;

            adaptor.addChild(root_0, view_name.getTree());
            AS426=(Token)match(input,AS,FOLLOW_AS_in_create_view_stmt3687); 
            AS426_tree = (Object)adaptor.create(AS426);
            adaptor.addChild(root_0, AS426_tree);

            pushFollow(FOLLOW_select_stmt_in_create_view_stmt3689);
            select_stmt427=select_stmt();

            state._fsp--;

            adaptor.addChild(root_0, select_stmt427.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "create_view_stmt"

    public static class drop_view_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "drop_view_stmt"
    // sqljet/src/Sql.g:477:1: drop_view_stmt : DROP VIEW ( IF EXISTS )? (database_name= id DOT )? view_name= id ;
    public final SqlParser.drop_view_stmt_return drop_view_stmt() throws RecognitionException {
        SqlParser.drop_view_stmt_return retval = new SqlParser.drop_view_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DROP428=null;
        Token VIEW429=null;
        Token IF430=null;
        Token EXISTS431=null;
        Token DOT432=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return view_name = null;


        Object DROP428_tree=null;
        Object VIEW429_tree=null;
        Object IF430_tree=null;
        Object EXISTS431_tree=null;
        Object DOT432_tree=null;

        try {
            // sqljet/src/Sql.g:477:15: ( DROP VIEW ( IF EXISTS )? (database_name= id DOT )? view_name= id )
            // sqljet/src/Sql.g:477:17: DROP VIEW ( IF EXISTS )? (database_name= id DOT )? view_name= id
            {
            root_0 = (Object)adaptor.nil();

            DROP428=(Token)match(input,DROP,FOLLOW_DROP_in_drop_view_stmt3697); 
            DROP428_tree = (Object)adaptor.create(DROP428);
            adaptor.addChild(root_0, DROP428_tree);

            VIEW429=(Token)match(input,VIEW,FOLLOW_VIEW_in_drop_view_stmt3699); 
            VIEW429_tree = (Object)adaptor.create(VIEW429);
            adaptor.addChild(root_0, VIEW429_tree);

            // sqljet/src/Sql.g:477:27: ( IF EXISTS )?
            int alt156=2;
            int LA156_0 = input.LA(1);

            if ( (LA156_0==IF) ) {
                int LA156_1 = input.LA(2);

                if ( (LA156_1==EXISTS) ) {
                    alt156=1;
                }
            }
            switch (alt156) {
                case 1 :
                    // sqljet/src/Sql.g:477:28: IF EXISTS
                    {
                    IF430=(Token)match(input,IF,FOLLOW_IF_in_drop_view_stmt3702); 
                    IF430_tree = (Object)adaptor.create(IF430);
                    adaptor.addChild(root_0, IF430_tree);

                    EXISTS431=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_drop_view_stmt3704); 
                    EXISTS431_tree = (Object)adaptor.create(EXISTS431);
                    adaptor.addChild(root_0, EXISTS431_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:477:40: (database_name= id DOT )?
            int alt157=2;
            int LA157_0 = input.LA(1);

            if ( (LA157_0==ID||LA157_0==STRING) ) {
                int LA157_1 = input.LA(2);

                if ( (LA157_1==DOT) ) {
                    alt157=1;
                }
            }
            else if ( ((LA157_0>=EXPLAIN && LA157_0<=PLAN)||(LA157_0>=INDEXED && LA157_0<=BY)||(LA157_0>=OR && LA157_0<=ESCAPE)||(LA157_0>=IS && LA157_0<=BETWEEN)||LA157_0==COLLATE||(LA157_0>=DISTINCT && LA157_0<=THEN)||(LA157_0>=CURRENT_TIME && LA157_0<=CURRENT_TIMESTAMP)||(LA157_0>=RAISE && LA157_0<=ROW)) ) {
                int LA157_2 = input.LA(2);

                if ( (LA157_2==DOT) ) {
                    alt157=1;
                }
            }
            switch (alt157) {
                case 1 :
                    // sqljet/src/Sql.g:477:41: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_drop_view_stmt3711);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT432=(Token)match(input,DOT,FOLLOW_DOT_in_drop_view_stmt3713); 
                    DOT432_tree = (Object)adaptor.create(DOT432);
                    adaptor.addChild(root_0, DOT432_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_drop_view_stmt3719);
            view_name=id();

            state._fsp--;

            adaptor.addChild(root_0, view_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "drop_view_stmt"

    public static class create_index_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "create_index_stmt"
    // sqljet/src/Sql.g:480:1: create_index_stmt : CREATE ( UNIQUE )? INDEX ( IF NOT EXISTS )? (database_name= id DOT )? index_name= id ON table_name= id LPAREN columns+= indexed_column ( COMMA columns+= indexed_column )* RPAREN -> ^( CREATE_INDEX ^( OPTIONS ( UNIQUE )? ( EXISTS )? ) ^( $index_name ( $database_name)? ) $table_name ( ^( COLUMNS ( $columns)+ ) )? ) ;
    public final SqlParser.create_index_stmt_return create_index_stmt() throws RecognitionException {
        SqlParser.create_index_stmt_return retval = new SqlParser.create_index_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CREATE433=null;
        Token UNIQUE434=null;
        Token INDEX435=null;
        Token IF436=null;
        Token NOT437=null;
        Token EXISTS438=null;
        Token DOT439=null;
        Token ON440=null;
        Token LPAREN441=null;
        Token COMMA442=null;
        Token RPAREN443=null;
        List list_columns=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return index_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.indexed_column_return columns = null;
         columns = null;
        Object CREATE433_tree=null;
        Object UNIQUE434_tree=null;
        Object INDEX435_tree=null;
        Object IF436_tree=null;
        Object NOT437_tree=null;
        Object EXISTS438_tree=null;
        Object DOT439_tree=null;
        Object ON440_tree=null;
        Object LPAREN441_tree=null;
        Object COMMA442_tree=null;
        Object RPAREN443_tree=null;
        RewriteRuleTokenStream stream_INDEX=new RewriteRuleTokenStream(adaptor,"token INDEX");
        RewriteRuleTokenStream stream_EXISTS=new RewriteRuleTokenStream(adaptor,"token EXISTS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_UNIQUE=new RewriteRuleTokenStream(adaptor,"token UNIQUE");
        RewriteRuleTokenStream stream_ON=new RewriteRuleTokenStream(adaptor,"token ON");
        RewriteRuleTokenStream stream_CREATE=new RewriteRuleTokenStream(adaptor,"token CREATE");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_indexed_column=new RewriteRuleSubtreeStream(adaptor,"rule indexed_column");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:480:18: ( CREATE ( UNIQUE )? INDEX ( IF NOT EXISTS )? (database_name= id DOT )? index_name= id ON table_name= id LPAREN columns+= indexed_column ( COMMA columns+= indexed_column )* RPAREN -> ^( CREATE_INDEX ^( OPTIONS ( UNIQUE )? ( EXISTS )? ) ^( $index_name ( $database_name)? ) $table_name ( ^( COLUMNS ( $columns)+ ) )? ) )
            // sqljet/src/Sql.g:480:20: CREATE ( UNIQUE )? INDEX ( IF NOT EXISTS )? (database_name= id DOT )? index_name= id ON table_name= id LPAREN columns+= indexed_column ( COMMA columns+= indexed_column )* RPAREN
            {
            CREATE433=(Token)match(input,CREATE,FOLLOW_CREATE_in_create_index_stmt3727);  
            stream_CREATE.add(CREATE433);

            // sqljet/src/Sql.g:480:27: ( UNIQUE )?
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==UNIQUE) ) {
                alt158=1;
            }
            switch (alt158) {
                case 1 :
                    // sqljet/src/Sql.g:480:28: UNIQUE
                    {
                    UNIQUE434=(Token)match(input,UNIQUE,FOLLOW_UNIQUE_in_create_index_stmt3730);  
                    stream_UNIQUE.add(UNIQUE434);


                    }
                    break;

            }

            INDEX435=(Token)match(input,INDEX,FOLLOW_INDEX_in_create_index_stmt3734);  
            stream_INDEX.add(INDEX435);

            // sqljet/src/Sql.g:480:43: ( IF NOT EXISTS )?
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==IF) ) {
                int LA159_1 = input.LA(2);

                if ( (LA159_1==NOT) ) {
                    alt159=1;
                }
            }
            switch (alt159) {
                case 1 :
                    // sqljet/src/Sql.g:480:44: IF NOT EXISTS
                    {
                    IF436=(Token)match(input,IF,FOLLOW_IF_in_create_index_stmt3737);  
                    stream_IF.add(IF436);

                    NOT437=(Token)match(input,NOT,FOLLOW_NOT_in_create_index_stmt3739);  
                    stream_NOT.add(NOT437);

                    EXISTS438=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_create_index_stmt3741);  
                    stream_EXISTS.add(EXISTS438);


                    }
                    break;

            }

            // sqljet/src/Sql.g:480:60: (database_name= id DOT )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==ID||LA160_0==STRING) ) {
                int LA160_1 = input.LA(2);

                if ( (LA160_1==DOT) ) {
                    alt160=1;
                }
            }
            else if ( ((LA160_0>=EXPLAIN && LA160_0<=PLAN)||(LA160_0>=INDEXED && LA160_0<=BY)||(LA160_0>=OR && LA160_0<=ESCAPE)||(LA160_0>=IS && LA160_0<=BETWEEN)||LA160_0==COLLATE||(LA160_0>=DISTINCT && LA160_0<=THEN)||(LA160_0>=CURRENT_TIME && LA160_0<=CURRENT_TIMESTAMP)||(LA160_0>=RAISE && LA160_0<=ROW)) ) {
                int LA160_2 = input.LA(2);

                if ( (LA160_2==DOT) ) {
                    alt160=1;
                }
            }
            switch (alt160) {
                case 1 :
                    // sqljet/src/Sql.g:480:61: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_create_index_stmt3748);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT439=(Token)match(input,DOT,FOLLOW_DOT_in_create_index_stmt3750);  
                    stream_DOT.add(DOT439);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_create_index_stmt3756);
            index_name=id();

            state._fsp--;

            stream_id.add(index_name.getTree());
            ON440=(Token)match(input,ON,FOLLOW_ON_in_create_index_stmt3760);  
            stream_ON.add(ON440);

            pushFollow(FOLLOW_id_in_create_index_stmt3764);
            table_name=id();

            state._fsp--;

            stream_id.add(table_name.getTree());
            LPAREN441=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_create_index_stmt3766);  
            stream_LPAREN.add(LPAREN441);

            pushFollow(FOLLOW_indexed_column_in_create_index_stmt3770);
            columns=indexed_column();

            state._fsp--;

            stream_indexed_column.add(columns.getTree());
            if (list_columns==null) list_columns=new ArrayList();
            list_columns.add(columns.getTree());

            // sqljet/src/Sql.g:481:51: ( COMMA columns+= indexed_column )*
            loop161:
            do {
                int alt161=2;
                int LA161_0 = input.LA(1);

                if ( (LA161_0==COMMA) ) {
                    alt161=1;
                }


                switch (alt161) {
            	case 1 :
            	    // sqljet/src/Sql.g:481:52: COMMA columns+= indexed_column
            	    {
            	    COMMA442=(Token)match(input,COMMA,FOLLOW_COMMA_in_create_index_stmt3773);  
            	    stream_COMMA.add(COMMA442);

            	    pushFollow(FOLLOW_indexed_column_in_create_index_stmt3777);
            	    columns=indexed_column();

            	    state._fsp--;

            	    stream_indexed_column.add(columns.getTree());
            	    if (list_columns==null) list_columns=new ArrayList();
            	    list_columns.add(columns.getTree());


            	    }
            	    break;

            	default :
            	    break loop161;
                }
            } while (true);

            RPAREN443=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_create_index_stmt3781);  
            stream_RPAREN.add(RPAREN443);



            // AST REWRITE
            // elements: EXISTS, UNIQUE, database_name, columns, table_name, index_name
            // token labels: 
            // rule labels: index_name, database_name, retval, table_name
            // token list labels: 
            // rule list labels: columns
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_index_name=new RewriteRuleSubtreeStream(adaptor,"rule index_name",index_name!=null?index_name.tree:null);
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_table_name=new RewriteRuleSubtreeStream(adaptor,"rule table_name",table_name!=null?table_name.tree:null);
            RewriteRuleSubtreeStream stream_columns=new RewriteRuleSubtreeStream(adaptor,"token columns",list_columns);
            root_0 = (Object)adaptor.nil();
            // 482:1: -> ^( CREATE_INDEX ^( OPTIONS ( UNIQUE )? ( EXISTS )? ) ^( $index_name ( $database_name)? ) $table_name ( ^( COLUMNS ( $columns)+ ) )? )
            {
                // sqljet/src/Sql.g:482:4: ^( CREATE_INDEX ^( OPTIONS ( UNIQUE )? ( EXISTS )? ) ^( $index_name ( $database_name)? ) $table_name ( ^( COLUMNS ( $columns)+ ) )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CREATE_INDEX, "CREATE_INDEX"), root_1);

                // sqljet/src/Sql.g:482:19: ^( OPTIONS ( UNIQUE )? ( EXISTS )? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONS, "OPTIONS"), root_2);

                // sqljet/src/Sql.g:482:29: ( UNIQUE )?
                if ( stream_UNIQUE.hasNext() ) {
                    adaptor.addChild(root_2, stream_UNIQUE.nextNode());

                }
                stream_UNIQUE.reset();
                // sqljet/src/Sql.g:482:37: ( EXISTS )?
                if ( stream_EXISTS.hasNext() ) {
                    adaptor.addChild(root_2, stream_EXISTS.nextNode());

                }
                stream_EXISTS.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:482:46: ^( $index_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_index_name.nextNode(), root_2);

                // sqljet/src/Sql.g:482:60: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_table_name.nextTree());
                // sqljet/src/Sql.g:482:89: ( ^( COLUMNS ( $columns)+ ) )?
                if ( stream_columns.hasNext() ) {
                    // sqljet/src/Sql.g:482:89: ^( COLUMNS ( $columns)+ )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_2);

                    if ( !(stream_columns.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_columns.hasNext() ) {
                        adaptor.addChild(root_2, stream_columns.nextTree());

                    }
                    stream_columns.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_columns.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "create_index_stmt"

    public static class indexed_column_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "indexed_column"
    // sqljet/src/Sql.g:484:1: indexed_column : column_name= id ( COLLATE collation_name= id )? ( ASC | DESC )? -> ^( $column_name ( ^( COLLATE $collation_name) )? ( ASC )? ( DESC )? ) ;
    public final SqlParser.indexed_column_return indexed_column() throws RecognitionException {
        SqlParser.indexed_column_return retval = new SqlParser.indexed_column_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLLATE444=null;
        Token ASC445=null;
        Token DESC446=null;
        SqlParser.id_return column_name = null;

        SqlParser.id_return collation_name = null;


        Object COLLATE444_tree=null;
        Object ASC445_tree=null;
        Object DESC446_tree=null;
        RewriteRuleTokenStream stream_DESC=new RewriteRuleTokenStream(adaptor,"token DESC");
        RewriteRuleTokenStream stream_COLLATE=new RewriteRuleTokenStream(adaptor,"token COLLATE");
        RewriteRuleTokenStream stream_ASC=new RewriteRuleTokenStream(adaptor,"token ASC");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:484:15: (column_name= id ( COLLATE collation_name= id )? ( ASC | DESC )? -> ^( $column_name ( ^( COLLATE $collation_name) )? ( ASC )? ( DESC )? ) )
            // sqljet/src/Sql.g:484:17: column_name= id ( COLLATE collation_name= id )? ( ASC | DESC )?
            {
            pushFollow(FOLLOW_id_in_indexed_column3827);
            column_name=id();

            state._fsp--;

            stream_id.add(column_name.getTree());
            // sqljet/src/Sql.g:484:32: ( COLLATE collation_name= id )?
            int alt162=2;
            int LA162_0 = input.LA(1);

            if ( (LA162_0==COLLATE) ) {
                alt162=1;
            }
            switch (alt162) {
                case 1 :
                    // sqljet/src/Sql.g:484:33: COLLATE collation_name= id
                    {
                    COLLATE444=(Token)match(input,COLLATE,FOLLOW_COLLATE_in_indexed_column3830);  
                    stream_COLLATE.add(COLLATE444);

                    pushFollow(FOLLOW_id_in_indexed_column3834);
                    collation_name=id();

                    state._fsp--;

                    stream_id.add(collation_name.getTree());

                    }
                    break;

            }

            // sqljet/src/Sql.g:484:61: ( ASC | DESC )?
            int alt163=3;
            int LA163_0 = input.LA(1);

            if ( (LA163_0==ASC) ) {
                alt163=1;
            }
            else if ( (LA163_0==DESC) ) {
                alt163=2;
            }
            switch (alt163) {
                case 1 :
                    // sqljet/src/Sql.g:484:62: ASC
                    {
                    ASC445=(Token)match(input,ASC,FOLLOW_ASC_in_indexed_column3839);  
                    stream_ASC.add(ASC445);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:484:68: DESC
                    {
                    DESC446=(Token)match(input,DESC,FOLLOW_DESC_in_indexed_column3843);  
                    stream_DESC.add(DESC446);


                    }
                    break;

            }



            // AST REWRITE
            // elements: COLLATE, DESC, column_name, collation_name, ASC
            // token labels: 
            // rule labels: collation_name, column_name, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_collation_name=new RewriteRuleSubtreeStream(adaptor,"rule collation_name",collation_name!=null?collation_name.tree:null);
            RewriteRuleSubtreeStream stream_column_name=new RewriteRuleSubtreeStream(adaptor,"rule column_name",column_name!=null?column_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 485:1: -> ^( $column_name ( ^( COLLATE $collation_name) )? ( ASC )? ( DESC )? )
            {
                // sqljet/src/Sql.g:485:4: ^( $column_name ( ^( COLLATE $collation_name) )? ( ASC )? ( DESC )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_column_name.nextNode(), root_1);

                // sqljet/src/Sql.g:485:19: ( ^( COLLATE $collation_name) )?
                if ( stream_COLLATE.hasNext()||stream_collation_name.hasNext() ) {
                    // sqljet/src/Sql.g:485:19: ^( COLLATE $collation_name)
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot(stream_COLLATE.nextNode(), root_2);

                    adaptor.addChild(root_2, stream_collation_name.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_COLLATE.reset();
                stream_collation_name.reset();
                // sqljet/src/Sql.g:485:47: ( ASC )?
                if ( stream_ASC.hasNext() ) {
                    adaptor.addChild(root_1, stream_ASC.nextNode());

                }
                stream_ASC.reset();
                // sqljet/src/Sql.g:485:52: ( DESC )?
                if ( stream_DESC.hasNext() ) {
                    adaptor.addChild(root_1, stream_DESC.nextNode());

                }
                stream_DESC.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "indexed_column"

    public static class drop_index_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "drop_index_stmt"
    // sqljet/src/Sql.g:488:1: drop_index_stmt : DROP INDEX ( IF EXISTS )? (database_name= id DOT )? index_name= id -> ^( DROP_INDEX ^( OPTIONS ( EXISTS )? ) ^( $index_name ( $database_name)? ) ) ;
    public final SqlParser.drop_index_stmt_return drop_index_stmt() throws RecognitionException {
        SqlParser.drop_index_stmt_return retval = new SqlParser.drop_index_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DROP447=null;
        Token INDEX448=null;
        Token IF449=null;
        Token EXISTS450=null;
        Token DOT451=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return index_name = null;


        Object DROP447_tree=null;
        Object INDEX448_tree=null;
        Object IF449_tree=null;
        Object EXISTS450_tree=null;
        Object DOT451_tree=null;
        RewriteRuleTokenStream stream_INDEX=new RewriteRuleTokenStream(adaptor,"token INDEX");
        RewriteRuleTokenStream stream_EXISTS=new RewriteRuleTokenStream(adaptor,"token EXISTS");
        RewriteRuleTokenStream stream_DROP=new RewriteRuleTokenStream(adaptor,"token DROP");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // sqljet/src/Sql.g:488:16: ( DROP INDEX ( IF EXISTS )? (database_name= id DOT )? index_name= id -> ^( DROP_INDEX ^( OPTIONS ( EXISTS )? ) ^( $index_name ( $database_name)? ) ) )
            // sqljet/src/Sql.g:488:18: DROP INDEX ( IF EXISTS )? (database_name= id DOT )? index_name= id
            {
            DROP447=(Token)match(input,DROP,FOLLOW_DROP_in_drop_index_stmt3874);  
            stream_DROP.add(DROP447);

            INDEX448=(Token)match(input,INDEX,FOLLOW_INDEX_in_drop_index_stmt3876);  
            stream_INDEX.add(INDEX448);

            // sqljet/src/Sql.g:488:29: ( IF EXISTS )?
            int alt164=2;
            int LA164_0 = input.LA(1);

            if ( (LA164_0==IF) ) {
                int LA164_1 = input.LA(2);

                if ( (LA164_1==EXISTS) ) {
                    alt164=1;
                }
            }
            switch (alt164) {
                case 1 :
                    // sqljet/src/Sql.g:488:30: IF EXISTS
                    {
                    IF449=(Token)match(input,IF,FOLLOW_IF_in_drop_index_stmt3879);  
                    stream_IF.add(IF449);

                    EXISTS450=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_drop_index_stmt3881);  
                    stream_EXISTS.add(EXISTS450);


                    }
                    break;

            }

            // sqljet/src/Sql.g:488:42: (database_name= id DOT )?
            int alt165=2;
            int LA165_0 = input.LA(1);

            if ( (LA165_0==ID||LA165_0==STRING) ) {
                int LA165_1 = input.LA(2);

                if ( (LA165_1==DOT) ) {
                    alt165=1;
                }
            }
            else if ( ((LA165_0>=EXPLAIN && LA165_0<=PLAN)||(LA165_0>=INDEXED && LA165_0<=BY)||(LA165_0>=OR && LA165_0<=ESCAPE)||(LA165_0>=IS && LA165_0<=BETWEEN)||LA165_0==COLLATE||(LA165_0>=DISTINCT && LA165_0<=THEN)||(LA165_0>=CURRENT_TIME && LA165_0<=CURRENT_TIMESTAMP)||(LA165_0>=RAISE && LA165_0<=ROW)) ) {
                int LA165_2 = input.LA(2);

                if ( (LA165_2==DOT) ) {
                    alt165=1;
                }
            }
            switch (alt165) {
                case 1 :
                    // sqljet/src/Sql.g:488:43: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_drop_index_stmt3888);
                    database_name=id();

                    state._fsp--;

                    stream_id.add(database_name.getTree());
                    DOT451=(Token)match(input,DOT,FOLLOW_DOT_in_drop_index_stmt3890);  
                    stream_DOT.add(DOT451);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_drop_index_stmt3896);
            index_name=id();

            state._fsp--;

            stream_id.add(index_name.getTree());


            // AST REWRITE
            // elements: database_name, EXISTS, index_name
            // token labels: 
            // rule labels: index_name, database_name, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_index_name=new RewriteRuleSubtreeStream(adaptor,"rule index_name",index_name!=null?index_name.tree:null);
            RewriteRuleSubtreeStream stream_database_name=new RewriteRuleSubtreeStream(adaptor,"rule database_name",database_name!=null?database_name.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 489:1: -> ^( DROP_INDEX ^( OPTIONS ( EXISTS )? ) ^( $index_name ( $database_name)? ) )
            {
                // sqljet/src/Sql.g:489:4: ^( DROP_INDEX ^( OPTIONS ( EXISTS )? ) ^( $index_name ( $database_name)? ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DROP_INDEX, "DROP_INDEX"), root_1);

                // sqljet/src/Sql.g:489:17: ^( OPTIONS ( EXISTS )? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONS, "OPTIONS"), root_2);

                // sqljet/src/Sql.g:489:27: ( EXISTS )?
                if ( stream_EXISTS.hasNext() ) {
                    adaptor.addChild(root_2, stream_EXISTS.nextNode());

                }
                stream_EXISTS.reset();

                adaptor.addChild(root_1, root_2);
                }
                // sqljet/src/Sql.g:489:36: ^( $index_name ( $database_name)? )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_index_name.nextNode(), root_2);

                // sqljet/src/Sql.g:489:50: ( $database_name)?
                if ( stream_database_name.hasNext() ) {
                    adaptor.addChild(root_2, stream_database_name.nextTree());

                }
                stream_database_name.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "drop_index_stmt"

    public static class create_trigger_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "create_trigger_stmt"
    // sqljet/src/Sql.g:492:1: create_trigger_stmt : CREATE ( TEMPORARY )? TRIGGER ( IF NOT EXISTS )? (database_name= id DOT )? trigger_name= id ( BEFORE | AFTER | INSTEAD OF )? ( DELETE | INSERT | UPDATE ( OF column_names+= id ( COMMA column_names+= id )* )? ) ON table_name= id ( FOR EACH ROW )? ( WHEN expr )? BEGIN ( ( update_stmt | insert_stmt | delete_stmt | select_stmt ) SEMI )+ END ;
    public final SqlParser.create_trigger_stmt_return create_trigger_stmt() throws RecognitionException {
        SqlParser.create_trigger_stmt_return retval = new SqlParser.create_trigger_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CREATE452=null;
        Token TEMPORARY453=null;
        Token TRIGGER454=null;
        Token IF455=null;
        Token NOT456=null;
        Token EXISTS457=null;
        Token DOT458=null;
        Token BEFORE459=null;
        Token AFTER460=null;
        Token INSTEAD461=null;
        Token OF462=null;
        Token DELETE463=null;
        Token INSERT464=null;
        Token UPDATE465=null;
        Token OF466=null;
        Token COMMA467=null;
        Token ON468=null;
        Token FOR469=null;
        Token EACH470=null;
        Token ROW471=null;
        Token WHEN472=null;
        Token BEGIN474=null;
        Token SEMI479=null;
        Token END480=null;
        List list_column_names=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return trigger_name = null;

        SqlParser.id_return table_name = null;

        SqlParser.expr_return expr473 = null;

        SqlParser.update_stmt_return update_stmt475 = null;

        SqlParser.insert_stmt_return insert_stmt476 = null;

        SqlParser.delete_stmt_return delete_stmt477 = null;

        SqlParser.select_stmt_return select_stmt478 = null;

        SqlParser.id_return column_names = null;
         column_names = null;
        Object CREATE452_tree=null;
        Object TEMPORARY453_tree=null;
        Object TRIGGER454_tree=null;
        Object IF455_tree=null;
        Object NOT456_tree=null;
        Object EXISTS457_tree=null;
        Object DOT458_tree=null;
        Object BEFORE459_tree=null;
        Object AFTER460_tree=null;
        Object INSTEAD461_tree=null;
        Object OF462_tree=null;
        Object DELETE463_tree=null;
        Object INSERT464_tree=null;
        Object UPDATE465_tree=null;
        Object OF466_tree=null;
        Object COMMA467_tree=null;
        Object ON468_tree=null;
        Object FOR469_tree=null;
        Object EACH470_tree=null;
        Object ROW471_tree=null;
        Object WHEN472_tree=null;
        Object BEGIN474_tree=null;
        Object SEMI479_tree=null;
        Object END480_tree=null;

        try {
            // sqljet/src/Sql.g:492:20: ( CREATE ( TEMPORARY )? TRIGGER ( IF NOT EXISTS )? (database_name= id DOT )? trigger_name= id ( BEFORE | AFTER | INSTEAD OF )? ( DELETE | INSERT | UPDATE ( OF column_names+= id ( COMMA column_names+= id )* )? ) ON table_name= id ( FOR EACH ROW )? ( WHEN expr )? BEGIN ( ( update_stmt | insert_stmt | delete_stmt | select_stmt ) SEMI )+ END )
            // sqljet/src/Sql.g:492:22: CREATE ( TEMPORARY )? TRIGGER ( IF NOT EXISTS )? (database_name= id DOT )? trigger_name= id ( BEFORE | AFTER | INSTEAD OF )? ( DELETE | INSERT | UPDATE ( OF column_names+= id ( COMMA column_names+= id )* )? ) ON table_name= id ( FOR EACH ROW )? ( WHEN expr )? BEGIN ( ( update_stmt | insert_stmt | delete_stmt | select_stmt ) SEMI )+ END
            {
            root_0 = (Object)adaptor.nil();

            CREATE452=(Token)match(input,CREATE,FOLLOW_CREATE_in_create_trigger_stmt3926); 
            CREATE452_tree = (Object)adaptor.create(CREATE452);
            adaptor.addChild(root_0, CREATE452_tree);

            // sqljet/src/Sql.g:492:29: ( TEMPORARY )?
            int alt166=2;
            int LA166_0 = input.LA(1);

            if ( (LA166_0==TEMPORARY) ) {
                alt166=1;
            }
            switch (alt166) {
                case 1 :
                    // sqljet/src/Sql.g:492:29: TEMPORARY
                    {
                    TEMPORARY453=(Token)match(input,TEMPORARY,FOLLOW_TEMPORARY_in_create_trigger_stmt3928); 
                    TEMPORARY453_tree = (Object)adaptor.create(TEMPORARY453);
                    adaptor.addChild(root_0, TEMPORARY453_tree);


                    }
                    break;

            }

            TRIGGER454=(Token)match(input,TRIGGER,FOLLOW_TRIGGER_in_create_trigger_stmt3931); 
            TRIGGER454_tree = (Object)adaptor.create(TRIGGER454);
            adaptor.addChild(root_0, TRIGGER454_tree);

            // sqljet/src/Sql.g:492:48: ( IF NOT EXISTS )?
            int alt167=2;
            alt167 = dfa167.predict(input);
            switch (alt167) {
                case 1 :
                    // sqljet/src/Sql.g:492:49: IF NOT EXISTS
                    {
                    IF455=(Token)match(input,IF,FOLLOW_IF_in_create_trigger_stmt3934); 
                    IF455_tree = (Object)adaptor.create(IF455);
                    adaptor.addChild(root_0, IF455_tree);

                    NOT456=(Token)match(input,NOT,FOLLOW_NOT_in_create_trigger_stmt3936); 
                    NOT456_tree = (Object)adaptor.create(NOT456);
                    adaptor.addChild(root_0, NOT456_tree);

                    EXISTS457=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_create_trigger_stmt3938); 
                    EXISTS457_tree = (Object)adaptor.create(EXISTS457);
                    adaptor.addChild(root_0, EXISTS457_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:492:65: (database_name= id DOT )?
            int alt168=2;
            alt168 = dfa168.predict(input);
            switch (alt168) {
                case 1 :
                    // sqljet/src/Sql.g:492:66: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_create_trigger_stmt3945);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT458=(Token)match(input,DOT,FOLLOW_DOT_in_create_trigger_stmt3947); 
                    DOT458_tree = (Object)adaptor.create(DOT458);
                    adaptor.addChild(root_0, DOT458_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_create_trigger_stmt3953);
            trigger_name=id();

            state._fsp--;

            adaptor.addChild(root_0, trigger_name.getTree());
            // sqljet/src/Sql.g:493:3: ( BEFORE | AFTER | INSTEAD OF )?
            int alt169=4;
            switch ( input.LA(1) ) {
                case BEFORE:
                    {
                    alt169=1;
                    }
                    break;
                case AFTER:
                    {
                    alt169=2;
                    }
                    break;
                case INSTEAD:
                    {
                    alt169=3;
                    }
                    break;
            }

            switch (alt169) {
                case 1 :
                    // sqljet/src/Sql.g:493:4: BEFORE
                    {
                    BEFORE459=(Token)match(input,BEFORE,FOLLOW_BEFORE_in_create_trigger_stmt3958); 
                    BEFORE459_tree = (Object)adaptor.create(BEFORE459);
                    adaptor.addChild(root_0, BEFORE459_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:493:13: AFTER
                    {
                    AFTER460=(Token)match(input,AFTER,FOLLOW_AFTER_in_create_trigger_stmt3962); 
                    AFTER460_tree = (Object)adaptor.create(AFTER460);
                    adaptor.addChild(root_0, AFTER460_tree);


                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:493:21: INSTEAD OF
                    {
                    INSTEAD461=(Token)match(input,INSTEAD,FOLLOW_INSTEAD_in_create_trigger_stmt3966); 
                    INSTEAD461_tree = (Object)adaptor.create(INSTEAD461);
                    adaptor.addChild(root_0, INSTEAD461_tree);

                    OF462=(Token)match(input,OF,FOLLOW_OF_in_create_trigger_stmt3968); 
                    OF462_tree = (Object)adaptor.create(OF462);
                    adaptor.addChild(root_0, OF462_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:493:34: ( DELETE | INSERT | UPDATE ( OF column_names+= id ( COMMA column_names+= id )* )? )
            int alt172=3;
            switch ( input.LA(1) ) {
            case DELETE:
                {
                alt172=1;
                }
                break;
            case INSERT:
                {
                alt172=2;
                }
                break;
            case UPDATE:
                {
                alt172=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 172, 0, input);

                throw nvae;
            }

            switch (alt172) {
                case 1 :
                    // sqljet/src/Sql.g:493:35: DELETE
                    {
                    DELETE463=(Token)match(input,DELETE,FOLLOW_DELETE_in_create_trigger_stmt3973); 
                    DELETE463_tree = (Object)adaptor.create(DELETE463);
                    adaptor.addChild(root_0, DELETE463_tree);


                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:493:44: INSERT
                    {
                    INSERT464=(Token)match(input,INSERT,FOLLOW_INSERT_in_create_trigger_stmt3977); 
                    INSERT464_tree = (Object)adaptor.create(INSERT464);
                    adaptor.addChild(root_0, INSERT464_tree);


                    }
                    break;
                case 3 :
                    // sqljet/src/Sql.g:493:53: UPDATE ( OF column_names+= id ( COMMA column_names+= id )* )?
                    {
                    UPDATE465=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_create_trigger_stmt3981); 
                    UPDATE465_tree = (Object)adaptor.create(UPDATE465);
                    adaptor.addChild(root_0, UPDATE465_tree);

                    // sqljet/src/Sql.g:493:60: ( OF column_names+= id ( COMMA column_names+= id )* )?
                    int alt171=2;
                    int LA171_0 = input.LA(1);

                    if ( (LA171_0==OF) ) {
                        alt171=1;
                    }
                    switch (alt171) {
                        case 1 :
                            // sqljet/src/Sql.g:493:61: OF column_names+= id ( COMMA column_names+= id )*
                            {
                            OF466=(Token)match(input,OF,FOLLOW_OF_in_create_trigger_stmt3984); 
                            OF466_tree = (Object)adaptor.create(OF466);
                            adaptor.addChild(root_0, OF466_tree);

                            pushFollow(FOLLOW_id_in_create_trigger_stmt3988);
                            column_names=id();

                            state._fsp--;

                            adaptor.addChild(root_0, column_names.getTree());
                            if (list_column_names==null) list_column_names=new ArrayList();
                            list_column_names.add(column_names.getTree());

                            // sqljet/src/Sql.g:493:81: ( COMMA column_names+= id )*
                            loop170:
                            do {
                                int alt170=2;
                                int LA170_0 = input.LA(1);

                                if ( (LA170_0==COMMA) ) {
                                    alt170=1;
                                }


                                switch (alt170) {
                            	case 1 :
                            	    // sqljet/src/Sql.g:493:82: COMMA column_names+= id
                            	    {
                            	    COMMA467=(Token)match(input,COMMA,FOLLOW_COMMA_in_create_trigger_stmt3991); 
                            	    COMMA467_tree = (Object)adaptor.create(COMMA467);
                            	    adaptor.addChild(root_0, COMMA467_tree);

                            	    pushFollow(FOLLOW_id_in_create_trigger_stmt3995);
                            	    column_names=id();

                            	    state._fsp--;

                            	    adaptor.addChild(root_0, column_names.getTree());
                            	    if (list_column_names==null) list_column_names=new ArrayList();
                            	    list_column_names.add(column_names.getTree());


                            	    }
                            	    break;

                            	default :
                            	    break loop170;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;

            }

            ON468=(Token)match(input,ON,FOLLOW_ON_in_create_trigger_stmt4004); 
            ON468_tree = (Object)adaptor.create(ON468);
            adaptor.addChild(root_0, ON468_tree);

            pushFollow(FOLLOW_id_in_create_trigger_stmt4008);
            table_name=id();

            state._fsp--;

            adaptor.addChild(root_0, table_name.getTree());
            // sqljet/src/Sql.g:494:20: ( FOR EACH ROW )?
            int alt173=2;
            int LA173_0 = input.LA(1);

            if ( (LA173_0==FOR) ) {
                alt173=1;
            }
            switch (alt173) {
                case 1 :
                    // sqljet/src/Sql.g:494:21: FOR EACH ROW
                    {
                    FOR469=(Token)match(input,FOR,FOLLOW_FOR_in_create_trigger_stmt4011); 
                    FOR469_tree = (Object)adaptor.create(FOR469);
                    adaptor.addChild(root_0, FOR469_tree);

                    EACH470=(Token)match(input,EACH,FOLLOW_EACH_in_create_trigger_stmt4013); 
                    EACH470_tree = (Object)adaptor.create(EACH470);
                    adaptor.addChild(root_0, EACH470_tree);

                    ROW471=(Token)match(input,ROW,FOLLOW_ROW_in_create_trigger_stmt4015); 
                    ROW471_tree = (Object)adaptor.create(ROW471);
                    adaptor.addChild(root_0, ROW471_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:494:36: ( WHEN expr )?
            int alt174=2;
            int LA174_0 = input.LA(1);

            if ( (LA174_0==WHEN) ) {
                alt174=1;
            }
            switch (alt174) {
                case 1 :
                    // sqljet/src/Sql.g:494:37: WHEN expr
                    {
                    WHEN472=(Token)match(input,WHEN,FOLLOW_WHEN_in_create_trigger_stmt4020); 
                    WHEN472_tree = (Object)adaptor.create(WHEN472);
                    adaptor.addChild(root_0, WHEN472_tree);

                    pushFollow(FOLLOW_expr_in_create_trigger_stmt4022);
                    expr473=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr473.getTree());

                    }
                    break;

            }

            BEGIN474=(Token)match(input,BEGIN,FOLLOW_BEGIN_in_create_trigger_stmt4028); 
            BEGIN474_tree = (Object)adaptor.create(BEGIN474);
            adaptor.addChild(root_0, BEGIN474_tree);

            // sqljet/src/Sql.g:495:9: ( ( update_stmt | insert_stmt | delete_stmt | select_stmt ) SEMI )+
            int cnt176=0;
            loop176:
            do {
                int alt176=2;
                int LA176_0 = input.LA(1);

                if ( (LA176_0==REPLACE||LA176_0==SELECT||LA176_0==INSERT||LA176_0==UPDATE||LA176_0==DELETE) ) {
                    alt176=1;
                }


                switch (alt176) {
            	case 1 :
            	    // sqljet/src/Sql.g:495:10: ( update_stmt | insert_stmt | delete_stmt | select_stmt ) SEMI
            	    {
            	    // sqljet/src/Sql.g:495:10: ( update_stmt | insert_stmt | delete_stmt | select_stmt )
            	    int alt175=4;
            	    switch ( input.LA(1) ) {
            	    case UPDATE:
            	        {
            	        alt175=1;
            	        }
            	        break;
            	    case REPLACE:
            	    case INSERT:
            	        {
            	        alt175=2;
            	        }
            	        break;
            	    case DELETE:
            	        {
            	        alt175=3;
            	        }
            	        break;
            	    case SELECT:
            	        {
            	        alt175=4;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 175, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt175) {
            	        case 1 :
            	            // sqljet/src/Sql.g:495:11: update_stmt
            	            {
            	            pushFollow(FOLLOW_update_stmt_in_create_trigger_stmt4032);
            	            update_stmt475=update_stmt();

            	            state._fsp--;

            	            adaptor.addChild(root_0, update_stmt475.getTree());

            	            }
            	            break;
            	        case 2 :
            	            // sqljet/src/Sql.g:495:25: insert_stmt
            	            {
            	            pushFollow(FOLLOW_insert_stmt_in_create_trigger_stmt4036);
            	            insert_stmt476=insert_stmt();

            	            state._fsp--;

            	            adaptor.addChild(root_0, insert_stmt476.getTree());

            	            }
            	            break;
            	        case 3 :
            	            // sqljet/src/Sql.g:495:39: delete_stmt
            	            {
            	            pushFollow(FOLLOW_delete_stmt_in_create_trigger_stmt4040);
            	            delete_stmt477=delete_stmt();

            	            state._fsp--;

            	            adaptor.addChild(root_0, delete_stmt477.getTree());

            	            }
            	            break;
            	        case 4 :
            	            // sqljet/src/Sql.g:495:53: select_stmt
            	            {
            	            pushFollow(FOLLOW_select_stmt_in_create_trigger_stmt4044);
            	            select_stmt478=select_stmt();

            	            state._fsp--;

            	            adaptor.addChild(root_0, select_stmt478.getTree());

            	            }
            	            break;

            	    }

            	    SEMI479=(Token)match(input,SEMI,FOLLOW_SEMI_in_create_trigger_stmt4047); 
            	    SEMI479_tree = (Object)adaptor.create(SEMI479);
            	    adaptor.addChild(root_0, SEMI479_tree);


            	    }
            	    break;

            	default :
            	    if ( cnt176 >= 1 ) break loop176;
                        EarlyExitException eee =
                            new EarlyExitException(176, input);
                        throw eee;
                }
                cnt176++;
            } while (true);

            END480=(Token)match(input,END,FOLLOW_END_in_create_trigger_stmt4051); 
            END480_tree = (Object)adaptor.create(END480);
            adaptor.addChild(root_0, END480_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "create_trigger_stmt"

    public static class drop_trigger_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "drop_trigger_stmt"
    // sqljet/src/Sql.g:498:1: drop_trigger_stmt : DROP TRIGGER ( IF EXISTS )? (database_name= id DOT )? trigger_name= id ;
    public final SqlParser.drop_trigger_stmt_return drop_trigger_stmt() throws RecognitionException {
        SqlParser.drop_trigger_stmt_return retval = new SqlParser.drop_trigger_stmt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DROP481=null;
        Token TRIGGER482=null;
        Token IF483=null;
        Token EXISTS484=null;
        Token DOT485=null;
        SqlParser.id_return database_name = null;

        SqlParser.id_return trigger_name = null;


        Object DROP481_tree=null;
        Object TRIGGER482_tree=null;
        Object IF483_tree=null;
        Object EXISTS484_tree=null;
        Object DOT485_tree=null;

        try {
            // sqljet/src/Sql.g:498:18: ( DROP TRIGGER ( IF EXISTS )? (database_name= id DOT )? trigger_name= id )
            // sqljet/src/Sql.g:498:20: DROP TRIGGER ( IF EXISTS )? (database_name= id DOT )? trigger_name= id
            {
            root_0 = (Object)adaptor.nil();

            DROP481=(Token)match(input,DROP,FOLLOW_DROP_in_drop_trigger_stmt4059); 
            DROP481_tree = (Object)adaptor.create(DROP481);
            adaptor.addChild(root_0, DROP481_tree);

            TRIGGER482=(Token)match(input,TRIGGER,FOLLOW_TRIGGER_in_drop_trigger_stmt4061); 
            TRIGGER482_tree = (Object)adaptor.create(TRIGGER482);
            adaptor.addChild(root_0, TRIGGER482_tree);

            // sqljet/src/Sql.g:498:33: ( IF EXISTS )?
            int alt177=2;
            int LA177_0 = input.LA(1);

            if ( (LA177_0==IF) ) {
                int LA177_1 = input.LA(2);

                if ( (LA177_1==EXISTS) ) {
                    alt177=1;
                }
            }
            switch (alt177) {
                case 1 :
                    // sqljet/src/Sql.g:498:34: IF EXISTS
                    {
                    IF483=(Token)match(input,IF,FOLLOW_IF_in_drop_trigger_stmt4064); 
                    IF483_tree = (Object)adaptor.create(IF483);
                    adaptor.addChild(root_0, IF483_tree);

                    EXISTS484=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_drop_trigger_stmt4066); 
                    EXISTS484_tree = (Object)adaptor.create(EXISTS484);
                    adaptor.addChild(root_0, EXISTS484_tree);


                    }
                    break;

            }

            // sqljet/src/Sql.g:498:46: (database_name= id DOT )?
            int alt178=2;
            int LA178_0 = input.LA(1);

            if ( (LA178_0==ID||LA178_0==STRING) ) {
                int LA178_1 = input.LA(2);

                if ( (LA178_1==DOT) ) {
                    alt178=1;
                }
            }
            else if ( ((LA178_0>=EXPLAIN && LA178_0<=PLAN)||(LA178_0>=INDEXED && LA178_0<=BY)||(LA178_0>=OR && LA178_0<=ESCAPE)||(LA178_0>=IS && LA178_0<=BETWEEN)||LA178_0==COLLATE||(LA178_0>=DISTINCT && LA178_0<=THEN)||(LA178_0>=CURRENT_TIME && LA178_0<=CURRENT_TIMESTAMP)||(LA178_0>=RAISE && LA178_0<=ROW)) ) {
                int LA178_2 = input.LA(2);

                if ( (LA178_2==DOT) ) {
                    alt178=1;
                }
            }
            switch (alt178) {
                case 1 :
                    // sqljet/src/Sql.g:498:47: database_name= id DOT
                    {
                    pushFollow(FOLLOW_id_in_drop_trigger_stmt4073);
                    database_name=id();

                    state._fsp--;

                    adaptor.addChild(root_0, database_name.getTree());
                    DOT485=(Token)match(input,DOT,FOLLOW_DOT_in_drop_trigger_stmt4075); 
                    DOT485_tree = (Object)adaptor.create(DOT485);
                    adaptor.addChild(root_0, DOT485_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_drop_trigger_stmt4081);
            trigger_name=id();

            state._fsp--;

            adaptor.addChild(root_0, trigger_name.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "drop_trigger_stmt"

    public static class id_core_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "id_core"
    // sqljet/src/Sql.g:501:1: id_core : str= ( ID | STRING ) ;
    public final SqlParser.id_core_return id_core() throws RecognitionException {
        SqlParser.id_core_return retval = new SqlParser.id_core_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token str=null;

        Object str_tree=null;

        try {
            // sqljet/src/Sql.g:501:8: (str= ( ID | STRING ) )
            // sqljet/src/Sql.g:501:10: str= ( ID | STRING )
            {
            root_0 = (Object)adaptor.nil();

            str=(Token)input.LT(1);
            if ( input.LA(1)==ID||input.LA(1)==STRING ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(str));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             str.setText(unquoteId((str!=null?str.getText():null)));

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "id_core"

    public static class id_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "id"
    // sqljet/src/Sql.g:505:1: id : ( id_core | keyword );
    public final SqlParser.id_return id() throws RecognitionException {
        SqlParser.id_return retval = new SqlParser.id_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.id_core_return id_core486 = null;

        SqlParser.keyword_return keyword487 = null;



        try {
            // sqljet/src/Sql.g:505:3: ( id_core | keyword )
            int alt179=2;
            int LA179_0 = input.LA(1);

            if ( (LA179_0==ID||LA179_0==STRING) ) {
                alt179=1;
            }
            else if ( ((LA179_0>=EXPLAIN && LA179_0<=PLAN)||(LA179_0>=INDEXED && LA179_0<=BY)||(LA179_0>=OR && LA179_0<=ESCAPE)||(LA179_0>=IS && LA179_0<=BETWEEN)||LA179_0==COLLATE||(LA179_0>=DISTINCT && LA179_0<=THEN)||(LA179_0>=CURRENT_TIME && LA179_0<=CURRENT_TIMESTAMP)||(LA179_0>=RAISE && LA179_0<=ROW)) ) {
                alt179=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 179, 0, input);

                throw nvae;
            }
            switch (alt179) {
                case 1 :
                    // sqljet/src/Sql.g:505:5: id_core
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_id_core_in_id4110);
                    id_core486=id_core();

                    state._fsp--;

                    adaptor.addChild(root_0, id_core486.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:505:15: keyword
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_keyword_in_id4114);
                    keyword487=keyword();

                    state._fsp--;

                    adaptor.addChild(root_0, keyword487.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "id"

    public static class keyword_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "keyword"
    // sqljet/src/Sql.g:507:1: keyword : ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COLUMN | COMMIT | CONFLICT | CONSTRAINT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GROUP | HAVING | IF | IGNORE | IMMEDIATE | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | JOIN | KEY | LEFT | LIMIT | NATURAL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE ) ;
    public final SqlParser.keyword_return keyword() throws RecognitionException {
        SqlParser.keyword_return retval = new SqlParser.keyword_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set488=null;

        Object set488_tree=null;

        try {
            // sqljet/src/Sql.g:507:8: ( ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COLUMN | COMMIT | CONFLICT | CONSTRAINT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GROUP | HAVING | IF | IGNORE | IMMEDIATE | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | JOIN | KEY | LEFT | LIMIT | NATURAL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE ) )
            // sqljet/src/Sql.g:507:10: ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COLUMN | COMMIT | CONFLICT | CONSTRAINT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GROUP | HAVING | IF | IGNORE | IMMEDIATE | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | JOIN | KEY | LEFT | LIMIT | NATURAL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE )
            {
            root_0 = (Object)adaptor.nil();

            set488=(Token)input.LT(1);
            if ( (input.LA(1)>=EXPLAIN && input.LA(1)<=PLAN)||(input.LA(1)>=INDEXED && input.LA(1)<=BY)||(input.LA(1)>=OR && input.LA(1)<=ESCAPE)||(input.LA(1)>=IS && input.LA(1)<=BETWEEN)||input.LA(1)==COLLATE||(input.LA(1)>=DISTINCT && input.LA(1)<=THEN)||(input.LA(1)>=CURRENT_TIME && input.LA(1)<=CURRENT_TIMESTAMP)||(input.LA(1)>=RAISE && input.LA(1)<=ROW) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set488));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "keyword"

    public static class id_column_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "id_column_def"
    // sqljet/src/Sql.g:626:1: id_column_def : ( id_core | keyword_column_def );
    public final SqlParser.id_column_def_return id_column_def() throws RecognitionException {
        SqlParser.id_column_def_return retval = new SqlParser.id_column_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        SqlParser.id_core_return id_core489 = null;

        SqlParser.keyword_column_def_return keyword_column_def490 = null;



        try {
            // sqljet/src/Sql.g:626:14: ( id_core | keyword_column_def )
            int alt180=2;
            int LA180_0 = input.LA(1);

            if ( (LA180_0==ID||LA180_0==STRING) ) {
                alt180=1;
            }
            else if ( ((LA180_0>=EXPLAIN && LA180_0<=PLAN)||(LA180_0>=INDEXED && LA180_0<=IN)||(LA180_0>=ISNULL && LA180_0<=BETWEEN)||(LA180_0>=LIKE && LA180_0<=MATCH)||LA180_0==COLLATE||(LA180_0>=DISTINCT && LA180_0<=THEN)||(LA180_0>=CURRENT_TIME && LA180_0<=CURRENT_TIMESTAMP)||(LA180_0>=RAISE && LA180_0<=EXISTS)||(LA180_0>=PRIMARY && LA180_0<=ADD)||(LA180_0>=VIEW && LA180_0<=ROW)) ) {
                alt180=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 180, 0, input);

                throw nvae;
            }
            switch (alt180) {
                case 1 :
                    // sqljet/src/Sql.g:626:16: id_core
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_id_core_in_id_column_def4788);
                    id_core489=id_core();

                    state._fsp--;

                    adaptor.addChild(root_0, id_core489.getTree());

                    }
                    break;
                case 2 :
                    // sqljet/src/Sql.g:626:26: keyword_column_def
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_keyword_column_def_in_id_column_def4792);
                    keyword_column_def490=keyword_column_def();

                    state._fsp--;

                    adaptor.addChild(root_0, keyword_column_def490.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "id_column_def"

    public static class keyword_column_def_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "keyword_column_def"
    // sqljet/src/Sql.g:628:1: keyword_column_def : ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COMMIT | CONFLICT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GLOB | GROUP | HAVING | IF | IGNORE | IMMEDIATE | IN | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | ISNULL | JOIN | KEY | LEFT | LIKE | LIMIT | MATCH | NATURAL | NOT | NOTNULL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REGEXP | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE ) ;
    public final SqlParser.keyword_column_def_return keyword_column_def() throws RecognitionException {
        SqlParser.keyword_column_def_return retval = new SqlParser.keyword_column_def_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set491=null;

        Object set491_tree=null;

        try {
            // sqljet/src/Sql.g:628:19: ( ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COMMIT | CONFLICT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GLOB | GROUP | HAVING | IF | IGNORE | IMMEDIATE | IN | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | ISNULL | JOIN | KEY | LEFT | LIKE | LIMIT | MATCH | NATURAL | NOT | NOTNULL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REGEXP | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE ) )
            // sqljet/src/Sql.g:628:21: ( ABORT | ADD | AFTER | ALL | ALTER | ANALYZE | AND | AS | ASC | ATTACH | AUTOINCREMENT | BEFORE | BEGIN | BETWEEN | BY | CASCADE | CASE | CAST | CHECK | COLLATE | COMMIT | CONFLICT | CREATE | CROSS | CURRENT_TIME | CURRENT_DATE | CURRENT_TIMESTAMP | DATABASE | DEFAULT | DEFERRABLE | DEFERRED | DELETE | DESC | DETACH | DISTINCT | DROP | EACH | ELSE | END | ESCAPE | EXCEPT | EXCLUSIVE | EXISTS | EXPLAIN | FAIL | FOR | FOREIGN | FROM | GLOB | GROUP | HAVING | IF | IGNORE | IMMEDIATE | IN | INDEX | INDEXED | INITIALLY | INNER | INSERT | INSTEAD | INTERSECT | INTO | IS | ISNULL | JOIN | KEY | LEFT | LIKE | LIMIT | MATCH | NATURAL | NOT | NOTNULL | NULL | OF | OFFSET | ON | OR | ORDER | OUTER | PLAN | PRAGMA | PRIMARY | QUERY | RAISE | REFERENCES | REGEXP | REINDEX | RELEASE | RENAME | REPLACE | RESTRICT | ROLLBACK | ROW | SAVEPOINT | SELECT | SET | TABLE | TEMPORARY | THEN | TO | TRANSACTION | TRIGGER | UNION | UNIQUE | UPDATE | USING | VACUUM | VALUES | VIEW | VIRTUAL | WHEN | WHERE )
            {
            root_0 = (Object)adaptor.nil();

            set491=(Token)input.LT(1);
            if ( (input.LA(1)>=EXPLAIN && input.LA(1)<=PLAN)||(input.LA(1)>=INDEXED && input.LA(1)<=IN)||(input.LA(1)>=ISNULL && input.LA(1)<=BETWEEN)||(input.LA(1)>=LIKE && input.LA(1)<=MATCH)||input.LA(1)==COLLATE||(input.LA(1)>=DISTINCT && input.LA(1)<=THEN)||(input.LA(1)>=CURRENT_TIME && input.LA(1)<=CURRENT_TIMESTAMP)||(input.LA(1)>=RAISE && input.LA(1)<=EXISTS)||(input.LA(1)>=PRIMARY && input.LA(1)<=ADD)||(input.LA(1)>=VIEW && input.LA(1)<=ROW) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set491));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "keyword_column_def"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA25 dfa25 = new DFA25(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA27 dfa27 = new DFA27(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA38 dfa38 = new DFA38(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA32 dfa32 = new DFA32(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA44 dfa44 = new DFA44(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA49 dfa49 = new DFA49(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA66 dfa66 = new DFA66(this);
    protected DFA67 dfa67 = new DFA67(this);
    protected DFA68 dfa68 = new DFA68(this);
    protected DFA71 dfa71 = new DFA71(this);
    protected DFA69 dfa69 = new DFA69(this);
    protected DFA70 dfa70 = new DFA70(this);
    protected DFA74 dfa74 = new DFA74(this);
    protected DFA73 dfa73 = new DFA73(this);
    protected DFA72 dfa72 = new DFA72(this);
    protected DFA76 dfa76 = new DFA76(this);
    protected DFA75 dfa75 = new DFA75(this);
    protected DFA83 dfa83 = new DFA83(this);
    protected DFA77 dfa77 = new DFA77(this);
    protected DFA79 dfa79 = new DFA79(this);
    protected DFA80 dfa80 = new DFA80(this);
    protected DFA82 dfa82 = new DFA82(this);
    protected DFA93 dfa93 = new DFA93(this);
    protected DFA118 dfa118 = new DFA118(this);
    protected DFA121 dfa121 = new DFA121(this);
    protected DFA122 dfa122 = new DFA122(this);
    protected DFA123 dfa123 = new DFA123(this);
    protected DFA125 dfa125 = new DFA125(this);
    protected DFA126 dfa126 = new DFA126(this);
    protected DFA127 dfa127 = new DFA127(this);
    protected DFA128 dfa128 = new DFA128(this);
    protected DFA129 dfa129 = new DFA129(this);
    protected DFA130 dfa130 = new DFA130(this);
    protected DFA132 dfa132 = new DFA132(this);
    protected DFA141 dfa141 = new DFA141(this);
    protected DFA142 dfa142 = new DFA142(this);
    protected DFA143 dfa143 = new DFA143(this);
    protected DFA147 dfa147 = new DFA147(this);
    protected DFA167 dfa167 = new DFA167(this);
    protected DFA168 dfa168 = new DFA168(this);
    static final String DFA1_eotS =
        "\26\uffff";
    static final String DFA1_eofS =
        "\1\1\25\uffff";
    static final String DFA1_minS =
        "\1\41\25\uffff";
    static final String DFA1_maxS =
        "\1\u00a6\25\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\2\1\1\23\uffff";
    static final String DFA1_specialS =
        "\26\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\60\uffff\1\2\16\uffff\1\2\2\uffff\2\2\1\uffff\5\2\11\uffff"+
            "\1\2\14\uffff\1\2\3\uffff\1\2\1\uffff\2\2\4\uffff\1\2\1\uffff"+
            "\2\2\1\uffff\1\2\21\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()* loopback of 138:32: ( sql_stmt SEMI )*";
        }
    }
    static final String DFA5_eotS =
        "\25\uffff";
    static final String DFA5_eofS =
        "\25\uffff";
    static final String DFA5_minS =
        "\1\41\24\uffff";
    static final String DFA5_maxS =
        "\1\u00a6\24\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA5_specialS =
        "\25\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\60\uffff\1\2\16\uffff\1\2\2\uffff\2\2\1\uffff\5\2\11\uffff"+
            "\1\2\14\uffff\1\2\3\uffff\1\2\1\uffff\2\2\4\uffff\1\2\1\uffff"+
            "\2\2\1\uffff\1\2\21\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "142:11: ( EXPLAIN ( QUERY PLAN )? )?";
        }
    }
    static final String DFA4_eotS =
        "\25\uffff";
    static final String DFA4_eofS =
        "\25\uffff";
    static final String DFA4_minS =
        "\1\42\24\uffff";
    static final String DFA4_maxS =
        "\1\u00a6\24\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA4_specialS =
        "\25\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1\57\uffff\1\2\16\uffff\1\2\2\uffff\2\2\1\uffff\5\2\11\uffff"+
            "\1\2\14\uffff\1\2\3\uffff\1\2\1\uffff\2\2\4\uffff\1\2\1\uffff"+
            "\2\2\1\uffff\1\2\21\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "142:20: ( QUERY PLAN )?";
        }
    }
    static final String DFA6_eotS =
        "\42\uffff";
    static final String DFA6_eofS =
        "\42\uffff";
    static final String DFA6_minS =
        "\1\122\20\uffff\1\u0094\1\u0095\2\uffff\1\u0095\14\uffff";
    static final String DFA6_maxS =
        "\1\u00a6\20\uffff\2\u00ac\2\uffff\1\u00ac\14\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff\1\11\1\12\1\13"+
        "\1\14\1\15\1\16\1\17\2\uffff\1\23\1\20\1\uffff\1\30\1\21\1\24\1"+
        "\26\1\uffff\1\22\1\25\1\27\1\31\3\uffff";
    static final String DFA6_specialS =
        "\42\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\15\16\uffff\1\16\2\uffff\1\1\1\2\1\uffff\1\3\1\4\1\5\1\6"+
            "\1\10\11\uffff\1\7\14\uffff\1\10\3\uffff\1\12\1\uffff\1\13\1"+
            "\14\4\uffff\1\15\1\uffff\1\17\1\20\1\uffff\1\21\21\uffff\1\22"+
            "\1\23",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\24\1\27\1\25\6\uffff\1\31\14\uffff\1\30\1\31\1\26",
            "\1\33\24\uffff\1\34\1\35\1\36",
            "",
            "",
            "\1\27\24\uffff\1\30\1\uffff\1\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "144:1: sql_stmt_core : ( pragma_stmt | attach_stmt | detach_stmt | analyze_stmt | reindex_stmt | vacuum_stmt | select_stmt | insert_stmt | update_stmt | delete_stmt | begin_stmt | commit_stmt | rollback_stmt | savepoint_stmt | release_stmt | create_virtual_table_stmt | create_table_stmt | drop_table_stmt | alter_table_stmt | create_view_stmt | drop_view_stmt | create_index_stmt | drop_index_stmt | create_trigger_stmt | drop_trigger_stmt );";
        }
    }
    static final String DFA8_eotS =
        "\25\uffff";
    static final String DFA8_eofS =
        "\1\uffff\2\4\22\uffff";
    static final String DFA8_minS =
        "\1\41\2\40\22\uffff";
    static final String DFA8_maxS =
        "\1\u00b3\2\u0087\22\uffff";
    static final String DFA8_acceptS =
        "\3\uffff\1\1\1\2\20\uffff";
    static final String DFA8_specialS =
        "\25\uffff}>";
    static final String[] DFA8_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\4\3\uffff\1\3\1\4\1\uffff\1\4\106\uffff\2\4\7\uffff\1\4"+
            "\17\uffff\1\4",
            "\1\4\3\uffff\1\3\1\4\1\uffff\1\4\106\uffff\2\4\7\uffff\1\4"+
            "\17\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "176:23: (database_name= id DOT )?";
        }
    }
    static final String DFA10_eotS =
        "\142\uffff";
    static final String DFA10_eofS =
        "\1\1\34\uffff\1\1\104\uffff";
    static final String DFA10_minS =
        "\1\40\34\uffff\1\40\2\uffff\2\41\6\44\72\uffff";
    static final String DFA10_maxS =
        "\1\u00b3\34\uffff\1\u00b3\2\uffff\2\u00b3\1\46\3\165\1\46\1\125"+
        "\72\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\2\51\uffff\1\1\66\uffff";
    static final String DFA10_specialS =
        "\142\uffff}>";
    static final String[] DFA10_transitionS = {
            "\4\1\1\uffff\2\1\1\uffff\1\35\2\1\2\uffff\2\1\2\uffff\3\1\27"+
            "\uffff\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\1\3\53\1\uffff\6\53\1\uffff\1\53\2\1\2\uffff\3\53\20\uffff"+
            "\2\53\4\uffff\44\53\1\46\1\47\1\53\1\43\1\53\1\44\1\45\1\53"+
            "\1\40\1\41\1\42\73\53",
            "",
            "",
            "\3\1\1\53\2\1\1\uffff\3\1\1\uffff\1\1\4\uffff\3\1\27\uffff"+
            "\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "\3\1\1\53\6\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2\1\4\uffff"+
            "\152\1",
            "\1\53\1\uffff\1\1",
            "\1\53\115\uffff\1\1\2\uffff\1\1",
            "\1\53\120\uffff\1\1",
            "\1\53\120\uffff\1\1",
            "\1\53\1\uffff\1\1",
            "\1\53\60\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "()* loopback of 178:18: ( OR or_subexpr )*";
        }
    }
    static final String DFA11_eotS =
        "\143\uffff";
    static final String DFA11_eofS =
        "\1\1\35\uffff\1\1\104\uffff";
    static final String DFA11_minS =
        "\1\40\35\uffff\1\40\2\uffff\2\41\6\44\72\uffff";
    static final String DFA11_maxS =
        "\1\u00b3\35\uffff\1\u00b3\2\uffff\2\u00b3\1\46\3\165\1\46\1\125"+
        "\72\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\2\52\uffff\1\1\66\uffff";
    static final String DFA11_specialS =
        "\143\uffff}>";
    static final String[] DFA11_transitionS = {
            "\4\1\1\uffff\2\1\1\uffff\1\1\1\36\1\1\2\uffff\2\1\2\uffff\3"+
            "\1\27\uffff\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\1\3\54\1\uffff\6\54\1\uffff\1\54\2\1\2\uffff\3\54\20\uffff"+
            "\2\54\4\uffff\44\54\1\47\1\50\1\54\1\44\1\54\1\45\1\46\1\54"+
            "\1\41\1\42\1\43\73\54",
            "",
            "",
            "\3\1\1\54\2\1\1\uffff\3\1\1\uffff\1\1\4\uffff\3\1\27\uffff"+
            "\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "\3\1\1\54\6\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2\1\4\uffff"+
            "\152\1",
            "\1\54\1\uffff\1\1",
            "\1\54\115\uffff\1\1\2\uffff\1\1",
            "\1\54\120\uffff\1\1",
            "\1\54\120\uffff\1\1",
            "\1\54\1\uffff\1\1",
            "\1\54\60\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "()* loopback of 180:25: ( AND and_subexpr )*";
        }
    }
    static final String DFA12_eotS =
        "\171\uffff";
    static final String DFA12_eofS =
        "\1\11\5\uffff\2\11\161\uffff";
    static final String DFA12_minS =
        "\1\40\5\uffff\2\40\57\uffff\2\41\6\44\72\uffff";
    static final String DFA12_maxS =
        "\1\u00b3\5\uffff\1\170\1\u00b3\57\uffff\2\u00b3\1\46\3\165\1\46"+
        "\1\125\72\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\7\uffff\1\2\157\uffff";
    static final String DFA12_specialS =
        "\171\uffff}>";
    static final String[] DFA12_transitionS = {
            "\4\11\1\uffff\2\11\1\1\3\11\1\1\1\uffff\2\11\2\1\1\6\1\11\1"+
            "\7\10\1\17\uffff\12\11\2\uffff\1\11\1\uffff\3\11\3\uffff\125"+
            "\11",
            "",
            "",
            "",
            "",
            "",
            "\1\11\6\uffff\1\1\5\uffff\2\11\3\uffff\1\1\73\uffff\2\11\1"+
            "\uffff\1\11\1\uffff\2\11\1\uffff\3\11",
            "\1\11\3\1\1\uffff\6\1\1\uffff\1\1\2\11\2\uffff\3\1\20\uffff"+
            "\2\1\4\uffff\44\1\1\75\1\76\1\1\1\72\1\1\1\73\1\74\1\1\1\67"+
            "\1\70\1\71\73\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\11\1\1\2\11\1\uffff\3\11\1\uffff\1\11\4\uffff\3\11\27\uffff"+
            "\12\11\2\uffff\1\11\1\uffff\3\11\3\uffff\125\11",
            "\3\11\1\1\6\11\1\uffff\1\11\4\uffff\3\11\20\uffff\2\11\4\uffff"+
            "\152\11",
            "\1\1\1\uffff\1\11",
            "\1\1\115\uffff\1\11\2\uffff\1\11",
            "\1\1\120\uffff\1\11",
            "\1\1\120\uffff\1\11",
            "\1\1\1\uffff\1\11",
            "\1\1\60\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "182:34: ( cond_expr )?";
        }
    }
    static final String DFA22_eotS =
        "\23\uffff";
    static final String DFA22_eofS =
        "\23\uffff";
    static final String DFA22_minS =
        "\1\47\1\53\1\uffff\1\41\6\uffff\1\41\10\uffff";
    static final String DFA22_maxS =
        "\2\73\1\uffff\1\u00b3\6\uffff\1\u00b3\10\uffff";
    static final String DFA22_acceptS =
        "\2\uffff\1\1\1\uffff\1\4\2\uffff\1\5\1\6\4\uffff\1\2\1\3\4\uffff";
    static final String DFA22_specialS =
        "\23\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\1\3\uffff\1\3\3\uffff\3\4\1\uffff\1\7\4\10\4\2",
            "\1\12\6\uffff\1\4\1\7\4\uffff\4\2",
            "",
            "\3\16\1\uffff\2\16\1\uffff\3\16\1\uffff\1\15\4\uffff\3\16\27"+
            "\uffff\12\16\2\uffff\1\16\1\uffff\3\16\3\uffff\125\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\16\1\uffff\2\16\1\uffff\3\16\1\uffff\1\15\4\uffff\3\16\27"+
            "\uffff\12\16\2\uffff\1\16\1\uffff\3\16\3\uffff\125\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "184:1: cond_expr : ( ( NOT )? match_op match_expr= eq_subexpr ( ESCAPE escape_expr= eq_subexpr )? -> ^( match_op $match_expr ( NOT )? ( ^( ESCAPE $escape_expr) )? ) | ( NOT )? IN LPAREN expr ( COMMA expr )* RPAREN -> ^( IN_VALUES ( NOT )? ^( IN ( expr )+ ) ) | ( NOT )? IN (database_name= id DOT )? table_name= id -> ^( IN_TABLE ( NOT )? ^( IN ^( $table_name ( $database_name)? ) ) ) | ( ISNULL -> IS_NULL | NOTNULL -> NOT_NULL | IS NULL -> IS_NULL | NOT NULL -> NOT_NULL | IS NOT NULL -> NOT_NULL ) | ( NOT )? BETWEEN e1= eq_subexpr AND e2= eq_subexpr -> ^( BETWEEN ( NOT )? ^( AND $e1 $e2) ) | ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+ );";
        }
    }
    static final String DFA14_eotS =
        "\144\uffff";
    static final String DFA14_eofS =
        "\2\2\142\uffff";
    static final String DFA14_minS =
        "\2\40\40\uffff\2\41\6\44\72\uffff";
    static final String DFA14_maxS =
        "\2\u00b3\40\uffff\2\u00b3\1\46\3\165\1\46\1\125\72\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\52\uffff\1\1\66\uffff";
    static final String DFA14_specialS =
        "\144\uffff}>";
    static final String[] DFA14_transitionS = {
            "\4\2\1\uffff\2\2\1\uffff\2\2\1\1\2\uffff\2\2\2\uffff\3\2\27"+
            "\uffff\12\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "\1\2\3\55\1\uffff\6\55\1\uffff\1\55\2\2\2\uffff\3\55\20\uffff"+
            "\2\55\4\uffff\44\55\1\50\1\51\1\55\1\45\1\55\1\46\1\47\1\55"+
            "\1\42\1\43\1\44\73\55",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\2\1\55\2\2\1\uffff\3\2\1\uffff\1\2\4\uffff\3\2\27\uffff"+
            "\12\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "\3\2\1\55\6\2\1\uffff\1\2\4\uffff\3\2\20\uffff\2\2\4\uffff"+
            "\152\2",
            "\1\55\1\uffff\1\2",
            "\1\55\115\uffff\1\2\2\uffff\1\2",
            "\1\55\120\uffff\1\2",
            "\1\55\120\uffff\1\2",
            "\1\55\1\uffff\1\2",
            "\1\55\60\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "185:41: ( ESCAPE escape_expr= eq_subexpr )?";
        }
    }
    static final String DFA18_eotS =
        "\103\uffff";
    static final String DFA18_eofS =
        "\1\uffff\2\3\100\uffff";
    static final String DFA18_minS =
        "\1\41\2\40\100\uffff";
    static final String DFA18_maxS =
        "\3\u00b3\100\uffff";
    static final String DFA18_acceptS =
        "\3\uffff\1\2\36\uffff\1\1\40\uffff";
    static final String DFA18_specialS =
        "\103\uffff}>";
    static final String[] DFA18_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\4\3\1\42\2\3\1\uffff\3\3\2\uffff\2\3\2\uffff\3\3\27\uffff"+
            "\12\3\2\uffff\1\3\1\uffff\3\3\3\uffff\125\3",
            "\4\3\1\42\2\3\1\uffff\3\3\2\uffff\2\3\2\uffff\3\3\27\uffff"+
            "\12\3\2\uffff\1\3\1\uffff\3\3\3\uffff\125\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "187:13: (database_name= id DOT )?";
        }
    }
    static final String DFA21_eotS =
        "\41\uffff";
    static final String DFA21_eofS =
        "\1\1\40\uffff";
    static final String DFA21_minS =
        "\1\40\40\uffff";
    static final String DFA21_maxS =
        "\1\u00b3\40\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA21_specialS =
        "\41\uffff}>";
    static final String[] DFA21_transitionS = {
            "\4\1\1\uffff\2\1\1\uffff\3\1\2\uffff\2\1\2\uffff\3\1\4\40\23"+
            "\uffff\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "()+ loopback of 192:5: ( ( EQUALS | EQUALS2 | NOT_EQUALS | NOT_EQUALS2 ) eq_subexpr )+";
        }
    }
    static final String DFA23_eotS =
        "\52\uffff";
    static final String DFA23_eofS =
        "\1\1\51\uffff";
    static final String DFA23_minS =
        "\1\40\51\uffff";
    static final String DFA23_maxS =
        "\1\u00b3\51\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\2\47\uffff\1\1";
    static final String DFA23_specialS =
        "\52\uffff}>";
    static final String[] DFA23_transitionS = {
            "\4\1\1\uffff\7\1\1\uffff\17\1\4\51\13\uffff\12\1\2\uffff\1\1"+
            "\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "()* loopback of 197:25: ( ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ ) neq_subexpr )*";
        }
    }
    static final String DFA24_eotS =
        "\53\uffff";
    static final String DFA24_eofS =
        "\1\1\52\uffff";
    static final String DFA24_minS =
        "\1\40\52\uffff";
    static final String DFA24_maxS =
        "\1\u00b3\52\uffff";
    static final String DFA24_acceptS =
        "\1\uffff\1\2\50\uffff\1\1";
    static final String DFA24_specialS =
        "\53\uffff}>";
    static final String[] DFA24_transitionS = {
            "\4\1\1\uffff\7\1\1\uffff\23\1\4\52\7\uffff\12\1\2\uffff\1\1"+
            "\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "()* loopback of 199:26: ( ( SHIFT_LEFT | SHIFT_RIGHT | AMPERSAND | PIPE ) bit_subexpr )*";
        }
    }
    static final String DFA25_eotS =
        "\54\uffff";
    static final String DFA25_eofS =
        "\1\1\53\uffff";
    static final String DFA25_minS =
        "\1\40\53\uffff";
    static final String DFA25_maxS =
        "\1\u00b3\53\uffff";
    static final String DFA25_acceptS =
        "\1\uffff\1\2\51\uffff\1\1";
    static final String DFA25_specialS =
        "\54\uffff}>";
    static final String[] DFA25_transitionS = {
            "\4\1\1\uffff\7\1\1\uffff\27\1\2\53\5\uffff\12\1\2\uffff\1\1"+
            "\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
    static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
    static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
    static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
    static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
    static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }
        public String getDescription() {
            return "()* loopback of 201:26: ( ( PLUS | MINUS ) add_subexpr )*";
        }
    }
    static final String DFA26_eotS =
        "\55\uffff";
    static final String DFA26_eofS =
        "\1\1\54\uffff";
    static final String DFA26_minS =
        "\1\40\54\uffff";
    static final String DFA26_maxS =
        "\1\u00b3\54\uffff";
    static final String DFA26_acceptS =
        "\1\uffff\1\2\52\uffff\1\1";
    static final String DFA26_specialS =
        "\55\uffff}>";
    static final String[] DFA26_transitionS = {
            "\4\1\1\uffff\7\1\1\uffff\31\1\3\54\2\uffff\12\1\2\uffff\1\1"+
            "\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "()* loopback of 203:26: ( ( ASTERISK | SLASH | PERCENT ) mul_subexpr )*";
        }
    }
    static final String DFA27_eotS =
        "\56\uffff";
    static final String DFA27_eofS =
        "\1\1\55\uffff";
    static final String DFA27_minS =
        "\1\40\55\uffff";
    static final String DFA27_maxS =
        "\1\u00b3\55\uffff";
    static final String DFA27_acceptS =
        "\1\uffff\1\2\53\uffff\1\1";
    static final String DFA27_specialS =
        "\56\uffff}>";
    static final String[] DFA27_transitionS = {
            "\4\1\1\uffff\7\1\1\uffff\34\1\1\55\1\uffff\12\1\2\uffff\1\1"+
            "\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "()* loopback of 205:26: ( DOUBLE_PIPE con_subexpr )*";
        }
    }
    static final String DFA28_eotS =
        "\23\uffff";
    static final String DFA28_eofS =
        "\23\uffff";
    static final String DFA28_minS =
        "\1\41\22\uffff";
    static final String DFA28_maxS =
        "\1\u00b3\22\uffff";
    static final String DFA28_acceptS =
        "\1\uffff\1\1\20\uffff\1\2";
    static final String DFA28_specialS =
        "\23\uffff}>";
    static final String[] DFA28_transitionS = {
            "\3\1\1\uffff\2\1\1\22\3\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2"+
            "\22\4\uffff\1\22\151\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "207:1: con_subexpr : ( unary_subexpr | unary_op unary_subexpr -> ^( unary_op unary_subexpr ) );";
        }
    }
    static final String DFA29_eotS =
        "\74\uffff";
    static final String DFA29_eofS =
        "\2\2\72\uffff";
    static final String DFA29_minS =
        "\2\40\72\uffff";
    static final String DFA29_maxS =
        "\1\u00b3\1\170\72\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\54\uffff\1\1\14\uffff";
    static final String DFA29_specialS =
        "\74\uffff}>";
    static final String[] DFA29_transitionS = {
            "\4\2\1\uffff\7\2\1\uffff\35\2\1\uffff\1\1\11\2\2\uffff\1\2\1"+
            "\uffff\3\2\3\uffff\125\2",
            "\1\2\14\uffff\2\2\35\uffff\1\57\41\uffff\2\2\1\uffff\1\2\1"+
            "\uffff\2\2\1\uffff\3\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "211:26: ( COLLATE collation_name= ID )?";
        }
    }
    static final String DFA38_eotS =
        "\u0145\uffff";
    static final String DFA38_eofS =
        "\3\uffff\1\1\1\uffff\4\1\3\uffff\1\21\u0138\uffff";
    static final String DFA38_minS =
        "\1\41\2\uffff\1\40\1\uffff\4\40\3\uffff\1\40\1\44\1\uffff\1\41\1"+
        "\44\u0134\uffff";
    static final String DFA38_maxS =
        "\1\u00b3\2\uffff\1\u00b3\1\uffff\4\u00b3\3\uffff\1\u00b3\1\54\1"+
        "\uffff\1\u00b3\1\54\u0134\uffff";
    static final String DFA38_acceptS =
        "\1\uffff\1\1\7\uffff\1\2\4\uffff\1\5\2\uffff\1\3\u00eb\uffff\1\4"+
        "\57\uffff\1\6\2\uffff\1\7\22\uffff\1\10\1\uffff";
    static final String DFA38_specialS =
        "\u0145\uffff}>";
    static final String[] DFA38_transitionS = {
            "\3\21\1\uffff\2\21\1\uffff\3\21\1\uffff\1\16\4\uffff\1\21\1"+
            "\5\1\21\27\uffff\1\21\1\14\1\21\1\15\1\21\1\17\4\21\2\1\1\3"+
            "\1\1\1\6\1\7\1\10\3\11\1\20\124\21",
            "",
            "",
            "\4\1\1\21\7\1\1\uffff\35\1\1\uffff\12\1\2\uffff\1\1\1\uffff"+
            "\3\1\3\uffff\125\1",
            "",
            "\4\1\1\21\7\1\1\uffff\35\1\1\uffff\12\1\2\uffff\1\1\1\uffff"+
            "\3\1\3\uffff\125\1",
            "\4\1\1\21\7\1\1\uffff\35\1\1\uffff\12\1\2\uffff\1\1\1\uffff"+
            "\3\1\3\uffff\125\1",
            "\4\1\1\21\7\1\1\uffff\35\1\1\uffff\12\1\2\uffff\1\1\1\uffff"+
            "\3\1\3\uffff\125\1",
            "\4\1\1\21\7\1\1\uffff\35\1\1\uffff\12\1\2\uffff\1\1\1\uffff"+
            "\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "\14\21\1\u00fd\35\21\1\uffff\12\21\2\uffff\1\21\1\uffff\3\21"+
            "\3\uffff\125\21",
            "\1\21\7\uffff\1\u012d",
            "",
            "\3\u0130\1\21\6\u0130\1\uffff\1\u0130\4\uffff\3\u0130\20\uffff"+
            "\2\u0130\4\uffff\152\u0130",
            "\1\21\7\uffff\1\u0143",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
    static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
    static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
    static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
    static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
    static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
    static final short[][] DFA38_transition;

    static {
        int numStates = DFA38_transitionS.length;
        DFA38_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
        }
    }

    class DFA38 extends DFA {

        public DFA38(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 38;
            this.eot = DFA38_eot;
            this.eof = DFA38_eof;
            this.min = DFA38_min;
            this.max = DFA38_max;
            this.accept = DFA38_accept;
            this.special = DFA38_special;
            this.transition = DFA38_transition;
        }
        public String getDescription() {
            return "213:1: atom_expr : ( literal_value | bind_parameter | ( (database_name= id DOT )? table_name= id DOT )? column_name= ID -> ^( COLUMN_EXPRESSION ^( $column_name ( ^( $table_name ( $database_name)? ) )? ) ) | name= ID LPAREN ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )? RPAREN -> ^( FUNCTION_EXPRESSION $name ( DISTINCT )? ( $args)* ( ASTERISK )? ) | LPAREN expr RPAREN | CAST LPAREN expr AS type_name RPAREN | CASE (case_expr= expr )? ( when_expr )+ ( ELSE else_expr= expr )? END -> ^( CASE ( $case_expr)? ( when_expr )+ ( $else_expr)? ) | raise_function );";
        }
    }
    static final String DFA31_eotS =
        "\63\uffff";
    static final String DFA31_eofS =
        "\1\uffff\1\5\61\uffff";
    static final String DFA31_minS =
        "\1\41\1\40\61\uffff";
    static final String DFA31_maxS =
        "\2\u00b3\61\uffff";
    static final String DFA31_acceptS =
        "\2\uffff\1\1\2\uffff\1\2\55\uffff";
    static final String DFA31_specialS =
        "\63\uffff}>";
    static final String[] DFA31_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "\4\5\1\2\7\5\1\uffff\35\5\1\uffff\12\5\2\uffff\1\5\1\uffff"+
            "\3\5\3\uffff\125\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "216:5: ( (database_name= id DOT )? table_name= id DOT )?";
        }
    }
    static final String DFA30_eotS =
        "\151\uffff";
    static final String DFA30_eofS =
        "\5\uffff\1\14\2\uffff\1\14\140\uffff";
    static final String DFA30_minS =
        "\1\41\2\44\2\41\1\40\2\uffff\1\40\140\uffff";
    static final String DFA30_maxS =
        "\1\u00b3\2\44\3\u00b3\2\uffff\1\u00b3\140\uffff";
    static final String DFA30_acceptS =
        "\6\uffff\1\1\5\uffff\1\2\134\uffff";
    static final String DFA30_specialS =
        "\151\uffff}>";
    static final String[] DFA30_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\3",
            "\1\4",
            "\3\6\1\uffff\2\6\1\uffff\3\6\6\uffff\3\6\27\uffff\1\6\1\5\10"+
            "\6\2\uffff\1\6\1\uffff\3\6\3\uffff\125\6",
            "\3\6\1\uffff\2\6\1\uffff\3\6\6\uffff\3\6\27\uffff\1\6\1\10"+
            "\10\6\2\uffff\1\6\1\uffff\3\6\3\uffff\125\6",
            "\4\14\1\6\7\14\1\uffff\35\14\1\uffff\12\14\2\uffff\1\14\1\uffff"+
            "\3\14\3\uffff\125\14",
            "",
            "",
            "\4\14\1\6\7\14\1\uffff\35\14\1\uffff\12\14\2\uffff\1\14\1\uffff"+
            "\3\14\3\uffff\125\14",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "216:6: (database_name= id DOT )?";
        }
    }
    static final String DFA34_eotS =
        "\26\uffff";
    static final String DFA34_eofS =
        "\26\uffff";
    static final String DFA34_minS =
        "\1\41\25\uffff";
    static final String DFA34_maxS =
        "\1\u00b3\25\uffff";
    static final String DFA34_acceptS =
        "\1\uffff\1\1\22\uffff\1\2\1\3";
    static final String DFA34_specialS =
        "\26\uffff}>";
    static final String[] DFA34_transitionS = {
            "\3\1\1\uffff\6\1\1\uffff\1\1\1\uffff\1\25\2\uffff\3\1\20\uffff"+
            "\2\1\1\24\3\uffff\152\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;

    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }

    class DFA34 extends DFA {

        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "217:20: ( ( DISTINCT )? args+= expr ( COMMA args+= expr )* | ASTERISK )?";
        }
    }
    static final String DFA32_eotS =
        "\47\uffff";
    static final String DFA32_eofS =
        "\47\uffff";
    static final String DFA32_minS =
        "\2\41\45\uffff";
    static final String DFA32_maxS =
        "\2\u00b3\45\uffff";
    static final String DFA32_acceptS =
        "\2\uffff\1\2\22\uffff\1\1\21\uffff";
    static final String DFA32_specialS =
        "\47\uffff}>";
    static final String[] DFA32_transitionS = {
            "\3\2\1\uffff\6\2\1\uffff\1\2\4\uffff\3\2\20\uffff\2\2\4\uffff"+
            "\3\2\1\1\146\2",
            "\3\25\1\2\6\25\1\uffff\1\25\4\uffff\3\25\20\uffff\2\25\4\uffff"+
            "\152\25",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
    static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
    static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
    static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
    static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
    static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }
        public String getDescription() {
            return "217:21: ( DISTINCT )?";
        }
    }
    static final String DFA35_eotS =
        "\47\uffff";
    static final String DFA35_eofS =
        "\47\uffff";
    static final String DFA35_minS =
        "\1\41\20\uffff\1\41\25\uffff";
    static final String DFA35_maxS =
        "\1\u00b3\20\uffff\1\u00b3\25\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\1\22\uffff\1\2\22\uffff";
    static final String DFA35_specialS =
        "\47\uffff}>";
    static final String[] DFA35_transitionS = {
            "\3\1\1\uffff\6\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2\1\4\uffff"+
            "\11\1\1\21\140\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\24\1\1\6\24\1\uffff\1\24\4\uffff\3\24\20\uffff\2\24\4\uffff"+
            "\152\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "222:10: (case_expr= expr )?";
        }
    }
    static final String DFA40_eotS =
        "\63\uffff";
    static final String DFA40_eofS =
        "\1\uffff\1\5\61\uffff";
    static final String DFA40_minS =
        "\1\134\1\40\61\uffff";
    static final String DFA40_maxS =
        "\1\136\1\u00b3\61\uffff";
    static final String DFA40_acceptS =
        "\2\uffff\1\3\1\4\1\2\1\1\55\uffff";
    static final String DFA40_specialS =
        "\63\uffff}>";
    static final String[] DFA40_transitionS = {
            "\1\1\1\2\1\3",
            "\4\5\1\uffff\7\5\1\uffff\35\5\1\uffff\12\5\1\4\1\uffff\1\5"+
            "\1\uffff\3\5\3\uffff\125\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        public String getDescription() {
            return "239:1: bind_parameter : ( QUESTION -> BIND | QUESTION position= INTEGER -> ^( BIND $position) | COLON name= id -> ^( BIND_NAME $name) | AT name= id -> ^( BIND_NAME $name) );";
        }
    }
    static final String DFA42_eotS =
        "\20\uffff";
    static final String DFA42_eofS =
        "\1\1\17\uffff";
    static final String DFA42_minS =
        "\1\40\17\uffff";
    static final String DFA42_maxS =
        "\1\u00a0\17\uffff";
    static final String DFA42_acceptS =
        "\1\uffff\1\2\15\uffff\1\1";
    static final String DFA42_specialS =
        "\20\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\1\6\uffff\1\1\4\uffff\3\1\3\uffff\1\1\30\uffff\1\1\1\17\70"+
            "\uffff\1\1\23\uffff\2\1\2\uffff\2\1\1\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        public String getDescription() {
            return "()+ loopback of 250:17: (names+= ID )+";
        }
    }
    static final String DFA44_eotS =
        "\17\uffff";
    static final String DFA44_eofS =
        "\1\2\16\uffff";
    static final String DFA44_minS =
        "\1\40\16\uffff";
    static final String DFA44_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA44_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA44_specialS =
        "\17\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\2\6\uffff\1\2\4\uffff\1\1\2\2\3\uffff\1\2\30\uffff\1\2\71"+
            "\uffff\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA44_eot = DFA.unpackEncodedString(DFA44_eotS);
    static final short[] DFA44_eof = DFA.unpackEncodedString(DFA44_eofS);
    static final char[] DFA44_min = DFA.unpackEncodedStringToUnsignedChars(DFA44_minS);
    static final char[] DFA44_max = DFA.unpackEncodedStringToUnsignedChars(DFA44_maxS);
    static final short[] DFA44_accept = DFA.unpackEncodedString(DFA44_acceptS);
    static final short[] DFA44_special = DFA.unpackEncodedString(DFA44_specialS);
    static final short[][] DFA44_transition;

    static {
        int numStates = DFA44_transitionS.length;
        DFA44_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA44_transition[i] = DFA.unpackEncodedString(DFA44_transitionS[i]);
        }
    }

    class DFA44 extends DFA {

        public DFA44(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 44;
            this.eot = DFA44_eot;
            this.eof = DFA44_eof;
            this.min = DFA44_min;
            this.max = DFA44_max;
            this.accept = DFA44_accept;
            this.special = DFA44_special;
            this.transition = DFA44_transition;
        }
        public String getDescription() {
            return "250:23: ( LPAREN size1= signed_number ( COMMA size2= signed_number )? RPAREN )?";
        }
    }
    static final String DFA46_eotS =
        "\15\uffff";
    static final String DFA46_eofS =
        "\1\uffff\2\4\12\uffff";
    static final String DFA46_minS =
        "\1\41\2\40\12\uffff";
    static final String DFA46_maxS =
        "\1\u00b3\2\64\12\uffff";
    static final String DFA46_acceptS =
        "\3\uffff\1\1\1\2\10\uffff";
    static final String DFA46_specialS =
        "\15\uffff}>";
    static final String[] DFA46_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\4\3\uffff\1\3\7\uffff\1\4\7\uffff\1\4",
            "\1\4\3\uffff\1\3\7\uffff\1\4\7\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        public String getDescription() {
            return "256:21: (database_name= id DOT )?";
        }
    }
    static final String DFA49_eotS =
        "\16\uffff";
    static final String DFA49_eofS =
        "\10\uffff\1\2\5\uffff";
    static final String DFA49_minS =
        "\2\41\2\uffff\1\41\3\uffff\1\40\5\uffff";
    static final String DFA49_maxS =
        "\2\u00b3\2\uffff\1\u00b3\3\uffff\1\u00b3\5\uffff";
    static final String DFA49_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\10\uffff";
    static final String DFA49_specialS =
        "\16\uffff}>";
    static final String[] DFA49_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\12\2\2\uffff"+
            "\1\2\1\uffff\3\2\3\uffff\7\2\1\1\115\2",
            "\3\5\1\uffff\2\5\1\uffff\3\5\6\uffff\3\5\27\uffff\4\5\1\4\5"+
            "\5\2\uffff\1\5\1\uffff\3\5\3\uffff\125\5",
            "",
            "",
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\4\2\1\10"+
            "\5\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "",
            "",
            "",
            "\1\2\3\5\1\uffff\2\5\1\uffff\3\5\6\uffff\3\5\27\uffff\12\5"+
            "\2\uffff\1\5\1\uffff\3\5\3\uffff\125\5",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "266:21: ( DATABASE )?";
        }
    }
    static final String DFA51_eotS =
        "\13\uffff";
    static final String DFA51_eofS =
        "\1\3\2\6\10\uffff";
    static final String DFA51_minS =
        "\3\40\10\uffff";
    static final String DFA51_maxS =
        "\1\u00b3\2\44\10\uffff";
    static final String DFA51_acceptS =
        "\3\uffff\1\3\1\uffff\1\2\1\1\4\uffff";
    static final String DFA51_specialS =
        "\13\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\3\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1"+
            "\1\10\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\6\3\uffff\1\5",
            "\1\6\3\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "272:23: (database_or_table_name= id | database_name= id DOT table_name= id )?";
        }
    }
    static final String DFA53_eotS =
        "\15\uffff";
    static final String DFA53_eofS =
        "\1\3\14\uffff";
    static final String DFA53_minS =
        "\1\40\14\uffff";
    static final String DFA53_maxS =
        "\1\171\14\uffff";
    static final String DFA53_acceptS =
        "\1\uffff\1\1\1\2\1\3\11\uffff";
    static final String DFA53_specialS =
        "\15\uffff}>";
    static final String[] DFA53_transitionS = {
            "\1\3\14\uffff\2\3\75\uffff\1\1\1\2\2\3\1\uffff\1\3\1\uffff\2"+
            "\3\4\uffff\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA53_eot = DFA.unpackEncodedString(DFA53_eotS);
    static final short[] DFA53_eof = DFA.unpackEncodedString(DFA53_eofS);
    static final char[] DFA53_min = DFA.unpackEncodedStringToUnsignedChars(DFA53_minS);
    static final char[] DFA53_max = DFA.unpackEncodedStringToUnsignedChars(DFA53_maxS);
    static final short[] DFA53_accept = DFA.unpackEncodedString(DFA53_acceptS);
    static final short[] DFA53_special = DFA.unpackEncodedString(DFA53_specialS);
    static final short[][] DFA53_transition;

    static {
        int numStates = DFA53_transitionS.length;
        DFA53_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA53_transition[i] = DFA.unpackEncodedString(DFA53_transitionS[i]);
        }
    }

    class DFA53 extends DFA {

        public DFA53(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 53;
            this.eot = DFA53_eot;
            this.eof = DFA53_eof;
            this.min = DFA53_min;
            this.max = DFA53_max;
            this.accept = DFA53_accept;
            this.special = DFA53_special;
            this.transition = DFA53_transition;
        }
        public String getDescription() {
            return "286:82: ( ASC | DESC )?";
        }
    }
    static final String DFA65_eotS =
        "\76\uffff";
    static final String DFA65_eofS =
        "\76\uffff";
    static final String DFA65_minS =
        "\3\41\73\uffff";
    static final String DFA65_maxS =
        "\3\u00b3\73\uffff";
    static final String DFA65_acceptS =
        "\3\uffff\1\3\23\uffff\1\1\23\uffff\1\2\22\uffff";
    static final String DFA65_specialS =
        "\76\uffff}>";
    static final String[] DFA65_transitionS = {
            "\3\3\1\uffff\6\3\1\uffff\1\3\4\uffff\3\3\20\uffff\3\3\3\uffff"+
            "\3\3\1\2\44\3\1\1\101\3",
            "\3\27\1\3\6\27\1\uffff\1\27\4\uffff\3\27\20\uffff\3\27\3\uffff"+
            "\152\27",
            "\3\53\1\3\6\53\1\uffff\1\53\4\uffff\3\53\20\uffff\3\53\3\uffff"+
            "\152\53",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "307:10: ( ALL | DISTINCT )?";
        }
    }
    static final String DFA66_eotS =
        "\15\uffff";
    static final String DFA66_eofS =
        "\1\1\14\uffff";
    static final String DFA66_minS =
        "\1\40\14\uffff";
    static final String DFA66_maxS =
        "\1\170\14\uffff";
    static final String DFA66_acceptS =
        "\1\uffff\1\2\12\uffff\1\1";
    static final String DFA66_specialS =
        "\15\uffff}>";
    static final String[] DFA66_transitionS = {
            "\1\1\14\uffff\1\14\1\1\77\uffff\2\1\1\uffff\1\1\1\uffff\2\1"+
            "\1\uffff\3\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA66_eot = DFA.unpackEncodedString(DFA66_eotS);
    static final short[] DFA66_eof = DFA.unpackEncodedString(DFA66_eofS);
    static final char[] DFA66_min = DFA.unpackEncodedStringToUnsignedChars(DFA66_minS);
    static final char[] DFA66_max = DFA.unpackEncodedStringToUnsignedChars(DFA66_maxS);
    static final short[] DFA66_accept = DFA.unpackEncodedString(DFA66_acceptS);
    static final short[] DFA66_special = DFA.unpackEncodedString(DFA66_specialS);
    static final short[][] DFA66_transition;

    static {
        int numStates = DFA66_transitionS.length;
        DFA66_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA66_transition[i] = DFA.unpackEncodedString(DFA66_transitionS[i]);
        }
    }

    class DFA66 extends DFA {

        public DFA66(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 66;
            this.eot = DFA66_eot;
            this.eof = DFA66_eof;
            this.min = DFA66_min;
            this.max = DFA66_max;
            this.accept = DFA66_accept;
            this.special = DFA66_special;
            this.transition = DFA66_transition;
        }
        public String getDescription() {
            return "()* loopback of 307:42: ( COMMA result_column )*";
        }
    }
    static final String DFA67_eotS =
        "\14\uffff";
    static final String DFA67_eofS =
        "\1\2\13\uffff";
    static final String DFA67_minS =
        "\1\40\13\uffff";
    static final String DFA67_maxS =
        "\1\170\13\uffff";
    static final String DFA67_acceptS =
        "\1\uffff\1\1\1\2\11\uffff";
    static final String DFA67_specialS =
        "\14\uffff}>";
    static final String[] DFA67_transitionS = {
            "\1\2\15\uffff\1\2\77\uffff\2\2\1\uffff\1\2\1\uffff\2\2\1\uffff"+
            "\1\1\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA67_eot = DFA.unpackEncodedString(DFA67_eotS);
    static final short[] DFA67_eof = DFA.unpackEncodedString(DFA67_eofS);
    static final char[] DFA67_min = DFA.unpackEncodedStringToUnsignedChars(DFA67_minS);
    static final char[] DFA67_max = DFA.unpackEncodedStringToUnsignedChars(DFA67_maxS);
    static final short[] DFA67_accept = DFA.unpackEncodedString(DFA67_acceptS);
    static final short[] DFA67_special = DFA.unpackEncodedString(DFA67_specialS);
    static final short[][] DFA67_transition;

    static {
        int numStates = DFA67_transitionS.length;
        DFA67_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA67_transition[i] = DFA.unpackEncodedString(DFA67_transitionS[i]);
        }
    }

    class DFA67 extends DFA {

        public DFA67(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 67;
            this.eot = DFA67_eot;
            this.eof = DFA67_eof;
            this.min = DFA67_min;
            this.max = DFA67_max;
            this.accept = DFA67_accept;
            this.special = DFA67_special;
            this.transition = DFA67_transition;
        }
        public String getDescription() {
            return "307:65: ( FROM join_source )?";
        }
    }
    static final String DFA68_eotS =
        "\13\uffff";
    static final String DFA68_eofS =
        "\1\2\12\uffff";
    static final String DFA68_minS =
        "\1\40\12\uffff";
    static final String DFA68_maxS =
        "\1\170\12\uffff";
    static final String DFA68_acceptS =
        "\1\uffff\1\1\1\2\10\uffff";
    static final String DFA68_specialS =
        "\13\uffff}>";
    static final String[] DFA68_transitionS = {
            "\1\2\15\uffff\1\2\77\uffff\2\2\1\uffff\1\2\1\uffff\2\2\2\uffff"+
            "\1\1\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA68_eot = DFA.unpackEncodedString(DFA68_eotS);
    static final short[] DFA68_eof = DFA.unpackEncodedString(DFA68_eofS);
    static final char[] DFA68_min = DFA.unpackEncodedStringToUnsignedChars(DFA68_minS);
    static final char[] DFA68_max = DFA.unpackEncodedStringToUnsignedChars(DFA68_maxS);
    static final short[] DFA68_accept = DFA.unpackEncodedString(DFA68_acceptS);
    static final short[] DFA68_special = DFA.unpackEncodedString(DFA68_specialS);
    static final short[][] DFA68_transition;

    static {
        int numStates = DFA68_transitionS.length;
        DFA68_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA68_transition[i] = DFA.unpackEncodedString(DFA68_transitionS[i]);
        }
    }

    class DFA68 extends DFA {

        public DFA68(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 68;
            this.eot = DFA68_eot;
            this.eof = DFA68_eof;
            this.min = DFA68_min;
            this.max = DFA68_max;
            this.accept = DFA68_accept;
            this.special = DFA68_special;
            this.transition = DFA68_transition;
        }
        public String getDescription() {
            return "307:85: ( WHERE where_expr= expr )?";
        }
    }
    static final String DFA71_eotS =
        "\12\uffff";
    static final String DFA71_eofS =
        "\1\2\11\uffff";
    static final String DFA71_minS =
        "\1\40\11\uffff";
    static final String DFA71_maxS =
        "\1\170\11\uffff";
    static final String DFA71_acceptS =
        "\1\uffff\1\1\1\2\7\uffff";
    static final String DFA71_specialS =
        "\12\uffff}>";
    static final String[] DFA71_transitionS = {
            "\1\2\15\uffff\1\2\77\uffff\2\2\1\uffff\1\2\1\uffff\2\2\3\uffff"+
            "\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA71_eot = DFA.unpackEncodedString(DFA71_eotS);
    static final short[] DFA71_eof = DFA.unpackEncodedString(DFA71_eofS);
    static final char[] DFA71_min = DFA.unpackEncodedStringToUnsignedChars(DFA71_minS);
    static final char[] DFA71_max = DFA.unpackEncodedStringToUnsignedChars(DFA71_maxS);
    static final short[] DFA71_accept = DFA.unpackEncodedString(DFA71_acceptS);
    static final short[] DFA71_special = DFA.unpackEncodedString(DFA71_specialS);
    static final short[][] DFA71_transition;

    static {
        int numStates = DFA71_transitionS.length;
        DFA71_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA71_transition[i] = DFA.unpackEncodedString(DFA71_transitionS[i]);
        }
    }

    class DFA71 extends DFA {

        public DFA71(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 71;
            this.eot = DFA71_eot;
            this.eof = DFA71_eof;
            this.min = DFA71_min;
            this.max = DFA71_max;
            this.accept = DFA71_accept;
            this.special = DFA71_special;
            this.transition = DFA71_transition;
        }
        public String getDescription() {
            return "308:3: ( GROUP BY ordering_term ( COMMA ordering_term )* ( HAVING having_expr= expr )? )?";
        }
    }
    static final String DFA69_eotS =
        "\13\uffff";
    static final String DFA69_eofS =
        "\1\1\12\uffff";
    static final String DFA69_minS =
        "\1\40\12\uffff";
    static final String DFA69_maxS =
        "\1\171\12\uffff";
    static final String DFA69_acceptS =
        "\1\uffff\1\2\10\uffff\1\1";
    static final String DFA69_specialS =
        "\13\uffff}>";
    static final String[] DFA69_transitionS = {
            "\1\1\14\uffff\1\12\1\1\77\uffff\2\1\1\uffff\1\1\1\uffff\2\1"+
            "\4\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA69_eot = DFA.unpackEncodedString(DFA69_eotS);
    static final short[] DFA69_eof = DFA.unpackEncodedString(DFA69_eofS);
    static final char[] DFA69_min = DFA.unpackEncodedStringToUnsignedChars(DFA69_minS);
    static final char[] DFA69_max = DFA.unpackEncodedStringToUnsignedChars(DFA69_maxS);
    static final short[] DFA69_accept = DFA.unpackEncodedString(DFA69_acceptS);
    static final short[] DFA69_special = DFA.unpackEncodedString(DFA69_specialS);
    static final short[][] DFA69_transition;

    static {
        int numStates = DFA69_transitionS.length;
        DFA69_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA69_transition[i] = DFA.unpackEncodedString(DFA69_transitionS[i]);
        }
    }

    class DFA69 extends DFA {

        public DFA69(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 69;
            this.eot = DFA69_eot;
            this.eof = DFA69_eof;
            this.min = DFA69_min;
            this.max = DFA69_max;
            this.accept = DFA69_accept;
            this.special = DFA69_special;
            this.transition = DFA69_transition;
        }
        public String getDescription() {
            return "()* loopback of 308:28: ( COMMA ordering_term )*";
        }
    }
    static final String DFA70_eotS =
        "\12\uffff";
    static final String DFA70_eofS =
        "\1\2\11\uffff";
    static final String DFA70_minS =
        "\1\40\11\uffff";
    static final String DFA70_maxS =
        "\1\171\11\uffff";
    static final String DFA70_acceptS =
        "\1\uffff\1\1\1\2\7\uffff";
    static final String DFA70_specialS =
        "\12\uffff}>";
    static final String[] DFA70_transitionS = {
            "\1\2\15\uffff\1\2\77\uffff\2\2\1\uffff\1\2\1\uffff\2\2\4\uffff"+
            "\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA70_eot = DFA.unpackEncodedString(DFA70_eotS);
    static final short[] DFA70_eof = DFA.unpackEncodedString(DFA70_eofS);
    static final char[] DFA70_min = DFA.unpackEncodedStringToUnsignedChars(DFA70_minS);
    static final char[] DFA70_max = DFA.unpackEncodedStringToUnsignedChars(DFA70_maxS);
    static final short[] DFA70_accept = DFA.unpackEncodedString(DFA70_acceptS);
    static final short[] DFA70_special = DFA.unpackEncodedString(DFA70_specialS);
    static final short[][] DFA70_transition;

    static {
        int numStates = DFA70_transitionS.length;
        DFA70_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA70_transition[i] = DFA.unpackEncodedString(DFA70_transitionS[i]);
        }
    }

    class DFA70 extends DFA {

        public DFA70(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 70;
            this.eot = DFA70_eot;
            this.eof = DFA70_eof;
            this.min = DFA70_min;
            this.max = DFA70_max;
            this.accept = DFA70_accept;
            this.special = DFA70_special;
            this.transition = DFA70_transition;
        }
        public String getDescription() {
            return "308:51: ( HAVING having_expr= expr )?";
        }
    }
    static final String DFA74_eotS =
        "\u0116\uffff";
    static final String DFA74_eofS =
        "\2\uffff\2\4\2\uffff\1\4\1\uffff\3\4\u010b\uffff";
    static final String DFA74_minS =
        "\1\41\1\uffff\2\40\2\uffff\1\40\1\uffff\3\40\1\44\4\uffff\1\41\2"+
        "\44\1\uffff\1\41\76\uffff\1\41\40\uffff\1\41\37\uffff\2\41\37\uffff"+
        "\1\41\40\uffff\1\41\23\uffff\1\41\1\uffff\2\41\50\uffff";
    static final String DFA74_maxS =
        "\1\u00b3\1\uffff\2\u00b3\2\uffff\1\u00b3\1\uffff\3\u00b3\1\54\4"+
        "\uffff\1\u00b3\1\54\1\44\1\uffff\1\u00b3\76\uffff\1\u00b3\40\uffff"+
        "\1\u00b3\37\uffff\2\u00b3\37\uffff\1\u00b3\40\uffff\1\u00b3\23\uffff"+
        "\1\u00b3\1\uffff\2\u00b3\50\uffff";
    static final String DFA74_acceptS =
        "\1\uffff\1\1\2\uffff\1\3\u00e9\uffff\1\2\47\uffff";
    static final String DFA74_specialS =
        "\u0116\uffff}>";
    static final String[] DFA74_transitionS = {
            "\3\22\1\uffff\2\22\1\4\3\22\1\uffff\1\4\4\uffff\1\22\1\3\1\22"+
            "\20\uffff\2\4\1\1\3\uffff\1\4\1\22\1\6\1\22\1\13\1\22\1\20\4"+
            "\22\2\4\1\2\1\4\1\10\1\11\1\12\3\4\1\21\124\22",
            "",
            "\4\4\1\24\7\4\1\uffff\35\4\1\uffff\12\4\2\uffff\1\4\1\uffff"+
            "\3\4\3\uffff\125\4",
            "\4\4\1\123\7\4\1\uffff\35\4\1\uffff\12\4\2\uffff\1\4\1\uffff"+
            "\3\4\3\uffff\125\4",
            "",
            "",
            "\4\4\1\164\45\4\1\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff"+
            "\125\4",
            "",
            "\4\4\1\u0094\7\4\1\uffff\35\4\1\uffff\12\4\2\uffff\1\4\1\uffff"+
            "\3\4\3\uffff\125\4",
            "\4\4\1\u0095\7\4\1\uffff\35\4\1\uffff\12\4\2\uffff\1\4\1\uffff"+
            "\3\4\3\uffff\125\4",
            "\4\4\1\u00b5\7\4\1\uffff\35\4\1\uffff\12\4\2\uffff\1\4\1\uffff"+
            "\3\4\3\uffff\125\4",
            "\1\u00d6\7\uffff\1\4",
            "",
            "",
            "",
            "",
            "\3\4\1\u00ea\6\4\1\uffff\1\4\4\uffff\3\4\20\uffff\2\4\4\uffff"+
            "\152\4",
            "\1\u00ec\7\uffff\1\4",
            "\1\u00ed",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "\3\4\1\uffff\2\4\1\uffff\3\4\6\uffff\3\4\22\uffff\1\u00ee\4"+
            "\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\125\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA74_eot = DFA.unpackEncodedString(DFA74_eotS);
    static final short[] DFA74_eof = DFA.unpackEncodedString(DFA74_eofS);
    static final char[] DFA74_min = DFA.unpackEncodedStringToUnsignedChars(DFA74_minS);
    static final char[] DFA74_max = DFA.unpackEncodedStringToUnsignedChars(DFA74_maxS);
    static final short[] DFA74_accept = DFA.unpackEncodedString(DFA74_acceptS);
    static final short[] DFA74_special = DFA.unpackEncodedString(DFA74_specialS);
    static final short[][] DFA74_transition;

    static {
        int numStates = DFA74_transitionS.length;
        DFA74_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA74_transition[i] = DFA.unpackEncodedString(DFA74_transitionS[i]);
        }
    }

    class DFA74 extends DFA {

        public DFA74(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 74;
            this.eot = DFA74_eot;
            this.eof = DFA74_eof;
            this.min = DFA74_min;
            this.max = DFA74_max;
            this.accept = DFA74_accept;
            this.special = DFA74_special;
            this.transition = DFA74_transition;
        }
        public String getDescription() {
            return "314:1: result_column : ( ASTERISK | table_name= id DOT ASTERISK -> ^( ASTERISK $table_name) | expr ( ( AS )? column_alias= id )? -> ^( ALIAS expr ( $column_alias)? ) );";
        }
    }
    static final String DFA73_eotS =
        "\u00d7\uffff";
    static final String DFA73_eofS =
        "\1\4\2\uffff\1\1\1\uffff\7\1\u00cb\uffff";
    static final String DFA73_minS =
        "\1\40\2\uffff\1\40\1\uffff\7\40\5\uffff\2\41\6\44\10\uffff\2\41"+
        "\6\44\u00ae\uffff";
    static final String DFA73_maxS =
        "\1\u00b3\2\uffff\1\u00b3\1\uffff\1\u00b3\6\170\5\uffff\2\u00b3\1"+
        "\46\3\165\1\46\1\125\10\uffff\2\u00b3\1\46\3\165\1\46\1\125\u00ae"+
        "\uffff";
    static final String DFA73_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\u00d2\uffff";
    static final String DFA73_specialS =
        "\u00d7\uffff}>";
    static final String[] DFA73_transitionS = {
            "\1\4\3\1\1\uffff\2\1\1\uffff\3\1\2\uffff\2\4\2\uffff\3\1\27"+
            "\uffff\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\17\1\1\12\1\13\1"+
            "\1\1\7\1\1\1\10\1\11\1\1\1\3\1\5\1\6\73\1",
            "",
            "",
            "\1\1\3\4\1\uffff\2\4\1\uffff\3\4\1\uffff\1\4\2\1\2\uffff\3"+
            "\4\27\uffff\12\4\2\uffff\1\4\1\uffff\3\4\3\uffff\17\4\1\27\1"+
            "\30\1\4\1\24\1\4\1\25\1\26\1\4\1\21\1\22\1\23\73\4",
            "",
            "\1\1\3\4\1\uffff\6\4\1\uffff\1\4\2\1\2\uffff\3\4\20\uffff\2"+
            "\4\4\uffff\44\4\1\47\1\50\1\4\1\44\1\4\1\45\1\46\1\4\1\41\1"+
            "\42\1\43\73\4",
            "\1\1\5\uffff\1\4\6\uffff\2\1\77\uffff\2\1\1\uffff\1\1\1\uffff"+
            "\2\1\1\uffff\3\1",
            "\1\1\14\uffff\2\1\77\uffff\2\1\1\uffff\1\1\1\4\2\1\1\4\3\1",
            "\1\1\14\uffff\2\1\77\uffff\2\1\1\uffff\1\1\1\uffff\2\1\1\4"+
            "\3\1",
            "\1\1\14\uffff\2\1\77\uffff\2\1\1\uffff\1\1\1\uffff\2\1\1\4"+
            "\3\1",
            "\1\1\5\uffff\1\4\6\uffff\2\1\77\uffff\2\1\1\uffff\1\1\1\uffff"+
            "\2\1\1\uffff\3\1",
            "\1\1\14\uffff\2\1\46\uffff\1\4\30\uffff\2\1\1\uffff\1\1\1\uffff"+
            "\2\1\1\uffff\3\1",
            "",
            "",
            "",
            "",
            "",
            "\3\1\1\4\2\1\1\uffff\3\1\1\uffff\1\1\4\uffff\3\1\27\uffff\12"+
            "\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "\3\1\1\4\6\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2\1\4\uffff\152"+
            "\1",
            "\1\4\1\uffff\1\1",
            "\1\4\115\uffff\1\1\2\uffff\1\1",
            "\1\4\120\uffff\1\1",
            "\1\4\120\uffff\1\1",
            "\1\4\1\uffff\1\1",
            "\1\4\60\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\1\1\4\2\1\1\uffff\3\1\1\uffff\1\1\4\uffff\3\1\27\uffff\12"+
            "\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "\3\1\1\4\6\1\1\uffff\1\1\4\uffff\3\1\20\uffff\2\1\4\uffff\152"+
            "\1",
            "\1\4\1\uffff\1\1",
            "\1\4\115\uffff\1\1\2\uffff\1\1",
            "\1\4\120\uffff\1\1",
            "\1\4\120\uffff\1\1",
            "\1\4\1\uffff\1\1",
            "\1\4\60\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA73_eot = DFA.unpackEncodedString(DFA73_eotS);
    static final short[] DFA73_eof = DFA.unpackEncodedString(DFA73_eofS);
    static final char[] DFA73_min = DFA.unpackEncodedStringToUnsignedChars(DFA73_minS);
    static final char[] DFA73_max = DFA.unpackEncodedStringToUnsignedChars(DFA73_maxS);
    static final short[] DFA73_accept = DFA.unpackEncodedString(DFA73_acceptS);
    static final short[] DFA73_special = DFA.unpackEncodedString(DFA73_specialS);
    static final short[][] DFA73_transition;

    static {
        int numStates = DFA73_transitionS.length;
        DFA73_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA73_transition[i] = DFA.unpackEncodedString(DFA73_transitionS[i]);
        }
    }

    class DFA73 extends DFA {

        public DFA73(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 73;
            this.eot = DFA73_eot;
            this.eof = DFA73_eof;
            this.min = DFA73_min;
            this.max = DFA73_max;
            this.accept = DFA73_accept;
            this.special = DFA73_special;
            this.transition = DFA73_transition;
        }
        public String getDescription() {
            return "317:10: ( ( AS )? column_alias= id )?";
        }
    }
    static final String DFA72_eotS =
        "\u00d9\uffff";
    static final String DFA72_eofS =
        "\1\uffff\1\2\3\uffff\10\20\u00cc\uffff";
    static final String DFA72_minS =
        "\1\41\1\40\3\uffff\10\40\6\uffff\2\41\6\44\10\uffff\2\41\6\44\u00ae"+
        "\uffff";
    static final String DFA72_maxS =
        "\2\u00b3\3\uffff\2\u00b3\6\170\6\uffff\2\u00b3\1\46\3\165\1\46\1"+
        "\125\10\uffff\2\u00b3\1\46\3\165\1\46\1\125\u00ae\uffff";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\15\uffff\1\1\u00c8\uffff";
    static final String DFA72_specialS =
        "\u00d9\uffff}>";
    static final String[] DFA72_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\4\2\1\1\5"+
            "\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "\1\2\3\20\1\uffff\2\20\1\uffff\3\20\2\uffff\2\2\2\uffff\3\20"+
            "\27\uffff\12\20\2\uffff\1\20\1\uffff\3\20\3\uffff\17\20\1\13"+
            "\1\14\1\20\1\10\1\20\1\11\1\12\1\20\1\5\1\6\1\7\73\20",
            "",
            "",
            "",
            "\1\20\3\2\1\uffff\2\2\1\uffff\3\2\1\uffff\1\2\2\20\2\uffff"+
            "\3\2\27\uffff\12\2\2\uffff\1\2\1\uffff\3\2\3\uffff\17\2\1\31"+
            "\1\32\1\2\1\26\1\2\1\27\1\30\1\2\1\23\1\24\1\25\73\2",
            "\1\20\3\2\1\uffff\6\2\1\uffff\1\2\2\20\2\uffff\3\2\20\uffff"+
            "\2\2\4\uffff\44\2\1\51\1\52\1\2\1\46\1\2\1\47\1\50\1\2\1\43"+
            "\1\44\1\45\73\2",
            "\1\20\5\uffff\1\2\6\uffff\2\20\77\uffff\2\20\1\uffff\1\20\1"+
            "\uffff\2\20\1\uffff\3\20",
            "\1\20\14\uffff\2\20\77\uffff\2\20\1\uffff\1\20\1\2\2\20\1\2"+
            "\3\20",
            "\1\20\14\uffff\2\20\77\uffff\2\20\1\uffff\1\20\1\uffff\2\20"+
            "\1\2\3\20",
            "\1\20\14\uffff\2\20\77\uffff\2\20\1\uffff\1\20\1\uffff\2\20"+
            "\1\2\3\20",
            "\1\20\5\uffff\1\2\6\uffff\2\20\77\uffff\2\20\1\uffff\1\20\1"+
            "\uffff\2\20\1\uffff\3\20",
            "\1\20\14\uffff\2\20\46\uffff\1\2\30\uffff\2\20\1\uffff\1\20"+
            "\1\uffff\2\20\1\uffff\3\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\20\1\2\2\20\1\uffff\3\20\1\uffff\1\20\4\uffff\3\20\27\uffff"+
            "\12\20\2\uffff\1\20\1\uffff\3\20\3\uffff\125\20",
            "\3\20\1\2\6\20\1\uffff\1\20\4\uffff\3\20\20\uffff\2\20\4\uffff"+
            "\152\20",
            "\1\2\1\uffff\1\20",
            "\1\2\115\uffff\1\20\2\uffff\1\20",
            "\1\2\120\uffff\1\20",
            "\1\2\120\uffff\1\20",
            "\1\2\1\uffff\1\20",
            "\1\2\60\uffff\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\20\1\2\2\20\1\uffff\3\20\1\uffff\1\20\4\uffff\3\20\27\uffff"+
            "\12\20\2\uffff\1\20\1\uffff\3\20\3\uffff\125\20",
            "\3\20\1\2\6\20\1\uffff\1\20\4\uffff\3\20\20\uffff\2\20\4\uffff"+
            "\152\20",
            "\1\2\1\uffff\1\20",
            "\1\2\115\uffff\1\20\2\uffff\1\20",
            "\1\2\120\uffff\1\20",
            "\1\2\120\uffff\1\20",
            "\1\2\1\uffff\1\20",
            "\1\2\60\uffff\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
    static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
    static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
    static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
    static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
    static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
    static final short[][] DFA72_transition;

    static {
        int numStates = DFA72_transitionS.length;
        DFA72_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
        }
    }

    class DFA72 extends DFA {

        public DFA72(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 72;
            this.eot = DFA72_eot;
            this.eof = DFA72_eof;
            this.min = DFA72_min;
            this.max = DFA72_max;
            this.accept = DFA72_accept;
            this.special = DFA72_special;
            this.transition = DFA72_transition;
        }
        public String getDescription() {
            return "317:11: ( AS )?";
        }
    }
    static final String DFA76_eotS =
        "\22\uffff";
    static final String DFA76_eofS =
        "\1\1\21\uffff";
    static final String DFA76_minS =
        "\1\40\21\uffff";
    static final String DFA76_maxS =
        "\1\177\21\uffff";
    static final String DFA76_acceptS =
        "\1\uffff\1\2\11\uffff\1\1\6\uffff";
    static final String DFA76_specialS =
        "\22\uffff}>";
    static final String[] DFA76_transitionS = {
            "\1\1\14\uffff\1\13\1\1\77\uffff\2\1\1\uffff\1\1\1\uffff\2\1"+
            "\2\uffff\2\1\1\uffff\6\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA76_eot = DFA.unpackEncodedString(DFA76_eotS);
    static final short[] DFA76_eof = DFA.unpackEncodedString(DFA76_eofS);
    static final char[] DFA76_min = DFA.unpackEncodedStringToUnsignedChars(DFA76_minS);
    static final char[] DFA76_max = DFA.unpackEncodedStringToUnsignedChars(DFA76_maxS);
    static final short[] DFA76_accept = DFA.unpackEncodedString(DFA76_acceptS);
    static final short[] DFA76_special = DFA.unpackEncodedString(DFA76_specialS);
    static final short[][] DFA76_transition;

    static {
        int numStates = DFA76_transitionS.length;
        DFA76_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA76_transition[i] = DFA.unpackEncodedString(DFA76_transitionS[i]);
        }
    }

    class DFA76 extends DFA {

        public DFA76(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 76;
            this.eot = DFA76_eot;
            this.eof = DFA76_eof;
            this.min = DFA76_min;
            this.max = DFA76_max;
            this.accept = DFA76_accept;
            this.special = DFA76_special;
            this.transition = DFA76_transition;
        }
        public String getDescription() {
            return "()* loopback of 319:28: ( join_op single_source ( join_constraint )? )*";
        }
    }
    static final String DFA75_eotS =
        "\24\uffff";
    static final String DFA75_eofS =
        "\1\3\23\uffff";
    static final String DFA75_minS =
        "\1\40\23\uffff";
    static final String DFA75_maxS =
        "\1\u0081\23\uffff";
    static final String DFA75_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\20\uffff";
    static final String DFA75_specialS =
        "\24\uffff}>";
    static final String[] DFA75_transitionS = {
            "\1\3\14\uffff\2\3\77\uffff\2\3\1\uffff\1\3\1\uffff\2\3\2\uffff"+
            "\2\3\1\uffff\6\3\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA75_eot = DFA.unpackEncodedString(DFA75_eotS);
    static final short[] DFA75_eof = DFA.unpackEncodedString(DFA75_eofS);
    static final char[] DFA75_min = DFA.unpackEncodedStringToUnsignedChars(DFA75_minS);
    static final char[] DFA75_max = DFA.unpackEncodedStringToUnsignedChars(DFA75_maxS);
    static final short[] DFA75_accept = DFA.unpackEncodedString(DFA75_acceptS);
    static final short[] DFA75_special = DFA.unpackEncodedString(DFA75_specialS);
    static final short[][] DFA75_transition;

    static {
        int numStates = DFA75_transitionS.length;
        DFA75_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA75_transition[i] = DFA.unpackEncodedString(DFA75_transitionS[i]);
        }
    }

    class DFA75 extends DFA {

        public DFA75(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 75;
            this.eot = DFA75_eot;
            this.eof = DFA75_eof;
            this.min = DFA75_min;
            this.max = DFA75_max;
            this.accept = DFA75_accept;
            this.special = DFA75_special;
            this.transition = DFA75_transition;
        }
        public String getDescription() {
            return "319:52: ( join_constraint )?";
        }
    }
    static final String DFA83_eotS =
        "\40\uffff";
    static final String DFA83_eofS =
        "\40\uffff";
    static final String DFA83_minS =
        "\1\41\3\uffff\1\41\1\uffff\1\41\31\uffff";
    static final String DFA83_maxS =
        "\1\u00b3\3\uffff\1\u00b3\1\uffff\1\u00b3\31\uffff";
    static final String DFA83_acceptS =
        "\1\uffff\1\1\3\uffff\1\3\4\uffff\1\2\25\uffff";
    static final String DFA83_specialS =
        "\40\uffff}>";
    static final String[] DFA83_transitionS = {
            "\3\1\1\uffff\2\1\1\uffff\3\1\1\uffff\1\4\4\uffff\3\1\27\uffff"+
            "\12\1\2\uffff\1\1\1\uffff\3\1\3\uffff\125\1",
            "",
            "",
            "",
            "\3\5\1\uffff\2\5\1\uffff\3\5\1\uffff\1\5\4\uffff\3\5\27\uffff"+
            "\12\5\2\uffff\1\5\1\uffff\3\5\3\uffff\26\5\1\6\76\5",
            "",
            "\3\12\1\5\6\12\1\uffff\1\12\4\uffff\3\12\20\uffff\3\12\3\uffff"+
            "\152\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA83_eot = DFA.unpackEncodedString(DFA83_eotS);
    static final short[] DFA83_eof = DFA.unpackEncodedString(DFA83_eofS);
    static final char[] DFA83_min = DFA.unpackEncodedStringToUnsignedChars(DFA83_minS);
    static final char[] DFA83_max = DFA.unpackEncodedStringToUnsignedChars(DFA83_maxS);
    static final short[] DFA83_accept = DFA.unpackEncodedString(DFA83_acceptS);
    static final short[] DFA83_special = DFA.unpackEncodedString(DFA83_specialS);
    static final short[][] DFA83_transition;

    static {
        int numStates = DFA83_transitionS.length;
        DFA83_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA83_transition[i] = DFA.unpackEncodedString(DFA83_transitionS[i]);
        }
    }

    class DFA83 extends DFA {

        public DFA83(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 83;
            this.eot = DFA83_eot;
            this.eof = DFA83_eof;
            this.min = DFA83_min;
            this.max = DFA83_max;
            this.accept = DFA83_accept;
            this.special = DFA83_special;
            this.transition = DFA83_transition;
        }
        public String getDescription() {
            return "321:1: single_source : ( (database_name= id DOT )? table_name= ID ( ( AS )? table_alias= ID )? ( INDEXED BY index_name= id | NOT INDEXED )? -> ^( ALIAS ^( $table_name ( $database_name)? ) ( $table_alias)? ( ^( INDEXED ( NOT )? ( $index_name)? ) )? ) | LPAREN select_stmt RPAREN ( ( AS )? table_alias= ID )? -> ^( ALIAS select_stmt ( $table_alias)? ) | LPAREN join_source RPAREN );";
        }
    }
    static final String DFA77_eotS =
        "\34\uffff";
    static final String DFA77_eofS =
        "\1\uffff\1\4\32\uffff";
    static final String DFA77_minS =
        "\1\41\1\40\32\uffff";
    static final String DFA77_maxS =
        "\1\u00b3\1\u0081\32\uffff";
    static final String DFA77_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\27\uffff";
    static final String DFA77_specialS =
        "\34\uffff}>";
    static final String[] DFA77_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\2\1\uffff\3\2\3\uffff\125\2",
            "\1\4\3\uffff\1\2\1\4\1\uffff\1\4\5\uffff\2\4\35\uffff\1\4\2"+
            "\uffff\1\4\36\uffff\2\4\1\uffff\1\4\1\uffff\2\4\2\uffff\2\4"+
            "\1\uffff\10\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA77_eot = DFA.unpackEncodedString(DFA77_eotS);
    static final short[] DFA77_eof = DFA.unpackEncodedString(DFA77_eofS);
    static final char[] DFA77_min = DFA.unpackEncodedStringToUnsignedChars(DFA77_minS);
    static final char[] DFA77_max = DFA.unpackEncodedStringToUnsignedChars(DFA77_maxS);
    static final short[] DFA77_accept = DFA.unpackEncodedString(DFA77_acceptS);
    static final short[] DFA77_special = DFA.unpackEncodedString(DFA77_specialS);
    static final short[][] DFA77_transition;

    static {
        int numStates = DFA77_transitionS.length;
        DFA77_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA77_transition[i] = DFA.unpackEncodedString(DFA77_transitionS[i]);
        }
    }

    class DFA77 extends DFA {

        public DFA77(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 77;
            this.eot = DFA77_eot;
            this.eof = DFA77_eof;
            this.min = DFA77_min;
            this.max = DFA77_max;
            this.accept = DFA77_accept;
            this.special = DFA77_special;
            this.transition = DFA77_transition;
        }
        public String getDescription() {
            return "322:5: (database_name= id DOT )?";
        }
    }
    static final String DFA79_eotS =
        "\30\uffff";
    static final String DFA79_eofS =
        "\1\3\27\uffff";
    static final String DFA79_minS =
        "\1\40\27\uffff";
    static final String DFA79_maxS =
        "\1\u0081\27\uffff";
    static final String DFA79_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\24\uffff";
    static final String DFA79_specialS =
        "\30\uffff}>";
    static final String[] DFA79_transitionS = {
            "\1\3\4\uffff\1\3\1\uffff\1\3\5\uffff\2\3\35\uffff\1\1\2\uffff"+
            "\1\1\36\uffff\2\3\1\uffff\1\3\1\uffff\2\3\2\uffff\2\3\1\uffff"+
            "\10\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA79_eot = DFA.unpackEncodedString(DFA79_eotS);
    static final short[] DFA79_eof = DFA.unpackEncodedString(DFA79_eofS);
    static final char[] DFA79_min = DFA.unpackEncodedStringToUnsignedChars(DFA79_minS);
    static final char[] DFA79_max = DFA.unpackEncodedStringToUnsignedChars(DFA79_maxS);
    static final short[] DFA79_accept = DFA.unpackEncodedString(DFA79_acceptS);
    static final short[] DFA79_special = DFA.unpackEncodedString(DFA79_specialS);
    static final short[][] DFA79_transition;

    static {
        int numStates = DFA79_transitionS.length;
        DFA79_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA79_transition[i] = DFA.unpackEncodedString(DFA79_transitionS[i]);
        }
    }

    class DFA79 extends DFA {

        public DFA79(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 79;
            this.eot = DFA79_eot;
            this.eof = DFA79_eof;
            this.min = DFA79_min;
            this.max = DFA79_max;
            this.accept = DFA79_accept;
            this.special = DFA79_special;
            this.transition = DFA79_transition;
        }
        public String getDescription() {
            return "322:43: ( ( AS )? table_alias= ID )?";
        }
    }
    static final String DFA80_eotS =
        "\26\uffff";
    static final String DFA80_eofS =
        "\1\3\25\uffff";
    static final String DFA80_minS =
        "\1\40\25\uffff";
    static final String DFA80_maxS =
        "\1\u0081\25\uffff";
    static final String DFA80_acceptS =
        "\1\uffff\1\1\1\2\1\3\22\uffff";
    static final String DFA80_specialS =
        "\26\uffff}>";
    static final String[] DFA80_transitionS = {
            "\1\3\4\uffff\1\1\1\uffff\1\2\5\uffff\2\3\77\uffff\2\3\1\uffff"+
            "\1\3\1\uffff\2\3\2\uffff\2\3\1\uffff\10\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA80_eot = DFA.unpackEncodedString(DFA80_eotS);
    static final short[] DFA80_eof = DFA.unpackEncodedString(DFA80_eofS);
    static final char[] DFA80_min = DFA.unpackEncodedStringToUnsignedChars(DFA80_minS);
    static final char[] DFA80_max = DFA.unpackEncodedStringToUnsignedChars(DFA80_maxS);
    static final short[] DFA80_accept = DFA.unpackEncodedString(DFA80_acceptS);
    static final short[] DFA80_special = DFA.unpackEncodedString(DFA80_specialS);
    static final short[][] DFA80_transition;

    static {
        int numStates = DFA80_transitionS.length;
        DFA80_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA80_transition[i] = DFA.unpackEncodedString(DFA80_transitionS[i]);
        }
    }

    class DFA80 extends DFA {

        public DFA80(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 80;
            this.eot = DFA80_eot;
            this.eof = DFA80_eof;
            this.min = DFA80_min;
            this.max = DFA80_max;
            this.accept = DFA80_accept;
            this.special = DFA80_special;
            this.transition = DFA80_transition;
        }
        public String getDescription() {
            return "322:67: ( INDEXED BY index_name= id | NOT INDEXED )?";
        }
    }
    static final String DFA82_eotS =
        "\26\uffff";
    static final String DFA82_eofS =
        "\1\3\25\uffff";
    static final String DFA82_minS =
        "\1\40\25\uffff";
    static final String DFA82_maxS =
        "\1\u0081\25\uffff";
    static final String DFA82_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\22\uffff";
    static final String DFA82_specialS =
        "\26\uffff}>";
    static final String[] DFA82_transitionS = {
            "\1\3\14\uffff\2\3\35\uffff\1\1\2\uffff\1\1\36\uffff\2\3\1\uffff"+
            "\1\3\1\uffff\2\3\2\uffff\2\3\1\uffff\10\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA82_eot = DFA.unpackEncodedString(DFA82_eotS);
    static final short[] DFA82_eof = DFA.unpackEncodedString(DFA82_eofS);
    static final char[] DFA82_min = DFA.unpackEncodedStringToUnsignedChars(DFA82_minS);
    static final char[] DFA82_max = DFA.unpackEncodedStringToUnsignedChars(DFA82_maxS);
    static final short[] DFA82_accept = DFA.unpackEncodedString(DFA82_acceptS);
    static final short[] DFA82_special = DFA.unpackEncodedString(DFA82_specialS);
    static final short[][] DFA82_transition;

    static {
        int numStates = DFA82_transitionS.length;
        DFA82_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA82_transition[i] = DFA.unpackEncodedString(DFA82_transitionS[i]);
        }
    }

    class DFA82 extends DFA {

        public DFA82(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 82;
            this.eot = DFA82_eot;
            this.eof = DFA82_eof;
            this.min = DFA82_min;
            this.max = DFA82_max;
            this.accept = DFA82_accept;
            this.special = DFA82_special;
            this.transition = DFA82_transition;
        }
        public String getDescription() {
            return "324:31: ( ( AS )? table_alias= ID )?";
        }
    }
    static final String DFA93_eotS =
        "\15\uffff";
    static final String DFA93_eofS =
        "\15\uffff";
    static final String DFA93_minS =
        "\1\41\2\44\12\uffff";
    static final String DFA93_maxS =
        "\1\u00b3\2\u0085\12\uffff";
    static final String DFA93_acceptS =
        "\3\uffff\1\2\3\uffff\1\1\5\uffff";
    static final String DFA93_specialS =
        "\15\uffff}>";
    static final String[] DFA93_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\7\7\uffff\1\3\110\uffff\1\3\16\uffff\2\3",
            "\1\7\7\uffff\1\3\110\uffff\1\3\16\uffff\2\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA93_eot = DFA.unpackEncodedString(DFA93_eotS);
    static final short[] DFA93_eof = DFA.unpackEncodedString(DFA93_eofS);
    static final char[] DFA93_min = DFA.unpackEncodedStringToUnsignedChars(DFA93_minS);
    static final char[] DFA93_max = DFA.unpackEncodedStringToUnsignedChars(DFA93_maxS);
    static final short[] DFA93_accept = DFA.unpackEncodedString(DFA93_acceptS);
    static final short[] DFA93_special = DFA.unpackEncodedString(DFA93_specialS);
    static final short[][] DFA93_transition;

    static {
        int numStates = DFA93_transitionS.length;
        DFA93_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA93_transition[i] = DFA.unpackEncodedString(DFA93_transitionS[i]);
        }
    }

    class DFA93 extends DFA {

        public DFA93(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 93;
            this.eot = DFA93_eot;
            this.eof = DFA93_eof;
            this.min = DFA93_min;
            this.max = DFA93_max;
            this.accept = DFA93_accept;
            this.special = DFA93_special;
            this.transition = DFA93_transition;
        }
        public String getDescription() {
            return "337:67: (database_name= id DOT )?";
        }
    }
    static final String DFA118_eotS =
        "\76\uffff";
    static final String DFA118_eofS =
        "\76\uffff";
    static final String DFA118_minS =
        "\1\55\1\41\2\uffff\4\47\66\uffff";
    static final String DFA118_maxS =
        "\1\56\1\u00b3\2\uffff\4\u00a0\66\uffff";
    static final String DFA118_acceptS =
        "\2\uffff\1\2\5\uffff\1\1\65\uffff";
    static final String DFA118_specialS =
        "\76\uffff}>";
    static final String[] DFA118_transitionS = {
            "\1\1\1\2",
            "\3\10\1\uffff\7\10\3\uffff\5\10\4\uffff\4\10\17\uffff\12\10"+
            "\2\uffff\1\10\1\uffff\3\10\3\uffff\72\10\1\2\1\4\2\10\1\5\1"+
            "\6\1\7\11\10\1\uffff\12\10",
            "",
            "",
            "\1\10\5\uffff\2\10\3\uffff\1\10\30\uffff\2\10\70\uffff\1\10"+
            "\23\uffff\2\10\1\2\1\uffff\2\10\1\uffff\1\10",
            "\1\10\4\uffff\1\2\2\10\3\uffff\1\10\30\uffff\2\10\70\uffff"+
            "\1\10\23\uffff\2\10\2\uffff\2\10\1\uffff\1\10",
            "\1\10\4\uffff\1\2\2\10\3\uffff\1\10\30\uffff\2\10\70\uffff"+
            "\1\10\23\uffff\2\10\2\uffff\2\10\1\uffff\1\10",
            "\1\10\5\uffff\2\10\3\uffff\1\10\30\uffff\2\10\70\uffff\1\10"+
            "\23\uffff\2\10\1\2\1\uffff\2\10\1\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA118_eot = DFA.unpackEncodedString(DFA118_eotS);
    static final short[] DFA118_eof = DFA.unpackEncodedString(DFA118_eofS);
    static final char[] DFA118_min = DFA.unpackEncodedStringToUnsignedChars(DFA118_minS);
    static final char[] DFA118_max = DFA.unpackEncodedStringToUnsignedChars(DFA118_maxS);
    static final short[] DFA118_accept = DFA.unpackEncodedString(DFA118_acceptS);
    static final short[] DFA118_special = DFA.unpackEncodedString(DFA118_specialS);
    static final short[][] DFA118_transition;

    static {
        int numStates = DFA118_transitionS.length;
        DFA118_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA118_transition[i] = DFA.unpackEncodedString(DFA118_transitionS[i]);
        }
    }

    class DFA118 extends DFA {

        public DFA118(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 118;
            this.eot = DFA118_eot;
            this.eof = DFA118_eof;
            this.min = DFA118_min;
            this.max = DFA118_max;
            this.accept = DFA118_accept;
            this.special = DFA118_special;
            this.transition = DFA118_transition;
        }
        public String getDescription() {
            return "()* loopback of 381:23: ( COMMA column_def )*";
        }
    }
    static final String DFA121_eotS =
        "\17\uffff";
    static final String DFA121_eofS =
        "\1\2\16\uffff";
    static final String DFA121_minS =
        "\1\40\16\uffff";
    static final String DFA121_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA121_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA121_specialS =
        "\17\uffff}>";
    static final String[] DFA121_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\1\1\70"+
            "\uffff\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA121_eot = DFA.unpackEncodedString(DFA121_eotS);
    static final short[] DFA121_eof = DFA.unpackEncodedString(DFA121_eofS);
    static final char[] DFA121_min = DFA.unpackEncodedStringToUnsignedChars(DFA121_minS);
    static final char[] DFA121_max = DFA.unpackEncodedStringToUnsignedChars(DFA121_maxS);
    static final short[] DFA121_accept = DFA.unpackEncodedString(DFA121_acceptS);
    static final short[] DFA121_special = DFA.unpackEncodedString(DFA121_specialS);
    static final short[][] DFA121_transition;

    static {
        int numStates = DFA121_transitionS.length;
        DFA121_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA121_transition[i] = DFA.unpackEncodedString(DFA121_transitionS[i]);
        }
    }

    class DFA121 extends DFA {

        public DFA121(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 121;
            this.eot = DFA121_eot;
            this.eof = DFA121_eof;
            this.min = DFA121_min;
            this.max = DFA121_max;
            this.accept = DFA121_accept;
            this.special = DFA121_special;
            this.transition = DFA121_transition;
        }
        public String getDescription() {
            return "386:32: ( type_name )?";
        }
    }
    static final String DFA122_eotS =
        "\16\uffff";
    static final String DFA122_eofS =
        "\1\1\15\uffff";
    static final String DFA122_minS =
        "\1\40\15\uffff";
    static final String DFA122_maxS =
        "\1\u00a0\15\uffff";
    static final String DFA122_acceptS =
        "\1\uffff\1\2\3\uffff\1\1\10\uffff";
    static final String DFA122_specialS =
        "\16\uffff}>";
    static final String[] DFA122_transitionS = {
            "\1\1\6\uffff\1\5\5\uffff\2\1\3\uffff\1\5\30\uffff\1\5\71\uffff"+
            "\1\5\23\uffff\2\5\2\uffff\2\5\1\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA122_eot = DFA.unpackEncodedString(DFA122_eotS);
    static final short[] DFA122_eof = DFA.unpackEncodedString(DFA122_eofS);
    static final char[] DFA122_min = DFA.unpackEncodedStringToUnsignedChars(DFA122_minS);
    static final char[] DFA122_max = DFA.unpackEncodedStringToUnsignedChars(DFA122_maxS);
    static final short[] DFA122_accept = DFA.unpackEncodedString(DFA122_acceptS);
    static final short[] DFA122_special = DFA.unpackEncodedString(DFA122_specialS);
    static final short[][] DFA122_transition;

    static {
        int numStates = DFA122_transitionS.length;
        DFA122_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA122_transition[i] = DFA.unpackEncodedString(DFA122_transitionS[i]);
        }
    }

    class DFA122 extends DFA {

        public DFA122(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 122;
            this.eot = DFA122_eot;
            this.eof = DFA122_eof;
            this.min = DFA122_min;
            this.max = DFA122_max;
            this.accept = DFA122_accept;
            this.special = DFA122_special;
            this.transition = DFA122_transition;
        }
        public String getDescription() {
            return "()* loopback of 386:43: ( column_constraint )*";
        }
    }
    static final String DFA123_eotS =
        "\12\uffff";
    static final String DFA123_eofS =
        "\12\uffff";
    static final String DFA123_minS =
        "\1\47\11\uffff";
    static final String DFA123_maxS =
        "\1\u00a0\11\uffff";
    static final String DFA123_acceptS =
        "\1\uffff\1\1\1\2\7\uffff";
    static final String DFA123_specialS =
        "\12\uffff}>";
    static final String[] DFA123_transitionS = {
            "\1\2\12\uffff\1\2\30\uffff\1\2\71\uffff\1\2\23\uffff\1\1\1\2"+
            "\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA123_eot = DFA.unpackEncodedString(DFA123_eotS);
    static final short[] DFA123_eof = DFA.unpackEncodedString(DFA123_eofS);
    static final char[] DFA123_min = DFA.unpackEncodedStringToUnsignedChars(DFA123_minS);
    static final char[] DFA123_max = DFA.unpackEncodedStringToUnsignedChars(DFA123_maxS);
    static final short[] DFA123_accept = DFA.unpackEncodedString(DFA123_acceptS);
    static final short[] DFA123_special = DFA.unpackEncodedString(DFA123_specialS);
    static final short[][] DFA123_transition;

    static {
        int numStates = DFA123_transitionS.length;
        DFA123_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA123_transition[i] = DFA.unpackEncodedString(DFA123_transitionS[i]);
        }
    }

    class DFA123 extends DFA {

        public DFA123(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 123;
            this.eot = DFA123_eot;
            this.eof = DFA123_eof;
            this.min = DFA123_min;
            this.max = DFA123_max;
            this.accept = DFA123_accept;
            this.special = DFA123_special;
            this.transition = DFA123_transition;
        }
        public String getDescription() {
            return "389:20: ( CONSTRAINT name= id )?";
        }
    }
    static final String DFA125_eotS =
        "\21\uffff";
    static final String DFA125_eofS =
        "\1\2\20\uffff";
    static final String DFA125_minS =
        "\1\40\20\uffff";
    static final String DFA125_maxS =
        "\1\u00a0\20\uffff";
    static final String DFA125_acceptS =
        "\1\uffff\1\1\1\2\16\uffff";
    static final String DFA125_specialS =
        "\21\uffff}>";
    static final String[] DFA125_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\40\uffff"+
            "\2\1\22\uffff\1\2\4\uffff\1\2\23\uffff\2\2\1\uffff\3\2\1\uffff"+
            "\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA125_eot = DFA.unpackEncodedString(DFA125_eotS);
    static final short[] DFA125_eof = DFA.unpackEncodedString(DFA125_eofS);
    static final char[] DFA125_min = DFA.unpackEncodedStringToUnsignedChars(DFA125_minS);
    static final char[] DFA125_max = DFA.unpackEncodedStringToUnsignedChars(DFA125_maxS);
    static final short[] DFA125_accept = DFA.unpackEncodedString(DFA125_acceptS);
    static final short[] DFA125_special = DFA.unpackEncodedString(DFA125_specialS);
    static final short[][] DFA125_transition;

    static {
        int numStates = DFA125_transitionS.length;
        DFA125_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA125_transition[i] = DFA.unpackEncodedString(DFA125_transitionS[i]);
        }
    }

    class DFA125 extends DFA {

        public DFA125(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 125;
            this.eot = DFA125_eot;
            this.eof = DFA125_eof;
            this.min = DFA125_min;
            this.max = DFA125_max;
            this.accept = DFA125_accept;
            this.special = DFA125_special;
            this.transition = DFA125_transition;
        }
        public String getDescription() {
            return "409:37: ( ASC | DESC )?";
        }
    }
    static final String DFA126_eotS =
        "\20\uffff";
    static final String DFA126_eofS =
        "\1\2\17\uffff";
    static final String DFA126_minS =
        "\1\40\17\uffff";
    static final String DFA126_maxS =
        "\1\u00a0\17\uffff";
    static final String DFA126_acceptS =
        "\1\uffff\1\1\1\2\15\uffff";
    static final String DFA126_specialS =
        "\20\uffff}>";
    static final String[] DFA126_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\64\uffff"+
            "\1\1\4\uffff\1\2\23\uffff\2\2\1\uffff\3\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA126_eot = DFA.unpackEncodedString(DFA126_eotS);
    static final short[] DFA126_eof = DFA.unpackEncodedString(DFA126_eofS);
    static final char[] DFA126_min = DFA.unpackEncodedStringToUnsignedChars(DFA126_minS);
    static final char[] DFA126_max = DFA.unpackEncodedStringToUnsignedChars(DFA126_maxS);
    static final short[] DFA126_accept = DFA.unpackEncodedString(DFA126_acceptS);
    static final short[] DFA126_special = DFA.unpackEncodedString(DFA126_specialS);
    static final short[][] DFA126_transition;

    static {
        int numStates = DFA126_transitionS.length;
        DFA126_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA126_transition[i] = DFA.unpackEncodedString(DFA126_transitionS[i]);
        }
    }

    class DFA126 extends DFA {

        public DFA126(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 126;
            this.eot = DFA126_eot;
            this.eof = DFA126_eof;
            this.min = DFA126_min;
            this.max = DFA126_max;
            this.accept = DFA126_accept;
            this.special = DFA126_special;
            this.transition = DFA126_transition;
        }
        public String getDescription() {
            return "409:51: ( table_conflict_clause )?";
        }
    }
    static final String DFA127_eotS =
        "\17\uffff";
    static final String DFA127_eofS =
        "\1\2\16\uffff";
    static final String DFA127_minS =
        "\1\40\16\uffff";
    static final String DFA127_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA127_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA127_specialS =
        "\17\uffff}>";
    static final String[] DFA127_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\71\uffff"+
            "\1\2\23\uffff\2\2\1\uffff\1\1\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA127_eot = DFA.unpackEncodedString(DFA127_eotS);
    static final short[] DFA127_eof = DFA.unpackEncodedString(DFA127_eofS);
    static final char[] DFA127_min = DFA.unpackEncodedStringToUnsignedChars(DFA127_minS);
    static final char[] DFA127_max = DFA.unpackEncodedStringToUnsignedChars(DFA127_maxS);
    static final short[] DFA127_accept = DFA.unpackEncodedString(DFA127_acceptS);
    static final short[] DFA127_special = DFA.unpackEncodedString(DFA127_specialS);
    static final short[][] DFA127_transition;

    static {
        int numStates = DFA127_transitionS.length;
        DFA127_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA127_transition[i] = DFA.unpackEncodedString(DFA127_transitionS[i]);
        }
    }

    class DFA127 extends DFA {

        public DFA127(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 127;
            this.eot = DFA127_eot;
            this.eof = DFA127_eof;
            this.min = DFA127_min;
            this.max = DFA127_max;
            this.accept = DFA127_accept;
            this.special = DFA127_special;
            this.transition = DFA127_transition;
        }
        public String getDescription() {
            return "409:74: ( AUTOINCREMENT )?";
        }
    }
    static final String DFA128_eotS =
        "\17\uffff";
    static final String DFA128_eofS =
        "\1\2\16\uffff";
    static final String DFA128_minS =
        "\1\40\16\uffff";
    static final String DFA128_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA128_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA128_specialS =
        "\17\uffff}>";
    static final String[] DFA128_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\64\uffff"+
            "\1\1\4\uffff\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA128_eot = DFA.unpackEncodedString(DFA128_eotS);
    static final short[] DFA128_eof = DFA.unpackEncodedString(DFA128_eofS);
    static final char[] DFA128_min = DFA.unpackEncodedStringToUnsignedChars(DFA128_minS);
    static final char[] DFA128_max = DFA.unpackEncodedStringToUnsignedChars(DFA128_maxS);
    static final short[] DFA128_accept = DFA.unpackEncodedString(DFA128_acceptS);
    static final short[] DFA128_special = DFA.unpackEncodedString(DFA128_specialS);
    static final short[][] DFA128_transition;

    static {
        int numStates = DFA128_transitionS.length;
        DFA128_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA128_transition[i] = DFA.unpackEncodedString(DFA128_transitionS[i]);
        }
    }

    class DFA128 extends DFA {

        public DFA128(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 128;
            this.eot = DFA128_eot;
            this.eof = DFA128_eof;
            this.min = DFA128_min;
            this.max = DFA128_max;
            this.accept = DFA128_accept;
            this.special = DFA128_special;
            this.transition = DFA128_transition;
        }
        public String getDescription() {
            return "411:38: ( table_conflict_clause )?";
        }
    }
    static final String DFA129_eotS =
        "\17\uffff";
    static final String DFA129_eofS =
        "\1\2\16\uffff";
    static final String DFA129_minS =
        "\1\40\16\uffff";
    static final String DFA129_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA129_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA129_specialS =
        "\17\uffff}>";
    static final String[] DFA129_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\64\uffff"+
            "\1\1\4\uffff\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA129_eot = DFA.unpackEncodedString(DFA129_eotS);
    static final short[] DFA129_eof = DFA.unpackEncodedString(DFA129_eofS);
    static final char[] DFA129_min = DFA.unpackEncodedStringToUnsignedChars(DFA129_minS);
    static final char[] DFA129_max = DFA.unpackEncodedStringToUnsignedChars(DFA129_maxS);
    static final short[] DFA129_accept = DFA.unpackEncodedString(DFA129_acceptS);
    static final short[] DFA129_special = DFA.unpackEncodedString(DFA129_specialS);
    static final short[][] DFA129_transition;

    static {
        int numStates = DFA129_transitionS.length;
        DFA129_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA129_transition[i] = DFA.unpackEncodedString(DFA129_transitionS[i]);
        }
    }

    class DFA129 extends DFA {

        public DFA129(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 129;
            this.eot = DFA129_eot;
            this.eof = DFA129_eof;
            this.min = DFA129_min;
            this.max = DFA129_max;
            this.accept = DFA129_accept;
            this.special = DFA129_special;
            this.transition = DFA129_transition;
        }
        public String getDescription() {
            return "413:30: ( table_conflict_clause )?";
        }
    }
    static final String DFA130_eotS =
        "\17\uffff";
    static final String DFA130_eofS =
        "\1\2\16\uffff";
    static final String DFA130_minS =
        "\1\40\16\uffff";
    static final String DFA130_maxS =
        "\1\u00a0\16\uffff";
    static final String DFA130_acceptS =
        "\1\uffff\1\1\1\2\14\uffff";
    static final String DFA130_specialS =
        "\17\uffff}>";
    static final String[] DFA130_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\64\uffff"+
            "\1\1\4\uffff\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA130_eot = DFA.unpackEncodedString(DFA130_eotS);
    static final short[] DFA130_eof = DFA.unpackEncodedString(DFA130_eofS);
    static final char[] DFA130_min = DFA.unpackEncodedStringToUnsignedChars(DFA130_minS);
    static final char[] DFA130_max = DFA.unpackEncodedStringToUnsignedChars(DFA130_maxS);
    static final short[] DFA130_accept = DFA.unpackEncodedString(DFA130_acceptS);
    static final short[] DFA130_special = DFA.unpackEncodedString(DFA130_specialS);
    static final short[][] DFA130_transition;

    static {
        int numStates = DFA130_transitionS.length;
        DFA130_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA130_transition[i] = DFA.unpackEncodedString(DFA130_transitionS[i]);
        }
    }

    class DFA130 extends DFA {

        public DFA130(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 130;
            this.eot = DFA130_eot;
            this.eof = DFA130_eof;
            this.min = DFA130_min;
            this.max = DFA130_max;
            this.accept = DFA130_accept;
            this.special = DFA130_special;
            this.transition = DFA130_transition;
        }
        public String getDescription() {
            return "415:35: ( table_conflict_clause )?";
        }
    }
    static final String DFA132_eotS =
        "\13\uffff";
    static final String DFA132_eofS =
        "\13\uffff";
    static final String DFA132_minS =
        "\1\54\12\uffff";
    static final String DFA132_maxS =
        "\1\133\12\uffff";
    static final String DFA132_acceptS =
        "\1\uffff\1\1\1\2\7\uffff\1\3";
    static final String DFA132_specialS =
        "\13\uffff}>";
    static final String[] DFA132_transitionS = {
            "\1\12\5\uffff\1\2\21\uffff\2\1\17\uffff\7\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA132_eot = DFA.unpackEncodedString(DFA132_eotS);
    static final short[] DFA132_eof = DFA.unpackEncodedString(DFA132_eofS);
    static final char[] DFA132_min = DFA.unpackEncodedStringToUnsignedChars(DFA132_minS);
    static final char[] DFA132_max = DFA.unpackEncodedStringToUnsignedChars(DFA132_maxS);
    static final short[] DFA132_accept = DFA.unpackEncodedString(DFA132_acceptS);
    static final short[] DFA132_special = DFA.unpackEncodedString(DFA132_specialS);
    static final short[][] DFA132_transition;

    static {
        int numStates = DFA132_transitionS.length;
        DFA132_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA132_transition[i] = DFA.unpackEncodedString(DFA132_transitionS[i]);
        }
    }

    class DFA132 extends DFA {

        public DFA132(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 132;
            this.eot = DFA132_eot;
            this.eof = DFA132_eof;
            this.min = DFA132_min;
            this.max = DFA132_max;
            this.accept = DFA132_accept;
            this.special = DFA132_special;
            this.transition = DFA132_transition;
        }
        public String getDescription() {
            return "427:37: ( signed_default_number | literal_value | LPAREN expr RPAREN )";
        }
    }
    static final String DFA141_eotS =
        "\22\uffff";
    static final String DFA141_eofS =
        "\1\2\21\uffff";
    static final String DFA141_minS =
        "\1\40\21\uffff";
    static final String DFA141_maxS =
        "\1\u00a3\21\uffff";
    static final String DFA141_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA141_specialS =
        "\22\uffff}>";
    static final String[] DFA141_transitionS = {
            "\1\2\6\uffff\1\2\4\uffff\1\1\2\2\3\uffff\1\2\10\uffff\1\2\17"+
            "\uffff\1\2\64\uffff\1\2\4\uffff\1\2\23\uffff\2\2\2\uffff\2\2"+
            "\1\uffff\1\2\2\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA141_eot = DFA.unpackEncodedString(DFA141_eotS);
    static final short[] DFA141_eof = DFA.unpackEncodedString(DFA141_eofS);
    static final char[] DFA141_min = DFA.unpackEncodedStringToUnsignedChars(DFA141_minS);
    static final char[] DFA141_max = DFA.unpackEncodedStringToUnsignedChars(DFA141_maxS);
    static final short[] DFA141_accept = DFA.unpackEncodedString(DFA141_acceptS);
    static final short[] DFA141_special = DFA.unpackEncodedString(DFA141_specialS);
    static final short[][] DFA141_transition;

    static {
        int numStates = DFA141_transitionS.length;
        DFA141_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA141_transition[i] = DFA.unpackEncodedString(DFA141_transitionS[i]);
        }
    }

    class DFA141 extends DFA {

        public DFA141(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 141;
            this.eot = DFA141_eot;
            this.eof = DFA141_eof;
            this.min = DFA141_min;
            this.max = DFA141_max;
            this.accept = DFA141_accept;
            this.special = DFA141_special;
            this.transition = DFA141_transition;
        }
        public String getDescription() {
            return "456:40: ( LPAREN column_names+= id ( COMMA column_names+= id )* RPAREN )?";
        }
    }
    static final String DFA142_eotS =
        "\21\uffff";
    static final String DFA142_eofS =
        "\1\1\20\uffff";
    static final String DFA142_minS =
        "\1\40\20\uffff";
    static final String DFA142_maxS =
        "\1\u00a3\20\uffff";
    static final String DFA142_acceptS =
        "\1\uffff\1\2\15\uffff\1\1\1\uffff";
    static final String DFA142_specialS =
        "\21\uffff}>";
    static final String[] DFA142_transitionS = {
            "\1\1\6\uffff\1\1\5\uffff\2\1\3\uffff\1\1\10\uffff\1\17\17\uffff"+
            "\1\1\64\uffff\1\17\4\uffff\1\1\23\uffff\2\1\2\uffff\2\1\1\uffff"+
            "\1\1\2\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA142_eot = DFA.unpackEncodedString(DFA142_eotS);
    static final short[] DFA142_eof = DFA.unpackEncodedString(DFA142_eofS);
    static final char[] DFA142_min = DFA.unpackEncodedStringToUnsignedChars(DFA142_minS);
    static final char[] DFA142_max = DFA.unpackEncodedStringToUnsignedChars(DFA142_maxS);
    static final short[] DFA142_accept = DFA.unpackEncodedString(DFA142_acceptS);
    static final short[] DFA142_special = DFA.unpackEncodedString(DFA142_specialS);
    static final short[][] DFA142_transition;

    static {
        int numStates = DFA142_transitionS.length;
        DFA142_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA142_transition[i] = DFA.unpackEncodedString(DFA142_transitionS[i]);
        }
    }

    class DFA142 extends DFA {

        public DFA142(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 142;
            this.eot = DFA142_eot;
            this.eof = DFA142_eof;
            this.min = DFA142_min;
            this.max = DFA142_max;
            this.accept = DFA142_accept;
            this.special = DFA142_special;
            this.transition = DFA142_transition;
        }
        public String getDescription() {
            return "()* loopback of 457:3: ( fk_clause_action )*";
        }
    }
    static final String DFA143_eotS =
        "\21\uffff";
    static final String DFA143_eofS =
        "\1\3\20\uffff";
    static final String DFA143_minS =
        "\1\40\1\62\17\uffff";
    static final String DFA143_maxS =
        "\2\u00a3\17\uffff";
    static final String DFA143_acceptS =
        "\2\uffff\1\1\1\2\15\uffff";
    static final String DFA143_specialS =
        "\21\uffff}>";
    static final String[] DFA143_transitionS = {
            "\1\3\6\uffff\1\1\5\uffff\2\3\3\uffff\1\3\30\uffff\1\3\71\uffff"+
            "\1\3\23\uffff\2\3\2\uffff\2\3\1\uffff\1\3\2\uffff\1\2",
            "\1\3\160\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA143_eot = DFA.unpackEncodedString(DFA143_eotS);
    static final short[] DFA143_eof = DFA.unpackEncodedString(DFA143_eofS);
    static final char[] DFA143_min = DFA.unpackEncodedStringToUnsignedChars(DFA143_minS);
    static final char[] DFA143_max = DFA.unpackEncodedStringToUnsignedChars(DFA143_maxS);
    static final short[] DFA143_accept = DFA.unpackEncodedString(DFA143_acceptS);
    static final short[] DFA143_special = DFA.unpackEncodedString(DFA143_specialS);
    static final short[][] DFA143_transition;

    static {
        int numStates = DFA143_transitionS.length;
        DFA143_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA143_transition[i] = DFA.unpackEncodedString(DFA143_transitionS[i]);
        }
    }

    class DFA143 extends DFA {

        public DFA143(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 143;
            this.eot = DFA143_eot;
            this.eof = DFA143_eof;
            this.min = DFA143_min;
            this.max = DFA143_max;
            this.accept = DFA143_accept;
            this.special = DFA143_special;
            this.transition = DFA143_transition;
        }
        public String getDescription() {
            return "457:21: ( fk_clause_deferrable )?";
        }
    }
    static final String DFA147_eotS =
        "\21\uffff";
    static final String DFA147_eofS =
        "\1\2\20\uffff";
    static final String DFA147_minS =
        "\1\40\1\u008a\17\uffff";
    static final String DFA147_maxS =
        "\1\u00a4\1\u008b\17\uffff";
    static final String DFA147_acceptS =
        "\2\uffff\1\3\14\uffff\1\1\1\2";
    static final String DFA147_specialS =
        "\21\uffff}>";
    static final String[] DFA147_transitionS = {
            "\1\2\6\uffff\1\2\5\uffff\2\2\3\uffff\1\2\30\uffff\1\2\71\uffff"+
            "\1\2\23\uffff\2\2\2\uffff\2\2\1\uffff\1\2\3\uffff\1\1",
            "\1\17\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA147_eot = DFA.unpackEncodedString(DFA147_eotS);
    static final short[] DFA147_eof = DFA.unpackEncodedString(DFA147_eofS);
    static final char[] DFA147_min = DFA.unpackEncodedStringToUnsignedChars(DFA147_minS);
    static final char[] DFA147_max = DFA.unpackEncodedStringToUnsignedChars(DFA147_maxS);
    static final short[] DFA147_accept = DFA.unpackEncodedString(DFA147_acceptS);
    static final short[] DFA147_special = DFA.unpackEncodedString(DFA147_specialS);
    static final short[][] DFA147_transition;

    static {
        int numStates = DFA147_transitionS.length;
        DFA147_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA147_transition[i] = DFA.unpackEncodedString(DFA147_transitionS[i]);
        }
    }

    class DFA147 extends DFA {

        public DFA147(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 147;
            this.eot = DFA147_eot;
            this.eof = DFA147_eof;
            this.min = DFA147_min;
            this.max = DFA147_max;
            this.accept = DFA147_accept;
            this.special = DFA147_special;
            this.transition = DFA147_transition;
        }
        public String getDescription() {
            return "464:42: ( INITIALLY DEFERRED | INITIALLY IMMEDIATE )?";
        }
    }
    static final String DFA167_eotS =
        "\14\uffff";
    static final String DFA167_eofS =
        "\14\uffff";
    static final String DFA167_minS =
        "\1\41\1\44\12\uffff";
    static final String DFA167_maxS =
        "\1\u00b3\1\u00af\12\uffff";
    static final String DFA167_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\7\uffff";
    static final String DFA167_specialS =
        "\14\uffff}>";
    static final String[] DFA167_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\12\2\2\uffff"+
            "\1\2\1\uffff\3\2\3\uffff\70\2\1\1\34\2",
            "\1\2\2\uffff\1\4\132\uffff\1\2\3\uffff\1\2\1\uffff\1\2\44\uffff"+
            "\3\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA167_eot = DFA.unpackEncodedString(DFA167_eotS);
    static final short[] DFA167_eof = DFA.unpackEncodedString(DFA167_eofS);
    static final char[] DFA167_min = DFA.unpackEncodedStringToUnsignedChars(DFA167_minS);
    static final char[] DFA167_max = DFA.unpackEncodedStringToUnsignedChars(DFA167_maxS);
    static final short[] DFA167_accept = DFA.unpackEncodedString(DFA167_acceptS);
    static final short[] DFA167_special = DFA.unpackEncodedString(DFA167_specialS);
    static final short[][] DFA167_transition;

    static {
        int numStates = DFA167_transitionS.length;
        DFA167_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA167_transition[i] = DFA.unpackEncodedString(DFA167_transitionS[i]);
        }
    }

    class DFA167 extends DFA {

        public DFA167(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 167;
            this.eot = DFA167_eot;
            this.eof = DFA167_eof;
            this.min = DFA167_min;
            this.max = DFA167_max;
            this.accept = DFA167_accept;
            this.special = DFA167_special;
            this.transition = DFA167_transition;
        }
        public String getDescription() {
            return "492:48: ( IF NOT EXISTS )?";
        }
    }
    static final String DFA168_eotS =
        "\21\uffff";
    static final String DFA168_eofS =
        "\21\uffff";
    static final String DFA168_minS =
        "\1\41\2\44\16\uffff";
    static final String DFA168_maxS =
        "\1\u00b3\2\u00af\16\uffff";
    static final String DFA168_acceptS =
        "\3\uffff\1\1\1\2\14\uffff";
    static final String DFA168_specialS =
        "\21\uffff}>";
    static final String[] DFA168_transitionS = {
            "\3\2\1\uffff\2\2\1\uffff\3\2\6\uffff\3\2\27\uffff\1\2\1\1\10"+
            "\2\2\uffff\1\1\1\uffff\3\2\3\uffff\125\2",
            "\1\3\135\uffff\1\4\3\uffff\1\4\1\uffff\1\4\44\uffff\3\4",
            "\1\3\135\uffff\1\4\3\uffff\1\4\1\uffff\1\4\44\uffff\3\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA168_eot = DFA.unpackEncodedString(DFA168_eotS);
    static final short[] DFA168_eof = DFA.unpackEncodedString(DFA168_eofS);
    static final char[] DFA168_min = DFA.unpackEncodedStringToUnsignedChars(DFA168_minS);
    static final char[] DFA168_max = DFA.unpackEncodedStringToUnsignedChars(DFA168_maxS);
    static final short[] DFA168_accept = DFA.unpackEncodedString(DFA168_acceptS);
    static final short[] DFA168_special = DFA.unpackEncodedString(DFA168_specialS);
    static final short[][] DFA168_transition;

    static {
        int numStates = DFA168_transitionS.length;
        DFA168_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA168_transition[i] = DFA.unpackEncodedString(DFA168_transitionS[i]);
        }
    }

    class DFA168 extends DFA {

        public DFA168(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 168;
            this.eot = DFA168_eot;
            this.eof = DFA168_eof;
            this.min = DFA168_min;
            this.max = DFA168_max;
            this.accept = DFA168_accept;
            this.special = DFA168_special;
            this.transition = DFA168_transition;
        }
        public String getDescription() {
            return "492:65: (database_name= id DOT )?";
        }
    }
 

    public static final BitSet FOLLOW_sql_stmt_in_sql_stmt_list198 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEMI_in_sql_stmt_list201 = new BitSet(new long[]{0x0000000200000000L,0x00200FB200040000L,0x00000060000B4344L});
    public static final BitSet FOLLOW_sql_stmt_in_sql_stmt_list205 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEMI_in_sql_stmt_list207 = new BitSet(new long[]{0x0000000200000000L,0x00200FB200040000L,0x00000060000B4344L});
    public static final BitSet FOLLOW_EOF_in_sql_stmt_list215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sql_stmt_in_sql_stmt_itself223 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEMI_in_sql_stmt_itself226 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_sql_stmt_itself231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPLAIN_in_sql_stmt240 = new BitSet(new long[]{0x0000000600000000L,0x00200FB200040000L,0x00000060000B4344L});
    public static final BitSet FOLLOW_QUERY_in_sql_stmt243 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_PLAN_in_sql_stmt245 = new BitSet(new long[]{0x0000000200000000L,0x00200FB200040000L,0x00000060000B4344L});
    public static final BitSet FOLLOW_sql_stmt_core_in_sql_stmt251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pragma_stmt_in_sql_stmt_core261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attach_stmt_in_sql_stmt_core267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_detach_stmt_in_sql_stmt_core273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_analyze_stmt_in_sql_stmt_core279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_reindex_stmt_in_sql_stmt_core285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vacuum_stmt_in_sql_stmt_core291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_stmt_in_sql_stmt_core300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_insert_stmt_in_sql_stmt_core306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_update_stmt_in_sql_stmt_core312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delete_stmt_in_sql_stmt_core318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_begin_stmt_in_sql_stmt_core324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_commit_stmt_in_sql_stmt_core330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rollback_stmt_in_sql_stmt_core336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_savepoint_stmt_in_sql_stmt_core342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_release_stmt_in_sql_stmt_core348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_virtual_table_stmt_in_sql_stmt_core357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_table_stmt_in_sql_stmt_core363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_drop_table_stmt_in_sql_stmt_core369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alter_table_stmt_in_sql_stmt_core375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_view_stmt_in_sql_stmt_core381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_drop_view_stmt_in_sql_stmt_core387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_index_stmt_in_sql_stmt_core393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_drop_index_stmt_in_sql_stmt_core399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_trigger_stmt_in_sql_stmt_core405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_drop_trigger_stmt_in_sql_stmt_core411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_virtual_table_stmt_in_schema_create_table_stmt421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_create_table_stmt_in_schema_create_table_stmt425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_qualified_table_name435 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_qualified_table_name437 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_qualified_table_name443 = new BitSet(new long[]{0x000000A000000002L});
    public static final BitSet FOLLOW_INDEXED_in_qualified_table_name446 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_BY_in_qualified_table_name448 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_qualified_table_name452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_qualified_table_name456 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_INDEXED_in_qualified_table_name458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_subexpr_in_expr467 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_OR_in_expr470 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_or_subexpr_in_expr473 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr482 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_AND_in_or_subexpr485 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr488 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_eq_subexpr_in_and_subexpr497 = new BitSet(new long[]{0x0FFB888000000002L});
    public static final BitSet FOLLOW_cond_expr_in_and_subexpr499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_cond_expr511 = new BitSet(new long[]{0x0F00008000000000L});
    public static final BitSet FOLLOW_match_op_in_cond_expr514 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_eq_subexpr_in_cond_expr518 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_cond_expr521 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_eq_subexpr_in_cond_expr525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_cond_expr553 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_IN_in_cond_expr556 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_cond_expr558 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_cond_expr560 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_cond_expr563 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_cond_expr565 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_cond_expr569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_cond_expr591 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_IN_in_cond_expr594 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_cond_expr599 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_cond_expr601 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_cond_expr607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISNULL_in_cond_expr638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTNULL_in_cond_expr646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_cond_expr654 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NULL_in_cond_expr656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_cond_expr664 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NULL_in_cond_expr666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_cond_expr674 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_NOT_in_cond_expr676 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NULL_in_cond_expr678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_cond_expr689 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_BETWEEN_in_cond_expr692 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_eq_subexpr_in_cond_expr696 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_AND_in_cond_expr698 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_eq_subexpr_in_cond_expr702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_cond_expr728 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_eq_subexpr_in_cond_expr745 = new BitSet(new long[]{0x00F0000000000002L});
    public static final BitSet FOLLOW_set_in_match_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_neq_subexpr_in_eq_subexpr778 = new BitSet(new long[]{0xF000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr781 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_neq_subexpr_in_eq_subexpr798 = new BitSet(new long[]{0xF000000000000002L});
    public static final BitSet FOLLOW_bit_subexpr_in_neq_subexpr807 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000FL});
    public static final BitSet FOLLOW_set_in_neq_subexpr810 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_bit_subexpr_in_neq_subexpr827 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000FL});
    public static final BitSet FOLLOW_add_subexpr_in_bit_subexpr836 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_set_in_bit_subexpr839 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_add_subexpr_in_bit_subexpr848 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_mul_subexpr_in_add_subexpr857 = new BitSet(new long[]{0x0000000000000002L,0x00000000000001C0L});
    public static final BitSet FOLLOW_set_in_add_subexpr860 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_mul_subexpr_in_add_subexpr873 = new BitSet(new long[]{0x0000000000000002L,0x00000000000001C0L});
    public static final BitSet FOLLOW_con_subexpr_in_mul_subexpr882 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_mul_subexpr885 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_con_subexpr_in_mul_subexpr888 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_unary_subexpr_in_con_subexpr897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_op_in_con_subexpr901 = new BitSet(new long[]{0x000E176E00000000L,0xFFFFFFFFFFFFF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_unary_subexpr_in_con_subexpr903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unary_op0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_expr_in_unary_subexpr937 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_COLLATE_in_unary_subexpr940 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_unary_subexpr945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_value_in_atom_expr957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_parameter_in_atom_expr963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom_expr973 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_atom_expr975 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_atom_expr981 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_atom_expr983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_atom_expr989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom_expr1018 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_atom_expr1020 = new BitSet(new long[]{0x000E57EE00000000L,0xFFFFFFFFFFFFFC70L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_atom_expr1023 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1028 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_atom_expr1031 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1035 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_ASTERISK_in_atom_expr1041 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_atom_expr1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_atom_expr1070 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1073 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_atom_expr1075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CAST_in_atom_expr1082 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_atom_expr1085 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_atom_expr1090 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_type_name_in_atom_expr1093 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_atom_expr1095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_atom_expr1104 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1109 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_when_expr_in_atom_expr1113 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ELSE_in_atom_expr1117 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_atom_expr1121 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_END_in_atom_expr1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_raise_function_in_atom_expr1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_expr1158 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_when_expr1162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_THEN_in_when_expr1164 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_when_expr1168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_literal_value1190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_value1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_value1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOB_in_literal_value1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_value1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_literal_value1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_literal_value1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_literal_value1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_bind_parameter1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_bind_parameter1311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_INTEGER_in_bind_parameter1315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_bind_parameter1330 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_bind_parameter1334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_bind_parameter1349 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_bind_parameter1353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RAISE_in_raise_function1374 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_raise_function1377 = new BitSet(new long[]{0x0000000000000000L,0x0000000F00000000L});
    public static final BitSet FOLLOW_IGNORE_in_raise_function1381 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_set_in_raise_function1385 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_COMMA_in_raise_function1397 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_STRING_in_raise_function1402 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_raise_function1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type_name1415 = new BitSet(new long[]{0x0000100000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_type_name1419 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600030L});
    public static final BitSet FOLLOW_signed_number_in_type_name1423 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_type_name1426 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600030L});
    public static final BitSet FOLLOW_signed_number_in_type_name1430 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_type_name1434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_signed_number1465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_set_in_signed_number1474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRAGMA_in_pragma_stmt1488 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_pragma_stmt1493 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_pragma_stmt1495 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_pragma_stmt1501 = new BitSet(new long[]{0x0010100000000002L});
    public static final BitSet FOLLOW_EQUALS_in_pragma_stmt1504 = new BitSet(new long[]{0x0000000000000000L,0x0000000000E01030L});
    public static final BitSet FOLLOW_pragma_value_in_pragma_stmt1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_pragma_stmt1510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000E01030L});
    public static final BitSet FOLLOW_pragma_value_in_pragma_stmt1512 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_pragma_stmt1514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_signed_number_in_pragma_value1543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pragma_value1556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_pragma_value1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTACH_in_attach_stmt1587 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DATABASE_in_attach_stmt1590 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_attach_stmt1596 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_attach_stmt1598 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_attach_stmt1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DETACH_in_detach_stmt1610 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DATABASE_in_detach_stmt1613 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_detach_stmt1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANALYZE_in_analyze_stmt1627 = new BitSet(new long[]{0x000E076E00000002L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_analyze_stmt1632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_analyze_stmt1638 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_analyze_stmt1640 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_analyze_stmt1644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REINDEX_in_reindex_stmt1654 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_reindex_stmt1659 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_reindex_stmt1661 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_reindex_stmt1667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VACUUM_in_vacuum_stmt1675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_operation_conflict_clause1686 = new BitSet(new long[]{0x0000000000000000L,0x0000080F00000000L});
    public static final BitSet FOLLOW_set_in_operation_conflict_clause1688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_ordering_term1713 = new BitSet(new long[]{0x0000000000000002L,0x0000300000000000L});
    public static final BitSet FOLLOW_ASC_in_ordering_term1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_ordering_term1722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_operation_limited_clause1752 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_BY_in_operation_limited_clause1754 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_operation_limited_clause1756 = new BitSet(new long[]{0x0000200000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_COMMA_in_operation_limited_clause1759 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_operation_limited_clause1761 = new BitSet(new long[]{0x0000200000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_LIMIT_in_operation_limited_clause1769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_INTEGER_in_operation_limited_clause1773 = new BitSet(new long[]{0x0000200000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_set_in_operation_limited_clause1776 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_INTEGER_in_operation_limited_clause1786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_list_in_select_stmt1796 = new BitSet(new long[]{0x0000000000000002L,0x0000C00000000000L});
    public static final BitSet FOLLOW_ORDER_in_select_stmt1801 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_BY_in_select_stmt1803 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_select_stmt1805 = new BitSet(new long[]{0x0000200000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt1808 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_select_stmt1810 = new BitSet(new long[]{0x0000200000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_LIMIT_in_select_stmt1819 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt1823 = new BitSet(new long[]{0x0000200000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_OFFSET_in_select_stmt1827 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt1831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt1836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_core_in_select_list1881 = new BitSet(new long[]{0x0000000000000002L,0x001A000000000000L});
    public static final BitSet FOLLOW_select_op_in_select_list1884 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_select_core_in_select_list1887 = new BitSet(new long[]{0x0000000000000002L,0x001A000000000000L});
    public static final BitSet FOLLOW_UNION_in_select_op1896 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_ALL_in_select_op1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTERSECT_in_select_op1906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCEPT_in_select_op1910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_select_core1919 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC70L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ALL_in_select_core1922 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC70L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_DISTINCT_in_select_core1926 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC70L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_result_column_in_select_core1930 = new BitSet(new long[]{0x0000200000000002L,0x01C0000000000000L});
    public static final BitSet FOLLOW_COMMA_in_select_core1933 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC70L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_result_column_in_select_core1935 = new BitSet(new long[]{0x0000200000000002L,0x01C0000000000000L});
    public static final BitSet FOLLOW_FROM_in_select_core1940 = new BitSet(new long[]{0x000E176E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_join_source_in_select_core1942 = new BitSet(new long[]{0x0000000000000002L,0x0180000000000000L});
    public static final BitSet FOLLOW_WHERE_in_select_core1947 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_select_core1951 = new BitSet(new long[]{0x0000000000000002L,0x0100000000000000L});
    public static final BitSet FOLLOW_GROUP_in_select_core1959 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_BY_in_select_core1961 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_select_core1963 = new BitSet(new long[]{0x0000200000000002L,0x0200000000000000L});
    public static final BitSet FOLLOW_COMMA_in_select_core1966 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_ordering_term_in_select_core1968 = new BitSet(new long[]{0x0000200000000002L,0x0200000000000000L});
    public static final BitSet FOLLOW_HAVING_in_select_core1973 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_select_core1977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_result_column2047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_result_column2055 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_result_column2057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_ASTERISK_in_result_column2059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_result_column2074 = new BitSet(new long[]{0x000E076E00000002L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_AS_in_result_column2078 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_result_column2084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_single_source_in_join_source2105 = new BitSet(new long[]{0x0000200000000002L,0xFC00000000000000L});
    public static final BitSet FOLLOW_join_op_in_join_source2108 = new BitSet(new long[]{0x000E176E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_single_source_in_join_source2111 = new BitSet(new long[]{0x0000200000000002L,0xFC00000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_join_constraint_in_join_source2114 = new BitSet(new long[]{0x0000200000000002L,0xFC00000000000000L});
    public static final BitSet FOLLOW_id_in_single_source2131 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_single_source2133 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_single_source2139 = new BitSet(new long[]{0x000000A000000002L,0x0000000000009000L});
    public static final BitSet FOLLOW_AS_in_single_source2143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_single_source2149 = new BitSet(new long[]{0x000000A000000002L});
    public static final BitSet FOLLOW_INDEXED_in_single_source2154 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_BY_in_single_source2156 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_single_source2160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_single_source2164 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_INDEXED_in_single_source2166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_single_source2207 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_select_stmt_in_single_source2209 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_single_source2211 = new BitSet(new long[]{0x0000000000000002L,0x0000000000009000L});
    public static final BitSet FOLLOW_AS_in_single_source2215 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_single_source2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_single_source2243 = new BitSet(new long[]{0x000E176E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_join_source_in_single_source2246 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_single_source2248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_join_op2259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NATURAL_in_join_op2266 = new BitSet(new long[]{0x0000000000000000L,0xF800000000000000L});
    public static final BitSet FOLLOW_LEFT_in_join_op2272 = new BitSet(new long[]{0x0000000000000000L,0x9000000000000000L});
    public static final BitSet FOLLOW_OUTER_in_join_op2277 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_INNER_in_join_op2283 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_CROSS_in_join_op2287 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_JOIN_in_join_op2290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ON_in_join_constraint2301 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_join_constraint2304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_USING_in_join_constraint2310 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_join_constraint2312 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_join_constraint2316 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_join_constraint2319 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_join_constraint2323 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_join_constraint2327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSERT_in_insert_stmt2346 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_operation_conflict_clause_in_insert_stmt2349 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_REPLACE_in_insert_stmt2355 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_INTO_in_insert_stmt2358 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_insert_stmt2363 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_insert_stmt2365 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_insert_stmt2371 = new BitSet(new long[]{0x0000100000000000L,0x0020000000000000L,0x0000000000000030L});
    public static final BitSet FOLLOW_LPAREN_in_insert_stmt2378 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_insert_stmt2382 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_insert_stmt2385 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_insert_stmt2389 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_insert_stmt2393 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_VALUES_in_insert_stmt2402 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_insert_stmt2404 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_insert_stmt2408 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_insert_stmt2411 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_insert_stmt2415 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_insert_stmt2419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_stmt_in_insert_stmt2423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_insert_stmt2430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_VALUES_in_insert_stmt2432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_update_stmt2442 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_operation_conflict_clause_in_update_stmt2445 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_qualified_table_name_in_update_stmt2449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_SET_in_update_stmt2453 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_update_set_in_update_stmt2457 = new BitSet(new long[]{0x0000200000000002L,0x0080C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_update_stmt2460 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_update_set_in_update_stmt2464 = new BitSet(new long[]{0x0000200000000002L,0x0080C00000000000L});
    public static final BitSet FOLLOW_WHERE_in_update_stmt2469 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_update_stmt2471 = new BitSet(new long[]{0x0000000000000002L,0x0000C00000000000L});
    public static final BitSet FOLLOW_operation_limited_clause_in_update_stmt2476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_update_set2487 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_EQUALS_in_update_set2489 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_update_set2491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_delete_stmt2499 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_FROM_in_delete_stmt2501 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_qualified_table_name_in_delete_stmt2503 = new BitSet(new long[]{0x0000000000000002L,0x0080C00000000000L});
    public static final BitSet FOLLOW_WHERE_in_delete_stmt2506 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_delete_stmt2508 = new BitSet(new long[]{0x0000000000000002L,0x0000C00000000000L});
    public static final BitSet FOLLOW_operation_limited_clause_in_delete_stmt2513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BEGIN_in_begin_stmt2523 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000003C00L});
    public static final BitSet FOLLOW_set_in_begin_stmt2525 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_TRANSACTION_in_begin_stmt2539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_commit_stmt2549 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_TRANSACTION_in_commit_stmt2558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROLLBACK_in_rollback_stmt2568 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x000000000000A000L});
    public static final BitSet FOLLOW_TRANSACTION_in_rollback_stmt2571 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_TO_in_rollback_stmt2576 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_SAVEPOINT_in_rollback_stmt2579 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_rollback_stmt2585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SAVEPOINT_in_savepoint_stmt2595 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_savepoint_stmt2599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RELEASE_in_release_stmt2607 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_SAVEPOINT_in_release_stmt2610 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_release_stmt2616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ON_in_table_conflict_clause2628 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_CONFLICT_in_table_conflict_clause2631 = new BitSet(new long[]{0x0000000000000000L,0x0000080F00000000L});
    public static final BitSet FOLLOW_set_in_table_conflict_clause2634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_in_create_virtual_table_stmt2661 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_VIRTUAL_in_create_virtual_table_stmt2663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_TABLE_in_create_virtual_table_stmt2665 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_virtual_table_stmt2670 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_create_virtual_table_stmt2672 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_virtual_table_stmt2678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_USING_in_create_virtual_table_stmt2682 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_virtual_table_stmt2686 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LPAREN_in_create_virtual_table_stmt2689 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFDFFFDFFFFFFL});
    public static final BitSet FOLLOW_column_def_in_create_virtual_table_stmt2691 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_create_virtual_table_stmt2694 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFDFFFDFFFFFFL});
    public static final BitSet FOLLOW_column_def_in_create_virtual_table_stmt2696 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_create_virtual_table_stmt2700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_in_create_table_stmt2746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_TEMPORARY_in_create_table_stmt2748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_TABLE_in_create_table_stmt2751 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_create_table_stmt2754 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_NOT_in_create_table_stmt2756 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_create_table_stmt2758 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_table_stmt2765 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_create_table_stmt2767 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_table_stmt2773 = new BitSet(new long[]{0x0000100000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_LPAREN_in_create_table_stmt2779 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFDFFFDFFFFFFL});
    public static final BitSet FOLLOW_column_def_in_create_table_stmt2781 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_create_table_stmt2784 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFDFFFDFFFFFFL});
    public static final BitSet FOLLOW_column_def_in_create_table_stmt2786 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_create_table_stmt2791 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000E6000000L});
    public static final BitSet FOLLOW_table_constraint_in_create_table_stmt2793 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_create_table_stmt2797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AS_in_create_table_stmt2803 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_select_stmt_in_create_table_stmt2805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_column_def_in_column_def2861 = new BitSet(new long[]{0x0004008000000002L,0x0000000000001800L,0x0000000166000020L});
    public static final BitSet FOLLOW_type_name_in_column_def2863 = new BitSet(new long[]{0x0004008000000002L,0x0000000000000800L,0x0000000166000020L});
    public static final BitSet FOLLOW_column_constraint_in_column_def2866 = new BitSet(new long[]{0x0004008000000002L,0x0000000000000800L,0x0000000166000020L});
    public static final BitSet FOLLOW_CONSTRAINT_in_column_constraint2892 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_column_constraint2896 = new BitSet(new long[]{0x0004008000000000L,0x0000000000000800L,0x0000000166000020L});
    public static final BitSet FOLLOW_column_constraint_pk_in_column_constraint2904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_not_null_in_column_constraint2910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_null_in_column_constraint2916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_unique_in_column_constraint2922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_check_in_column_constraint2928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_default_in_column_constraint2934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_constraint_collate_in_column_constraint2940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fk_clause_in_column_constraint2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIMARY_in_column_constraint_pk3006 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_KEY_in_column_constraint_pk3009 = new BitSet(new long[]{0x0000000000000002L,0x0000300000000000L,0x0000000010000001L});
    public static final BitSet FOLLOW_set_in_column_constraint_pk3012 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_column_constraint_pk3021 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_AUTOINCREMENT_in_column_constraint_pk3025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_column_constraint_not_null3034 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NULL_in_column_constraint_not_null3036 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_column_constraint_not_null3038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_column_constraint_null3055 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_column_constraint_null3057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNIQUE_in_column_constraint_unique3074 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_column_constraint_unique3077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHECK_in_column_constraint_check3085 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_column_constraint_check3088 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_column_constraint_check3091 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_column_constraint_check3093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_numeric_literal_value3104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numeric_literal_value3118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_signed_default_number3136 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_numeric_literal_value_in_signed_default_number3145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_column_constraint_default3153 = new BitSet(new long[]{0x0004100000000000L,0x000000000FE00030L});
    public static final BitSet FOLLOW_signed_default_number_in_column_constraint_default3157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_value_in_column_constraint_default3161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_column_constraint_default3165 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_column_constraint_default3168 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_column_constraint_default3170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLATE_in_column_constraint_collate3179 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_column_constraint_collate3184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONSTRAINT_in_table_constraint3193 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint3197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000E6000000L});
    public static final BitSet FOLLOW_table_constraint_pk_in_table_constraint3205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_table_constraint_unique_in_table_constraint3211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_table_constraint_check_in_table_constraint3217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_table_constraint_fk_in_table_constraint3223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIMARY_in_table_constraint_pk3263 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_KEY_in_table_constraint_pk3265 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_table_constraint_pk3269 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_pk3273 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_table_constraint_pk3276 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_pk3280 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_table_constraint_pk3284 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_table_constraint_pk3286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNIQUE_in_table_constraint_unique3311 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_table_constraint_unique3315 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_unique3319 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_table_constraint_unique3322 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_unique3326 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_table_constraint_unique3330 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_table_conflict_clause_in_table_constraint_unique3332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHECK_in_table_constraint_check3357 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_table_constraint_check3360 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_table_constraint_check3363 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RPAREN_in_table_constraint_check3365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOREIGN_in_table_constraint_fk3373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_KEY_in_table_constraint_fk3375 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_table_constraint_fk3377 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_fk3381 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_table_constraint_fk3384 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_table_constraint_fk3388 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_table_constraint_fk3392 = new BitSet(new long[]{0x0004008000000000L,0x0000000000000800L,0x0000000166000020L});
    public static final BitSet FOLLOW_fk_clause_in_table_constraint_fk3394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REFERENCES_in_fk_clause3417 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_fk_clause3421 = new BitSet(new long[]{0x0800108000000002L,0x0000000000000000L,0x0000000800000001L});
    public static final BitSet FOLLOW_LPAREN_in_fk_clause3424 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_fk_clause3428 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_fk_clause3431 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_fk_clause3435 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_fk_clause3439 = new BitSet(new long[]{0x0800008000000002L,0x0000000000000000L,0x0000000800000001L});
    public static final BitSet FOLLOW_fk_clause_action_in_fk_clause3445 = new BitSet(new long[]{0x0800008000000002L,0x0000000000000000L,0x0000000800000001L});
    public static final BitSet FOLLOW_fk_clause_deferrable_in_fk_clause3448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ON_in_fk_clause_action3482 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000144L});
    public static final BitSet FOLLOW_set_in_fk_clause_action3485 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000600000080L});
    public static final BitSet FOLLOW_SET_in_fk_clause_action3498 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NULL_in_fk_clause_action3501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_fk_clause_action3505 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DEFAULT_in_fk_clause_action3508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASCADE_in_fk_clause_action3512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESTRICT_in_fk_clause_action3516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCH_in_fk_clause_action3523 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_fk_clause_action3526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_fk_clause_deferrable3534 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_DEFERRABLE_in_fk_clause_deferrable3538 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_INITIALLY_in_fk_clause_deferrable3542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_DEFERRED_in_fk_clause_deferrable3545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INITIALLY_in_fk_clause_deferrable3549 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_IMMEDIATE_in_fk_clause_deferrable3552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DROP_in_drop_table_stmt3562 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_TABLE_in_drop_table_stmt3564 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_drop_table_stmt3567 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_drop_table_stmt3569 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_table_stmt3576 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_drop_table_stmt3578 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_table_stmt3584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALTER_in_alter_table_stmt3614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_TABLE_in_alter_table_stmt3616 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_alter_table_stmt3621 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_alter_table_stmt3623 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_alter_table_stmt3629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000018000000000L});
    public static final BitSet FOLLOW_RENAME_in_alter_table_stmt3632 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_TO_in_alter_table_stmt3634 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_alter_table_stmt3638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ADD_in_alter_table_stmt3642 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFDFFFFFFL});
    public static final BitSet FOLLOW_COLUMN_in_alter_table_stmt3645 = new BitSet(new long[]{0x0F0F8FEE00000000L,0xFFFFFFFF8E9FF800L,0x000FFDFFFDFFFFFFL});
    public static final BitSet FOLLOW_column_def_in_alter_table_stmt3649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_in_create_view_stmt3658 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000040000400000L});
    public static final BitSet FOLLOW_TEMPORARY_in_create_view_stmt3660 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_VIEW_in_create_view_stmt3663 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_create_view_stmt3666 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_NOT_in_create_view_stmt3668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_create_view_stmt3670 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_view_stmt3677 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_create_view_stmt3679 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_view_stmt3685 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_create_view_stmt3687 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_select_stmt_in_create_view_stmt3689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DROP_in_drop_view_stmt3697 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_VIEW_in_drop_view_stmt3699 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_drop_view_stmt3702 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_drop_view_stmt3704 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_view_stmt3711 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_drop_view_stmt3713 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_view_stmt3719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_in_create_index_stmt3727 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080020000000L});
    public static final BitSet FOLLOW_UNIQUE_in_create_index_stmt3730 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_INDEX_in_create_index_stmt3734 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_create_index_stmt3737 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_NOT_in_create_index_stmt3739 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_create_index_stmt3741 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_index_stmt3748 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_create_index_stmt3750 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_index_stmt3756 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ON_in_create_index_stmt3760 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_index_stmt3764 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAREN_in_create_index_stmt3766 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_indexed_column_in_create_index_stmt3770 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_COMMA_in_create_index_stmt3773 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_indexed_column_in_create_index_stmt3777 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_RPAREN_in_create_index_stmt3781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_indexed_column3827 = new BitSet(new long[]{0x0000000000000002L,0x0000300000000800L});
    public static final BitSet FOLLOW_COLLATE_in_indexed_column3830 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_indexed_column3834 = new BitSet(new long[]{0x0000000000000002L,0x0000300000000000L});
    public static final BitSet FOLLOW_ASC_in_indexed_column3839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_indexed_column3843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DROP_in_drop_index_stmt3874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_INDEX_in_drop_index_stmt3876 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_drop_index_stmt3879 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_drop_index_stmt3881 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_index_stmt3888 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_drop_index_stmt3890 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_index_stmt3896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CREATE_in_create_trigger_stmt3926 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000100000400000L});
    public static final BitSet FOLLOW_TEMPORARY_in_create_trigger_stmt3928 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_TRIGGER_in_create_trigger_stmt3931 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_create_trigger_stmt3934 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_NOT_in_create_trigger_stmt3936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_create_trigger_stmt3938 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_trigger_stmt3945 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_create_trigger_stmt3947 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_trigger_stmt3953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000000144L});
    public static final BitSet FOLLOW_BEFORE_in_create_trigger_stmt3958 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000144L});
    public static final BitSet FOLLOW_AFTER_in_create_trigger_stmt3962 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000144L});
    public static final BitSet FOLLOW_INSTEAD_in_create_trigger_stmt3966 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_OF_in_create_trigger_stmt3968 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000144L});
    public static final BitSet FOLLOW_DELETE_in_create_trigger_stmt3973 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_INSERT_in_create_trigger_stmt3977 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_UPDATE_in_create_trigger_stmt3981 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0001000000000001L});
    public static final BitSet FOLLOW_OF_in_create_trigger_stmt3984 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_trigger_stmt3988 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COMMA_in_create_trigger_stmt3991 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_trigger_stmt3995 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ON_in_create_trigger_stmt4004 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_create_trigger_stmt4008 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L,0x0002000000000200L});
    public static final BitSet FOLLOW_FOR_in_create_trigger_stmt4011 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_EACH_in_create_trigger_stmt4013 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_ROW_in_create_trigger_stmt4015 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L,0x0000000000000200L});
    public static final BitSet FOLLOW_WHEN_in_create_trigger_stmt4020 = new BitSet(new long[]{0x000E17EE00000000L,0xFFFFFFFFFFFFFC30L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_expr_in_create_trigger_stmt4022 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_BEGIN_in_create_trigger_stmt4028 = new BitSet(new long[]{0x0000000000000000L,0x0020080000000000L,0x0000000000000144L});
    public static final BitSet FOLLOW_update_stmt_in_create_trigger_stmt4032 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_insert_stmt_in_create_trigger_stmt4036 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_delete_stmt_in_create_trigger_stmt4040 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_select_stmt_in_create_trigger_stmt4044 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEMI_in_create_trigger_stmt4047 = new BitSet(new long[]{0x0000000000000000L,0x0020080000040000L,0x0000000000000144L});
    public static final BitSet FOLLOW_END_in_create_trigger_stmt4051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DROP_in_drop_trigger_stmt4059 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_TRIGGER_in_drop_trigger_stmt4061 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_IF_in_drop_trigger_stmt4064 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_EXISTS_in_drop_trigger_stmt4066 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_trigger_stmt4073 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_DOT_in_drop_trigger_stmt4075 = new BitSet(new long[]{0x000E076E00000000L,0xFFFFFFFF8E9FF800L,0x000FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_id_in_drop_trigger_stmt4081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_id_core4091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_core_in_id4110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_in_id4114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_keyword4121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_core_in_id_column_def4788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyword_column_def_in_id_column_def4792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_keyword_column_def4799 = new BitSet(new long[]{0x0000000000000002L});

}
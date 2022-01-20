package Frontend;

import AST.*;
import AST.Const.*;
import AST.Def.*;
import AST.Expr.*;
import AST.New.*;
import AST.Stmt.*;
import AST.ASTtype.*;
import AST.Expr.unaryExprNode.unaryOpType;
import AST.Expr.prefixSelfExprNode.prefixSelfOpType;
import AST.Expr.suffixSelfExprNode.suffixSelfOpType;
import AST.Expr.binaryExprNode.binaryOpType;
import Util.*;
import Util.error.*;
import Parser.MxBaseVisitor;
import Parser.MxParser;
import org.antlr.v4.runtime.misc.Pair;

public class ASTBuilder extends MxBaseVisitor<ASTNode>{
    public globalScope gScope;

    public ASTBuilder(globalScope gScope){
        this.gScope = gScope;
    }

    @Override public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        programNode program = new programNode(new position(ctx));
        ctx.subprogram().forEach(cd -> program.subprogramList.add((subprogramNode) visit(cd)));
        return program;
    }

    @Override public ASTNode visitSubprogram(MxParser.SubprogramContext ctx) {
        if (ctx.varDef() != null) return visit(ctx.varDef());
        else if (ctx.funcDef() != null) return visit(ctx.funcDef());
        else return visit(ctx.classDef());
    }

    @Override public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        varDefNode varDef = new varDefNode((variableTypeNode) visit(ctx.variableType()) , new position(ctx));
        for (int i = 0 ; i < ctx.Id().size() ; ++i) {
            if (varDef.varList.containsKey(ctx.Id(i).toString())) {
                throw new semanticError("Semantic Error: class is not defined" , new position(ctx));
            }
            if (ctx.expression(i) != null) varDef.varList.put(ctx.Id(i).toString() , (ExprNode) visit(ctx.expression(i)));
            else varDef.varList.put(ctx.Id(i).toString() , null);
        }
        return varDef;
    }

    @Override public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        classDefNode classDef = new classDefNode(ctx.Id().toString() , new position(ctx));
        ctx.varDef().forEach(cd -> classDef.varDefList.add((varDefNode) visit(cd)));
        ctx.funcDef().forEach(cd -> classDef.funcDefList.add((funcDefNode) visit(cd)));
        ctx.constructfuncDef().forEach(cd -> classDef.constructfuncDefList.add((constructfuncDefNode) visit(cd)));
        return classDef;
    }

    @Override public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        parameterListNode parameterList;
        if (ctx.parameterList() != null) {
            parameterList = (parameterListNode) visit(ctx.parameterList());
        } else parameterList = null;
        return new funcDefNode((returnTypeNode) visit(ctx.returnType()) , ctx.Id().toString()
                , parameterList , (suiteNode) visit(ctx.suite()) , new position(ctx));
    }

    @Override public ASTNode visitConstructfuncDef(MxParser.ConstructfuncDefContext ctx) {
        return new constructfuncDefNode(ctx.Id().toString() , (suiteNode) visit(ctx.suite()) , new position(ctx));
    }

    @Override public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        suiteNode suite = new suiteNode(new position(ctx));
        ctx.statement().forEach(cd -> suite.stmts.add((StmtNode) visit(cd)));
        return suite;
    }

    @Override public ASTNode visitBlockStmt(MxParser.BlockStmtContext ctx) {
        return new blockStmtNode((suiteNode) visit(ctx.suite()) , new position(ctx));
    }

    @Override public ASTNode visitVarDefStmt(MxParser.VarDefStmtContext ctx) {
        return new varDefStmtNode((varDefNode) visit(ctx.varDef()) , new position(ctx));
    }

    @Override public ASTNode visitClassDefStmt(MxParser.ClassDefStmtContext ctx) {
        return new classDefStmtNode((classDefNode) visit(ctx.classDef()) , new position(ctx));
    }

    @Override public ASTNode visitConditionStmt(MxParser.ConditionStmtContext ctx) {
        StmtNode thenStmt , elseStmt;
        if (ctx.statement(0) != null) {
            thenStmt = (StmtNode) visit(ctx.statement(0));
        } else thenStmt = null;
        if (ctx.statement(1) != null) {
            elseStmt = (StmtNode) visit(ctx.statement(1));
        } else elseStmt = null;
        return new conditionStmtNode((ExprNode) visit(ctx.expression()) , thenStmt , elseStmt , new position(ctx));
    }

    @Override public ASTNode visitWhileLoopStmt(MxParser.WhileLoopStmtContext ctx) {
        return new whileLoopStmtNode((ExprNode) visit(ctx.expression()) , (StmtNode) visit(ctx.statement())
                , new position(ctx));
    }

    @Override public ASTNode visitForLoopStmt(MxParser.ForLoopStmtContext ctx) {
        ExprNode forInit , forCond , forIncrement;
        if (ctx.ForInit != null) {
            forInit = (ExprNode) visit(ctx.ForInit);
        } else forInit = null;
        if (ctx.ForCond != null) {
            forCond = (ExprNode) visit(ctx.ForCond);
        } else forCond = null;
        if (ctx.ForIncrement != null) {
            forIncrement = (ExprNode) visit(ctx.ForIncrement);
        } else forIncrement = null;
        return new forLoopStmtNode(forInit , forCond , forIncrement , (StmtNode) visit(ctx.statement()) , new position(ctx));
    }

    @Override public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        ExprNode expr;
        if (ctx.expression() != null) {
            expr = (ExprNode) visit(ctx.expression());
        } else expr = null;
        return new returnStmtNode(expr , new position(ctx));
    }

    @Override public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        return new breakStmtNode(new position(ctx));
    }

    @Override public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        return new continueStmtNode(new position(ctx));
    }

    @Override public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
        return new exprStmtNode((ExprNode) visit(ctx.expression()) , new position(ctx));
    }

    @Override public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) { return null; }

    @Override public ASTNode visitIndexExpr(MxParser.IndexExprContext ctx) {
        return new indexExprNode((ExprNode) visit(ctx.expression(0)) , (ExprNode) visit(ctx.expression(1))
                , new position(ctx));
    }

    @Override public ASTNode visitThisExpr(MxParser.ThisExprContext ctx) {
        return new thisExprNode(new position(ctx));
    }

    @Override public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        return visit(ctx.newexpression());
    }

    @Override public ASTNode visitUnaryExpr(MxParser.UnaryExprContext ctx) {
        unaryOpType op;
        if (ctx.Not() != null) op = unaryOpType.not;
        else if (ctx.Tilde() != null) op = unaryOpType.tilde;
        else if (ctx.Plus() != null) op = unaryOpType.plus;
        else op = unaryOpType.minus;
        return new unaryExprNode((ExprNode) visit(ctx.expression()) , op , new position(ctx));
    }

    @Override public ASTNode visitPrefixSelfExpr(MxParser.PrefixSelfExprContext ctx) {
        prefixSelfOpType op;
        if (ctx.SelfPlus() != null) op = prefixSelfOpType.selfPlus;
        else op = prefixSelfOpType.selfMinus;
        return new prefixSelfExprNode((ExprNode) visit(ctx.expression()) , op , new position(ctx));
    }

    @Override public ASTNode visitSuffixSelfExpr(MxParser.SuffixSelfExprContext ctx) {
        suffixSelfOpType op;
        if (ctx.SelfPlus() != null) op = suffixSelfOpType.selfPlus;
        else op = suffixSelfOpType.selfMinus;
        return new suffixSelfExprNode((ExprNode) visit(ctx.expression()) , op , new position(ctx));
    }

    @Override public ASTNode visitLambdaExpr(MxParser.LambdaExprContext ctx) {
        parameterListNode parameterList;
        if (ctx.parameterList() != null) {
            parameterList = (parameterListNode) visit(ctx.parameterList());
        } else parameterList = null;
        expressionListNode expressionList;
        if (ctx.expressionList() != null) {
            expressionList = (expressionListNode) visit(ctx.expressionList());
        } else expressionList = null;
        return new lambdaExprNode(parameterList , (suiteNode) visit(ctx.suite())
                , expressionList , new position(ctx));
    }

    @Override public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        return visit(ctx.primary());
    }

    @Override public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        binaryOpType op;
        if (ctx.Mul() != null) op = binaryOpType.mul;
        else if (ctx.Div() != null) op = binaryOpType.div;
        else if (ctx.Mod() != null) op = binaryOpType.mod;
        else if (ctx.Plus() != null) op = binaryOpType.add;
        else if (ctx.Minus() != null) op = binaryOpType.sub;
        else if (ctx.LeftShift() != null) op = binaryOpType.leftShift;
        else if (ctx.RightShift() != null) op = binaryOpType.rightShift;
        else if (ctx.Less() != null) op = binaryOpType.less;
        else if (ctx.Greater() != null) op = binaryOpType.greater;
        else if (ctx.LessEqual() != null) op = binaryOpType.lessEqual;
        else if (ctx.GreaterEqual() != null) op = binaryOpType.greaterEqual;
        else if (ctx.Equal() != null) op = binaryOpType.equal;
        else if (ctx.NotEqual() != null) op = binaryOpType.notequal;
        else if (ctx.And() != null) op = binaryOpType.and;
        else if (ctx.Caret() != null) op = binaryOpType.caret;
        else if (ctx.Or() != null) op = binaryOpType.or;
        else if (ctx.AndAnd() != null) op = binaryOpType.andand;
        else op = binaryOpType.oror;
        return new binaryExprNode((ExprNode) visit(ctx.expression(0)) , (ExprNode) visit(ctx.expression(1))
                , op , new position(ctx));
    }

    @Override public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
        return new assignExprNode((ExprNode) visit(ctx.expression(0)) , (ExprNode) visit(ctx.expression(1))
                , new position(ctx));

    }

    @Override public ASTNode visitPointExpr(MxParser.PointExprContext ctx) {
        return new pointExprNode((ExprNode) visit(ctx.expression(0)) , (ExprNode) visit(ctx.expression(1))
                , new position(ctx));
    }

    @Override public ASTNode visitFunctionExpr(MxParser.FunctionExprContext ctx) {
        expressionListNode expressionList;
        if (ctx.expressionList() != null) {
            expressionList = (expressionListNode) visit(ctx.expressionList());
        } else expressionList = null;
        return new functionExprNode((ExprNode) visit(ctx.expression()) , expressionList , new position(ctx));
    }

    @Override public ASTNode visitNewArray(MxParser.NewArrayContext ctx) {
        int dim = ctx.LeftBracket().size();
        newArrayNode newArray = new newArrayNode((singleTypeNode) visit(ctx.singleType()) , dim , new position(ctx));
        ctx.expression().forEach(cd -> newArray.dimExpr.add((ExprNode) visit(cd)));
        return newArray;
    }

    @Override public ASTNode visitNewClass(MxParser.NewClassContext ctx) {
        Type type = new Type(ctx.Id().toString() , 0);
        return new newClassNode(type , new position(ctx));
    }

    @Override public ASTNode visitWrongNew_1(MxParser.WrongNew_1Context ctx) {
        throw new semanticError("Semantic Error: WrongNew_1 error" , new position(ctx));
    }

    @Override public ASTNode visitWrongNew_2(MxParser.WrongNew_2Context ctx) {
        throw new semanticError("Semantic Error: WrongNew_2 error" , new position(ctx));
    }

    @Override public ASTNode visitParameterList(MxParser.ParameterListContext ctx) {
        parameterListNode parameterList = new parameterListNode(new position(ctx));
        for (int i = 0 ; i < ctx.Id().size() ; ++i) {
            parameterList.parameterList.add(new Pair<>(ctx.Id(i).toString(), (variableTypeNode) visit(ctx.variableType(i))));
        }
        return parameterList;
    }

    @Override public ASTNode visitExpressionList(MxParser.ExpressionListContext ctx) {
        expressionListNode expressionList = new expressionListNode(new position(ctx));
        ctx.expression().forEach(cd -> expressionList.exprList.add((ExprNode) visit(cd)));
        return expressionList;
    }

    @Override public ASTNode visitSingleType(MxParser.SingleTypeContext ctx) {
        Type type;
        if (ctx.Int() != null) type = new Type(Type.var_type.Int , 0);
        else if (ctx.Bool() != null) type = new Type(Type.var_type.Bool , 0);
        else if (ctx.String() != null) type = new Type(Type.var_type.Str , 0);
        else type = new Type(ctx.Id().toString() , 0);
        return new singleTypeNode(type , new position(ctx));
    }

    @Override public ASTNode visitArrayType(MxParser.ArrayTypeContext ctx) {
        variableTypeNode variableType;
        if (ctx.singleType() != null) variableType = (variableTypeNode) visit(ctx.singleType());
        else variableType = (variableTypeNode) visit(ctx.arrayType());
        return new arrayTypeNode(variableType , new position(ctx));
    }

    @Override public ASTNode visitVariableType(MxParser.VariableTypeContext ctx) {
        if (ctx.singleType() != null) return visit(ctx.singleType());
        else return visit(ctx.arrayType());
    }

    @Override public ASTNode visitReturnType(MxParser.ReturnTypeContext ctx) {
        if (ctx.Void() != null) return new voidTypeNode(new position(ctx));
        else return visit(ctx.variableType());
    }

    @Override public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
        if (ctx.expression() != null) return visit(ctx.expression());
        else if (ctx.Id() != null) return new varExprNode(ctx.Id().toString() , new position(ctx));
        else return visit(ctx.literal());
    }

    @Override public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        if (ctx.IntConst() != null) return new intConstNode(Long.parseLong(ctx.IntConst().toString())
                , new position(ctx));
        else if (ctx.StringConst() != null) {
            String stringConst = ctx.StringConst().toString().substring(1 , ctx.StringConst().toString().length() - 1);
            return new stringConstNode(stringConst , new position(ctx));
        }
        else if (ctx.Null() != null) return new nullConstNode(new position(ctx));
        else {
            boolean flag;
            flag = ctx.True() != null;
            return new booleanConstNode(flag , new position(ctx));
        }
    }
}

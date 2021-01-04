package com.elias.proxy.dynamic;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.lang.model.element.Modifier;

/**
 * 以下代码转换成动态代理过程 // 第一步：生成TimeProxy源码 // 第二步：编译TimeProxy源码 // 第三步：加载到内存中并创建对象 //
 * 第四步：增加InvocationHandler接口 - 代理类继承的接口参数化 - 代理的处理逻辑也抽离（新增InvocationHandler接口，用于处理自定义逻辑）
 * public interface InvocationHandler { void invoke(Object proxy, Method method,
 * Object[] args); } proxy =>  这个参数指定动态生成的代理类，这里是TimeProxy method =>
 * 这个参数表示传入接口中的所有Method对象 args =>   这个参数对应当前method方法中的参数
 */


public class Proxy {

    public static Object newProxyInstance() throws IOException {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("TimeProxy")
            .addSuperinterface(Flyable.class);

        FieldSpec fieldSpec = FieldSpec
            .builder(Flyable.class, "flyable", Modifier.PRIVATE).build();
        typeSpecBuilder.addField(fieldSpec);

        MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Flyable.class, "flyable")
            .addStatement("this.flyable = flyable")
            .build();
        typeSpecBuilder.addMethod(constructorMethodSpec);

        Method[] methods = Flyable.class.getDeclaredMethods();
        for (Method method : methods) {
            MethodSpec methodSpec = MethodSpec.methodBuilder(method.getName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(method.getReturnType())
                .addStatement("long start = $T.currentTimeMillis()",
                    System.class)
                .addCode("\n")
                .addStatement("this.flyable." + method.getName() + "()")
                .addCode("\n")
                .addStatement("long end = $T.currentTimeMillis()", System.class)
                .addStatement("$T.out.println(\"Fly Time =\" + (end - start))",
                    System.class)
                .build();
            typeSpecBuilder.addMethod(methodSpec);
        }

        JavaFile javaFile = JavaFile
            .builder("com.elias.proxy", typeSpecBuilder.build()).build();
        // 为了看的更清楚，我将源码文件生成到桌面
        javaFile.writeTo(new File("/Users/Downloads/"));

        return null;
    }

}

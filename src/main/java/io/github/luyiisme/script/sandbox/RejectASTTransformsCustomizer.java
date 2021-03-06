/*
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.luyiisme.script.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import groovy.lang.Grapes;
import groovy.transform.ASTTest;
import groovy.transform.AnnotationCollector;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

/**
 * 参考 script-security-plugin 的 RejectASTTransformsCustomizer 逻辑（也保留文件license）。作用：禁止groovy编译期对AST的动态调整留有安全漏洞；
 *
 * @author: kevin.luy@antfin.com
 * @create: 2021-02-19 20:18
 **/
public class RejectASTTransformsCustomizer extends CompilationCustomizer {
    private static final List<String> BLOCKED_TRANSFORMS = Arrays.asList(ASTTest.class.getCanonicalName(),
        AnnotationCollector.class.getCanonicalName(), Grapes.class.getCanonicalName());

    public RejectASTTransformsCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(final SourceUnit source, GeneratorContext context, ClassNode classNode)
        throws CompilationFailedException {
        new RejectASTTransformsVisitor(source).visitClass(classNode);
    }

    // Note: Methods in this visitor that override methods from the superclass should call the implementation from the
    // superclass to ensure that any nested AST nodes are traversed.
    private static class RejectASTTransformsVisitor extends ClassCodeVisitorSupport {
        private SourceUnit source;

        public RejectASTTransformsVisitor(SourceUnit source) {
            this.source = source;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return source;
        }

        @Override
        public void visitImports(ModuleNode node) {
            if (node != null) {
                for (ImportNode importNode : node.getImports()) {
                    checkImportForBlockedAnnotation(importNode);
                }
                for (ImportNode importStaticNode : node.getStaticImports().values()) {
                    checkImportForBlockedAnnotation(importStaticNode);
                }
            }
            super.visitImports(node);
        }

        private void checkImportForBlockedAnnotation(ImportNode node) {
            if (node != null && node.getType() != null) {
                for (String blockedAnnotation : getBlockedTransforms()) {
                    if (blockedAnnotation.equals(node.getType().getName()) || blockedAnnotation.endsWith(
                        "." + node.getType().getName())) {
                        throw new SecurityException("Insecure annotation '" + node.getType().getName() +
                            "' you can tweak the security sandbox to allow it. Read more about this in the " +
                            "documentation.");
                    }
                }
            }
        }

        /**
         * If the node is annotated with one of the blocked transform annotations, throw a security exception.
         *
         * @param node the node to process
         */
        @Override
        public void visitAnnotations(AnnotatedNode node) {
            for (AnnotationNode an : node.getAnnotations()) {
                for (String blockedAnnotation : getBlockedTransforms()) {
                    if (blockedAnnotation.equals(an.getClassNode().getName()) || blockedAnnotation.endsWith(
                        "." + an.getClassNode().getName())) {
                        throw new SecurityException("Insecure annotation '" + an.getClassNode().getName() +
                            "' you can tweak the security sandbox to allow it. Read more about this in the " +
                            "documentation.");
                    }
                }
            }
            super.visitAnnotations(node);
        }
    }

    private static List<String> getBlockedTransforms() {
        List<String> blocked = new ArrayList<String>(BLOCKED_TRANSFORMS);

        String additionalBlocked = System.getProperty(
            RejectASTTransformsCustomizer.class.getName() + ".ADDITIONAL_BLOCKED_TRANSFORMS");

        if (additionalBlocked != null) {
            for (String b : additionalBlocked.split(",")) {
                blocked.add(b.trim());
            }
        }

        return blocked;
    }
}
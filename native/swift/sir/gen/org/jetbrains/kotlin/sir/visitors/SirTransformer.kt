/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See native/swift/sir/tree-generator/Readme.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.sir.visitors

import org.jetbrains.kotlin.sir.*

/**
 * Auto-generated by [org.jetbrains.kotlin.sir.tree.generator.printer.TransformerPrinter]
 */
abstract class SirTransformer<in D> : SirVisitor<SirElement, D>() {

    abstract fun <E : SirElement> transformElement(element: E, data: D): E

    final override fun visitElement(element: SirElement, data: D): SirElement {
        return transformElement(element, data)
    }

    open fun transformModule(module: SirModule, data: D): SirModule {
        return transformElement(module, data)
    }

    final override fun visitModule(module: SirModule, data: D): SirModule {
        return transformModule(module, data)
    }


    open fun transformDeclarationContainer(declarationContainer: SirDeclarationContainer, data: D): SirDeclarationContainer {
        return transformElement(declarationContainer, data)
    }

    final override fun visitDeclarationContainer(declarationContainer: SirDeclarationContainer, data: D): SirDeclarationContainer {
        return transformDeclarationContainer(declarationContainer, data)
    }

    open fun transformDeclaration(declaration: SirDeclaration, data: D): SirDeclaration {
        return transformElement(declaration, data)
    }

    final override fun visitDeclaration(declaration: SirDeclaration, data: D): SirDeclaration {
        return transformDeclaration(declaration, data)
    }


    open fun transformNamedDeclaration(namedDeclaration: SirNamedDeclaration, data: D): SirDeclaration {
        return transformDeclaration(namedDeclaration, data)
    }

    final override fun visitNamedDeclaration(declaration: SirNamedDeclaration, data: D): SirDeclaration {
        return transformNamedDeclaration(declaration, data)
    }

    open fun transformEnum(enum: SirEnum, data: D): SirDeclaration {
        return transformNamedDeclaration(enum, data)
    }

    final override fun visitEnum(enum: SirEnum, data: D): SirDeclaration {
        return transformEnum(enum, data)
    }

    open fun transformStruct(struct: SirStruct, data: D): SirDeclaration {
        return transformNamedDeclaration(struct, data)
    }

    final override fun visitStruct(struct: SirStruct, data: D): SirDeclaration {
        return transformStruct(struct, data)
    }

    open fun transformClass(klass: SirClass, data: D): SirDeclaration {
        return transformNamedDeclaration(klass, data)
    }

    final override fun visitClass(klass: SirClass, data: D): SirDeclaration {
        return transformClass(klass, data)
    }

    open fun transformCallable(callable: SirCallable, data: D): SirDeclaration {
        return transformDeclaration(callable, data)
    }

    final override fun visitCallable(callable: SirCallable, data: D): SirDeclaration {
        return transformCallable(callable, data)
    }

    open fun transformFunction(function: SirFunction, data: D): SirDeclaration {
        return transformCallable(function, data)
    }

    final override fun visitFunction(function: SirFunction, data: D): SirDeclaration {
        return transformFunction(function, data)
    }

    open fun transformAccessor(accessor: SirAccessor, data: D): SirDeclaration {
        return transformCallable(accessor, data)
    }

    final override fun visitAccessor(accessor: SirAccessor, data: D): SirDeclaration {
        return transformAccessor(accessor, data)
    }

    open fun transformGetter(getter: SirGetter, data: D): SirDeclaration {
        return transformAccessor(getter, data)
    }

    final override fun visitGetter(getter: SirGetter, data: D): SirDeclaration {
        return transformGetter(getter, data)
    }

    open fun transformSetter(setter: SirSetter, data: D): SirDeclaration {
        return transformAccessor(setter, data)
    }

    final override fun visitSetter(setter: SirSetter, data: D): SirDeclaration {
        return transformSetter(setter, data)
    }

    open fun transformVariable(variable: SirVariable, data: D): SirDeclaration {
        return transformDeclaration(variable, data)
    }

    final override fun visitVariable(variable: SirVariable, data: D): SirDeclaration {
        return transformVariable(variable, data)
    }

    open fun transformImport(import: SirImport, data: D): SirDeclaration {
        return transformDeclaration(import, data)
    }

    final override fun visitImport(import: SirImport, data: D): SirDeclaration {
        return transformImport(import, data)
    }
}

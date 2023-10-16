/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.bir

abstract class BirElementBase : BirElement {
    internal var owner: BirForest? = null
    final override var parent: BirElementBase? = null
        private set
    private var level: UByte = 0u
    internal var containingListId: Byte = 0

    val attachedToTree
        get() = owner != null

    internal fun getContainingList(): BirChildElementList<*>? {
        val containingListId = containingListId.toInt()
        return if (containingListId == 0) null
        else parent?.getChildrenListById(containingListId)
    }

    internal fun updateLevel() {
        val parent = parent
        level = if (parent != null) {
            val parentLevel = parent.level
            if (parentLevel == UByte.MAX_VALUE) UByte.MAX_VALUE else (parentLevel + 1u).toUByte()
        } else 0u
    }

    fun isAncestorOf(other: BirElementBase): Boolean {
        // fixme: ensure level is tracked for of-the-tree cases
        if (attachedToTree != other.attachedToTree) {
            return false
        }

        val distance = other.level.toInt() - level.toInt()
        if (distance < 0 || (distance == 0 && level != UByte.MAX_VALUE)) {
            return false
        }

        var n = other
        repeat(distance.toInt()) {
            n = n.parent ?: return false
            if (n === this) return true
        }

        return false
    }

    override fun <D> acceptChildren(visitor: BirElementVisitor<D>, data: D) {}

    internal fun initChild(new: BirElement?) {
        new as BirElementBase?

        new?.checkCanBeAttachedAsChild(this)

        if (new != null) {
            new.parent = this
            childAttached(new)
        }
    }

    internal fun replaceChild(old: BirElement?, new: BirElement?) {
        old as BirElementBase?
        new as BirElementBase?

        new?.checkCanBeAttachedAsChild(this)

        if (old != null) {
            old.parent = null
            childDetached(old)
        }
        if (new != null) {
            new.parent = this
            childAttached(new)
        }
    }

    private fun childDetached(childElement: BirElementBase) {
        owner?.elementDetached(childElement)
    }

    private fun childAttached(childElement: BirElementBase) {
        owner?.elementAttached(childElement)
    }

    internal fun checkCanBeAttachedAsChild(newParent: BirElement) {
        require(parent == null) { "Cannot attach element $this as a child of $newParent as it is already a child of $parent." }
    }


    internal open fun replaceChildProperty(old: BirElement, new: BirElement?) {
        throwChildForReplacementNotFound(old)
    }

    protected fun throwChildForReplacementNotFound(old: BirElement): Nothing {
        throw IllegalStateException("The child property $old not found in its parent $this")
    }

    internal open fun getChildrenListById(id: Int): BirChildElementList<*> {
        throwChildrenListWithIdNotFound(id)
    }

    protected fun throwChildrenListWithIdNotFound(id: Int): Nothing {
        throw IllegalStateException("The element $this does not have a children list with id $id")
    }

    internal fun removeFromList(
        list: BirChildElementList<BirElement?>,
    ) {
        if (!list.remove(this)) {
            list.parent.throwChildForReplacementNotFound(this)
        }
    }

    internal fun replaceInsideList(
        list: BirChildElementList<BirElement?>,
        new: BirElement?,
    ) {
        if (!list.replace(this, new)) {
            list.parent.throwChildForReplacementNotFound(this)
        }
    }
}

fun BirElement.replaceWith(new: BirElement?) {
    this as BirElementBase

    val parent = parent
    require(parent != null && attachedToTree) { "Element is not attached to a tree" }

    val list = getContainingList()
    if (list != null) {
        @Suppress("UNCHECKED_CAST")
        replaceInsideList(list as BirChildElementList<BirElement?>, new)
    } else {
        parent.replaceChildProperty(this, new)
    }
}

fun BirElement.remove() {
    this as BirElementBase

    val parent = parent
    require(parent != null && attachedToTree) { "Element is not attached to a tree" }

    val list = getContainingList()
    if (list != null) {
        @Suppress("UNCHECKED_CAST")
        removeFromList(list as BirChildElementList<BirElement?>)
    } else {
        parent.replaceChildProperty(this, null)
    }
}
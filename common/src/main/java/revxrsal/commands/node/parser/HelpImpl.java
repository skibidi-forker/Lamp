/*
 * This file is part of lamp, licensed under the MIT License.
 *
 *  Copyright (c) Revxrsal <reflxction.github@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package revxrsal.commands.node.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.InvalidHelpPageException;
import revxrsal.commands.help.Help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class HelpImpl {

    static <A extends CommandActor> List<ExecutableCommand<A>> paginate(List<ExecutableCommand<A>> commands, int page, int elementsPerPage) throws InvalidHelpPageException {
        if (commands.isEmpty())
            return List.of();
        int size = getNumberOfPages(commands.size(), elementsPerPage);
        if (page <= 0)
            throw new InvalidHelpPageException(commands, page, elementsPerPage, size);
        List<ExecutableCommand<A>> list = new ArrayList<>();
        if (page > size)
            throw new InvalidHelpPageException(commands, page, elementsPerPage, size);
        int listIndex = page - 1;
        int l = Math.min(page * elementsPerPage, commands.size());
        for (int i = listIndex * elementsPerPage; i < l; ++i) {
            list.add(commands.get(i));
        }
        return Collections.unmodifiableList(list);
    }

    static @Range(from = 1, to = Long.MAX_VALUE) int getNumberOfPages(int size, int elementsPerPage) {
        if (elementsPerPage < 1)
            throw new IllegalArgumentException("Elements per page cannot be less than 1! (Found " + elementsPerPage + ")");
        return (size / elementsPerPage) + (size % elementsPerPage == 0 ? 0 : 1);
    }

    static abstract class CommandListImpl<A extends CommandActor> implements Help.CommandList<A> {
        private final @Unmodifiable List<ExecutableCommand<A>> commands;

        public CommandListImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            this.commands = commands;
        }

        @Override public @Range(from = 1, to = Integer.MAX_VALUE) int numberOfPages(int elementsPerPage) {
            return getNumberOfPages(commands.size(), elementsPerPage);
        }

        @Override public @Unmodifiable List<ExecutableCommand<A>> all() {
            return commands;
        }

        @Override public @Unmodifiable List<ExecutableCommand<A>> asPage(int pageNumber, int elementsPerPage) {
            return HelpImpl.paginate(commands, pageNumber, elementsPerPage);
        }

        @Override public @NotNull Iterator<ExecutableCommand<A>> iterator() {
            return commands.iterator();
        }

        @Override public String toString() {
            return getClass().getSimpleName() + "(commands=" + commands + ')';
        }
    }

    static final class RelatedCommandsImpl<A extends CommandActor> extends CommandListImpl<A> implements Help.RelatedCommands<A> {

        public RelatedCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }

    static final class ChildrenCommandsImpl<A extends CommandActor> extends CommandListImpl<A> implements Help.ChildrenCommands<A> {

        public ChildrenCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }

    static final class SiblingCommandsImpl<A extends CommandActor> extends CommandListImpl<A> implements Help.SiblingCommands<A> {

        public SiblingCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }
}
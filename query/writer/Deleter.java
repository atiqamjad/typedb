/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package grakn.core.query.writer;

import grabl.tracing.client.GrablTracingThreadStatic.ThreadTrace;
import grakn.core.common.parameters.Context;
import grakn.core.concept.Concepts;
import grakn.core.concept.answer.ConceptMap;
import grakn.core.concept.data.Thing;
import grakn.core.query.pattern.Conjunction;
import grakn.core.query.pattern.variable.Variable;
import graql.lang.pattern.variable.Reference;

import java.util.HashMap;
import java.util.Map;

import static grabl.tracing.client.GrablTracingThreadStatic.traceOnThread;

public class Deleter {

    private static final String TRACE_PREFIX = "deletewriter.";
    private final Concepts conceptMgr;
    private final Context.Query context;
    private final ConceptMap existing;
    private final Conjunction<Variable> variables;
    private final Map<Reference, Thing> deleted;

    public Deleter(Concepts conceptMgr, Conjunction<Variable> variables, Context.Query context, ConceptMap existing) {
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "constructor")) {
            this.conceptMgr = conceptMgr;
            this.context = context;
            this.existing = existing;
            this.variables = variables;
            this.deleted = new HashMap<>();
        }
    }

    public void write() {
        // TODO
    }
}
